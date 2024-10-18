package net.snowflake.hibernate.dialect;

import static org.hibernate.type.SqlTypes.BLOB;
import static org.hibernate.type.SqlTypes.CLOB;
import static org.hibernate.type.SqlTypes.TIMESTAMP_WITH_TIMEZONE;

import java.util.List;
import java.util.Map;
import net.snowflake.client.jdbc.SnowflakeDriver;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.TimeZoneSupport;
import org.hibernate.dialect.sequence.SequenceSupport;
import org.hibernate.dialect.unique.CreateTableUniqueDelegate;
import org.hibernate.dialect.unique.UniqueDelegate;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.mapping.Column;
import org.hibernate.sql.ast.SqlAstTranslatorFactory;
import org.hibernate.sql.ast.spi.StandardSqlAstTranslatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Snowflake SQL Dialect. */
public class SnowflakeDialect extends Dialect {
  private static final Logger log = LoggerFactory.getLogger(SnowflakeDialect.class);

  private static final String CONFIGURATION_PROPERTY_PREFIX = "hibernate.dialect.snowflake.";
  static final String CONFIGURATION_PROPERTY_TABLE_TYPE =
      CONFIGURATION_PROPERTY_PREFIX + "table_type";
  static final String CONFIGURATION_PROPERTY_DEVELOPMENT_MODE =
      CONFIGURATION_PROPERTY_PREFIX + "development_mode";
  static final String ALLOW_UNRECOMMENDED_JDBC_DRIVER_VERSION =
      CONFIGURATION_PROPERTY_PREFIX + "allow_unrecommended_jdbc_driver";
  private static final String HIBERNATE_PARAMETER_BINDING_LOGGER = "org.hibernate.orm.jdbc.bind";
  private static final String HIBERNATE_EXTRACTED_VALUES_LOGGER = "org.hibernate.orm.jdbc.extract";
  private final TableType tableType;

  /**
   * Minimal version suppporting HTAP is <a
   * href="https://docs.snowflake.com/en/release-notes/clients-drivers/jdbc-2023#version-3-13-31-may-25-2023">3.13.31</a>
   */
  private final Version MINIMAL_DRIVER_VERSION = Version.from("3.13.31");

  /**
   * Constructor used to instantiate and configure the Dialect behavior.
   *
   * @param info used to configure the behavior of the Dialect.
   */
  public SnowflakeDialect(DialectResolutionInfo info) {
    super(info);
    log.trace("Called SnowflakeDialect(DialectResolutionInfo)");
    Map<String, Object> configurationValues = info.getConfigurationValues();
    logDialectProperties(configurationValues);
    tableType =
        TableType.valueOf(
            (String)
                configurationValues.getOrDefault(
                    CONFIGURATION_PROPERTY_TABLE_TYPE, TableType.HYBRID.name()));
    warnOnUsingNonHybridTables();
    warnOnPossibleSensitiveDataLoggingEnabled(configurationValues);
    warnOnUnsupportedDriverVersion(configurationValues);
  }

  private void warnOnUnsupportedDriverVersion(Map<String, Object> configurationValues) {
    // security risk mitigation
    // we cannot use info.getDriverMajorVersion() and info.getDriverMinorVersion() since there is no
    // method for patch part of the version
    try {
      boolean allowUnrecommendedJdbcDriver =
          Boolean.parseBoolean(
              (String)
                  configurationValues.getOrDefault(
                      ALLOW_UNRECOMMENDED_JDBC_DRIVER_VERSION, "false"));
      Version driverVersion = Version.from(SnowflakeDriver.implementVersion);
      log.debug("JDBC Driver version {}", driverVersion);
      if (MINIMAL_DRIVER_VERSION.compareTo(driverVersion) > 0) {
        String errorMessage =
            String.format(
                "Using driver in version %s is not recommended - driver version should be at least %s",
                driverVersion, MINIMAL_DRIVER_VERSION);
        if (allowUnrecommendedJdbcDriver) {
          log.warn(errorMessage);
        } else {
          log.error(errorMessage);
          throw new JdbcDriverVersionException(driverVersion, MINIMAL_DRIVER_VERSION);
        }
      }
    } catch (VersionParsingException e) {
      log.warn(e.getMessage());
    }
  }

  private void warnOnUsingNonHybridTables() {
    // security risk mitigation
    if (tableType != TableType.HYBRID) {
      log.warn(
          "Be aware, that not hybrid tables don't enforce constraints (see: https://docs.snowflake.com/en/sql-reference/constraints-overview). It can also reduce performance as hybrid tables are preferred for OLTP.");
    }
  }

  private static void warnOnPossibleSensitiveDataLoggingEnabled(
      Map<String, Object> configurationValues) {
    // security risk mitigation
    boolean developmentMode =
        Boolean.parseBoolean(
            configurationValues
                .getOrDefault(CONFIGURATION_PROPERTY_DEVELOPMENT_MODE, "false")
                .toString());
    warnOnPossibleSensitiveDataLogging(
        developmentMode,
        LoggerFactory.getLogger(HIBERNATE_PARAMETER_BINDING_LOGGER),
        "Statement parameter bindings logging is enabled - it's recommended to turn it off on production environment");
    warnOnPossibleSensitiveDataLogging(
        developmentMode,
        LoggerFactory.getLogger(HIBERNATE_EXTRACTED_VALUES_LOGGER),
        "Extracted in select data logging is enabled - it's recommended to turn it off on production environment");
  }

  private static void warnOnPossibleSensitiveDataLogging(
      boolean developmentMode, Logger sensitiveDataLogger, String logMessage) {
    if (sensitiveDataLogger.isTraceEnabled()) {
      if (developmentMode) {
        log.warn(logMessage);
      } else {
        log.error(logMessage);
      }
    }
  }

  private static void logDialectProperties(Map<String, Object> configurationValues) {
    if (log.isTraceEnabled()) {
      configurationValues.entrySet().stream()
          .filter(e -> e.getKey().startsWith(CONFIGURATION_PROPERTY_PREFIX))
          .forEach(e -> log.trace("Dialect property {} has value {}", e.getKey(), e.getValue()));
    }
  }

  @Override
  public String getCreateTableString() {
    return tableType.createTableStatement();
  }

  @Override
  public boolean supportsIfExistsBeforeTableName() {
    return true;
  }

  @Override
  public SequenceSupport getSequenceSupport() {
    return new SnowflakeSequenceSupport();
  }

  @Override
  public String getNativeIdentifierGeneratorStrategy() {
    // it configures auto id generation type
    return "sequence";
  }

  @Override
  public String getForUpdateString() {
    // Hybrid tables accepts for update whereas standard tables not
    // we should revisit lock strategies and/or prohibit using table id generator for standard
    // tables
    return tableType == TableType.HYBRID ? " FOR UPDATE" : "";
  }

  @Override
  public String getAddForeignKeyConstraintString(
      String constraintName,
      String[] foreignKey,
      String referencedTable,
      String[] primaryKey,
      boolean referencesPrimaryKey) {
    // Base dialect by default does not add name of primary table when referencesPrimaryKey is true
    // Unistore allows creating foreign keys after creation but it should be disabled

    return " add constraint "
        + quote(constraintName)
        + " foreign key ("
        + String.join(", ", foreignKey)
        + ") references "
        + referencedTable
        + " ("
        + String.join(", ", primaryKey)
        + ')';
  }

  @Override
  public SqlAstTranslatorFactory getSqlAstTranslatorFactory() {
    // needed to fill join table in the many-to-many relation - default implementation returns null
    return new StandardSqlAstTranslatorFactory();
  }

  @Override
  public boolean hasAlterTable() {
    // HYBRID tables cannot be altered - e.g. by adding constraints or indexes
    return tableType != TableType.HYBRID;
  }

  @Override
  public UniqueDelegate getUniqueDelegate() {
    // unique can be defined on create table, not on alter table
    return new CreateTableUniqueDelegate(this);
  }

  @Override
  public String getCreateIndexString(boolean unique) {
    return super.getCreateIndexString(unique);
  }

  @Override
  public String getCreateIndexTail(boolean unique, List<Column> columns) {
    return super.getCreateIndexTail(unique, columns);
  }

  @Override
  protected void registerDefaultKeywords() {
    // parent class adds only ansi keywords
    // some Snowflake keywords are provided by the driver:
    // https://github.com/snowflakedb/snowflake-jdbc/blob/fa658a430cb9b810c791753ad1f0afb29c5528f3/src/main/java/net/snowflake/client/jdbc/SnowflakeDatabaseMetaData.java#L96
    // but some are missing - there is an OSS issue to fix it
    // https://github.com/snowflakedb/snowflake-jdbc/issues/1630
    // state based on driver version 3.15.0
    // keywords are automatically applied when hibernate property hibernate.auto_quote_keyword is
    // set to true
    super.registerDefaultKeywords();
    registerKeyword("ilike");
    registerKeyword("qualify");
    registerKeyword("connection");
    registerKeyword("organization");
    registerKeyword("gscluster");
  }

  /**
   * Default timestamp precision as in <a
   * href="https://docs.snowflake.com/en/sql-reference/data-types-datetime#timestamp">Snowflake
   * documentation</a>
   *
   * @return the default precision
   */
  @Override
  public int getDefaultTimestampPrecision() {
    // Default timestamp precision as in Snowflake documentation
    // https://docs.snowflake.com/en/sql-reference/data-types-datetime#timestamp
    return 9;
  }

  @Override
  protected String columnType(int sqlTypeCode) {
    // TIMESTAMP WITH TIME ZONE from other databases in Snowflake is called TIMESTAMP_TZ
    // CLOB and BLOB are supported by different types
    // https://docs.snowflake.com/en/sql-reference/data-types-unsupported
    switch (sqlTypeCode) {
      case TIMESTAMP_WITH_TIMEZONE:
        return "TIMESTAMP_TZ";
      case CLOB:
        return "VARCHAR(16777216)";
      case BLOB:
        return "BINARY";
      default:
        return super.columnType(sqlTypeCode);
    }
  }

  @Override
  public boolean useInputStreamToInsertBlob() {
    // Inserting clobs/blobs with input stream is not supported by the snowflake-jdbc
    return false;
  }

  @Override
  public TimeZoneSupport getTimeZoneSupport() {
    // Snowflake support timezones natively but driver setTimestamp always set TIMESTAMP_LTZ
    // It seems that the best way to keep date with timezone from Hibernate is to have a split table
    // with timezone
    return TimeZoneSupport.NONE;
  }

  @Override
  public boolean supportsColumnCheck() {
    // Snowflake does not support check e.g. for enum values
    return false;
  }

  @Override
  public int getInExpressionCountLimit() {
    // Snowflake limits elements in IN expression
    return 16_384;
  }

  @Override
  public boolean supportsCaseInsensitiveLike() {
    return true;
  }

  @Override
  public String getCaseInsensitiveLike() {
    return "ILIKE";
  }

  /**
   * Get the configured {@link TableType} for current instance of the Dialect.
   *
   * @return the {@link TableType}
   */
  public TableType getTableType() {
    return tableType;
  }
}

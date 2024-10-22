package net.snowflake.hibernate.dialect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.AfterAll;

public abstract class DroppingTablesBaseTest {
  protected static TableType tableType = TableType.HYBRID;

  protected static List<Class<?>> classes;

  protected static SessionFactory sessionFactory;

  protected static String hbm2ddlMode = "create";
  private static final String driverConnectionString =
      "jdbc:snowflake://"
          + System.getenv("SNOWFLAKE_TEST_ACCOUNT")
          + ".snowflakecomputing.com/?"
          + "database="
          + System.getenv("SNOWFLAKE_TEST_DATABASE")
          + "&JDBC_USE_SESSION_TIMEZONE=false"
          + "&TIMEZONE=UTC"
          + "&TIMESTAMP_TYPE_MAPPING=TIMESTAMP_NTZ"
          + "&AUTOCOMMIT=false"
          + "&schema="
          + System.getenv("SNOWFLAKE_TEST_SCHEMA")
          + "&warehouse="
          + System.getenv("SNOWFLAKE_TEST_WAREHOUSE");
  private static final String userName = System.getenv("SNOWFLAKE_TEST_USER");
  private static final String password = System.getenv("SNOWFLAKE_TEST_PASSWORD");

  protected static SessionFactory initSessionFactory() {
    System.setProperty("net.snowflake.jdbc.loggerImpl", "net.snowflake.client.log.SLF4JLogger");

    Map<String, Object> settings = new HashMap<>();
    settings.put("hibernate.dialect", SnowflakeDialect.class.getName());
    settings.put(SnowflakeDialect.CONFIGURATION_PROPERTY_TABLE_TYPE, tableType.name());
    settings.put(SnowflakeDialect.CONFIGURATION_PROPERTY_DEVELOPMENT_MODE, "false");
    settings.put("hibernate.connection.url", driverConnectionString);
    settings.put("hibernate.connection.username", userName);
    settings.put("hibernate.connection.password", password);
    settings.put("hibernate.show_sql", "false");
    settings.put("hibernate.format_sql", "false");
    settings.put("hibernate.highlight_sql", "true");
    settings.put("hibernate.use_sql_comments", "false");
    settings.put("hibernate.hbm2ddl.auto", hbm2ddlMode);
    settings.put("hibernate.auto_quote_keyword", "true");
    settings.put("hibernate.connection.autocommit", "false");
    settings.put("hibernate.connection.provider_disables_autocommit", "true");
    if (tableType == TableType.HYBRID) {
      settings.put("hibernate.jdbc.batch_size", "10");
      settings.put("hibernate.order_inserts", "true");
      settings.put("hibernate.order_updates", "true");
    } else {
      // For standard tables batches may return different batch size e.g.
      // [09:51:13.983] [DEBUG] [org.hibernate.SQL.logStatement(135)] - insert into employee2
      // (firstName,lastName,id) values (?,?,?)
      // [09:51:14.210] [DEBUG] [org.hibernate.SQL.logStatement(135)] - insert into employee2
      // (firstName,lastName,id) values (?,?,?)
      // [09:51:14.210] [DEBUG] [org.hibernate.SQL.logStatement(135)] - insert into employee2
      // (firstName,lastName,id) values (?,?,?)
      // [09:51:14.787] [WARN ] [org.hibernate.orm.jdbc.checkRowCounts(312)] - HHH100001: JDBC
      // driver did not return the expected number of row counts (employee2) - expected 1, but
      // received 3
      settings.put("hibernate.jdbc.batch_size", "1");
      settings.put("hibernate.order_inserts", "false");
      settings.put("hibernate.order_updates", "false");
    }
    settings.put("hibernate.generate_statistics", "false");
    ServiceRegistry serviceRegistry =
        new StandardServiceRegistryBuilder().applySettings(settings).build();
    MetadataSources metadataSources = new MetadataSources(serviceRegistry);
    metadataSources.addAnnotatedClasses(classes.toArray(new Class[] {}));
    return metadataSources.buildMetadata().buildSessionFactory();
  }

  protected static Connection getDriverRawConnection() throws SQLException {
    Properties properties = new Properties();
    properties.put("user", userName);
    properties.put("password", password);
    return DriverManager.getDriver(driverConnectionString)
        .connect(driverConnectionString, properties);
  }

  @AfterAll
  public static void tearDownClass() {
    sessionFactory.close();
    tableType = TableType.HYBRID;
    hbm2ddlMode = "create";
  }
}

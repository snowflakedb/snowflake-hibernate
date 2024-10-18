package net.snowflake.hibernate.dialect.datatypes;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import net.snowflake.hibernate.dialect.DroppingTablesBaseTest;
import net.snowflake.hibernate.dialect.TestTags;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(TestTags.HYBRID)
public class DataTypesTest extends DroppingTablesBaseTest {
  // This class does not use Hybrid table specific features - there is no need to test this class
  // also with standard tables

  private static final List<Class<?>> mappedClasses =
      Arrays.asList(DataTypes.class, AuditData.class);

  @BeforeAll
  public static void setupClass() {
    classes = mappedClasses;
    sessionFactory = initSessionFactory();
  }

  @Test
  public void shouldAddTableWithDifferentDataTypes() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    DataTypes entity = new DataTypes();
    sessionFactory.inTransaction(session -> session.persist(entity));
    sessionFactory.inTransaction(
        session -> {
          DataTypes fetched = session.get(DataTypes.class, entity.id);
          assertNotNull(fetched);
          assertAll(
              () ->
                  assertEquals(entity.stringAsVarchar, fetched.stringAsVarchar, "stringAsVarchar"),
              () -> assertEquals(entity.stringAsString, fetched.stringAsString, "stringAsString"),
              () -> assertEquals(entity.longString, fetched.longString, "longString"),
              () -> assertEquals(entity.aBoolean, fetched.aBoolean, "aBoolean"),
              () -> assertEquals(entity.aByte, fetched.aByte, "aByte"),
              () -> assertEquals(entity.aShort, fetched.aShort, "aShort"),
              () -> assertEquals(entity.anInt, fetched.anInt, "anInt"),
              () -> assertEquals(entity.aLong, fetched.aLong, "aLong"),
              () -> assertEquals(entity.aBigInteger, fetched.aBigInteger, "aBigInteger"),
              () -> assertEquals(entity.aBigDecimal, fetched.aBigDecimal, "aBigDecimal"),
              () -> assertEquals(entity.aFloat, fetched.aFloat, "aFloat"),
              () -> assertEquals(entity.aDouble, fetched.aDouble, "aDouble"),
              () -> assertEquals(entity.date, fetched.date, "date"),
              () -> assertEquals(entity.instant, fetched.instant, "instant"),
              () -> assertEquals(entity.localDate, fetched.localDate, "localDate"),
              () -> assertEquals(entity.localTime, fetched.localTime, "localTime"),
              () -> assertEquals(entity.localDateTime, fetched.localDateTime, "localDateTime"),
              () -> assertEquals(entity.offsetDateTime, fetched.offsetDateTime, "offsetDateTime"),
              () ->
                  assertEquals(
                      entity.offsetDateTimeDifferentTz,
                      fetched.offsetDateTimeDifferentTz,
                      "offsetDateTimeDifferentTz"),
              () ->
                  assertEquals(
                      entity.offsetDateTimeUTC, fetched.offsetDateTimeUTC, "offsetDateTimeUTC"),
              // Zoned date time are saved with offset and without time zone name
              () ->
                  assertEquals(
                      entity.zonedDateTime.toOffsetDateTime(),
                      fetched.zonedDateTime.toOffsetDateTime(),
                      "zonedDateTime"),
              () ->
                  assertEquals(
                      entity.zonedDateTimeDifferentTz.toOffsetDateTime(),
                      fetched.zonedDateTimeDifferentTz.toOffsetDateTime(),
                      "zonedDateTimeDifferentTz"),
              () ->
                  assertEquals(
                      entity.zonedDateTimeUTC.toOffsetDateTime(),
                      fetched.zonedDateTimeUTC.toOffsetDateTime(),
                      "zonedDateTimeUTC"),
              () -> assertEquals(entity.sqlDate, fetched.sqlDate, "sqlDate"),
              () -> assertEquals(entity.sqlTime, fetched.sqlTime, "sqlTime"),
              () -> assertEquals(entity.sqlTimestamp, fetched.sqlTimestamp, "sqlTimestamp"),
              () -> assertEquals(entity.timeZone, fetched.timeZone, "timeZone"),
              () -> assertArrayEquals(entity.bytes, fetched.bytes, "bytes"),
              () -> assertArrayEquals(entity.bytesAsBinary, fetched.bytesAsBinary, "bytesAsBinary"),
              () -> assertArrayEquals(entity.lob, fetched.lob, "lob"),
              () -> assertEquals(entity.clob.toString(), fetched.clob.toString(), "clob"),
              () -> assertEquals(entity.enumAsString, fetched.enumAsString, "enumAsString"),
              () -> assertEquals(entity.enumAsNumber, fetched.enumAsNumber, "enumAsNumber"));
        });
  }

  @Test
  public void shouldSaveEntityWithAuditData() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    AuditData entity = new AuditData();

    sessionFactory.inTransaction(session -> session.persist(entity));

    assertNotNull(entity.createdAt);
    assertNotNull(entity.updatedAt);

    sessionFactory.inTransaction(
        session -> {
          AuditData fetched = session.get(AuditData.class, entity.id);
          assertNotNull(fetched);
          assertAll(
              () -> assertEquals(entity.createdAt, fetched.createdAt, "createdAt"),
              () -> assertEquals(entity.updatedAt, fetched.updatedAt, "updatedAt"));
        });

    sessionFactory.inTransaction(
        session -> {
          AuditData fetched = session.get(AuditData.class, entity.id);
          fetched.content = "test content";
          session.persist(fetched);
        });

    sessionFactory.inTransaction(
        session -> {
          AuditData fetched = session.get(AuditData.class, entity.id);
          assertNotNull(fetched);
          assertAll(
              () -> assertEquals(entity.createdAt, fetched.createdAt, "createdAt"),
              () -> assertNotEquals(entity.updatedAt, fetched.updatedAt, "updatedAt"));
        });
  }
}

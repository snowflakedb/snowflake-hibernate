package net.snowflake.hibernate.dialect.datatypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;
import net.snowflake.client.jdbc.SnowflakeClob;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.TimeZoneColumn;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType;

@Entity
@Table(name = "data_types")
class DataTypes {
  // every field is in the package scope to not generate getters for tests

  @Id @GeneratedValue long id;

  String stringAsVarchar = "some string 1";

  @Column(columnDefinition = "string")
  String stringAsString = "some string 2";

  @Column(length = 16 * 1024 * 1024)
  String longString = "some string 2";

  boolean aBoolean = true;
  byte aByte = 5;
  short aShort = 300;
  int anInt = 10000;
  long aLong = 23131;
  BigInteger aBigInteger = BigInteger.valueOf(123456);

  @Column(precision = 38, scale = 12)
  BigDecimal aBigDecimal = new BigDecimal("43242.432432423423");

  float aFloat = -54.3f;
  double aDouble = 434.5454;
  Date date = new Date();
  Instant instant = Instant.now();
  LocalDate localDate = LocalDate.now();
  LocalTime localTime =
      LocalTime.now().truncatedTo(ChronoUnit.MILLIS); // local time cannot be saved with nanos
  LocalDateTime localDateTime = LocalDateTime.now();

  @TimeZoneStorage(TimeZoneStorageType.COLUMN)
  @TimeZoneColumn(name = "offsetDateTime_on_offset", columnDefinition = "smallint")
  OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneId.of("Europe/Warsaw"));

  @TimeZoneStorage(TimeZoneStorageType.COLUMN)
  @TimeZoneColumn(name = "offsetDateTimeDifferentTz_on_offset", columnDefinition = "smallint")
  OffsetDateTime offsetDateTimeDifferentTz = OffsetDateTime.now(ZoneId.of("Asia/Tokyo"));

  @TimeZoneStorage(TimeZoneStorageType.COLUMN)
  @TimeZoneColumn(name = "offsetDateTimeUTC_on_offset", columnDefinition = "smallint")
  OffsetDateTime offsetDateTimeUTC = OffsetDateTime.now(ZoneId.of("UTC"));

  @TimeZoneStorage(TimeZoneStorageType.COLUMN)
  @TimeZoneColumn(name = "zonedDateTime_on_offset", columnDefinition = "smallint")
  ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Warsaw"));

  @TimeZoneStorage(TimeZoneStorageType.COLUMN)
  @TimeZoneColumn(name = "zonedDateTimeDifferentTz_on_offset", columnDefinition = "smallint")
  ZonedDateTime zonedDateTimeDifferentTz = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));

  @TimeZoneStorage(TimeZoneStorageType.COLUMN)
  @TimeZoneColumn(name = "zonedDateTimeUTC_on_offset", columnDefinition = "smallint")
  ZonedDateTime zonedDateTimeUTC = ZonedDateTime.now(ZoneId.of("UTC"));

  java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.now());
  java.sql.Time sqlTime = java.sql.Time.valueOf(LocalTime.now());
  java.sql.Timestamp sqlTimestamp = java.sql.Timestamp.valueOf(LocalDateTime.now());
  TimeZone timeZone = TimeZone.getTimeZone("America/Anchorage");
  byte[] bytes = "test string".getBytes(StandardCharsets.UTF_8);

  @Column(columnDefinition = "binary")
  byte[] bytesAsBinary = "test string".getBytes(StandardCharsets.UTF_8);

  @Lob
  @JdbcType(BinaryJdbcType.class) // Lob must be set as binary
  byte[] lob = "test string".getBytes(StandardCharsets.UTF_8);

  Clob clob = new SnowflakeClob("żółć");

  @Enumerated(EnumType.STRING)
  EnumSample enumAsString = EnumSample.GREEN;

  @Enumerated(EnumType.ORDINAL)
  EnumSample enumAsNumber = EnumSample.GREEN;

  enum EnumSample {
    RED,
    GREEN
  }
}

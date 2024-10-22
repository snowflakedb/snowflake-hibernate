package net.snowflake.hibernate.dialect;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Tag(TestTags.UNIT)
class TableTypeTest {
  @ParameterizedTest
  @CsvSource({"HYBRID,CREATE HYBRID TABLE", "STANDARD,CREATE TABLE"})
  public void shouldUseCustomCreateStatement(
      TableType tableType, String expectedCreateTableString) {
    assertEquals(expectedCreateTableString, tableType.createTableStatement());
  }
}

package net.snowflake.hibernate.dialect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@Tag(TestTags.UNIT)
class VersionTest {

  @ValueSource(
      strings = {
        "3.13.31", "3.15.0",
      })
  @ParameterizedTest
  void shouldCreateVersion(String rawVersion) {
    Version version = Version.from(rawVersion);
    assertEquals(rawVersion, version.toString());
  }

  @ValueSource(strings = {"3.13.xx", "3.15", "3.15.1.23", "bla", "  "})
  @ParameterizedTest
  void shouldFailOnVersionCreation(String rawVersion) {
    assertThrows(VersionParsingException.class, () -> Version.from(rawVersion));
  }

  @CsvSource(
      delimiter = '|',
      value = {
        "3.15.0|3.13.30|1",
        "3.12.15|3.14.0|-1",
        "3.15.0|3.15.1|-1",
        "3.15.1|3.15.0|1",
        "3.15.0|3.15.0|0",
        "3.15.0|3.15.0|0",
        "3.15.0|4.0.1|-1",
        "3.15.0|1.11.1|1",
      })
  @ParameterizedTest
  void shouldCompareVersions(String rawVersionLeft, String rawVersionRight, int compareResult) {
    Version versionLeft = Version.from(rawVersionLeft);
    Version versionRight = Version.from(rawVersionRight);
    assertEquals(compareResult, versionLeft.compareTo(versionRight));
  }
}

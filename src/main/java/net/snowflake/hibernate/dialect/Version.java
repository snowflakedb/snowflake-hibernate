package net.snowflake.hibernate.dialect;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Version implements Comparable<Version> {
  private final int major;
  private final int minor;
  private final int patch;

  static Version from(String rawVersion) {
    List<Integer> versionParts;
    try {
      versionParts =
          Stream.of(rawVersion.split("\\.")).map(Integer::parseInt).collect(Collectors.toList());
    } catch (Exception e) {
      throw new VersionParsingException(rawVersion, e);
    }
    if (versionParts.size() != 3) {
      throw new VersionParsingException(rawVersion);
    }
    return new Version(versionParts.get(0), versionParts.get(1), versionParts.get(2));
  }

  private Version(int major, int minor, int patch) {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
  }

  @Override
  public int compareTo(Version o) {
    int majorCompare = Integer.compare(major, o.major);
    if (majorCompare != 0) {
      return majorCompare;
    }
    int minorCompare = Integer.compare(minor, o.minor);
    if (minorCompare != 0) {
      return minorCompare;
    }
    return Integer.compare(patch, o.patch);
  }

  @Override
  public String toString() {
    return major + "." + minor + "." + patch;
  }
}

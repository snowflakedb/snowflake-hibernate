package net.snowflake.hibernate.dialect;

class JdbcDriverVersionException extends RuntimeException {
  public JdbcDriverVersionException(Version actual, Version minimalRecommended) {
    super(
        String.format(
            "Using driver in version %s must be forced - recommended driver version should be at least %s",
            actual, minimalRecommended));
  }
}

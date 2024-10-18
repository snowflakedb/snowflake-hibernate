package net.snowflake.hibernate.dialect;

import org.hibernate.MappingException;
import org.hibernate.dialect.sequence.SequenceSupport;

class SnowflakeSequenceSupport implements SequenceSupport {
  @Override
  public String getSelectSequenceNextValString(String sequenceName) {
    return sequenceName + ".nextval";
  }

  @Override
  public String getSelectSequencePreviousValString(String sequenceName) throws MappingException {
    // https://docs.snowflake.com/en/user-guide/querying-sequences#currval-not-supported
    throw new UnsupportedOperationException(
        "Getting previous value from sequence is not available");
  }

  @Override
  public String getDropSequenceString(String sequenceName) {
    return "drop sequence if exists " + sequenceName;
  }
}

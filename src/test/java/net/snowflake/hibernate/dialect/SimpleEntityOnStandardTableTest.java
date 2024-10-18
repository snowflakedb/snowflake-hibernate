package net.snowflake.hibernate.dialect;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;

@Tag(TestTags.STANDARD)
public class SimpleEntityOnStandardTableTest extends SimpleEntityTest {
  @BeforeAll
  public static void setupClass() {
    classes = SimpleEntityTest.mappedClasses;
    tableType = TableType.STANDARD;
    sessionFactory = initSessionFactory();
  }
}

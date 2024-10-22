package net.snowflake.hibernate.dialect.idgeneration;

import net.snowflake.hibernate.dialect.TableType;
import net.snowflake.hibernate.dialect.TestTags;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;

@Tag(TestTags.STANDARD)
public class IdGenerationOnStandardTableTest extends IdGenerationTest {
  @BeforeAll
  public static void setupClass() {
    classes = IdGenerationTest.mappedClasses;
    tableType = TableType.STANDARD;
    sessionFactory = initSessionFactory();
  }
}

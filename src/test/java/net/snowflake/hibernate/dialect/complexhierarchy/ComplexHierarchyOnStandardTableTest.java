package net.snowflake.hibernate.dialect.complexhierarchy;

import net.snowflake.hibernate.dialect.TableType;
import net.snowflake.hibernate.dialect.TestTags;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;

@Tag(TestTags.STANDARD)
public class ComplexHierarchyOnStandardTableTest extends ComplexHierarchyTest {

  @BeforeAll
  public static void setupClass() {
    classes = ComplexHierarchyTest.mappedClasses;
    tableType = TableType.STANDARD;
    sessionFactory = initSessionFactory();
  }
}

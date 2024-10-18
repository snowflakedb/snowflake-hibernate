package net.snowflake.hibernate.dialect.keywords;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.List;
import net.snowflake.hibernate.dialect.DroppingTablesBaseTest;
import net.snowflake.hibernate.dialect.TestTags;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(TestTags.HYBRID)
public class KeywordsTest extends DroppingTablesBaseTest {
  // This class does not use Hybrid table specific features - there is no need to test this class
  // also with standard tables

  private static final List<Class<?>> mappedClasses =
      Collections.singletonList(KeywordEntity.class);

  @BeforeAll
  public static void setupClass() {
    classes = mappedClasses;
    sessionFactory = initSessionFactory();
  }

  @Test
  public void shouldAddTableWithKeywordNameAndColumns() {
    KeywordEntity entity = new KeywordEntity();
    sessionFactory.inTransaction(session -> session.persist(entity));
    sessionFactory.inTransaction(
        session -> assertNotNull(session.get(KeywordEntity.class, entity.getId())));
  }
}

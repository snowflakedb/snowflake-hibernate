package net.snowflake.hibernate.dialect.idgeneration;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;
import net.snowflake.hibernate.dialect.AbstractSimplePerson;
import net.snowflake.hibernate.dialect.DroppingTablesBaseTest;
import net.snowflake.hibernate.dialect.TestTags;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(TestTags.HYBRID)
public class IdGenerationTest extends DroppingTablesBaseTest {
  public static List<Class<?>> mappedClasses =
      Arrays.asList(
          SimplePersonSequenceId.class,
          SimplePersonUuidId.class,
          SimplePersonUuidAsStringId.class,
          SimplePersonAutoId.class,
          SimplePersonTableId.class,
          SimplePersonHiLoSequenceId.class);

  @BeforeAll
  public static void setupClass() {
    classes = mappedClasses;
    sessionFactory = initSessionFactory();
  }

  @Test
  public void shouldCreateSimplePersonWithSequencedId() {
    testSimplePersonAdding(
        new SimplePersonSequenceId(null, "John", "Doe"),
        new SimplePersonSequenceId(null, "Alice", "Doe"));
  }

  @Test
  public void shouldCreateSimplePersonWithUuidId() {
    testSimplePersonAdding(
        new SimplePersonUuidId(null, "John", "Doe"), new SimplePersonUuidId(null, "Alice", "Doe"));
  }

  @Test
  public void shouldCreateSimplePersonWithUuidAsStringId() {
    testSimplePersonAdding(
        new SimplePersonUuidAsStringId(null, "John", "Doe"),
        new SimplePersonUuidAsStringId(null, "Alice", "Doe"));
  }

  @Test
  public void shouldCreateSimplePersonWithAutoId() {
    testSimplePersonAdding(
        new SimplePersonAutoId(null, "John", "Doe"), new SimplePersonAutoId(null, "Alice", "Doe"));
  }

  @Test
  public void shouldCreateSimplePersonWithTableId() {
    testSimplePersonAdding(
        new SimplePersonTableId(null, "John", "Doe"),
        new SimplePersonTableId(null, "Alice", "Doe"));
  }

  @Test
  public void shouldCreateSimplePersonWithHiLoSequenceId() {
    testSimplePersonAdding(
        new SimplePersonHiLoSequenceId(null, "John", "Doe"),
        new SimplePersonHiLoSequenceId(null, "Alice", "Doe"));
  }

  @SafeVarargs
  private final <ID> void testSimplePersonAdding(AbstractSimplePerson<ID>... people) {
    AbstractSimplePerson<ID> person1 = people[0];
    AbstractSimplePerson<ID> person2 = people[1];
    sessionFactory.inTransaction(
        session -> {
          for (AbstractSimplePerson<ID> person : people) {
            session.persist(person);
          }
        });

    assertNotNull(person1.getId());
    assertNotNull(person2.getId());
    assertNotEquals(person1.getId(), person2.getId());
  }
}

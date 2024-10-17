package net.snowflake.hibernate.dialect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.snowflake.hibernate.dialect.idgeneration.SimplePersonAutoId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(TestTags.HYBRID)
public class SimpleEntityTest extends DroppingTablesBaseTest {

  public static List<Class<?>> mappedClasses =
      Arrays.asList(SimplePerson.class, SimplePersonAutoId.class);

  @BeforeAll
  public static void setupClass() {
    classes = mappedClasses;
    sessionFactory = initSessionFactory();
  }

  @Test
  public void shouldRunCrudOperationsOnSimplePerson() {
    sessionFactory.inTransaction(
        session -> {
          session.persist(new SimplePerson(5L, "John", "Doe"));
          session.persist(new SimplePerson(6L, "Alice", "Doe"));
        });

    List<SimplePerson> fetchedPeople = new ArrayList<>();
    sessionFactory.inTransaction(
        session -> {
          List<SimplePerson> result =
              session
                  .createSelectionQuery(
                      "from SimplePerson where id in (?1, ?2) order by id", SimplePerson.class)
                  .setParameter(1, 5L)
                  .setParameter(2, 6L)
                  .getResultList();
          fetchedPeople.addAll(result);
        });

    assertEquals(2, fetchedPeople.size());
    assertEquals(new SimplePerson(5L, "John", "Doe"), fetchedPeople.get(0));
    assertEquals(new SimplePerson(6L, "Alice", "Doe"), fetchedPeople.get(1));

    sessionFactory.inTransaction(
        session -> {
          SimplePerson simplePerson = session.get(SimplePerson.class, 5L);
          simplePerson.setFirstName("Bob");
          simplePerson.setLastName("Kowalski");
        });

    sessionFactory.inTransaction(
        session ->
            assertEquals(
                new SimplePerson(5L, "Bob", "Kowalski"), session.get(SimplePerson.class, 5L)));

    sessionFactory.inTransaction(
        session -> {
          SimplePerson simplePerson = session.get(SimplePerson.class, 5L);
          session.remove(simplePerson);
          assertNull(session.get(SimplePerson.class, 5L));
        });
  }

  @Test
  public void shouldLimitParametersInExpression() {
    sessionFactory.inSession(
        session -> {
          List<Long> ids = new ArrayList<>();
          for (long i = 0L; i < 17_000L; ++i) {
            ids.add(i);
          }
          List<SimplePerson> people =
              session
                  .createQuery("from SimplePerson where id in (:list)", SimplePerson.class)
                  .setParameter("list", ids)
                  .list();
          assertNotNull(people);
        });
  }

  @Test
  public void shouldSelectWithLimit() {
    SimplePersonAutoId person1 = new SimplePersonAutoId(null, "John", "Doe");
    SimplePersonAutoId person2 = new SimplePersonAutoId(null, "Alice", "Doe");
    sessionFactory.inTransaction(
        session -> {
          session.persist(person1);
          session.persist(person2);
        });

    sessionFactory.inSession(
        session -> {
          List<SimplePersonAutoId> people =
              session
                  .createQuery(
                      "from SimplePersonAutoId where id in (?1) ORDER BY id limit 1",
                      SimplePersonAutoId.class)
                  .setParameter(1, Arrays.asList(person1.getId(), person2.getId()))
                  .list();
          assertNotNull(people);
          assertEquals(1, people.size());
          assertEquals(person1.getId(), people.get(0).getId());
        });

    sessionFactory.inSession(
        session -> {
          List<SimplePersonAutoId> people =
              session
                  .createQuery(
                      "from SimplePersonAutoId where id in (?1) ORDER BY id",
                      SimplePersonAutoId.class)
                  .setFirstResult(1)
                  .setMaxResults(1)
                  .setParameter(1, Arrays.asList(person1.getId(), person2.getId()))
                  .list();
          assertNotNull(people);
          assertEquals(1, people.size());
          assertEquals(person2.getId(), people.get(0).getId());
        });
  }

  @Test
  public void shouldSelectCaseInsensitive() {
    SimplePersonAutoId person1 =
        new SimplePersonAutoId(null, UUID.randomUUID().toString().toUpperCase(), "Doe");
    sessionFactory.inTransaction(session -> session.persist(person1));

    sessionFactory.inSession(
        session -> {
          SimplePersonAutoId found =
              session
                  .createQuery(
                      "from SimplePersonAutoId where firstName ilike ?1", SimplePersonAutoId.class)
                  .setParameter(1, person1.firstName.toLowerCase())
                  .getSingleResult();
          assertNotNull(found);
          assertEquals(person1.getId(), found.getId());
        });
  }

  @Test
  public void shouldUseAggregateFunctionsSelectWithLimit() {
    SimplePersonAutoId person1 = new SimplePersonAutoId(null, "John", "Doe");
    SimplePersonAutoId person2 = new SimplePersonAutoId(null, "Alice", "Doe");
    SimplePersonAutoId person3 = new SimplePersonAutoId(null, "Bob", "Doe");
    sessionFactory.inTransaction(
        session -> {
          session.persist(person1);
          session.persist(person2);
          session.persist(person3);
        });

    sessionFactory.inSession(
        session -> {
          List<Long> peopleIds = Arrays.asList(person1.getId(), person2.getId(), person3.getId());
          TableAggregatesView view =
              session
                  .createQuery(
                      "select new net.snowflake.hibernate.dialect.TableAggregatesView(min(id), avg(id), max(id), sum(id), count(id)) from SimplePersonAutoId where id in (?1) group by lastName",
                      TableAggregatesView.class)
                  .setParameter(1, peopleIds)
                  .getSingleResult();
          assertEquals(person1.getId(), view.min);
          assertEquals((double) person2.getId(), view.avg, 0.00000001);
          assertEquals(person3.getId(), view.max);
          assertEquals(peopleIds.stream().reduce(0L, Long::sum), view.sum);
          assertEquals(peopleIds.size(), view.count);

          long countDistinctLastName =
              session
                  .createQuery(
                      "select count(distinct lastName) from SimplePersonAutoId where id in (?1)",
                      Long.class)
                  .setParameter(1, peopleIds)
                  .getSingleResult();
          assertEquals(1, countDistinctLastName);
        });
  }
}

class TableAggregatesView {
  public TableAggregatesView(long min, double avg, long max, long sum, long count) {
    this.min = min;
    this.avg = avg;
    this.max = max;
    this.sum = sum;
    this.count = count;
  }

  long min;
  double avg;
  long max;
  long sum;
  long count;
}

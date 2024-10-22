package net.snowflake.hibernate.dialect.transactionrollback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.snowflake.hibernate.dialect.DroppingTablesBaseTest;
import net.snowflake.hibernate.dialect.TestTags;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(TestTags.HYBRID)
public class TransactionRollbackTest extends DroppingTablesBaseTest {
  // Unique constraint and PK breaking in this class has impact only on transactions in hybrid
  // tables

  public static List<Class<?>> mappedClasses =
      Arrays.asList(Person.class, Address.class, EntityWithManuallySetPk.class);

  @BeforeAll
  public static void setupClass() {
    classes = mappedClasses;
    sessionFactory = initSessionFactory();
  }

  @Test
  public void shouldRollbackTransactionOnUniqueBreak() {
    String addressLine = UUID.randomUUID().toString();
    String person1FirstName = UUID.randomUUID().toString();
    String person2FirstName = UUID.randomUUID().toString();
    Address address = new Address(addressLine);
    Person person1 = new Person(person1FirstName, "Doe", address);
    Person person2 = new Person(person2FirstName, "Kowalski", address);

    try {
      sessionFactory.inTransaction(
          session -> {
            session.persist(person1);
            session.persist(person2);
          });
    } catch (DataException e) {
      assertTrue(e.getMessage().contains("violates unique constraint"));
    }

    sessionFactory.inSession(
        session -> {
          assertNull(
              session
                  .createQuery("FROM Address where addressLine = :addressLine", Address.class)
                  .setParameter("addressLine", addressLine)
                  .getSingleResultOrNull());
          Arrays.asList(person1FirstName, person2FirstName)
              .forEach(
                  firstName ->
                      assertNull(
                          session
                              .createQuery("FROM Person where firstName = :firstName", Person.class)
                              .setParameter("firstName", firstName)
                              .getSingleResultOrNull()));
        });
  }

  @Test
  public void shouldRollbackTransactionOnPkDuplicate() {
    String id = UUID.randomUUID().toString();
    EntityWithManuallySetPk entity1 = new EntityWithManuallySetPk(id, "test1");
    EntityWithManuallySetPk entity2 = new EntityWithManuallySetPk(id, "test2");

    sessionFactory.inTransaction(session -> session.persist(entity1));

    try {
      sessionFactory.inTransaction(session -> session.persist(entity2));
    } catch (DataException e) {
      assertTrue(e.getMessage().contains("A primary key already exists."));
    }

    sessionFactory.inSession(
        session -> {
          List<EntityWithManuallySetPk> entries =
              session
                  .createQuery(
                      "FROM EntityWithManuallySetPk where id = :id", EntityWithManuallySetPk.class)
                  .setParameter("id", id)
                  .list();
          assertEquals(1, entries.size());
        });
  }
}

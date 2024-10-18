package net.snowflake.hibernate.dialect.deletecascade;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.snowflake.hibernate.dialect.DroppingTablesBaseTest;
import net.snowflake.hibernate.dialect.TestTags;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(TestTags.HYBRID)
public class DeleteCascadeTest extends DroppingTablesBaseTest {
  public static List<Class<?>> mappedClasses = Arrays.asList(Address.class, Person.class);

  @BeforeAll
  public static void setupClass() throws SQLException {
    try (Connection con = getDriverRawConnection();
        Statement statement = con.createStatement()) {
      statement.execute(
          "CREATE OR REPLACE HYBRID TABLE person_address_cascade (id varchar(255) not null, addressLine varchar(255), primary key (id) )");
      statement.execute(
          "CREATE OR REPLACE HYBRID TABLE person_cascade (id varchar(255) not null, firstname varchar(255), lastname varchar(255), address_id varchar(255) not null, primary key (id), constraint to_person_address_cascade foreign key (address_id) references person_address_cascade(id))");
    }
    classes = mappedClasses;
    hbm2ddlMode = "validate";
    sessionFactory = initSessionFactory();
  }

  @Test
  public void shouldDeleteCascade() {
    String addressLine = UUID.randomUUID().toString();
    String personFirstName = UUID.randomUUID().toString();
    Address address = new Address(addressLine);
    Person person = new Person(personFirstName, "Doe", address);

    sessionFactory.inTransaction(session -> session.persist(person));

    sessionFactory.inSession(
        session -> {
          Person fetched =
              session
                  .createQuery("FROM Person where firstName = :firstName", Person.class)
                  .setParameter("firstName", personFirstName)
                  .getSingleResultOrNull();
          assertNotNull(fetched);
          assertNotNull(fetched.address);
        });

    sessionFactory.inTransaction(
        session -> {
          Person fetched =
              session
                  .createQuery("FROM Person where firstName = :firstName", Person.class)
                  .setParameter("firstName", personFirstName)
                  .getSingleResultOrNull();
          session.remove(fetched);
        });

    sessionFactory.inSession(
        session -> {
          Person fetched =
              session
                  .createQuery("FROM Person where firstName = :firstName", Person.class)
                  .setParameter("firstName", personFirstName)
                  .getSingleResultOrNull();
          assertNull(fetched);
          Address fetchedAddress =
              session
                  .createQuery("FROM Address where addressLine = :addressLine", Address.class)
                  .setParameter("addressLine", addressLine)
                  .getSingleResultOrNull();
          assertNull(fetchedAddress);
        });
  }
}

package net.snowflake.hibernate.dialect.transactionrollback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
public class TransactionRollbackWithFkTest extends DroppingTablesBaseTest {
  // Hibernate is not able to create FK yet and we need to create tables manually

  public static List<Class<?>> mappedClasses =
      Arrays.asList(EntityWithManuallySetFk.class, EntityWithManuallySetFkChild.class);

  @BeforeAll
  public static void setupClass() throws SQLException {
    try (Connection con = getDriverRawConnection();
        Statement statement = con.createStatement()) {
      statement.execute(
          "CREATE OR REPLACE HYBRID TABLE entity_with_manually_set_fk (id varchar(255) not null, \"name\" varchar(255), primary key (id))");
      statement.execute(
          "CREATE OR REPLACE HYBRID TABLE entity_with_manually_set_fk_child (id varchar(255) not null, \"name\" varchar(255), parent_id varchar(255), primary key (id), constraint to_parent foreign key (parent_id) references entity_with_manually_set_fk(id) )");
    }
    classes = mappedClasses;
    hbm2ddlMode = "validate";
    sessionFactory = initSessionFactory();
  }

  @Test
  public void shouldRollbackTransactionOnFkViolation() {
    EntityWithManuallySetFk parent1 =
        new EntityWithManuallySetFk(UUID.randomUUID().toString(), "parent 1", null);
    EntityWithManuallySetFk parent2 =
        new EntityWithManuallySetFk(UUID.randomUUID().toString(), "parent 2", null);
    EntityWithManuallySetFkChild child =
        new EntityWithManuallySetFkChild(UUID.randomUUID().toString(), "test1", parent1);
    parent1.child = child;

    sessionFactory.inTransaction(
        session -> {
          session.persist(parent1);
          session.persist(child);
        });

    try {
      sessionFactory.inTransaction(
          session -> {
            EntityWithManuallySetFkChild fetchedChild =
                session.get(EntityWithManuallySetFkChild.class, child.id);
            fetchedChild.parent = parent2; // parent2 is not persisted
            session.persist(fetchedChild);
          });
    } catch (DataException e) {
      assertTrue(e.getMessage().contains("Foreign key constraint \"TO_PARENT\" was violated."));
    }

    sessionFactory.inSession(
        session -> {
          EntityWithManuallySetFkChild fetchedChild =
              session.get(EntityWithManuallySetFkChild.class, child.id);
          assertEquals(parent1.id, fetchedChild.parent.id);
        });
  }
}

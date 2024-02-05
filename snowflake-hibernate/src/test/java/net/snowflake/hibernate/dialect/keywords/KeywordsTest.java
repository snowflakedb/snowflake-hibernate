package net.snowflake.hibernate.dialect.keywords;

import net.snowflake.hibernate.dialect.DroppingTablesBaseTest;
import net.snowflake.hibernate.dialect.TestTags;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag(TestTags.HYBRID)
public class KeywordsTest extends DroppingTablesBaseTest {
    // This class does not use Hybrid table specific features - there is no need to test this class also with standard tables

    private static final List<Class<?>> mappedClasses = Arrays.asList(
            KeywordEntity.class
    );

    @BeforeAll
    public static void setupClass() {
        classes = mappedClasses;
        sessionFactory = initSessionFactory();
    }

    @Test
    public void shouldAddTableWithKeywordNameAndColumns() {
        KeywordEntity entity = new KeywordEntity();
        sessionFactory.inTransaction(session -> session.persist(entity));
        sessionFactory.inTransaction(session -> {
            assertNotNull(session.get(KeywordEntity.class, entity.getId()));
        });
    }
}


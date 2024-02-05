package net.snowflake.hibernate.dialect.idgeneration;

import net.snowflake.hibernate.dialect.AbstractSimplePerson;
import net.snowflake.hibernate.dialect.DroppingTablesBaseTest;
import net.snowflake.hibernate.dialect.SimpleEntityTest;
import net.snowflake.hibernate.dialect.TableType;
import net.snowflake.hibernate.dialect.TestTags;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag(TestTags.STANDARD)
public class IdGenerationOnStandardTableTest extends IdGenerationTest {
    @BeforeAll
    public static void setupClass() {
        classes = IdGenerationTest.mappedClasses;
        tableType = TableType.STANDARD;
        sessionFactory = initSessionFactory();
    }
}

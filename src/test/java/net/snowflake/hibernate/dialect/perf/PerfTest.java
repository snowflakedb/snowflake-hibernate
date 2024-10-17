package net.snowflake.hibernate.dialect.perf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.snowflake.hibernate.dialect.DroppingTablesBaseTest;
import net.snowflake.hibernate.dialect.TestTags;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Tag(TestTags.PERF)
public class PerfTest extends DroppingTablesBaseTest {

  public static List<Class<?>> mappedClasses =
      Arrays.asList(
          PerfEntityWithDbId.class,
          PerfEntityWithLocalId.class,
          PerfOneToOneUniDirectional.class,
          PerfOneToManyUniDirectional.class,
          ChildEntityWithLocalIdBiDirectionalOneToMany.class,
          ChildEntityWithLocalIdBiDirectionalOneToOne.class,
          PerfOneToManyBiDirectional.class,
          PerfOneToOneBiDirectional.class);

  @BeforeAll
  public static void setupClass() {
    classes = mappedClasses;
    sessionFactory = initSessionFactory();
  }

  @AfterEach
  public void clearTables() {
    sessionFactory.inTransaction(
        session -> {
          session.createMutationQuery("delete from PerfEntityWithLocalId").executeUpdate();
          session.createMutationQuery("delete from PerfEntityWithDbId ").executeUpdate();
          session.createMutationQuery("delete from PerfOneToManyUniDirectional ").executeUpdate();
          session.createMutationQuery("delete from PerfOneToOneUniDirectional ").executeUpdate();
          session
              .createMutationQuery("delete from ChildEntityWithLocalIdBiDirectionalOneToMany ")
              .executeUpdate();
          session
              .createMutationQuery("delete from ChildEntityWithLocalIdBiDirectionalOneToOne ")
              .executeUpdate();
          session.createMutationQuery("delete from PerfOneToManyBiDirectional ").executeUpdate();
          session.createMutationQuery("delete from PerfOneToOneBiDirectional ").executeUpdate();
        });
  }

  private void measureTime(String actionName, Consumer<Session> action) {
    System.out.println("Start measuring time");
    try (Session session = sessionFactory.openSession()) {
      long start = System.currentTimeMillis();
      Transaction transaction = session.beginTransaction();
      action.accept(session);
      transaction.commit();
      long stop = System.currentTimeMillis();
      System.out.println(actionName + " took " + (stop - start) + " ms");
    }
  }

  private static int getCountOfTenPercentOfAll(List<?> entities) {
    return (int) (0.1 * entities.size());
  }

  private static <T> List<T> generateEntities(Supplier<T> supplier, int count) {
    return Stream.generate(supplier).limit(count).collect(Collectors.toList());
  }

  private static void persistAll(List<?> entities) {
    sessionFactory.inTransaction(session -> entities.forEach(session::persist));
  }

  @Nested
  class SimpleEntityPerfTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 100})
    public void insertEntitiesWithLocalIds(int count) {
      List<PerfEntityWithLocalId> entities = generateEntities(PerfEntityWithLocalId::sample, count);
      measureTime(
          "insertEntitiesWithLocalIds with " + count + " entities",
          session -> entities.forEach(session::persist));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 100})
    public void insertEntitiesWithDbIds(int count) {
      List<PerfEntityWithDbId> entities = generateEntities(PerfEntityWithDbId::sample, count);
      measureTime(
          "insertEntitiesWithDbIds with " + count + " entities",
          session -> entities.forEach(session::persist));
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    public void selectAndUpdate10PercentOfEntitiesOneByOne(int count) {
      List<PerfEntityWithLocalId> entities = insertRandomPerfEntitiesWithLocalIds(count);
      int tenPercentOfAll = getCountOfTenPercentOfAll(entities);
      List<String> entityIdsToChange =
          pickRandomIds(entities, entity -> entity.id, tenPercentOfAll);
      measureTime(
          "selectAndUpdate10PercentOfEntitiesOneByOne with " + count + " entities",
          session ->
              entityIdsToChange.forEach(
                  id -> {
                    PerfEntityWithLocalId entity = session.get(PerfEntityWithLocalId.class, id);
                    PerfEntityWithLocalId.updateAllFields(entity);
                    session.persist(entity);
                  }));
    }

    private List<PerfEntityWithLocalId> insertRandomPerfEntitiesWithLocalIds(int count) {
      List<PerfEntityWithLocalId> entities = generateEntities(PerfEntityWithLocalId::sample, count);
      persistAll(entities);
      return entities;
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    public void selectAndUpdate10PercentOfEntitiesFetchedInSingleQuery(int count) {
      List<PerfEntityWithLocalId> entities = insertRandomPerfEntitiesWithLocalIds(count);
      int tenPercentOfAll = getCountOfTenPercentOfAll(entities);
      List<String> entityIdsToChange =
          pickRandomIds(entities, entity -> entity.id, tenPercentOfAll);
      measureTime(
          "selectAndUpdate10PercentOfEntitiesFetchedInSingleQuery with " + count + " entities",
          session -> {
            List<PerfEntityWithLocalId> foundEntities =
                session
                    .createQuery(
                        "from PerfEntityWithLocalId where id in (:ids)",
                        PerfEntityWithLocalId.class)
                    .setParameter("ids", entityIdsToChange)
                    .list();
            assertEquals(tenPercentOfAll, foundEntities.size());
            foundEntities.forEach(
                entity -> {
                  PerfEntityWithLocalId.updateAllFields(entity);
                  session.persist(entity);
                });
          });
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    public void selectAndDelete10PercentOfEntitiesOneByOne(int count) {
      List<PerfEntityWithLocalId> entities = insertRandomPerfEntitiesWithLocalIds(count);
      int tenPercentOfAll = getCountOfTenPercentOfAll(entities);
      List<String> entityIdsToChange =
          pickRandomIds(entities, entity -> entity.id, tenPercentOfAll);
      measureTime(
          "selectAndDelete10PercentOfEntitiesOneByOne with " + count + " entities",
          session ->
              entityIdsToChange.forEach(
                  id -> {
                    PerfEntityWithLocalId entity = session.get(PerfEntityWithLocalId.class, id);
                    assertNotNull(entity);
                    session.remove(entity);
                  }));
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    public void delete10PercentOfEntitiesInOneQuery(int count) {
      List<PerfEntityWithLocalId> entities = insertRandomPerfEntitiesWithLocalIds(count);
      int tenPercentOfAll = getCountOfTenPercentOfAll(entities);
      List<String> entityIdsToChange =
          pickRandomIds(entities, entity -> entity.id, tenPercentOfAll);
      measureTime(
          "delete10PercentOfEntitiesInOneQuery with " + count + " entities",
          session -> {
            int deletedRows =
                session
                    .createMutationQuery("delete from PerfEntityWithLocalId where id in (:ids)")
                    .setParameter("ids", entityIdsToChange)
                    .executeUpdate();
            assertEquals(tenPercentOfAll, deletedRows);
          });
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    public void select10PercentOfEntitiesOneByOne(int count) {
      List<PerfEntityWithLocalId> entities = insertRandomPerfEntitiesWithLocalIds(count);
      int tenPercentOfAll = getCountOfTenPercentOfAll(entities);
      List<String> entityIdsToChange =
          pickRandomIds(entities, entity -> entity.id, tenPercentOfAll);
      measureTime(
          "select10PercentOfEntitiesOneByOne with " + count + " entities",
          session ->
              entityIdsToChange.forEach(
                  id -> assertNotNull(session.get(PerfEntityWithLocalId.class, id))));
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    public void select10PercentOfEntitiesFetchedInSingleQuery(int count) {
      List<PerfEntityWithLocalId> entities = insertRandomPerfEntitiesWithLocalIds(count);
      int tenPercentOfAll = getCountOfTenPercentOfAll(entities);
      List<String> entityIdsToChange =
          pickRandomIds(entities, entity -> entity.id, tenPercentOfAll);
      measureTime(
          "select10PercentOfEntitiesFetchedInSingleQuery with " + count + " entities",
          session -> {
            List<PerfEntityWithLocalId> result =
                session
                    .createQuery(
                        "from PerfEntityWithLocalId where id in (:ids)",
                        PerfEntityWithLocalId.class)
                    .setParameter("ids", entityIdsToChange)
                    .list();
            assertEquals(tenPercentOfAll, result.size());
          });
    }
  }

  @Nested
  class JoinsUniDirectionalPerfTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 100})
    public void insertEntitiesWithOneToOneRelation(int count) {
      List<PerfOneToOneUniDirectional> entities =
          generateEntities(PerfOneToOneUniDirectional::sample, count);

      measureTime(
          "insertEntitiesWithOneToOneRelation with " + count + " entities",
          session -> entities.forEach(session::persist));
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    public void select10PercentOfEntitiesWithOneToOneRelationOneByOne(int count) {
      List<PerfOneToOneUniDirectional> entities =
          generateEntities(PerfOneToOneUniDirectional::sample, count);
      persistAll(entities);
      int tenPercentOfAll = getCountOfTenPercentOfAll(entities);
      List<String> entityIdsToFind = pickRandomIds(entities, entity -> entity.id, tenPercentOfAll);

      measureTime(
          "select10PercentOfEntitiesWithOneToOneRelationOneByOne with " + count + " entities",
          session ->
              entityIdsToFind.forEach(
                  id -> assertNotNull(session.get(PerfOneToOneUniDirectional.class, id))));
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    public void select10PercentOfEntitiesWithOneToOneRelationByOneQuery(int count) {
      List<PerfOneToOneUniDirectional> entities =
          generateEntities(PerfOneToOneUniDirectional::sample, count);
      persistAll(entities);
      int tenPercentOfAll = getCountOfTenPercentOfAll(entities);
      List<String> entityIdsToFind = pickRandomIds(entities, entity -> entity.id, tenPercentOfAll);

      measureTime(
          "select10PercentOfEntitiesWithOneToOneRelationByOneQuery with " + count + " entities",
          session -> {
            List<PerfOneToOneUniDirectional> foundEntities =
                session
                    .createQuery(
                        "from PerfOneToOneUniDirectional where id in (:ids)",
                        PerfOneToOneUniDirectional.class)
                    .setParameter("ids", entityIdsToFind)
                    .list();
            assertEquals(tenPercentOfAll, foundEntities.size());
          });
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 100})
    public void insertEntitiesWithOneToManyRelation(int count) {
      List<PerfOneToManyUniDirectional> entities =
          generateEntities(PerfOneToManyUniDirectional::sample, count);

      measureTime(
          "insertEntitiesWithOneToManyRelation with " + count + " entities",
          session -> entities.forEach(session::persist));
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    public void select10PercentOfEntitiesWithOneToManyRelationOneByOne(int count) {
      List<PerfOneToManyUniDirectional> entities =
          generateEntities(PerfOneToManyUniDirectional::sample, count);
      persistAll(entities);
      int tenPercentOfAll = getCountOfTenPercentOfAll(entities);
      List<String> entityIdsToFind = pickRandomIds(entities, entity -> entity.id, tenPercentOfAll);

      measureTime(
          "select10PercentOfEntitiesWithOneToManyRelationOneByOne with " + count + " entities",
          session ->
              entityIdsToFind.forEach(
                  id -> assertNotNull(session.get(PerfOneToManyUniDirectional.class, id))));
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    public void select10PercentOfEntitiesWithOneToManyRelationByOneQuery(int count) {
      List<PerfOneToManyUniDirectional> entities =
          generateEntities(PerfOneToManyUniDirectional::sample, count);
      persistAll(entities);
      int tenPercentOfAll = getCountOfTenPercentOfAll(entities);
      List<String> entityIdsToFind = pickRandomIds(entities, entity -> entity.id, tenPercentOfAll);

      measureTime(
          "select10PercentOfEntitiesWithOneToManyRelationByOneQuery with " + count + " entities",
          session -> {
            List<PerfOneToManyUniDirectional> foundEntities =
                session
                    .createQuery(
                        "from PerfOneToManyUniDirectional where id in (:ids)",
                        PerfOneToManyUniDirectional.class)
                    .setParameter("ids", entityIdsToFind)
                    .list();
            assertEquals(tenPercentOfAll, foundEntities.size());
          });
    }
  }

  @Nested
  class JoinsBiDirectionalPerfTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 100})
    public void insertEntitiesWithOneToOneRelation(int count) {
      List<PerfOneToOneBiDirectional> entities =
          generateEntities(PerfOneToOneBiDirectional::sample, count);

      measureTime(
          "insertEntitiesWithOneToOneRelation with " + count + " entities",
          session -> entities.forEach(session::persist));
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    public void select10PercentOfEntitiesWithOneToOneRelationOneByOne(int count) {
      List<PerfOneToOneBiDirectional> entities =
          generateEntities(PerfOneToOneBiDirectional::sample, count);
      persistAll(entities);
      Collections.shuffle(entities);
      int tenPercentOfAll = getCountOfTenPercentOfAll(entities);
      List<String> entityIdsToFind = pickRandomIds(entities, entity -> entity.id, tenPercentOfAll);

      measureTime(
          "select10PercentOfEntitiesWithOneToOneRelationOneByOne with " + count + " entities",
          session ->
              entityIdsToFind.forEach(
                  id -> assertNotNull(session.get(PerfOneToOneBiDirectional.class, id))));
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    public void select10PercentOfEntitiesWithOneToOneRelationByOneQuery(int count) {
      List<PerfOneToOneBiDirectional> entities =
          generateEntities(PerfOneToOneBiDirectional::sample, count);
      persistAll(entities);
      Collections.shuffle(entities);
      int tenPercentOfAll = getCountOfTenPercentOfAll(entities);
      List<String> entityIdsToFind = pickRandomIds(entities, entity -> entity.id, tenPercentOfAll);

      measureTime(
          "select10PercentOfEntitiesWithOneToOneRelationByOneQuery with " + count + " entities",
          session -> {
            List<PerfOneToOneBiDirectional> foundEntities =
                session
                    .createQuery(
                        "from PerfOneToOneBiDirectional where id in (:ids)",
                        PerfOneToOneBiDirectional.class)
                    .setParameter("ids", entityIdsToFind)
                    .list();
            assertEquals(tenPercentOfAll, foundEntities.size());
          });
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 100})
    public void insertEntitiesWithOneToManyRelation(int count) {
      List<PerfOneToManyBiDirectional> entities =
          generateEntities(PerfOneToManyBiDirectional::sample, count);

      measureTime(
          "insertEntitiesWithOneToManyRelation with " + count + " entities",
          session -> entities.forEach(session::persist));
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    public void select10PercentOfEntitiesWithOneToManyRelationOneByOne(int count) {
      List<PerfOneToManyBiDirectional> entities =
          generateEntities(PerfOneToManyBiDirectional::sample, count);
      persistAll(entities);
      int tenPercentOfAll = getCountOfTenPercentOfAll(entities);
      List<String> entityIdsToFind = pickRandomIds(entities, entity -> entity.id, tenPercentOfAll);

      measureTime(
          "select10PercentOfEntitiesWithOneToManyRelationOneByOne with " + count + " entities",
          session ->
              entityIdsToFind.forEach(
                  id -> assertNotNull(session.get(PerfOneToManyBiDirectional.class, id))));
    }

    @ParameterizedTest
    @ValueSource(ints = {100})
    public void select10PercentOfEntitiesWithOneToManyRelationByOneQuery(int count) {
      List<PerfOneToManyBiDirectional> entities =
          generateEntities(PerfOneToManyBiDirectional::sample, count);
      persistAll(entities);
      int tenPercentOfAll = getCountOfTenPercentOfAll(entities);
      List<String> entityIdsToFind = pickRandomIds(entities, entity -> entity.id, tenPercentOfAll);

      measureTime(
          "select10PercentOfEntitiesWithOneToManyRelationByOneQuery with " + count + " entities",
          session -> {
            List<PerfOneToManyBiDirectional> foundEntities =
                session
                    .createQuery(
                        "from PerfOneToManyBiDirectional where id in (:ids)",
                        PerfOneToManyBiDirectional.class)
                    .setParameter("ids", entityIdsToFind)
                    .list();
            assertEquals(tenPercentOfAll, foundEntities.size());
          });
    }
  }

  private static <T, ID> List<ID> pickRandomIds(
      List<T> entities, Function<T, ID> idExtractor, int count) {
    Collections.shuffle(entities);
    return entities.stream().limit(count).map(idExtractor).collect(Collectors.toList());
  }
}

package net.snowflake.hibernate.dialect.complexhierarchy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.snowflake.hibernate.dialect.DroppingTablesBaseTest;
import net.snowflake.hibernate.dialect.TestTags;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(TestTags.HYBRID)
public class ComplexHierarchyTest extends DroppingTablesBaseTest {

  public static List<Class<?>> mappedClasses =
      Arrays.asList(
          Project.class,
          Company.class,
          Employee.class,
          Address.class,
          ProjectEmployeeV1.class,
          ProjectEmployeeV2.class,
          ProjectEmployeeV3.class,
          Project2.class,
          Employee2.class);

  @BeforeAll
  public static void setupClass() {
    classes = mappedClasses;
    sessionFactory = initSessionFactory();
  }

  @Test
  public void shouldSaveComplexHierarchy() {
    Company company = new Company("Test");
    Employee employee1 = new Employee("John", "Doe", new Address("San Mateo"), company);
    Employee employee2 = new Employee("Jan", "Kowalski", new Address("Warsaw"), company);
    Employee employee3 = new Employee(null, "Smith", null, company);
    // Foreign key cannot be null
    // Employee employee4 = new Employee(null, "Smith", null, company);

    sessionFactory.inTransaction(
        session -> {
          company.getEmployees().addAll(Arrays.asList(employee1, employee2, employee3));
          session.persist(company);
        });

    Project project1 = new Project("Project 1", company);
    Project project2 = new Project("Project 2", company);
    Project project3 = new Project("Project 3", company);
    sessionFactory.inTransaction(
        session -> {
          Company companyFetched = session.get(Company.class, company.getId());
          List<Project> projects = Arrays.asList(project1, project2, project3);
          companyFetched.getProjects().addAll(projects);
          session.persist(companyFetched);
        });

    assertNotNull(company.getId());

    sessionFactory.inTransaction(
        session -> {
          Company companyFetched = session.get(Company.class, company.getId());
          assertEquals(3, companyFetched.getEmployees().size());
          assertEquals(3, companyFetched.getProjects().size());
        });

    sessionFactory.inTransaction(
        session -> {
          Employee employee =
              session
                  .createSelectionQuery("from Employee where id = :id", Employee.class)
                  .setParameter("id", employee1.getId())
                  .getSingleResultOrNull();
          assertEquals(3, employee.getCompany().getProjects().size());
        });

    sessionFactory.inTransaction(
        session -> {
          Employee employee = session.get(Employee.class, employee1.getId());
          Project project = session.get(Project.class, project1.getId());
          session.persist(new ProjectEmployeeV1(employee, project));
          session.persist(new ProjectEmployeeV2(employee, project));
          session.persist(new ProjectEmployeeV3(employee, project));
        });

    sessionFactory.inTransaction(
        session -> {
          assertNotNull(
              session
                  .createSelectionQuery(
                      "from ProjectEmployeeV1 where projectEmployeeKey = ?1",
                      ProjectEmployeeV1.class)
                  .setParameter(1, new ProjectEmployeeKey(project1.getId(), employee1.getId()))
                  .getSingleResultOrNull());
          assertNotNull(
              session
                  .createSelectionQuery(
                      "from ProjectEmployeeV2 where employee.id = ?1 and project.id = ?2",
                      ProjectEmployeeV2.class)
                  .setParameter(1, employee1.getId())
                  .setParameter(2, project1.getId())
                  .getSingleResultOrNull());
          assertNotNull(
              session
                  .createSelectionQuery(
                      "from ProjectEmployeeV3 where id = ?1", ProjectEmployeeV3.class)
                  .setParameter(1, new ProjectEmployeeKeyJoin(project1, employee1))
                  .getSingleResultOrNull());
        });

    sessionFactory.inTransaction(
        session -> {
          String projectName1 =
              session
                  .createSelectionQuery(
                      "SELECT pev.project.name from ProjectEmployeeV1 pev inner join pev.employee.address a where a.addressLine = ?1",
                      String.class)
                  .setParameter(1, employee1.getAddress().getAddressLine())
                  .getSingleResultOrNull();
          assertEquals(project1.getName(), projectName1);

          String projectName2 =
              session
                  .createSelectionQuery(
                      "SELECT pev.project.name from ProjectEmployeeV2 pev inner join pev.employee.address a where a.addressLine = ?1",
                      String.class)
                  .setParameter(1, employee1.getAddress().getAddressLine())
                  .getSingleResultOrNull();
          assertEquals(project1.getName(), projectName2);

          String projectName3 =
              session
                  .createSelectionQuery(
                      "SELECT pev.project.name from ProjectEmployeeV3 pev inner join pev.employee.address a where a.addressLine = ?1",
                      String.class)
                  .setParameter(1, employee1.getAddress().getAddressLine())
                  .getSingleResultOrNull();
          assertEquals(project1.getName(), projectName3);
        });
  }

  @Test
  public void shouldUpdateMultipleHierarchiesInOneTransaction() {
    Company company;
    Employee employee;
    Project project;
    try (Session session = sessionFactory.openSession()) {
      Transaction transaction = session.beginTransaction();
      company = new Company("Testing 1");
      employee = new Employee("FirstName1", "LastName1", new Address("address 1"), company);
      project = new Project("Project1", company);
      company.getEmployees().add(employee);
      company.getProjects().add(project);
      session.persist(company);
      session.persist(new ProjectEmployeeV1(employee, project));
      session.persist(new ProjectEmployeeV2(employee, project));
      session.persist(new ProjectEmployeeV3(employee, project));
      transaction.commit();
    }

    try (Session session = sessionFactory.openSession()) {
      assertNotNull(
          session
              .createSelectionQuery(
                  "from ProjectEmployeeV1 where projectEmployeeKey = ?1", ProjectEmployeeV1.class)
              .setParameter(1, new ProjectEmployeeKey(project.getId(), employee.getId()))
              .getSingleResultOrNull());
      assertNotNull(
          session
              .createSelectionQuery(
                  "from ProjectEmployeeV2 where employee.id = ?1 and project.id = ?2",
                  ProjectEmployeeV2.class)
              .setParameter(1, employee.getId())
              .setParameter(2, project.getId())
              .getSingleResultOrNull());
      assertNotNull(
          session
              .createSelectionQuery("from ProjectEmployeeV3 where id = ?1", ProjectEmployeeV3.class)
              .setParameter(1, new ProjectEmployeeKeyJoin(project, employee))
              .getSingleResultOrNull());
    }
  }

  @Test
  public void shouldSaveEntitiesWithManyToMany() {
    Employee2 employee1 = new Employee2("John", "Doe");
    Employee2 employee2 = new Employee2("Jan", "Kowalski");
    Employee2 employee3 = new Employee2("Jane", "Smith");
    sessionFactory.inTransaction(
        session -> {
          session.persist(employee1);
          session.persist(employee2);
          session.persist(employee3);
        });

    Project2 project1 = new Project2("Project 1");
    Project2 project2 = new Project2("Project 2");
    Project2 project3 = new Project2("Project 3");
    sessionFactory.inTransaction(
        session -> {
          List<Employee2> employees =
              session
                  .byMultipleIds(Employee2.class)
                  .multiLoad(employee1.getId(), employee2.getId(), employee3.getId());
          project1.getEmployees().addAll(employees);
          project2
              .getEmployees()
              .addAll(
                  employees.stream()
                      .filter(it -> it.getId().equals(employee1.getId()))
                      .collect(Collectors.toList()));
          project3
              .getEmployees()
              .addAll(
                  employees.stream()
                      .filter(it -> it.getId().equals(employee2.getId()))
                      .collect(Collectors.toList()));
          session.persist(project1);
          session.persist(project2);
          session.persist(project3);
        });

    sessionFactory.inTransaction(
        session -> {
          List<Employee2> employees = session.createQuery("from Employee2", Employee2.class).list();
          assertEquals(3, employees.size());

          Function<Employee2, Set<Project2>> findEmployeeProjects =
              employee ->
                  employees.stream()
                      .filter(e -> e.getId().equals(employee.getId()))
                      .findAny()
                      .orElseThrow(
                          () -> new RuntimeException("No employee with id " + employee.getId()))
                      .getProjects();
          assertEquals(2, findEmployeeProjects.apply(employee1).size());
          assertEquals(2, findEmployeeProjects.apply(employee2).size());
          assertEquals(1, findEmployeeProjects.apply(employee3).size());
        });
  }
}

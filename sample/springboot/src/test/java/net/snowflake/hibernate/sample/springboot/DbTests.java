package net.snowflake.hibernate.sample.springboot;

import net.snowflake.hibernate.sample.springboot.entity.Company;
import net.snowflake.hibernate.sample.springboot.entity.Customer;
import net.snowflake.hibernate.sample.springboot.entity.Employee;
import net.snowflake.hibernate.sample.springboot.entity.PersonUUID;
import net.snowflake.hibernate.sample.springboot.entity.Product;
import net.snowflake.hibernate.sample.springboot.service.CompanyService;
import net.snowflake.hibernate.sample.springboot.service.PeopleService;
import net.snowflake.hibernate.sample.springboot.service.PeopleUUIDService;
import net.snowflake.hibernate.sample.springboot.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest
public class DbTests {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private PeopleService peopleService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PeopleUUIDService peopleUUIDService;

    @AfterEach
    public void cleanup() {
        companyService.deleteAll();
        peopleService.deleteAll();
    }

    @Test
    public void testSaveCompany() {
        Company company = companyService.save("TestCompany");
        Company receivedCompany = companyService.findCompanyByName("TestCompany").get(0);
        Assertions.assertEquals(company.getId(), receivedCompany.getId());
        Assertions.assertEquals(company.getName(), receivedCompany.getName());
    }

    @Test
    public void testRemoveCompany() {
        Company company = companyService.save("TestCompany");
        Assertions.assertEquals(1, companyService.findAll().size());

        companyService.deleteById(company.getId());
        Assertions.assertEquals(0, companyService.findAll().size());
    }

    @Test
    public void testSaveCompanyAndEmployee() {
        Company company = companyService.save("TestCompany");
        peopleService.saveEmployee("John", "Doe", company);
        Employee employeeInDb = peopleService.findEmployeeByLastName("Doe").get(0);
        Assertions.assertEquals(company.getId(), employeeInDb.getCompany().getId());
    }

    @Test
    public void testRemoveEmployeeFromCompany() {
        Company company = companyService.save("TestCompany");
        Employee employee = peopleService.saveEmployee("John", "Doe", company);
        company = companyService.findCompanyById(company.getId()).orElseThrow();
        Assertions.assertEquals(1, company.getEmployees().size());

        peopleService.removeEmployee(employee);
        company = companyService.findCompanyById(company.getId()).orElseThrow();
        Assertions.assertEquals(0, company.getEmployees().size());
    }

    @Test
    public void testTransition() {
        Company company = companyService.save("TestCompany");
        productService.save("TestProduct", "Just a description", company);
        Employee employee = peopleService.saveEmployee("John", "Doe", company);
        Customer customer = peopleService.saveCustomer("Adam", "Smith", employee);
        Assertions.assertEquals(1, companyService.findAll().size());
        company = companyService.findCompanyById(company.getId()).orElseThrow();

        Set<Employee> employees = company.getEmployees();
        Assertions.assertEquals(1, employees.size());

        Set<Customer> customers = employees.stream().findFirst().orElseThrow().getCustomers();
        Assertions.assertEquals(1, customers.size());

        Set<Product> products = company.getProducts();
        Assertions.assertEquals(1, products.size());

        Assertions.assertEquals(customer.getFirstName(), customers.stream().findFirst().orElseThrow().getFirstName());
    }

    @Test
    void testSaveAndGetPersonByUUID() {
        PersonUUID person1 = peopleUUIDService.save("John", UUID.randomUUID().toString());

        Optional<PersonUUID> maybePerson = peopleUUIDService.findById(person1.getId());

        Assertions.assertTrue(maybePerson.isPresent());
        Assertions.assertEquals(person1.getId(), maybePerson.get().getId());
        Assertions.assertEquals(person1.getFirstName(), maybePerson.get().getFirstName());
        Assertions.assertEquals(person1.getLastName(), maybePerson.get().getLastName());
    }

    @Test
    void testGetPersonUUIDByLastName() {
        PersonUUID person1 = peopleUUIDService.save("John", UUID.randomUUID().toString());
        String lastName = UUID.randomUUID().toString();
        PersonUUID person2 = peopleUUIDService.save("Bla", lastName);
        PersonUUID person3 = peopleUUIDService.save("Ble", lastName);

        List<PersonUUID> people1 = peopleUUIDService.findByLastName(person1.getLastName());
        List<PersonUUID> people2 = peopleUUIDService.findByLastName(lastName);

        Assertions.assertEquals(Set.of(person1.getId()), people1.stream().map(p -> p.getId()).collect(Collectors.toSet()));
        Assertions.assertEquals(Set.of(person2.getId(), person3.getId()), people2.stream().map(p -> p.getId()).collect(Collectors.toSet()));
    }
}

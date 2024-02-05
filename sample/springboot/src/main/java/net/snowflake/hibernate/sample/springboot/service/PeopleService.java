package net.snowflake.hibernate.sample.springboot.service;

import net.snowflake.hibernate.sample.springboot.entity.Company;
import net.snowflake.hibernate.sample.springboot.entity.Customer;
import net.snowflake.hibernate.sample.springboot.entity.Employee;
import jakarta.transaction.Transactional;
import net.snowflake.hibernate.sample.springboot.repository.PeopleRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PeopleService {
    private final PeopleRepository<Employee> employeeRepository;
    private final PeopleRepository<Customer> customerRepository;
    private final CompanyService companyService;

    public Employee saveEmployee(String firstName, String lastName, Company company) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setCompany(company);
        return employeeRepository.save(employee);
    }

    public Customer saveCustomer(String firstName, String lastName, Employee contactPerson) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setContactPerson(contactPerson);
        return customerRepository.save(customer);
    }

    public Optional<Employee> findEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public List<Employee> findEmployeeByLastName(String lastName) {
        return employeeRepository.findByLastName(lastName);
    }

    public Optional<Customer> findCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public List<Customer> findCustomerByLastName(String lastName) {
        return customerRepository.findByLastName(lastName);
    }

    public void removeEmployee(Employee employee) {
        Company company = employee.getCompany();
        company.getEmployees().remove(employee);
        companyService.save(company);
        employeeRepository.deleteById(employee.getId());
    }

    public void deleteAll() {
        employeeRepository.deleteAll();
        customerRepository.deleteAll();
    }
}

package net.snowflake.hibernate.dialect.complexhierarchy;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(
    name = "employee",
    indexes = {@Index(columnList = "firstName,lastName")})
public class Employee {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String firstName;
  private String lastName;

  @OneToOne(cascade = CascadeType.PERSIST)
  private Address address;

  @ManyToOne
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  public Employee(String firstName, String lastName, Address address, Company company) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.address = address;
    this.company = company;
  }

  public Employee() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }
}

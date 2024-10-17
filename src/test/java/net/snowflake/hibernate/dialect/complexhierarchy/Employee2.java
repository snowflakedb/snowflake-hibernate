package net.snowflake.hibernate.dialect.complexhierarchy;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "employee2")
public class Employee2 {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String firstName;
  private String lastName;

  @ManyToMany(mappedBy = "employees")
  private Set<Project2> projects = new HashSet<>();

  public Employee2(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public Employee2() {}

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

  public Set<Project2> getProjects() {
    return projects;
  }

  public void setProjects(Set<Project2> projects) {
    this.projects = projects;
  }
}

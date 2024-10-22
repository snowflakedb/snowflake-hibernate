package net.snowflake.hibernate.dialect.complexhierarchy;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "company")
public class Company {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String name;

  @OneToMany(mappedBy = "company", cascade = CascadeType.PERSIST)
  private Set<Employee> employees = new HashSet<>();

  @OneToMany(mappedBy = "company", cascade = CascadeType.PERSIST)
  private Set<Project> projects = new HashSet<>();

  public Company(String name) {
    this.name = name;
  }

  public Company() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<Employee> getEmployees() {
    return employees;
  }

  public Set<Project> getProjects() {
    return projects;
  }
}

package net.snowflake.hibernate.dialect.complexhierarchy;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "project2")
public class Project2 {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String name;

  @ManyToMany(
      cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinTable(
      name = "project_employee",
      joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"))
  private Set<Employee2> employees = new HashSet<>();

  public Project2(String name) {
    this.name = name;
  }

  public Project2() {}

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

  public Set<Employee2> getEmployees() {
    return employees;
  }

  public void setEmployees(Set<Employee2> employees) {
    this.employees = employees;
  }
}

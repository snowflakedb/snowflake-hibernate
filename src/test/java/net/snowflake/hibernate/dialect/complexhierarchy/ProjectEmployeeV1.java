package net.snowflake.hibernate.dialect.complexhierarchy;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "project_employee1")
public class ProjectEmployeeV1 {
  // Join column as a separate entity with explicit compound key

  @EmbeddedId private ProjectEmployeeKey projectEmployeeKey = new ProjectEmployeeKey();

  @ManyToOne
  @MapsId("employeeId")
  @JoinColumn(name = "employee_id")
  private Employee employee;

  @ManyToOne
  @MapsId("projectId")
  @JoinColumn(name = "project_id")
  private Project project;

  public ProjectEmployeeKey getProjectEmployeeKey() {
    return projectEmployeeKey;
  }

  public void setProjectEmployeeKey(ProjectEmployeeKey projectEmployeeKey) {
    this.projectEmployeeKey = projectEmployeeKey;
  }

  public Employee getEmployee() {
    return employee;
  }

  public void setEmployee(Employee employee) {
    this.employee = employee;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public ProjectEmployeeV1(Employee employee, Project project) {
    this.employee = employee;
    this.project = project;
  }

  public ProjectEmployeeV1() {}
}

package net.snowflake.hibernate.dialect.complexhierarchy;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "project_employee3")
@IdClass(ProjectEmployeeKeyJoin.class)
public class ProjectEmployeeV3 {
  // Alternative version where IdClass is used instead of embeddable id

  @ManyToOne
  @Id
  @JoinColumn(name = "employee_id")
  private Employee employee;

  @ManyToOne
  @Id
  @JoinColumn(name = "project_id")
  private Project project;

  public ProjectEmployeeV3(Employee employee, Project project) {
    this.employee = employee;
    this.project = project;
  }

  public ProjectEmployeeV3() {}

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
}

package net.snowflake.hibernate.dialect.complexhierarchy;

import java.io.Serializable;
import java.util.Objects;

public class ProjectEmployeeKeyJoin implements Serializable {
  private Project project;

  private Employee employee;

  public ProjectEmployeeKeyJoin(Project project, Employee employee) {
    this.project = project;
    this.employee = employee;
  }

  public ProjectEmployeeKeyJoin() {}

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectEmployeeKeyJoin that = (ProjectEmployeeKeyJoin) o;
    return Objects.equals(project, that.project) && Objects.equals(employee, that.employee);
  }

  @Override
  public int hashCode() {
    return Objects.hash(project, employee);
  }
}

package net.snowflake.hibernate.dialect.complexhierarchy;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ProjectEmployeeKey implements Serializable {

  @Column(name = "project_id", nullable = false)
  UUID projectId;

  @Column(name = "employee_id", nullable = false)
  UUID employeeId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectEmployeeKey that = (ProjectEmployeeKey) o;
    return Objects.equals(projectId, that.projectId) && Objects.equals(employeeId, that.employeeId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(projectId, employeeId);
  }

  public UUID getProjectId() {
    return projectId;
  }

  public void setProjectId(UUID projectId) {
    this.projectId = projectId;
  }

  public UUID getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(UUID employeeId) {
    this.employeeId = employeeId;
  }

  public ProjectEmployeeKey(UUID projectId, UUID employeeId) {
    this.projectId = projectId;
    this.employeeId = employeeId;
  }

  public ProjectEmployeeKey() {}
}

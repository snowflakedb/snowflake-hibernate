package net.snowflake.hibernate.dialect;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractSimplePerson<ID> {
  @Column(name = "first_name", nullable = false)
  protected String firstName;

  @Column(name = "last_name", nullable = false)
  protected String lastName;

  protected AbstractSimplePerson(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  protected AbstractSimplePerson() {}

  public abstract ID getId();

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
}

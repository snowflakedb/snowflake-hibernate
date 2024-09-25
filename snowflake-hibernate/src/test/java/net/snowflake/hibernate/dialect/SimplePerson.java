package net.snowflake.hibernate.dialect;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "simple_person")
public class SimplePerson extends AbstractSimplePerson<Long> {
  @Id private Long id;

  public SimplePerson(Long id, String firstName, String lastName) {
    super(firstName, lastName);
    this.id = id;
  }

  public SimplePerson() {}

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimplePerson that = (SimplePerson) o;
    return Objects.equals(id, that.id)
        && Objects.equals(firstName, that.firstName)
        && Objects.equals(lastName, that.lastName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, firstName, lastName);
  }
}

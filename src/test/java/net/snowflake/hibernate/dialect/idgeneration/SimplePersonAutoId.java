package net.snowflake.hibernate.dialect.idgeneration;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import net.snowflake.hibernate.dialect.AbstractSimplePerson;

@Entity
@Table(name = "simple_person_auto_id")
public class SimplePersonAutoId extends AbstractSimplePerson<Long> {
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  private Long id;

  public SimplePersonAutoId(Long id, String firstName, String lastName) {
    super(firstName, lastName);
    this.id = id;
  }

  public SimplePersonAutoId() {}

  public Long getId() {
    return id;
  }
}

package net.snowflake.hibernate.dialect.idgeneration;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import net.snowflake.hibernate.dialect.AbstractSimplePerson;

@Entity
@Table(name = "simple_person_sequence_id")
class SimplePersonSequenceId extends AbstractSimplePerson<Long> {
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "simple_person_sequenced-gen")
  @SequenceGenerator(name = "simple_person_sequenced-gen", sequenceName = "simple_person_sequence")
  @Id
  private Long id;

  public SimplePersonSequenceId(Long id, String firstName, String lastName) {
    super(firstName, lastName);
    this.id = id;
  }

  public SimplePersonSequenceId() {}

  @Override
  public Long getId() {
    return id;
  }
}

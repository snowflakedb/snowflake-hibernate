package net.snowflake.hibernate.dialect.idgeneration;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import net.snowflake.hibernate.dialect.AbstractSimplePerson;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

@Entity
@Table(name = "simple_person_hilo_sequence_id")
class SimplePersonHiLoSequenceId extends AbstractSimplePerson<Long> {
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "simple_person_hilo_sequenced-gen")
  @GenericGenerator(
      name = "simple_person_hilo_sequenced-gen",
      type = SequenceStyleGenerator.class,
      parameters = {
        @Parameter(name = "sequence_name", value = "simple_person_hilo_sequence"),
        @Parameter(name = "initial_value", value = "1"),
        @Parameter(name = "increment_size", value = "50"),
        @Parameter(name = "optimizer", value = "hilo")
      })
  @Id
  private Long id;

  public SimplePersonHiLoSequenceId(Long id, String firstName, String lastName) {
    super(firstName, lastName);
    this.id = id;
  }

  public SimplePersonHiLoSequenceId() {}

  @Override
  public Long getId() {
    return id;
  }
}

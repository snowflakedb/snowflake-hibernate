package net.snowflake.hibernate.dialect.idgeneration;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import net.snowflake.hibernate.dialect.AbstractSimplePerson;

@Entity
@Table(name = "simple_person_table_id")
class SimplePersonTableId extends AbstractSimplePerson<Long> {
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "simple-person-table-generator")
  @TableGenerator(
      name = "simple-person-table-generator",
      table = "simple_person_table_id_provider",
      pkColumnName = "seq_id",
      valueColumnName = "seq_value")
  @Id
  private Long id;

  public SimplePersonTableId(Long id, String firstName, String lastName) {
    super(firstName, lastName);
    this.id = id;
  }

  public SimplePersonTableId() {}

  @Override
  public Long getId() {
    return id;
  }
}

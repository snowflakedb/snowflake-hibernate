package net.snowflake.hibernate.dialect.idgeneration;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import net.snowflake.hibernate.dialect.AbstractSimplePerson;

@Entity
@Table(name = "simple_person_uuid_as_string_id")
class SimplePersonUuidAsStringId extends AbstractSimplePerson<String> {
  @GeneratedValue(strategy = GenerationType.UUID)
  @Id
  private String id;

  public SimplePersonUuidAsStringId(String id, String firstName, String lastName) {
    super(firstName, lastName);
    this.id = id;
  }

  public SimplePersonUuidAsStringId() {}

  @Override
  public String getId() {
    return id;
  }
}

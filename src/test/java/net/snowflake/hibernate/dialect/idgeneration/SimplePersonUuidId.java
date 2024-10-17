package net.snowflake.hibernate.dialect.idgeneration;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import net.snowflake.hibernate.dialect.AbstractSimplePerson;

@Entity
@Table(name = "simple_person_uuid_id")
class SimplePersonUuidId extends AbstractSimplePerson<UUID> {
  @GeneratedValue(strategy = GenerationType.UUID)
  @Id
  private UUID id;

  public SimplePersonUuidId(UUID id, String firstName, String lastName) {
    super(firstName, lastName);
    this.id = id;
  }

  public SimplePersonUuidId() {}

  @Override
  public UUID getId() {
    return id;
  }
}

package net.snowflake.hibernate.dialect.transactionrollback;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "entity_with_manually_set_pk")
class EntityWithManuallySetPk {
  @Id String id;

  String name;

  public EntityWithManuallySetPk(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public EntityWithManuallySetPk() {}
}

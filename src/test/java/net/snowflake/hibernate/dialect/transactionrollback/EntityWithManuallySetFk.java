package net.snowflake.hibernate.dialect.transactionrollback;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "entity_with_manually_set_fk")
class EntityWithManuallySetFk {
  @Id String id;

  @Column(name = "`name`")
  String name;

  @OneToOne(mappedBy = "parent")
  EntityWithManuallySetFkChild child;

  public EntityWithManuallySetFk(String id, String name, EntityWithManuallySetFkChild child) {
    this.id = id;
    this.name = name;
    this.child = child;
  }

  public EntityWithManuallySetFk() {}
}

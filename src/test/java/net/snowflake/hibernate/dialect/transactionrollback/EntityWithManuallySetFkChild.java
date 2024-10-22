package net.snowflake.hibernate.dialect.transactionrollback;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "entity_with_manually_set_fk_child")
class EntityWithManuallySetFkChild {
  @Id String id;

  @Column(name = "`name`")
  String name;

  @OneToOne
  @JoinColumn(name = "parent_id")
  EntityWithManuallySetFk parent;

  public EntityWithManuallySetFkChild(String id, String name, EntityWithManuallySetFk parent) {
    this.id = id;
    this.name = name;
    this.parent = parent;
  }

  public EntityWithManuallySetFkChild() {}
}

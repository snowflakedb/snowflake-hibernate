package net.snowflake.hibernate.dialect.perf;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "child_entity_with_local_id_bi_directional_one_to_many")
class ChildEntityWithLocalIdBiDirectionalOneToMany extends AbstractPerfEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @ManyToOne
  @JoinColumn(name = "parent_id")
  PerfOneToManyBiDirectional parent;

  public static ChildEntityWithLocalIdBiDirectionalOneToMany sample(
      PerfOneToManyBiDirectional parent) {
    ChildEntityWithLocalIdBiDirectionalOneToMany sample =
        new ChildEntityWithLocalIdBiDirectionalOneToMany();
    updateAllFields(sample);
    sample.parent = parent;
    return sample;
  }
}

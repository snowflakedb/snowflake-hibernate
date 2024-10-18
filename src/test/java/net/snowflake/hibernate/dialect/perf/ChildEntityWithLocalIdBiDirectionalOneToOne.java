package net.snowflake.hibernate.dialect.perf;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "child_entity_with_local_id_bi_directional_one_to_one")
class ChildEntityWithLocalIdBiDirectionalOneToOne extends AbstractPerfEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @OneToOne
  @JoinColumn(name = "parent_id")
  PerfOneToOneBiDirectional parent;

  public static ChildEntityWithLocalIdBiDirectionalOneToOne sample(
      PerfOneToOneBiDirectional parent) {
    ChildEntityWithLocalIdBiDirectionalOneToOne sample =
        new ChildEntityWithLocalIdBiDirectionalOneToOne();
    updateAllFields(sample);
    sample.parent = parent;
    return sample;
  }
}

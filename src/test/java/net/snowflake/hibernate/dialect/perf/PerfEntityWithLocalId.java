package net.snowflake.hibernate.dialect.perf;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "perf_entity_with_local_id")
class PerfEntityWithLocalId extends AbstractPerfEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  public static PerfEntityWithLocalId sample() {
    PerfEntityWithLocalId sample = new PerfEntityWithLocalId();
    updateAllFields(sample);
    return sample;
  }
}

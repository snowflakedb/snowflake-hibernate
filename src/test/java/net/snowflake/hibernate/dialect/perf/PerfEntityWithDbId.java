package net.snowflake.hibernate.dialect.perf;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "perf_entity_with_db_id")
class PerfEntityWithDbId extends AbstractPerfEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  long id;

  public static PerfEntityWithDbId sample() {
    PerfEntityWithDbId sample = new PerfEntityWithDbId();
    updateAllFields(sample);
    return sample;
  }
}

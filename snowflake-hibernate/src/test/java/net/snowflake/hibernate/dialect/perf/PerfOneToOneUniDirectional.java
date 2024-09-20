package net.snowflake.hibernate.dialect.perf;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "perf_one_to_one_uni_directional")
class PerfOneToOneUniDirectional {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  String content;

  @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
  @JoinColumn(name = "perf_one_to_one_id")
  PerfEntityWithLocalId child;

  public static PerfOneToOneUniDirectional sample() {
    PerfOneToOneUniDirectional sample = new PerfOneToOneUniDirectional();
    sample.content = UUID.randomUUID().toString();
    sample.child = PerfEntityWithLocalId.sample();
    return sample;
  }
}

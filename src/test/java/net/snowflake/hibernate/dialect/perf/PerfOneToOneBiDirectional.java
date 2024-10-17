package net.snowflake.hibernate.dialect.perf;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "perf_one_to_one_bi_directional")
class PerfOneToOneBiDirectional {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  String content;

  @OneToOne(cascade = CascadeType.PERSIST, mappedBy = "parent", fetch = FetchType.EAGER)
  ChildEntityWithLocalIdBiDirectionalOneToOne child;

  public static PerfOneToOneBiDirectional sample() {
    PerfOneToOneBiDirectional sample = new PerfOneToOneBiDirectional();
    sample.content = UUID.randomUUID().toString();
    sample.child = ChildEntityWithLocalIdBiDirectionalOneToOne.sample(sample);
    return sample;
  }
}

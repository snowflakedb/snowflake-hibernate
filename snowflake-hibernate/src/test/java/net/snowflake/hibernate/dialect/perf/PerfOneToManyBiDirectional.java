package net.snowflake.hibernate.dialect.perf;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "perf_one_to_many_bi_directional")
class PerfOneToManyBiDirectional {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  String content;

  @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "parent", fetch = FetchType.EAGER)
  Set<ChildEntityWithLocalIdBiDirectionalOneToMany> children = new HashSet<>();

  public static PerfOneToManyBiDirectional sample() {
    PerfOneToManyBiDirectional sample = new PerfOneToManyBiDirectional();
    sample.content = UUID.randomUUID().toString();
    sample.children.addAll(
        Stream.generate(() -> ChildEntityWithLocalIdBiDirectionalOneToMany.sample(sample))
            .limit(5)
            .collect(Collectors.toList()));
    return sample;
  }
}

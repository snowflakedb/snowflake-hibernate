package net.snowflake.hibernate.dialect.perf;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "perf_one_to_many_uni_directional")
class PerfOneToManyUniDirectional {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  String content;

  @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
  @JoinColumn(name = "perf_one_to_many_id")
  Set<PerfEntityWithLocalId> children = new HashSet<>();

  public static PerfOneToManyUniDirectional sample() {
    PerfOneToManyUniDirectional sample = new PerfOneToManyUniDirectional();
    sample.content = UUID.randomUUID().toString();
    sample.children.addAll(
        Stream.generate(PerfEntityWithLocalId::sample).limit(5).collect(Collectors.toList()));
    return sample;
  }
}

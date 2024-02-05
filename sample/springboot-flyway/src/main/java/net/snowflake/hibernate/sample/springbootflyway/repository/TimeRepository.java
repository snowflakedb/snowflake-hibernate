package net.snowflake.hibernate.sample.springbootflyway.repository;

import net.snowflake.hibernate.sample.springflyway.model.Time;
import org.springframework.data.repository.CrudRepository;

public interface TimeRepository extends CrudRepository<Time, Long> {
}

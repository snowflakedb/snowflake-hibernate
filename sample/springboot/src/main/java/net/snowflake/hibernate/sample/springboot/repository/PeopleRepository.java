package net.snowflake.hibernate.sample.springboot.repository;

import net.snowflake.hibernate.sample.springboot.entity.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PeopleRepository<T extends Person> extends CrudRepository<T, Long> {
    List<T> findByLastName(String name);
}

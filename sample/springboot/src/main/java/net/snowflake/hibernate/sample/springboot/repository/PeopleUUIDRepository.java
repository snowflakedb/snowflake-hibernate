package net.snowflake.hibernate.sample.springboot.repository;

import net.snowflake.hibernate.sample.springboot.entity.PersonUUID;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PeopleUUIDRepository extends CrudRepository<PersonUUID, String> {
    List<PersonUUID> findByLastName(String lastName);
}

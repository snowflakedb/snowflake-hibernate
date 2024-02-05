package net.snowflake.hibernate.sample.springboot.repository;

import net.snowflake.hibernate.sample.springboot.entity.Company;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CompanyRepository extends CrudRepository<Company, Long> {
    List<Company> findByName(String name);
}

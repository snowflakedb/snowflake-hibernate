package net.snowflake.hibernate.sample.springbootflyway.repository;

import net.snowflake.hibernate.sample.springflyway.model.Company;
import org.springframework.data.repository.CrudRepository;

public interface CompanyRepository extends CrudRepository<Company, Long> {
}

package net.snowflake.hibernate.sample.springboot.repository;

import net.snowflake.hibernate.sample.springboot.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> { }

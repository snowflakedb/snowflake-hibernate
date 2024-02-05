package net.snowflake.hibernate.sample.springboot.service;

import jakarta.transaction.Transactional;
import net.snowflake.hibernate.sample.springboot.entity.Company;
import net.snowflake.hibernate.sample.springboot.entity.Product;
import net.snowflake.hibernate.sample.springboot.repository.ProductRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CompanyService companyService;

    public Product save(String name, String description, Company company) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        company.getProducts().add(product);
        company = companyService.save(company);
        product.getCompanies().add(company);
        return productRepository.save(product);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> findAll() {
        return (List<Product>) productRepository.findAll();
    }
}

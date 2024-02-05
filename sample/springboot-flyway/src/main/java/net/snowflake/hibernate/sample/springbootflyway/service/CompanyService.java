package net.snowflake.hibernate.sample.springbootflyway.service;

import jakarta.transaction.Transactional;
import net.snowflake.hibernate.sample.springflyway.model.Company;
import net.snowflake.hibernate.sample.springflyway.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    public Optional<Company> findCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    public Company save(String name) {
        Company company = new Company();
        company.setName(name);
        return companyRepository.save(company);
    }
}

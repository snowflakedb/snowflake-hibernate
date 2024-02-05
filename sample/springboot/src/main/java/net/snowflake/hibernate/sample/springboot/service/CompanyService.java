package net.snowflake.hibernate.sample.springboot.service;

import net.snowflake.hibernate.sample.springboot.entity.Company;
import jakarta.transaction.Transactional;
import net.snowflake.hibernate.sample.springboot.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    public Company save(String name) {
        Company company = new Company();
        company.setName(name);
        return companyRepository.save(company);
    }

    public Company save(Company company) {
        return companyRepository.save(company);
    }

    public void deleteById(Long id) {
        companyRepository.deleteById(id);
    }

    public List<Company> findAll() {
        return (List<Company>) companyRepository.findAll();
    }

    public List<Company> findCompanyByName(String name) {
        return companyRepository.findByName(name);
    }

    public Optional<Company> findCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    public void deleteAll() {
        companyRepository.deleteAll();
    }
}

package net.snowflake.hibernate.sample.springbootflyway;

import net.snowflake.hibernate.sample.springflyway.model.Company;
import net.snowflake.hibernate.sample.springflyway.service.CompanyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringbootFlywayApplicationTests {
    @Autowired
    private CompanyService companyService;

    @Test
    void contextLoads() {
    }

    @Test
    public void testReadAndSave() {
        Company company = companyService.findCompanyById(0L).orElseThrow();
        Assertions.assertEquals("Test Company", company.getName());

        Company company2 = companyService.save("Snowflake");
        Assertions.assertEquals("Snowflake", companyService.findCompanyById(company2.getId()).orElseThrow().getName());
    }
}

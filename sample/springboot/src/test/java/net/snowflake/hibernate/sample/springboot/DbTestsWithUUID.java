package net.snowflake.hibernate.sample.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import net.snowflake.hibernate.sample.springboot.service.PeopleUUIDService;
import net.snowflake.hibernate.sample.springboot.entity.PersonUUID;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DbTestsWithUUID {
    @Autowired
    PeopleUUIDService peopleUUIDService;

    @Test
    void shouldSaveAndGetPersonById() {
        PersonUUID person1 = peopleUUIDService.save("John", UUID.randomUUID().toString());

        Optional<PersonUUID> maybePerson = peopleUUIDService.findById(person1.getId());

        assertTrue(maybePerson.isPresent());
        assertEquals(person1.getId(), maybePerson.get().getId());
        assertEquals(person1.getFirstName(), maybePerson.get().getFirstName());
        assertEquals(person1.getLastName(), maybePerson.get().getLastName());
    }

    @Test
    void shouldGetPeopleByLastName() {
        PersonUUID person1 = peopleUUIDService.save("John", UUID.randomUUID().toString());
        String lastName = UUID.randomUUID().toString();
        PersonUUID person2 = peopleUUIDService.save("Bla", lastName);
        PersonUUID person3 = peopleUUIDService.save("Ble", lastName);

        List<PersonUUID> people1 = peopleUUIDService.findByLastName(person1.getLastName());
        List<PersonUUID> people2 = peopleUUIDService.findByLastName(lastName);

        assertEquals(Set.of(person1.getId()), people1.stream().map(p -> p.getId()).collect(Collectors.toSet()));
        assertEquals(Set.of(person2.getId(), person3.getId()), people2.stream().map(p -> p.getId()).collect(Collectors.toSet()));
    }
}

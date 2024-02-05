package net.snowflake.hibernate.sample.springboot.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import net.snowflake.hibernate.sample.springboot.repository.PeopleUUIDRepository;
import net.snowflake.hibernate.sample.springboot.entity.PersonUUID;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PeopleUUIDService {

    private final PeopleUUIDRepository peopleUUIDRepository;

    public PersonUUID save(String firstName, String lastName) {
        PersonUUID person = new PersonUUID();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        peopleUUIDRepository.save(person);
        return person;
    }

    public Optional<PersonUUID> findById(String id) {
        return peopleUUIDRepository.findById(id);
    }

    public List<PersonUUID> findByLastName(String lastName) {
        return peopleUUIDRepository.findByLastName(lastName);
    }
}

package net.snowflake.hibernate.sample.springbootflyway.service;

import jakarta.transaction.Transactional;
import net.snowflake.hibernate.sample.springflyway.model.Time;
import net.snowflake.hibernate.sample.springflyway.repository.TimeRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TimeService {
    private final TimeRepository timeRepository;

    public List<Time> findAll() {
        return (List<Time>) timeRepository.findAll();
    }

    public Optional<Time> findById(Long id) {
        return timeRepository.findById(id);
    }

    public Time save(Time time) {
        return timeRepository.save(time);
    }
}

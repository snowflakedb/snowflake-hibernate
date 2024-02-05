package net.snowflake.hibernate.sample.springbootflyway.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "timestamps")
@Getter
@Setter
public class Time {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "d")
    private LocalDate date;

    @Column(name = "t_tz")
    private OffsetDateTime timestamp_tz;

    @Column(name = "t_ntz")
    private LocalDateTime timestamp_ntz;

    @Column(name = "t_ltz")
    private OffsetDateTime timestamp_ltz;
}

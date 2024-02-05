package net.snowflake.hibernate.sample.springboot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer extends Person {
    @ManyToOne
    private Employee contactPerson;
}

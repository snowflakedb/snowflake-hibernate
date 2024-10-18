package net.snowflake.hibernate.dialect.transactionrollback;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(
    name = "person",
    indexes = {@Index(columnList = "firstName,lastName")})
public class Person {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  String firstName;
  String lastName;

  @OneToOne(cascade = CascadeType.PERSIST)
  private Address address;

  public Person(String firstName, String lastName, Address address) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.address = address;
  }

  public Person() {}
}

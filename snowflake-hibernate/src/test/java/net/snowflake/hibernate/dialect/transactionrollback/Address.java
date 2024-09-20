package net.snowflake.hibernate.dialect.transactionrollback;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "person_address")
public class Address {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  Long id;

  String addressLine;

  public Address(String addressLine) {
    this.addressLine = addressLine;
  }

  public Address() {}
}

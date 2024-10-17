package net.snowflake.hibernate.dialect.keywords;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rlike") // rlike is also a Snowflake reserved keyword
class KeywordEntity {
  // Reserved keywords in Snowflake https://docs.snowflake.com/en/sql-reference/reserved-keywords
  // in this entity there are columns that are named as ANSI reserved keywords (some of them) and
  // all Snowflake reserved keywords

  @Id @GeneratedValue private Long id;
  private String firstName;
  private String account;

  @Column(name = "case")
  private String casee;

  private String cast;
  private String connection;
  private String constraint;
  private String cross;
  private String database;
  private String delete;

  @Column(name = "false")
  private String falsee;

  private String full;
  private String gscluster;

  private String ilike;
  private String increment;
  private String inner;
  private String issue;
  private String join;
  private String lateral;
  private String left;
  private String minus;
  private String natural;
  private String of;
  private String order;
  private String organization;
  private String qualify;
  private String regexp;
  private String right;
  private String rlike;
  private String schema;
  private String some;

  @Column(name = "true")
  private String truee;

  @Column(name = "try_cast")
  private String tryCast;

  private String using;
  private String when;

  public Long getId() {
    return id;
  }
}

package net.snowflake.hibernate.dialect.datatypes;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "audit_data")
class AuditData {
  // every field is in the package scope to not generate getters for tests

  @Id @GeneratedValue long id;

  @CreationTimestamp(source = SourceType.DB)
  Instant createdAt;

  @UpdateTimestamp(source = SourceType.DB)
  Instant updatedAt;

  String content;
}

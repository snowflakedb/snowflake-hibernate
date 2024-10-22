package net.snowflake.hibernate.dialect.perf;

import jakarta.persistence.MappedSuperclass;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@MappedSuperclass
abstract class AbstractPerfEntity {
  Integer anInt;
  byte[] bytes;
  String string;
  LocalDateTime dateTime;
  Double aDouble;
  Float aFloat;
  Boolean aBoolean;

  static void updateAllFields(AbstractPerfEntity sample) {
    Random random = new Random();
    sample.aBoolean = random.nextBoolean();
    sample.aDouble = random.nextDouble();
    sample.aFloat = random.nextFloat();
    sample.anInt = random.nextInt();
    sample.bytes = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);
    sample.dateTime = LocalDateTime.now();
    sample.string = UUID.randomUUID().toString();
  }
}

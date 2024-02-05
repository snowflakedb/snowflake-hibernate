package net.snowflake.hibernate.sample.springbootflyway;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import net.snowflake.hibernate.sample.springflyway.model.Time;
import net.snowflake.hibernate.sample.springflyway.service.TimeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.TimeZone;

@SpringBootTest
public class TimeTests {
    @Autowired
    private TimeService timeService;
    @Autowired
    private EntityManager entityManager;

    private final LocalDate date = LocalDate.of(2000, 1, 1);
    private final OffsetDateTime timestampTz = OffsetDateTime.parse("2022-03-17T10:10:08+11:00");
    private final LocalDateTime timestampNtz = LocalDateTime.parse("2012-07-01T12:00:00.000000");
    private final OffsetDateTime timestampLtz = OffsetDateTime.parse("1981-02-28T23:59:59.9876540+02:00");

    @Test
    public void testTime() {
        Time time = new Time();
        time.setDate(date);
        time.setTimestamp_tz(timestampTz);
        time.setTimestamp_ntz(timestampNtz);
        time.setTimestamp_ltz(timestampLtz);
        time = timeService.save(time);

        Time fetchedTime = timeService.findById(time.getId()).orElseThrow();
        System.out.println("Fetched time id: " + fetchedTime.getId());
        System.out.println("System timezone: " + TimeZone.getDefault().getID());
        System.out.println("Session timezone: " + getSessionTimezone());
        Assertions.assertEquals(time.getDate(), fetchedTime.getDate());

        // These assertions fail as there is an issue how the jdbc driver operates on the timestamps
        Assertions.assertEquals(timestampTz, fetchedTime.getTimestamp_tz());
        Assertions.assertEquals(timestampNtz, fetchedTime.getTimestamp_ntz());
        Assertions.assertEquals(timestampLtz, fetchedTime.getTimestamp_ltz());
    }

    private String getSessionTimezone() {
        Query query = entityManager.createNativeQuery("show parameters like 'TIMEZONE';");
        return ((Object[]) query.getSingleResult())[1].toString();
    }
}

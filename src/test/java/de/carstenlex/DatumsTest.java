package de.carstenlex;

import com.google.api.client.util.DateTime;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DatumsTest {

    @Test
    void testDatumConversion() {
        Spiel spiel = new Spiel();
        spiel.teamHeim = "H1";
        spiel.halle="Seegarten";
        spiel.teamAuswaerts="H2";
        spiel.datumUhrzeit = LocalDateTime.of(2020,11,03,20,30,00);

        ZonedDateTime zonedDateTime = ZonedDateTime.of(spiel.datumUhrzeit, ZoneId.systemDefault());
        String formatted = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        System.out.println(formatted);
        DateTime startDateTime = new DateTime(formatted);
        System.out.println(startDateTime);
    }
}

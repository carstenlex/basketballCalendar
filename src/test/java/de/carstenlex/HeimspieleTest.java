package de.carstenlex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class HeimspieleTest extends BaseTest{

    @Test
    public void testToCSV() throws IOException {
        String content = loadResourceAsString("/spielplan_herren1.txt");

        Spielplan spielplan = new Spielplan();
        System.out.println("parse spiele");
        List<Spiel> spiele = spielplan.parseSpiele(content, Mannschaft.HERREN1);

        BasketballHeimspieleToFile heimspiele = new BasketballHeimspieleToFile();
        heimspiele.addHeimspiele(spiele);

        Assertions.assertEquals(8,heimspiele.size());
    }
}

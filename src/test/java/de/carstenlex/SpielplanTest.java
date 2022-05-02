package de.carstenlex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class SpielplanTest extends BaseTest{

    @Test
    public void testParseSpielplan() throws IOException {
        String content = loadResourceAsString("/spielplan_herren1.txt");

        Spielplan spielplan = new Spielplan();
        System.out.println("parse spiele");
        List<Spiel> spiele = spielplan.parseSpiele(content, Mannschaft.HERREN1);

        spiele.forEach(System.out::println);
        Assertions.assertEquals(16,spiele.size());

    }



}

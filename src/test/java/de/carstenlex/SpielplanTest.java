package de.carstenlex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class SpielplanTest {

    @Test
    public void testParseSpielplan() throws IOException {
        String content = loadResourceAsString("/spielplan_herren1.txt");

        Spielplan spielplan = new Spielplan();
        System.out.println("parse spiele");
        List<Spiel> spiele = spielplan.parseSpiele(content, Mannschaft.HERREN1);

        spiele.forEach(System.out::println);
        Assertions.assertEquals(16,spiele.size());

    }

    private String loadResourceAsString(String resourceName) throws IOException {
        InputStream resourceAsStream = this.getClass().getResourceAsStream(resourceName);
        byte[] bytes = resourceAsStream.readAllBytes();
        String content = new String(bytes);
        return content;
    }

}

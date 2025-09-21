package de.carstenlex;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
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
        Elements document = Jsoup.parse(content).select("tr"); //FIXME
        List<Spiel> spiele = spielplan.parseSpiele(document, Mannschaft.HERREN1);

        BasketballSpieleToFile heimspiele = new BasketballSpieleToFile();
        heimspiele.addHeimspiele(spiele);

        Assertions.assertEquals(8,heimspiele.size());
    }
}

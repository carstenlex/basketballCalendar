package de.carstenlex;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
        List<Spiel> spiele = spielplan.parseSpiele(Jsoup.parse(content).select("tr"), Mannschaft.HERREN1);

        spiele.forEach(System.out::println);
        Assertions.assertEquals(16,spiele.size());

    }

    @Test
    public void testLoadSpielplanForTeam() throws IOException {

        Spielplan spielplan = new Spielplan();
        List<Spiel> spiele = spielplan.loadFromBasketplan(Mannschaft.DU16);

        assert spiele != null;
    }

    @Test
    public void jsoupTest() throws IOException {


        Document document = Jsoup.connect("https://www.basketplan.ch/showLeagueSchedule.do?leagueHoldingId=10668").get();

        Elements zeilenMitSpielen = document.select("tr[onmouseover]");
        //System.out.println(zeilenMitSpielen);

        for (Element element : zeilenMitSpielen) {
            Elements zellen = element.select("> td");
            String datum = zellen.get(0).text();
            String spielNr = zellen.get(3).text();
            String ort = zellen.get(4).text();
            String heim = zellen.get(5).text();
            String gast = zellen.get(6).text();
            String schiri1 = zellen.get(7).text();
            String schiri2 = zellen.get(8).text();
            String ergebnis = zellen.get(9).text();

            System.out.println(spielNr);
        }


    }

}

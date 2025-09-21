package de.carstenlex;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.carstenlex.Configuration.AUTO_SYNC_MARKER;
import static de.carstenlex.Spielplan.*;

public class Spielplan {

    private static Logger log = Logger.getLogger(Spielplan.class.getName());

    public static final int SPIELNR = 3;
    public static final int ORT = 4;
    public static final int HEIM = 5;
    public static final int GAST = 6;

    public List<Spiel> loadFromBasketplan(Mannschaft mannschaft) throws IOException {

        String url = Configuration.BASKETPLAN_BASE_SPIELPLAN_PRO_TEAM.replace("#liga#", mannschaft.getLiga() + "").replace("#team#", mannschaft.getTeam() + "");
        log.info("Call Basketplan for Team "+mannschaft+": "+url);
        Document basketplanSpieleForLiga = Jsoup.connect(url).get(); // HTML-Seite von Basketplan
        Elements zeilenMitSpielen = basketplanSpieleForLiga.select("tr[onmouseover]");

        if (basketplanSpieleForLiga != null) {
            //log.info(responseAsString);
            return parseSpiele(zeilenMitSpielen, mannschaft);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     *
     * @param zeilenMitSpielen das ist die Seite im basketplan, die für eine Liga die Spiele aller Mannschaften anzeigt
     * @param mannschaft
     * @return
     */
    List<Spiel> parseSpiele(Elements zeilenMitSpielen, Mannschaft mannschaft) {
        List<Spiel> liste = new ArrayList<>();

        for (Element element : zeilenMitSpielen) {
            Elements zellen = element.select("> td"); // alle td in der aktuellen Zeile
            String heim = zellen.get(HEIM).text();
            String gast = zellen.get(GAST).text();

            if (heim.contains(mannschaft.getBasketplanName()) || gast.contains(mannschaft.getBasketplanName())) {
                try {
                    Spiel spiel = new Spiel(zellen, mannschaft);
                    liste.add(spiel);
                } catch (ParseException | IllegalArgumentException e) {
                    log.warning(e.getMessage());
                }
            }
        }
        System.out.println("Anzahl Spiele: "+ liste.size());

        return liste;
    }
}

class Spiel {

    public static final int SCHIRI_1 = 7;
    public static final int SCHIRI_2 = 8;
    public static final int ERGEBNIS = 9;
    public static final int DATUM_UHRZEIT = 0;
    public static final String OBERTHURGAU_PIRATES = "Oberthurgau Pirates";
    LocalDateTime datumUhrzeit;
    String teamHeim;
    String teamAuswaerts;
    String halle;
    private Mannschaft mannschaft;
    boolean heimspiel;
    String spielNr;

    public Spiel(){}

    public Spiel(Elements zellen, Mannschaft mannschaft) throws ParseException {

        spielNr = zellen.get(Spielplan.SPIELNR).text();
        String ort = zellen.get(ORT).text();
        String heim = zellen.get(HEIM).text();
        String gast = zellen.get(GAST).text();
        String schiri1 = zellen.get(SCHIRI_1).text();
        String schiri2 = zellen.get(SCHIRI_2).text();
        String ergebnis = zellen.get(ERGEBNIS).text();

        datumUhrzeit = extractDate(zellen.get(DATUM_UHRZEIT).text());
        teamHeim = heim;
        if (teamHeim.contains(OBERTHURGAU_PIRATES)){
            teamHeim = mannschaft.getShortName();
            heimspiel = true;
        }
        teamAuswaerts = gast;
        if (teamAuswaerts.contains(OBERTHURGAU_PIRATES)){
            teamAuswaerts = mannschaft.getShortName();
            heimspiel = false;
        }

        halle = ort;

        this.mannschaft = mannschaft;
    }



    private LocalDateTime extractDate(String datumZeit) throws ParseException {
            // System.out.println(dateTime);
        DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("dd.MM.yy HH:mm")
                .toFormatter(Locale.GERMAN);
            return LocalDateTime.parse(datumZeit.substring(3), dtf); // Wochentag kann irgendwie nicht parsed werden, dann nehmen wir ab INdex 3 das Datum

    }

    public String getStartzeitForCalendar() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(datumUhrzeit, ZoneId.systemDefault());
        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public String getEndeZeitForCalendar() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(datumUhrzeit.plusHours(2), ZoneId.systemDefault());
        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @Override
    public String toString() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        return mannschaft.getShortName() +"-Spiel am " + dateFormat.format(datumUhrzeit) + " um "+timeFormat.format(datumUhrzeit)+" Uhr\n"+
                teamHeim + " gegen " + teamAuswaerts +"\n" +
                "Ort: " + halle + "\n" +
                AUTO_SYNC_MARKER; // Der hier ist wichtig: nur Termine mit diesem Text in der Description werden auch wieder beim Clear gelöscht
    }

    public static String toCSV(Spiel spiel) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter wochentag = DateTimeFormatter.ofPattern("EEEE");
        return spiel.mannschaft+";"+wochentag.format(spiel.datumUhrzeit)+";"+dateFormat.format(spiel.datumUhrzeit)+";"+timeFormat.format(spiel.datumUhrzeit)+";"+spiel.teamHeim+";"+spiel.teamAuswaerts+";"+spiel.halle+";;;";
    }


    public boolean isHeimspiel() {
        return heimspiel;
    }

    public Mannschaft getMannschaft() {
        return mannschaft;
    }

    public String getHalle() {
        return halle;
    }

    public String getTeamAuswaerts() {
        return teamAuswaerts;
    }

    public String getTeamHeim() {
        return teamHeim;
    }

    public LocalDateTime getDatumUhrzeit() {
        return datumUhrzeit;
    }
}

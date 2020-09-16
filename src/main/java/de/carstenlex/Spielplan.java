package de.carstenlex;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.java.Log;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log
public class Spielplan {

    public List<Spiel> loadFromBasketplan(Mannschaft mannschaft) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();

        String url = Configuration.BASKETPLAN_BASE_SPIELPLAN_PRO_TEAM.replace("#liga#", mannschaft.getLiga() + "").replace("#team", mannschaft.getTeam() + "");

        HttpGet request = new HttpGet(url);
        CloseableHttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String responseAsString = EntityUtils.toString(entity);
            //log.info(responseAsString);
            return parseSpiele(responseAsString, mannschaft);
        } else {
            return new ArrayList<>();
        }
    }

    List<Spiel> parseSpiele(String content, Mannschaft mannschaft) {
        List<Spiel> liste = new ArrayList<>();
        // <tr class=\"even upcoming\"><td class=\"scheduleDate\">26.09.2020, 13:30<\/td><td class=\"scheduleHome\">CVJM Frauenfeld<\/td><td class=\"scheduleGuest thisTeam\">Oberthurgau Pirates<\/td><td class=\"scheduleLocation\">Kanti Frauenfeld 1<\/td><td class=\"scheduleResult\">&nbsp;<\/td><\/tr>

        Pattern pattern = Pattern.compile("<tr.+?/tr>");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String oneGame = matcher.group();
            try {
                Spiel spiel = new Spiel(oneGame, mannschaft);
                liste.add(spiel);
            } catch (ParseException | IllegalArgumentException e) {
                log.warning(e.getMessage());
            }
        }


        return liste;
    }
}

@Data
@ToString
@NoArgsConstructor
class Spiel {
    LocalDateTime datumUhrzeit;
    String teamHeim;
    String teamAuswaerts;
    String halle;
    private Mannschaft mannschaft;

    public Spiel(String rawString, Mannschaft mannschaft) throws ParseException {
        datumUhrzeit = extractDate(rawString);
        teamHeim = extractTeamHeim(rawString);
        if (teamHeim.equalsIgnoreCase("Oberthurgau Pirates")){
            teamHeim = mannschaft.getShortName();
        }
        teamAuswaerts = extractTeamAuswaerts(rawString);
        if (teamAuswaerts.equalsIgnoreCase("Oberthurgau Pirates")){
            teamAuswaerts = mannschaft.getShortName();
        }
        halle = extractHalle(rawString);

        this.mannschaft = mannschaft;
    }

    private String extractTeamHeim(String rawString) {
        return extractString(rawString, "scheduleHome");
    }

    private String extractTeamAuswaerts(String rawString) {
        return extractString(rawString, "scheduleGuest");
    }

    private String extractHalle(String rawString) {
        return extractString(rawString, "scheduleLocation");
    }

    private String extractString(String rawString, String field) {
        Pattern pattern = Pattern.compile("<td(.*?)" + field + "(.*?)>(.+?)<(.*?)td>");
        Matcher matcher = pattern.matcher(rawString);
        if (matcher.find()) {
            String heimTeam = matcher.group(3);
            heimTeam = heimTeam.replaceAll("&uuml;", "ü").replaceAll("&ouml;", "ö").replaceAll("&auml;", "ä");
            //System.out.println(heimTeam);
            return heimTeam;
        }

        return "";
    }

    private LocalDateTime extractDate(String rawString) throws ParseException {
        Pattern pattern = Pattern.compile("<td(.*?)scheduleDate(.*?)>(.+?)<(.*?)td>");
        Matcher matcher = pattern.matcher(rawString);
        if (matcher.find()) {
            String dateTime = matcher.group(3);
            // System.out.println(dateTime);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
            return LocalDateTime.parse(dateTime, dtf);
        } else {
            throw new IllegalArgumentException("Kein gültiges Spiel: " + rawString);
        }

    }

    public String getStartzeitForCalendar() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(datumUhrzeit, ZoneId.systemDefault());
        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public String getEndeZeitForCalendar() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(datumUhrzeit.plusHours(2), ZoneId.systemDefault());
        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}

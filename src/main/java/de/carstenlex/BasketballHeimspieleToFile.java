package de.carstenlex;

import com.google.api.services.calendar.model.CalendarListEntry;
import lombok.extern.java.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Log
public class BasketballHeimspieleToFile {

    public static final String HEADER ="Tag;Datum;Uhrzeit;Mannschaft;Gegner;Halle;Uhr;Matchblatt;Bemerkung";

    List<Spiel> alleHeimspiele = new ArrayList<>();


    public static void main(String... args) throws IOException {
        log.info("Starte Heimspiele als CSV aus Basketplan lesen");



        log.info("Spiele laden ...");
        Spielplan spielplan = new Spielplan();
        BasketballHeimspieleToFile heimspiele = new BasketballHeimspieleToFile();
        for (Mannschaft mannschaft : Mannschaft.values()) {
            System.out.println("===========================");
            System.out.println("Mannschaft: "+mannschaft);
            System.out.println("===========================");
            List<Spiel> spiele = spielplan.loadFromBasketplan(mannschaft);
            //spiele.forEach(spiel -> log.info(spiel.toString()));
            heimspiele.addHeimspiele(spiele);
        }
        log.info("Terminübertragung fertig!");
        heimspiele.toFile("heimspiele.csv");
    }






    public BasketballHeimspieleToFile addHeimspiele(List<Spiel> list) {
        if (list != null) {
            alleHeimspiele.addAll(list.stream().filter(Spiel::isHeimspiel).collect(Collectors.toList()));
        }
        return this;
    }

    public String toCSV() {
        Comparator<Spiel> comparingDateTime = Comparator.comparing(Spiel::getDatumUhrzeit);

        String csvFile = alleHeimspiele.stream()
                .sorted(comparingDateTime)
                .map(Spiel::toCSV)
                .collect(Collectors.joining("\n"));
        return HEADER+"\n"+csvFile;
    }

    public int size() {
        return alleHeimspiele.size();
    }


    public void toFile(String fileName) throws IOException {
        System.out.println("=============================");
        System.out.println("Generate CSV mit Heimspielen ");
        System.out.println("=============================");
        try( FileWriter writer = new FileWriter(fileName)) {
            writer.write(toCSV());
        }
    }
}

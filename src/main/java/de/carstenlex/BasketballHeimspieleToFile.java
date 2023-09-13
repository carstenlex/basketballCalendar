package de.carstenlex;

import com.google.api.services.calendar.model.CalendarListEntry;
import lombok.extern.java.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@Log
public class BasketballHeimspieleToFile {

    public static final String HEADER ="Tag;Datum;Uhrzeit;Mannschaft;Gegner;Halle;Uhr;Matchblatt;Bemerkung";

    List<Spiel> alleHeimspiele = new ArrayList<>();

    public boolean ignoreVergangeneSpiele = true;


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
            if (args[0].equalsIgnoreCase("heim")) {
                heimspiele.addHeimspiele(spiele);
            }else if (args[0].equalsIgnoreCase("auswaerts")) {
                heimspiele.addAuswaertsspiele(spiele);
            }else if (args[0].equalsIgnoreCase("alle")) {
                heimspiele.addAlleSpiele(spiele);
            }
        }
        log.info("Terminübertragung fertig!");
        if (args[0].equalsIgnoreCase("heim")) {
            heimspiele.toFile("heimspiele.csv");
        }else if (args[0].equalsIgnoreCase("auswaerts")) {
            heimspiele.toFile("auswaertsspiele.csv");
        }else if (args[0].equalsIgnoreCase("alle")) {
            heimspiele.toFile("allespiele.csv");
        }

    }

    private BasketballHeimspieleToFile addAuswaertsspiele(List<Spiel> list) {
        if (list != null) {
            alleHeimspiele.addAll(list.stream().filter(not(Spiel::isHeimspiel)).collect(Collectors.toList()));
        }
        return this;
    }

    private BasketballHeimspieleToFile addAlleSpiele(List<Spiel> spiele) {
        alleHeimspiele.addAll(spiele);
        return this;
    }


    public BasketballHeimspieleToFile addHeimspiele(List<Spiel> list) {
        if (list != null) {
            alleHeimspiele.addAll(list.stream()
                    .filter(Spiel::isHeimspiel)
                            .filter(spiel -> {
                                if(ignoreVergangeneSpiele){
                                    return spiel.datumUhrzeit.isAfter(LocalDateTime.now()); // nur die in der Zukunft
                                }else{
                                    return true; // alle Spiele
                                }

                            })
                    .collect(Collectors.toList()));
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

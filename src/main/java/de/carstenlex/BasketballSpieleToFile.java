package de.carstenlex;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class BasketballSpieleToFile {

    private static Logger log = Logger.getLogger(BasketballSpieleToFile.class.getName());


    public static final String HEADER ="Team;Tag;Datum;Uhrzeit;Mannschaft;Gegner;Halle;Uhr;Matchblatt;Bemerkung";

    List<Spiel> alleHeimspiele = new ArrayList<>();

    public boolean ignoreVergangeneSpiele = true;


    public static void main(String... args) throws IOException {
        log.info("Starte Heimspiele als CSV aus Basketplan lesen");

        String spielart = "alle";
        if (args != null && args.length > 0) {
            spielart = args[0];
        }

        log.info("Spiele laden ...");
        Spielplan spielplan = new Spielplan();
        BasketballSpieleToFile heimspiele = new BasketballSpieleToFile();
        for (Mannschaft mannschaft : Mannschaft.values()) {
            System.out.println("===========================");
            System.out.println("Mannschaft: "+mannschaft);
            System.out.println("===========================");
            List<Spiel> spiele = spielplan.loadFromBasketplan(mannschaft);
            //spiele.forEach(spiel -> log.info(spiel.toString()));
            if (spielart.equalsIgnoreCase("heim")) {
                heimspiele.addHeimspiele(spiele);
            }else if (spielart.equalsIgnoreCase("auswaerts")) {
                heimspiele.addAuswaertsspiele(spiele);
            }else if (spielart.equalsIgnoreCase("alle")) {
                heimspiele.addAlleSpiele(spiele);
            }
        }
        log.info("Terminübertragung fertig!");


        if (spielart.equalsIgnoreCase("heim")) {
            heimspiele.toFile("heimspiele.csv");
        }else if (spielart.equalsIgnoreCase("auswaerts")) {
            heimspiele.toFile("auswaertsspiele.csv");
        }else if (spielart.equalsIgnoreCase("alle")) {
            heimspiele.toFile("allespiele.csv");
        }

    }

    private BasketballSpieleToFile addAuswaertsspiele(List<Spiel> list) {
        if (list != null) {
            alleHeimspiele.addAll(list.stream().filter(not(Spiel::isHeimspiel)).collect(Collectors.toList()));
        }
        return this;
    }

    private BasketballSpieleToFile addAlleSpiele(List<Spiel> list) {
        alleHeimspiele.addAll(list.stream().filter(spiel -> {
                    if(ignoreVergangeneSpiele){
                        return spiel.datumUhrzeit.isAfter(LocalDateTime.now()); // nur die in der Zukunft
                    }else{
                        return true; // alle Spiele
                    }

                })
                .collect(Collectors.toList()));
        return this;
    }


    public BasketballSpieleToFile addHeimspiele(List<Spiel> list) {
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

package de.carstenlex.webling;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.carstenlex.Mannschaft;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class WeblingClient {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        int startSaison = Calendar.getInstance().get(Calendar.YEAR);
        if (args.length > 0){
            startSaison = Integer.parseInt(args[1]);
        }

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest.Builder getMemeber = HttpRequest.newBuilder(new URI("https://bbotg.webling.ch/api/1/member?format=full")).GET().header("apikey", "417a9001b785a51096bde529f7705dd0");

        HttpRequest memberRequest = getMemeber.build();

        HttpResponse<String> response = client.send(memberRequest, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN,true);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Member[] members = objectMapper.readValue(body, Member[].class);
        System.out.println(body);


        try(FileWriter fileWriter = new FileWriter("Saison_"+startSaison+".csv")) {
            for (Mannschaft mannschaft : Mannschaft.values()) {
                List<Member> u14m = list(members, mannschaft, startSaison);//Saison 22 -> Jahrgang 09/10
                String mannschaftHeader = "======== " + mannschaft + " ============";
                System.out.println(mannschaftHeader);
                fileWriter.write(mannschaftHeader+"\n");
                String csv = u14m.stream()
                        .map(m -> m.vorname + ";" + m.getName() + ";" + m.jahrgang()+";"+m.getGeschlecht()+";"+m.getStandort())
                        .collect(Collectors.joining("\n"));
                System.out.println(csv);
                fileWriter.write(csv);
                fileWriter.write("\n");
            }
        }
    }

    

    private static List<Member> list(Member[] members, Mannschaft mannschaft, int startSaison) {

        List<Member> collect = Arrays.stream(members)
                .filter(getGeschlechtPredicate(mannschaft))
                .filter(m -> m.status !=null && m.status.equalsIgnoreCase("Aktivmitglied"))
                .filter(getJahrgangPredicate(mannschaft,startSaison))
                .collect(Collectors.toList());

        return collect;
    }

    private static Predicate<Member> getJahrgangPredicate(Mannschaft mannschaft, int startSaison){
        if (mannschaft.isJugend()){
           return  m -> startSaison-m.jahrgang() >= mannschaft.getAlterVon() && startSaison-m.jahrgang() <= mannschaft.getAlterBis();
        }else{
            return  m -> startSaison-m.jahrgang() >= mannschaft.getAlterVon();
        }
    }

    private static Predicate<Member> getGeschlechtPredicate(Mannschaft mannschaft) {
        if (mannschaft.isMaennlich()) {
            return m -> mannschaft.isMaennlich() == m.maennlich();
        }else if (mannschaft.isWeiblich()){
            return m -> mannschaft.isMaennlich() == m.maennlich();
        }else {
            return m -> true;
        }
    }
}

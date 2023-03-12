package de.carstenlex.webling;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
public class Member {
    String type;
     String vorname;
     Integer id;
    private String name;
    private Date geburtstag;
    private String geschlecht;

    String status;

public boolean maennlich(){
    return geschlecht != null && geschlecht.equalsIgnoreCase("m");
}
public boolean weiblich(){
    return geschlecht != null && geschlecht.equalsIgnoreCase("w");
}

public boolean keinGeschlecht(){
    return !weiblich() && ! maennlich();
}

public int jahrgang(){
    if (geburtstag == null){
        return 0;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(geburtstag);
    int i = calendar.get(Calendar.YEAR);
    return i;
}


    @JsonProperty("properties")
    private void unpackNested(Map<String,Object> properties) throws ParseException {
        this.vorname = (String)properties.get("Vorname");
        this.name = (String)properties.get("Name");
        this.id = (Integer) properties.get("Mitglieder ID");
        this.geschlecht = (String)properties.get("Geschlecht");
        this.status = (String)properties.get("Status");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            String geburtstagfeld = (String) properties.get("Geburtstag");
            if (geburtstagfeld !=null) {
                this.geburtstag = sdf.parse(geburtstagfeld);
            }else{
                geburtstag = null;
            }
        } catch (ParseException e) {
            this.geburtstag = null;
        }


    }
}

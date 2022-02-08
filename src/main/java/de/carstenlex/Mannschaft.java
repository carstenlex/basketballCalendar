package de.carstenlex;

public enum Mannschaft {

    // SHortname zur Anzeige im Calendar; liga=id im Basketplan und team = id im Basketplan
    HERREN1("H1",7737,5310),
    HERREN_SEN("H2",7204,5485),
    HU18("U18",7975,5311),
    HU12("U12",7729,5312),
    HU14("U14",7727,5369),
    HU16("U16",7960,5488),
    //HU09("U10",7236,5313),
    MIX("Mix",7984,5489);


    private String shortName;
    private int liga;
    private int team;

    Mannschaft(String shortName,int liga, int team) {
        this.shortName = shortName;

        this.liga = liga;
        this.team = team;
    }

    public String getShortName() {
        return shortName;
    }

    public int getLiga() {
        return liga;
    }

    public int getTeam() {
        return team;
    }
}

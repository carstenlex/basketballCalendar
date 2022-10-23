package de.carstenlex;

public enum Mannschaft {

    // SHortname zur Anzeige im Calendar; liga=id im Basketplan und team = id im Basketplan
    HERREN1("H1",8279,5310),
    HU18("U18",8290,5311),
    HU12("U12",8310,5312),
    HU12_2("U12-2",8310,6236),
    HU14("HU14",8296,6437),
    MixU14("HU14",8309,5369),
    DU14("DU14",8452,5491),
    HU16("HU16",8292,5488),
    //HU09("U10",7236,5313),
    MIX("Mix",8314,5489);


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

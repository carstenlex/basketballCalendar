package de.carstenlex;

public enum Mannschaft {

    // SHortname zur Anzeige im Calendar; liga=id im Basketplan und team = id im Basketplan
    HERREN1("H1",7204,5310),
    HERREN_SEN("H2",7204,5485),
    HU17("U17",7211,5311),
    HU11("U11",7235,5312),
    HU13("U13",7234,5369),
    HU15("U15",7217,5488),
    HU09("U9",7236,5313),
    MIX("Mix",7232,5489);


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

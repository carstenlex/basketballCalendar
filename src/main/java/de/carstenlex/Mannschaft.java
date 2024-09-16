package de.carstenlex;

public enum Mannschaft {

    // SHortname zur Anzeige im Calendar; liga=id im Basketplan und team = id im Basketplan
    HERREN1("Herren",9879,5310, false,"m",18,99),
    //HU18("U18",9105,5311,true,"m",16,17),
    HU12("U12",9872,5312,true,"mix",10,11),
    //HU12_2("U12-2",8310,6236,true,"mix",10,11),
    HU14("HU14",9884,6437,true,"m",12,13),
    MixU14("U14-Mix",9874,7259,true,"mix",12,13),
    //DU14("DU14",8604,5491,true,"w",12,13),
    DU18("DU16",9927,6849,true,"w",14,17),
    HU16("HU16",9902,5488,true,"m",14,15),
    U10("U10",7236,5313,true,"mix",8,9),
    U8("U8",9870,6092,true,"mix",7,6);
    //MIX("Mix",8314,5489,false,"mix",18,99);


    private String shortName;
    private int liga;
    private int team;

    private boolean jugend;
    private String geschlecht;
    private int alterVon;
    private int alterBis;

    Mannschaft(String shortName,int liga, int team, boolean jugend, String geschlecht,int alterVon, int alterBis) {
        this.shortName = shortName;

        this.liga = liga;
        this.team = team;
        this.jugend = jugend;
        this.geschlecht = geschlecht;
        this.alterVon = alterVon;
        this.alterBis = alterBis;
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

    public boolean isJugend(){
        return jugend == true;
    }

    public boolean isWeiblich(){
        return geschlecht.equals("w");
    }
    public boolean isMaennlich(){
        return geschlecht.equals("m");
    }
    public boolean isMix(){
        return geschlecht.equals("mix");
    }

    public int getAlterVon() {
        return alterVon;
    }

    public int getAlterBis() {
        return alterBis;
    }
}

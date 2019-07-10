package no.finansportalen.fripolise;

public enum Sex {
    MALE("Mann"),
    FEMALE("Kvinne");
    
    private String label;
    
    Sex(String label) {
        this.label = label;
    }
    
    public static Sex fromLabel(String label) {
        for (Sex sex : Sex.values()) {
            if (sex.label.equals(label)) return sex;
        }
        throw new IllegalArgumentException("Invalid Sex label:" + label);
    }
    
    public String getLabel() {
        return label;
    }
    
}

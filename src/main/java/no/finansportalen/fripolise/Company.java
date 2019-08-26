package no.finansportalen.fripolise;

public enum Company {
    DNB("DNB"),
    STOREBRAND("Storebrand"),
    NORDEA_LIV("Nordea Liv"),
    SPAREBANK("Sparebank 1"),
    GJENSIDIGE_PENSJON("Gjensidige Pensjon"),
    KLP_BEDRIFTSPENSJON("KLP Bedriftspensjon");
    
    private String label;
    
    Company(String label) {
        this.label = label;
    }
    
    public static Company fromLabel(String label) {
        
        for (Company company : Company.values()) {
            if (company.label.equals(label)) return company;
        }
        throw new IllegalArgumentException("Invalid Company label:" + label);
    }
    
    public String getLabel() {
        return label;
    }
}

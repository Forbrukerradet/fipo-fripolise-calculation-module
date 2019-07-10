package no.finansportalen.fripolise;

public enum Payout {
    
    LIFELONG("Livsvarig", 120),
    UNTIL_AGE_82("Til 82 år", 82),
    UNTIL_AGE_77("Til 77 år", 77);
    
    private String label;
    private int payoutStopAge;
    
    
    Payout(String label, int payoutStopAge) {
        this.label = label;
        this.payoutStopAge = payoutStopAge;
    }
    
    public String getLabel() {
        return label;
    }
    
    public int getPayoutStopAge() {
        return payoutStopAge;
    }
    
    public static Payout fromLabel(String label) {
        
        for (Payout payout : Payout.values()) {
            if (payout.label.equals(label)) return payout;
        }
        throw new IllegalArgumentException("Invalid Payout label:" + label);
    }
    
    public static Payout fromPayoutStopAge(int payoutStopAge) {
        
        for (Payout payout : Payout.values()) {
            if (payout.payoutStopAge == payoutStopAge) return payout;
        }
        throw new IllegalArgumentException("Invalid Payout label:" + payoutStopAge);
    }
}

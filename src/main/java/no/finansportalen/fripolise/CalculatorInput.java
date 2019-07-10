package no.finansportalen.fripolise;

import java.util.*;

public class CalculatorInput {
    private double arligSikretAlderspensjon;
    private Sex kjonn;
    private double alder;
    private int pensjonsalder = 67;
    private int utbetalingStopperAlder;
    private double garantertRente;
    private double premiereserve;
    private double tilleggsavsetninger;
    private double kursreserve;
    private Company selskap;
    private double[] interestRates;
    private double calculationTime;
    
    public CalculatorInput(
            double arligSikretAlderspensjon,
            Sex kjonn,
            Date fodselsdato,
            int utbetalingStopperAlder,
            double garantertRente,
            double premiereserve,
            double tilleggsavsetninger,
            double kursreserve,
            Company selskap,
            double[] interestRates,
            long eiopaPublishDate
    ) throws FripoliseCalculationModuleException {
        this.calculationTime = calculateCalculationTime(eiopaPublishDate);
        
        this.arligSikretAlderspensjon = arligSikretAlderspensjon;
        this.kjonn = kjonn;
        this.alder = calculateAge(fodselsdato, this.calculationTime);
        this.utbetalingStopperAlder = utbetalingStopperAlder;
        this.garantertRente = garantertRente;
        this.premiereserve = premiereserve;
        this.tilleggsavsetninger = tilleggsavsetninger;
        this.kursreserve = kursreserve;
        this.selskap = selskap;
        this.interestRates = interestRates;
        
        validateCalculatorInput(this);
    }
    
    /**
     *
     * @param eiopaPublishDate Unix time in milliseconds
     * @return whole part of year of the eiopaPublishDate plus the fractional part year that has passed since the start
     * of the year up until eiopaPublishDate. Is used here for age calculation and in calculation module as
     * calculationTime parameter
     */
    public static double calculateCalculationTime(long eiopaPublishDate) {
        
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(eiopaPublishDate);
        
        int numberOfDaysInThisYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
        
        double dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        
        return year + (dayOfYear / numberOfDaysInThisYear);
    }
    
    /**
     *
     * @param dateOfBirth date of birth of the person
     * @param calculationTime calculated via no.finansportalen.fripolise.CalculatorInput#calculateCalculationTime(long)
     * @return age as a fraction, similar to no.finansportalen.fripolise.CalculatorInput#calculateCalculationTime(long)
     */
    private double calculateAge(Date dateOfBirth, double calculationTime) {
        
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(dateOfBirth);
        
        int numberOfDaysInThisYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
        
        double dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        
        double birthTime = year + (dayOfYear / numberOfDaysInThisYear);
        return calculationTime - birthTime;
        
    }
    
    /**
     * Sanity checks for relative values of age, retirement age and pension payout stop age.
     * Calculation module expects birth dates of non retired users
     * age must be less than retirement age
     * retirement age must be less than pension payout stop age
     */
    private void validateCalculatorInput(CalculatorInput input) throws FripoliseCalculationModuleException {
        
        if (input.getAlder() <= 0
                || input.getPensjonsalder() <= input.getAlder()
                || input.getUtbetalingStopperAlder() <= input.getPensjonsalder()) {
            throw new FripoliseCalculationModuleException("Incorrect input detected:[input.getAge() <= 0 || input.getRetirementage() <= input.getAge() || input.getStopage() <= input.getRetirementage()]");
        }
    }
    
    public double getArligSikretAlderspensjon() {
        return arligSikretAlderspensjon;
    }
    
    public void setArligSikretAlderspensjon(double arligSikretAlderspensjon) {
        this.arligSikretAlderspensjon = arligSikretAlderspensjon;
    }
    
    public Sex getKjonn() {
        return kjonn;
    }
    
    public void setKjonn(Sex kjonn) {
        this.kjonn = kjonn;
    }
    
    public double getAlder() {
        return alder;
    }
    
    public void setAlder(double alder) {
        this.alder = alder;
    }
    
    public int getPensjonsalder() {
        return pensjonsalder;
    }
    
    public void setPensjonsalder(int pensjonsalder) {
        this.pensjonsalder = pensjonsalder;
    }
    
    public int getUtbetalingStopperAlder() {
        return utbetalingStopperAlder;
    }
    
    public void setUtbetalingStopperAlder(int utbetalingStopperAlder) {
        this.utbetalingStopperAlder = utbetalingStopperAlder;
    }
    
    public double getGarantertRente() {
        return garantertRente;
    }
    
    public void setGarantertRente(double garantertRente) {
        this.garantertRente = garantertRente;
    }
    
    public double getPremiereserve() {
        return premiereserve;
    }
    
    public void setPremiereserve(double premiereserve) {
        this.premiereserve = premiereserve;
    }
    
    public double getTilleggsavsetninger() {
        return tilleggsavsetninger;
    }
    
    public void setTilleggsavsetninger(double tilleggsavsetninger) {
        this.tilleggsavsetninger = tilleggsavsetninger;
    }
    
    public double getKursreserve() {
        return kursreserve;
    }
    
    public void setKursreserve(double kursreserve) {
        this.kursreserve = kursreserve;
    }
    
    public Company getSelskap() {
        return selskap;
    }
    
    public void setSelskap(Company selskap) {
        this.selskap = selskap;
    }
    
    public double[] getInterestRates() {
        return interestRates;
    }
    
    public double getCalculationTime() {
        return calculationTime;
    }
    
    public void setInterestRates(double[] interestRates) {
        this.interestRates = interestRates;
    }
    
    public void setCalculationTime(double calculationTime) {
        this.calculationTime = calculationTime;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalculatorInput)) return false;
        CalculatorInput input = (CalculatorInput) o;
        return Double.compare(input.arligSikretAlderspensjon, arligSikretAlderspensjon) == 0 &&
                Double.compare(input.alder, alder) == 0 &&
                pensjonsalder == input.pensjonsalder &&
                utbetalingStopperAlder == input.utbetalingStopperAlder &&
                Double.compare(input.garantertRente, garantertRente) == 0 &&
                Double.compare(input.premiereserve, premiereserve) == 0 &&
                Double.compare(input.tilleggsavsetninger, tilleggsavsetninger) == 0 &&
                Double.compare(input.kursreserve, kursreserve) == 0 &&
                Double.compare(input.calculationTime, calculationTime) == 0 &&
                kjonn == input.kjonn &&
                selskap == input.selskap &&
                Arrays.equals(interestRates, input.interestRates);
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(arligSikretAlderspensjon, kjonn, alder, pensjonsalder, utbetalingStopperAlder, garantertRente, premiereserve, tilleggsavsetninger, kursreserve, selskap, calculationTime);
        result = 31 * result + Arrays.hashCode(interestRates);
        return result;
    }
}

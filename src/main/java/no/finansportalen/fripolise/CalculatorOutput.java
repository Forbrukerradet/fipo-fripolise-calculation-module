package no.finansportalen.fripolise;

import java.util.Objects;

public class CalculatorOutput {
    
    private double garantertverdi;
    private double opsjonsverdi;
    private double markedsverdi;
    private double tilbudtverdi;
    private double premiereserveTilAlderspensjon;
    private double tilleggsavsetningerTilAlderspensjon;
    private double kursreserveTilAlderspensjon;
    private double garantiverdi;
    private double garantiverdiUtenOpsjon;
    private double forsikringsdekninger;
    
    public CalculatorOutput(double garantertverdi, double opsjonsverdi, double markedsverdi, double tilbudtverdi, double premiereserveTilAlderspensjon, double tilleggsavsetningerTilAlderspensjon, double kursreserveTilAlderspensjon, double garantiverdi, double garantiverdiUtenOpsjon, double forsikringsdekninger) {
        this.garantertverdi = garantertverdi;
        this.opsjonsverdi = opsjonsverdi;
        this.markedsverdi = markedsverdi;
        this.tilbudtverdi = tilbudtverdi;
        this.premiereserveTilAlderspensjon = premiereserveTilAlderspensjon;
        this.tilleggsavsetningerTilAlderspensjon = tilleggsavsetningerTilAlderspensjon;
        this.kursreserveTilAlderspensjon = kursreserveTilAlderspensjon;
        this.garantiverdi = garantiverdi;
        this.garantiverdiUtenOpsjon = garantiverdiUtenOpsjon;
        this.forsikringsdekninger = forsikringsdekninger;
    }
    
    public double getGarantertverdi() {
        return garantertverdi;
    }
    
    public void setGarantertverdi(double garantertverdi) {
        this.garantertverdi = garantertverdi;
    }
    
    public double getOpsjonsverdi() {
        return opsjonsverdi;
    }
    
    public void setOpsjonsverdi(double opsjonsverdi) {
        this.opsjonsverdi = opsjonsverdi;
    }
    
    public double getMarkedsverdi() {
        return markedsverdi;
    }
    
    public void setMarkedsverdi(double markedsverdi) {
        this.markedsverdi = markedsverdi;
    }
    
    public double getTilbudtverdi() {
        return tilbudtverdi;
    }
    
    public void setTilbudtverdi(double tilbudtverdi) {
        this.tilbudtverdi = tilbudtverdi;
    }
    
    public double getPremiereserveTilAlderspensjon() {
        return premiereserveTilAlderspensjon;
    }
    
    public void setPremiereserveTilAlderspensjon(double premiereserveTilAlderspensjon) {
        this.premiereserveTilAlderspensjon = premiereserveTilAlderspensjon;
    }
    
    public double getTilleggsavsetningerTilAlderspensjon() {
        return tilleggsavsetningerTilAlderspensjon;
    }
    
    public void setTilleggsavsetningerTilAlderspensjon(double tilleggsavsetningerTilAlderspensjon) {
        this.tilleggsavsetningerTilAlderspensjon = tilleggsavsetningerTilAlderspensjon;
    }
    
    public double getKursreserveTilAlderspensjon() {
        return kursreserveTilAlderspensjon;
    }
    
    public void setKursreserveTilAlderspensjon(double kursreserveTilAlderspensjon) {
        this.kursreserveTilAlderspensjon = kursreserveTilAlderspensjon;
    }
    
    public double getGarantiverdi() {
        return garantiverdi;
    }
    
    public void setGarantiverdi(double garantiverdi) {
        this.garantiverdi = garantiverdi;
    }
    
    public double getGarantiverdiUtenOpsjon() {
        return garantiverdiUtenOpsjon;
    }
    
    public void setGarantiverdiUtenOpsjon(double garantiverdiUtenOpsjon) {
        this.garantiverdiUtenOpsjon = garantiverdiUtenOpsjon;
    }
    
    public double getForsikringsdekninger() {
        return forsikringsdekninger;
    }
    
    public void setForsikringsdekninger(double forsikringsdekninger) {
        this.forsikringsdekninger = forsikringsdekninger;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalculatorOutput output = (CalculatorOutput) o;
        return Double.compare(output.garantertverdi, garantertverdi) == 0 &&
                Double.compare(output.opsjonsverdi, opsjonsverdi) == 0 &&
                Double.compare(output.markedsverdi, markedsverdi) == 0 &&
                Double.compare(output.tilbudtverdi, tilbudtverdi) == 0 &&
                Double.compare(output.premiereserveTilAlderspensjon, premiereserveTilAlderspensjon) == 0 &&
                Double.compare(output.tilleggsavsetningerTilAlderspensjon, tilleggsavsetningerTilAlderspensjon) == 0 &&
                Double.compare(output.kursreserveTilAlderspensjon, kursreserveTilAlderspensjon) == 0 &&
                Double.compare(output.garantiverdi, garantiverdi) == 0 &&
                Double.compare(output.garantiverdiUtenOpsjon, garantiverdiUtenOpsjon) == 0 &&
                Double.compare(output.forsikringsdekninger, forsikringsdekninger) == 0;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(garantertverdi, opsjonsverdi, markedsverdi, tilbudtverdi, premiereserveTilAlderspensjon, tilleggsavsetningerTilAlderspensjon, kursreserveTilAlderspensjon, garantiverdi, garantiverdiUtenOpsjon, forsikringsdekninger);
    }
    
    
}

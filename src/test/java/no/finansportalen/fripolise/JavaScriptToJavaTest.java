package no.finansportalen.fripolise;

import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

public class JavaScriptToJavaTest extends FripoliseCalculatorJavaScriptTest {
    
    @Test
    public void testCalculationDefaultInput() throws Exception {
        CalculatorOutput javaOutput = calculationModule.calculateOutput(getDefaultCalculatorInput());
        CalculatorOutput jsOutput = getJavaScriptCalculatorOutput(engine, getDefaultCalculatorInput());
        
        assertEqualOutputs(javaOutput, jsOutput);
    }
    
    @Test
    public void testCalculationAllEnums() throws Exception {
//        Company does nothing
        
        CalculatorInput input = getDefaultCalculatorInput();
        
        for (Sex sex : Sex.values()) {
            
            input.setKjonn(sex);
            
            for (Payout payout : Payout.values()) {
                
                input.setUtbetalingStopperAlder(payout.getPayoutStopAge());
                
                CalculatorOutput javaOutput = calculationModule.calculateOutput(input);
                CalculatorOutput jsOutput = getJavaScriptCalculatorOutput(engine, input);
                assertEqualOutputs(javaOutput, jsOutput);
                
            }
        }
    }
    
    @Ignore
    @Test
    public void testCalculationInputFromFile() throws Exception {
        
        List<CalculatorInput> inputs = getCalculatorInputsFromFile(CSV_FILE_3072);
        
        for (CalculatorInput input : inputs) {
            CalculatorOutput javaOutput = calculationModule.calculateOutput(input);
            CalculatorOutput jsOutput = getJavaScriptCalculatorOutput(engine, input);
            assertEqualOutputs(javaOutput, jsOutput);
        }
    }
    
    @Test
    public void testCalculationInterestRatesFromFile() throws Exception {
        
        List<double[]> rates = interestRatesFromFile(INTEREST_RATES_FILE);
        CalculatorInput input = getDefaultCalculatorInput();
        
        for (double[] rate : rates) {
            input.setInterestRates(rate);
            input.setCalculationTime(CalculatorInput.calculateCalculationTime(getDefaultEiopaInterestRateEiopaPublishDate()));
            CalculatorOutput javaOutput = calculationModule.calculateOutput(input);
            CalculatorOutput jsOutput = getJavaScriptCalculatorOutput(engine, input);
            
            assertEqualOutputs(javaOutput, jsOutput);
        }
    }
    
}

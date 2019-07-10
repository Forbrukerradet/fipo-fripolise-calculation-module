package no.finansportalen.fripolise;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertFalse;


public class FripoliseCalculationModuleTest extends FripoliseCalculatorTest {
    
    private static final Logger log = LoggerFactory.getLogger(FripoliseCalculationModuleTest.class);
    
    private static final double maxAcceptableRelativeErrorDiff = 0.00000000001D;
    
    @Test
    public void testPresentValueGuaranteedPension() throws Exception {
        List<PresentValueGuaranteedPensionTestData> inputs = getPresentValueGuaranteedPensionInputsFromFile();
        logJavascriptErrors(inputs);
        compareErrors(getPresentValueGuaranteedPensionErrors(inputs, PresentValueGuaranteedPensionTestData::getJavascriptOutput), inputs, "getJavascriptOutput");
    }
    
    @Test
    public void testPresentValueOption() throws Exception {
        List<PresentValueOptionTestData> inputs = getPresentValueOptionInputsFromFile();
        logJavascriptErrors(inputs);
        compareErrors(getPresentValueOptionErrors(inputs, PresentValueOptionTestData::getJavascriptOutput), inputs, "getJavascriptOutput");
    }
    
    private void logJavascriptErrors(List<? extends PresentValueGuaranteedPensionTestData> inputs) {
        
        double javascriptMaxAbsoluteErrorAbs = inputs.stream().mapToDouble(x -> Math.abs(x.javascriptAbsoluteError)).max().getAsDouble();
        double javascriptAverageAbsoluteErrorAbs = inputs.stream().mapToDouble(x -> Math.abs(x.javascriptAbsoluteError)).average().getAsDouble();
        double javascriptMaxRelativeErrorAbs = inputs.stream().mapToDouble(x -> Math.abs(x.javascriptRelativeError)).max().getAsDouble();
        double javascriptAverageRelativeErrorAbs = inputs.stream().mapToDouble(x -> Math.abs(x.javascriptRelativeError)).average().getAsDouble();
        
        log.info("JavascriptMaxAbsoluteErrorAbs:{}", javascriptMaxAbsoluteErrorAbs);
        log.info("JavascriptAverageAbsoluteErrorAbs:{}", javascriptAverageAbsoluteErrorAbs);
        log.info("JavascriptMaxRelativeErrorAbs:{}", javascriptMaxRelativeErrorAbs);
        log.info("JavascriptAverageRelativeErrorAbs:{}", javascriptAverageRelativeErrorAbs);
    }
    
    private void compareErrors(List<ErrorPair> errors, List<? extends PresentValueGuaranteedPensionTestData> inputs, String outputToCompare) {
        
        double javaMaxAbsoluteErrorAbs = errors.stream().mapToDouble(x -> Math.abs(x.getLeft())).max().getAsDouble();
        double javaAverageAbsoluteErrorAbs = errors.stream().mapToDouble(x -> Math.abs(x.getLeft())).average().getAsDouble();
        double javaMaxRelativeErrorAbs = errors.stream().mapToDouble(x -> Math.abs(x.getRight())).max().getAsDouble();
        double javaAverageRelativeErrorAbs = errors.stream().mapToDouble(x -> Math.abs(x.getRight())).average().getAsDouble();
        
        log.info("CompareTo:{}", outputToCompare);
        log.info("JavaMaxAbsoluteErrorAbs:{}", javaMaxAbsoluteErrorAbs);
        log.info("JavaAverageAbsoluteErrorAbs:{}", javaAverageAbsoluteErrorAbs);
        log.info("JavaMaxRelativeErrorAbs:{}", javaMaxRelativeErrorAbs);
        log.info("JavaAverageRelativeErrorAbs:{}", javaAverageRelativeErrorAbs);
        
        assertFalse(javaAverageRelativeErrorAbs > maxAcceptableRelativeErrorDiff);
    }
    
    private class ErrorPair {
        private double left;
        private double right;
        
        public ErrorPair(double left, double right) {
            this.left = left;
            this.right = right;
        }
        
        public double getLeft() {
            return left;
        }
        
        public double getRight() {
            return right;
        }
    }
    
    
    private class PresentValueGuaranteedPensionTestData {
        double garantertRente;
        Sex kjonn;
        double alder;
        int pensjonsalder;
        int utbetalingStopperAlder;
        
        double getMathematicaOutput() {
            return mathematicaOutput;
        }
        
        double getJavascriptOutput() {
            return javascriptOutput;
        }
        
        double mathematicaOutput;
        double javascriptOutput;
        double javascriptAbsoluteError;
        double javascriptRelativeError;
        
        PresentValueGuaranteedPensionTestData(double garantertRente, Sex kjonn, double alder, int pensjonsalder, int utbetalingStopperAlder, double mathematicaOutput, double javascriptOutput, double javascriptAbsoluteError, double javascriptRelativeError) {
            this.garantertRente = garantertRente;
            this.kjonn = kjonn;
            this.alder = alder;
            this.pensjonsalder = pensjonsalder;
            this.utbetalingStopperAlder = utbetalingStopperAlder;
            this.mathematicaOutput = mathematicaOutput;
            this.javascriptOutput = javascriptOutput;
            this.javascriptAbsoluteError = javascriptAbsoluteError;
            this.javascriptRelativeError = javascriptRelativeError;
        }
    }
    
    private class PresentValueOptionTestData extends PresentValueGuaranteedPensionTestData {
        
        double aksjeandel;
        
        PresentValueOptionTestData(double garantertRente, Sex kjonn, double alder, int pensjonsalder, int utbetalingStopperAlder, double mathematicaOutput, double javascriptOutput, double javascriptAbsoluteError, double javascriptRelativeError, double aksjeandel) {
            super(garantertRente, kjonn, alder, pensjonsalder, utbetalingStopperAlder, mathematicaOutput, javascriptOutput, javascriptAbsoluteError, javascriptRelativeError);
            this.aksjeandel = aksjeandel;
        }
    }
    
    private List<ErrorPair> getPresentValueGuaranteedPensionErrors(List<PresentValueGuaranteedPensionTestData> inputs, Function<PresentValueGuaranteedPensionTestData, Double> func) {
        
        setUpModule(calculationModule);
        
        List<ErrorPair> errors = new ArrayList<>();
        
        for (PresentValueGuaranteedPensionTestData input : inputs) {
            double javaValue = calculationModule.presentValueGuaranteedPension(
                    input.garantertRente,
                    input.kjonn,
                    input.alder,
                    input.pensjonsalder,
                    input.utbetalingStopperAlder,
                    0.0
            );
            double absoluteError = javaValue - func.apply(input);
            errors.add(new ErrorPair(absoluteError, absoluteError / javaValue));
        }
        return errors;
    }
    
    private List<ErrorPair> getPresentValueOptionErrors(List<PresentValueOptionTestData> inputs, Function<PresentValueOptionTestData, Double> func) {
        
        setUpModule(calculationModule);
        
        List<ErrorPair> errors = new ArrayList<>();
        
        for (PresentValueOptionTestData input : inputs) {
            double javaValue = calculationModule.presentValueOption(
                    input.garantertRente,
                    input.aksjeandel,
                    input.kjonn,
                    input.alder,
                    input.pensjonsalder,
                    input.utbetalingStopperAlder
            );
            double absoluteError = javaValue - func.apply(input);
            errors.add(new ErrorPair(absoluteError, absoluteError / javaValue));
        }
        return errors;
    }
    
    private void setUpModule(FripoliseCalculationModule calculationModule) {
        
        DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_OF_BIRTH_FORMAT);
        calculationModule.setCalculationTime(CalculatorInput.calculateCalculationTime(formatter.parseDateTime("30.04.2019").getMillis()));
        calculationModule.setEiopaInterestRate(rates);
        
    }
    
    //    Testing av funksjonen som gir nåverdi: presentValueGuaranteedPension(calculationRate, male, age, retirementAge, terminationAge)
    private List<PresentValueGuaranteedPensionTestData> getPresentValueGuaranteedPensionInputsFromFile() throws FileNotFoundException {
        
        List<PresentValueGuaranteedPensionTestData> inputs = new LinkedList<>();
        File file = ResourceUtils.getFile(this.getClass().getResource(PRESENT_VALUE_GUARANTEED_PENSION_FILE));
        
        String line;
        String cvsSplitBy = ",";
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                inputs.add(inputFromFileLine(line.split(cvsSplitBy)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputs;
    }
    
    private PresentValueGuaranteedPensionTestData inputFromFileLine(String[] data) {
        return new PresentValueGuaranteedPensionTestData(
                Double.parseDouble(data[0]),
                Sex.valueOf(data[1]),
                Double.parseDouble(data[2]),
                Integer.parseInt(data[3]),
                Integer.parseInt(data[4]),
                Double.parseDouble(data[5]),
                Double.parseDouble(data[6]),
                Double.parseDouble(data[7]),
                Double.parseDouble(data[8])
        );
    }
    
    private List<PresentValueOptionTestData> getPresentValueOptionInputsFromFile() throws FileNotFoundException {
        
        List<PresentValueOptionTestData> inputs = new LinkedList<>();
        File file = ResourceUtils.getFile(this.getClass().getResource(PRESENT_VALUE_OPTION_FILE));
        
        String line;
        String cvsSplitBy = ",";
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                
                inputs.add(new PresentValueOptionTestData(
                        Double.parseDouble(data[0]),
                        Sex.valueOf(data[1]),
                        Double.parseDouble(data[2]),
                        Integer.parseInt(data[3]),
                        Integer.parseInt(data[4]),
                        Double.parseDouble(data[6]),
                        Double.parseDouble(data[7]),
                        Double.parseDouble(data[8]),
                        Double.parseDouble(data[9]),
                        Double.parseDouble(data[5])
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputs;
    }
    
    private static double[] rates = {0, 0.01485, 0.01664, 0.01725, 0.01775, 0.01821, 0.0186, 0.01899, 0.01939, 0.01976, 0.02013, 0.02057, 0.02105, 0.02156, 0.02208, 0.0226, 0.0231, 0.0236, 0.02408, 0.02455, 0.02499, 0.02542, 0.02584, 0.02623, 0.02661, 0.02697, 0.02732, 0.02765, 0.02796, 0.02827, 0.02856, 0.02883, 0.0291, 0.02935, 0.02959, 0.02983, 0.03005, 0.03026, 0.03047, 0.03066, 0.03085, 0.03103, 0.03121, 0.03138, 0.03154, 0.03169, 0.03184, 0.03199, 0.03212, 0.03226, 0.03239, 0.03251, 0.03263, 0.03275, 0.03286, 0.03297, 0.03307, 0.03317, 0.03327, 0.03336, 0.03346, 0.03355, 0.03363, 0.03372, 0.0338, 0.03388, 0.03395, 0.03403, 0.0341, 0.03417, 0.03424, 0.0343, 0.03437, 0.03443, 0.03449, 0.03455, 0.03461, 0.03467, 0.03472, 0.03478, 0.03483, 0.03488, 0.03493, 0.03498, 0.03503, 0.03507, 0.03512, 0.03516, 0.03521, 0.03525, 0.03529, 0.03533, 0.03537, 0.03541, 0.03545, 0.03548, 0.03552, 0.03556, 0.03559, 0.03563, 0.03566, 0.03569, 0.03573, 0.03576, 0.03579, 0.03582, 0.03585, 0.03588, 0.03591, 0.03593, 0.03596, 0.03599, 0.03602, 0.03604, 0.03607, 0.03609, 0.03612, 0.03614, 0.03617, 0.03619, 0.03622, 0.03624, 0.03626, 0.03628, 0.03631, 0.03633, 0.03635, 0.03637, 0.03639, 0.03641, 0.03643, 0.03645, 0.03647, 0.03649, 0.03651, 0.03652, 0.03654, 0.03656, 0.03658, 0.0366, 0.03661, 0.03663, 0.03665, 0.03666, 0.03668, 0.0367, 0.03671, 0.03673, 0.03674, 0.03676, 0.03677};
}
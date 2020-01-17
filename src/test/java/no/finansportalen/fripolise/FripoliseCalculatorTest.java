package no.finansportalen.fripolise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;


public abstract class FripoliseCalculatorTest {
    
    FripoliseCalculationModule calculationModule;

    private static final Logger log = LogManager.getLogger(FripoliseCalculatorTest.class);
    
    static final int EXPECTED_INTEREST_RATE_ARRAY_SIZE_WITHOUT_LEADING_ZERO = 150;
    
    static final String DATE_OF_BIRTH_FORMAT = "dd.MM.yyyy";
    
    static final String JS_FILE = "/fripolise/fripolisekalkulator.js";
    static final String CSV_FILE_3072 = "/fripolise/fripolise-calculator-inputs-3072.csv";
    static final String INTEREST_RATES_FILE = "/fripolise/interest_rates_transposed.csv";
    static final String PRESENT_VALUE_OPTION_FILE = "/fripolise/present-value-option-data.csv";
    static final String PRESENT_VALUE_GUARANTEED_PENSION_FILE = "/fripolise/present-value-guaranteed-pension-data.csv";
    
    @Before
    public void setUp() throws Exception {
        calculationModule = new FripoliseCalculationModule();
    }
    
    private List<double[]> readFromFile(File file) {
        
        String line;
        String cvsSplitBy = ",";
        List<double[]> result = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                result.add(inputFromFile(data));
            }
        } catch (IOException e) {
            log.error("Failed to get interest rates from file:", e);
        }
        return result;
    }
    
    private static double[] inputFromFile(String[] data) {
        
        double[] result = new double[EXPECTED_INTEREST_RATE_ARRAY_SIZE_WITHOUT_LEADING_ZERO];
        
        for (int i = 0; i < data.length; i++) {
            result[i] = Double.parseDouble(data[i]);
        }
        
        return result;
    }
    
    List<double[]> interestRatesFromFile(String name) throws FileNotFoundException {
        return readFromFile(ResourceUtils.getFile(this.getClass().getResource(name)));
    }
    
    private CalculatorInput inputFromFileLine(String[] data) throws ParseException, FripoliseCalculationModuleException {
        
        return new CalculatorInput(
                Integer.parseInt(data[0]),
                Sex.valueOf(data[1]),
                new SimpleDateFormat(DATE_OF_BIRTH_FORMAT, Locale.ENGLISH).parse((data[2])),
                Integer.parseInt(data[3]),
                Double.parseDouble(data[4]),
                Double.parseDouble(data[5]),
                Double.parseDouble(data[6]),
                Double.parseDouble(data[7]),
                Company.DNB,
                getDefaultEiopaInterestRateArray(),
                getDefaultEiopaInterestRateEiopaPublishDate()
        );
    }
    
    List<CalculatorInput> getCalculatorInputsFromFile(String fileName) throws FileNotFoundException {
        
        List<CalculatorInput> inputs = new LinkedList<>();
        File file = ResourceUtils.getFile(this.getClass().getResource(fileName));
        
        String line;
        String cvsSplitBy = ",";
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                inputs.add(inputFromFileLine(line.split(cvsSplitBy)));
            }
            
        } catch (IOException | ParseException | FripoliseCalculationModuleException e) {
            e.printStackTrace();
        }
        return inputs;
    }
    
    private void printRoundedOutput(CalculatorOutput output) {
        List<String> values = new ArrayList<>(Arrays.asList(
                output.getGarantertverdi(),
                output.getOpsjonsverdi(),
                output.getMarkedsverdi(),
                output.getTilbudtverdi(),
                output.getPremiereserveTilAlderspensjon(),
                output.getTilleggsavsetningerTilAlderspensjon(),
                output.getKursreserveTilAlderspensjon(),
                output.getGarantiverdi(),
                output.getGarantiverdiUtenOpsjon(),
                output.getForsikringsdekninger()))
                .stream().map(x -> String.valueOf(Math.round(x))).collect(Collectors.toList());
        
        log.info(String.join(",", values));
    }
    
    void assertEqualOutputs(CalculatorOutput javaOutput, CalculatorOutput jsOutput) {
        
        printRoundedOutput(javaOutput);
        printRoundedOutput(jsOutput);
        log.info("-------------------------------------------");
        
        assertEquals(javaOutput, jsOutput);
        
    }
    
    public static CalculatorInput getDefaultCalculatorInput() throws ParseException, FripoliseCalculationModuleException {
        return new CalculatorInput(
                27617,
                Sex.MALE,
                new SimpleDateFormat(DATE_OF_BIRTH_FORMAT, Locale.ENGLISH).parse("31.08.1972"),
                Payout.LIFELONG.getPayoutStopAge(),
                0.0266,
                327235,
                13807,
                7264,
                Company.DNB,
                getDefaultEiopaInterestRateArray(),
                getDefaultEiopaInterestRateEiopaPublishDate()
        );
    }
    
    public static CalculatorOutput getExpectedOutputToDefaultCalculatorInput() {
        return new CalculatorOutput(
                226037,
                29266,
                255303,
                267050,
                250895,
                10586,
                5569,
                -11747,
                -41013,
                81256
        );
    }
    
    public static double[] getDefaultEiopaInterestRateArray() {
        return new double[]{
                0, 0.01247, 0.01436, 0.01538, 0.01627, 0.01706, 0.01781, 0.01852,
                0.01917, 0.01976, 0.02026, 0.0208, 0.02137, 0.02196, 0.02254,
                0.02312, 0.02368, 0.02422, 0.02475, 0.02525, 0.02573, 0.0262,
                0.02664, 0.02706, 0.02747, 0.02785, 0.02822, 0.02857, 0.02891,
                0.02923, 0.02953, 0.02983, 0.03011, 0.03038, 0.03063, 0.03088,
                0.03111, 0.03134, 0.03155, 0.03176, 0.03196, 0.03215, 0.03233,
                0.03251, 0.03268, 0.03284, 0.033, 0.03315, 0.03329, 0.03343, 0.03357,
                0.0337, 0.03383, 0.03395, 0.03406, 0.03418, 0.03429, 0.03439, 0.0345,
                0.0346, 0.03469, 0.03479, 0.03488, 0.03496, 0.03505, 0.03513,
                0.03521, 0.03529, 0.03537, 0.03544, 0.03551, 0.03558, 0.03565,
                0.03571, 0.03578, 0.03584, 0.0359, 0.03596, 0.03602, 0.03607,
                0.03613, 0.03618, 0.03624, 0.03629, 0.03634, 0.03639, 0.03643,
                0.03648, 0.03653, 0.03657, 0.03661, 0.03666, 0.0367, 0.03674,
                0.03678, 0.03682, 0.03686, 0.03689, 0.03693, 0.03697, 0.037, 0.03704,
                0.03707, 0.0371, 0.03714, 0.03717, 0.0372, 0.03723, 0.03726, 0.03729,
                0.03732, 0.03735, 0.03737, 0.0374, 0.03743, 0.03746, 0.03748,
                0.03751, 0.03753, 0.03756, 0.03758, 0.03761, 0.03763, 0.03765,
                0.03768, 0.0377, 0.03772, 0.03774, 0.03777, 0.03779, 0.03781,
                0.03783, 0.03785, 0.03787, 0.03789, 0.03791, 0.03793, 0.03794,
                0.03796, 0.03798, 0.038, 0.03802, 0.03803, 0.03805, 0.03807, 0.03809,
                0.0381, 0.03812, 0.03813, 0.03815, 0.03817};
    }
    
    public static long getDefaultEiopaInterestRateIdaPublishDate() {
        return DateTime.now().withZone(DateTimeZone.UTC).getMillis() - 172800000;
    }
    
    public static long getDefaultEiopaInterestRateEiopaPublishDate() {
        return DateTime.now().withZone(DateTimeZone.UTC).getMillis() + 172800000;
    }
    
}

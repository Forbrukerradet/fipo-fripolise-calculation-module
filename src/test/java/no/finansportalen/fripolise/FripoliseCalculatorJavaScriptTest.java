package no.finansportalen.fripolise;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;

public class FripoliseCalculatorJavaScriptTest extends FripoliseCalculatorTest {

    private static final Logger log = LogManager.getLogger(FripoliseCalculatorJavaScriptTest.class);
    
    ScriptEngine engine;
    
    @Before
    public void setUp() throws Exception {
        calculationModule = new FripoliseCalculationModule();
        log.info("init engine");
        engine = initEngine();
    }
    
    private ScriptEngine initEngine() throws FileNotFoundException, ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine jsEngine = manager.getEngineByName("JavaScript");
        File file = ResourceUtils.getFile(this.getClass().getResource(JS_FILE));
        
        jsEngine.eval(new java.io.FileReader(file));
        return jsEngine;
    }
    
    @Test
    public void listEngines() {
        ScriptEngineManager manager = new ScriptEngineManager();
        List<ScriptEngineFactory> factoryList = manager.getEngineFactories();
        for (ScriptEngineFactory factory : factoryList) {
            log.info(factory.getEngineName());
            log.info(factory.getLanguageName());
        }
    }
    
    @Ignore
    @Test
    public void testEngineAvailability() throws ScriptException {
        
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine jsEngine = manager.getEngineByName("JavaScript");
//        ScriptEngine jsEngine = manager.getEngineByName("Nashorn");
//        ScriptEngine jsEngine = manager.getEngineByName("ECMAScript");
        
        BigDecimal oldagepension = BigDecimal.valueOf(1458.2313);
        jsEngine.put("oldagepension", oldagepension);
        jsEngine.eval("print(oldagepension)");
        
    }
    
    
    //Need all public fields for access in js tests to work
    public class TestCalculatorInput {
        
        public double oldagepension;
        public Sex sex;
        public double age;
        public int retirementage;
        public int stopage;
        public double calculationRate;
        public double premiumReserve;
        public double TA;
        public double KR;
        public Company company = Company.DNB;
        
        
        public TestCalculatorInput(CalculatorInput input) {
            this.oldagepension = input.getArligSikretAlderspensjon();
            this.sex = input.getKjonn();
            this.age = input.getAlder();
            this.retirementage = input.getPensjonsalder();
            this.stopage = input.getUtbetalingStopperAlder();
            this.calculationRate = input.getGarantertRente();
            this.premiumReserve = input.getPremiereserve();
            this.TA = input.getTilleggsavsetninger();
            this.KR = input.getKursreserve();
        }
    }
    
    CalculatorOutput getJavaScriptCalculatorOutput(ScriptEngine engine, CalculatorInput calculatorInput) throws ScriptException {
        
        engine.put("input", new TestCalculatorInput(calculatorInput));
        engine.put("interestRates", calculatorInput.getInterestRates());
        engine.put("calculationTime", calculatorInput.getCalculationTime());
        
        String script = "var output = calculateOutput(input, interestRates, calculationTime);";
        engine.eval(script);
        
        return getOutput(engine);
    }
    
    
    private CalculatorOutput getOutput(ScriptEngine engine) {
        ScriptObjectMirror outputJs = (ScriptObjectMirror) engine.get("output");
        return new CalculatorOutput(
                (double) outputJs.getMember("garantertverdi"),
                (double) outputJs.getMember("opsjonsverdi"),
                (double) outputJs.getMember("markedsverdi"),
                (double) outputJs.getMember("tilbudtverdi"),
                (double) outputJs.getMember("premiumReserveAP"),
                (double) outputJs.getMember("TAAP"),
                (double) outputJs.getMember("KRAP"),
                (double) outputJs.getMember("garantiverdi"),
                (double) outputJs.getMember("garantiverdiUtenOpsjon"),
                (double) outputJs.getMember("forsikringsdekninger")
        );
        
    }
    
}

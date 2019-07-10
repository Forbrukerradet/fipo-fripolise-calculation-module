package no.finansportalen.fripolise;

public class FripoliseCalculationModule {
    
    private static final int gaussP = 20;
    private static final int trapezP = 200;
    public static final double omega = 120;
    
    private static final double volatility = 0.15044492820393698;
    
    private double calculationTime = 2019.;
    private double[] eiopaInterestRate;
    
    // Mortality medium alternative Finans Norge:
    private double muFNOm(Sex kj, double x) {
        if (kj == Sex.MALE) return (0.21585 + 0.00405 * Math.pow(10, 0.051 * x)) / 1000;
        else return (0.07626 + 0.00278 * Math.pow(10, 0.051 * x)) / 1000;
    }


//     Reduction function medium alternative Finans Norge:
    
    private double wFNOm(Sex kj, double x) {
        if (kj == Sex.MALE) return Math.min(2.42868 - 0.1568 * x + 0.00135 * Math.pow(x, 2), 0);
        else return Math.min(1.17088 - 0.0919 * x + 0.00074 * Math.pow(x, 2), 0);
    }
    
    
    // Intensity with safety margin sS for start mortality og safety margin sF for extrapolations:
    private double muFNO(Sex kj, double x, double t, double sS, double sF) {
        return (1 - sS) * muFNOm(kj, x) * Math.pow(1 + 0.01 * (1 + sF) * wFNOm(kj, x), t - 2013);
    }
    
    private double[][] gauleg(double x1, double x2, int n) {
        
        int size = n + 1;
        double[][] res = new double[2][];
        res[0] = new double[size];
        res[1] = new double[size];
        double xm = 0.5 * (x2 + x1);
        double xl = 0.5 * (x2 - x1);
        double z, p1, p2, p3, pp, z1;
        int i, j;
        for (i = 1; i <= (n + 1) / 2; i++) {
            z = Math.cos(Math.PI * (i - 0.25) / (n + 0.5));
            do {
                p1 = 1.0;
                p2 = 0.0;
                for (j = 1; j <= n; j++) {
                    p3 = p2;
                    p2 = p1;
                    p1 = ((2.0 * j - 1.0) * z * p2 - (j - 1.0) * p3) / j;
                }
                pp = n * (z * p1 - p2) / (z * z - 1.0);
                z1 = z;
                z = z1 - p1 / pp;
            }
            while (Math.abs(z - z1) > .00000000003);
            res[1][i] = xm - xl * z;
            res[1][(n + 1 - i)] = xm + xl * z;
            res[0][(i)] = 2.0 * xl / ((1.0 - z * z) * pp * pp);
            res[0][(n + 1 - i)] = res[0][i];
        }
        return res;
        
    }
    
    
    // Numerical integration trapezoidal rule for oscillatory integrands:
    private double[][] trapez(double x1, double x2, double n) {
        
        int size = (int) n + 1;
        double[][] res = new double[2][];
        res[0] = new double[size];
        res[1] = new double[size];
        
        for (int i = 1; i <= n; i++) {
            res[0][i] = (x2 - x1) / n;
            res[1][i] = x1 + (x2 - x1) * (i - 0.5) / n;
        }
        return res;
    }
    
    
    // Survival probability for age x until time u in year t:
    private double tpxFNO(Sex kj, double x, double u, double t, double sS, double sF) {
        double[][] weight = gauleg(0, u, gaussP);
        double[] h = weight[0], tau = weight[1];
        double sum = 0;
        for (int i = 1; i <= gaussP; i++) {
            sum += h[i] * muFNO(kj, x + tau[i], t + tau[i], sS, sF);
        }
        return Math.exp(-sum);
    }
    
    // Present value per unit old age pension for gender kj, age x in year t from age r to s:
    private double eAP(Sex kj, double x, double t, double r, double s, double v) {
        double[][] weight = gauleg(0, s - r, gaussP);
        double[] h = weight[0], tau = weight[1];
        double sum = 0;
        for (int i = 1; i <= gaussP; i++) {
            sum += h[i] * Math.pow(v, tau[i]) * tpxFNO(kj, r, tau[i], t + (r - x), 0.12, 0.10);
        }
        return Math.pow(v, r - x) * tpxFNO(kj, x, r - x, t, 0.12, 0.10) * sum;
    }
    
    
    // Cumulative distribution function for Standard Normal Distribution:
    private double CDFnormal(double x) {
        if (x <= -5) return 0;
        else if (x >= 5) return 1;
        else {
            double[][] weight = gauleg(-5, x, gaussP);
            double[] h = weight[0], tau = weight[1];
            double sum = 0;
            for (int i = 1; i <= gaussP; i++) {
                sum += h[i] * Math.exp(-0.5 * Math.pow(tau[i], 2)) / Math.sqrt(2 * Math.PI);
            }
            return sum;
        }
    }
    
    
    // Price of put option with share alpha in stocks, guaranteed interest rate gamma, risk free interest rate delta, volatiliy sigma and maturity t:
    private double put(double gamma, double delta, double sigma, double alpha, double t) {
        double S0 = alpha;
        double K = Math.exp(gamma * t) - (1 - alpha) * Math.exp(delta * t);
        double d1 = (Math.log(S0 / K) + (delta * t + (Math.pow(sigma, 2) * t / 2))) / (sigma * Math.sqrt(t));
        double d2 = d1 - sigma * Math.sqrt(t);
        if (alpha > 1 - Math.exp((gamma - delta) * t) && alpha <= 1 && K > 0)
            return K * Math.exp(-delta * t) * CDFnormal(-d2) - S0 * CDFnormal(-d1);
        else return 0;
    }
    
    private double eiopaInterestRateYearly(int t) {
        return Math.pow(1 + eiopaInterestRate[t + 1], t + 1) / Math.pow(1 + eiopaInterestRate[t], t) - 1;
    }
    
    private double eiopaInterestRateSmooth(double t) {
        return eiopaInterestRate[(int) Math.floor(t)] + (t - Math.floor(t)) * (eiopaInterestRate[(int) Math.ceil(t)] - eiopaInterestRate[(int) Math.floor(t)]);
    }
    
    
    private double eiopaInterestRateYearlySmooth(double t) {
        return eiopaInterestRateYearly((int) Math.floor(t)) + (t - Math.floor(t)) * (eiopaInterestRateYearly((int) Math.ceil(t)) - eiopaInterestRateYearly((int) Math.floor(t)));
    }
    
    //initially company enum was deciding w1 and w2 values, but this was scrapped.
    private double stockWeight(double calculationRate, Company company) {
        double w1 = 0.10; // stock weight with the lowest calculation rate
        double w2 = 0.02; // stock weight with the highest calculation rate
        double r1 = 0.02; // the lowest calculation rate
        double r2 = 0.04; // the highest calculation rate
        return w1 + (calculationRate - r1) * (w2 - w1) / (r2 - r1); // Assuming linear reduction in stock weight from lowest to highest calculation rate
    }
    
    // DS 22-05-2019: Replacing eAP() with presentValueGuaranteedPension() in this function:
    private double integrand(double calculationRate, double stockWeight, Sex male, double age, int retirementAge, int terminationAge, double t) {
        return Math.pow(1 / (1 + eiopaInterestRateSmooth(t)), t) * put(Math.log(1 + eiopaInterestRateYearlySmooth(t)), Math.log(1 + eiopaInterestRateYearlySmooth(t)), volatility, stockWeight, 1) *
                tpxFNO(male, age, t, calculationTime, 0.12, 0.10) * presentValueGuaranteedPension(calculationRate, male, age, retirementAge, terminationAge, t);
    }
    
    
    // Assuming age < retirementAge:
    double presentValueOption(double calculationRate, double stockWeight, Sex male, double age, int retirementAge, int terminationAge) {
        double[][] weight1 = trapez(0, retirementAge - age, trapezP);
        double[][] weight2 = trapez(retirementAge - age, terminationAge - age, trapezP);
        double[] h1 = weight1[0], tau1 = weight1[1], h2 = weight2[0], tau2 = weight2[1];
        double sum1 = 0, sum2 = 0;
        for (int i = 1; i <= h1.length - 1; i++) {
            sum1 += h1[i] * integrand(calculationRate, stockWeight, male, age, retirementAge, terminationAge, tau1[i]);
        }
        for (int i = 1; i <= h2.length - 1; i++) {
            sum2 += h2[i] * integrand(calculationRate, stockWeight, male, age, retirementAge, terminationAge, tau2[i]);
        }
        // DS 22-05-2019: Removing the division by eAP() in the output
        return (sum1 + sum2);
    }
    
    
    // DS 22-05-2019: Generalizing this function by adding av variable t to the list of arguments.
    double presentValueGuaranteedPension(double calculationRate, Sex male, double age, int retirementAge, int terminationAge, double t) {
        
        double[][] weight = trapez(Math.max(0, retirementAge - (age + t)), Math.max(0, terminationAge - (age + t)), trapezP);
        double[] h = weight[0], tau = weight[1];
        double sum = 0;
        for (int i = 1; i <= h.length - 1; i++) {
            sum += h[i] * Math.pow(1 + eiopaInterestRateSmooth(t), t) / Math.pow(1 + eiopaInterestRateSmooth(t + tau[i]), t + tau[i]) * tpxFNO(male, age + t, tau[i], calculationTime + t, 0.12, 0.10);
        }
        return sum;
    }
    
    /**
     * Performs the main calculation
     * @param input input to the module wrapped in a single object
     * @return resulting output
     */
    public CalculatorOutput calculateOutput(CalculatorInput input) {
        
        this.eiopaInterestRate = input.getInterestRates();
        this.calculationTime = input.getCalculationTime();
        
        double oldagepension = input.getArligSikretAlderspensjon();
        Sex male = input.getKjonn();
        double age = input.getAlder();
        int retirementage = input.getPensjonsalder();
        int stopage = input.getUtbetalingStopperAlder();
        double calculationRate = input.getGarantertRente();
        double premiumReserve = input.getPremiereserve();
        double TA = input.getTilleggsavsetninger();
        double KR = input.getKursreserve();
        Company company = input.getSelskap();
        double premiumReserveAP = Math.round(oldagepension * eAP(male, age, calculationTime, retirementage, stopage, 1 / (1 + calculationRate)));
        double TAAP = Math.round(TA * premiumReserveAP / premiumReserve);
        double KRAP = Math.round(KR * premiumReserveAP / premiumReserve);
        double tilbudtverdi = premiumReserveAP + TAAP + KRAP;
        double forsikringsdekninger = premiumReserve + TA + KR - tilbudtverdi;
        // DS 22-05-2019: Adding the last parameter to the new function presentValueGuaranteedPension:
        double garantertverdi = Math.round(oldagepension * presentValueGuaranteedPension(calculationRate, male, age, retirementage, stopage, 0));
        // DS 22-05-2019: Changed output from presentValueOption() needs to be multiplied by oldagepension instead of premiumReserveAP:
        double opsjonsverdi = Math.round(oldagepension * presentValueOption(calculationRate, stockWeight(calculationRate, company), male, age, retirementage, stopage));
        double markedsverdi = garantertverdi + opsjonsverdi;
        double garantiverdi = markedsverdi - tilbudtverdi;
        double garantiverdiUtenOpsjon = garantertverdi - tilbudtverdi;
        
        
        return new CalculatorOutput(
                garantertverdi,
                opsjonsverdi,
                markedsverdi,
                tilbudtverdi,
                premiumReserveAP,
                TAAP,
                KRAP,
                garantiverdi,
                garantiverdiUtenOpsjon,
                forsikringsdekninger);
    }
    
    
    void setCalculationTime(double calculationTime) {
        this.calculationTime = calculationTime;
    }
    
    void setEiopaInterestRate(double[] eiopaInterestRate) {
        this.eiopaInterestRate = eiopaInterestRate;
    }
}

var gaussP = 20;
var trapezP = 200;
var omega = 120;

// Parameters:
var volatility = 0.15044492820393698;
var calculationTime = 2019.;

// Mortality medium alternative Finans Norge:
function muFNOm(kj, x) { 
   if(kj == "MALE") return (0.21585 + 0.00405 * Math.pow(10, 0.051*x))/1000;
   else          return (0.07626 + 0.00278 * Math.pow(10, 0.051*x))/1000;
}

// Reduction function medium alternative Finans Norge:
function wFNOm(kj, x) {
   if(kj == "MALE") return Math.min(2.42868 - 0.1568*x + 0.00135*Math.pow(x, 2), 0);
   else          return Math.min(1.17088 - 0.0919*x + 0.00074*Math.pow(x, 2), 0);
}
// Intensity with safety margin sS for start mortality og safety margin sF for extrapolations: 
function muFNO(kj, x, t, sS, sF) {
   return (1 - sS) * muFNOm(kj, x) * Math.pow(1 + 0.01*(1 + sF)*wFNOm(kj, x), t - 2013);
}

// Gauss Legendre method for numerical integration with smooth integrands: 
function gauleg(x1, x2, n) {
   var res = new Array();
   res[0] = new Array();
   res[1] = new Array();	
   var xm = 0.5*(x2+x1);
   var xl = 0.5*(x2-x1);
   var z, p1, p2, p3, pp, z1;
   var i, j;
   for (i=1;i<=(n+1)/2;i++) {
      z = Math.cos(Math.PI*(i-0.25)/(n+0.5));
      do {
         p1 = 1.0;
         p2 = 0.0;
         for (j=1;j<=n;j++) {
            p3 = p2;
            p2 = p1;
            p1 = ((2.0*j-1.0)*z*p2-(j-1.0)*p3)/j;
         }
         pp = n*(z*p1-p2)/(z*z-1.0);
         z1 = z;
         z  = z1-p1/pp;
      } 
      while (Math.abs(z-z1) > .00000000003);
      res[1][i]     = xm-xl*z;
      res[1][n+1-i] = xm+xl*z;
      res[0][i]       = 2.0*xl/((1.0-z*z)*pp*pp);
      res[0][n+1-i]   = res[0][i];
   }
   return res;
}

// Numerical integration trapezoidal rule for oscillatory integrands:
function trapez(x1, x2, n) {
   var res = new Array();
   res[0] = new Array();
   res[1] = new Array();	
   for(var i=1;i<=n;i++){
	   res[0][i] = (x2-x1)/n;
	   res[1][i] = x1 + (x2-x1)*(i-0.5)/n;
   } 
   return res;
}

// Survival probability for age x until time u in year t:
function tpxFNO(kj, x, u, t, sS, sF) {
   var weight = gauleg(0, u, gaussP);
   var h = weight[0], tau = weight[1]; 
   var sum = 0;
      for(var i=1;i<=gaussP;i++) {
      sum += h[i] * muFNO(kj, x + tau[i], t + tau[i], sS, sF); 
   }
   return Math.exp(-sum);
}

// Present value per unit old age pension for gender kj, age x in year t from age r to s:
function eAP(kj, x, t, r, s, v) {
   var weight = gauleg(0, s - r, gaussP);
   var h = weight[0], tau = weight[1];
   var sum = 0;
   for(var i=1;i<=gaussP;i++) {
      sum += h[i] * Math.pow(v, tau[i]) * tpxFNO(kj, r, tau[i], t+(r-x), 0.12, 0.10);
   }
   return Math.pow(v, r - x) * tpxFNO(kj, x, r-x, t, 0.12, 0.10) * sum;
}

// Cumulative distribution function for Standard Normal Distribution:
function CDFnormal(x) {
	if(x <= -5) return 0;
	else if(x >= 5) return 1;
	else {
		var weight = gauleg(-5, x, gaussP);
		var h = weight[0], tau = weight[1];
		var sum = 0;
		for(var i=1;i<=gaussP;i++) {
			sum += h[i] * Math.exp(-0.5*Math.pow(tau[i], 2))/Math.sqrt(2*Math.PI);
		}
		return sum;
	}
}

// Price of put option with share alpha in stocks, guaranteed interest rate gamma, risk free interest rate delta, volatiliy sigma and maturity t:   
function put(gamma, delta, sigma, alpha, t){
	var S0 = alpha;
	var K = Math.exp(gamma*t) - (1 - alpha)*Math.exp(delta*t);
	var d1 = (Math.log(S0/K) + (delta*t + (Math.pow(sigma, 2)*t/2)))/(sigma*Math.sqrt(t)); 
	var d2 = d1 - sigma*Math.sqrt(t); 
	if(alpha > 1 - Math.exp((gamma - delta)*t) && alpha <= 1 && K > 0)
		return K*Math.exp(-delta*t)*CDFnormal(-d2) - S0*CDFnormal(-d1);
	else return 0;
}

// EIOPA interest rates Norway:
var eiopaInterestRate = [0, 0.01247, 0.01436, 0.01538, 0.01627, 0.01706, 0.01781, 0.01852, 
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
0.0381, 0.03812, 0.03813, 0.03815, 0.03817];

function eiopaInterestRateYearly(t){
	return Math.pow(1+eiopaInterestRate[t+1], t+1)/Math.pow(1+eiopaInterestRate[t], t)-1;
}

function eiopaInterestRateSmooth(t){
	return eiopaInterestRate[Math.floor(t)] + (t-Math.floor(t))*(eiopaInterestRate[Math.ceil(t)]-eiopaInterestRate[Math.floor(t)]);
}

function eiopaInterestRateYearlySmooth(t){
	return eiopaInterestRateYearly(Math.floor(t)) + (t-Math.floor(t))*(eiopaInterestRateYearly(Math.ceil(t))-eiopaInterestRateYearly(Math.floor(t)));
}

function stockWeight(calculationRate, company){
	var w1 = 0.10; // stock weight with the lowest calculation rate 	
	var w2 = 0.02; // stock weight with the highest calculation rate
	var r1 = 0.02; // the lowest calculation rate
	var r2 = 0.04; // the highest calculation rate
	return w1 + (calculationRate-r1)*(w2-w1)/(r2-r1); // Assuming linear reduction in stock weight from lowest to highest calculation rate
}

// DS 22-05-2019: Replacing eAP() with presentValueGuaranteedPension() in this function:
function integrand(calculationRate, stockWeight, male, age, retirementAge, terminationAge,  t){
    return Math.pow(1/(1+eiopaInterestRateSmooth(t)), t) * put(Math.log(1+eiopaInterestRateYearlySmooth(t)), Math.log(1 + eiopaInterestRateYearlySmooth(t)), volatility, stockWeight, 1) *
        tpxFNO(male, age, t, calculationTime, 0.12, 0.10) * presentValueGuaranteedPension(calculationRate, male, age, retirementAge, terminationAge, t);
}

// Assuming age < retirementAge:
function presentValueOption(calculationRate, stockWeight, male, age, retirementAge, terminationAge){
	var weight1 = trapez(0, retirementAge - age, trapezP);
	var weight2 = trapez(retirementAge - age, terminationAge - age, trapezP);
	var h1 = weight1[0], tau1 = weight1[1], h2 = weight2[0], tau2 = weight2[1];
	var sum1 = 0, sum2 = 0;
	for(var i=1;i<=h1.length-1;i++) {
		sum1 += h1[i] * integrand(calculationRate, stockWeight, male, age, retirementAge, terminationAge,  tau1[i]);
	}
	for(var i=1;i<=h2.length-1;i++) {
		sum2 += h2[i] * integrand(calculationRate, stockWeight, male, age, retirementAge, terminationAge,  tau2[i]);
	}
    // DS 22-05-2019: Removing the division by eAP() in the output
    return (sum1+sum2);
}

// DS 22-05-2019: Generalizing this function by adding av variable t to the list of arguments.
function presentValueGuaranteedPension(calculationRate, male, age, retirementAge, terminationAge, t){
	//var weight = gauleg(retirementAge - age, terminationAge - age, gaussP);
    var weight = trapez(Math.max(0, retirementAge - (age+t)), Math.max(0, terminationAge - (age+t)), trapezP);
	var h = weight[0], tau = weight[1];
	var sum = 0;
	for(var i=1;i<=h.length-1;i++) {
        sum += h[i] * Math.pow(1+eiopaInterestRateSmooth(t), t)/Math.pow(1+eiopaInterestRateSmooth(t+tau[i]), t+tau[i]) * tpxFNO(male, age + t, tau[i], calculationTime + t, 0.12, 0.10);
	}
	return sum;
}

function tusenSeparator(tall) {
 return tall.toString().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1 "); 
}

// read user input
function readInput(){
	var male = document.getElementById("M").checked ?  "MALE" : "FEMALE";
	var birthdate = document.getElementById("inputFodselsdato").value;
	var company = document.getElementById("inputSelskap").value;
	var payment = document.getElementById("inputUtbetaling").value;
	var oldagepension = parseFloat(document.getElementById("inputAlderspensjon").value);
	var calculationRate = parseFloat(document.getElementById("inputRente").value)/100;
	var premiumReserve = parseFloat(document.getElementById("inputPremiereserve").value);
	var TA = parseFloat(document.getElementById("inputTA").value);
	var KR = parseFloat(document.getElementById("inputKR").value);
	// format user input and create helper variables (derivatives from user input)
	var retirementage = 67;
	var day = parseFloat(birthdate.substring(0,2)); 
	var month = parseFloat(birthdate.substring(3,5)); 
	var year = parseFloat(birthdate.substring(6,10)); 
	var birthTime = year + (month-1)/12 + (day-1)/360;
	var age = calculationTime - birthTime;
	var stopage;
	if(payment=="Livsvarig") stopage = 120;
	else if(payment=="10 år") stopage = 77;
	else if(payment=="15 år") stopage = 82;
   // build calculation input
   var input = {
	   oldagepension: oldagepension,
       sex: male,
	   age: age,
	   retirementage: retirementage,
       stopage: stopage,
	   calculationRate: calculationRate,
	   premiumReserve: premiumReserve,
	   TA: TA,
	   KR: KR,
	   company: company
   };
   return input;
}

function calculateOutput(input, interestRates, time) {
    eiopaInterestRate = interestRates;
    calculationTime = time;
	var oldagepension = input.oldagepension;
	var male = input.sex;
	var age = input.age;
	var retirementage = input.retirementage;
	var stopage = input.stopage;
	var calculationRate = input.calculationRate;
	var premiumReserve = input.premiumReserve;
	var TA = input.TA;
	var KR = input.KR;
	var company = input.company;
	var premiumReserveAP = Math.round(oldagepension*eAP(male, age, calculationTime, retirementage, stopage, 1/(1+calculationRate)));
	var TAAP = Math.round(TA*premiumReserveAP/premiumReserve);
	var KRAP = Math.round(KR*premiumReserveAP/premiumReserve);	
	var tilbudtverdi = premiumReserveAP + TAAP + KRAP;
	var forsikringsdekninger = premiumReserve + TA + KR - tilbudtverdi;
    // DS 22-05-2019: Adding the last parameter to the new function presentValueGuaranteedPension:
    var garantertverdi = Math.round(oldagepension*presentValueGuaranteedPension(calculationRate, male, age, retirementage, stopage, 0));
    // DS 22-05-2019: Changed output from presentValueOption() needs to be multiplied by oldagepension instead of premiumReserveAP:
    var opsjonsverdi = Math.round(oldagepension*presentValueOption(calculationRate, stockWeight(calculationRate, company), male, age, retirementage, stopage));
	var markedsverdi = garantertverdi + opsjonsverdi;
	var garantiverdi = markedsverdi - tilbudtverdi;
	var garantiverdiUtenOpsjon = garantertverdi - tilbudtverdi;

	var output  = {
	   garantertverdi: garantertverdi,
	   opsjonsverdi: opsjonsverdi,
	   markedsverdi: markedsverdi,
	   tilbudtverdi: tilbudtverdi,
	   premiumReserveAP: premiumReserveAP,
	   TAAP: TAAP,
	   KRAP: KRAP,
	   garantiverdi: garantiverdi,
	   garantiverdiUtenOpsjon: garantiverdiUtenOpsjon,
	   forsikringsdekninger: forsikringsdekninger,
	};

	return output;
}

function displayOutput(output){
   	document.getElementById("garantertverdi").innerHTML = tusenSeparator(output.garantertverdi);
	document.getElementById("opsjonsverdi").innerHTML = tusenSeparator(output.opsjonsverdi);
	document.getElementById("markedsverdi").innerHTML = tusenSeparator(output.markedsverdi);
	document.getElementById("tilbudtverdi").innerHTML = tusenSeparator(output.tilbudtverdi);	
	document.getElementById("premiereserveAP").innerHTML = tusenSeparator(output.premiumReserveAP);
	document.getElementById("TAAP").innerHTML = tusenSeparator(output.TAAP);
	document.getElementById("KRAP").innerHTML = tusenSeparator(output.KRAP);
	document.getElementById("garantiverdi").innerHTML = tusenSeparator(output.garantiverdi);	
	document.getElementById("garantiverdiUtenOpsjon").innerHTML = tusenSeparator(output.garantiverdiUtenOpsjon);
	document.getElementById("opsjonsverdi2").innerHTML = tusenSeparator(output.opsjonsverdi);
	document.getElementById("forsikringsdekninger").innerHTML = tusenSeparator(output.forsikringsdekninger);
	
}

function calculate_enter(e) {
 if (e.keyCode == 13) calculate();
}

function calculate(){
	displayOutput(calculateOutput(readInput()));
}
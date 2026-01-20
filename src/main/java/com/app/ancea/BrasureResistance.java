package com.app.ancea;

import java.util.HashMap;

import javafx.scene.image.ImageView;

public class BrasureResistance extends BrasureComposantLeadless
		implements InterfaceBrasureComposant {

	public static ImageView image = new ImageView(
			BrasureResistance.class.getResource("images/RESISTANCE_BRASURE.png")
					.toExternalForm());

	private final static double vernis_eparnge = 0.02;

	public BrasureResistance() {
		super();
	}

	public BrasureResistance(String path) {
		super(path);
		composant = Resistance.getResistance(format);

		infoComposant = "as: " + getAs() + " bs: " + getBs() + " es: " + getEs()
				+ "\n a1: " + getA1() + " b1: " + getB1() + " c1: " + getC1();
	}

	public HashMap<String, Double> getResistance() {
		return composant;
	}

	protected void setFormat(HashMap<String, Double> composant) {
		this.composant = composant;
	}

	public void setFormat(String format) {
		this.format = format;
		composant = Resistance.getResistance(format);
	}

	protected double getH1() {
		System.out.println("[RESISTANCE] H1 Valeur: " + composant.get("h1"));
		return composant.get("h1");
	}

	protected double getVa() { /* OK */
		double calcul = 0;

		calcul = getAs() * getBs() * (getEs() + vernis_eparnge) / 2;

		System.out.println("[RESISTANCE][Va] Va = " + calcul);

		return calcul;
	}

	@Override
	protected double getCoefficientDePoisson() {
		return Ceramique.coefficient_poisson;
	}

	protected double getV1() {
		double V1 = 0;

		System.out.println("[RESISTANCE][V1] --- HxH1 Calculation Start ---");
		System.out.println("[RESISTANCE][V1] Formula:");
		System.out.println("[RESISTANCE][V1] V1 = a * b * h1 + (a1 - a) * h1 * b");

		V1 = composant.get("a") * composant.get("b") * getH1()
				+ (getA1() - composant.get("a")) * getH1() * composant.get("b");
		System.out.println("[RESISTANCE][V1] V1 = " + V1);
		System.out.println("[RESISTANCE][V1] --- V1 Calculation End ---");


		return V1;
	}

	protected double getV2() {
		double calcul = 0;

		calcul = getVa() - getV1();

		System.out.println("[RESISTANCE][V2] V2 = " + calcul);

		return calcul;
	}
//
//	protected double getHxH1() {
//		double calcul = 0;
//
//		if (Math.sqrt(2 * getV2() / composant.get("a")) < composant.get("h")) {
//			calcul = Math.sqrt(2 * getV2() / composant.get("a"));
//		} else {
//			calcul = composant.get("h") + getH1();
//		}
//
//		System.out.println("[Debug] H1 value: " + calcul);
//		System.out.println("[Debug] V2 value: " + getV2());
//
//		return calcul;
//	}

	protected double getHxH1() {
		double HxH1 = 0.0;

		double v2 = getV2();

		System.out.println("[RESISTANCE][HxH1] --- HxH1 Calculation Start ---");
		System.out.println("[RESISTANCE][HxH1] Formula:");
		double sqrtTerm = Math.sqrt(2 * v2 / b1);
//		if (sqrtTerm < composant.get("h")) {
			HxH1 = sqrtTerm;
			System.out.println("[RESISTANCE][HxH1]   HxH1 = sqrt((2 × v2) / b1)");
			System.out.println("[RESISTANCE][HxH1]   v2 = " + v2 + ", b1 = " + b1);
//		} else {
//			HxH1 = composant.get("h") + getH1();
//			System.out.println("[RESISTANCE][HxH1]   HxH1 = h / h1)");
//			System.out.println("[RESISTANCE][HxH1]   h = " + composant.get("h") + ", h1 = " + getH1());
//		}

		System.out.println("[RESISTANCE][HxH1] HxH1 = " + HxH1);
		System.out.println("[RESISTANCE][HxH1] --- B2 Calculation End ---");

		return HxH1;
	}

	protected double getHx() {
		System.out.println("[RESISTANCE][Hx]=" + (getHxH1() - getH1()));
		return getHxH1() - getH1();
	}

	protected double getC() {
		double calcul = 0;

		calcul = 2 * getV2() / (composant.get("a") * getHxH1());

		return calcul;
	}

	protected double getV45() {
		double calcul = 0;

		calcul = Math.pow((getH1() + getHxH1()), 2) / 2 * composant.get("b");

		System.out.println("[RESISTANCE][V45] V45 = " + calcul);

		return calcul;
	}

	protected double getV2V45() {
		double calcul = 0;

		calcul = getV2() - getV45();

		System.out.println("[RESISTANCE][V2V45] V2V45 = " + calcul);

		return calcul;
	}

//	protected double getTheta() {
//		double calcul = 0;
//		if (composant.get("h") == getHx()) {
//			calcul = Math.atan(getC() / getHxH1());
//		} else {
//			calcul = Math.PI / 4;
//		}
//		return calcul;
//	}

	protected double getTheta() {
		double theta = 0;
//		if (composant.get("h") == getHx()) {
		if (getV2V45() > 0){
			theta = Math.atan(getC() / (getHxH1() + getH1()));
		} else {
			theta = Math.PI / 4;
		}
		System.out.println("[RESISTANCE][Theta] Theta = " + theta);
		return theta;
	}

//	protected double getB2() {
//		double calcul = 0;
//
//		calcul = getHx() * Math.sin(getTheta());
//
//		return calcul;
//	}

	protected double getB2() {
		System.out.println("[RESISTANCE][B2] --- B2 Calculation Start ---");

		double hxh1 = getHxH1();  // Combined height (NOT just Hx!)
		double theta = getTheta();

		System.out.println("[RESISTANCE][B2] Input Components:");
		System.out.println("[RESISTANCE][B2]   - HxH1 (combined height) = " + hxh1);
		System.out.println("[RESISTANCE][B2]   - θ (theta) = " + theta + " radians (" + Math.toDegrees(theta) + "°)");

		// CORRECTED: Use HxH1, not Hx!
		double b2 = hxh1 * Math.sin(theta);

		System.out.println("[RESISTANCE][B2] Formula:");
		System.out.println("[RESISTANCE][B2]   B2 = HxH1 × sin(θ)");
		System.out.println("[RESISTANCE][B2]   B2 = (Hx + H1) × sin(θ)");
		System.out.println("[RESISTANCE][B2]   B2 = " + hxh1 + " × sin(" + theta + ")");
		System.out.println("[RESISTANCE][B2]   B2 = " + b2);
		System.out.println("[RESISTANCE][B2] --- B2 Calculation End ---");

		return b2;
	}

//	protected double getH2() {
//		double calcul = 0;
//		calcul = (getHx() + 2 * getH1()) * Math.tan(getTheta())
//				* Math.sqrt(Math.pow(getC(), 2) + Math.pow(getHxH1(), 2))
//				/ (2 * getC());
//		return calcul;
//	}

	protected double getH2() {
		System.out.println("[RESISTANCE][H2] --- H2 Calculation Start ---");
		double H2 = 0;

		// Input Components:
		double theta = getTheta();
		double C = getC();
		double H1 = getH1();
		System.out.println("[RESISTANCE][H2] Input Components:");
		System.out.println("[RESISTANCE][H2]   - Hx = " + getHx());
		System.out.println("[RESISTANCE][H2]   - H1 = " + getH1());
		System.out.println("[RESISTANCE][H2]   - θ (theta) = " + theta + " radians (" + Math.toDegrees(theta) + "°)");
		System.out.println("[RESISTANCE][H2]   - C = " + getC());
		System.out.println("[RESISTANCE][H2]   - HxH1 = " + getHxH1());
		H2 = (getHx() + 2 * H1) * Math.tan(theta)
				* Math.sqrt(Math.pow(C, 2) + Math.pow(getHxH1(), 2))
				/ (2 * C);
		System.out.println("[RESISTANCE][H2]   - H2 = " + H2);
		System.out.println("[RESISTANCE][H2] --- H2 Calculation Start ---");
		return H2;
	}

//	@Override
//	public double getHeq() {
//		System.out.println("[Debug] Calculer de Heq: " + composant.get("h"));
//		double calcul = 0;
//
//		calcul = getH1() * getH2() * (composant.get("b") + getB2())
//				/ (composant.get("b") * getH2() + getB2() * getH1());
//
//		return calcul;
//	}

	@Override
	public double getHeq() {

		System.out.println("====================================================================");
		System.out.println("[RESISTANCE][Heq] ========== Heq CALCULATION START ==========");
		System.out.println("====================================================================");

		// Get all input parameters
		double h1 = getH1();
		double h2 = getH2();
		double b = composant.get("b");
		double b2 = getB2();

		// Log all individual components
		System.out.println("[RESISTANCE][Heq] Input Parameters:");
		System.out.println("[RESISTANCE][Heq]   - H1 = " + h1 + " mm");
		System.out.println("[RESISTANCE][Heq]   - H2 = " + h2 + " mm");
		System.out.println("[RESISTANCE][Heq]   - b (pad width) = " + b + " mm");
		System.out.println("[RESISTANCE][Heq]   - B2 = " + b2 + " mm");

		// Log intermediate calculations
		double numerator = h1 * h2 * (b + b2);
		double denominator = (b * h2) + (b2 * h1);

		System.out.println("[RESISTANCE][Heq] Intermediate Calculations:");
		System.out.println("[RESISTANCE][Heq]   - (b + B2) = " + (b + b2) + " mm");
		System.out.println("[RESISTANCE][Heq]   - Numerator = H1 × H2 × (b + B2)");
		System.out.println("[RESISTANCE][Heq]                = " + h1 + " × " + h2 + " × " + (b + b2));
		System.out.println("[RESISTANCE][Heq]                = " + numerator + " mm³");
		System.out.println("[RESISTANCE][Heq]   - Denominator = (b × H2) + (B2 × H1)");
		System.out.println("[RESISTANCE][Heq]                 = (" + b + " × " + h2 + ") + (" + b2 + " × " + h1 + ")");
		System.out.println("[RESISTANCE][Heq]                 = " + (b * h2) + " + " + (b2 * h1));
		System.out.println("[RESISTANCE][Heq]                 = " + denominator + " mm²");

		// Calculate Heq
		double heq = numerator / denominator;

		// Log the formula
		System.out.println("[RESISTANCE][Heq] Formula:");
		System.out.println("[RESISTANCE][Heq]   Heq = (H1 × H2 × (b + B2)) / ((b × H2) + (B2 × H1))");
		System.out.println("[RESISTANCE][Heq]   Heq = " + numerator + " / " + denominator);
		System.out.println("[RESISTANCE][Heq]   Heq = " + heq + " mm");

		System.out.println("====================================================================");
		System.out.println("[RESISTANCE][Heq] ========== Heq CALCULATION END ==========");
		System.out.println("====================================================================");

		return heq;
	}

	protected double getD1() {
		double calcul = 0;

		calcul = ((getC1() + 2 * getB1()) - composant.get("d")) / 2;

		return calcul;
	}

	@Override
	protected void calcul_heq() {
		heq.set_resultat(
				String.valueOf(Outils.nombreChiffreApresVirgule(getHeq(), 3)));
	}

//	public double getSc() {
//		double calcul = 0;
//
//		calcul = (composant.get("b") + getB2()) * composant.get("a") / 2;
//
//		return calcul;
//
//	}

	public double getSc() {

		System.out.println("[DureeDeVie][RESISTANCE] Calcul Sc");
		double Sc = 0;
		Sc = (composant.get("b") + getB2()) * composant.get("a") / 2;
		System.out.println("[DureeDeVie][RESISTANCE] Calcul Sc = " + Sc);
		return Sc;
	}

	@Override
	protected void calcul_sc() {
		Sc.set_resultat(
				String.valueOf(Outils.nombreChiffreApresVirgule(getSc(), 3)));
	}

//	@Override
//	public double getHi() {
//
//		double calcul;
//
//		if (composant.get("a") == getA1()) {
//
//			calcul = this.getHeq();
//
//		} else if (composant.get("a") < getA1()) {
//
//			calcul = (composant.get("a") * getHeq()
//					/ (composant.get("a") - getA1()))
//					* Math.log(composant.get("a") / getA1());
//
//		} else {
//			calcul = getA1();
//		}
//		/*
//		 * // Afin d'eviter une division par 0 if (composant.get("a") ==
//		 * getA1()) { calcul = getH1(); } else {
//		 * 
//		 * calcul = getSc() * getHeq() / (getHxH1() * (composant.get("a") -
//		 * getA1())) Math.log(composant.get("a") / getA1()); }
//		 */
//		System.out.println("[Debugger] Hi valuer: " + calcul);
//		return calcul;
//	}


	@Override
	public double getHi() {
		System.out.println("====================================================================");
		System.out.println("[RESISTANCE][Hi] ========== Hi CALCULATION START ==========");
		System.out.println("====================================================================");

		// Cache repeated lookups
		double a = composant.get("a");
		double a1 = getA1();

		// Tolerance for floating-point comparison (1 micrometer)
		final double EPSILON = 1e-6;

		double difference = a - a1;

		// Log input parameters
		System.out.println("[RESISTANCE][Hi] Input Parameters:");
		System.out.println("[RESISTANCE][Hi]   - a (component width) = " + a + " mm");
		System.out.println("[RESISTANCE][Hi]   - a1 (extended pad dimension) = " + a1 + " mm");
		System.out.println("[RESISTANCE][Hi]   - (a - a1) = " + difference + " mm");
		System.out.println("[RESISTANCE][Hi]   - EPSILON (tolerance) = " + EPSILON + " mm");

		// Case 1: Component width equals pad width (within tolerance)
		if (Math.abs(difference) < EPSILON) {
			double heq = getHeq();
			System.out.println("[RESISTANCE][Hi] Case 1: Component width ≈ pad width");
			System.out.println("[RESISTANCE][Hi]   |a - a1| = " + Math.abs(difference) + " < " + EPSILON);
			System.out.println("[RESISTANCE][Hi]   Hi = Heq = " + heq + " mm");
			System.out.println("====================================================================");
			System.out.println("[RESISTANCE][Hi] ========== Hi CALCULATION END ==========");
			System.out.println("====================================================================");
			return heq;
		}

		// Case 2: Pad extends beyond component (typical case)
		// Excel formula (C69): =C68*C67/(C57*(C31-C38))*LN(C31/C38)
		// Hi = (Sc × Heq) / ((hx+h1) × (a - a1)) × ln(a / a1)
		if (a < a1) {
			System.out.println("[RESISTANCE][Hi] Case 2: Pad extends beyond component (a < a1)");

			double sc = getSc();
			double heq = getHeq();
			double hxh1 = getHxH1();

			System.out.println("[RESISTANCE][Hi] Retrieved Parameters:");
			System.out.println("[RESISTANCE][Hi]   - Sc (contact surface) = " + sc + " mm²");
			System.out.println("[RESISTANCE][Hi]   - Heq (equivalent height) = " + heq + " mm");
			System.out.println("[RESISTANCE][Hi]   - (hx+h1) (combined height) = " + hxh1 + " mm");

			// Calculate intermediate values
			double numeratorPart = sc * heq;
			double denominatorPart = hxh1 * difference;
			double logTerm = Math.log(a / a1);

			System.out.println("[RESISTANCE][Hi] Intermediate Calculations:");
			System.out.println("[RESISTANCE][Hi]   - Numerator part = Sc × Heq");
			System.out.println("[RESISTANCE][Hi]                    = " + sc + " × " + heq);
			System.out.println("[RESISTANCE][Hi]                    = " + numeratorPart + " mm³");
			System.out.println("[RESISTANCE][Hi]   - Denominator part = (hx+h1) × (a - a1)");
			System.out.println("[RESISTANCE][Hi]                      = " + hxh1 + " × " + difference);
			System.out.println("[RESISTANCE][Hi]                      = " + denominatorPart + " mm²");
			System.out.println("[RESISTANCE][Hi]   - ln(a/a1) = ln(" + a + "/" + a1 + ")");
			System.out.println("[RESISTANCE][Hi]              = ln(" + (a/a1) + ")");
			System.out.println("[RESISTANCE][Hi]              = " + logTerm);

			// Calculate Hi
			double hi = (numeratorPart / denominatorPart) * logTerm;

			System.out.println("[RESISTANCE][Hi] Formula:");
			System.out.println("[RESISTANCE][Hi]   Hi = (Sc × Heq) / ((hx+h1) × (a - a1)) × ln(a/a1)");
			System.out.println("[RESISTANCE][Hi]   Hi = (" + numeratorPart + " / " + denominatorPart + ") × " + logTerm);
			System.out.println("[RESISTANCE][Hi]   Hi = " + (numeratorPart / denominatorPart) + " × " + logTerm);
			System.out.println("[RESISTANCE][Hi]   Hi = " + hi + " mm");

			System.out.println("====================================================================");
			System.out.println("[RESISTANCE][Hi] ========== Hi CALCULATION END ==========");
			System.out.println("====================================================================");

			return hi;
		}

		// Case 3: Component wider than pad (abnormal - fallback)
		System.out.println("[RESISTANCE][Hi] Case 3: Component wider than pad (a > a1) - ABNORMAL");
		System.out.println("[RESISTANCE][Hi]   This is an unusual case - possible data error");
		System.out.println("[RESISTANCE][Hi]   Hi = a1 = " + a1 + " mm");
		System.out.println("====================================================================");
		System.out.println("[RESISTANCE][Hi] ========== Hi CALCULATION END ==========");
		System.out.println("====================================================================");

		return a1;
	}

	@Override
	protected void calcul_Hi() {
		Hi.set_resultat(
				String.valueOf(Outils.nombreChiffreApresVirgule(getHi(), 3)));
	}

	@Override
	protected void setCalculs() {
		calcul_heq();
		calcul_sc();
		calcul_Hi();
	}

}

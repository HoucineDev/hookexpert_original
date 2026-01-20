package com.app.ancea;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.SimpleDoubleProperty;

/**
 * Classe permettant de calculer la duree de vie d'un composant sur un pcb.
 * Toute les formules sont issus des feuilles excel.
 * 
 * @author FX603984
 *
 * @param <C> composant
 * @param <B> brasure
 */
public strictfp class DDV<C extends Composant, B extends BrasureComposant> {

	private StringBuilder stringLogger;
	private String td;

	private Resultat_temperature_pcb resultat_pcb1;
	private C composant;
	private B brasure;

	private Pcb pcb;

	private boolean matrice_creuse;

	private double temperature, duree, x1t1, x1t2;

	private OngletPcb onglet;

	private final static double K = Math.sqrt(2);
	private static final double SQRT_2 = Math.sqrt(2.0);

	private static final Logger logger = Logger.getLogger(Main.class.getName());
	/**
	 * Poisson ratios for deformation calculations
	 */
	private static final double POISSON_PCB_RESISTOR = 0.2;      // νpcb for resistors
	private static final double POISSON_PCB_CAPACITOR = 0.3;     // νpcb for capacitors

	private static final double POISSON_COMPONENT_RESISTOR = 0.3;
	private static final double POISSON_COMPONENT_CAPACITOR = 0.25;


	public DDV(
			double temperature,
			double duree,
			String PCB,
			C composant,
			B brasure) {

		this.temperature = temperature;
		this.duree = duree;

		this.composant = composant;
		this.brasure = brasure;

		this.matrice_creuse = false;

		System.out.println("[DDV] Constructor:");
		System.out.println("[DDV]   - Temperature: " + this.temperature);
		System.out.println("[DDV]   - Duration: " + this.duree);
		System.out.println("[DDV]   - PCB name: " + PCB);

		this.pcb = new Pcb();
		this.onglet = new OngletPcb();
		this.onglet.load_circuit(pcb, PCB);
		this.resultat_pcb1 = new Resultat_temperature_pcb(
				onglet,
				this.temperature);

		this.x1t1 = 0;
		this.x1t2 = 0;

		stringLogger = new StringBuilder();
		td = "[Temperature: " + temperature + " durée: " + duree + "]";
		stringLogger.append(
				"--- Temperature: " + temperature + " durée: " + duree
						+ " ---");
		stringLogger.append("CTE PCB: " + resultat_pcb1.getCteXY());
	}

	/**
	 * Gets the appropriate Poisson ratio for the PCB based on component type.
	 *
	 * @return Poisson ratio value
	 */
	private double getPoissonPcb() {
		// Check if component is a capacitor type
		if (composant instanceof Composant_Capacite) {  // adjust class names as needed
			return POISSON_PCB_CAPACITOR;
		}
		// Default to resistor (or other component types)
		return POISSON_PCB_RESISTOR;
	}

	public double getX1t1() {
		return x1t1;
	}

	public void setX1t1(double x1t1) {
		this.x1t1 = x1t1;
	}

	public double getX1t2() {
		return x1t2;
	}

	public void setX1t2(double x1t2) {
		this.x1t2 = x1t2;
	}

	public boolean isMatrice_creuse() {
		return matrice_creuse;
	}

	public void setMatrice_creuse(boolean matrice_creuse) {
		this.matrice_creuse = matrice_creuse;
	}

	public SimpleDoubleProperty getTemperature() {
		return new SimpleDoubleProperty(temperature);
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
		resultat_pcb1.setTemperature(temperature);
		resultat_pcb1.setCalculs();
	}

	public void setDuree(double duree) {
		this.duree = duree;
	}

	public double getDuree() {
		return duree;
	}

	// ΔT = temperature - 20
	private double deltaT() {
		double deltaT = temperature - 20;
		System.out.println("[DDV] deltaT: " + deltaT);
		return deltaT;
	}

	// Rgm(T,t) = (K'3·T² + K'4·T + K'5) × t^(K'6·e^(K'7·T))
	private double getRgm() {

		// Polynomial part: A*T² + B*T + C
		double polynomial = Ressources.SAC305_K3_CISAILLEMENT * temperature * temperature
				+ Ressources.SAC305_K4_CISAILLEMENT * temperature
				+ Ressources.SAC305_K5_CISAILLEMENT;

		// Exponent part: D * exp(E * T)
		double exponent = Ressources.SAC305_K6_CISAILLEMENT * Math.exp(Ressources.SAC305_K7_CISAILLEMENT * temperature);

		// Power term: duration^exponent
		double powerTerm = Math.pow((duree), exponent);

		// Final rgm
		double rgm = polynomial * powerTerm;

		System.out.println("[LIFESPAN] Rgm(t): " + rgm  + " {" + td + "}");
		System.out.println("[LIFESPAN] calculateRmg: T=" + temperature + "°C, duration=" + duree
				+ "min, polynomial=" + polynomial + ", exponent=" + exponent
				+ ", powerTerm=" + powerTerm + ", rgm=" + rgm);
		return rgm;
	}

	// Δxmax = (αxPCB - αxcomp) × ΔT × Lnpx
	private double getDeltaLMaxX() {
		double pcbCteX = resultat_pcb1.getCteX();
		double calcul;

		if (composant instanceof ComposantQFN) {
			// QFN has temperature-dependent CTE via getCte(temperature)
			ComposantQFN qfn = (ComposantQFN) composant;
			calcul = deltaT() * (pcbCteX - qfn.getCte(temperature)) * qfn.getX();

			System.out.println("[LIFESPAN] getDeltaLMaxX: T=" + temperature + "°C, duration=" + duree
					+ "min, ∆T=" + deltaT() + ", CTE(x,pcb)=" + resultat_pcb1.getCteX()
					+ ", CTE=" + qfn.getCte(temperature) + ", Lnp_x=" + qfn.getX() + ", ∆LMax(X)=" + calcul);
		} else {
			// Other components use simple getCte()
			calcul = deltaT() * (pcbCteX - composant.getCte(temperature)) * composant.getX();

			System.out.println("[LIFESPAN] getDeltaLMaxX: T=" + temperature + "°C, duration=" + duree
					+ "min, ∆T=" + deltaT() + ", CTE(x,pcb)=" + pcbCteX
					+ ", CTE=" + composant.getCte(temperature) + ", Lnp_x=" + composant.getX() + ", ∆LMax(X)=" + calcul);
		}

		System.out.println(td + "  ∆Lmax(X)=" + calcul);
		return calcul;
	}

	//Δymax = (αyPCB - αycomp) × ΔT × Lnpy
	private double getDeltaLMaxY() {
		double pcbCteY = resultat_pcb1.getCteY();
		double calcul;

		if (composant instanceof ComposantQFN) {
			ComposantQFN qfn = (ComposantQFN) composant;
			calcul = deltaT() * (pcbCteY - qfn.getCte(temperature)) * qfn.getY();
		} else {
			calcul = deltaT() * (pcbCteY - composant.getCte(temperature)) * composant.getY();
			System.out.println("[LIFESPAN] getDeltaLMaxY: T=" + temperature + "°C, duration=" + duree
					+ "min, ∆T=" + deltaT() + ", CTE(x,pcb)=" + pcbCteY
					+ ", CTE=" + composant.getCte(temperature) + ", Lnp_x=" + composant.getX() + ", ∆LMax(Y)=" + calcul);
		}

		System.out.println(td + "delta max Y: " + calcul);
		return calcul;
	}

	// Lnpxy = √(Lnpx² + Lnpy²)
	private double getDeltaMaxXY() {
		double calcul = 0;

		if (deltaT() >= 0) {
			calcul = Math.sqrt(
					Math.pow(getDeltaLMaxX(), 2) + Math.pow(getDeltaLMaxY(), 2));
		} else {
			calcul = -Math.sqrt(
					Math.pow(getDeltaLMaxX(), 2) + Math.pow(getDeltaLMaxY(), 2));
		}
		logger.info(td + "Delta L Max(X,Y): " + calcul);
		System.out.println(td + ", Delta L Max(X,Y): " + calcul);
		return calcul;
	}

	// Fxy = Rmg × Sc
	private double getFxy() {
		double rmg = getRgm();
		double sc = brasure.getSc();  // Contact surface from BrasureResistance

		double fxy = rmg * sc;
		System.out.println("[LIFESPAN] calculateFxy: Rmg=" + rmg + ", Sc=" + sc + ", Fxy=" + fxy + ", " + td);

		return fxy;
	}

	// Sxpcb = epcb × (a/2) × √2
	private double getSxpcb() {
		double epcb = resultat_pcb1.getEpaisseurPcb();
		double a = composant.getComposant().get("a");

		double sxpcb = epcb * (a / 2.0) * SQRT_2;

		System.out.println("[LIFESPAN] calculate Sxpcb: ΔT=" + deltaT()
				+ "°C,  epcb = " + epcb
				+ ", a = " + a
				+ " Sxcpb = " + sxpcb
		);
		return sxpcb;
	}

	// Sypcb = epcb × b × √2
	private double getSypcb() {
		double epcb = resultat_pcb1.getEpaisseurPcb();
		double b = composant.getComposant().get("b");// PCB thickness
		System.out.println("calculate Sypcb: ΔT=" + deltaT()
				+ "°C, epcb = "
				+ epcb + ", b = "
				+ b + " Sycpb = " + (epcb * b * SQRT_2));
		return epcb * b * SQRT_2;
	}

	// R = Lnpx / Lnpy
	private double getR() {
		double lnpx = composant.getX();  // Distance to neutral point in X
		double lnpy = composant.getY();  // Distance to neutral point in Y

		if (lnpy == 0) {
			logger.severe(td + " ERROR: Lnpy is zero, cannot calculate R ratio!");
			return 1.0;  // Fallback
		}

		double r = lnpx / lnpy;

		System.out.println("[LIFESPAN] calculateR: Lnpx=" + lnpx + ", Lnpy=" + lnpy + ", R=" + r + ", " + td);

		return r;
	}

	// Fx = Fxy / √(1 + 1/ R²)
	private double getFx() {
		double r = getR();
		double fxy = getFxy();
		double fx = fxy / Math.sqrt(1.0 + 1.0 / (r * r));

		System.out.println("[LIFESPAN] calculateFx: Fxy=" + fxy + ", R=" + r + " => Fx=" + fx + ", " + td);

		return fx;
	}

	// Fy = Fxy / √(1 + R²)
	private double getFy() {
		double fxy = getFxy();
		double r = getR();
		double fy = fxy / Math.sqrt(1.0 + r * r);

		System.out.println("[LIFESPAN] calculateFy: Fxy=" + fxy + ", R=" + r + " =>  Fy=" + fy + ", " + td);

		return fy;
	}

	// Kxcomp = (Sxcomp × Excomp) / Lnpx
	private double getKxcomp() {
		double sxcomp = getSxcomp();
		double excomp = composant.getE();
		double lnpx = composant.getX();

		System.out.println("calculate Kxcomp: Sxcomp = " + sxcomp +
				", excomp = " + excomp +
				" lnpx = " + lnpx +
				" KxComp = " + (sxcomp * excomp / lnpx));
		return sxcomp * excomp / lnpx;
	}

	// Kycomp = (Sycomp × Eycomp) / Lnpy
	private double getKycomp() {

		double lnpy = composant.getY();
		double sycomp = getSycomp();
		double eycomp = composant.getE();

		System.out.println("calculate Kycomp: Sycomp = " + sycomp +
				", eycomp = " + eycomp +
				" lnpy = " + lnpy +
				" Kycomp = " + (sycomp * eycomp / lnpy));
		return sycomp * eycomp / lnpy;
	}

	// Sxcomp = ecomp × (a/ 2)
	private double getSxcomp() {
		double ecomp = composant.getComposant().get("h");
		double a = composant.getComposant().get("a");

		System.out.println("calculate Sxcomp: ecomp = " + ecomp + ", a = " + a + " Sxcomp = " + (ecomp * (a / 2.0)));
		return ecomp * (a / 2.0);
	}

	// Sycomp = ecomp × b × √2
	private double getSycomp() {

		double ecomp = composant.getComposant().get("h");
		double b = composant.getComposant().get("b");

		System.out.println("calculate Sycomp: ecomp = " + ecomp + ", b = " + b + " Sycomp = " + (ecomp * b * SQRT_2));
		return ecomp * b * SQRT_2;
	}

	private double getKxpcb() {
		double lnpx = composant.getX();
		double sxpcb = getSxpcb();
		double expcb = resultat_pcb1.get_double_module_traction_x();

		double kxpcb = sxpcb * expcb / lnpx;
		System.out.println("[LIFESPAN] calculate Kxpcb: Sxpcb = " + sxpcb +
				", expcb = " + expcb +
				" lnpx = " + lnpx +
				" ΔT=" + deltaT() + "°C," +
				" Kxpcb = " + kxpcb);
		return kxpcb;
	}

	private double getKypcb() {

		double lnpy = composant.getY();
		double sypcb = getSypcb();
		double eypcb = resultat_pcb1.get_double_module_traction_y();

		double kypcb = sypcb * eypcb / lnpy;
		System.out.println("[LIFESPAN] calculate Kypcb: Sypcb = " + sypcb +
				", eypcb = " + eypcb +
				" lnpy = " + lnpy +
				" ΔT=" + deltaT() + "°C," +
				" Kypcb = " + kypcb);
		return kypcb;
	}

	// Δxpcb
	private double getDXCiu() {
		double calcul = 0;

		double Fxy = getFxy();

		if (composant instanceof ComposantQFN) {
			calcul = Fxy
					/ (composant.getComposant().get("a")
					* resultat_pcb1.getEpaisseurPcb() * Math.sqrt(2)
					* K)
					* (((ComposantQFN) composant).getXX()
					/ resultat_pcb1.get_double_module_traction_x()
					- Ressources.PCB_v
					* ((ComposantQFN) composant).getXY()
					/ resultat_pcb1
					.get_double_module_traction_x())
					+ getRgm() * ((ComposantQFN) composant).getSp()
					/ (((ComposantQFN) composant).getXa()
					* resultat_pcb1.getEpaisseurPcb()
					* Math.sqrt(2) * K)
					* (((ComposantQFN) composant).getLa()
					/ (resultat_pcb1
					.get_double_module_traction_x()
					- Ressources.PCB_v
					* ((ComposantQFN) composant)
					.getLb()
					/ resultat_pcb1
					.get_double_module_traction_x()));
		}
		else if (composant instanceof ComposantLCC) {

			calcul = Fxy
					/ (composant.getComposant().get("b")
					* resultat_pcb1.getEpaisseurPcb() * Math.sqrt(2)
					* K)
					* (((ComposantLCC) composant)
					.calculNombreX(composant, false, "nombreXX")
					/ resultat_pcb1.get_double_module_traction_x()
					- Ressources.PCB_v * ((ComposantLCC) composant)
					.calculNombreX(composant, false, "nombreXY")
					/ resultat_pcb1
					.get_double_module_traction_y());

			/*
			 * calcul = getRgm() * brasure.getSc() /
			 * (composant.getComposant().get("b")
			 * resultat_pcb1.getEpaisseurPcb() * Math.sqrt(2) K)
			 * (((ComposantLCC) composant).getXX() /
			 * resultat_pcb1.get_double_module_traction_x() - Ressources.PCB_v
			 * ((ComposantLCC) composant).getXY() / resultat_pcb1
			 * .get_double_module_traction_y());
			 */
			logger.info(
					td + "epaisseur PCB: " + resultat_pcb1.getEpaisseurPcb()
							+ " XX: " + ((ComposantLCC) composant).getXX()
							+ "E x: "
							+ resultat_pcb1.get_double_module_traction_x()
							+ " E y: "
							+ resultat_pcb1.get_double_module_traction_y());

		}
		else {
			calcul = Fxy
					/ (1.5 * Math.sqrt(2)
					* resultat_pcb1.get_double_module_traction_x()
					* resultat_pcb1.getEpaisseurPcb())
					* ((composant.getComposant().get("d")
					- composant.getComposant().get("b"))
					/ composant.getComposant().get("a")
					- Ressources.PCB_v
					* composant.getComposant().get("a")
					/ (4 * K * composant.getComposant()
					.get("b")));
		}
		logger.info(
				"1er terme: " + (getRgm() * brasure.getSc()
						/ (composant.getComposant().get("b")
						* resultat_pcb1.getEpaisseurPcb() * Math.sqrt(2)
						* K)));
		logger.info(td + "DX(ciu): " + calcul);

		System.out.println("[DXCiu] DX(ciu) = " + calcul + " {Fxy = " + Fxy
				+ ", Epaisseur PCB = " + resultat_pcb1.getEpaisseurPcb()
				+ "PCB_v" + Ressources.PCB_v
				+ "E(x) Traction = "+ resultat_pcb1.get_double_module_traction_x() +"}");
		return calcul;
	}


	private double getDeltaXpcb() {

		double deltaXpcb = 0.0;
		double Fxy = getFxy();
		double a = composant.getComposant().get("a");
		double b = composant.getComposant().get("b");
		double epcb = resultat_pcb1.getEpaisseurPcb();
		double expcb = resultat_pcb1.get_double_module_traction_x();
		double eypcb = resultat_pcb1.get_double_module_traction_y();
		double PCB_v = Ressources.PCB_v;

		if (composant instanceof ComposantQFN) {
			ComposantQFN qfn = (ComposantQFN) composant;
			deltaXpcb = Fxy / (a * epcb * SQRT_2 * K)
					* (qfn.getXX() / expcb - PCB_v * qfn.getXY() / expcb)
					+ getRgm() * qfn.getSp()
					/ (qfn.getXa() * epcb * SQRT_2 * K)
					* (qfn.getLa() / (expcb - PCB_v
					* qfn.getLb() / expcb));
		}
		else if (composant instanceof ComposantLCC) {
			ComposantLCC lcc = (ComposantLCC) composant;
			deltaXpcb = Fxy
					/ (b * epcb * SQRT_2 * K)
					* (lcc.calculNombreX(composant, false, "nombreXX")
					/ expcb - PCB_v * lcc.calculNombreX(composant, false, "nombreXY")
					/ eypcb);

			logger.info(
					td + "epaisseur PCB: " + epcb
							+ " XX: " + lcc.getXX()
							+ "E x: " + expcb
							+ " E y: " + eypcb);

		}
		else {
			double fx = getFx();
			double fy = getFy();
			double kxpcb = getKxpcb();
			double kypcb = getKypcb();
			double poissonPcb = getPoissonPcb();
			deltaXpcb = fx * (1.0 / kxpcb) - poissonPcb * fy * (1.0 / kypcb);

			System.out.println("[LIFESPAN] calculate DeltaXpcb = " + deltaXpcb + " {ΔT = " + deltaT() + "}");
			System.out.println("[LIFESPAN] input params: ");
			System.out.println("[LIFESPAN] \t- fx=" + fx);
			System.out.println("[LIFESPAN] \t- kxpcb=" + kxpcb);
			System.out.println("[LIFESPAN] \t- kypcb=" + kypcb);
			System.out.println("[LIFESPAN] \t- POISSON_PCB=" + poissonPcb +
					(composant instanceof Composant_Capacite ? " (capacitor)" : " (resistor)"));
		}
		return deltaXpcb;
	}
	// Δypcb
	private double getDYCiu() {
		double calcul = 0;

		if (composant instanceof ComposantQFN) {
			calcul = getRgm() * brasure.getSc()
					/ (composant.getComposant().get("b")
							* resultat_pcb1.getEpaisseurPcb() * Math.sqrt(2)
							* K)
					* (((ComposantQFN) composant).getXY()
							/ resultat_pcb1.get_double_module_traction_y()
							- Ressources.PCB_v
									* ((ComposantQFN) composant).getXX()
									/ resultat_pcb1
											.get_double_module_traction_y())
					+ getRgm() * ((ComposantQFN) composant).getSp()
							/ (composant.getComposant().get("b")
									* resultat_pcb1.getEpaisseurPcb()
									* Math.sqrt(2) * K)
							* (((ComposantQFN) composant).getLb()
									/ (resultat_pcb1
											.get_double_module_traction_y()
											- Ressources.PCB_v
													* ((ComposantQFN) composant)
															.getLa()
													/ resultat_pcb1
															.get_double_module_traction_y()));
		}

		else if (composant instanceof ComposantLCC) {
			calcul = getRgm() * brasure.getSc()
					/ (composant.getComposant().get("b")
							* resultat_pcb1.getEpaisseurPcb() * Math.sqrt(2)
							* K)
					* (((ComposantLCC) composant)
							.calculNombreX(composant, false, "nombreXY")
							/ resultat_pcb1.get_double_module_traction_y()
							- Ressources.PCB_v * ((ComposantLCC) composant)
									.calculNombreX(composant, false, "nombreXX")
									/ resultat_pcb1
											.get_double_module_traction_x());

			/*
			 * calcul = getRgm() * brasure.getSc() /
			 * (composant.getComposant().get("b")
			 * resultat_pcb1.getEpaisseurPcb() * Math.sqrt(2) K)
			 * (((ComposantLCC) composant).getXY() /
			 * resultat_pcb1.get_double_module_traction_y() - Ressources.PCB_v
			 * ((ComposantLCC) composant).getXX() / resultat_pcb1
			 * .get_double_module_traction_x());
			 */
		} else {
			calcul = getRgm() * brasure.getSc()
					/ (1.5 * Math.sqrt(2)
							* resultat_pcb1.get_double_module_traction_y()
							* resultat_pcb1.getEpaisseurPcb())
					* ((composant.getComposant().get("a")
							/ (4 * K * composant.getComposant().get("b")))
							- Ressources.PCB_v
									* (composant.getComposant().get("d")
											- composant.getComposant().get("b"))
									/ composant.getComposant().get("a"));
		}
		logger.info(td + "DY(ciu): " + calcul);

		return calcul;
	}

	private double getDeltaYpcb() {
		double deltaYpcb = 0;

		double fxy = getFxy();
		double b = composant.getComposant().get("b");
		double epcb = resultat_pcb1.getEpaisseurPcb();
		double expcb = resultat_pcb1.get_double_module_traction_x();
		double eypcb = resultat_pcb1.get_double_module_traction_y();
		double PCB_v = Ressources.PCB_v;


		if (composant instanceof ComposantQFN) {
			ComposantQFN qfn = (ComposantQFN) composant;
			deltaYpcb = fxy
					/ (b * epcb * SQRT_2 * K)
					* (qfn.getXY()
					/ eypcb	- PCB_v * qfn.getXX() / eypcb)
					+ getRgm() * qfn.getSp()
					/ (b * epcb	* SQRT_2 * K)
					* (qfn.getLb() / (eypcb	- PCB_v	* qfn.getLa() / eypcb));
		}

		else if (composant instanceof ComposantLCC) {
			ComposantLCC lcc = (ComposantLCC) composant;
			deltaYpcb = fxy
					/ (b
					* epcb * SQRT_2 * K)
					* (lcc.calculNombreX(composant, false, "nombreXY")
					/ eypcb	- PCB_v
					* lcc.calculNombreX(composant, false, "nombreXX")
					/ expcb);
		} else{
			double Fx = getFx();
			double Fy = getFy();
			double kxpcb = getKxpcb();
			double kypcb = getKypcb();
			double poissonPcb = getPoissonPcb();
			deltaYpcb = Fy * (1.0 / kypcb) - poissonPcb * Fx * (1.0 / kxpcb);
		}

		System.out.println("[LIFESPAN] calculate DeltaYpcb = " + deltaYpcb + " {ΔT = " + deltaT() + "}");
		return deltaYpcb;
	}

	// Δxypcb
	private double getDXYCiu() {
		double calcul = 0;
		calcul = Math.sqrt(Math.pow(getDXCiu(), 2) + Math.pow(getDYCiu(), 2));
		logger.info(td + "DXY(ciu): " + calcul);
		return calcul;
	}

	private double getDeltaXYpcb() {
		double deltaXpcb = getDeltaXpcb();
		double deltaYpcb = getDeltaYpcb();

		double deltaXYpcb = Math.sqrt(deltaXpcb * deltaXpcb + deltaYpcb * deltaYpcb);
		System.out.println("[LIFESPAN] calculate deltaXYpcb: deltaXpcb = " + deltaXpcb +
				", deltaYpcb = " + deltaYpcb +
				" ΔT=" + deltaT() + "°C," +
				" deltaXYpcb = " + deltaXYpcb);
		return deltaXYpcb;
	}

	private double getDXComp() {
		double calcul = 0;

		double fxy = getFxy();

		if (composant instanceof ComposantQFN) {
			double cteComposant = ((ComposantQFN) composant).getE(temperature);
			calcul = fxy
					/ (composant.getComposant().get("a")
							* composant.getComposant().get("z") * SQRT_2
							* K)
					* (((ComposantQFN) composant).getXX() / cteComposant
							- ResineEncapsulation.coefficient_poisson_k0
									* ((ComposantQFN) composant).getXY()
									/ cteComposant)
					+ getRgm() * ((ComposantQFN) composant).getSp()
							/ (((ComposantQFN) composant).getXa()
									* composant.getComposant().get("z")
									* SQRT_2 * K)
							* (((ComposantQFN) composant).getLa() / cteComposant
									- ResineEncapsulation.coefficient_poisson_k0
											* ((ComposantQFN) composant).getLb()
											/ cteComposant);
		}

		else if (composant instanceof ComposantLCC) {
			calcul = fxy
					/ (SQRT_2 * composant.getComposant().get("b")
							* composant.getComposant().get("z") * composants)
					* (((ComposantLCC) composant)
							.calculNombreX(composant, false, "nombreXX")
							/ composant.getE()
							- Ceramique.coefficient_poisson
									* ((ComposantLCC) composant).calculNombreX(
											composant,
											false,
											"nombreXY")
									/ composant.getE());

			/*
			 * calcul = getRgm() * brasure.getSc() / (Math.sqrt(2) *
			 * composant.getComposant().get("b")
			 * composant.getComposant().get("z") * composants) (((ComposantLCC)
			 * composant).getXX() / composant.getE() -
			 * Ceramique.coefficient_poisson ((ComposantLCC) composant).getXY()
			 * / composant.getE());
			 */
			logger.info("E composant: " + composant.getE());
		}
		else {
//			double excomp = composant.getE();
//			double ecomp = composant.getComposant().get("h");
//
//			double d = composant.getComposant().get("d");
//			double a = composant.getComposant().get("a");
//			double b = composant.getComposant().get("b");

			double coef_poisson = ((BrasureComposantLeadless) brasure).getCoefficientDePoisson();

//			calcul = fxy / (SQRT_2 * excomp * ecomp)
//					* ((d - b) / a - coef_poisson	* a / (4 * K * b));
//			calcul = getFx() * (1.0 / getKxcomp()) - coef_poisson * getFy() * (1.0 / getKycomp());
			calcul = getFx() * (1.0 / getKxcomp()) - coef_poisson * getFy() * (1.0 / getKypcb());
			System.out.println("[LIFESPAN] calculate DeltaXcomp fx: " + getFx() + " fy: " + getFy() + " kxcomp: " + getKxcomp()
					+ " kypcb: " + getKypcb() + " deltaXcomp: " + calcul + ", muComp="+ coef_poisson + " {ΔT = " + deltaT() + "}");
		}

		System.out.println("[LIFESPAN] calculate DeltaXcomp= " + calcul + " {ΔT = " + deltaT() + "}, " + td);
		return calcul;
	}

	public static final double composants = (SQRT_2 - 1) / 2 + 1;

	private double getDYComp() {
		double calcul = 0;

		double fxy = getFxy();

		if (composant instanceof ComposantQFN) {
			calcul = fxy
					/ (composant.getComposant().get("b")
							* composant.getComposant().get("z") * SQRT_2
							* K)
					* (((ComposantQFN) composant).getXY()
							/ ((ComposantQFN) composant).getE(temperature)
							- ResineEncapsulation.coefficient_poisson_k0
									* ((ComposantQFN) composant).getXX()
									/ ((ComposantQFN) composant)
											.getE(temperature))
					+ getRgm() * ((ComposantQFN) composant).getSp()
							/ (((ComposantQFN) composant).getYa()
									* composant.getComposant().get("z")
									* SQRT_2 * K)
							* (((ComposantQFN) composant).getLb()
									/ ((ComposantQFN) composant)
											.getE(temperature)
									- ResineEncapsulation.coefficient_poisson_k0
											* ((ComposantQFN) composant).getLa()
											/ ((ComposantQFN) composant)
													.getE(temperature));
		}

		else if (composant instanceof ComposantLCC) {
			calcul = getRgm() * brasure.getSc()
					/ (Math.sqrt(2) * composant.getComposant().get("b")
							* composant.getComposant().get("z") * composants)
					* (((ComposantLCC) composant)
							.calculNombreX(composant, false, "nombreXY")
							/ composant.getE()
							- Ceramique.coefficient_poisson
									* ((ComposantLCC) composant).calculNombreX(
											composant,
											false,
											"nombreXX")
									/ composant.getE());
		} else {
//			calcul = getRgm() * brasure.getSc()
//					/ (Math.sqrt(2) * composant.getE()
//							* composant.getComposant().get("h"))
//					* (composant.getComposant().get("a") / (4 * K
//							* composant.getComposant().get("b")
//							- ((BrasureComposantLeadless) brasure)
//									.getCoefficientDePoisson()
//									* (composant.getComposant().get("d")
//											- composant.getComposant().get("b"))
//									/ composant.getComposant().get("a")));
			double coef_poisson = ((BrasureComposantLeadless) brasure).getCoefficientDePoisson();
			calcul = getFy() * (1.0 / getKycomp()) - coef_poisson * getFx() * (1.0 / getKxcomp());

			System.out.println("[LIFESPAN] calculate DeltaYcomp fx: " + getFx() + " fy: " + getFy() + " kxcomp: " + getKxcomp()
					+ " kycomp: " + getKycomp() + " deltaXcomp: " + calcul + ", muComp="+ coef_poisson + " {ΔT = " + deltaT() + "}");
		}
		logger.info(td + "DY(composant): " + calcul);
		System.out.println("[LIFESPAN] calculate DeltaYcomp= " + calcul + " {ΔT = " + deltaT() + "}, " + td);

		return calcul;
	}

	/* TODO: BGA */

	public double np_pastille_y() {
		double calcul = 0;

		calcul = ((int) (((ComposantBGA) composant).getPuce_y() - 1) / 2.0)
				/ ((ComposantBGA) composant).getPas();
		return calcul;
	}

	public double lnp_pastille_y() {
		double calcul = 0;

		calcul = ((ComposantBilles) composant).getPuce_y() / 2.0;
		return calcul;
	}

	public double u0_pastille_y() {
		double calcul = 0;

		calcul = lnp_pastille_y()
				/ (double) ((ComposantBilles) composant).getPas();
		return calcul;
	}

	public double n_pastille_y() {
		double calcul = 0;
		if (this.u0_pastille_y() - ((int) this.u0_pastille_y()) > 0) {
			calcul = (int) this.u0_pastille_y();
		} else {
			calcul = this.u0_pastille_y() - 1;
		}
		stringLogger.append(td + " N pastille(y): " + calcul);

		return calcul;
	}

	public double n_pastille_x() {
		double calcul = 0;

		if (this.u0_pastille_x() - ((int) (this.u0_pastille_x())) > 0) {
			calcul = (int) (this.u0_pastille_x());
		} else {
			calcul = this.u0_pastille_x() - 1;
		}
		stringLogger.append(td + " N pastille(x): " + calcul + " \n");
		return calcul;
	}

	public double lnp_pastille_x() {
		double calcul = 0;

		calcul = ((ComposantBilles) composant).getPuce_x() / 2.0;

		stringLogger.append(td + " Lnp pastille x: " + calcul + " \n");
		return calcul;
	}

	public double u0_pastille_x() {
		double calcul = 0;

		calcul = lnp_pastille_x()
				/ (double) ((ComposantBilles) composant).getPas();
		logger.info(td + "U0 pastille: " + calcul);
		return calcul;
	}

	public double Dalpha1_t1() {
		double calcul = 0;
		calcul = resultat_pcb1.getCteXY()
				- ((ComposantBilles) composant).getCte(temperature);
		logger.info(td + "Delta alpha 1: " + calcul);
		return calcul;

	}

	public double Dalpha2_t1() {
		double calcul = 0;
		calcul = resultat_pcb1.getCteXY()
				- ((ComposantBGA) composant).getCte2(temperature);
		logger.info(td + "Delta alpha 2: " + calcul);
		return calcul;

	}

	public double point_neutre_t1_x() {

		double calcul = 0;

		double deltaAlphaT1 = Dalpha1_t1();
		double deltaAlphaT2 = Dalpha2_t1();
		double lnp_pastille_x = lnp_pastille_x();

		if (deltaAlphaT1 > 0 && deltaAlphaT2 < 0) {
			calcul = -deltaAlphaT1 * lnp_pastille_x / deltaAlphaT2
					+ lnp_pastille_x;
		} else if (deltaAlphaT1 > 0 && deltaAlphaT2 > 0) {
			calcul = lnp1_matrice1_x();
		} else {
			calcul = 0;
			logger.info("FAUX delta2");
		}
		stringLogger.append("Point neutre x: " + calcul);
		return calcul;
	}

	public double getPointNeutreX() {
		double calcul = 0;

		double deltaAlpha1 = Dalpha1_t1();
		double deltaAlpha2 = Dalpha2_t1();

		if (point_neutre_t1_x() > lnp1_matrice1_x()) {
			calcul = lnp1_matrice1_x();
		} else if (deltaAlpha1 > 0 && deltaAlpha2 > 0) {
			calcul = lnp1_matrice1_x();
		} else {
			calcul = point_neutre_t1_x();
		}
		stringLogger.append("Point neutre(x) corrigé: " + calcul);
		return calcul;
	}

	public double point_neutre_t1_y() {

		double calcul = 0;
		double deltaAlphaT1 = Dalpha1_t1();
		double deltaAlphaT2 = Dalpha2_t1();
		if (deltaAlphaT1 > 0 && deltaAlphaT2 < 0) {
			calcul = -deltaAlphaT1 * this.lnp_pastille_y() / deltaAlphaT2
					+ this.lnp_pastille_y();
		} else if (deltaAlphaT1 > 0 && deltaAlphaT2 > 0) {
			calcul = lnp1_matrice1_x();
		} else {
			calcul = 0;
		}
		stringLogger.append("Point neutre(y): " + calcul);
		return calcul;
	}

	public double N_t1_x() {
		double calcul = 0;

		calcul = ((int) (getPointNeutreX())) - this.lnp1_matrice1_x();
		logger.info(td + "N x: " + calcul);
		return calcul;
	}

	public double nombre_effort_hors_pastille_x_t1() {
		double calcul = 0;

		calcul = this.lnp1_matrice1_x() - this.lnp_pastille_x() + this.N_t1_x();
		logger.info(td + " Nombre effort hors pastille(x): " + calcul);
		return calcul;

	}

	public double X_pastille_x() {
		double calcul = 0;

		calcul = (this.n_pastille_x() + 1)
				* ((2 * this.u0_pastille_x())
						+ (Ressources.RAISON) * this.n_pastille_x())
				/ 2 * ((ComposantBilles) composant).getPas();
		logger.info(td + "X pastille x: " + calcul);
		return calcul;
	}

	public double palier1_Rgm_SAC305() {
		double calcul = 0;
		calcul = Ressources.SAC305_K3_CISAILLEMENT * Math.pow(temperature, 2)
				+ Ressources.SAC305_K4_CISAILLEMENT * temperature
				+ Ressources.SAC305_K5_CISAILLEMENT;

		return calcul;

	}

	public double X_pastille_y() {
		double calcul = 0;

		calcul = (this.n_pastille_y() + 1)
				* ((2 * this.u0_pastille_y())
						+ Ressources.RAISON * this.n_pastille_y())
				/ 2 * ((ComposantBilles) composant).getPas();

		return calcul;
	}

	public double getPointNeutreY() {
		double calcul = 0;

		if (point_neutre_t1_y() > lnp1_matrice1_y()) {
			calcul = lnp1_matrice1_y();
		} else {
			calcul = point_neutre_t1_y();
		}

		return calcul;
	}

	public double N_t1_y() {
		double calcul = 0;

		calcul = ((int) (getPointNeutreY())) - this.lnp1_matrice1_y();
		logger.info(td + "N y: " + calcul);
		return calcul;
	}

	public double uo_matrice1_x() {
		double calcul = 0;
		calcul = this.taille_matrice_bloc1_x();
		logger.info(td + "U0 matrice: " + calcul);

		return calcul;
	}

	public double nx_matrice1_x() {
		double calcul = 0;

		if (this.uo_matrice1_x() - ((int) (this.uo_matrice1_x())) > 0) {
			calcul = (int) this.uo_matrice1_x();
		} else {
			calcul = this.uo_matrice1_x() - 1;
		}
		logger.info("Nx matrice x: " + calcul);

		return calcul;
	}

	public double nombre_X1_x_t1() {
		double calcul = 0;
		if (composant instanceof ComposantBGA
				|| composant instanceof ComposantCSP) {

			if (((ComposantBGA) composant).getNombre_billes_x() % 2 == 0) {
				logger.log(Level.WARNING, "Nombre X1 pair");
				calcul = this.X_pastille_x() + ((int) (this
						.nombre_effort_hors_pastille_x_t1()))
						* (this.np_pastille_x()
								+ (((ComposantBGA) composant).getPas() / 2));
			} else {
				logger.log(Level.WARNING, "Nombre X1 impair");

				calcul = this.X_pastille_x() + ((int) (this
						.nombre_effort_hors_pastille_x_t1()))
						* (this.np_pastille_x()
								+ (((ComposantBGA) composant).getPas()));
			}
		} else {
			calcul = (this.nx_matrice1_x() + 1)
					* (2 * this.uo_matrice1_x()
							+ this.nx_matrice1_x() * Ressources.RAISON)
					/ 2 * ((ComposantBilles) composant).getPas();
		}
		logger.log(Level.INFO, td + "Nombre X1 x: " + calcul);
		return calcul;

	}

	public double nombre_X1_y_t1() {
		double calcul = 0;
		if (((ComposantBGA) composant).getNombre_billes_x() % 2 == 0) {
			calcul = this.X_pastille_y()
					+ ((int) (this.nombre_effort_hors_pastille_y_t1())) * (this
							.np_pastille_y()
							+ (((ComposantBGA) composant).getPas() / 2));
		} else {
			calcul = this.X_pastille_y()
					+ ((int) (this.nombre_effort_hors_pastille_y_t1()))
							* (this.np_pastille_y()
									+ (((ComposantBGA) composant).getPas()));
		}
		logger.info("Nombre X1 y: " + calcul);
		return calcul;

	}

	public double nombre_effort_hors_pastille_y_t1() {
		double calcul = 0;
		calcul = this.lnp1_matrice1_y() - this.lnp_pastille_y() + this.N_t1_y();
		logger.info("Nombre effort hors pastille: " + calcul);

		return calcul;

	}

	protected double getNX2() {
		double calcul = 0;

		calcul = getu0X2() - 1;

		return calcul;
	}

	protected double getX2() {
		double calcul = 0;

		calcul = (getNX2() + 1)
				* ((2 * getu0X2()) + (Ressources.RAISON) * getNX2()) / 2.0
				* ((ComposantBGA) composant).getPas();

		return calcul;
	}

	public double getu0X2() {

		double calcul = 0;
		if (N_t1_x() < 0) {
			calcul = Math.abs((int) (N_t1_x())) - 1;
		} else {
			calcul = lnp1_matrice1_x();
		}

		return calcul;
	}

	public double np_pastille_x() {
		double calcul = 0;

		calcul = ((int) ((((ComposantBGA) composant).getPuce_x() - 1)) / 2.0)
				/ ((ComposantBGA) composant).getPas();
		logger.info("Np Pastille x: " + calcul);
		return calcul;
	}

	public double nombre_X1_xy_eq_t1() {

		// Si la matrice de notre Bga n'es pas pleine on entre
		// on prend en compte les longeur équivalentes entrées
		// par l'utilisateur
		if (matrice_creuse) {
			return x1t1;
		}

		double calcul = 0;
		calcul = Math.sqrt(
				Math.pow(nombre_X1_x_t1(), 2) + Math.pow(nombre_X1_y_t1(), 2));
		logger.info("Nombre X1 eq xy: " + calcul);
		return calcul;

	}

	public double palier2_Rgm_SAC305() {
		double calcul = 0;
		calcul = Ressources.SAC305_K3_CISAILLEMENT * Math.pow(temperature, 2)
				+ Ressources.SAC305_K4_CISAILLEMENT * temperature
				+ Ressources.SAC305_K5_CISAILLEMENT;
		return calcul;

	}

	public double lnp1_matrice1_xy() {
		double calcul = 0;
		calcul = Math.sqrt(
				Math.pow(taille_matrice_bloc1_x(), 2)
						+ Math.pow(taille_matrice_bloc1_y(), 2));
		logger.info("lnp1 xy: " + calcul);
		return calcul;
	}

	public double taille_matrice_bloc1_y() {
		double calcul = 0;
		calcul = (((ComposantBilles) composant).getNombre_billes_y() - 1) / 2.0;
		logger.info(
				"Nombre de billes y: "
						+ ((ComposantBilles) composant).getNombre_billes_y());
		return calcul;
	}

	public double lnp1_matrice1_y() {
		double calcul = 0;
		calcul = taille_matrice_bloc1_y()
				* ((ComposantBilles) composant).getPas();
		logger.info("Lnp1 matrice y: " + calcul);
		return calcul;

	}

	public double taille_matrice_bloc1_x() {
		double calcul = 0;

		calcul = ((double) (((ComposantBilles) composant).getNombre_billes_x()
				- 1)) / 2.0;
		logger.info(
				"Nombre Billes x: "
						+ ((ComposantBilles) composant).getNombre_billes_x());
		logger.info("Taille matrice bloc x: " + calcul);
		return calcul;
	}

	public double lnp1_matrice1_x() {
		double calcul = 0;
		calcul = taille_matrice_bloc1_x()
				* ((ComposantBilles) composant).getPas();
		logger.info("Lnp1 matrice: " + calcul);
		return calcul;

	}

	public double lnp1() {
		double calcul = 0;

		calcul = Math.sqrt(
				(Math.pow(taille_matrice_bloc1_x(), 2)
						+ Math.pow(taille_matrice_bloc1_y(), 2)))
				/ 2;
		logger.info(td + "lnp1(): " + calcul);
		return calcul;
	}

	public double palier1_DLMax1() {

		double calcul;
		/*
		 * if (composant instanceof ComposantCSP) {
		 * 
		 * double coef_dilatation_xy_pcb = resultat_pcb1.getCteXY(); double
		 * cteR_1_2_bga = ((ComposantCSP) composant) .getCter(temperature);
		 * logger.warning( "coef_dilatation_xy_pcb" + coef_dilatation_xy_pcb +
		 * " cteR_1_2_bga: " + cteR_1_2_bga); calcul = (temperature - 20)
		 * (coef_dilatation_xy_pcb - cteR_1_2_bga) Math.abs( this.N_t1_x()
		 * ((ComposantCSP) composant).getPas());
		 * 
		 * } else if (composant instanceof ComposantBGA || composant instanceof
		 * ComposantWLP) {
		 * 
		 * double coef_dilatation_xy_pcb = resultat_pcb1.getCteXY(); double
		 * ct1_composant = ((ComposantBilles) composant) .getCte(temperature);
		 * logger.info(td + "cte 1: " + ct1_composant); logger.info(td +
		 * "CTE plan pcb: " + coef_dilatation_xy_pcb); logger.info( td +
		 * "epaisseur pcb: " + resultat_pcb1.getEpaisseurPcb());
		 * 
		 * if (composant instanceof ComposantBGA) { calcul = (temperature - 20)
		 * (coef_dilatation_xy_pcb - ct1_composant) this.lnp1_matrice1_xy();
		 * 
		 * } else { calcul = (temperature - 20) (coef_dilatation_xy_pcb -
		 * ct1_composant) this.lnp1(); } } logger.info(td + "Delta L max: " +
		 * calcul);
		 */
		double lnp1 = 0;
		double coef_dilatation_xy_pcb = resultat_pcb1.getCteXY();
		// Cte2Plus1 pour BGA, SBGA, CSP mais cte pour WLP ou CBGA ou PCBGA
		double ct2Plus1_composant = ((ComposantBilles) composant)
				.getCte(temperature);
		logger.info(td + "cte 2+1: " + ct2Plus1_composant);
		logger.info(td + "CTE plan pcb: " + coef_dilatation_xy_pcb);
		logger.info(td + "epaisseur pcb: " + resultat_pcb1.getEpaisseurPcb());

		if (composant instanceof ComposantCSP) {
			lnp1 = ((ComposantCSP) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "lnp1");
		} else if (composant instanceof ComposantWLP) {
			lnp1 = ((ComposantWLP) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "lnp1");
		} else if (composant instanceof ComposantCBGA) {
			lnp1 = ((ComposantCBGA) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "lnp1");
		} else if (composant instanceof ComposantPCBGA) {
			lnp1 = ((ComposantPCBGA) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "lnp1");
		} else if (composant instanceof ComposantBGA) {
			lnp1 = ((ComposantBGA) composant).calculNombreX(
					composant,
					this.getPointNeutreX(),
					this.getPointNeutreY(),
					false,
					"lnp1");
		}

		calcul = (temperature - 20)
				* (coef_dilatation_xy_pcb - ct2Plus1_composant) * lnp1;

		return calcul;
	}

	public double dr1_palier1() {

		System.out.println("[DureeDeVie] dr1_palier1() function called");

		double K1 = palier1_K1();
		System.out.println("[DureeDeVie] K1 = palier1_K1() " + K1);

		double DLMax = palier1_DLMax1();

		System.out.println("[DureeDeVie] DLMax = palier1_DLMax1() " + DLMax);

		double calcul = 0;

		// System.out.println(td+"k1: "+K1+" delta max: "+DLMax);
		if (Math.abs(K1) > Math.abs(DLMax)) {
			calcul = 0;
		} else if (K1 > 0 && DLMax < 0) {
			calcul = DLMax + K1;
		} else {
			calcul = DLMax - K1; /* VALEUR POUR ELSE */
		}

		logger.info(td + " dr1: " + calcul);
		return calcul;
	}

	public double palier1_Rgm_t_SAC305() {// mettre duree_paler en parametre
		double calcul = 0;

		calcul = this.palier1_Rgm_SAC305() * Math.pow(
				(duree * 60),
				Ressources.SAC305_K6_CISAILLEMENT * Math
						.exp(Ressources.SAC305_K7_CISAILLEMENT * temperature));
		return calcul;

	}

	public double getNombreX1() {
//		System.out.println("Taille de pastilles: "+((Composant_BGA) composant).getPuce_x());
//		System.out.println("this.X_pastille_x(): "+ this.X_pastille_x());
//		System.out.println(" Math.floor(nombre_effort_hors_pastille_x_t1()): "+ Math.floor(nombre_effort_hors_pastille_x_t1()));
//		System.out.println("np_pastille_x() : "+ np_pastille_x());
		double calcul = X_pastille_x()
				+ ((int) (nombre_effort_hors_pastille_x_t1()))
						* (np_pastille_x() + 1);
		return calcul;
	}

	public double nombre_x1_matrice1_x() {
		double calcul = 0;
		calcul = (this.nx_matrice1_x() + 1)
				* (2 * this.uo_matrice1_x()
						+ this.nx_matrice1_x() * Ressources.RAISON)
				/ 2 * ((ComposantBilles) composant).getPas();
		logger.info(td + "nombre X1 x: " + calcul);
		return calcul;
	}

	public double nombre_x1_matrice1_y() {
		double calcul = 0;
		calcul = (this.nx_matrice1_y() + 1)
				* (2 * this.uo_matrice1_y()
						+ this.nx_matrice1_y() * Ressources.RAISON)
				/ 2 * ((ComposantBilles) composant).getPas();
		logger.info(td + "nombre X1 y: " + calcul);
		return calcul;
	}

	public double nx_matrice1_y() {
		double calcul = 0;

		if (this.uo_matrice1_y() - ((int) (this.uo_matrice1_y())) > 0) {
			calcul = (int) this.uo_matrice1_y();
		} else {
			calcul = this.uo_matrice1_y() - 1;
		}

		return calcul;
	}

	public double uo_matrice1_y() {
		return taille_matrice_bloc1_y();
	}

	public double nombre_X1_xy_eq() {
		double calcul = 0;
		calcul = Math.sqrt(
				Math.pow(this.nombre_x1_matrice1_x(), 2)
						+ Math.pow(this.nombre_x1_matrice1_y(), 2));
		logger.info(td + "Nombre X1 xy: " + calcul);
		return calcul;
	}

	public double palier1_K1() {
		/*******************
		* VOIR CE QUI VA PAS ICI
		**************/
		double calcul = 0;

		System.out.println("[DureeDeVie] PALIER1 K1 Call");

		double module_traction_pcb = resultat_pcb1.get_double_module_traction_xy();
		double e1_composant = ((ComposantBilles) composant).getE1Traction(temperature);
		double epaisseur_pcb = resultat_pcb1.getEpaisseurPcb();
		double epaisseur_composant = Double.parseDouble(((ComposantBilles) composant).getComposant_z());

		System.out.println("[DureeDeVie] Module Traction PCB: " + module_traction_pcb);
		System.out.println("[DureeDeVie] E1 Composant : " + e1_composant);
		System.out.println("[DureeDeVie] Epaisseur PCB : " + epaisseur_pcb);
		System.out.println("[DureeDeVie] Epaisseur Composant : " + epaisseur_composant);

		System.out.println(td + "module_traction_pcb: " + module_traction_pcb
				+ " e1_composant: " + e1_composant + " epaisseur_pcb: "
				+ epaisseur_pcb + " epaisseur_composant: "
				+ epaisseur_composant + " pas: "
				+ ((ComposantBilles) composant).getPas() + " rgm: "
				+ this.palier1_Rgm_t_SAC305());

		if (composant instanceof ComposantBGA
				|| composant instanceof ComposantCSP
				|| composant instanceof ComposantPCBGA) {

			if (composant instanceof ComposantCSP) {

				calcul = (((ComposantCSP) composant)
						.calculNombreX(composant, 0.0, 0.0, false, "nombreX"))
						/ ((ComposantBilles) composant).getPas()
						* this.palier1_Rgm_t_SAC305() * Math.PI
						* Math.pow(
								((BrasureComposantBilles) brasure).getD1(),
								2)
						/ 4.0
						* ((1 - Ressources.PCB_v)
								/ (module_traction_pcb * epaisseur_pcb)
								+ (1 - Ressources.COMPOSANT_v)
										/ (e1_composant * epaisseur_composant));
				// BGA, SBGA
			} else if (composant instanceof ComposantBGA) {

				calcul = (((ComposantBGA) composant).calculNombreX(
						composant,
						this.getPointNeutreX(),
						this.getPointNeutreY(),
						false,
						"nombreX")) / ((ComposantBilles) composant).getPas()
						* this.palier1_Rgm_t_SAC305() * Math.PI
						* Math.pow(
								((BrasureComposantBilles) brasure).getD1(),
								2)
						/ 4.0
						* ((1 - Ressources.PCB_v)
								/ (module_traction_pcb * epaisseur_pcb)
								+ (1 - Ressources.COMPOSANT_v)
										/ (e1_composant * epaisseur_composant));

			} else if (composant instanceof ComposantPCBGA) {
				calcul = (((ComposantPCBGA) composant)
						.calculNombreX(composant, 0.0, 0.0, false, "nombreX"))
						/ ((ComposantBilles) composant).getPas()
						* this.palier1_Rgm_t_SAC305() * Math.PI
						* Math.pow(
								((BrasureComposantBilles) brasure).getD1(),
								2)
						/ 4.0
						* ((1 - Ressources.PCB_v)
								/ (module_traction_pcb * epaisseur_pcb)
								+ (1 - Ressources.COMPOSANT_v)
										/ (e1_composant * epaisseur_composant));
				// System.out.println("K1 a " + temperature + " :" + calcul);

			}
		}
		// Composant CBGA
		else if (composant instanceof ComposantCBGA) {
			logger.info(
					td + "d1 : " + ((BrasureComposantBilles) brasure).getD1());

			calcul = ((double) (((ComposantCBGA) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "nombreX")))
					/ ((ComposantBilles) composant).getPas()
					* this.palier1_Rgm_t_SAC305() * Math.PI
					* Math.pow(((BrasureComposantBilles) brasure).getD1(), 2)
					/ 4.0
					* ((1 - Ressources.PCB_v)
							/ (module_traction_pcb * epaisseur_pcb)
							+ (1 - Ceramique.coefficient_poisson)
									/ (e1_composant * epaisseur_composant));

			// Composant WLP
		} else {
			logger.info(
					td + "d1 : " + ((BrasureComposantBilles) brasure).getD1());

			calcul = ((double) (((ComposantWLP) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "nombreX")))
					/ ((ComposantBilles) composant).getPas()
					* this.palier1_Rgm_t_SAC305() * Math.PI
					* Math.pow(((BrasureComposantBilles) brasure).getD1(), 2)
					/ 4.0
					* ((1 - Ressources.PCB_v)
							/ (module_traction_pcb * epaisseur_pcb)
							+ (1 - Silicium.coefficient_poisson_k0)
									/ (e1_composant * epaisseur_composant));

		}
		logger.log(Level.INFO, td + "K1: " + calcul);
		return calcul;
	}

	public double palier1_yc1() {
		System.out.println("[DureeDeVie] palier1_yc1() function called");
		double calcul;

		double palierDR1 = this.dr1_palier1();

		if (((BrasureComposantBilles) brasure).getTechnologie().equals("NSMD")) {
			double HI = ((BrasureComposantBilles) brasure).getHauteurIntegreeNsmd();
			calcul = palierDR1 / HI;
			System.out.println("[DureeDeVie] palierDR1: " + palierDR1 + "; HI: " + HI);
			System.out.println("[DureeDeVie] NSMD: " + calcul);
		} else {
			double HI = ((BrasureComposantBilles) brasure).getHauteurIntegreeSmd();
			calcul = palierDR1 / HI;
			System.out.println("[DureeDeVie] SMD: " + calcul);
		}
		System.out.println("[DureeDeVie] Befor the return : calucl YC1 = " + calcul);
		return calcul;

	}

	/**
	 * Renvoie N50% pour les composants à Billes en connaissant l'angle de
	 * cisaillement de la température basse. Au cours du traitement la fonction
	 * ira cherher l'angle de cisaillement à la température haute.
	 * 
	 * @param yc12 L'angle de cisaillement à température basse
	 * 
	 * @return N50%
	 */
	public double calcul_cycle_bille_angles_pastille(double yc12) {
		double calcul = 0;
		double _yc11 = this.palier1_yc1();
		double _yc12 = yc12;

		calcul = Outils.absolute_difference(_yc11, _yc12);

		System.out.println("I Love You Both (Son & Wife): "+ calcul);

		// double resultat = Math.pow(
		// calcul / (((Math.PI * Math
		// .pow(((BrasureComposantBilles) brasure).getD1(), 2)
		// / 4.0) + 1) * Ressources.COEFFICIENT_FATIGUE_A),
		// (1.0 / Ressources.COEFFICIENT_FATIGUE_B));

		double res = Math.pow(
				calcul / (((Math.PI * Math
						.pow(((BrasureComposantBilles) brasure).getD1(), 2)
						/ 4.0) + 1) * composant.getCoefficientFatigueA()),
				(1.0 / composant.getCoefficientFatigueB()));
		System.out.println("## This is the final check: " + res);

        return Math.pow(
                calcul / (((Math.PI * Math
                        .pow(((BrasureComposantBilles) brasure).getD1(), 2)
                        / 4.0) + 1) * composant.getCoefficientFatigueA()),
                (1.0 / composant.getCoefficientFatigueB()));
	}

	/* TODO: FIN BGA */

	private double getDXYComp() {
		logger.log(
				Level.WARNING,
				td + "DXYComp: " + Math.sqrt(
						Math.pow(getDXComp(), 2) + Math.pow(getDYComp(), 2)));

		System.out.println(td + "DXYComp: " + Math.sqrt(
						Math.pow(getDXComp(), 2) + Math.pow(getDYComp(), 2)));
		return Math.sqrt(Math.pow(getDXComp(), 2) + Math.pow(getDYComp(), 2));
	}

	public double getYC1() {
		System.out.println("[LIFESPAN] ========== FORCE-BASED GAMMA CALCULATION START ==========");
		double gamma = 0;
		double deltaMaxXY = getDeltaMaxXY();

		double DXYCiu = getDeltaXYpcb(); //getDXYCiu();
//		double DXComp = getDXComp(); // Ok
		double DXYComp = getDXYComp(); // Ok

		// 11. Calculate shear angle with sign handling
		double totalDeformation = DXYCiu + DXYComp;  //deltaXYpcb + deltaXYcomp;
		double hauteur_integree = brasure.getHi();

		if (composant instanceof ComposantLCC || composant instanceof ComposantQFN) {
			gamma = getDeltaR() / hauteur_integree;
		}
		else {
			// Excel logic: IF(ABS(W57+AD57)>ABS(L57), 0, ...)
			if (Math.abs(totalDeformation) > Math.abs(deltaMaxXY)) {
				gamma = 0.0;
				System.out.println("[LIFESPAN] Deformation exceeds max displacement → γ = 0");
			}
			// Excel logic: IF(AND(L57<0, (W57+AD57)>0), (L57+W57+AD57)/Hi, ...)
			else if (deltaMaxXY < 0 && (totalDeformation) > 0) {
				// Compression case
				gamma = (deltaMaxXY + totalDeformation) / hauteur_integree;
				System.out.println("[LIFESPAN] Compression case: γ = (ΔxmaxXY + totalDef) / Hi");
			}
			else {
				// Standard case
				gamma = (deltaMaxXY - totalDeformation) / hauteur_integree;
				System.out.println("[LIFESPAN] Standard case: γ = (ΔxmaxXY - totalDef) / Hi");
			}

		}
//		else {
//			if (Math.abs(DXYCiu + DXComp) >= deltaMaxXY) {
//				calcul = 0;
//			} else {
//				calcul = (deltaMaxXY - (DXYCiu + DXYComp)) / hauteur_integree;
//			}
//		}
		System.out.println("[LIFESPAN] FINAL GAMMA: γ = " + gamma + " {" + td + "}");
		System.out.println("[LIFESPAN] ========== FORCE-BASED GAMMA CALCULATION END ==========");
		return gamma;
	}

	private double getDeltaR() {
		double calcul = 0;

		double DXYCiu = getDeltaXYpcb(); //getDXYCiu();
		double DeltaMaxXY = getDeltaMaxXY();
		double DXYComp = getDXYComp();

		if (Math.abs((DXYCiu + DXYComp)) > Math.abs(DeltaMaxXY)) {
			calcul = 0;
		} else if (DeltaMaxXY < 0 && (DXYCiu + DXYComp) > 0) {
			calcul = DeltaMaxXY + DXYCiu + DXYComp;
		} else {
			calcul = DeltaMaxXY - DXYCiu - DXYComp;
		}
		logger.severe(td + "delta R: " + calcul);
		return calcul;
	}

	/**
	 * Renvoie N50% à partir de l'angle de cisaillement à froid. Au cours du
	 * calcul la fonction ira chercher l'angle de cisaillement à chaud.
	 * 
	 * @param yc12 angle cisaillement t2 (température basse)
	 * @return N50%
	 */
	public double getN50(double yc12) {

		// Angle de cisaillement température haute
		double yc11 = getYC1();

		System.out.println("[LIFESPAN] ========== N50% CALCULATION ==========");
		System.out.println("[LIFESPAN] γ₁ (hot) = " + yc11);
		System.out.println("[LIFESPAN] γ₂ (cold) = " + yc12);

		// Calculate Δγ with proper sign handling
		double deltaGamma;

		if (yc11 < 0 && yc12 > 0) {
			// Compression to tension: opposite signs
			deltaGamma = Math.abs(yc11) + yc12;
			System.out.println("[LIFESPAN] Case: γ₁<0, γ₂>0 → Δγ = |γ₁| + γ₂");

		} else if (yc11 < 0 && yc12 < 0) {
			// Both compression
			deltaGamma = Math.abs(yc11 - yc12);
			System.out.println(td + " Case: γ₁<0, γ₂<0 → Δγ = |γ₁ - γ₂|");

		} else if (yc11 > 0 && yc12 < 0) {
			// Tension to compression: opposite signs
			deltaGamma = yc11 + Math.abs(yc12);
			System.out.println("[LIFESPAN] Case: γ₁>0, γ₂<0 → Δγ = γ₁ + |γ₂|");

		} else {
			// Both tension (or both zero)
			deltaGamma = Math.abs(yc11 - yc12);
			System.out.println("[LIFESPAN] Case: γ₁>0, γ₂>0 → Δγ = |γ₁ - γ₂|");
		}

		System.out.println("[LIFESPAN] Δγ = " + deltaGamma);

		// Get fatigue coefficients and Sc
		double Sc = brasure.getSc();
		double coefA = composant.getCoefficientFatigueA();
		double coefB = composant.getCoefficientFatigueB();

		System.out.println("[LIFESPAN] Sc = " + Sc);
		System.out.println("[LIFESPAN] Fatigue A = " + coefA);
		System.out.println("[LIFESPAN] Fatigue B = " + coefB);

//		// Calculate N50% = [(Δγ / (1 + Sc)) × (1/A)]^(1/B)
//		double n50 = Math.pow(((deltaGamma / (1.0 + Sc)) * (1.0 / coefA)), 1.0 / coefB);

		// --- NEW LOGIC START ---

		// Determine the geometric correction factor based on component type
		double geometricFactor;

		if (composant instanceof Composant_Resistance || composant instanceof Composant_Capacite) {
			// Formula for Passives (Resistors/Capacitors): Uses (1 + 2 * Sc)
			geometricFactor = 1.0 + 2.0 * Sc;
			System.out.println("[LIFESPAN] Type: Passive (R/C) -> Using factor (1 + 2*Sc)");
		} else {
			// Formula for LCCC and others: Uses (1 + Sc)
			geometricFactor = 1.0 + Sc;
			System.out.println("[LIFESPAN] Type: Standard (LCCC/Other) -> Using factor (1 + Sc)");
		}

		// Calculate N50% = [(Δγ / geometricFactor) × (1/A)]^(1/B)
		double n50 = Math.pow((deltaGamma / geometricFactor) * (1.0 / coefA), 1.0 / coefB);

		// --- NEW LOGIC END ---

		logger.info(td + "Sc: " + brasure.getSc() + " Hi:" + brasure.getHi());
		logger.info(td + " N50%: " + n50);

		System.out.println("[LIFESPAN] N50% = " + n50 + " cycles {"+ td + "}");

		return n50;
	}

	/**
	 *
	 * @return Le nombre X d'un composant à Billes.
	 */
	public double getNombreXComposantABilles() {
		double nombreX;

		if (composant instanceof ComposantBGA
				|| composant instanceof ComposantCSP
				|| composant instanceof ComposantPCBGA) {

			if (composant instanceof ComposantCSP) {
				nombreX = ((ComposantCSP) composant)
						.calculNombreX(composant, 0.0, 0.0, false, "nombreX");
				return nombreX;
			} else if (composant instanceof ComposantBGA) {

				nombreX = ((ComposantBGA) composant).calculNombreX(
						composant,
						this.getPointNeutreX(),
						this.getPointNeutreY(),
						false,
						"nombreX");
				return nombreX;
			} else if (composant instanceof ComposantPCBGA) {
				nombreX = ((ComposantPCBGA) composant)
						.calculNombreX(composant, 0.0, 0.0, false, "nombreX");
				return nombreX;
			}
		}
		// Composant CBGA
		else if (composant instanceof ComposantCBGA) {

			nombreX = ((ComposantCBGA) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "nombreX");
			return nombreX;
			// Composant WLP
		} else {

			nombreX = ((ComposantWLP) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "nombreX");
			return nombreX;
		}
		return (Double) null;
	}

	public double getLnp1ComposantABilles() {
		double lnp1;

		if (composant instanceof ComposantCSP) {
			lnp1 = ((ComposantCSP) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "lnp1");
			return lnp1;
		} else if (composant instanceof ComposantWLP) {
			lnp1 = ((ComposantWLP) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "lnp1");
			return lnp1;
		} else if (composant instanceof ComposantCBGA) {
			lnp1 = ((ComposantCBGA) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "lnp1");
			return lnp1;
		} else if (composant instanceof ComposantPCBGA) {
			lnp1 = ((ComposantPCBGA) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "lnp1");
			return lnp1;
		} else if (composant instanceof ComposantBGA) {
			lnp1 = ((ComposantBGA) composant).calculNombreX(
					composant,
					this.getPointNeutreX(),
					this.getPointNeutreY(),
					false,
					"lnp1");
			return lnp1;
		}
		return (Double) null;
	}

	/*
	 * Renvoie la valeur de delta gamma d'une température DDV
	 */
	public double getCalculs() {
		if (composant instanceof ComposantBilles) {
			System.out.println("[DureeDeVie] instance of ComposantBilles: (palier1_yc1()) = " + palier1_yc1());
			return palier1_yc1();
		} else {
			System.out.println("[DureeDeVie] not instance of ComposantBilles: (getYC1()) = " + getYC1());
			return getYC1();
		}
	}

}

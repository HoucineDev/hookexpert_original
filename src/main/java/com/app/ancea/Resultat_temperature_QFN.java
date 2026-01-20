package com.app.ancea;

import java.util.logging.Logger;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * @author FX603984 Classe permettant d'avoir les proprietes thermomecaniques
 *         d'un qfn a une temperature donnee
 *
 */
public class Resultat_temperature_QFN extends Resultat_temperature {

	protected ComposantQFN composant;

	protected DoubleProperty cte, e1, v;

	private static final Logger logger = Logger.getLogger(Main.class.getName());

	public Resultat_temperature_QFN(
			ComposantQFN composant,
			double temperature) {
		super(composant, temperature);

		this.composant = composant;

		cte = new SimpleDoubleProperty(getCte12());
		e1 = new SimpleDoubleProperty(getE12());

		getCte();
		getE1();

	}

	private double getL0() {
		double calcul = 0;

		calcul = composant.composant.get("x2") / 2;
		logger.severe("LO: " + calcul);
		return calcul;
	}

	private double getCteA() {
		double calcul = 0;

		calcul = (composant.composant.get("z2") * Silicium.module_elasticite_k0
				* getSilicium()
				+ composant.composant.get("z3")
						* ResineEncapsulation.module_elasticite_k0
						* getResine())
				/ (composant.composant.get("z2") * Silicium.module_elasticite_k0
						+ composant.composant.get("z3")
								* ResineEncapsulation.module_elasticite_k0);
		logger.severe("CTE A: " + calcul);

		return calcul;
	}

	protected double getT1() {
		double calcul = 0;

		calcul = getEa() * getGzA() * get_ea()
				* (CuivreC19400.module_elasticite * composant.composant.get("h")
						- 4 * CuivreC19400.module_cisaillement * getL0())
				* (CuivreC19400.coefficient_dilatation - getCteA());
		logger.severe("T1: " + calcul);

		return calcul;
	}

	protected double getT2() {
		double calcul = 0;

		calcul = CuivreC19400.module_elasticite
				* CuivreC19400.module_cisaillement
				* composant.composant.get("h")
				* (4 * getGzA() * getL0() + getEa() * get_ea());
		logger.severe("T2: " + calcul);

		return calcul;
	}

	protected double getT3() {
		double calcul = 0;

		calcul = getEa() * getGzA() * get_ea()
				* (4 * CuivreC19400.module_elasticite * getL0()
						+ CuivreC19400.module_elasticite
								* composant.composant.get("h"));
		logger.severe("T3: " + calcul);

		return calcul;
	}

	private double getGzA() {
		double calcul = 0;

		calcul = Silicium.module_cisaillement_k0
				* ResineEncapsulation.module_cisaillement_k0
				* (composant.composant.get("z2")
						+ composant.composant.get("z3"))
				/ (Silicium.module_cisaillement_k0
						* composant.composant.get("z3")
						+ ResineEncapsulation.module_cisaillement_k0
								* composant.composant.get("z2"));
		logger.severe("Gz A: " + calcul);

		return calcul;
	}

	private double get_ea() {
		double calcul = 0;

		calcul = composant.composant.get("z3") + composant.composant.get("z2");
		logger.severe("ea: " + calcul);

		return calcul;
	}

	private double getSilicium() {
		double calcul = 0;

		calcul = Silicium.coefficient_dilatation_xy_k4
				* Math.pow(getTemperature(), 4)
				+ Silicium.coefficient_dilatation_xy_k3
						* Math.pow(getTemperature(), 3)
				+ Silicium.coefficient_dilatation_xy_k2
						* Math.pow(getTemperature(), 2)
				+ Silicium.coefficient_dilatation_xy_k1 * getTemperature()
				+ Silicium.coefficient_dilatation_xy_k0;

		logger.severe("Silicium: " + calcul);

		return calcul;
	}

	private double getResine() {
		double calcul = 0;

		calcul = ResineEncapsulation.coefficient_dilatation_xy_k4
				* Math.pow(getTemperature(), 4)
				+ ResineEncapsulation.coefficient_dilatation_xy_k3
						* Math.pow(getTemperature(), 3)
				+ ResineEncapsulation.coefficient_dilatation_xy_k2
						* Math.pow(getTemperature(), 2)
				+ ResineEncapsulation.coefficient_dilatation_xy_k1
						* getTemperature()
				+ ResineEncapsulation.coefficient_dilatation_xy_k0;

		logger.severe("resine: " + calcul);

		return calcul;
	}

	private double getEa() {
		double calcul = 0;
		calcul = (composant.composant.get("z2") * Silicium.module_elasticite_k0
				+ composant.composant.get("z3")
						* ResineEncapsulation.module_elasticite_k0)
				/ (get_ea());
		logger.severe("Ea: " + calcul);

		return calcul;
	}

	protected double getCte1() {
		double calcul = 0;

		calcul = CuivreC19400.coefficient_dilatation
				+ getT1() / (getT2() + getT3());
		logger.severe("CTE : " + calcul);

		return calcul;
	}

	private double getEcuEq() {
		double calcul = 0;

		calcul = composant.composant.get("a") / composant.composant.get("pas")
				* composant.composant.get("h");
		logger.severe("Ecu Eq: " + calcul);

		return calcul;
	}

	protected double getE1Traction() {
		double calcul = 0;

		calcul = (get_ea() * getEa()
				+ composant.composant.get("z2") * getSilicium())
				/ composant.composant.get("z");
		logger.severe("E1 traction: " + calcul);

		return calcul;
	}

	private double getPositionCentreDeGraviteAxeZSilicium() {
		double calcul = 0;

		calcul = composant.composant.get("z2") / 2
				+ composant.composant.get("h");
		logger.severe("Position Centre Silicium: " + calcul);

		return calcul;
	}

	private double getPositionCentreDeGraviteAxeZZone1() {
		double calcul = 0;

		calcul = (getPositionCentreDeGraviteAxeZResine()
				* (composant.composant.get("x2")
						* composant.composant.get("y2"))
				* ResineEncapsulation.masse_volumique_k0
				+ getPositionCentreDeGraviteAxeZSilicium()
						* (composant.composant.get("x2")
								* composant.composant.get("y2"))
						* Silicium.masse_volumique_k0
				+ getPositionCentreDeGraviteAxeZLeadframe()
						* (composant.composant.get("x2")
								* composant.composant.get("y2"))
						* CuivreC19400.masse_volumique)
				/ ((composant.composant.get("x2")
						* composant.composant.get("y2"))
						* (ResineEncapsulation.masse_volumique_k0
								+ CuivreC19400.masse_volumique
								+ Silicium.masse_volumique_k0));
		logger.severe("Position Centre Axe Zone 1 : " + calcul);

		return calcul;
	}

	private double getPositionCentreDeGraviteAxeZResine() {
		double calcul = 0;

		calcul = composant.composant.get("z3") / 2
				+ composant.composant.get("h") + composant.composant.get("z2");
		logger.severe("Position centre de gravite resine: " + calcul);

		return calcul;
	}

	private double getMomentQuadratiqueResine() {
		double calcul = 0;

		calcul = composant.composant.get("x2")
				* Math.pow(composant.composant.get("z3"), 3) / 12
				+ composant.composant.get("x2") * composant.composant.get("z3")
						* Math.pow(
								Math.abs(
										getPositionCentreDeGraviteAxeZZone1()
												- getPositionCentreDeGraviteAxeZResine()),
								2);
		logger.severe("Moment quadratique resine: " + calcul);

		return calcul;
	}

	private double getPositionCentreDeGraviteAxeZLeadframe() {
		double calcul = 0;

		calcul = composant.composant.get("h") / 2;
		logger.severe("Position Centre de Gravite Leadframe: " + calcul);

		return calcul;
	}

	protected double getE1Flexion() {
		double calcul = 0;

		calcul = (ResineEncapsulation.module_elasticite_k0
				* getMomentQuadratiqueResine()
				+ Silicium.module_elasticite_k0
						* getPositionCentreDeGraviteAxeZSilicium()
				+ CuivreC19400.module_elasticite
						* getPositionCentreDeGraviteAxeZLeadframe())
				/ getPositionCentreDeGraviteAxeZZone1();
		logger.severe("E1 flexion: " + calcul);

		return calcul;

	}

	protected double getE2Traction() {
		double calcul = 0;

		calcul = (CuivreC19400.module_elasticite * getEcuEq()
				+ (composant.composant.get("z3") - getEcuEq())
						* ResineEncapsulation.module_elasticite_k0)
				/ (composant.composant.get("z3"));
		logger.severe("E2 flexion: " + calcul);

		return calcul;
	}

	private double getMomentQuadratiqueZone2() {
		double calcul = 0;

		calcul = getMomentQuadratiqueResineZone2()
				+ getMomentQuadratiqueLeadframeZone2();

		return calcul;
	}

	private double getPositionCentreDeGraviteAxeZResineZone2() {
		double calcul = 0;

		calcul = getEcuEq() + (composant.composant.get("z") - getEcuEq()) / 2;

		return calcul;
	}

	private double getPositionCentreDeGraviteAxeZZone2() {
		double calcul = 0;

		calcul = (getPositionCentreDeGraviteAxeZResineZone2()
				* (composant.composant.get("z") - getEcuEq())
				* ResineEncapsulation.masse_volumique_k0
				+ getPositionCentreDeGraviteAxeZLeadframeZone2() * getEcuEq()
						* CuivreC19400.masse_volumique)
				/ ((composant.composant.get("h") - getEcuEq())
						* ResineEncapsulation.masse_volumique_k0
						+ getEcuEq() * CuivreC19400.masse_volumique);

		return calcul;
	}

	private double getPositionCentreDeGraviteAxeZLeadframeZone2() {
		double calcul = 0;

		calcul = getEcuEq() / 2;

		return calcul;
	}

	private double getMomentQuadratiqueLeadframeZone2() {
		double calcul = 0;

		calcul = composant.composant.get("x") * Math.pow(getEcuEq(), 3) / 12
				+ getEcuEq() * Math.pow(
						Math.abs(
								getPositionCentreDeGraviteAxeZZone2()
										- getPositionCentreDeGraviteAxeZLeadframeZone2()),
						2);

		return calcul;
	}

	private double getMomentQuadratiqueResineZone2() {
		double calcul = 0;

		calcul = composant.composant.get("x")
				* Math.pow(composant.composant.get("h") - getEcuEq(), 2) / 12
				+ composant.composant.get("x")
						* (composant.composant.get("h") - getEcuEq())
						* Math.pow(
								Math.abs(
										getPositionCentreDeGraviteAxeZZone2()
												- getPositionCentreDeGraviteAxeZResineZone2()),
								2);

		return calcul;
	}

	protected double getE2Flexion() {
		double calcul = 0;

		calcul = (ResineEncapsulation.module_elasticite_k0
				* getMomentQuadratiqueResine()
				+ CuivreC19400.module_elasticite * getEcuEq())
				/ getMomentQuadratiqueZone2();
		logger.severe("E2 flexion: " + calcul);

		return calcul;
	}

	protected double getCte12() {
		double calcul = 0;

		calcul = (getCte1() * composant.composant.get("x2")
				+ getCte2() * (composant.composant.get("x")
						- composant.composant.get("x2")))
				/ composant.composant.get("x");
		logger.severe("CTE 1 2: " + calcul);
		calcul = Double.valueOf(Outils.StringWithCommaToDouble(Outils.nombreChiffreScientifiqueApresVirgule(calcul)));
		return calcul;
	}

	protected double getE12() {
		double calcul = 0;

		calcul = getE1Traction() * getE2Traction()
				* composant.composant.get("x")
				/ (getE1Traction()
						* (composant.composant.get("x")
								- composant.composant.get("x2"))
						+ getE2Traction() * composant.composant.get("x2"));
		logger.severe("E 1 2: " + calcul);
		
		return (double)(int)calcul;
	}

	protected double getCte2() {
		double calcul = 0;

		calcul = (getEcuEq() * CuivreC19400.coefficient_dilatation
				* CuivreC19400.module_elasticite
				+ (composant.composant.get("z3") - getEcuEq()) * getResine()
						* ResineEncapsulation.module_elasticite_k0)
				/ (getEcuEq() * CuivreC19400.module_elasticite
						+ (composant.composant.get("z3") - getEcuEq())
								* ResineEncapsulation.module_elasticite_k0);
		logger.severe("CTE 2: " + calcul);

		return calcul;

	}

	public DoubleProperty getE1() {
		return e1;
	}

	public DoubleProperty getCte() {
		return cte;
	}

	public String toString() {
		return getTemperature() + ";" + e1.get() + ";" + cte.get();
	}

}

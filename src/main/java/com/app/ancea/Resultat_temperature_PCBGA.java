package com.app.ancea;

/**
 * 
 * Cette classe calcule les paramètres de traction, de flexion et dilatation des
 * composants de la famille des BGA.
 *
 */
public class Resultat_temperature_PCBGA extends Resultat_temperature {

	ComposantPCBGA composant;

	double e1_traction;
	double e1_flexion;
	double cte1;

	public Resultat_temperature_PCBGA(
			ComposantPCBGA composant,
			double temperature) {
		super(composant, temperature);

		this.composant = composant;

		e1_traction = 0;

		e1_flexion = 0;

		cte1 = 0;

		setTemperature(temperature);

	}

	public void calculs() {
		calcul_cte1();
		calcul_e1_flexion();
		calcul_e1_traction();

	}

	/**
	 * @return Le module de flexion de la zone 1.
	 */
	public double calcul_e1_flexion() {
		double calcul;
		calcul = composant.pcb.calcul_flexion_ciu(getTemperature());
		int resultat = (int) calcul;
		setE1_flexion(resultat);
		return calcul;

	}

	public double calcul_e1_traction() {
		double calcul;
		calcul = composant.pcb.calcul_traction_ciu(getTemperature());
		int resultat = (int) calcul;
		setE1_traction(resultat);

		return calcul;
	}

	/**
	 * @return Le coefficient de dilatation de la zone 1.
	 */
	public double calcul_cte1() {
		double calcul;
		calcul = composant.pcb.calcul_coef_dil_xy_ciu(getTemperature());
		setCte1(calcul);

		return calcul;// attention à la conversion d'unité
	}

	public double getE1_traction() {
		return e1_traction;
	}

	public void setE1_traction(double e1_traction) {
		this.e1_traction = e1_traction;
	}

	public double getE1_flexion() {
		return e1_flexion;
	}

	public void setE1_flexion(double e1_flexion) {
		this.e1_flexion = e1_flexion;
	}

	public double getCte1() {
		return cte1;
	}

	public void setCte1(double cte1) {
		this.cte1 = cte1;
	}

	public double get_double_temperature() {
		return getTemperature();
	}

	public void calcul() {
		try {
			calcul_e1_flexion();
			calcul_e1_traction();
			calcul_cte1();
		} catch (NullPointerException e) {
		}
	}

	public String toString() {
		return getTemperature() + ";" + getE1_traction() + ";" + getE1_flexion()
				+ ";" + getCte1();
	}

}

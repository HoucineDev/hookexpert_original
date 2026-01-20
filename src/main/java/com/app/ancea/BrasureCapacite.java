package com.app.ancea;

import javafx.scene.image.ImageView;

public class BrasureCapacite extends BrasureResistance {

	static ImageView image = new ImageView(
			BrasureLCC.class.getResource("images/CAPACITE_BRASURE.png")
					.toExternalForm());

	protected String materiau;
	protected double valeurCapacite;

	public BrasureCapacite() {
		super();
	}

	public BrasureCapacite(String path) {
		super(path);
		this.composant = Capacite.getCapacite(format);
	}

	/**
	 * @return Le matériau avec lequel est fait la capacité.
	 */
	public String getMateriau() {
		return this.materiau;
	}

	/**
	 * 
	 * @param materiau Le matériau avec lequel est fait la capacité.
	 */
	public void setMateriau(String materiau) {
		this.materiau = materiau;
	}

	public Double getvaleurCapacite() {
		return this.valeurCapacite;
	}

	public void setvaleurCapacite(double valeur) {
		this.valeurCapacite = valeur;
	}

	@Override
	public void setFormat(String format) {
		this.format = format;
		composant = Capacite.getCapacite(format);
		setFormat(composant);
	}

	@Override
	public double getSc() {
		double calcul = 0;
		calcul = (composant.get("a") * composant.get("b")
				+ (2 * composant.get("b") + composant.get("a")) * getB2()) / 2;
		return calcul;
	}

	@Override
	public double getHxH1() {
		double calcul = 0;
		if (Math.sqrt(
				2 * getV2()
						/ (composant.get("a")
								+ 2 * composant.get("b"))) < composant
										.get("h")) {
			calcul = Math.sqrt(
					2 * getV2()
							/ (composant.get("a") + 2 * composant.get("b")));
		} else {
			calcul = composant.get("h") + getH1();
		}

		return calcul;
	}

	@Override
	public double getCoefficientDePoisson() {
		return Capacite.coefficient_poisson;
	}

	@Override
	public void setCalculs() {
		calcul_heq();
		calcul_sc();
		calcul_Hi();
	}

	@Override
	public String toString() {
		return format + ";" + as + ";" + bs + ";" + es + ";" + a1 + ";" + b1
				+ ";" + c1 + ";" + materiau + ";" + valeurCapacite;
	}
}

package com.app.ancea;

import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;

/**
 * Cette classe s'interface avec la classe DDV.java, elle agit comme un
 * controller. Elle permet de gerer la duree de vie. Pour fonctionner la classe
 * a besoin de deux objets ddv un pour chaque temperature/temps de l'assemblage
 * electronique.
 * 
 * TODO: Remplacer les t1 d1 etc.. par un objet cycle thermique.
 */
public class ProfilDeVie {

	private static final Logger logger = Logger.getLogger(Main.class.getName());

	private DDV<Composant, BrasureComposant> ddv1;
	private DDV<Composant, BrasureComposant> ddv2;

	private String t1, t2, d1, d2, n50, dommages, occurences;
	private double x1t1, x1t2, x2t1, x2t2;
	private Composant composant;
	private BrasureComposant brasure;
	private String PCB;
	private boolean matrice_creuse;

	public ProfilDeVie(
			double t1,
			double t2,
			double d1,
			double d2,
			double occurences,
			String PCB,
			Composant c,
			BrasureComposant b) {
		this.t1 = String.valueOf(t1);
		this.t2 = String.valueOf(t2);
		this.d1 = String.valueOf(d1);
		this.d2 = String.valueOf(d2);
		this.n50 = "0.0";
		this.occurences = String.valueOf(occurences);

		this.composant = c;
		this.brasure = b;

		this.PCB = PCB;

		System.out.println("Creating DDV instances with PCB: "+ PCB +"; t1 = " + t1 + "; t2 = " + t2 + "; d1 = " + d1 + "; d2 = " + d2);

		ddv1 = new DDV<>(t1, d1, this.PCB, c, b);
		ddv2 = new DDV<>(t2, d2, this.PCB, c, b);
	}

	public boolean isMatrice_creuse() {
		return matrice_creuse;
	}

	public void setMatrice_creuse(boolean matrice_creuse) {
		this.matrice_creuse = matrice_creuse;
		ddv1.setMatrice_creuse(matrice_creuse);
		ddv2.setMatrice_creuse(matrice_creuse);
	}

	public double getX1t1() {
		return x1t1;
	}

	public void setX1t1(double x1t1) {
		this.x1t1 = x1t1;
		ddv1.setX1t1(x1t1);
	}

	public double getX1t2() {
		return x1t2;
	}

	public void setX1t2(double x1t2) {
		this.x1t2 = x1t2;
		ddv1.setX1t2(x1t2);
	}

	public double getX2t1() {
		return x2t1;
	}

	public void setX2t1(double x2t1) {
		this.x2t1 = x2t1;
		ddv2.setX1t1(x2t1);
	}

	public double getX2t2() {
		return x2t2;
	}

	public void setX2t2(double x2t2) {
		this.x2t2 = x2t2;
		ddv2.setX1t2(x2t2);
	}

	public double getN50() {
		System.out.println("[DureeDeVie] n50 : " + n50);
		double yc12 = ddv2.getCalculs();
		System.out.println("[DureeDeVie] yc12 : " + yc12);

		if (composant instanceof ComposantLeadless || composant instanceof ComposantLeadframe) {
			System.out.println("[DureeDeVie] (1) {ComposantLeadless || ComposantLeadframe): " + ddv1.getN50(yc12));
			return ddv1.getN50(yc12);
		} else {
			System.out.println("[DureeDeVie] (2):  " + ddv1.calcul_cycle_bille_angles_pastille(yc12));
			return ddv1.calcul_cycle_bille_angles_pastille(yc12);

		}
	}

	/**
	 * 
	 * @return La valeur de l'angle de cisaillement palier 1.
	 */
	public double getyc11() {
		return ddv1.getCalculs();
	}

	/**
	 * 
	 * @return La valeur de l'angle de cisaillement palier 2.
	 */
	public double getyc12() {
		return ddv2.getCalculs();
	}

	/**
	 * 
	 * @return La hauteur intégrée de la brasure.
	 */
	public double getHauteurIntegree() {
		return this.brasure.getHi();
	}

	public double getNombreXComposantABilles() {
		return ddv1.getNombreXComposantABilles();
	}

	public double getLnp1ComposantABilles() {
		return ddv1.getLnp1ComposantABilles();
	}

	public double getLnp1Autre() {
		return ddv1.lnp1();
	}

	public void setN50() {
		this.n50 = String.valueOf(getN50());
	}

	public SimpleStringProperty getDommages() {
		double n50 = getN50();
		double dommage = Double.valueOf(occurences) / n50;
		return new SimpleStringProperty(String.valueOf(dommage));
	}

	public void setDommages(String dommages) {
		this.dommages = dommages;
	}

	public String getOccurences() {
		return occurences;
	}

	public void setOccurences(String occurences) {
		this.occurences = occurences;
	}

	public String getT1() {
		return t1;
	}

	public void setT1(double t1) {
		this.t1 = String.valueOf(t1);
		ddv1.setTemperature(t1);
	}

	public String getT2() {
		return t2;
	}

	public void setT2(double t2) {
		this.t2 = String.valueOf(t2);
		ddv2.setTemperature(t2);
	}

	public String getD1() {
		return d1;
	}

	public void setD1(double d1) {
		this.d1 = String.valueOf(d1);
		this.ddv1.setDuree(d1);
	}

	public String getD2() {
		return d2;
	}

	public void setD2(double d2) {
		this.d2 = String.valueOf(d2);
		this.ddv2.setDuree(d2);
	}

	public String toString() {
		return t1 + ";" + d1 + ";" + t2 + ";" + d2 + ";" + occurences + ";"
				+ getN50() + ";" + getDommages().getValue();
	}

}

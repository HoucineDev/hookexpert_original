package com.app.ancea;

/**
 * Cette classe contient les propriétés physiques du matériau Cuivre C19400.
 * 
 * @author Daniel
 * 
 *
 */
public class CuivreC19400 extends ModelPhysiqueStatique {

	static final double masse_volumique = 8.9;
	static final double conductivite_thermique = 260.0;
	static final double module_elasticite = 121000;
	static final double module_cisaillement = 45489;
	static final double coefficient_poisson = 0.33;

	static final double coefficient_dilatation = 1.67E-05;

	public CuivreC19400() {
		super();
	}

	public double masse_volumique(double temperature) {
		double liste[] = {
				CuivreC19400.masse_volumique
		};
		return super.calcul(liste, temperature);
	}

	public double conductivite_thermique(double temperature) {
		double liste[] = {
				CuivreC19400.conductivite_thermique
		};
		return super.calcul(liste, temperature);
	}

	public double module_cisaillement(double temperature) {
		double liste[] = {
				CuivreC19400.module_cisaillement
		};
		return super.calcul(liste, temperature);
	}

	public double module_elasticite(double temperature) {
		double liste[] = {
				CuivreC19400.module_elasticite
		};
		return super.calcul(liste, temperature);
	}

	public double coefficient_poisson(double temperature) {
		double liste[] = {
				CuivreC19400.coefficient_poisson
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_xy(double temperature) {
		double liste[] = {
				CuivreC19400.coefficient_dilatation
		};
		return super.calcul(liste, temperature);
	}

}

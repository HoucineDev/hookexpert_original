package com.app.ancea;

/**
 * 
 * Cette classe contient les propriétés physiques du matériau Cuivre C19400.
 *
 */
public class Cuivre extends ModelPhysiqueStatique {

	static final double masse_volumique_k0 = 8.94;

	static final double conductivite_thermique_k0 = 384;

	static final double module_elasticite_k0 = 110000;
	static final double module_elasticite_k1 = 0;
	static final double module_elastixite_k2 = 0;

	static final double coefficient_poisson_k0 = 0.33;

	static final double coefficient_dilatation_xy_k0 = 1.70E-05;

	static final double coefficient_dilatation_z_k0 = 1.70E-05;

	public Cuivre() {
		super();
	}

	public double masse_volumique(double temperature) {
		double liste[] = {
				Cuivre.masse_volumique_k0
		};
		return super.calcul(liste, temperature);
	}

	public double conductibilite(double temperature) {
		double liste[] = {
				Cuivre.conductivite_thermique_k0
		};
		return super.calcul(liste, temperature);
	}

	public double module_elastique(double temperature) {
		double liste[] = {
				Cuivre.module_elasticite_k0
		};
		return super.calcul(liste, temperature);
	}

	public double coef_poisson(double temperature) {
		double liste[] = {
				Cuivre.coefficient_poisson_k0
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_xy(double temperature) {
		double liste[] = {
				Cuivre.coefficient_dilatation_xy_k0
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_z(double temperature) {
		double liste[] = {
				Cuivre.coefficient_dilatation_z_k0
		};
		return super.calcul(liste, temperature);
	}

}

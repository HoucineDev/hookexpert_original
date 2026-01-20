package com.app.ancea;

/**
 * 
 * Cette classe contient les propriétés physiques du matériau VerreE.
 *
 */
public class VerreE extends ModelPhysiqueStatique {

	static final double masse_volumique_k0 = 2.6;

	static final double conductivite_thermique_k0 = 1.0;

	static final double module_elasticite_k0 = 52257; /* 73596 */
	static final double module_elasticite_k1 = -39.605; /* -50.58 */
	static final double module_elasticite_k2 = -0.0051; /* -0.0441 */

	static final double coefficient_poisson_k0 = 0.22;

	static final double coefficient_dilatation_xy_k0 = 5.30E-06;

	static final double coefficient_dilatation_z_k0 = 5.30E-06;

	public VerreE() {
		super();
	}

	public double masse_volumique(double temperature) {
		double liste[] = {
				VerreE.masse_volumique_k0
		};
		return super.calcul(liste, temperature);
	}

	public double conductibilite(double temperature) {
		double liste[] = {
				VerreE.conductivite_thermique_k0
		};
		return super.calcul(liste, temperature);
	}

	public double module_elastique(double temperature) {
		double liste[] = {
				VerreE.module_elasticite_k0, VerreE.module_elasticite_k1,
				VerreE.module_elasticite_k2
		};
		return super.calcul(liste, temperature);
	}

	public double coef_poisson(double temperature) {
		double liste[] = {
				VerreE.coefficient_poisson_k0
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_xy(double temperature) {
		double liste[] = {
				VerreE.coefficient_dilatation_xy_k0
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_z(double temperature) {
		double liste[] = {
				VerreE.coefficient_dilatation_z_k0
		};
		return super.calcul(liste, temperature);
	}

}

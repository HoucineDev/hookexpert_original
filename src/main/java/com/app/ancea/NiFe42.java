package com.app.ancea;

/**
 * Cette classe contient les propriétés physiques du matériau NiFe42.
 * 
 * @author Daniel
 * 
 */
public class NiFe42 extends ModelPhysiqueStatique {

	static final double masse_volumique = 8.12;
	static final double conductivite_thermique = 15.0;
	static final double module_elasticite = 1.47E05;
	static final double coefficient_poisson = 0.34;

	static final double coefficient_dilatation = 4.35E-06;

	public NiFe42() {
		super();
	}

	public double masse_volumique(double temperature) {
		double liste[] = {
				NiFe42.masse_volumique
		};
		return super.calcul(liste, temperature);
	}

	public double conductivite_thermique(double temperature) {
		double liste[] = {
				NiFe42.conductivite_thermique
		};
		return super.calcul(liste, temperature);
	}

	public double module_elasticite(double temperature) {
		double liste[] = {
				NiFe42.module_elasticite
		};
		return super.calcul(liste, temperature);
	}

	public double coefficient_poisson(double temperature) {
		double liste[] = {
				NiFe42.coefficient_poisson
		};
		return super.calcul(liste, temperature);
	}

	public double coefficient_dilatation_xy(double temperature) {
		double liste[] = {
				NiFe42.coefficient_dilatation
		};
		return super.calcul(liste, temperature);
	}

}

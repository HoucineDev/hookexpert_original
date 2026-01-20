package com.app.ancea;

import java.util.HashMap;

import javafx.beans.property.DoubleProperty;

public class ComposantLeadless extends Composant {

	protected DoubleProperty a1, b1,
			c1; /* Valeurs correspondantes à la plage de brasage */

	protected DoubleProperty as, bs,
			es; /* Valeurs correspondates à l'écran sérigraphe */

	protected DoubleProperty d, a, b, h, g, S,
			h1; /* Valeures correspondantes aux dimensions du composant. */

	protected HashMap<String, Double> composant;

	public ComposantLeadless(String nom) {
		super(nom);

	}

	public ComposantLeadless() {
		super();

	}

	protected double rgm(int t) {
		return (Double) null;
	}

	public DoubleProperty getA1() {
		return a1;
	}

	public void setA1(double a1) {
		this.a1.set(a1);
	}

	public DoubleProperty getB1() {
		return b1;
	}

	public void setB1(double b1) {
		this.b1.set(b1);
	}

	public DoubleProperty getC1() {
		return c1;
	}

	public void setC1(double c1) {
		this.c1.set(c1);
	}

	public DoubleProperty getAs() {
		return as;
	}

	public void setAs(double as) {
		this.as.set(as);
	}

	public DoubleProperty getBs() {
		return bs;
	}

	public void setBs(double bs) {
		this.bs.set(bs);
	}

	public DoubleProperty getEs() {
		return es;
	}

	public void setEs(double es) {
		this.es.set(es);
	}

	public DoubleProperty getD() {
		return d;
	}

	public void setD(double d) {
		this.d.set(d);
	}

	public DoubleProperty getA() {
		return a;
	}

	public void setA(double a) {
		this.a.set(a);
	}

	public DoubleProperty getB() {
		return b;
	}

	public void setB(double b) {
		this.b.set(b);
	}

	public DoubleProperty getH() {
		return h;
	}

	public void setH(double h) {
		this.h.set(h);
	}

	public DoubleProperty getG() {
		return g;
	}

	public void setG(double g) {
		this.g.set(g);
	}

	public DoubleProperty getS() {
		return S;
	}

	public void setS(double S) {
		this.S.set(S);
	}

	public DoubleProperty getH1() {
		return h1;
	}

	public void setH1(double h1) {
		this.h1.set(h1);
	}

	@Override
	protected void setProprietes() {
		if (composant != null) {
			for (String nomPropriete : composant.keySet()) {
				double valeur = composant.get(nomPropriete);
				Propriete p = new Propriete(nomPropriete);
				p.set_resultat(String.valueOf(valeur));
				proprietes.add(p);
			}
			tv_proprietes.setItems(proprietes);
			tv_proprietes.refresh();
		}
	}

	@Override
	public HashMap<String, Double> getComposant() {
		return composant;
	}

	public void setDimensionComposant(double[] formatResistance) {
		this.d.set(formatResistance[0]);
		this.a.set(formatResistance[1]);
		this.b.set(formatResistance[2]);
		this.h.set(formatResistance[3]);
		this.g.set(formatResistance[4]);
		this.S.set(formatResistance[5]);
		this.h1.set(formatResistance[6]);

	}

	protected double getX() {
		return (Double) null;
	}

	protected double getY() {
		return (Double) null;
	}

	@Override
	public double getCte(double temperature) {
		// TODO Auto-generated method stub
		return (Double) null;
	}

}

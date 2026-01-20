package com.app.ancea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class BrasureComposantLeadframe extends BrasureComposant {

	public Propriete heq, Sc, Hi, gainSmdNsmd;
	
	protected HashMap <String, Double> composant;
	
	protected String format, technologie;

	protected double a1, b1, c1;
	protected double as, bs, es;
	
	protected final double masse_volumique = 0;
	protected final double module_elasticite = 0;
	protected final double condutibilite_thermique = 0;
	protected final double coefficient_de_poisson = 0;
	protected final double coefficient_de_dilatation = 0;

	public BrasureComposantLeadframe() {
		super();

		heq = new Propriete("Hauteur equivalente");
		Sc = new Propriete("Section critique");
		Hi = new Propriete("Hauteur intégrée");
		gainSmdNsmd = new Propriete("Gain smd nsmd");

		technologie = "";

	}
	
	public BrasureComposantLeadframe(String path) {
		
		heq = new Propriete("Hauteur equivalente");
		Sc = new Propriete("Section critique");
		Hi = new Propriete("Hauteur intégrée");
		gainSmdNsmd = new Propriete("Gain smd nsmd");

		technologie = "";
		
		try ( FileReader fr = new FileReader(path);
				BufferedReader br = new BufferedReader(fr)) {
			
			String line = br.readLine();
			
			String[] parametres = line.split(";");
	
			format = parametres[0];
			
			setAs(Double.valueOf(parametres[1]));
			setBs(Double.valueOf(parametres[2]));
			setEs(Double.valueOf(parametres[3]));
			setA1(Double.valueOf(parametres[4]));
			setB1(Double.valueOf(parametres[5]));
			setC1(Double.valueOf(parametres[6]));
			infoComposant = "technologie: "+technologie+"\n as: "+as+" bs: "+bs+" Es: "+es+"\n a1: "+a1+" b1: "+b1+" c1: "+c1;
		}
		

		catch(IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public String getTechnologie() {
		return technologie;
	}
	
	public void setTechnologie(String technologie) {
		this.technologie = technologie;
	}
	
	public void setFormat(String format) {
		
	}
	
	public HashMap<String, Double> getComposant() {
		return composant;
	}
	
	public String getFormat() {
		return format;
	}

	public double getMasseVolumique() {
		return masse_volumique;
	}

	public double getCondutibiliteThermique() {
		return condutibilite_thermique;
	}

	public double getModuleElasticite() {
		return module_elasticite;
	}

	public double getCoefficientDePoisson() {
		return coefficient_de_poisson;
	}

	public double getCoefficientDeDilatation() {
		return coefficient_de_dilatation;
	}

	public double getC1() {
		return c1;
	}

	public double getAs() {
		return as;
	}

	public double getBs() {
		return bs;
	}

	public double getEs() {
		return es;
	}

	public void setA1(double A1) {
		a1 = A1;
	}

	public double getA1() {
		return a1;
	}

	public void setB1(double B1) {
		b1 = B1;
	}

	public double getB1() {
		return b1;
	}

	public void setC1(double C1) {
		c1 = C1;
	}

	public void setAs(double as) {
		this.as = as;
	}

	public void setBs(double bs) {
		this.bs = bs;
	}

	public void setEs(double es) {
		this.es = es;
	}

	protected void calcul_heq() {
	}

	protected void calcul_sc() {
	}

	protected void calcul_Hi() {
	}

	protected void setCalculs() {
		
	}
	

	public double getSc() {
		return Double.valueOf(Sc.get_resultat().get());
	}
	
	public void sauvegarderComposant(String nomFichier) {
		
	}
	
	public String toString() {
		return format+";"+as+";"+bs+";"+es+";"+a1+";"+b1+";"+c1+";"+technologie;
	}

}

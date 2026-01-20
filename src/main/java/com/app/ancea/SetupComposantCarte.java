package com.app.ancea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Cette classe permet de calculer les proprietes d'un composant sur un PCB.
 * Avec cette classe on recupere le dommage total d'un composant par rapport à
 * un profil de vie issus d'un profil de vie, les beta etc. et on calcul la
 * probabilite de defaillance et la densite de probabilite pour un nombre de
 * mois.
 *
 */
public class SetupComposantCarte {

	private String cheminPCB, cheminProfilDevie, nomComposant;
	private Pcb pcb;
	private OngletPcb ongletPcb;
	private CycleThermique cycleThermique;
	private double n50, dommageTotal, vernis, beta, mois;
	private int nombreComposant;
	private boolean booleanVernis;

	public SetupComposantCarte(
			String cheminProfilDeVie,
			String cheminPCB,
			String cheminBrasure,
			int nombreComposant,
			CycleThermique cycle,
			double mois,
			boolean vernis) {

		this.cheminPCB = cheminPCB;
		this.cheminProfilDevie = cheminProfilDeVie;
		this.cycleThermique = cycle;
		this.mois = mois;
		this.nombreComposant = nombreComposant;
		this.nomComposant = "";
		this.booleanVernis = vernis;

		initPcb();
		initComposant(cycle, 0, cheminPCB, cheminBrasure, cheminProfilDeVie);

	}

	private void initPcb() {
		this.pcb = new Pcb();
		this.ongletPcb = new OngletPcb();
		this.ongletPcb.init_pcb();
		this.ongletPcb.load_circuit(pcb, cheminPCB);
	}

	private double getDommageTotal(
			String pathProfil,
			Composant composant,
			BrasureComposant brasure,
			String PCB) {

		try {
			FileReader fr = new FileReader(pathProfil);

			BufferedReader br = new BufferedReader(fr);

			String line;

			double dommage = 0;

			while ((line = br.readLine()) != null) {
				String[] strings = line.split(";");
				ProfilDeVie pdv = new ProfilDeVie(
						Double.valueOf(strings[0]),
						Double.valueOf(strings[2]),
						Double.valueOf(strings[1]),
						Double.valueOf(strings[3]),
						Double.valueOf(strings[4]),
						PCB,
						composant,
						brasure);
				dommage += Double.valueOf(pdv.getDommages().getValue());

			}
			setDommageTotal(dommage);
			return dommage;
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		return Double.NaN;

	}

	/**
	 * @param cycle             Cycle thermique de reference d'un composant
	 * @param occurences        le nombre de fois que le composant va subir ce
	 *                          cycle
	 * @param PCB               String represente le chemin du PCB
	 * @param nomBrasure        nom de la brasure
	 * @param cheminProfilDeVie chemin du profil de vie
	 */
	private void initComposant(
			CycleThermique cycle,
			double occurences,
			String PCB,
			String nomBrasure,
			String cheminProfilDeVie) {

		BrasureComposant brasure;
		Composant composant;

		nomComposant = nomBrasure;

		/*
		 * instancie la brasure et le composant et mets a jour le vernis et le
		 * beta
		 */

		if (nomBrasure.contains(".brbga")) {
			OngletPcb onglet = new OngletPcb();
			onglet.init_pcb();
			brasure = new BrasureComposantBilles(
					Ressources.pathBrasures + nomBrasure);
			composant = new ComposantBGA(
					onglet,
					Ressources.pathComposants
							+ ((BrasureComposantBilles) brasure).getNom()
							+ ".bga");
			setBeta(ComposantBGA.beta);
			setVernis(ComposantBGA.vernis);

		} else if (nomBrasure.contains(".brcsp")) {
			OngletPcb onglet = new OngletPcb();
			onglet.init_pcb();
			brasure = new BrasureComposantBilles(
					Ressources.pathBrasures + nomBrasure);

			composant = new ComposantCSP(
					onglet,
					Ressources.pathComposants
							+ ((BrasureComposantBilles) brasure).getNom()
							+ ".csp");
			setBeta(ComposantCSP.beta);
			setVernis(ComposantCSP.vernis);

		} else if (nomBrasure.contains(".brwlp")) {
			brasure = new BrasureComposantBilles(
					Ressources.pathBrasures + nomBrasure);
			composant = new ComposantWLP();
			((ComposantWLP) composant).charger_wlp(
					Ressources.pathComposants
							+ ((BrasureComposantBilles) brasure).getNom()
							+ ".wlp");
			setBeta(ComposantWLP.beta);
			setVernis(ComposantWLP.vernis);

		} else if (nomBrasure.contains(".brcap")) {
			brasure = new BrasureCapacite(Ressources.pathBrasures + nomBrasure);
			((BrasureCapacite) brasure).setCalculs();
			composant = new Composant_Capacite(
					((BrasureCapacite) brasure).getNom(),
					((BrasureCapacite) brasure).getMateriau(),
					((BrasureCapacite) brasure).getValeurCapacite());
			setBeta(Capacite.beta);
			setVernis(Capacite.vernis);

		} else if (nomBrasure.contains(".brres")) {
			brasure = new BrasureResistance(
					Ressources.pathBrasures + nomBrasure);
			((BrasureResistance) brasure).setCalculs();
			composant = new Composant_Resistance(
					((BrasureResistance) brasure).getNom());
			setBeta(Resistance.BETA);
			setVernis(Resistance.VERNIS);
		}

		else if (nomBrasure.contains(".brlcc")) {
			brasure = new BrasureLCC(Ressources.pathBrasures + nomBrasure);
			((BrasureLCC) brasure).setCalculs();
			composant = new ComposantLCC(((BrasureLCC) brasure).getNom(),Ressources.pathComposants+((BrasureLCC) brasure).getNom()+".lcc");
			setBeta(LCC.beta);
			setVernis(LCC.vernis);
		}

		else {
			brasure = new BrasureQFN(Ressources.pathBrasures + nomBrasure);
			((BrasureQFN) brasure).setCalculs();
			composant = new ComposantQFN(((BrasureQFN) brasure).getFormat());
			setBeta(QFN.beta);
			setVernis(QFN.vernis);
		}

		ProfilDeVie pdv = new ProfilDeVie(
				cycle.getTemperature1(),
				cycle.getTemperature2(),
				cycle.getDuree1(),
				cycle.getDuree2(),
				occurences,
				PCB,
				composant,
				brasure);

		setDommageTotal(
				getDommageTotal(cheminProfilDeVie, composant, brasure, PCB));

		setN50(pdv.getN50());

	}

	protected double getN63() {
		double n50 = getN50();
		double vernis = getVernis();
		double beta = getBeta();

//		System.out.println(String.format("beta %s, vernis  %s", beta, vernis));

		double calcul = 0;

		if (booleanVernis) {
			calcul = n50 * vernis * Math
					.pow(Math.log(1 - 0.63) / Math.log(1 - 0.5), 1 / beta);
		} else {
			calcul = n50 * 1 * Math
					.pow(Math.log(1 - 0.63) / Math.log(1 - 0.5), 1 / beta);
		}

		return calcul;
	}

	/**
	 * @param x represente le Nf x %
	 * @return retourne le nombre de cycle a Nx % de défaillance.
	 */
	protected double getNx(double x) {

		double n50 = getN50();
		double vernis = getVernis();
		double beta = getBeta();

		double calcul = 0;

		calcul = n50 * vernis
				* Math.pow(Math.log(1 - x) / Math.log(1 - 0.5), 1 / beta);

		return calcul;
	}

	protected double getD63() {
		double d50 = getDommageTotal();
		double n50 = getN50();
		double n63 = getN63();

//		System.out.println("d50: "+d50);
		double calcul = 0;

		calcul = d50 * n50 / n63;

		return calcul;
	}

	private double getNeq() {
		double n63 = getN63();
		double d63 = getD63();
		double calcul = 0;

//		System.out.println("d63: "+d63);

		calcul = n63 * d63;

		return calcul;
	}

	private double getFa() {
		double neq = getNeq();
//		System.out.println("neq:  "+ neq);
		double calcul = 0;

		calcul = mois / neq;

		return calcul;
	}

	protected double getMu(double Nx) {
		double fa = getFa();

//		System.out.println("n63: "+n63+" fa: "+fa);

		double calcul = Nx * fa;
		return calcul;
	}

	protected double getMu() {
		double n63 = getN63();
		double fa = getFa();

//		System.out.println("n63: "+n63+" fa: "+fa);

		double calcul = n63 * fa;
		return calcul;
	}

	protected double getDensiteProbabilite(int mois) {
		double nombreComposant = getNombreComposant();
		double beta = getBeta();
		double mu = getMu();
//		System.out.println("nombreComposant: "+nombreComposant+" beta: "+beta+" mu: "+mu);
		double calcul = 0;

		calcul = Math.pow(nombreComposant * beta / mu * (mois / mu), beta - 1);
		return calcul;
	}

	protected double getDensiteProbabilite2(int mois) {

		double nombreComposant = getNombreComposant();
		double beta = getBeta();
		double mu = getMu();
		double calcul = 0;

		calcul = (-nombreComposant * (Math.pow(mois / mu, beta)));

		return calcul;
	}

	protected double getProbabiliteDeDefaillance(double mois, double nx) {
		double nombreComposant = getNombreComposant();
		double beta = getBeta();
		double mu = getMu(nx);

		double calcul = (1
				- Math.exp(-nombreComposant * (Math.pow(mois / mu, beta))));
		return calcul;
	}

	protected double getProbabiliteDeDefaillance(int mois) {
		double nombreComposant = getNombreComposant();
		double beta = getBeta();
		double mu = getMu();

		double calcul = (1
				- Math.exp(-nombreComposant * (Math.pow(mois / mu, beta))));
		return calcul;
	}

	public double getN50() {
		return n50;
	}

	public void setN50(double n50) {
		this.n50 = n50;
	}

	public double getDommageTotal() {
		return dommageTotal;
	}

	public void setDommageTotal(double dommageTotal) {
		this.dommageTotal = dommageTotal;
	}

	public double getVernis() {
		return vernis;
	}

	public void setVernis(double vernis) {
		this.vernis = vernis;
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public int getNombreComposant() {
		return nombreComposant;
	}

	public void setNombreComposant(int nombreComposant) {
		this.nombreComposant = nombreComposant;
	}

	public String getNomComposant() {
		return nomComposant;
	}

	public void setNomComposant(String nomComposant) {
		this.nomComposant = nomComposant;
	}

}

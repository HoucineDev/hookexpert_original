package com.app.ancea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javafx.scene.image.ImageView;

public class BrasureComposantBilles extends BrasureComposant
		implements InterfaceBrasureComposant {

	public static ImageView imageNSMD = new ImageView(
			BrasureComposantBilles.class
					.getResource("images/BGA_BRASURE_NSMD.png")
					.toExternalForm());
	public static ImageView imageSMD = new ImageView(
			BrasureComposantBilles.class
					.getResource("images/BGA_BRASURE_SMD.png")
					.toExternalForm());

	Composant composant;

	private double NSMD_d2;
	private double es;
	private double des;

	private double h1, db1;

	protected Propriete d1, dbille;
	protected Propriete hs1, hs2, hb, dr2, Rb, d2;
	protected Propriete hs1_optimisee, hs2Ec2, hb_optimisee, d2_optimisee,
			Rb_optimise, dr2_optimisee;
	protected Propriete section_resistante, hauteur_integree;
	protected Propriete section_resistante_optimisée,
			hauteur_integree_optimisee;
	protected Propriete gain_duree_de_vie;

	private String technologie = "";

	public BrasureComposantBilles() {

		super();

		imageNSMD.setFitHeight(310);
		imageNSMD.setFitWidth(600);

		imageSMD.setFitHeight(280);
		imageSMD.setFitWidth(600);

		d1 = new Propriete("d1");
		dbille = new Propriete("dbille");

		hs1 = new Propriete("hs1");
		hb = new Propriete("hb");
		hs2 = new Propriete("hs2-ec2");
		dr2 = new Propriete("dr2");
		Rb = new Propriete("R b");
		d2 = new Propriete("d2");

		hs2Ec2 = new Propriete("hs2-ec2");
		hs1_optimisee = new Propriete("hs1");
		hb_optimisee = new Propriete("hb");
		d2_optimisee = new Propriete("d2");
		dr2_optimisee = new Propriete("dr2");
		Rb_optimise = new Propriete("Rb");

		section_resistante = new Propriete("Section resistante");
		hauteur_integree = new Propriete("Hauteur integrée");

		section_resistante_optimisée = new Propriete(
				"Section resistance optimisée");
		hauteur_integree_optimisee = new Propriete(
				"Hauteur intégrée optimisée");

		gain_duree_de_vie = new Propriete("Gain estimé de DDV thermomecanique");

	}

	public BrasureComposantBilles(String path) {

		d1 = new Propriete("d1");
		dbille = new Propriete("dbille");

		hs1 = new Propriete("hs1");
		hs2 = new Propriete("hs2");
		hb = new Propriete("hb");
		dr2 = new Propriete("dr2");
		Rb = new Propriete("R b");
		d2 = new Propriete("d2");

		hs2Ec2 = new Propriete("hs2-ec2");
		hs1_optimisee = new Propriete("hs1");
		hb_optimisee = new Propriete("hb");
		d2_optimisee = new Propriete("d2");
		dr2_optimisee = new Propriete("dr2");
		Rb_optimise = new Propriete("Rb");

		section_resistante = new Propriete("Section resistante");
		hauteur_integree = new Propriete("Hauteur integrée");

		section_resistante_optimisée = new Propriete(
				"Section resistance optimisée");
		hauteur_integree_optimisee = new Propriete(
				"Hauteur intégrée optimisée");

		gain_duree_de_vie = new Propriete("Gain estimé de DDV thermomecanique");

		try (FileReader fr = new FileReader(path);
				BufferedReader br = new BufferedReader(fr)) {

			String line = br.readLine();

			String[] parametres = line.split(";");

			if (parametres[1].equals("NSMD")) {

				nom = parametres[0];
				this.technologie = parametres[1];
				this.h1 = Double.valueOf(parametres[2]);
				this.db1 = Double.valueOf(parametres[3]);
				this.d2.set_resultat(parametres[4]);
				this.NSMD_d2 = Double.valueOf(parametres[4]);
				this.es = Double.valueOf(parametres[5]);
				this.des = Double.valueOf(parametres[6]);

				infoComposant = "technologie: " + technologie + "\n h1: " + h1
						+ " db1: " + db1 + "\n es: " + es + " des: " + des;
			} else {

				nom = parametres[0];
				this.technologie = parametres[1];
				this.h1 = Double.valueOf(parametres[2]);
				this.db1 = Double.valueOf(parametres[3]);
				this.es = Double.valueOf(parametres[4]);
				this.des = Double.valueOf(parametres[5]);

				infoComposant = "technologie: " + technologie + " h1: " + h1
						+ " db1: " + db1 + "\nes: " + es + " des: " + des;

			}

		} catch (IOException ie) {
			ie.printStackTrace();
		}

		setCalculs();
	}

	public void setCalculs() {

		calcul_d1();
		calcul_dbille();

		if (technologie.equals("NSMD")) {
			calcul_hs1_nsmd();
			calcul_hs2_nsmd();
			calcul_hb_nsmd();
			calcul_dr2_nsmd();
			calcul_Rb_nsmd();
			calcul_section_resistance_nsmd();
			calcul_hauteur_integree_nsmd();

			calcul_d2_nsmd_optimisee();
			calcul_hs1_nsmd_optimisee();
			calcul_hs2ec2_nsmd_optimisee();
			calcul_hb_nsmd_optimisee();
			calcul_dr2_nsmd_optimisee();
			calcul_Rb_nsmd_optimisee();

			calcul_section_resistante_nsmd_optimisee();
			calcul_hauteur_integree_nsmd_optimisee();

			calcul_gain_duree_de_vie_nsmd();
		} else {
			calcul_hs1_smd();
			calcul_hs2_smd();
			calcul_hb_smd();
			calcul_hb_smd();
			calcul_d2_smd();
			calcul_Rb_smd();
			calcul_section_resistance_smd();
			calcul_hauteur_integree_smd();
			calcul_gain_duree_de_vie_smd();
		}

	}

	@Override
	public double getSc() {
		double calcul = 0;
		calcul = Math.PI * Math.pow(getD1(), 2) / 4.0;
		return calcul;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setEs(double es) {
		this.es = es;
	}

	public void setDes(double dEs) {
		this.des = dEs;
	}

	public void setd2(double d2) {
		this.NSMD_d2 = d2;
	}

	private void calcul_gain_duree_de_vie_smd() {
		double calcul = 0;

		calcul = (getHauteurIntegreeSmd() / getHauteurIntegreeNsmd())
				/ (1.0 / Ressources.COEFFICIENT_FATIGUE_B);
		gain_duree_de_vie.set_resultat(
				String.valueOf(Outils.nombreChiffreApresVirgule(calcul, 2)));
	}

	private void calcul_gain_duree_de_vie_nsmd() {
		double calcul = 0;
		calcul = Math.pow(
				(getHauteurIntegreeNsmd() / getHauteurIntegreeNsmdOptimisee()),
				(1.0 / Ressources.COEFFICIENT_FATIGUE_B));

		calcul = calcul * 100;
		gain_duree_de_vie.set_resultat(
				String.valueOf(Outils.nombreChiffreApresVirgule(calcul, 2)));
	}

	public void setTechnologie(String technologie) {
		this.technologie = technologie;
	}

	public String getTechnologie() {
		return this.technologie;
	}

	public void calcul_d1() {

		d1.set_resultat(
				String.valueOf(Outils.nombreChiffreApresVirgule(getD1(), 2)));
	}

	private double getHbNsmd() {
		return getHs1Nsmd() + getHs2Ec2Nsmd();
	}

	public void calcul_hb_nsmd() {
		hb.set_resultat(String.valueOf(getHbNsmd()));
	}

	public void calcul_section_resistance_nsmd() {
		double calcul = 0;

		calcul = Math.PI * Math.pow(getD1(), 2) / 4.0;
		this.section_resistante.set_resultat(String.valueOf(calcul));
	}

	public void calcul_section_resistance_smd() {
		double calcul = 0;
		calcul = Math.PI * Math.pow(getD1(), 2) / 4.0;
		this.section_resistante.set_resultat(
				String.valueOf(Outils.nombreChiffreApresVirgule(calcul, 2)));
	}

	public void calcul_hauteur_integree_nsmd() {
		this.hauteur_integree.set_resultat(
				String.valueOf(
						Outils.nombreChiffreApresVirgule(
								getHauteurIntegreeNsmd(),
								2)));
	}

	public double getHauteurIntegreeSmd() {
		double calcul = 0;

		calcul = Math.pow(getD1(), 2) / (8 * getRbSmd())
				* (Math.log(
						(getRbSmd() + getHs1Smd()) / (getRbSmd() - getHs1Smd()))
						- Math.log(
								(getRbSmd() - getHs2Smd())
										/ (getRbSmd() + getHs2Smd())))
				+ Ressources.EV1 + Ressources.EV2;

		return calcul;
	}

	public void calcul_hauteur_integree_smd() {

		this.hauteur_integree.set_resultat(
				String.valueOf(
						Outils.nombreChiffreApresVirgule(
								getHauteurIntegreeSmd(),
								2)));
	}

	public double getD1CalculsDimensionnelsDesConnexions() {

		double calcul = Math.sqrt(4 * getH1() * (getDb1() - getH1()));
		return calcul;
	}

	private double getDr2Nsmd() {
		double calcul = 0;
		calcul = 2 * Math.sqrt(
				Math.pow(getDb2Nsmd(), 2) / 4.0
						- Math.pow((this.getHs2Nsmd() - Ressources.EC2), 2));
		return calcul;
	}

	private double getHbSmd() {
		return getHs1Smd() + getHs2Smd();
	}

	public void calcul_hb_smd() {
		hb.set_resultat(String.valueOf(getHbSmd()));
	}

	public void calcul_dr2_nsmd() {

		dr2.set_resultat(
				String.valueOf(
						Outils.nombreChiffreApresVirgule(getDr2Nsmd(), 2)));
	}

	public double getVs1Nsmd() {
		double calcul = 0;
		calcul = Math.PI * getH1() / 6
				* (3 * Math.pow(getD1() / 2.0, 2) + Math.pow(getH1(), 2));
		return calcul;
	}

	public void calcul_dbille() {
		dbille.set_resultat(
				String.valueOf(
						Outils.nombreChiffreApresVirgule(getDbille(), 2)));
	}

	public double getVaNsmd() {
		double calcul = 0;

		calcul = getVs1Nsmd() + (getEes() + Ressources.EV2) * Math.PI
				* Math.pow(getdEs(), 2) / 8.0;

		return calcul;
	}

	public double getdEs() {
		return des;
	}

	public double getEs() {
		return es;
	}

	public double getEes() {
		return getEs();
	}

	public double getD2() {
		return NSMD_d2;
	}

	public double getVs2Nsmd() {
		double calcul = 0;
		calcul = getVaNsmd()
				+ Math.PI * Ressources.EC2 * Math.pow(getD2(), 2) / 4.0;
		return calcul;
	}

	private double getD2Nsmd() {
		return NSMD_d2;
	}

	public double getQ2Nsmd() {
		double calcul = 0;
		calcul = -6 * this.getVs2Nsmd() / Math.PI;
		return calcul;
	}

	public double getHsNsmd() {

		double calcul = 0;
		calcul = Math
				.cbrt(-getQ2Nsmd() / 2.0 - 0.5 * Math.sqrt(getA2Nsmd() / 27.0))
				+ Math.cbrt(
						-getQ2Nsmd() / 2.0
								+ 0.5 * Math.sqrt(getA2Nsmd() / 27.0));

		return calcul;
	}

	public double getA2Nsmd() {
		double calcul = 0;
		calcul = 4.0 * Math.pow(this.getP2Nsmd(), 3)
				+ 27 * Math.pow(this.getQ2Nsmd(), 2);
		return calcul;
	}

	private double getHsNsmdOptimisee() {
		double calcul = 0;

		calcul = getXOptimisee()
				- getBetaOptimisee() / 3.0; /* TODO: CHECK SI CORRECT */

		return calcul;
	}

	public void calcul_hs2_nsmd() {

		hs2.set_resultat(
				String.valueOf(
						Outils.nombreChiffreApresVirgule(getHs2Ec2Nsmd(), 2)));
	}

	public double getHs2Ec2Nsmd() {
		double calcul = 0;

		calcul = getHs2Nsmd() - Ressources.EC2;

		return calcul;
	}

	public double getHs2Nsmd() {
		double calcul = 0;
		calcul = Math.sqrt(
				Math.pow(this.getDb2Nsmd() / 2.0, 2)
						- Math.pow(this.getD2Nsmd() / 2.0, 2));
		return calcul;
	}

	public double getDb2Nsmd() {

		double calcul = 0;
		calcul = 2 * Math.sqrt(
				Math.pow(
						((Math.pow(this.getD1(), 2.0)
								- Math.pow(this.getD2Nsmd(), 2.0))
								/ (8 * this.getHsNsmd()))
								+ this.getHsNsmd() / 2.0,
						2.0) + Math.pow(this.getD2Nsmd(), 2) / 4.0);
		return calcul;
	}

	private double getDb2Smd() {
		double calcul = 0;
		calcul = Math.sqrt(Math.pow(getHsSmd(), 2) + Math.pow(getD1(), 2));
		return calcul;
	}

	public double getHs1Nsmd() {
		double calcul = 0;
		calcul = getHsNsmd() - this.getHs2Nsmd();
		return calcul;
	}

	public void calcul_hs1_nsmd() {
		double calcul = 0;
		calcul = getHsNsmd() - this.getHs2Nsmd();
		hs1.set_resultat(
				String.valueOf(Outils.nombreChiffreApresVirgule(calcul, 2)));
	}

	public double getHs1Smd() {
		double calcul = 0;
		calcul = getHsSmd() / 2;
		return calcul;
	}

	public void calcul_hs1_smd() {
		double calcul = 0;
		calcul = getHsSmd() / 2;
		hs1.set_resultat(
				String.valueOf(Outils.nombreChiffreApresVirgule(calcul, 2)));
	}

	private double getHs2Smd() {
		return Double.valueOf(hs1.get_resultat().get());
	}

	public void calcul_hs2_smd() {
		hs2.set_resultat(hs1.get_resultat().get());
	}

	public void calcul_d2_smd() {
		d2.set_resultat(d1.get_resultat().get());
	}

	private double getRbSmd() {
		double calcul = 0;
		calcul = getDb2Smd() / 2;
		return calcul;
	}

	public void calcul_Rb_smd() {
		double calcul = 0;
		calcul = getDb2Smd() / 2;
		Rb.set_resultat(
				String.valueOf(Outils.nombreChiffreApresVirgule(calcul, 2)));
	}

	private double getRbNsmd() {
		double calcul = 0;

		calcul = getDb2Nsmd() / 2;

		return getDb2Nsmd() / 2;
	}

	public void calcul_Rb_nsmd() {
		Rb.set_resultat(
				String.valueOf(
						Outils.nombreChiffreApresVirgule(
								(getDb2Nsmd() / 2),
								2)));
	}

	private double getP2Nsmd() {
		double calcul = 0;

		calcul = 3.0 / 4.0
				* (Math.pow(this.getD1(), 2) + Math.pow(this.getD2Nsmd(), 2));
		return calcul;
	}

	private double getP2Smd() {
		double calcul = 0;
		calcul = (double) 3 / (double) 2 * Math.pow(getD1(), 2);
		return calcul;
	}

	private double getA2Smd() {
		double calcul = 0;
		calcul = 4 * Math.pow(getP2Smd(), 3) + 27 * Math.pow(getQ2Smd(), 2);
		return calcul;
	}

	public double getHsSmd() {
		double calcul = 0;
		calcul = Math.cbrt(-getQ2Smd() / 2 - 0.5 * Math.sqrt((getA2Smd() / 27)))
				+ Math.cbrt(
						-getQ2Smd() / 2 + 0.5 * Math.sqrt((getA2Smd() / 27)));

		return calcul;
	}

	private double getQ2Smd() {
		double calcul = 0;
		calcul = -6 * getVs2Smd() / Math.PI;
		return calcul;
	}

	private double getVs2Smd() {
		double calcul = 0;
		calcul = getVaSmd() - (Ressources.EV1 + Ressources.EV2) * Math.PI
				* Math.pow(getD1(), 2) / 4;

		return calcul;
	}

	private double getVaSmd() {
		double calcul = 0;
		calcul = getVs1Nsmd() + (getEesSmd() + Ressources.EV2) * Math.PI
				* Math.pow(getdEs(), 2) / 8;
		return calcul;
	}

	private double getHbNsmdOptimisee() {
		double calcul = 0;

		calcul = getHs1Optimisee() + getHs2Ec2Optimisee();

		return calcul;
	}

	protected void calcul_hb_nsmd_optimisee() {
		hb_optimisee.set_resultat(String.valueOf(getHbNsmdOptimisee()));
	}

	private double getHb2() {
		double calcul = 0;

		calcul = Math.sqrt(
				Math.pow(getDb2Optimisee(), 2) / 4 - Math.pow(getD1(), 2) / 4)
				+ Math.sqrt(
						Math.pow(getDb2Optimisee(), 2) / 4
								- Math.pow(getD1(), 2) / 4);

		return calcul;
	}

	private double getEesSmd() {
		return getEs();
	}

	private double getEc2NsmdOptimisee() {
		double calcul = 0;

		calcul = getHsNsmdOptimisee() - getHb2();

		return calcul;
	}

	public double gammaOptimisee() {
		double calcul = 0;
		calcul = 3.0 / 2 * Math.pow(this.getD1(), 2)
				+ 6 * Math.pow(Ressources.EC2, 2);
		return calcul;
	}

	public double VaOptimisee() {
		double calcul = 0;
		calcul = Math.PI * Math.pow(this.getDbille(), 3) / 6
				+ (this.getEs() + Ressources.EV2) * Math.PI
						* Math.pow(getdEs(), 2) / 8;
		return calcul;
	}

	public double deltaOptimisee() {
		double calcul = 0;
		calcul = -3.0 / 2 * (Ressources.EC2 - Ressources.EV1)
				* Math.pow(this.getD1(), 2) - 6 * this.VaOptimisee() / Math.PI;
		return calcul;
	}

	public double q2Optimisee() {
		double calcul = 0;
		calcul = -Math.pow(this.getBetaOptimisee(), 3) / 27
				+ Math.pow(this.getBetaOptimisee(), 2) / 9
				- this.getBetaOptimisee() * this.gammaOptimisee() / 3
				+ this.deltaOptimisee();
		return calcul;
	}

	public double p2Optimisee() {
		double calcul = 0;
		calcul = -5 * Math.pow(this.getBetaOptimisee(), 2) / 9
				+ this.gammaOptimisee();
		return calcul;
	}

	public double getA2Optimisee() {
		double calcul = 0;
		calcul = 4 * Math.pow(this.p2Optimisee(), 3)
				+ 27 * Math.pow(this.q2Optimisee(), 2);
		return calcul;
	}

	public double getXOptimisee() { /* OK~~ */
		double calcul = 0;
		calcul = Math.cbrt(
				-this.q2Optimisee() / 2
						- 0.5 * Math.sqrt((this.getA2Optimisee() / 27)))
				+ Math.cbrt(
						-this.q2Optimisee() / 2 + 0.5
								* Math.sqrt((this.getA2Optimisee() / 27)));
		return calcul;
	}

	public double getBetaOptimisee() { /* OK */
		double calcul = 0;
		calcul = -3 * Ressources.EC2;
		return calcul;
	}

	public double hsOptimisee() { /* OK */
		double calcul = 0;
		calcul = this.getXOptimisee() - this.getBetaOptimisee() / 3;
		return calcul;
	}

	public double getHs1Optimisee() { /* OK */
		double calcul = 0;
		calcul = (this.hsOptimisee() - Ressources.EC2) / 2;
		return calcul;
	}

	public void calcul_hs1_nsmd_optimisee() {
		double calcul = 0;
		calcul = (this.hsOptimisee() - Ressources.EC2) / 2;
		hs1_optimisee.set_resultat(
				String.valueOf(Outils.nombreChiffreApresVirgule(calcul, 2)));
	}

	public double getDb2Optimisee() { /* OK */
		double calcul = 0;
		calcul = 2 * Math.sqrt(
				(Math.pow((this.getD1() / 2.0), 2)
						+ Math.pow(this.getHs1Optimisee(), 2)));
		return calcul;
	}

	public double getHs2Optimisee() { /* OK */
		double calcul = 0;
		calcul = this.getHs1Optimisee() + Ressources.EC2;
		return calcul;
	}

	public double getD2Optimisee() { /* OK */
		double calcul = 0;
		calcul = 2 * Math.sqrt(
				(Math.pow(this.getDb2Optimisee(), 2) / 4
						- Math.pow(this.getHs2Optimisee(), 2)));
		return calcul;
	}

	public void calcul_d2_nsmd_optimisee() {
		d2_optimisee.set_resultat(
				String.valueOf(
						Outils.nombreChiffreApresVirgule(getD2Optimisee(), 2)));
	}

	private double getHs2Ec2Optimisee() {
		double calcul = 0;
		calcul = getHs2Optimisee() - Ressources.EC2;
		return calcul;
	}

	public void calcul_hs2ec2_nsmd_optimisee() {
		hs2Ec2.set_resultat(
				String.valueOf(
						Outils.nombreChiffreApresVirgule(
								getHs2Ec2Optimisee(),
								2)));
	}

	public double getHauteurIntegreeNsmd() {
		double calcul = 0;

		calcul = Math.pow(getD1CalculsDimensionnelsDesConnexions(), 2)
				/ (8 * getRbNsmd())
				* (Math.log(
						(getRbNsmd() + this.getHs1Nsmd())
								/ (getRbNsmd() - this.getHs1Nsmd()))
						- Math.log(
								(getRbNsmd() - this.getHs2Ec2Nsmd())
										/ ((getRbNsmd())
												+ this.getHs2Ec2Nsmd())))
				+ Ressources.EV1;
		return calcul;
	}

	private double getHauteurIntegreeNsmdOptimisee() {
		double calcul = 0;
		calcul = Math.pow(this.getD1(), 2) / (8 * this.getRbOptimisee()) * (Math
				.log(
						(this.getRbOptimisee() + this.getHs1Optimisee())
								/ ((this.getRbOptimisee()
										- this.getHs1Optimisee())))
				- Math.log(
						((this.getRbOptimisee() - this.getHs2Ec2Optimisee())
								/ (this.getRbOptimisee()
										+ this.getHs2Ec2Optimisee()))))
				+ Ressources.EV1;

		return calcul;
	}

	public void calcul_hauteur_integree_nsmd_optimisee() {
		this.hauteur_integree_optimisee.set_resultat(
				String.valueOf(
						Outils.nombreChiffreApresVirgule(
								getHauteurIntegreeNsmdOptimisee(),
								2)));

	}

	public void calcul_dr2_nsmd_optimisee() {
		dr2_optimisee.set_resultat(
				String.valueOf(
						Outils.nombreChiffreApresVirgule(
								getDr2Optimisee(),
								2)));
	}

	private double getRbOptimisee() {
		return getDb2Optimisee() / 2;
	}

	public void calcul_Rb_nsmd_optimisee() {
		Rb_optimise.set_resultat(
				String.valueOf(
						Outils.nombreChiffreApresVirgule(getRbOptimisee(), 2)));
	}

	public double getDr2Optimisee() {
		double calcul = 0;
		calcul = 2 * Math.sqrt(
				(Math.pow(this.getDb2Optimisee(), 2) / 4 - Math
						.pow((this.getHs2Optimisee() - Ressources.EC2), 2)));
		return calcul;
	}

	public void calcul_section_resistante_nsmd_optimisee() { /* OK */
		double calcul = 0;
		calcul = Math.PI * Math.pow(getD1(), 2) / 4;
		this.section_resistante_optimisée.set_resultat(String.valueOf(calcul));

	}

	public double getD1() { /* OK */
		double calcul = 0;

		calcul = Math.sqrt(4 * getH1() * (getDb1() - getH1()));
		return calcul;
	}

	public double getDbille() {

		double calcul = 0;
		calcul = Math.cbrt(getVsc() * 6 / Math.PI);

		return calcul;
	}

	public double getH1() {
		return h1;
	}

	public void setH1(double h1) {
		this.h1 = h1;
	}

	private double getVsc() {
		double calcul = 0;

		calcul = getVs1Nsmd()
				+ Math.PI * Math.pow(getD1(), 2) / 4 * Ressources.EV1;
		return calcul;
	}

	public double getDb1() {
		return db1;
	}

	public void setDb1(double db1) {
		this.db1 = db1;
	}

	public double getDr2() {
		return Double.valueOf(dr2.get_resultat().get());
	}

	public void setDr2(double dr2) {
		this.dr2.set_resultat(String.valueOf(dr2));
	}

	public double getRb() {
		return Double.valueOf(Rb.get_resultat().get());
	}

	public void setRb(double rb) {
		this.Rb.set_resultat(String.valueOf(rb));
	}

	public void setHs2(double hs2) {
		this.hs2.set_resultat(String.valueOf(hs2));
	}

	public String toString() {
		if (technologie.equals("NSMD")) {
			return nom + ";" + technologie + ";" + h1 + ";" + db1 + ";"
					+ NSMD_d2 + ";" + es + ";" + des;
		} else {
			return nom + ";" + technologie + ";" + h1 + ";" + db1 + ";" + es
					+ ";" + des;
		}
	}

	@Override
	public double getHi() {
		return (Double) null;
	}
}

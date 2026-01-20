package com.app.ancea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import javafx.scene.image.ImageView;

public class BrasureLCC extends BrasureComposantLeadless
		implements InterfaceBrasureComposant {

	static ImageView image = new ImageView(
			BrasureLCC.class.getResource("images/LCCC_BRASURE.png")
					.toExternalForm());
	private static final Logger logger = Logger.getLogger(Main.class.getName());

	protected String technologie;

	private final double epaisseur_vernis_epargne = 0.02;
	private final double epaisseur_vernis_epargne_smd = 0.005;

	private double asSmd, bsSmd, esSmd;

	private double am, bm, hm;

	public BrasureLCC() {
		super();

		asSmd = 0;
		bsSmd = 0;
		esSmd = 0;

		this.am = 0;
		this.bm = 0;
		this.hm = 0;

		technologie = "";
		infoComposant = "Technlogie: " + technologie + "\nas: " + getAs()
				+ " bs: " + getBs() + " es: " + getEs() + "\n a1: " + getA1()
				+ " b1: " + getB1() + " c1: " + getC1();

	}

	@Override
	public void setFormat(String format) {
		this.format = format;
		composant = LCC.getLCCC(format);
	}

	public BrasureLCC(String path) {
		heq = new Propriete("Hauteur equivalente");
		Sc = new Propriete("Section critique");
		Hi = new Propriete("Hauteur intégrée");

		try (FileReader fr = new FileReader(path);
				BufferedReader br = new BufferedReader(fr)) {

			String line = br.readLine();

			String[] parametres = line.split(";");

			format = parametres[0];

			nom = parametres[0];
			setAs(Double.valueOf(parametres[1]));
			setBs(Double.valueOf(parametres[2]));
			setEs(Double.valueOf(parametres[3]));
			setA1(Double.valueOf(parametres[4]));
			setB1(Double.valueOf(parametres[5]));
			// épaisseur cuivre
			setC1(Double.valueOf(parametres[6]));
			setTechnologie(parametres[7]);

			setValeurSmd();

			infoComposant = "Technlogie: " + technologie + "\nas: " + getAs()
					+ " bs: " + getBs() + " es: " + getEs() + "\n a1: "
					+ getA1() + " b1: " + getB1() + " c1: " + getC1();

		} catch (IOException ie) {
			ie.printStackTrace();
		}
		composant = LCC.getLCCC(format);
		setCalculs();
	}

	public String getTechnologie() {
		return technologie;
	}

	private void setValeurSmd() {
		asSmd = a1 - 0.03 * 2;
		bsSmd = c1 - 0.03 * 2;
	}

	public void setTechnologie(String technologie) {
		this.technologie = technologie;
	}

	private double getVb() {

		double calcul;

		if (technologie.equals("NSMD")) {
			calcul = getAs() * getBs() * (getEs() + epaisseur_vernis_epargne)
					/ 2;
		} else {
			calcul = asSmd * bsSmd * (getEs() + epaisseur_vernis_epargne_smd)
					/ 2;
		}
		logger.fine("vb: " + calcul);
		return calcul;
	}

	protected double getH1() {
		return 0.055;
	}

	protected double getVb1() {
		double calcul;
		if (technologie.equals("NSMD")) {

			if (composant.get("a") < getA1()) {
				calcul = composant.get("a") * composant.get("b") * getH1()
						+ (getA1() - composant.get("a")) * composant.get("b")
								* getH1() / (double) 2.0;
			} else {
				calcul = getA1() * composant.get("b") * getH1()
						+ (composant.get("a") - getA1()) * composant.get("b")
								* getH1() / 2.0;
			}
		}

		else {
			calcul = composant.get("a") * composant.get("b") * getH1()
					+ Math.abs(getA1() - composant.get("a"))
							* composant.get("b") * getH1();
		}

		logger.fine("vb1: " + calcul);
		return calcul;
	}

	private double getVb2() {
		logger.fine("vb2: " + (getVb() - getVb1()));
		return getVb() - getVb1();
	}

	private double getS2() {
		double calcul;
		logger.fine("s2: " + (getVb2() / composant.get("a")));
		if (composant.get("a") < getA1()) {
			calcul = getVb2() / composant.get("a");
		} else {
			calcul = getVb2() / getA1();
		}

		return calcul;
	}

	protected double getHx() {
		double calcul;

		calcul = Math.sqrt(2 * getS2()) - getH1();

		logger.fine("hx: " + calcul);
		return calcul;
	}

	private double getH2() {
		double calcul;

		calcul = (getHx() / 2) * Math.sqrt(2) + getH1() * Math.sqrt(2);

		logger.fine("h2: " + calcul);
		return calcul;
	}

	private double getB2() {
		logger.fine("b2: " + ((getHx() * Math.sqrt(2)) / 2));
		return (getHx() * Math.sqrt(2)) / 2;
	}

	@Override
	public double getHeq() {
		double calcul;

		calcul = getH1() * getH2() * (composant.get("b") + getB2())
				/ (composant.get("b") * getH2() + getB2() * getH1());
		logger.fine("heq: " + calcul);
		return calcul;
	}

	private double getSrc() {
		double calcul;

		if (composant.get("a") > getA1()) {
			calcul = getA1() * (composant.get("b") + getB2()) - getH1();
		} else {
			calcul = composant.get("a") * (composant.get("b") + getB2());
			logger.fine("src: " + calcul);
		}
		return calcul;
	}

	protected double getEcu() {
		return getB1();
	}

	protected double getLr2() {
		double calcul;

		if (technologie.equals("NSMD")) {
			calcul = 2
					* Math.sqrt(Math.pow(getR(), 2) - (Math.pow(getHr(), 2)));
		} else {
			calcul = composant.get("a");
		}
		logger.fine("lr2: " + calcul);
		return calcul;
	}

	protected double getR() {
		double calcul;

		calcul = Math.sqrt(
				Math.pow(composant.get("a") / 2.0, 2) + Math.pow(getHa(), 2));
		logger.fine("R: " + calcul);
		return calcul;
	}

	protected double getHa() {
		double calcul;

		if (technologie.equals("NSMD")) {
			calcul = (-(Math.pow(composant.get("a"), 2) - Math.pow(getA1(), 2))
					+ 4 * Math.pow(getHeq() + getEcu(), 2))
					/ (8 * (getHeq() + getEcu()));
		} else {
			calcul = getHeq() / 2;
		}
		logger.fine("ha: " + calcul);
		return calcul;
	}

	protected double getHb() {
		double calcul;

		if (technologie.equals("NSMD")) {
			calcul = -getHeq() - getEcu() + getHa();
		} else {
			calcul = -getHeq() / 2;
		}

		logger.fine("hb: " + calcul);
		return calcul;
	}

	protected double getHr() {
		double calcul;

		calcul = getHb() + getEcu();
		logger.fine("hr: " + calcul);
		return calcul;
	}

	@Override
	public double getSc() {
		return getSrc();
	}

	@Override
	public double getHi() {
		double calcul;

		if (technologie.equals("NSMD")) {
			if (getLr2() < composant.get("a")) {
				calcul = getLr2() / 2 * (Math.asin((getHa() / getR()))
						- Math.asin(getHr() / getR()));
			} else {
				calcul = composant.get("a") / 2 * (Math.asin((getHa() / getR()))
						- Math.asin(getHr() / getR()));
			}
		} else {
			calcul = composant.get("a") * (Math.asin(getHa() / getR()))
					+ Ressources.EV1;
		}

		return calcul;

	}

	private double getGainNSMDSMD() {
//		double gainsmd = 0;
//		double gainnsmd = 0;
//		
//		String tmp = technologie;
//		technologie = "NSMD";
//		gainnsmd = getHi();
//		technologie = "SMD";
//		gainsmd = getHi();
//		technologie = tmp;
//		double calcul = Math.pow(gainnsmd / gainsmd, 1.0 / Ressources.COEFFICIENT_FATIGUE_B);
//		getHi();
		return 0;
	}

	@Override
	protected void calcul_Hi() {
		Hi.set_resultat(
				String.valueOf(
						Outils.nombreChiffreApresVirgule(
								getHi(),
								Ressources.NOMBRE_CHIFFRE_APRES_VIRGULE)));
	}

	@Override
	protected void calcul_heq() {
		heq.set_resultat(
				String.valueOf(
						Outils.nombreChiffreApresVirgule(
								getHeq(),
								Ressources.NOMBRE_CHIFFRE_APRES_VIRGULE)));
	}

	protected void calcul_src() {
		Sc.set_resultat(
				String.valueOf(
						Outils.nombreChiffreApresVirgule(
								getSrc(),
								Ressources.NOMBRE_CHIFFRE_APRES_VIRGULE)));
	}

	protected void calcul_gain_nsmd_smd() {
		gainSmdNsmd.set_resultat(String.valueOf(getGainNSMDSMD()));
	}

	@Override
	protected void setCalculs() {
		calcul_heq();
		calcul_src();
		calcul_Hi();
		calcul_gain_nsmd_smd();
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String toString() {
		return format + ";" + as + ";" + bs + ";" + es + ";" + a1 + ";" + b1
				+ ";" + c1 + ";" + technologie;
	}

}

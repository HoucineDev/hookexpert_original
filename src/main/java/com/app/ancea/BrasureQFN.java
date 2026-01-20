package com.app.ancea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import javafx.scene.image.ImageView;

public class BrasureQFN extends BrasureComposantLeadframe {

	static ImageView image = new ImageView(
			BrasureQFN.class.getResource("images/QFN_BRASURE.png")
					.toExternalForm());

	private static final Logger logger = Logger.getLogger(Main.class.getName());

	private final double epaisseur_vernis_epargne_nsmd = 0.02;
	private final double epaisseur_vernis_epargne_smd = 0.007;

	public BrasureQFN() {
		super();
	}

	@Override
	public void setFormat(String format) {
		this.format = format;
		composant = QFN.getComposant(format);
	}

	public BrasureQFN(String path) {
		heq = new Propriete("Hauteur equivalente");
		Sc = new Propriete("Section critique");
		Hi = new Propriete("Hauteur intégrée");

		try (FileReader fr = new FileReader(path);
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
			setTechnologie(parametres[7]);

			infoComposant = "Technlogie: " + technologie + "\nas: " + getAs()
					+ " bs: " + getBs() + " es: " + getEs() + "\n a1: "
					+ getA1() + " b1: " + getB1() + " c1: " + getC1();

		} catch (IOException ie) {
			// ie.printStackTrace();
			System.out.println("Brasure QFN introuvable: " + path + "\n");
		}
		composant = QFN.getComposant(format);
		setCalculs();

	}

	protected double getH1() {
		return 0.055;
	}

	private double getS2() {
		double calcul = 0;

		calcul = getVb2() / composant.get("a");

		return calcul;
	}

	private double getVb2() {
		double calcul = 0;

		calcul = getVb() - getVb1();

		return calcul;
	}

	private double getVb() {

		double calcul = 0;

		if (technologie.equals("NSMD")) {
			calcul = (getAs() * getBs())
					* (getEs() + epaisseur_vernis_epargne_nsmd) / 2;
		} else {
			calcul = (getAs() * getBs())
					* (getEs() + epaisseur_vernis_epargne_smd) / 2;
		}

		return calcul;
	}

	protected double getVb1() {
		double calcul = 0;

		if (technologie.equals("NSMD")) {
			calcul = composant.get("a") * composant.get("b") * getH1()
					+ (getA1() - composant.get("a")) * composant.get("b")
							* getH1();
		} else {
			calcul = composant.get("a") * composant.get("b") * getH1();
		}
		logger.severe("vb1: " + calcul);
		return calcul;
	}

	protected double getHx() {
		double calcul = 0;

		if (technologie.equals("NSMD")) {
			calcul = (getEs() + epaisseur_vernis_epargne_nsmd) / 2;
		} else {
			calcul = (getEs() + epaisseur_vernis_epargne_smd) / 2;
		}
		logger.severe("hx: " + calcul);

		return calcul;
	}

	protected double getEcu() {
		return getB1();
	}

	protected double getHa() {
		double calcul = 0;

		if (technologie.equals("NSMD")) {
			calcul = (-(Math.pow(composant.get("a"), 2) - Math.pow(getA1(), 2))
					+ 4 * Math.pow(getHeq() + getEcu(), 2))
					/ (8 * (getHeq() + getEcu()));
		} else {
			calcul = getHeq() / 2;
		}
		logger.severe("ha: " + calcul);

		return calcul;
	}

	protected double getHb() {
		double calcul = 0;

		if (technologie.equals("NSMD")) {
			calcul = -getHeq() - getEcu() + getHa();
		} else {
			calcul = -getHa();
		}
		logger.severe("hb: " + calcul);
		return calcul;
	}

	protected double getLr2() {
		double calcul;

		if (technologie.equals("NSMD")) {
			calcul = 2 * Math
					.sqrt(Math.pow(getR(), 2) - Math.pow(getHprimeB(), 2));
		} else {
//			System.out.println("getLr2: "+composant.get("a"));
			return composant.get("a");
		}
		logger.severe("lr2: " + calcul);

		return calcul;
	}

	protected double getHprimeB() {
		double calcul = 0;

		calcul = getHb() + getEcu();
		logger.severe("h'b: " + calcul);

		return calcul;
	}

	public double getB2() {
		double calcul = 0;

		calcul = getHx() / Math.sqrt(2);
		logger.severe("b2: " + calcul);

		return calcul;
	}

	private double getHr() {
		double calcul = 0;

		calcul = getHx() / (2 * Math.sqrt(2)) + getH1() * Math.sqrt(2);
		logger.severe("hr: " + calcul);

		return calcul;
	}

	@Override
	public double getHeq() {
		double calcul = 0;

		calcul = getH1() * getHr() * (composant.get("b") + getB2())
				/ (composant.get("b") * getHr() + getB2() * getH1());
		logger.severe("heq: " + calcul);

		return calcul;
	}

	@Override
	public double getHi() {
		double calcul = 0;

		if (technologie.equals("NSMD")) {
			if (getLr2() < composant.get("a")) {
				calcul = getLr2() / 2 * (Math.asin(getHa() / getR())
						- Math.asin(getHprimeB() / getR()));
			} else {
				calcul = composant.get("a") / 2 * (Math.asin(getHa() / getR())
						- Math.asin(getHprimeB() / getR()));
			}
		} else {
			calcul = composant.get("a") * Math.asin(getHa() / getR())
					+ epaisseur_vernis_epargne_smd;
		}
		logger.severe(
				"lr2: " + getLr2() + " ha: " + getHa() + " R: " + getR()
						+ " b: " + composant.get("b") + " hi: " + calcul);

		return calcul;

	}

	public double getR() {
		double calcul = 0;

		if (technologie.equals("NSMD")) {
			calcul = Math.sqrt(
					Math.pow(composant.get("a") / 2.0, 2)
							+ Math.pow(getHa(), 2));
		} else {
			calcul = Math.sqrt(
					Math.pow(composant.get("a") / 2.0, 2)
							+ Math.pow(getHa(), 2));
		}
		logger.severe("R: " + calcul);

		return calcul;
	}

	@Override
	public double getSc() {
		double calcul = 0;

		if (composant.get("a") <= getA1()) {
			calcul = composant.get("a") * (composant.get("b") + getB2());
		} else {
			calcul = getA1()
					* (composant.get("b") - getH1() * Math.sqrt(2) + getB2());
		}
		logger.severe("sc: " + calcul);

		return calcul;
	}

	protected void setCalculs() {
		heq.set_resultat(String.valueOf(getHeq()));
		Sc.set_resultat(String.valueOf(getSc()));
		Hi.set_resultat(String.valueOf(getHi()));
	}

}

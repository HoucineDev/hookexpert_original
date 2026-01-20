package com.app.ancea;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CoucheIsolantePcb extends CouchePcb {

	// Classe représentant les couches isolantes du PCB(les couches vert claire
	// et vert foncé)

	TextField epaisseur_apres_pressage;
	TextField taux_resine;
	TextField nombre;

	HBox hbx_couche;

	Rectangle vialaser1;// il y en a deux pour pouvoir créer un effet de
						// décalage des vialasers
						// succéssives
	Rectangle vialaser2;
	Rectangle trou_enterre;
	Rectangle trou_traversant;

	ComboBox<String> choix_isolant = new ComboBox<String>();
	ComboBox<String> choix_tissus = new ComboBox<String>();

	int epaisseur_stratifie;
	int epaisseur_tissus;

	// CoucheElectrique couche_sup;
	// CoucheElectrique couche_inf;

	public void init_trou_traversant() {

		trou_traversant = new Rectangle();
		trou_traversant.setHeight(Ressources.TROU_HEIGHT);
		trou_traversant.setWidth(Ressources.TROU_WIDTH);
		trou_traversant.setFill(Color.ORANGE);
		HBox.setMargin(trou_traversant, new Insets(0, 0, 0, 15));

		hbx_couche.getChildren().add(trou_traversant);
	}

	public void init_trou_enterre() {
		trou_enterre = new Rectangle();
		trou_enterre.setHeight(Ressources.TROU_HEIGHT);
		trou_enterre.setWidth(Ressources.TROU_WIDTH);
		trou_enterre.setFill(Color.ORANGE);
		trou_enterre.setVisible(false);
		HBox.setMargin(trou_enterre, new Insets(0, 0, 0, 20));

		hbx_couche.getChildren().add(trou_enterre);
	}

	public void init_vialasers() {
		// il y a deux vialaser pour créer un effet de décalage des couches
		// superposées
		// les unes sur les autres

		vialaser1 = new Rectangle();
		vialaser1.setHeight(Ressources.TROU_HEIGHT);
		vialaser1.setWidth(Ressources.TROU_WIDTH);
		vialaser1.setFill(Color.ORANGE);
		vialaser1.setVisible(false);
		HBox.setMargin(vialaser1, new Insets(0, 0, 0, 20));

		vialaser2 = new Rectangle();
		vialaser2.setHeight(Ressources.TROU_HEIGHT);
		vialaser2.setWidth(Ressources.TROU_WIDTH);
		vialaser2.setFill(Color.ORANGE);
		vialaser2.setVisible(false);
		HBox.setMargin(vialaser2, new Insets(0, 0, 0, 5));

		hbx_couche.getChildren().add(vialaser1);
		hbx_couche.getChildren().add(vialaser2);

	}

	public void init_choix_isolant() {

		choix_isolant.getItems()
				.addAll(Ressources.PREIMPREGNE, Ressources.STRATIFIE);
		choix_isolant.setValue(Ressources.PREIMPREGNE);
		choix_isolant.setScaleY(0.75);
		choix_isolant.setScaleX(0.75);
		HBox.setMargin(choix_isolant, new Insets(0, 0, 0, 60));
		choix_isolant.valueProperty().addListener(new ChangeListener<String>() {
			public void changed(
					ObservableValue<? extends String> arg0,
					String arg1,
					String arg2) {
				try {
					couleur_choix_isolant();
				} catch (Exception e) {

				}
			}

		});

		hbx_couche.getChildren().add(choix_isolant);
	}

	public void init_choix_tissus() {
		choix_tissus.getItems().addAll(Ressources.TISSUS);// ici remplir le get
															// item avec tous
															// les tissus de la
															// base de
		// donnée
		choix_tissus.setValue(Ressources.TISSUS_COUCHE_DEFAUT);
		choix_tissus.setScaleY(0.75);
		choix_tissus.setScaleX(0.80);
		HBox.setMargin(choix_isolant, new Insets(0, 0, 0, 5));

		hbx_couche.getChildren().add(choix_tissus);
	}

	public void init_nombre() {
		nombre = new TextField(Ressources.NOMBRE_COUCHE_ISOLANTE_DEFAUT);
		nombre.setStyle(Ressources.TEXTFIELD_STYLE);
		nombre.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		nombre.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		HBox.setMargin(nombre, new Insets(0, 0, 0, 15));

		hbx_couche.getChildren().add(nombre);
	}

	public void init_epaisseur_apres_pressage() {
		epaisseur_apres_pressage = new TextField(
				Ressources.TEXTFIELD_VALEURE_DEFAUT);
		epaisseur_apres_pressage.setStyle(Ressources.TEXTFIELD_STYLE);
		epaisseur_apres_pressage.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		epaisseur_apres_pressage.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		epaisseur_apres_pressage.setEditable(false);
		HBox.setMargin(epaisseur_apres_pressage, new Insets(0, 0, 0, 70));

		hbx_couche.getChildren().add(epaisseur_apres_pressage);
	}

	public void init_taux_resine() {
		taux_resine = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		taux_resine.setStyle(Ressources.TEXTFIELD_STYLE);
		taux_resine.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		taux_resine.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		taux_resine.setEditable(false);
		HBox.setMargin(taux_resine, new Insets(0, 0, 0, 80));

		hbx_couche.getChildren().add(taux_resine);

	}

	public CoucheIsolantePcb() {

		hbx_couche = new HBox();
		hbx_couche.setStyle(Ressources.COUCHE_ISOLANTE_PREIMPREGNE_STYLE);// couleur
																			// de
																			// la
																			// couche
																			// isolante
																			// (verte)
		hbx_couche.setPrefHeight(Ressources.COUCHE_ISOLANTE_HEIGHT);
		hbx_couche.setPrefWidth(Ressources.COUCHE_ISOLANTE_WIDTH);

		init_trou_traversant();
		init_trou_enterre();
		init_vialasers();
		init_choix_isolant();
		init_choix_tissus();
		init_nombre();
		init_epaisseur_apres_pressage();
		init_taux_resine();

		this.getChildren().add(hbx_couche);

	}

	public void couleur_choix_isolant() {
		if (this.isPreimpregne()) {
			hbx_couche.setStyle(Ressources.COUCHE_ISOLANTE_PREIMPREGNE_STYLE);
		} else {
			hbx_couche.setStyle(Ressources.COUCHE_ISOLANTE_STRATIFIE_STYLE);
		}
	}

	public boolean isPreimpregne() {
		return (this.choix_isolant.getValue().equals(Ressources.PREIMPREGNE));
	}

	public boolean isStratifie() {
		return (this.choix_isolant.getValue().equals(Ressources.STRATIFIE));
	}

	public String get_choix_isolant() {
		return choix_isolant.getValue();
	}

	public void set_choix_isolant(String val) {
		choix_isolant.setValue(val);
	}

	public String get_choix_tissus() {
		return choix_tissus.getValue();
	}

	public void set_choix_tissus(String val) {
		choix_tissus.setValue(val);
	}

	public double get_epaisseur_apres_pressage() {
		String s = epaisseur_apres_pressage.getText();
		return Double.parseDouble(s);
	}

	public void set_epaisseur_apres_pressage(String val) {
		this.epaisseur_apres_pressage.setText(val);
	}

	public int get_taux_resine() {
		String s = taux_resine.getText();
		return Integer.parseInt(s);
	}

	public void set_taux_resine(String val) {
		this.taux_resine.setText(val);
	}

	public int get_nombre() {
		String s = nombre.getText();
		return Integer.parseInt(s);
	}

	public void set_nombre(String val) {
		this.nombre.setText(val);
	}

	public int get_epaisseur_stratifie() {
		return this.epaisseur_stratifie;
	}

	public void set_epaisseur_stratifie(int e) {
		this.epaisseur_stratifie = e;
	}

	public int get_epaisseur_tissus() {
		return this.epaisseur_tissus;
	}

	public void set_epaisseur_tissus(int e) {
		this.epaisseur_tissus = e;
	}

	public String get_trou_enterre_visibilite() {
		return String.valueOf(this.trou_enterre.isVisible());
	}

	public void set_trou_enterre_visibilite(boolean b) {

		this.trou_enterre.setVisible(b);
	}

	public void set_trou_enterre_visibilite(String b) {
		this.trou_enterre.setVisible(Boolean.parseBoolean(b));
	}

	public String get_vialaser1() {
		return String.valueOf(this.vialaser1.isVisible());
	}

	public void set_vialaser1(String b) {
		if ((Boolean.parseBoolean(b)) == true) {
			this.set_contrainte_choix();
		}
		this.vialaser1.setVisible(Boolean.parseBoolean(b));
	}

	public String get_vialaser2() {
		return String.valueOf(this.vialaser2.isVisible());
	}

	public void set_vialaser2(String b) {
		if ((Boolean.parseBoolean(b)) == true) {
			this.set_contrainte_choix();
		}
		this.vialaser2.setVisible(Boolean.parseBoolean(b));
	}

	public String get_trou_traversant() {
		return String.valueOf(trou_traversant.isVisible());
	}

	public void set_trou_traversant(String b) {
		this.trou_traversant.setVisible(Boolean.parseBoolean(b));
	}

	public String toString() {

		return this.get_trou_traversant() + ";"
				+ this.get_trou_enterre_visibilite() + ";"
				+ this.get_vialaser1() + ";" + this.get_vialaser2() + ";"
				+ this.get_choix_isolant() + ";" + this.get_choix_tissus() + ";"
				+ this.get_nombre() + ";" + this.get_epaisseur_apres_pressage()
				+ ";" + this.get_taux_resine();

	}

	public boolean get_vialaser() {
		return (this.vialaser1.isVisible() || this.vialaser2.isVisible());
	}

	/*
	 * public void init_couche_sup(CoucheElectrique ce) { this.couche_sup=ce; }
	 * 
	 * public void init_couche_inf(CoucheElectrique ci) { this.couche_inf=ci; }
	 */

	public void get_epaisseurs() {
		if (this.choix_tissus.getValue().equals("2116")) {// REMPLACER CECI PAR
															// UNE INTERROGATION
															// DE LA BDD SELON
															// SON ID
															// TISSUS et mettre
															// cette
															// interrogation
															// dans
															// la fonction
															// calcul.
			this.epaisseur_stratifie = 119;// remplir le champs
											// epaisseur_stratifie et
											// taux_resine et checker
			// que l'epaisseur tissus est inferieur e epaisseur apres pressage
			// et mettre ce if au cas ou la fonction retourne -1 (elle s'est mal
			// passee)
			this.epaisseur_tissus = 97; // utiliser set
			this.taux_resine.setText("50"); // utiliser set: if connection
											// remplir avec l'entier retourne
											// sinon appeler la fonction
											// contenant le long if

		} else if (this.choix_tissus.getValue().equals("2113")) {

			this.epaisseur_stratifie = 94;
			this.epaisseur_tissus = 74;
			this.taux_resine.setText("54");
		} else if (this.choix_tissus.getValue().equals("1080")) {

			this.epaisseur_stratifie = 75;
			this.epaisseur_tissus = 64;
			this.taux_resine.setText("62");
		} else if (this.choix_tissus.getValue().equals("106")) {

			this.epaisseur_stratifie = 53;
			this.epaisseur_tissus = 32;
			this.taux_resine.setText("71");
		} else if (this.choix_tissus.getValue().equals("1037")) {

			this.epaisseur_stratifie = 50;
			this.epaisseur_tissus = 30;
			this.taux_resine.setText("72");
		} else if (this.choix_tissus.getValue().equals("1506")) {

			this.epaisseur_stratifie = 175;
			this.epaisseur_tissus = 140;
			this.taux_resine.setText("49");
		} else if (this.choix_tissus.getValue().equals("1078")) {

			this.epaisseur_stratifie = 79;
			this.epaisseur_tissus = 40;
			this.taux_resine.setText("64");
		} else if (this.choix_tissus.getValue().equals("7628")) {

			this.epaisseur_stratifie = 200;
			this.epaisseur_tissus = 175;
			this.taux_resine.setText("46");
		}

	}

	public void calcul(CoucheElectriquePcb c1, CoucheElectriquePcb c2) {
		// Calcul de l'epaisseur apres pressage pour les
		// couche Isolante

		// peut etre mettre le listener de l'epaisseur apres pressage ICI

		/*
		 * NumberFormat format=NumberFormat.getInstance();
		 * format.setMinimumFractionDigits(2); //nb de chiffres apres la virgule
		 */

		double calcul = 0;

		this.get_epaisseurs();
		////////////////////////////////////////////////////////

		if (this.isStratifie()) {

			calcul = Double.parseDouble(this.nombre.getText()) * this.epaisseur_stratifie;
			this.epaisseur_apres_pressage.setText(String.valueOf(calcul));

		}

		else if ((this.isPreimpregne()
				&& (c1.get_epaisseur_cuivre_recharge() == 30
						|| c2.get_epaisseur_cuivre_recharge() == 30))
				|| this.get_vialaser()) {

			// remplacer 30 par le chiffre de la bibliotheque statique contenant
			// les
			// constantes
			// genre epaisseur_cuivre_recharge_couche_externe

			double nombre = this.get_nombre();
			double taux_remplissage_c1 = 0;
			double epaisseur_cuivre_c1 = 0;
			double taux_remplissage_c2 = c2.get_taux_remplissage();
			double epaisseur_cuivre_c2 = c2.get_epaisseur_cuivre();

			calcul = (nombre * epaisseur_stratifie)
					- ((((100 - taux_remplissage_c1) / 100)
							* epaisseur_cuivre_c1)
							+ (((100 - taux_remplissage_c2) / 100)
									* epaisseur_cuivre_c2));

			// String calculbis=format.format(calcul);
			this.epaisseur_apres_pressage.setText(String.valueOf(calcul));
			// si epaisser_apres_pressage>epaisseur du tissus (dans la BDD)
			// alors encadrer
			// epaisseur apres pressage en rouge

		}

		else {

			double nombre = this.get_nombre();
			double taux_remplissage_c1 = c1.get_taux_remplissage();
			double epaisseur_cuivre_c1 = c1.get_epaisseur_cuivre();
			double taux_remplissage_c2 = c2.get_taux_remplissage();
			double epaisseur_cuivre_c2 = c2.get_epaisseur_cuivre();

			calcul = (nombre * this.epaisseur_stratifie)
					- ((((100 - taux_remplissage_c1) / 100)
							* epaisseur_cuivre_c1)
							+ (((100 - taux_remplissage_c2) / 100)
									* epaisseur_cuivre_c2));

			// String calculbis=format.format(calcul);
			this.epaisseur_apres_pressage.setText(String.valueOf(calcul));
			// si epaisser_apres_pressage>epaisseur du tissus (dans la BDD)
			// alors encadrer
			// epaisseur apres pressage en rouge

		}

		this.check_epaisseur_apres_pressage(calcul, this.epaisseur_tissus);

	}

	public void check_epaisseur_apres_pressage(
			double calcul,
			double epaisseur_tissus) {

		if (calcul < epaisseur_tissus) {

			this.epaisseur_apres_pressage.setStyle(Ressources.WRONG_STYLE);
		} else {
			this.epaisseur_apres_pressage.setStyle(Ressources.RIGHT_STYLE);
		}

	}

	public void calcul() {

	}

	public void setVialaser_position(boolean decalage) {

		if (decalage) {
			this.vialaser1.setVisible(true);
		}

		else {
			this.vialaser2.setVisible(true);
		}

		this.set_contrainte_choix();

	}

	public void set_contrainte_choix() {// lorsque l'on a un vialaser , les
										// choix de tissus et d'isolant sont
										// restreints
		choix_tissus.getItems().clear();
		choix_tissus.getItems().addAll(Ressources.TISSUS_COUCHE_VIA);
		choix_tissus.setValue(Ressources.TISSUS_COUCHE_VIA_DEFAUT);
		choix_isolant.getItems().clear();
		choix_isolant.getItems().add(Ressources.PREIMPREGNE);
		choix_isolant.setValue(Ressources.PREIMPREGNE);

	}

	@Override
	public double epaisseur_totale_couche() {
		double epaisseur = 0;
		epaisseur = this.get_nombre() * this.get_epaisseur_stratifie();
		return epaisseur;
	}

}

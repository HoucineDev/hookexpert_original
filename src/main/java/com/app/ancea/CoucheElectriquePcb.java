package com.app.ancea;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class CoucheElectriquePcb extends CouchePcb {
	
	//Classe représentant les couches électriques du PCB( les couche orange)

	ComboBox<String> epaisseur_cuivre = new ComboBox<String>();
	TextField epaisseur_cuivre_recharge;
	TextField taux_remplissage;
	TextField epaisseur_cuivre_equivalent;

	Rectangle trou_traversant;

	HBox hbx_couche;
	// faire un autre constructeur pour ouvrir un circuit déjà existant , dont on
	// passer les parametre de couche au
	// constructeur

	public CoucheElectriquePcb() {// remplacer toutes les constantes par les valeur d'une classe statique

		hbx_couche = new HBox();
		hbx_couche.setPrefHeight(Ressources.COUCHE_ELECTRIQUE_HEIGHT);
		hbx_couche.setStyle(Ressources.COUCHE_ELECTRIQUE_STYLE);// couleur de la couche electrique (orange)
		hbx_couche.setPrefWidth(Ressources.COUCHE_ELECTRIQUE_WIDTH);

		init_trou_traversant();

		init_epaisseur_cuivre();

		init_epaisseur_cuivre_recharge();

		init_taux_replissage();

		init_epaisseur_cuivre_equivalent();

		// hbx.getChildren().add(via);
		/*
		 * hbx.setMargin(epaisseur_cuivre, new Insets(0, 0, 0, 100));
		 * hbx.setMargin(taux_remplissage, new Insets(0, 0, 0, 100));
		 * hbx.setMargin(epaisseur_cuivre_equivalent, new Insets(0, 0, 0, 100));
		 */
		// hbx.setSpacing(20);

		this.getChildren().add(hbx_couche);

	}

	public void init_epaisseur_cuivre_equivalent() {
		epaisseur_cuivre_equivalent = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		epaisseur_cuivre_equivalent.setStyle(Ressources.TEXTFIELD_STYLE);
		epaisseur_cuivre_equivalent.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		epaisseur_cuivre_equivalent.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		epaisseur_cuivre_equivalent.setEditable(false);
		HBox.setMargin(epaisseur_cuivre_equivalent, new Insets(0, 0, 0, 80));
		
		hbx_couche.getChildren().add(epaisseur_cuivre_equivalent);

	}

	public void init_taux_replissage() {
		taux_remplissage = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		taux_remplissage.setStyle(Ressources.TEXTFIELD_STYLE);
		taux_remplissage.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		taux_remplissage.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		taux_remplissage.textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				calcul();
			}
		});
		HBox.setMargin(taux_remplissage, new Insets(0, 0, 0, 130));

		hbx_couche.getChildren().add(taux_remplissage);

	}

	public void init_epaisseur_cuivre_recharge() {
		epaisseur_cuivre_recharge = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		epaisseur_cuivre_recharge.setStyle(Ressources.TEXTFIELD_STYLE);
		epaisseur_cuivre_recharge.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		epaisseur_cuivre_recharge.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		epaisseur_cuivre_recharge.setEditable(false);
		HBox.setMargin(epaisseur_cuivre_recharge, new Insets(0, 0, 0, 10));

		hbx_couche.getChildren().add(epaisseur_cuivre_recharge);

	}

	public void init_trou_traversant() {
		trou_traversant = new Rectangle();
		trou_traversant.setHeight(18);
		trou_traversant.setWidth(20);
		trou_traversant.setFill(Color.ORANGE);
		HBox.setMargin(trou_traversant, new Insets(0, 0, 0, 20));

		hbx_couche.getChildren().add(trou_traversant);
	}

	public void init_epaisseur_cuivre() {
		
		epaisseur_cuivre.getItems().addAll(Ressources.EPAISSEUR_CUIVRE);
		epaisseur_cuivre.setStyle(Ressources.TEXTFIELD_STYLE);
		epaisseur_cuivre.setValue(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		epaisseur_cuivre.valueProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				calcul();

			}
		});

		HBox.setMargin(epaisseur_cuivre, new Insets(0, 0, 0, 100));
		hbx_couche.getChildren().add(epaisseur_cuivre);

	}

	public double epaisseur_totale_couche() {
		return Double.parseDouble((this.epaisseur_cuivre_equivalent.getText()));
	}

	public String get_trou_traversant() {
		return String.valueOf(this.trou_traversant.isVisible());
	}

	public double get_epaisseur_cuivre() {
		String s = epaisseur_cuivre.getValue();
		return Double.parseDouble(s);
	}

	public void set_epaisseur_cuivre(String val) {
		this.epaisseur_cuivre.setValue(val);
	}

	public double get_taux_remplissage() {
		String s = taux_remplissage.getText();
		return Double.parseDouble(s);
	}

	public void set_taux_remplissage(String val) {
		this.taux_remplissage.setText(val);
	}

	public double get_epaisseur_cuivre_equivalent() {
		String s = epaisseur_cuivre_equivalent.getText();
		return Double.parseDouble(s);
	}

	public void set_epaisseur_cuivre_equivalent(String val) {
		this.epaisseur_cuivre_equivalent.setText(val);
	}

	public double get_epaisseur_cuivre_recharge() {
		System.out.println("ECR: " + epaisseur_cuivre_recharge.getText());

		System.out.println("Current value: " + epaisseur_cuivre.getValue());
		System.out.println("Current value: " + epaisseur_cuivre_recharge.getText());
		String s = epaisseur_cuivre_recharge.getText();
		return Double.parseDouble(s);
	}

	public void set_epaisseur_cuivre_recharge(String val) {
		this.epaisseur_cuivre_recharge.setText(val);
	}

	public void set_trou_traversant(String b) {

		this.trou_traversant.setVisible(Boolean.parseBoolean(b));
	}

	public String toString() {
		return this.get_trou_traversant() + ";" + this.get_epaisseur_cuivre() + ";"
				+ this.get_epaisseur_cuivre_recharge() + ";" + this.get_taux_remplissage() + ";"
				+ this.get_epaisseur_cuivre_equivalent();
	}

	public void calcul() {// Calcul de l'épaisseur cuivre équivalent pour les couche electrique

		try {

			double cuivre = this.get_epaisseur_cuivre();
			double recharge = this.get_epaisseur_cuivre_recharge();
			double taux_remplissage = this.get_taux_remplissage();
			System.out.println("======================");
			System.out.println("cuivre = " + cuivre);
			System.out.println("recharge = " + recharge);
			System.out.println("taux = " + taux_remplissage);

			double ece = (cuivre + recharge) * (Math.pow((this.get_taux_remplissage()) / 100, 2));



			double arrondi = (double) Math.round(ece * 100) / 100;

			System.out.println("arrondi = " + arrondi);
			System.out.println("======================");
			this.epaisseur_cuivre_equivalent.setText(String.valueOf(arrondi));
		} catch (Exception e) {
			this.epaisseur_cuivre_equivalent.setText(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		}

	}

	public void set_cuivre_recharge(double ece) {

		this.epaisseur_cuivre_recharge.setEditable(true);
		this.epaisseur_cuivre_recharge.setText(String.valueOf(ece));

	}

	public void calcul(CoucheElectriquePcb c1, CoucheElectriquePcb c2) {

	}

}

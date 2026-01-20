package com.app.ancea;

import javafx.beans.property.SimpleStringProperty;

/* Classe Permettant d'afficher les resultats sous forme de "Tableur". 
 * Elle fonctionne avec un TableView.
 * 
 * @param nomVariable nom de la propriete que l'on veut afficher
 * @param resultat	resultat de la propriete
 * @see NouveauComposant.java et OngletPcb.java
 * 
 */

class Propriete {

	private SimpleStringProperty nomVariable;
	public SimpleStringProperty resultat;

	/**
	 * Constructeur Proprieté permettant de donner un nom et un resultat.
	 * 
	 * @param nomVariable Nom de la propriete a afficher
	 */
	public Propriete(String nomVariable) {
		this.nomVariable = new SimpleStringProperty(nomVariable);
		this.resultat = new SimpleStringProperty("0");
	}

	/**
	 * Constructeur Proprieté permettant de donner un nom et un resultat.
	 * 
	 * @param nomVariable Nom de la propriete a afficher
	 * @param resultat    resultat de la propriete version String
	 */
	public Propriete(String nomVariable, String resultat) {
		this.nomVariable = new SimpleStringProperty(nomVariable);
		this.resultat = new SimpleStringProperty(resultat);
	}

	/**
	 * Constructeur Proprieté permettant de donner un nom et un resultat.
	 * 
	 * @param nomVariable nomVariable Nom de la propriete a afficher
	 * @param resultat    resultat de la propriete version double
	 */
	public Propriete(String nomVariable, double resultat) {
		this.nomVariable = new SimpleStringProperty(nomVariable);
		this.resultat = new SimpleStringProperty(String.valueOf(resultat));
	}

	public Propriete(String nomVariable, int resultat) {
		this.nomVariable = new SimpleStringProperty(nomVariable);
		this.resultat = new SimpleStringProperty(String.valueOf(resultat));
	}

	public void set_resultat(String resultat) {
		this.resultat.set(resultat);
	}

	public void set_resultat(double resultat) {
		this.resultat.set(String.valueOf(resultat));
	}

	public SimpleStringProperty get_resultat() {
		return resultat;
	}

	public SimpleStringProperty get_nom_variable() {
		return nomVariable;
	}
}
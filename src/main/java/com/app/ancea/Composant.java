package com.app.ancea;

import java.util.HashMap;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

public class Composant extends VBox implements InterfaceComposant {

	protected StringProperty nom;

	protected ObservableList<Resultat_temperature> paliers_temperatures;
	protected ObservableList<Propriete> proprietes;
	protected TableView<Propriete> tv_proprietes;

	protected double beta, vernis;

	public Image image;

	public Composant(String nom) {

		this.nom = new SimpleStringProperty(nom);

		tv_proprietes = new TableView<Propriete>();

		proprietes = FXCollections.observableArrayList();

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature(this, d));
		}

		beta = Double.NaN;
		vernis = Double.NaN;

	}

	protected void setProprietes() {
	}

	public Composant() {

		nom = new SimpleStringProperty();
		tv_proprietes = new TableView<Propriete>();
		proprietes = FXCollections.observableArrayList();

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature(this, d));
		}
	}

	public void addTemperature(double temperature) {
		paliers_temperatures.add(new Resultat_temperature(this, temperature));
	}

	public String getNom() {
		return nom.get();
	}

	public StringProperty getObservableNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom.set(nom);
	}

	protected double getCte() {
		return (Double) null;
	}

	protected double getE() {
		return (Double) null;
	}

	protected double getX() {
		return (Double) null;
	}

	protected double getY() {
		return (Double) null;
	}

	protected double getBeta() {
		return beta;
	}

	protected double getVernis() {
		return beta;
	}

	public HashMap<String, Double> getComposant() {
		return new HashMap<String, Double>();
	}

	public TableView<
			? extends Resultat_temperature> get_tableView_temperature() {

		TableView<Resultat_temperature> tv = new TableView<>();
		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature(this, d));
		}
		tv.setItems(paliers_temperatures);

		return tv;
	}

	public TableView<Propriete> get_tableView_proprietes() {

		TableColumn<
				Propriete,
				String> colonneVariable = new TableColumn<Propriete, String>(
						"Propriétés");

		colonneVariable.setMinWidth(270);
		colonneVariable
				.setCellValueFactory(c -> c.getValue().get_nom_variable());

		TableColumn<
				Propriete,
				String> colonneResultat = new TableColumn<Propriete, String>(
						"Résultats");
		colonneResultat.setMinWidth(90);
		colonneResultat.setCellValueFactory(c -> c.getValue().get_resultat());

		tv_proprietes.setItems(proprietes);
		tv_proprietes.getColumns().addAll(colonneVariable, colonneResultat);

		return tv_proprietes;
	}

	public double getCoefficientFatigueA() {
		return (Double) null;
	}

	public double getCoefficientFatigueB() {
		return (Double) null;
	}

	protected double getCte(double temperature) {
		// TODO Auto-generated method stub
		return (Double) null;
	}

}

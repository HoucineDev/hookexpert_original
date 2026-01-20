package com.app.ancea;

import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

public class Composant_Capacite extends ComposantLeadless {

	static ImageView image = new ImageView(
			Composant_Capacite.class.getResource("images/Capacite.png")
					.toExternalForm());

	private ObservableList<Resultat_temperature_Capacite> paliers_temperatures;
	private Propriete poids;

	public Composant_Capacite(
			String nom,
			String materiau_utilise,
			double valeurCapacite) {

		super(nom);
		new Capacite(materiau_utilise, valeurCapacite, nom);
		composant = Capacite.getCapacite(nom);

		setProprietes();

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures
					.add(new Resultat_temperature_Capacite(this, d));
		}

	}

	public Composant_Capacite() {
		super();

		setProprietes();

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures
					.add(new Resultat_temperature_Capacite(this, d));
		}
	}

	@Override
	public void setProprietes() {
		for (String nomPropriete : composant.keySet()) {
			double valeur = composant.get(nomPropriete);
			if(!nomPropriete.equals("s") && !nomPropriete.equals("h1")) {
				Propriete p = new Propriete(nomPropriete);
				p.set_resultat(String.valueOf(valeur));
				proprietes.add(p);
			}
		}
		tv_proprietes.setItems(proprietes);
		tv_proprietes.refresh();
	}

	public TableView<
			Resultat_temperature_Capacite> get_tableView_temperature() {
		// Ajouté
		for (Resultat_temperature_Capacite resultat : paliers_temperatures) {
			resultat.setValues();
		} ///
		TableView<
				Resultat_temperature_Capacite> tv_temperatures = new TableView<
						Resultat_temperature_Capacite>();

		TableColumn<
				Resultat_temperature_Capacite,
				String> temperature = new TableColumn<>("Temperatures (°<c)");
		temperature.setMinWidth(120);
		temperature.setStyle("-fx-alignment: CENTER;");
		temperature.setCellValueFactory(
				c -> c.getValue().get_string_Temperature());

		TableColumn<
				Resultat_temperature_Capacite,
				Number> cte = new TableColumn<>("CTE (m/m/°C)");
		cte.setMinWidth(120);
		cte.setStyle("-fx-alignment: CENTER;");
		cte.setCellValueFactory(c -> c.getValue().get_cte1());

		TableColumn<
				Resultat_temperature_Capacite,
				Number> e1 = new TableColumn<>("E (MPa)");
		e1.setMinWidth(120);
		e1.setStyle("-fx-alignment: CENTER;");
		e1.setCellValueFactory(c -> c.getValue().getE1());

		TableColumn<
				Resultat_temperature_Capacite,
				Number> v = new TableColumn<>("v");
		v.setMinWidth(150);
		v.setStyle("-fx-alignment: CENTER;");
		v.setCellValueFactory(c -> c.getValue().getV());

		tv_temperatures.getColumns().addAll(temperature, e1, cte);
		tv_temperatures
				.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tv_temperatures.setItems(paliers_temperatures);

		return tv_temperatures;
	}

	@Override
	public HashMap<String, Double> getComposant() {
		return composant;
	}

	/**
	 * @return Le module d'élasticité de la Capacité.
	 */
	@Override
	public double getE() {
		return Capacite.module_elasticite;
	}

	@Override
	public double getCoefficientFatigueA() {
		return 1.1;
	}

	@Override
	public double getCoefficientFatigueB() {
		return -0.7;
	}

	/**
	 * Permet de passer de la prise en compte d'un seul coefficient de
	 * dilatation à autant que l'on souhaite. Cette fonction remplace:
	 * 
	 * "@Override protected double getCte() { return
	 * Capacite.coefficient_de_dilatation; }"
	 * 
	 * qui ne pouvait traiter que le cas où le coefficient de dilatation était
	 * seul.
	 * 
	 * @param La température à laquelle on souhaite connaître la dilatation.
	 **/
	@Override
	public double getCte(double temperature) {
		double cte = 0;

		for (Resultat_temperature_Capacite resultat : paliers_temperatures) {
			if (resultat.getTemperature() == temperature)
				return resultat.getCte1();
		}

		Resultat_temperature_Capacite resultat = new Resultat_temperature_Capacite(
				this,
				temperature);
		cte = resultat.getCte1();

		paliers_temperatures.add(resultat);

		return cte;
	}

	@Override
	protected double getX() {
		double calcul = 0;

		// calcul = (composant.get("d") - composant.get("b")) / 2;
		calcul = composant.get("d") / 2;

		return calcul;
	}

	@Override
	protected double getY() {
		double calcul = 0;

		// calcul = composant.get("a") / 4;
		calcul = composant.get("a") / 2;
		return calcul;
	}

	@Override
	public String toString() {
		return composant.get("d") + ";" + composant.get("a") + ";"
				+ composant.get("b") + ";" + composant.get("h");
	}
}

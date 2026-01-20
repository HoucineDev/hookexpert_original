package com.app.ancea;

import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

public class ComposantQFN extends ComposantLeadframe {

	static ImageView image = new ImageView(
			ComposantQFN.class.getResource("images/QFN.png").toExternalForm());

	private ObservableList<Resultat_temperature_QFN> paliers_temperatures;

	public ComposantQFN() {
		super();

		setProprietes();

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature_QFN(this, d));
		}
	}

	public ComposantQFN(String nom) {
		super(nom);
		composant = QFN.getComposant(nom);

		setProprietes();

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature_QFN(this, d));
		}
	}

	protected double getXa() {
		return (composant.get("x1") - 0.3) / 2;
	}

	protected double getYa() {
		return (composant.get("y1") - 0.3) / 2;
	}

	protected double getLa() {
		return (getXa() + 0.3) / 2;
	}

	protected double getLb() {
		return (getYa() + 0.3) / 2;
	}

	protected double getSp() {
		return getXa() * getYa();
	}

	protected double getNpxX() {
		double calcul = 0;

		if (composant.get("nx") == 0) {
			return 0;
		} else {
			calcul = (composant.get("nx") - 1) / 2;
		}
		return calcul;
	}

	protected double getU0X() {
		return getNpxX();
	}

	@Override
	protected double getLnp() {
		double calcul = 0;

		calcul = Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2));

		return calcul;
	}

	protected double getXX() {
		double calcul = 0;

		calcul = ((getNpxX() + 1) * (2 * getU0X() + getNpxX() * (-1.0)) / 2.0)
				* composant.get("pas");

		return calcul;
	}

	public double getNpxY() {
		double calcul = 0;

		if (composant.get("ny") % 2 != 0) {
			calcul = Math.floor((composant.get("ny") - 1) / 2);
		} else {
			calcul = composant.get("ny") / 2;
		}

		return calcul;
	}

	public double getU0Y() {
		return composant.get("ny") / 2;
	}

	protected double getXY() {
		double calcul = 0;

		calcul = ((getNpxY() + 1) * (2 * getU0Y() + getNpxY() * ((-1.0) / 2.0)))
				* composant.get("pas");

		return calcul;
	}

	public double getE(double temperature) {
		double e = 0;

		for (Resultat_temperature_QFN resultat : paliers_temperatures) {
			if (resultat.getTemperature() == temperature)
				return resultat.getE12();
		}

		Resultat_temperature_QFN resultat = new Resultat_temperature_QFN(
				this,
				temperature);
		e = resultat.getE12();

		paliers_temperatures.add(resultat);

		return e;
	}

	protected double getCte(double temperature) {
		double cte = 0;

		for (Resultat_temperature_QFN resultat : paliers_temperatures) {
			if (resultat.getTemperature() == temperature)
				return resultat.getCte12();
		}

		Resultat_temperature_QFN resultat = new Resultat_temperature_QFN(
				this,
				temperature);
		cte = resultat.getCte12();

		paliers_temperatures.add(resultat);

		return cte;
	}

	@Override
	protected double getX() {

		double calcul = 0;

		if (composant.get("nx") == 0) {
			calcul = composant.get("x") / 2;
		} else {
			calcul = (composant.get("nx") - 1) / 2 * composant.get("pas");
		}

		return calcul;
	}

	protected double getY() {
		double calcul = 0;

		if (composant.get("ny") == 0 || composant.get("nx") > 0) {
			calcul = composant.get("y") / 2;
		} else {
			calcul = (composant.get("y") - 1) / 2 * composant.get(("pas"));
		}

		return calcul;
	}

	public TableView<Resultat_temperature_QFN> get_tableView_temperature() {

		TableView<Resultat_temperature_QFN> tv_temperatures = new TableView<
				Resultat_temperature_QFN>();

		TableColumn<
				Resultat_temperature_QFN,
				String> temperature = new TableColumn<>("Temperatures (°C)");
		temperature.setMinWidth(120);
		temperature.setStyle("-fx-alignment: CENTER;");
		temperature.setCellValueFactory(
				c -> c.getValue().get_string_Temperature());

		TableColumn<
				Resultat_temperature_QFN,
				Number> cte = new TableColumn<>("CTE (m/m/°C)");
		cte.setMinWidth(120);
		cte.setStyle("-fx-alignment: CENTER;");
		cte.setCellValueFactory(c -> c.getValue().cte);

		TableColumn<
				Resultat_temperature_QFN,
				Number> e1 = new TableColumn<>("E (MPa)");
		e1.setMinWidth(120);
		e1.setStyle("-fx-alignment: CENTER;");
		e1.setCellValueFactory(c -> c.getValue().getE1());

		tv_temperatures.getColumns().addAll(temperature, e1, cte);
		tv_temperatures
				.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tv_temperatures.setItems(paliers_temperatures);

		return tv_temperatures;
	}

	@Override
	public double getCoefficientFatigueA() {
		return 29.084;
	}

	@Override
	public double getCoefficientFatigueB() {
		return -0.997;
	}
}

package com.app.ancea;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

public class Composant_Resistance extends ComposantLeadless {

	static ImageView image = new ImageView(Composant_Resistance.class.getResource("images/Resistance.png").toExternalForm());

	private ObservableList<
			Resultat_temperature_Resistance> paliers_temperatures;
	private Propriete poids;

	public Composant_Resistance(String nom) {
		super(nom);
		composant = Resistance.getResistance(nom);

		setProprietes();

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures
					.add(new Resultat_temperature_Resistance(this, d));
		}

		beta = 8;
		vernis = 1;
	}

	public Composant_Resistance() {
		super();

		setProprietes();

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures
					.add(new Resultat_temperature_Resistance(this, d));
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

	@Override
	public TableView<
			Resultat_temperature_Resistance> get_tableView_temperature() {
		// Ajouté
		for (Resultat_temperature_Resistance resultat : paliers_temperatures) {
			resultat.setValues();
		} ///
		TableView<
				Resultat_temperature_Resistance> tv_temperatures = new TableView<
						Resultat_temperature_Resistance>();

		TableColumn<
				Resultat_temperature_Resistance,
				String> temperature = new TableColumn<>("Températures (°C)");
		temperature.setMinWidth(120);
		temperature.setStyle("-fx-alignment: CENTER;");
		temperature.setCellValueFactory(
				c -> c.getValue().get_string_Temperature());

		TableColumn<
				Resultat_temperature_Resistance,
				Number> epaisseur = new TableColumn<>("Epaisseur");
		epaisseur.setMinWidth(150);
		epaisseur.setStyle("-fx-alignment: CENTER;");
		epaisseur.setCellValueFactory(c -> c.getValue().getEpaisseur());

		TableColumn<
				Resultat_temperature_Resistance,
				Number> cte = new TableColumn<>("CTE (m/m/°C)");
		cte.setMinWidth(120);
		cte.setStyle("-fx-alignment: CENTER;");
		cte.setCellValueFactory(c -> c.getValue().get_cte1());

		TableColumn<
				Resultat_temperature_Resistance,
				Number> e1 = new TableColumn<>("E (MPa)");
		e1.setMinWidth(120);
		e1.setStyle("-fx-alignment: CENTER;");
		e1.setCellValueFactory(c -> c.getValue().getE1());

		TableColumn<
				Resultat_temperature_Resistance,
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

	public void charge_resistance(String path) {
		if (path != null) {
			String line = null;
			try {
				FileReader fr = new FileReader(path);
				BufferedReader br = new BufferedReader(fr);

				while ((line = br.readLine()) != null) {
					String[] numbers = line.split(";");

					this.setD(Outils.StringWithCommaToDouble(numbers[0]));
					this.setA(Outils.StringWithCommaToDouble(numbers[1]));
					this.setB(Outils.StringWithCommaToDouble(numbers[2]));
					this.setH(Outils.StringWithCommaToDouble(numbers[3]));
					this.setG(Outils.StringWithCommaToDouble(numbers[4]));

				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Permet de passer de la prise en compte d'un seul coefficient de
	 * dilatation à autant que l'on souhaite. Cette fonction remplace:
	 * 
	 * "@Override protected double getCte() { return
	 * Resistance.coefficient_de_dilatation; }"
	 * 
	 * qui ne pouvait traiter que le cas où le coefficient de dilatation était
	 * seul.
	 * 
	 * @param La température à laquelle on souhaite connaître la dilatation.
	 **/
	@Override
	public double getCte(double temperature) {
		double cte = 0;

		for (Resultat_temperature_Resistance resultat : paliers_temperatures) {
			if (resultat.getTemperature() == temperature)
				return resultat.getCte1();
		}

		Resultat_temperature_Resistance resultat = new Resultat_temperature_Resistance(
				this,
				temperature);
		cte = resultat.getCte1();

		paliers_temperatures.add(resultat);

		return cte;
	}
	///////

//

	/**
	 * Distance au point neutre en direction X (axe longitudinal)
	 *
	 * CORRECTION 2025-11-11:
	 * Formule corrigée selon PDF "Calcul_resistance___1_.pdf" page 1, équation (1)
	 *
	 * Formule: Lnp_x = (d - b) / 2
	 *
	 * Pour format 1206:
	 * - d = 3.2 mm (longueur totale composant)
	 * - b = 0.4 mm (largeur pad de brasure)
	 * - Lnp_x = (3.2 - 0.4) / 2 = 1.4 mm
	 *
	 * ANCIEN (incorrect): X = d/2 = 1.6 mm (erreur +14%)
	 * NOUVEAU (correct):  X = (d-b)/2 = 1.4 mm ✓
	 *
	 * @return Distance au point neutre X en mm
	 */
	@Override
	protected double getX() {
		double d = composant.get("d");  // Longueur totale
		double b = composant.get("b");  // Largeur pad

		// Formule PDF page 1 équation (1): Lnp_x = (d - b) / 2
		double Lnp_x = (d - b) / 2.0;

		// Log pour debugging (optionnel)
//		if (System.getProperty("debug.resistance") != null) {
			System.out.println(String.format(
					"[Composant_Resistance.getX] d=%.3f, b=%.3f, Lnp_x=%.3f",
					d, b, Lnp_x));
//		}

		return Lnp_x;
	}

	/**
	 * Distance au point neutre en direction Y (axe transversal)
	 *
	 * CORRECTION 2025-11-11:
	 * Formule corrigée selon PDF "Calcul_resistance___1_.pdf" page 1, équation (2)
	 *
	 * Formule: Lnp_y = a / 4
	 *
	 * Pour format 1206:
	 * - a = 1.6 mm (largeur composant)
	 * - Lnp_y = 1.6 / 4 = 0.4 mm
	 *
	 * ANCIEN (incorrect): Y = a/2 = 0.8 mm (erreur +100%)
	 * NOUVEAU (correct):  Y = a/4 = 0.4 mm ✓
	 *
	 * @return Distance au point neutre Y en mm
	 */
	@Override
	protected double getY() {
		double a = composant.get("a");  // Largeur composant

		// Formule PDF page 1 équation (2): Lnp_y = a / 4
		double Lnp_y = a / 4.0;

		// Log pour debugging (optionnel)
//		if (System.getProperty("debug.resistance") != null) {
			System.out.println(String.format(
					"[Composant_Resistance.getY] a=%.3f, Lnp_y=%.3f",
					a, Lnp_y));
//		}

		return Lnp_y;
	}

	@Override
	public HashMap<String, Double> getComposant() {
		return composant;
	}

	/**
	 * @return Le module d'élasticité de la Céramique.
	 */
	@Override
	public double getE() {
		return Ceramique.module_elasticite;
	}

	@Override
	public double getCoefficientFatigueA() {
		return 0.3584;
	}

	@Override
	public double getCoefficientFatigueB() {
		return -0.4;
	}

	@Override
	public String toString() {
		return composant.get("d") + ";" + composant.get("a") + ";"
				+ composant.get("b") + ";" + composant.get("h");
	}
}

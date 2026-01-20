package com.app.ancea;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class Resultat_temperature_pcb extends Resultat_temperature {

	private OngletPcb pcb;
	private StringProperty module_traction_x, module_traction_y,
			module_traction_xy;
	private StringProperty cte_x, cte_y, cte_z, cte_xy;
	private StringProperty module_cisaillement_xy;
	private StringProperty module_cisaillement_z;
	private StringProperty module_flexion_x, module_flexion_y,
			module_flexion_xy;
	private StringProperty dilatation_plan;
	private double moduleTractionX, moduleTractionY, moduleTractionXY,
			moduleFlexionX, moduleFlexionY, moduleFlexionXY, cteX, cteY, cteZ,
			cteXY, moduleCisaillementXY, moduleCisaillementZ;

	protected double temperature;

	public Resultat_temperature_pcb(OngletPcb pcb, double temperature) {

		super(new Composant(), temperature);
		this.pcb = pcb;
		module_traction_x = new SimpleStringProperty();
		module_traction_y = new SimpleStringProperty();
		module_traction_xy = new SimpleStringProperty();

		cte_x = new SimpleStringProperty();
		cte_y = new SimpleStringProperty();
		cte_z = new SimpleStringProperty();
		cte_xy = new SimpleStringProperty();

		dilatation_plan = new SimpleStringProperty();

		module_cisaillement_xy = new SimpleStringProperty();
		module_cisaillement_z = new SimpleStringProperty();

		module_flexion_x = new SimpleStringProperty();
		module_flexion_y = new SimpleStringProperty();
		module_flexion_xy = new SimpleStringProperty();

		this.temperature = temperature;

		System.out.println("[Temp_PCB_CALCUL] temperature = " + temperature);

		setCalculs();

	}

	public void setCalculs() {

		calcul_traction_x();
		calcul_traction_y();
		calcul_traction_xy();

		calcul_cte_x();
		calcul_cte_y();
		calcul_cte_xy();

		calcul_coef_dil_z_ciu();

		calcul_cisaillement_xy_ciu();
		calcul_cisaillement_z_ciu();

		calcul_flexion_x();
		calcul_flexion_y();
		calcul_flexion_xy();
	}

	private void calcul_flexion_xy() {
		moduleFlexionXY = getModuleFlexionXY();
		set_module_flexion_xy(moduleFlexionXY);

	}

	protected double getCteX() {
		double calcul = 0;

		double sommeEpaisseurModule = 0;
		/* TODO: CHANGER NOM COEF DIL X POUR METTRE TISSU */

		/*
		 * *((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i)) .get_nombre()
		 */
		for (int i = 0; i < pcb.liste_couches_pcb.size(); i++) {

			if (i % 2 != 0) {

				/*
				 * System.out.println("Couche X: " + i); System.out.println(
				 * "Nombre X: " + ((CoucheIsolantePcb) pcb.liste_couches_pcb
				 * .get(i)).get_nombre());
				 */
				calcul += pcb.resine.coef_dil_x(
						getTemperature(),
						((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
								.get_choix_tissus())
						// * ((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
						// .get_epaisseur_tissus()
						* ((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
								.get_epaisseur_stratifie()
						* ((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
								.get_nombre()
						* pcb.resine.module_tissus_x(
								getTemperature(),
								((CoucheIsolantePcb) pcb.liste_couches_pcb
										.get(i)).get_choix_tissus());

				sommeEpaisseurModule += // ((CoucheIsolantePcb)
										// pcb.liste_couches_pcb
						// .get(i)).get_epaisseur_tissus()
						((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
								.get_epaisseur_stratifie()
								* ((CoucheIsolantePcb) pcb.liste_couches_pcb
										.get(i)).get_nombre()
								* pcb.resine.module_tissus_x(
										getTemperature(),
										((CoucheIsolantePcb) pcb.liste_couches_pcb
												.get(i)).get_choix_tissus());
			}
		}

		calcul = (calcul + pcb.get_cuivre_thermique() * Ressources.E_CUIVRE
				* Ressources.CTE_CUIVRE) / sommeEpaisseurModule;

		return calcul;
	}

	protected void calcul_cte_x() {
		cteX = getCteX();
		set_cte_x(cteX);
	}

	protected double getCteY() {
		double calcul = 0;

		double sommeEpaisseurModule = 0;
		/* TODO: CHANGER NOM COEF DIL X POUR METTRE TISSU */
		/*
		 * *((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i)) .get_nombre()
		 */
		for (int i = 0; i < pcb.liste_couches_pcb.size(); i++) {
			if (i % 2 != 0) {
				/*
				 * System.out.println("Couche Y: " + i); System.out.println(
				 * "Nombre Y: " + ((CoucheIsolantePcb) pcb.liste_couches_pcb
				 * .get(i)).get_nombre());
				 */
				// Epaisseur stratifie au lieu de epaisseur tissu
				calcul += pcb.resine.coef_dil_y(
						getTemperature(),
						((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
								.get_choix_tissus())
						// * ((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
						// .get_epaisseur_tissus()
						* ((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
								.get_epaisseur_stratifie()
						* ((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
								.get_nombre()
						* pcb.resine.module_tissus_y(
								getTemperature(),
								((CoucheIsolantePcb) pcb.liste_couches_pcb
										.get(i)).get_choix_tissus());

				sommeEpaisseurModule += // ((CoucheIsolantePcb)
										// pcb.liste_couches_pcb
						// .get(i)).get_epaisseur_tissus()
						((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
								.get_epaisseur_stratifie()
								* ((CoucheIsolantePcb) pcb.liste_couches_pcb
										.get(i)).get_nombre()
								* pcb.resine.module_tissus_y(
										getTemperature(),
										((CoucheIsolantePcb) pcb.liste_couches_pcb
												.get(i)).get_choix_tissus());
			}
		}

		calcul = (calcul + pcb.get_cuivre_thermique() * Ressources.E_CUIVRE
				* Ressources.CTE_CUIVRE) / sommeEpaisseurModule;
		return calcul;
	}

	protected double getModuleFlexionXY() {
		double calcul = 0;
		double moduleFlexionX = getModuleFlexionX();
		double moduleFlexionY = getModuleFlexionY();

		calcul = Math.sqrt(2) * moduleFlexionX * moduleFlexionY / Math.sqrt(
				Math.pow(moduleFlexionX, 2) + Math.pow(moduleFlexionY, 2));

		return calcul;
	}

	protected void calcul_cte_y() {
		cteY = getCteY();
		set_cte_y(cteY);
	}

	protected double getCteXY() {
		double calcul = 0;
//		System.out.println("Temperature: "+temperature);
		calcul = Math.sqrt(Math.pow(getCteX(), 2) + Math.pow(getCteY(), 2))
				/ Math.sqrt(2);

		return calcul;
	}

	protected void calcul_cte_xy() {
		cteXY = getCteXY();
		set_cte_xy(cteXY);
	}

	private double getTotalTissus() {

		double calcul = 0;

		for (int i = 0; i < pcb.liste_couches_pcb.size(); i++) {
			if (i % 2 != 0) {
				calcul += (((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
						.get_nombre()
						* ((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
								.get_epaisseur_stratifie());
			}
		}
		return calcul;
	}

	protected double getCalculTractionXY() {
		double calcul = 0;

		double module_traction_x = getCalculTractionX();
		double module_traction_y = getCalculTractionY();
		calcul = (Math.sqrt(2) * module_traction_x * module_traction_y)
				/ Math.sqrt(
						Math.pow(module_traction_x, 2)
								+ Math.pow(module_traction_y, 2));
		return calcul;
	}

	protected void calcul_traction_xy() {
		moduleTractionXY = getCalculTractionXY();
		set_module_traction_xy(moduleTractionXY);
	}

	protected double getCalculTractionX() {
		double calcul = 0;
		double module_isolant = module_isolant_x();
		double cuivre_thermique = pcb.getCuivreThermique();
		double tissus = getTotalTissus();

		calcul = (module_isolant * tissus
				+ Ressources.E_CUIVRE * cuivre_thermique)
				/ (tissus + cuivre_thermique);
		return calcul;
	}

	protected double getCalculTractionY() {
		double calcul = 0;

		double module_isolant = module_isolant_y();
		double cuivre_thermique = pcb.getCuivreThermique();

		double tissus = getTotalTissus();

		calcul = (module_isolant * tissus
				+ Ressources.E_CUIVRE * cuivre_thermique)
				/ (tissus + cuivre_thermique);
		return calcul;
	}

	public void calcul_traction_x() {
		moduleTractionX = getCalculTractionX();
		set_module_traction_x(moduleTractionX);
	}

	public double module_isolant_y() {
		double calcul = 0;

		double tissus = getTotalTissus();

		for (int i = 0; i < pcb.liste_couches_pcb.size(); i++) {
			if (i % 2 != 0) {
				calcul += ((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
						.get_nombre()
						* ((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
								.get_epaisseur_stratifie()
						* pcb.resine.module_tissus_y(
								getTemperature(),
								((CoucheIsolantePcb) pcb.liste_couches_pcb
										.get(i)).get_choix_tissus());

			}
		}

		calcul = calcul / tissus;

		return calcul;
	}

	protected double module_isolant_x() {
		double calcul = 0;

		double somme_tissus = getTotalTissus();

		for (int i = 0; i < pcb.liste_couches_pcb.size(); i++) {
			if (i % 2 != 0) {
				calcul += ((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
						.get_nombre()
						* ((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
								.get_epaisseur_stratifie()
						* pcb.resine.module_tissus_x(
								getTemperature(),
								((CoucheIsolantePcb) pcb.liste_couches_pcb
										.get(i)).get_choix_tissus());
			}
		}

		calcul = calcul / somme_tissus;

		return calcul;
	}

	public void calcul_traction_y() {
		moduleTractionY = getCalculTractionY();
		set_module_traction_y(moduleTractionY);
	}

	public double get_coef_dil_xy_ciu() {
		double a_verre = 0;
		double a_resine = 0;
		double module_traction_verre = 0;
		double module_resine = 0;

		double calcul_module_traction_materiau_isolant = 0;
		double calcul_coef_dil_xy_materiau_isolant = 0;
		double calcul_coef_dil_xy_ciu = 0;

		VerreE verre = new VerreE();
		a_verre = verre.coef_dil_xy(getTemperature());
		module_traction_verre = verre.module_elastique(getTemperature());

		a_resine = pcb.resine.coef_dil_xy(getTemperature());
		module_resine = pcb.resine.module_elastique(getTemperature());

		calcul_coef_dil_xy_materiau_isolant = a_verre + (module_resine
				* module_traction_verre
				/ (module_resine * pcb.get_b_xy()
						+ module_traction_verre * pcb.get_a_xy())
				* pcb.get_a_xy())
				/ (module_traction_verre * pcb.get_b_xy() + (module_resine
						* module_traction_verre
						/ (module_resine * pcb.get_b_xy()
								+ module_traction_verre * pcb.get_a_xy())
						* pcb.get_a_xy()))
				* (a_resine * pcb.get_a_xy() + a_verre * pcb.get_b_xy()
						- a_verre);

		calcul_module_traction_materiau_isolant = (module_resine
				* module_traction_verre
				* (Math.pow((2 * pcb.get_Va() + pcb.get_Vb()), 2))
				/ (4 * module_traction_verre * pcb.get_Va()
						+ 2 * module_resine * pcb.get_Vb()))
				+ (module_traction_verre * pcb.get_Vb() / 2);

		calcul_coef_dil_xy_ciu = (pcb.get_isolant_verre_epoxy()
				* calcul_module_traction_materiau_isolant
				* calcul_coef_dil_xy_materiau_isolant
				+ pcb.get_cuivre_reel() * Ressources.E_CUIVRE
						* Ressources.CTE_CUIVRE)
				/ (pcb.get_isolant_verre_epoxy()
						* calcul_module_traction_materiau_isolant
						+ pcb.get_cuivre_reel() * Ressources.E_CUIVRE);

		calcul_coef_dil_xy_ciu = (calcul_coef_dil_xy_ciu * 100) / 100.0;

		return calcul_coef_dil_xy_ciu;
	}

	public void calcul_coef_dil_xy_ciu() {
		cteXY = get_coef_dil_xy_ciu();
		set_dilatation_plan(cteXY);
	}

	protected double getCteZ() {
		double a_verre = 0;
		double a_resine = 0;

		double calcul_coef_dil_z_materiau_isolant = 0;
		double calcul_coef_dil_z_ciu = 0;

		a_verre = pcb.calcul_a_xy_verre(getTemperature());

		a_resine = pcb.calcul_a_xy_resine(getTemperature());

		calcul_coef_dil_z_materiau_isolant = a_resine * pcb.get_Va()
				+ a_verre * pcb.get_Vb();

		calcul_coef_dil_z_ciu = (pcb.get_isolant_verre_epoxy()
				* calcul_coef_dil_z_materiau_isolant
				+ pcb.get_cuivre_reel() * Ressources.CTE_CUIVRE)
				/ (pcb.get_isolant_verre_epoxy() + pcb.get_cuivre_reel());

		calcul_coef_dil_z_ciu = (calcul_coef_dil_z_ciu * 100) / 100.0;

		return calcul_coef_dil_z_ciu;
	}

	public void calcul_coef_dil_z_ciu() {
		cteZ = getCteZ();
		set_dilatation_z(cteZ);

	}

	public double getEpaisseurPcb() {
		return Outils.nombreChiffreApresVirgule(
				pcb.get_totale_nominal_reel(),
				Ressources.NOMBRE_CHIFFRE_APRES_VIRGULE);
	}

	public void calcul_cisaillement_xy_ciu() {

		double module_traction_verre = 0;
		double module_resine = 0;
		double calcul_module_traction_materiau_isolant = 0;
		double calcul_module_traction_ciu = 0;
		double calcul_cisaillement_ciu = 0;

		VerreE verre = new VerreE();

		module_traction_verre = verre.module_elastique(getTemperature());

		module_resine = pcb.resine.module_elastique(getTemperature());

		calcul_module_traction_materiau_isolant = (module_resine
				* module_traction_verre
				* (Math.pow((2 * pcb.get_Va() + pcb.get_Vb()), 2))
				/ (4 * module_traction_verre * pcb.get_Va()
						+ 2 * module_resine * pcb.get_Vb()))
				+ (module_traction_verre * pcb.get_Vb() / 2);

		calcul_module_traction_ciu = getCalculTractionXY();

		calcul_module_traction_ciu = (calcul_module_traction_ciu * 100) / 100;

		calcul_cisaillement_ciu = calcul_module_traction_ciu
				/ (2 * (1 + Ressources.COEF_POISSON));

		moduleCisaillementXY = calcul_cisaillement_ciu;
		set_module_cisaillement_xy(moduleCisaillementXY);

	}

	public void calcul_cisaillement_z_ciu() {

		double ga = 0;
		double gb = 0;
		double calcul_g_verre_global = 0;
		double calcul_cisaillement_z_ciu = 0;

		ga = pcb.calcul_module_resine(getTemperature())
				/ (2 * (1 + Ressources.COEF_POISSON));

		gb = pcb.calcul_module_traction_verre(getTemperature())
				/ (2 * (1 + Ressources.COEF_POISSON));

		calcul_g_verre_global = (pcb.get_a_xy() + pcb.get_b_xy())
				/ ((pcb.get_a_xy() / ga) + (pcb.get_b_xy() / gb));

		calcul_cisaillement_z_ciu = (pcb.get_isolant_verre_epoxy()
				+ pcb.get_cuivre_thermique())
				/ ((pcb.get_isolant_verre_epoxy() / calcul_g_verre_global)
						+ (pcb.get_cuivre_thermique() / Ressources.G_CUIVRE));

		moduleCisaillementZ = calcul_cisaillement_z_ciu;
		set_module_cisaillement_z(moduleCisaillementZ);
	}

	protected double getModuleFlexionX() {
		double epaisseur_totale_ciu = 0;
		double epaisseur_differentielle = 0;
		double epaisseur_precedente = 0;
		double epaisseur_courrante = 0;
		double module_tissus = 0;

		double module_couche_centrale = pcb.resine.module_tissus_x(
				getTemperature(),
				((CoucheIsolantePcb) pcb.liste_couches_pcb
						.get((pcb.liste_couches_pcb.size() / 2)))
								.get_choix_tissus());

		double epaisseur_centrale = +(pcb.liste_couches_pcb
				.get((pcb.liste_couches_pcb.size() / 2))
				.epaisseur_totale_couche());

		epaisseur_precedente = epaisseur_centrale;
		epaisseur_courrante = epaisseur_centrale;

		for (int i = ((pcb.liste_couches_pcb.size() / 2) - 1); i >= 0; i--) {

			if (i % 2 == 0) {

				epaisseur_courrante = epaisseur_courrante
						+ (((CoucheElectriquePcb) pcb.liste_couches_pcb.get(i))
								.epaisseur_totale_couche() * 2);

				epaisseur_differentielle = epaisseur_differentielle
						+ (Ressources.E_CUIVRE
								* (Math.pow(epaisseur_courrante, 3)
										- (Math.pow(epaisseur_precedente, 3))));

				epaisseur_precedente = epaisseur_precedente
						+ (((CoucheElectriquePcb) pcb.liste_couches_pcb.get(i))
								.epaisseur_totale_couche() * 2);

			}

			else {

				module_tissus = pcb.resine.module_tissus_x(
						getTemperature(),
						((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
								.get_choix_tissus());

				epaisseur_courrante = epaisseur_courrante
						+ (((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
								.epaisseur_totale_couche() * 2);

				epaisseur_differentielle = epaisseur_differentielle
						+ (module_tissus * (Math.pow(epaisseur_courrante, 3)
								- (Math.pow(epaisseur_precedente, 3))));

				epaisseur_precedente = epaisseur_precedente
						+ (((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
								.epaisseur_totale_couche() * 2);

			}

			epaisseur_totale_ciu = epaisseur_totale_ciu
					+ pcb.liste_couches_pcb.get(i).epaisseur_totale_couche()
							* 2;
			module_tissus = 0;

		}

		epaisseur_differentielle = epaisseur_differentielle
				+ module_couche_centrale * (Math.pow(epaisseur_centrale, 3));

		epaisseur_totale_ciu = epaisseur_totale_ciu + epaisseur_centrale;

		double calcul = epaisseur_differentielle
				/ (Math.pow(epaisseur_totale_ciu, 3));

		return calcul;
	}

	public void calcul_flexion_x() {
		moduleFlexionX = getModuleFlexionX();
		set_module_flexion_x(moduleFlexionX);
	}

	protected double getModuleFlexionY() {
		double epaisseur_totale_ciu = 0;
		double epaisseur_differentielle = 0;
		double epaisseur_precedente = 0;
		double epaisseur_courrante = 0;
		double module_tissus = 0;

		double module_couche_centrale = pcb.resine.module_tissus_y(
				getTemperature(),
				((CoucheIsolantePcb) pcb.liste_couches_pcb
						.get((pcb.liste_couches_pcb.size() / 2)))
								.get_choix_tissus());

		double epaisseur_centrale = +(pcb.liste_couches_pcb
				.get((pcb.liste_couches_pcb.size() / 2))
				.epaisseur_totale_couche());

		epaisseur_precedente = epaisseur_centrale;
		epaisseur_courrante = epaisseur_centrale;

		for (int i = ((pcb.liste_couches_pcb.size() / 2) - 1); i >= 0; i--) {

			if (i % 2 == 0) {

				epaisseur_courrante = epaisseur_courrante
						+ (((CoucheElectriquePcb) pcb.liste_couches_pcb.get(i))
								.epaisseur_totale_couche() * 2);

				epaisseur_differentielle = epaisseur_differentielle
						+ (Ressources.E_CUIVRE
								* (Math.pow(epaisseur_courrante, 3)
										- (Math.pow(epaisseur_precedente, 3))));

				epaisseur_precedente = epaisseur_precedente
						+ (((CoucheElectriquePcb) pcb.liste_couches_pcb.get(i))
								.epaisseur_totale_couche() * 2);

			}

			else {

				module_tissus = pcb.resine.module_tissus_y(
						getTemperature(),
						((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
								.get_choix_tissus());

				epaisseur_courrante = epaisseur_courrante
						+ (((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
								.epaisseur_totale_couche() * 2);

				epaisseur_differentielle = epaisseur_differentielle
						+ (module_tissus * (Math.pow(epaisseur_courrante, 3)
								- (Math.pow(epaisseur_precedente, 3))));

				epaisseur_precedente = epaisseur_precedente
						+ (((CoucheIsolantePcb) pcb.liste_couches_pcb.get(i))
								.epaisseur_totale_couche() * 2);

			}

			epaisseur_totale_ciu = epaisseur_totale_ciu
					+ pcb.liste_couches_pcb.get(i).epaisseur_totale_couche()
							* 2;
			module_tissus = 0;

		}

		epaisseur_differentielle = epaisseur_differentielle
				+ module_couche_centrale * (Math.pow(epaisseur_centrale, 3));

		epaisseur_totale_ciu = epaisseur_totale_ciu + epaisseur_centrale;

		double calcul = epaisseur_differentielle
				/ (Math.pow(epaisseur_totale_ciu, 3));

		return calcul;
	}

	protected void calcul_flexion_y() {
		moduleFlexionY = getModuleFlexionY();
		set_module_flexion_y(moduleFlexionY);
	}

	public SimpleStringProperty get_temperature() {
		return new SimpleStringProperty(String.valueOf(getTemperature()));
	}

	public Integer get_int_temperature() {
		return (int) getTemperature();
	}

	public void set_module_traction_y(double traction) {
		module_traction_y.set(String.valueOf((int) traction));
	}

	public void set_cte_x(double cte) {
		cte_x.set(Outils.nombreChiffreScientifiqueApresVirgule(cte));
	}

	public void set_cte_y(double cte) {
		cte_y.set(Outils.nombreChiffreScientifiqueApresVirgule(cte));
	}

	public void set_cte_xy(double cte) {
		cte_xy.set(Outils.nombreChiffreScientifiqueApresVirgule(cte));
	}

	public StringProperty get_cte_x() {
		return cte_x;
	}

	public StringProperty get_cte_y() {
		return cte_y;
	}

	public StringProperty get_cte_xy() {
		return cte_xy;
	}

	public void set_module_traction_x(double traction) {
		module_traction_x.set(String.valueOf((int) traction));
	}

	public void set_module_traction_xy(double traction) {
		module_traction_xy.set(String.valueOf((int) traction));
	}

	public StringProperty get_module_traction_x() {
		return module_traction_x;
	}

	public int get_int_module_traction_x() {
		return (int) Outils
				.StringWithCommaToDouble(module_traction_x.getValue());
	}

	public double get_double_module_traction_x() {
		return Outils.StringWithCommaToDouble(module_traction_x.getValue());
	}

	public StringProperty get_module_traction_y() {
		return module_traction_y;
	}

	public StringProperty get_module_traction_xy() {
		return module_traction_xy;
	}

	public int get_int_module_traction_y() {
		return (int) Outils
				.StringWithCommaToDouble(module_traction_y.getValue());
	}

	public double get_double_module_traction_y() {
		return Outils.StringWithCommaToDouble(module_traction_y.getValue());
	}

	public double get_double_module_traction_xy() {
		return Outils.StringWithCommaToDouble(module_traction_xy.getValue());
	}

	public void set_module_cisaillement_xy(double cisaillement) {
		module_cisaillement_xy.set(String.valueOf((int) cisaillement));
	}

	public StringProperty get_module_cisaillement_xy() {
		return module_cisaillement_xy;
	}

	public int get_int_module_cisaillement_xy() {
		return (int) Outils
				.StringWithCommaToDouble(module_cisaillement_xy.getValue());
	}

	public void set_module_cisaillement_z(double cisaillement) {
		module_cisaillement_z.set(String.valueOf((int) cisaillement));
	}

	public StringProperty get_module_cisaillement_z() {
		return module_cisaillement_z;
	}

	public int get_int_module_cisaillement_z() {
		return (int) Outils
				.StringWithCommaToDouble(module_cisaillement_z.getValue());
	}

	public void set_module_flexion_x(double flexion) {
		module_flexion_x.set(String.valueOf((int) flexion));
	}

	public void set_module_flexion_y(double flexion) {
		module_flexion_y.set(String.valueOf((int) flexion));
	}

	public StringProperty get_module_flexion_x() {
		return module_flexion_x;
	}

	public StringProperty get_module_flexion_y() {
		return module_flexion_y;
	}

	public int get_int_module_flexion_x() {
		return (int) Outils
				.StringWithCommaToDouble(module_flexion_x.getValue());
	}

	public int get_int_module_flexion_y() {
		return (int) Outils
				.StringWithCommaToDouble(module_flexion_y.getValue());
	}

	public void set_dilatation_plan(double dilatation) {
		dilatation_plan.set(String.valueOf(dilatation));
	}

	public StringProperty get_module_dilatation_plan() {
		return cte_xy;
	}

	public double get_double_module_dilatation_plan() {
		return Outils.StringWithCommaToDouble(cte_xy.getValue());
	}

	public void set_dilatation_z(double cte) {
		cte_z.set(Outils.nombreChiffreScientifiqueApresVirgule(cte));
	}

	public StringProperty get_module_dilatation_z() {
		return cte_z;
	}

	public double get_double_module_dilatation_z() {
		return Double.valueOf(Outils.StringWithCommaToDouble(cte_z.getValue()));
	}

	private void set_module_flexion_xy(double moduleFlexionXY) {
		module_flexion_xy.setValue(String.valueOf(moduleFlexionXY));
	}

	private double get_module_flexion_xy() {
		return Double.valueOf(module_flexion_xy.get());
	}

	@SuppressWarnings({
			"unchecked"
	})
	public static TableView<Resultat_temperature_pcb> get_tableview() {

		TableView<
				Resultat_temperature_pcb> tv_resultats_temperatures = new TableView<>();
		tv_resultats_temperatures
				.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		tv_resultats_temperatures.setRowFactory(

				new Callback<
						TableView<Resultat_temperature_pcb>,
						TableRow<Resultat_temperature_pcb>>() {

					@Override
					public TableRow<Resultat_temperature_pcb> call(
							TableView<Resultat_temperature_pcb> tableView) {
						final TableRow<
								Resultat_temperature_pcb> row = new TableRow<>();
						final ContextMenu rowMenu = new ContextMenu();
						MenuItem editItem = new MenuItem("Edit");

						MenuItem removeItem = new MenuItem("Delete");
						removeItem.setOnAction(new EventHandler<ActionEvent>() {

							@Override
							public void handle(ActionEvent event) {
								tv_resultats_temperatures.getItems()
										.remove(row.getItem());
							}
						});
						rowMenu.getItems().addAll(editItem, removeItem);

						// only display context menu for non-empty rows:
						row.contextMenuProperty().bind(
								Bindings.when(row.emptyProperty()).then(rowMenu)
										.otherwise((ContextMenu) null));
						return row;
					}
				});

		TableColumn<
				Resultat_temperature_pcb,
				String> colonne_temperature = new TableColumn<>(
						"Température (°C)");
		colonne_temperature
				.setCellValueFactory(c -> c.getValue().get_temperature());

		TableColumn<
				Resultat_temperature_pcb,
				String> colonne_module_traction_x = new TableColumn<>(
						"E x (MPa)");
		colonne_module_traction_x
				.setCellValueFactory(c -> c.getValue().get_module_traction_x());

		TableColumn<
				Resultat_temperature_pcb,
				String> colonne_module_traction_y = new TableColumn<>(
						"E y (MPa)");
		colonne_module_traction_y
				.setCellValueFactory(c -> c.getValue().get_module_traction_y());

		TableColumn<
				Resultat_temperature_pcb,
				String> colonne_module_traction_xy = new TableColumn<>(
						"E xy (MPa)");
		colonne_module_traction_xy.setCellValueFactory(
				c -> c.getValue().get_module_traction_xy());

		TableColumn<
				Resultat_temperature_pcb,
				String> colonne_cisaillement_xy = new TableColumn<>(
						"G xy (MPa)");
		colonne_cisaillement_xy.setCellValueFactory(
				c -> c.getValue().get_module_cisaillement_xy());

		TableColumn<
				Resultat_temperature_pcb,
				String> colonne_cisaillement_z = new TableColumn<>("G z (MPa)");
		colonne_cisaillement_z.setCellValueFactory(
				c -> c.getValue().get_module_cisaillement_z());

		TableColumn<
				Resultat_temperature_pcb,
				String> colonne_module_flexion_x = new TableColumn<>(
						"Ef x (MPa)");
		colonne_module_flexion_x
				.setCellValueFactory(c -> c.getValue().get_module_flexion_x());

		TableColumn<
				Resultat_temperature_pcb,
				String> colonne_module_flexion_y = new TableColumn<>(
						"Ef y (MPa)");
		colonne_module_flexion_y
				.setCellValueFactory(c -> c.getValue().get_module_flexion_y());

		TableColumn<
				Resultat_temperature_pcb,
				Number> colonne_module_flexion_plan = new TableColumn<>(
						"Ef xy (MPa)");
		colonne_module_flexion_plan.setCellValueFactory(
				c -> new SimpleIntegerProperty(
						(int) c.getValue().get_module_flexion_xy()));

		TableColumn<
				Resultat_temperature_pcb,
				String> colonne_coefficient_dilatation_x = new TableColumn<>(
						"CTE x (m/m/°C)");
		colonne_coefficient_dilatation_x
				.setCellValueFactory(c -> c.getValue().get_cte_x());

		TableColumn<
				Resultat_temperature_pcb,
				String> colonne_coefficient_dilatation_y = new TableColumn<>(
						"CTE y (m/m/°C)");
		colonne_coefficient_dilatation_y
				.setCellValueFactory(c -> c.getValue().get_cte_y());

		TableColumn<
				Resultat_temperature_pcb,
				String> colonne_coefficient_dilatation_plan = new TableColumn<>(
						"CTE xy (m/m/°C)");
		colonne_coefficient_dilatation_plan
				.setCellValueFactory(c -> c.getValue().get_cte_xy());

		TableColumn<
				Resultat_temperature_pcb,
				String> colonne_coefficient_dilatation_z = new TableColumn<>(
						"CTE z (m/m/°C)");
		colonne_coefficient_dilatation_z.setCellValueFactory(
				c -> c.getValue().get_module_dilatation_z());

		tv_resultats_temperatures.getColumns().addAll(
				colonne_temperature,
				colonne_module_traction_x,
				colonne_module_traction_y,
				colonne_module_traction_xy,
				colonne_cisaillement_xy,
				colonne_cisaillement_z,
				colonne_module_flexion_x,
				colonne_module_flexion_y,
				colonne_module_flexion_plan,
				colonne_coefficient_dilatation_x,
				colonne_coefficient_dilatation_y,
				colonne_coefficient_dilatation_plan,
				colonne_coefficient_dilatation_z);

		for (TableColumn<
				Resultat_temperature_pcb,
				?> res : tv_resultats_temperatures.getColumns()) {
			res.setStyle("-fx-alignment: CENTER;");
		}

		return tv_resultats_temperatures;
	}

	public String toString() {
		System.out.println(cteX);
		return temperature + ";" + moduleTractionX + ";" + moduleTractionY + ";"
				+ moduleTractionXY + ";" + moduleCisaillementXY + ";"
				+ moduleCisaillementZ + ";" + moduleFlexionX + ";"
				+ moduleFlexionY + ";" + moduleFlexionXY + ";" + cteX + ";"
				+ cteY + ";" + cteXY + ";" + cteZ;
	}
}

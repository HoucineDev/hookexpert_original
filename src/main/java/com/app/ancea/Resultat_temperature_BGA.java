package com.app.ancea;

/**
 * 
 * Cette classe calcule les paramètres de traction, de flexion et dilatation des
 * composants de la famille des BGA.
 *
 */
public class Resultat_temperature_BGA extends Resultat_temperature {

	ComposantBGA composant;

	double e1_traction;
	double e2_traction;
	double e1_flexion;
	double e2_flexion;
	double cte1, cte2, cte2Plus1;

	public Resultat_temperature_BGA(
			ComposantBGA composant,
			double temperature) {
		super(composant, temperature);

		this.composant = composant;

		e1_traction = 0;
		e2_traction = 0;

		e1_flexion = 0;
		e2_flexion = 0;

		cte1 = 0;
		cte2 = 0;
		cte2Plus1 = 0;

		setTemperature(temperature);

	}

	public void calculs() {
		calcul_cte1();
		calcul_cte2();
		calcul_e1_flexion();
		calcul_e2_flexion();
		calcul_e1_traction();
		calcul_e2_traction();

	}

	/**
	 * @return La position du centre de gravite axe z de la resine dans la zone
	 *         1.
	 */
	public double position_gravite_resine_axe_z_zone1() {
		double calcul;
		double resine_z = composant.getResine_z();
		double pcb_z = composant.getPcb_z();
		double pastille_z = composant.getPuce_z();
		double liaison_z = composant.getEpaisseurLiaison();

		calcul = resine_z / 2 + pcb_z + liaison_z + pastille_z;

		return calcul;
	}

	/**
	 * @return La position du centre de gravite axe z du cuivre C19400 dans la
	 *         zone 1.
	 */
	public double position_gravite_cuivreC19400_axe_z_zone1() {

		double calcul;
		double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();
		double pcb_z = composant.getPcb_z();
		double pastille_z = composant.getPuce_z();
		double liaison_z = composant.getEpaisseurLiaison();

		calcul = cuivre_z / 2 + pcb_z + liaison_z + pastille_z;

		return calcul;
	}

	/**
	 * @return La position du centre de gravité axe z du silicium dans la zone
	 *         1.
	 */
	double position_gravite_silicium_axe_z_zone1() {
		double calcul;
		double pastille_z = composant.getPuce_z();
		double pcb_z = composant.getPcb_z();
		double liaison_z = composant.getEpaisseurLiaison();

		calcul = pastille_z / 2 + liaison_z + pcb_z;

		return calcul;
	}

	/**
	 * @return La position du centre de gravité de la liaison Puce axe z de la
	 *         zone 1.
	 */
	double position_gravite_liaisonPuce_axe_z_zone1() {

		double calcul;
		double liaison_z = composant.getEpaisseurLiaison();
		double pcb_z = composant.getPcb_z();

		calcul = liaison_z / 2 + pcb_z;

		return calcul;
	}

	/**
	 * @return La position du centre de gravité axe z du pcb dans la zone 1.
	 */
	public double position_gravite_pcb_axe_z_zone1() {
		double calcul;
		double pcb_z = composant.getPcb_z();

		calcul = pcb_z / 2;

		return calcul;
	}

	/**
	 * @return la position du centre de gravité axe z equivalent de la zone 1.
	 */
	public double position_gravite_zone1_axe_z_zone1() {

		Silicium s = new Silicium();
		double masseVolumiqueSilicium = s.masse_volumique(0);

		double masseVolumiqueLiaisonPuce;
		double pastille_z = composant.getPuce_z();
		double liaison_z = composant.getEpaisseurLiaison();
		double masseVolumiquePcb = composant.getMasse_volumique();
		double pcb_z = composant.getPcb_z();
		double calcul;

		switch (composant.getLiaisonPuce()) {

		case "Underfill":
			masseVolumiqueLiaisonPuce = Underfill.masseVolumique;
			break;

		case "Die Attach":
			masseVolumiqueLiaisonPuce = DieAttach.masseVolumique;
			break;

		default:
			masseVolumiqueLiaisonPuce = 0;
			Outils.alerteUtilisateur(
					"ERROR",
					"Masse Volumique Liaison Puce Inconnue",
					"Impossible de calculer 'position_gravite_zone1_axe_z_zone1',"
							+ " Problème de valeur de la masse volumique de la"
							+ " liaison puce");
			return (Double) null;
		}

		if (composant instanceof ComposantSBGA) {

			CuivreC19400 cu = new CuivreC19400();
			double masseVolumiqueCuivreC19400 = cu.masse_volumique(0);
			double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();

			calcul = (position_gravite_cuivreC19400_axe_z_zone1()
					* masseVolumiqueCuivreC19400 * cuivre_z
					+ position_gravite_silicium_axe_z_zone1()
							* masseVolumiqueSilicium * pastille_z
					+ position_gravite_liaisonPuce_axe_z_zone1()
							* masseVolumiqueLiaisonPuce * liaison_z
					+ position_gravite_pcb_axe_z_zone1() * masseVolumiquePcb
							* pcb_z)
					/ (cuivre_z * masseVolumiqueCuivreC19400
							+ pastille_z * masseVolumiqueSilicium
							+ liaison_z * masseVolumiqueLiaisonPuce
							+ pcb_z * masseVolumiquePcb);

		} else {

			ResineEncapsulation re = new ResineEncapsulation();
			double masseVolumiqueResine = re.masse_volumique(0);
			double resine_z = composant.getResine_z();

			calcul = (position_gravite_resine_axe_z_zone1()
					* masseVolumiqueResine * resine_z
					+ position_gravite_silicium_axe_z_zone1()
							* masseVolumiqueSilicium * pastille_z
					+ position_gravite_liaisonPuce_axe_z_zone1()
							* masseVolumiqueLiaisonPuce * liaison_z
					+ position_gravite_pcb_axe_z_zone1() * masseVolumiquePcb
							* pcb_z)
					/ (resine_z * masseVolumiqueResine
							+ pastille_z * masseVolumiqueSilicium
							+ liaison_z * masseVolumiqueLiaisonPuce
							+ pcb_z * masseVolumiquePcb);

		}

		return calcul;
	}

	/**
	 * @return Le moment quadratique de la résine dans la zone 1.
	 */
	public double moment_quadratique_resine__zone1() {

		double calcul;
		double pastille_x = composant.getPuce_x();
		double resine_z = composant.getResine_z();

		calcul = pastille_x * Math.pow(resine_z, 3) / 12 + pastille_x * resine_z
				* Math.pow(
						Math.abs(
								position_gravite_resine_axe_z_zone1()
										- position_gravite_zone1_axe_z_zone1()),
						2);
		return calcul;
	}

	/**
	 * @return Le moment quadratique du cuivre C19400
	 */
	public double moment_quadratique__cuivreC19400_zone1() {

		double calcul;
		double pastille_x = composant.getPuce_x();
		double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();

		calcul = pastille_x * Math.pow(cuivre_z, 3) / 12 + pastille_x * cuivre_z
				* Math.pow(
						Math.abs(
								position_gravite_cuivreC19400_axe_z_zone1()
										- position_gravite_zone1_axe_z_zone1()),
						2);
		return calcul;

	}

	/**
	 * @return Le moment quadratique du pcb dans la zone 1.
	 */
	public double moment_quadratique_pcb__zone1() {

		double calcul;
		double pastille_x = composant.getPuce_x();
		double pcb_z = composant.getPcb_z();

		calcul = pastille_x * Math.pow(pcb_z, 3) / 12 + pastille_x * pcb_z
				* Math.pow(
						Math.abs(
								position_gravite_pcb_axe_z_zone1()
										- position_gravite_zone1_axe_z_zone1()),
						2);

		return calcul;
	}

	/**
	 * @return Le moment quadratique du silicium de la zone 1.
	 */
	public double moment_quadratique_silicium__zone1() {

		double calcul;
		double pastille_x = composant.getPuce_x();
		double pastille_z = composant.getPuce_z();

		calcul = pastille_x * Math.pow(pastille_z, 3) / 12 + pastille_x
				* pastille_z
				* Math.pow(
						Math.abs(
								position_gravite_silicium_axe_z_zone1()
										- position_gravite_zone1_axe_z_zone1()),
						2);

		return calcul;
	}

	/**
	 * 
	 * @return Le moment quadratique du matériau de liaison avec la puce (ie.
	 *         Underfill ou DieAttach)
	 */
	public double moment_quadratique_liaisonPuce__zone1() {

		double calcul;
		double pastille_x = composant.getPuce_x();
		double liaison_z = composant.getEpaisseurLiaison();

		calcul = pastille_x * Math.pow(liaison_z, 3) / 12 + pastille_x
				* liaison_z
				* Math.pow(
						Math.abs(
								position_gravite_liaisonPuce_axe_z_zone1()
										- position_gravite_zone1_axe_z_zone1()),
						2);

		return calcul;
	}

	/**
	 * @return Le moment quadratique équivalent de la zone 1
	 */
	public double moment_quadratique_zone1_zone1() {
		double calcul;

		if (composant instanceof ComposantSBGA) {

			calcul = moment_quadratique__cuivreC19400_zone1()
					+ moment_quadratique_silicium__zone1()
					+ moment_quadratique_pcb__zone1()
					+ moment_quadratique_liaisonPuce__zone1();

		} else {

			calcul = moment_quadratique_resine__zone1()
					+ moment_quadratique_silicium__zone1()
					+ moment_quadratique_pcb__zone1()
					+ moment_quadratique_liaisonPuce__zone1();

		}

		return calcul;
	}

	/**
	 * @return Le module de flexion de la zone 1.
	 */
	public double calcul_e1_flexion() {

		double moduleElastiqueLiaisonPuce;
		double calcul;
		double e3Traction;
		double moduleElastiqueSilicium;

		Silicium s = new Silicium();
		moduleElastiqueSilicium = s.module_elastique(getTemperature());

		e3Traction = composant.pcb.calcul_traction_ciu(getTemperature());

		switch (composant.getLiaisonPuce()) {

		case "Underfill":
			Underfill u = new Underfill();
			moduleElastiqueLiaisonPuce = u.module_elasticite(getTemperature());
			break;

		case "Die Attach":
			DieAttach d = new DieAttach();
			moduleElastiqueLiaisonPuce = d.module_elasticite(getTemperature());
			break;

		default:
			moduleElastiqueLiaisonPuce = 0;
			Outils.alerteUtilisateur(
					"ERROR",
					"Module Elasticite Liaison Puce Inconnu",
					"Impossible de calculer le module de flexion e1,"
							+ " Problème de valeur du module d'elasticite de "
							+ "la liaison puce");
			return (Double) null;
		}

		if (composant instanceof ComposantSBGA) {

			CuivreC19400 cu = new CuivreC19400();
			double moduleElastiqueCuivreC19400 = cu
					.module_elasticite(getTemperature());

			calcul = (moduleElastiqueCuivreC19400
					* moment_quadratique__cuivreC19400_zone1()
					+ moduleElastiqueSilicium
							* moment_quadratique_silicium__zone1()
					+ moduleElastiqueLiaisonPuce
							* moment_quadratique_liaisonPuce__zone1()
					+ moment_quadratique_pcb__zone1() * e3Traction)
					/ moment_quadratique_zone1_zone1();

		} else {

			ResineEncapsulation re = new ResineEncapsulation();
			double moduleElastiqueResine = re
					.module_elastique(getTemperature());

			calcul = (moduleElastiqueResine * moment_quadratique_resine__zone1()
					+ moduleElastiqueSilicium
							* moment_quadratique_silicium__zone1()
					+ moduleElastiqueLiaisonPuce
							* moment_quadratique_liaisonPuce__zone1()
					+ moment_quadratique_pcb__zone1() * e3Traction)
					/ moment_quadratique_zone1_zone1();

		}

		int resultat = (int) calcul;
		setE1_flexion(resultat);
		return calcul;

	}

	public double calcul_e1_traction() {

		Silicium s = new Silicium();
		double moduleElastiqueSilicium = s.module_elastique(getTemperature());

		double moduleElastiqueLiaisonPuce;
		double pastille_z = composant.getPuce_z();
		double liaison_z = composant.getEpaisseurLiaison();
		double e3Traction = composant.pcb.calcul_traction_ciu(getTemperature());
		double composant_z = Double.parseDouble(composant.getComposant_z());
		double pcb_z = composant.getPcb_z();
		double calcul;

		switch (composant.getLiaisonPuce()) {

		case "Underfill":
			Underfill u = new Underfill();
			moduleElastiqueLiaisonPuce = u.module_elasticite(getTemperature());
			break;

		case "Die Attach":
			DieAttach d = new DieAttach();
			moduleElastiqueLiaisonPuce = d.module_elasticite(getTemperature());
			break;

		default:
			moduleElastiqueLiaisonPuce = 0;
			Outils.alerteUtilisateur(
					"ERROR",
					"Module Elasticite Liaison Puce Inconnu",
					"Impossible de calculer le module de traction e1,"
							+ " Problème de valeur du module d'elasticite de "
							+ "la liaison puce");
			return (Double) null;

		}

		// Le SBGA n'a pas de Resine mais un drain Cuivre
		if (composant instanceof ComposantSBGA) {

			CuivreC19400 cu = new CuivreC19400();
			double moduleElastiqueCuivreC19400 = cu
					.module_elasticite(getTemperature());

			double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();

			calcul = (pastille_z * moduleElastiqueSilicium
					+ cuivre_z * moduleElastiqueCuivreC19400
					+ pcb_z * e3Traction
					+ moduleElastiqueLiaisonPuce * liaison_z) / composant_z;

		} else {

			ResineEncapsulation re = new ResineEncapsulation();
			double moduleElastiqueResine = re
					.module_elastique(getTemperature());

			double resine_z = composant.getResine_z();

			calcul = (pastille_z * moduleElastiqueSilicium
					+ resine_z * moduleElastiqueResine + pcb_z * e3Traction
					+ moduleElastiqueLiaisonPuce * liaison_z) / composant_z;

		}

		int resultat = (int) calcul;
		setE1_traction(resultat);

		return calcul;
	}

	/**
	 * @return Le module d'élasticité de la zone A
	 */
	public double ea_zone_a() {

		Silicium s = new Silicium();
		double moduleElasticiteSilicium = s.module_elastique(0);
		double pastille_z = composant.getPuce_z();

		double calcul = 0;

		if (composant instanceof ComposantSBGA) {

			CuivreC19400 cu = new CuivreC19400();
			double moduleElasticiteCuivreC19400 = cu
					.module_elasticite(getTemperature());

			double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();

			if (pastille_z + cuivre_z != 0) {

				calcul = (pastille_z * moduleElasticiteSilicium
						+ cuivre_z * moduleElasticiteCuivreC19400)
						/ (pastille_z + cuivre_z);
			}

		} else {

			ResineEncapsulation re = new ResineEncapsulation();
			double moduleElasticiteResine = re.module_elastique(0);

			double resine_z = composant.getResine_z();

			if (pastille_z + resine_z != 0) {

				calcul = (pastille_z * moduleElasticiteSilicium
						+ resine_z * moduleElasticiteResine)
						/ (pastille_z + resine_z);
			}
		}

		return calcul;

	}

	/**
	 * @return Le module de cisaillement de la zone A
	 */
	public double gza_zone_a() {

		Silicium s = new Silicium();
		double moduleCisaillementSilicium = s.module_cisaillement(0);
		double pastille_z = composant.getPuce_z();

		double calcul = 0;

		if (composant instanceof ComposantSBGA) {

			CuivreC19400 cu = new CuivreC19400();
			double moduleCisaillementCuivreC19400 = cu
					.module_cisaillement(getTemperature());

			double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();

			if (pastille_z + cuivre_z != 0) {

				calcul = (moduleCisaillementSilicium
						* moduleCisaillementCuivreC19400
						* (pastille_z + cuivre_z))
						/ (moduleCisaillementSilicium * cuivre_z
								+ moduleCisaillementCuivreC19400 * pastille_z);
			}

		} else {

			ResineEncapsulation re = new ResineEncapsulation();
			double moduleCisaillementResine = re.module_cisaillement(0);

			double resine_z = composant.getResine_z();

			if (pastille_z + resine_z != 0) {

				calcul = (moduleCisaillementSilicium * moduleCisaillementResine
						* (pastille_z + resine_z))
						/ (moduleCisaillementSilicium * resine_z
								+ moduleCisaillementResine * pastille_z);
			}
		}
		return calcul;

	}

	public double cte_silicium_zone_a() {
		Silicium s = new Silicium();
		double cd_xy_s = s.coef_dil_xy(getTemperature());

		return cd_xy_s;

	}

	public double cte_resine_zone_a() {
		ResineEncapsulation re = new ResineEncapsulation();
		double cd_xy_re = re.coef_dil_xy(getTemperature());

		return cd_xy_re;

	}

	public double cte_cuivreC19400_zone_a() {
		CuivreC19400 cu = new CuivreC19400();
		double cd_xy_cu = cu.coef_dil_xy(getTemperature());

		return cd_xy_cu;
	}

	/**
	 * @return Le coefficient de dilatation de la zone A
	 */
	public double cte_a_zone_a() {

		Silicium s = new Silicium();
		double moduleElasticiteSilicium = s.module_elastique(getTemperature());
		double pastille_z = composant.getPuce_z();

		double calcul = 0;

		if (composant instanceof ComposantSBGA) {

			CuivreC19400 cu = new CuivreC19400();
			double moduleElasticiteCuivreC19400 = cu
					.module_elasticite(getTemperature());
			double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();

			if (pastille_z + cuivre_z != 0) {

				calcul = (pastille_z * moduleElasticiteSilicium
						* cte_silicium_zone_a()
						+ cuivre_z * moduleElasticiteCuivreC19400
								* cte_cuivreC19400_zone_a())
						/ (pastille_z * moduleElasticiteSilicium
								+ cuivre_z * moduleElasticiteCuivreC19400);
			}

		} else {

			ResineEncapsulation re = new ResineEncapsulation();
			double moduleElasticiteResine = re
					.module_elastique(getTemperature());
			double resine_z = composant.getResine_z();

			if (pastille_z + resine_z != 0) {

				calcul = (pastille_z * moduleElasticiteSilicium
						* cte_silicium_zone_a()
						+ resine_z * moduleElasticiteResine
								* cte_resine_zone_a())
						/ (pastille_z * moduleElasticiteSilicium
								+ resine_z * moduleElasticiteResine);
			}

		}
		return calcul;

	}

	/**
	 * 
	 * @return Le module de flexion de la zone 4
	 */
	public double calcul_e4_flexion() {

		double calcul;
		double e3Traction = composant.pcb.calcul_traction_ciu(getTemperature());
		double pcb_z = composant.getPcb_z();
		double moduleElasticiteLiaisonPuce;
		double liaison_z = composant.getEpaisseurLiaison();

		switch (composant.getLiaisonPuce()) {

		case "Underfill":
			Underfill u = new Underfill();
			moduleElasticiteLiaisonPuce = u.module_elasticite(getTemperature());
			break;

		case "Die Attach":
			DieAttach d = new DieAttach();
			moduleElasticiteLiaisonPuce = d.module_elasticite(getTemperature());
			break;

		default:
			moduleElasticiteLiaisonPuce = 0;
			Outils.alerteUtilisateur(
					"ERROR",
					"Module Elasticite Liaison Puce Inconnu",
					"Impossible de calculer le module de flexion e4,"
							+ " Problème de valeur du module d'elasticite de "
							+ "la liaison puce");
			return (Double) null;

		}

		calcul = (e3Traction * pcb_z + moduleElasticiteLiaisonPuce * liaison_z)
				/ (pcb_z + liaison_z);

		return calcul;
	}

	/**
	 * 
	 * @return Le module de cisaillement de la zone 4
	 */
	public double calcul_g4_z() {

		double calcul;
		double g3_z = composant.pcb.calcul_cisaillement_z_ciu(getTemperature());
		double moduleCisaillementLiaisonPuce;
		double liaison_z = composant.getEpaisseurLiaison();
		double pcb_z = composant.getPcb_z();

		switch (composant.getLiaisonPuce()) {

		case "Underfill":
			Underfill u = new Underfill();
			moduleCisaillementLiaisonPuce = u
					.module_elasticite(getTemperature())
					/ (2 * (1 + Underfill.coefficientPoisson));
			break;

		case "Die Attach":
			DieAttach d = new DieAttach();
			moduleCisaillementLiaisonPuce = d
					.module_elasticite(getTemperature())
					/ (2 * (1 + DieAttach.coefficientPoisson));
			break;

		default:
			moduleCisaillementLiaisonPuce = 0;
			Outils.alerteUtilisateur(
					"ERROR",
					"Module Cisaillement Liaison Puce Inconnu",
					"Impossible de calculer le module de cisaillement g4z,"
							+ " Problème de valeur du module de cisaillement de "
							+ "la liaison puce");
			return (Double) null;

		}

		calcul = g3_z * moduleCisaillementLiaisonPuce * (liaison_z + pcb_z)
				/ (moduleCisaillementLiaisonPuce * pcb_z + g3_z * liaison_z);

		return calcul;
	}

	public double t1_zone1() {

		double calcul;
		double liaison_z = composant.getEpaisseurLiaison();
		double pcb_z = composant.getPcb_z();
		double pastille_y = composant.getPuce_y();
		double pastille_z = composant.getPuce_z();

		if (composant instanceof ComposantSBGA) {

			double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();

			calcul = ea_zone_a() * gza_zone_a() * (pastille_z + cuivre_z)
					* (calcul_e4_flexion() * (liaison_z + pcb_z)
							- 4 * calcul_g4_z() * pastille_y)
					* (calcul_cte4() - cte_a_zone_a());

		} else {

			double resine_z = composant.getResine_z();

			calcul = ea_zone_a() * gza_zone_a() * (pastille_z + resine_z)
					* (calcul_e4_flexion() * (liaison_z + pcb_z)
							- 4 * calcul_g4_z() * pastille_y)
					* (calcul_cte4() - cte_a_zone_a());
		}

		return calcul;

	}

	public double t2_zone1() {

		double calcul;
		double liaison_z = composant.getEpaisseurLiaison();
		double pcb_z = composant.getPcb_z();
		double pastille_y = composant.getPuce_y();
		double pastille_z = composant.getPuce_z();

		if (composant instanceof ComposantSBGA) {

			double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();

			calcul = calcul_g4_z() * calcul_e4_flexion() * (liaison_z + pcb_z)
					* (4 * gza_zone_a() * pastille_y
							+ ea_zone_a() * (pastille_z + cuivre_z));

		} else {

			double resine_z = composant.getResine_z();

			calcul = calcul_g4_z() * calcul_e4_flexion() * (liaison_z + pcb_z)
					* (4 * gza_zone_a() * pastille_y
							+ ea_zone_a() * (pastille_z + resine_z));
		}

		return calcul;

	}

	public double t3_zone1() {

		double calcul;
		double pastille_z = composant.getPuce_z();
		double pastille_y = composant.getPuce_y();
		double liaison_z = composant.getEpaisseurLiaison();
		double pcb_z = composant.getPcb_z();

		if (composant instanceof ComposantSBGA) {

			double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();

			calcul = ea_zone_a() * gza_zone_a() * (pastille_z + cuivre_z)
					* (4 * calcul_g4_z() * pastille_y
							+ calcul_e4_flexion() * (liaison_z + pcb_z));

		} else {

			double resine_z = composant.getResine_z();

			calcul = ea_zone_a() * gza_zone_a() * (pastille_z + resine_z)
					* (4 * calcul_g4_z() * pastille_y
							+ calcul_e4_flexion() * (liaison_z + pcb_z));

		}

		return calcul;

	}

	/**
	 * @return Le coefficient de dilatation de la zone 1.
	 */
	public double calcul_cte1() {

		double pastille_z = composant.getPuce_z();
		double cte3 = composant.pcb.calcul_coef_dil_xy_ciu(getTemperature());
		double liaison_z = composant.getEpaisseurLiaison();
		double calcul;

		if (composant instanceof ComposantSBGA) {
			double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();

			if (pastille_z + cuivre_z + liaison_z != 0) {

				calcul = calcul_cte4() + t1_zone1() / (t2_zone1() + t3_zone1());

			} else {

				calcul = cte3;
			}

		} else {

			double resine_z = composant.getResine_z();

			if (pastille_z + resine_z + liaison_z != 0) {

				calcul = calcul_cte4() + t1_zone1() / (t2_zone1() + t3_zone1());

			} else {

				calcul = cte3;
			}
		}

		setCte1(calcul);

		return calcul;// attention à la conversion d'unité
	}

	/**
	 * @return Le coefficient de dilatation de la zone 4
	 */
	public double calcul_cte4() {

		double calcul;
		int e3Traction = composant.pcb.calcul_traction_ciu(getTemperature());
		double cte3 = composant.pcb.calcul_coef_dil_xy_ciu(getTemperature());
		double pcb_z = composant.getPcb_z();
		double liaison_z = composant.getEpaisseurLiaison();
		double moduleElasticiteLiaisonPuce;
		double cteLiaisonPuce;

		switch (composant.getLiaisonPuce()) {

		case "Underfill":
			Underfill u = new Underfill();
			moduleElasticiteLiaisonPuce = u.module_elasticite(getTemperature());
			cteLiaisonPuce = u.coef_dil_xy(getTemperature());
			break;

		case "Die Attach":
			DieAttach d = new DieAttach();
			moduleElasticiteLiaisonPuce = d.module_elasticite(getTemperature());
			cteLiaisonPuce = d.coef_dil_xy(getTemperature());
			break;

		default:
			moduleElasticiteLiaisonPuce = 0;
			cteLiaisonPuce = 0;
			Outils.alerteUtilisateur(
					"ERROR",
					"Module Elasticite et/ou CTE Liaison Puce Inconnu",
					"Impossible de calculer le module de dilatation cte4,"
							+ " Problème de valeur du module d'elasticite et/ou du cte de "
							+ "la liaison puce");
			return (Double) null;
		}

		calcul = (e3Traction * cte3 * pcb_z
				+ moduleElasticiteLiaisonPuce * cteLiaisonPuce * liaison_z)
				/ (e3Traction * pcb_z
						+ moduleElasticiteLiaisonPuce * liaison_z);

		return calcul;
	}

	/**
	 * @return Le coefficient de traction E2
	 */
	public double calcul_e2_traction() {

		double pcb_z = composant.getPcb_z();
		double composant_z = composant.getComposantZ();
		double pastille_z = composant.getPuce_z();
		double e3Traction = composant.pcb.calcul_traction_ciu(getTemperature());

		double calcul;

		if (composant instanceof ComposantSBGA) {

			CuivreC19400 cu = new CuivreC19400();
			double moduleElasticiteCuivreC19400 = cu
					.module_elasticite(getTemperature());
			double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();
			double cuivre_z2 = ((ComposantSBGA) composant).getCuivreZ2();
			double composant_z2 = ((ComposantSBGA) composant).getComposantZ2();

			calcul = (cuivre_z2 * moduleElasticiteCuivreC19400
					+ pcb_z * e3Traction) / composant_z2;

		} else {

			ResineEncapsulation re = new ResineEncapsulation();
			double moduleElasticiteResine = re
					.module_elastique(getTemperature());
			double resine_z = composant.getResine_z();

			calcul = ((resine_z + pastille_z) * moduleElasticiteResine
					+ pcb_z * e3Traction) / composant_z;

		}

		int resultat = (int) calcul;
		setE2_traction(resultat);

		return calcul;
	}

	public double alpha_a_zone2() {
		if (composant instanceof ComposantSBGA) {
			return cte_cuivreC19400_zone_a();
		} else {
			return cte_resine_zone_a();
		}

	}

	public double t1_zone2() {

		double pastille_z = composant.getPuce_z();
		double e3Traction = composant.pcb.calcul_traction_ciu(getTemperature());
		double pcb_z = composant.getPcb_z();
		double pastille_x = composant.getPuce_x();
		double cte3 = composant.pcb.calcul_coef_dil_xy_ciu(getTemperature());
		double liaison_z = composant.getEpaisseurLiaison();
		double g3z = composant.pcb.calcul_cisaillement_z_ciu(getTemperature());
		double calcul;

		if (composant instanceof ComposantSBGA) {

			CuivreC19400 cu = new CuivreC19400();
			double moduleElastiqueCuivreC19400 = cu
					.module_elasticite(getTemperature());
			double moduleCisaillementCuivreC19400 = cu
					.module_cisaillement(getTemperature());

			double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();
			double cuivre_z2 = ((ComposantSBGA) composant).getCuivreZ2();
			double cuivre_x = ((ComposantSBGA) composant).getCuivreX();

			calcul = moduleElastiqueCuivreC19400
					* moduleCisaillementCuivreC19400 * cuivre_z2
					* (e3Traction * pcb_z - 4 * g3z * (cuivre_x - pastille_x))
					* (cte3 - alpha_a_zone2());

		} else {

			ResineEncapsulation re = new ResineEncapsulation();
			double moduleElastiqueResine = re.module_elastique(getTemperature());
			double moduleCisaillementResine = re.module_cisaillement(getTemperature());

			double resine_z = composant.getResine_z();
			double resine_x = composant.getResine_x();

			calcul = moduleElastiqueResine * moduleCisaillementResine
					* (resine_z + pastille_z + liaison_z)
					* (e3Traction * pcb_z - 4 * g3z * (resine_x - pastille_x))
					* (cte3 - alpha_a_zone2());

		}

		return calcul;

	}

	public double t2_zone2() {

		double g3z = composant.pcb.calcul_cisaillement_z_ciu(getTemperature());
		double e3Traction = composant.pcb.calcul_traction_ciu(getTemperature());
		double pcb_z = composant.getPcb_z();
		double pastille_x = composant.getPuce_x();
		double pastille_z = composant.getPuce_z();
		double liaison_z = composant.getEpaisseurLiaison();
		double calcul;

		if (composant instanceof ComposantSBGA) {

			CuivreC19400 cu = new CuivreC19400();
			double moduleElastiqueCuivreC19400 = cu
					.module_elasticite(getTemperature());
			double moduleCisaillementCuivreC19400 = cu
					.module_cisaillement(getTemperature());
			double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();
			double cuivre_z2 = ((ComposantSBGA) composant).getCuivreZ2();
			double cuivre_x = ((ComposantSBGA) composant).getCuivreX();

			calcul = g3z * e3Traction * pcb_z
					* (4 * moduleCisaillementCuivreC19400
							* (cuivre_x - pastille_x)
							+ moduleElastiqueCuivreC19400 * cuivre_z2);

		} else {

			ResineEncapsulation re = new ResineEncapsulation();
			double moduleElastiqueResine = re
					.module_elastique(getTemperature());
			double moduleCisaillementResine = re
					.module_cisaillement(getTemperature());
			double resine_z = composant.getResine_z();
			double resine_x = composant.getResine_x();

			calcul = g3z * e3Traction * pcb_z
					* (4 * moduleCisaillementResine * (resine_x - pastille_x)
							+ moduleElastiqueResine
									* (resine_z + pastille_z + liaison_z));

		}

		return calcul;

	}

	public double t3_zone2() {

		double pastille_z = composant.getPuce_z();
		double g3z = composant.pcb.calcul_cisaillement_z_ciu(getTemperature());
		double pastille_x = composant.getPuce_x();
		double e3Traction = composant.pcb.calcul_traction_ciu(getTemperature());
		double pcb_z = composant.getPcb_z();
		double liaison_z = composant.getEpaisseurLiaison();
		double calcul;

		if (composant instanceof ComposantSBGA) {

			CuivreC19400 cu = new CuivreC19400();
			double moduleElastiqueCuivreC19400 = cu
					.module_elasticite(getTemperature());
			double moduleCisaillementCuivreC19400 = cu
					.module_cisaillement(getTemperature());

			double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();
			double cuivre_z2 = ((ComposantSBGA) composant).getCuivreZ2();
			double cuivre_x = ((ComposantSBGA) composant).getCuivreX();

			calcul = moduleElastiqueCuivreC19400
					* moduleCisaillementCuivreC19400 * cuivre_z2
					* (4 * g3z * (cuivre_x - pastille_x) + e3Traction * pcb_z);

		} else {

			ResineEncapsulation re = new ResineEncapsulation();
			double moduleElastiqueResine = re
					.module_elastique(getTemperature());
			double moduleCisaillementResine = re
					.module_cisaillement(getTemperature());
			double resine_z = composant.getResine_z();
			double resine_x = composant.getResine_x();

			calcul = moduleElastiqueResine * moduleCisaillementResine
					* (resine_z + pastille_z + liaison_z)
					* (4 * g3z * (resine_x - pastille_x) + e3Traction * pcb_z);

		}

		return calcul;

	}

	/**
	 * @return La position du centre de gravité axe z de la résine dans la zone
	 *         2.
	 */
	public double position_gravite_resine_axe_z_zone2() {

		double calcul;
		double resine_z = composant.getResine_z();
		double pastille_z = composant.getPuce_z();
		double pcb_z = composant.getPcb_z();
		double liaison_z = composant.getEpaisseurLiaison();

		calcul = (resine_z + pastille_z + liaison_z) / 2 + pcb_z;

		return calcul;
	}

	/**
	 * @return La position du centre de gravité axe z du cuivre C19400 dans la
	 *         zone 2.
	 */
	public double position_gravite_cuivreC19400_axe_z_zone2() {

		double calcul = 0;
		double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();
		double cuivre_z2 = ((ComposantSBGA) composant).getCuivreZ2();
		double pastille_z = composant.getPuce_z();
		double pcb_z = composant.getPcb_z();
		double liaison_z = composant.getEpaisseurLiaison();

		calcul = cuivre_z2 / 2 + pcb_z + liaison_z + pastille_z;

		return calcul;

	}

	/**
	 * @return La position du centre de gravité axe z du pcb dans la zone 2.
	 */
	public double position_gravite_pcb_axe_z_zone2() {

		double calcul = 0;
		double pcb_z = composant.getPcb_z();

		calcul = pcb_z / 2;

		return calcul;
	}

	/**
	 * @return La position du centre de gravité axe z équivalente dans la zone
	 *         2.
	 */
	public double position_gravite_zone2_axe_z_zone2() {

		double pastille_z = composant.getPuce_z();
		double pcb_z = composant.getPcb_z();
		double masseVolumiquePcb = composant.getMasse_volumique();
		double calcul;

		if (composant instanceof ComposantSBGA) {

			CuivreC19400 cu = new CuivreC19400();
			double masseVolumiqueCuivreC19400 = cu.masse_volumique(0);

			double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();

			calcul = (position_gravite_cuivreC19400_axe_z_zone2()
					* (cuivre_z + pastille_z) * masseVolumiqueCuivreC19400
					+ position_gravite_pcb_axe_z_zone2() * pcb_z
							* masseVolumiquePcb)
					/ ((cuivre_z + pastille_z) * masseVolumiqueCuivreC19400
							+ pcb_z * masseVolumiquePcb);

		} else {

			ResineEncapsulation re = new ResineEncapsulation();
			double masseVolumiqueResine = re.masse_volumique(0);

			double resine_z = composant.getResine_z();

			calcul = (position_gravite_resine_axe_z_zone2()
					* (resine_z + pastille_z) * masseVolumiqueResine
					+ position_gravite_pcb_axe_z_zone2() * pcb_z
							* masseVolumiquePcb)
					/ ((resine_z + pastille_z) * masseVolumiqueResine
							+ pcb_z * masseVolumiquePcb);

		}

		return calcul;

	}

	/**
	 * @return Le moment quadratique de la résine dans la zone 2.
	 */
	public double moment_quadratique_resine_zone2() {

		double calcul;
		double resine_z = composant.getResine_z();
		double resine_x = composant.getResine_x();
		double pastille_z = composant.getPuce_z();
		double pastille_x = composant.getPuce_x();
		double liaison_z = composant.getEpaisseurLiaison();

		calcul = (resine_x - pastille_x)
				* Math.pow(liaison_z + resine_z + pastille_z, 3) / 12
				+ (resine_x - pastille_x) * (liaison_z + resine_z + pastille_z)
						* Math.pow(
								Math.abs(
										position_gravite_resine_axe_z_zone2()
												- position_gravite_zone2_axe_z_zone2()),
								2);

		return calcul;
	}

	/**
	 * @return Le moment quadratique du Cuivre C19400 dans la zone 2.
	 */
	public double moment_quadratique_cuivreC19400_zone2() {

		double calcul;
		double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();
		double cuivre_z2 = ((ComposantSBGA) composant).getCuivreZ2();
		double cuivre_x = ((ComposantSBGA) composant).getCuivreX();
		double pastille_z = composant.getPuce_z();
		double pastille_x = composant.getPuce_x();
		double liaison_z = composant.getEpaisseurLiaison();

		calcul = (cuivre_x - pastille_x) * Math.pow(cuivre_z2, 3) / 12
				+ (cuivre_x - pastille_x) * cuivre_z2 * Math.pow(
						Math.abs(
								position_gravite_cuivreC19400_axe_z_zone2()
										- position_gravite_zone2_axe_z_zone2()),
						2);

		return calcul;
	}

	/**
	 * @return Le moment quadratique du pcb dans la zone 2.
	 */
	public double moment_quadratique_pcb_zone2() {

		double calcul;

		double pastille_x = composant.getPuce_x();
		double pcb_z = composant.getPcb_z();

		if (composant instanceof ComposantSBGA) {

			double cuivre_x = ((ComposantSBGA) composant).getCuivreX();

			calcul = (cuivre_x - pastille_x) * Math.pow(pcb_z, 3) / 12
					+ (cuivre_x - pastille_x) * pcb_z * Math.pow(
							Math.abs(
									position_gravite_pcb_axe_z_zone2()
											- position_gravite_zone2_axe_z_zone2()),
							2);

		} else {

			double resine_x = composant.getResine_x();

			calcul = (resine_x - pastille_x) * Math.pow(pcb_z, 3) / 12
					+ (resine_x - pastille_x) * pcb_z * Math.pow(
							Math.abs(
									position_gravite_pcb_axe_z_zone2()
											- position_gravite_zone2_axe_z_zone2()),
							2);
		}

		return calcul;

	}

	public double moment_quadratique_zone2_zone2() {

		double calcul;

		if (composant instanceof ComposantSBGA) {

			calcul = moment_quadratique_cuivreC19400_zone2()
					+ moment_quadratique_pcb_zone2();

		} else {

			calcul = moment_quadratique_resine_zone2()
					+ moment_quadratique_pcb_zone2();

		}

		return calcul;

	}

	public double calcul_e2_flexion() {

		double e3Traction = composant.pcb.calcul_traction_ciu(getTemperature());
		double calcul;

		if (composant instanceof ComposantSBGA) {

			CuivreC19400 cu = new CuivreC19400();
			double moduleElasticiteCuivreC19400 = cu
					.module_elasticite(getTemperature());

			calcul = (moduleElasticiteCuivreC19400
					* this.moment_quadratique_cuivreC19400_zone2()
					+ e3Traction * this.moment_quadratique_pcb_zone2())
					/ this.moment_quadratique_zone2_zone2();

		} else {

			ResineEncapsulation re = new ResineEncapsulation();
			double moduleElasticiteResine = re
					.module_elastique(getTemperature());

			calcul = (moduleElasticiteResine
					* this.moment_quadratique_resine_zone2()
					+ e3Traction * this.moment_quadratique_pcb_zone2())
					/ this.moment_quadratique_zone2_zone2();

		}

		int resultat = (int) calcul;
		setE2_flexion(resultat);

		return resultat;
	}

	public double calcul_cte2() {
		double calcul;

		calcul = (composant.pcb.calcul_coef_dil_xy_ciu(getTemperature()))
				+ (t1_zone2() / ((t2_zone2() + t3_zone2())));

		setCte2(calcul);
		return calcul;

	}

	/**
	 * Calcule le coefficient de dilatation de la liaison entre la zone 1 et 2
	 * selon x.
	 */
	public double calcul_cte2Plus1_x() {
		double calcul;
		double pastille_x = composant.getPuce_x();
		double composant_z = composant.getComposantZ();
		double pcb_z = composant.getPcb_z();

		if (composant instanceof ComposantSBGA) {

			double cuivre_x = ((ComposantSBGA) composant).getCuivreX();
			double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();

			calcul = (calcul_cte1() * pastille_x * composant_z
					* calcul_e1_traction()
					+ calcul_cte2() * (cuivre_x - pastille_x)
							* (cuivre_z + pcb_z) * calcul_e2_traction())
					/ (pastille_x * composant_z * calcul_e1_traction()
							+ (cuivre_x - pastille_x) * (cuivre_z + pcb_z)
									* calcul_e2_traction());
		} else {

			double resine_x = composant.getResine_x();
			double resine_z = composant.getResine_z();

			calcul = (calcul_cte1() * pastille_x * composant_z
					* calcul_e1_traction()
					+ calcul_cte2() * (resine_x - pastille_x)
							* (resine_z + pcb_z) * calcul_e2_traction())
					/ (pastille_x * composant_z * calcul_e1_traction()
							+ (resine_x - pastille_x) * (resine_z + pcb_z)
									* calcul_e2_traction());
		}
		return calcul;
	}

	/**
	 * Calcule le coefficient de dilatation de la liaison entre la zone 1 et 2
	 * selon y.
	 */
	public double calcul_cte2Plus1_y() {
		double calcul;
//		double pastille_y = composant.getPuce_x(); // <--- ERREUR ICI
		double pastille_y = composant.getPuce_y();
		double composant_z = composant.getComposantZ();
		double pcb_z = composant.getPcb_z();

		if (composant instanceof ComposantSBGA) {

			double cuivre_y = ((ComposantSBGA) composant).getCuivreY();
			double cuivre_z = ((ComposantSBGA) composant).getCuivreZ();

			calcul = (calcul_cte1() * pastille_y * composant_z
					* calcul_e1_traction()
					+ calcul_cte2() * (cuivre_y - pastille_y)
							* (cuivre_z + pcb_z) * calcul_e2_traction())
					/ (pastille_y * composant_z * calcul_e1_traction()
							+ (cuivre_y - pastille_y) * (cuivre_z + pcb_z)
									* calcul_e2_traction());
		} else {

			double resine_y = composant.getResine_y();
			double resine_z = composant.getResine_z();

			calcul = (calcul_cte1() * pastille_y * composant_z
					* calcul_e1_traction()
					+ calcul_cte2() * (resine_y - pastille_y)
							* (resine_z + pcb_z) * calcul_e2_traction())
					/ (pastille_y * composant_z * calcul_e1_traction()
							+ (resine_y - pastille_y) * (resine_z + pcb_z)
									* calcul_e2_traction());
		}
		return calcul;
	}

	public double calcul_cte2Plus1_y_CORRIGE() {
		double calcul;

		// CORRECTION 1 : Utilisation de getPuce_y()
		double pastille_y = composant.getPuce_y();
		double composant_z = composant.getComposantZ();
		double pcb_z = composant.getPcb_z();

		double resine_y = composant.getResine_y();
		double resine_z = composant.getResine_z();

		// Note : Pour être physiquement cohérent, si on inclut le PCB_z dans le terme 2,
		// on devrait théoriquement l'inclure dans le terme 1 ou l'exclure des deux.
		// Ici, je garde votre logique mais corrige le bug de variable.

		double K1 = pastille_y * composant_z * calcul_e1_traction();
		double K2 = (resine_y - pastille_y) * (resine_z + pcb_z) * calcul_e2_traction();

		calcul = (calcul_cte1() * K1 + calcul_cte2() * K2) / (K1 + K2);

		return calcul;
	}

	/**
	 * Calcule le coefficient de dilatation de la liaison entre la zone 1 et 2
	 * selon xy.
	 */
	public double calcul_cte2Plus1_xy() {
		double calcul;

		calcul = Math.sqrt(
				Math.pow(calcul_cte2Plus1_x(), 2)
						+ Math.pow(calcul_cte2Plus1_y(), 2))
				/ Math.sqrt(2);

		setCte2Plus1(calcul);
		return calcul;
	}

	public void calcul() {
		try {
			calcul_e1_flexion();
			calcul_e1_traction();
			calcul_e2_flexion();
			calcul_e2_traction();
			calcul_cte1();
			calcul_cte2();
			calcul_cte2Plus1_xy();
		} catch (NullPointerException e) {

		}

		// Pour afficher touts les résulats de calcul decommenter ligne juste en
		// dessous
		affichageResultatsCalcul();

	}

	/**
	 * Permet d'afficher tous les paramètres utilisés dans cette classe afin de
	 * les omparer à des valers de référence
	 */
	public void affichageResultatsCalcul() {
		System.out.println(
				"\n\nMasse volumique: " + composant.pcb.getMasseVolumique());
		if (composant instanceof ComposantSBGA) {
			System.out.println(
					"Position gravite cuivreC19400 zone1: "
							+ position_gravite_cuivreC19400_axe_z_zone1());
			System.out.println(
					"Moment quadratique CuivreC19400 zone1: "
							+ moment_quadratique__cuivreC19400_zone1());
		} else {
			System.out.println(
					"Position gravite resine zone1: "
							+ position_gravite_resine_axe_z_zone1());
			System.out.println(
					"Moment quadratique resine zone1: "
							+ moment_quadratique_resine__zone1());
		}

		System.out.println(
				"Position gravite silicium zone1: "
						+ position_gravite_silicium_axe_z_zone1());
		System.out.println(
				"Moment quadratique silicium zone1: "
						+ moment_quadratique_silicium__zone1());
		System.out.println(
				"Position gravite liaison zone1: "
						+ position_gravite_liaisonPuce_axe_z_zone1());
		System.out.println(
				"Moment quadratique liaison zone1: "
						+ moment_quadratique_liaisonPuce__zone1());
		System.out.println(
				"Position gravite pcb zone1: "
						+ position_gravite_pcb_axe_z_zone1());
		System.out.println(
				"Moment quadratique pcb zone1: "
						+ moment_quadratique_pcb__zone1());
		System.out.println(
				"Position gravite leq zone1: "
						+ position_gravite_zone1_axe_z_zone1());
		System.out.println(
				"Moment quadratique zone1: "
						+ moment_quadratique_zone1_zone1());
		if (composant instanceof ComposantSBGA) {
			System.out.println(
					"Position gravite CuivreC19400 zone2: "
							+ position_gravite_cuivreC19400_axe_z_zone2());
			System.out.println(
					"Moment quadratique CuivreC19400 zone2: "
							+ moment_quadratique_cuivreC19400_zone2());
		} else {
			System.out.println(
					"Position gravite resine zone2: "
							+ position_gravite_resine_axe_z_zone2());
			System.out.println(
					"Moment quadratique resine zone2: "
							+ moment_quadratique_resine_zone2());
		}
		System.out.println(
				"Position gravite pcb zone2: "
						+ position_gravite_pcb_axe_z_zone2());
		System.out.println(
				"Moment quadratique pcb zone2: "
						+ moment_quadratique_pcb_zone2());
		System.out.println(
				"Position gravite leq zone 2: "
						+ position_gravite_zone2_axe_z_zone2());
		System.out.println(
				"Moment quadratique zone2: "
						+ moment_quadratique_zone2_zone2());

		System.out.println("T: " + getTemperature() + " °C");
		System.out.println("E1Traction: " + calcul_e1_traction());
		System.out.println("E1Flexion: " + calcul_e1_flexion());
		System.out.println("CTE1: " + calcul_cte1());
		System.out.println("E2Traction: " + calcul_e2_traction());
		System.out.println("E2Flexion: " + calcul_e2_flexion());
		System.out.println("CTE2: " + calcul_cte2());
		System.out.println(
				"Exy traction: "
						+ composant.pcb.calcul_traction_ciu(getTemperature()));
		System.out.println(
				"Efxy flexion : "
						+ composant.pcb.calcul_flexion_ciu(getTemperature()));
		System.out.println(
				"CTExy: " + composant.pcb
						.calcul_coef_dil_xy_ciu(getTemperature()));
		System.out.println(
				"G3z: " + composant.pcb
						.calcul_cisaillement_z_ciu(getTemperature()));

		switch (composant.getLiaisonPuce()) {

		case "Underfill":
			Underfill und = new Underfill();
			System.out.println(
					"Eund: " + und.module_elasticite(getTemperature()));
			System.out.println(
					"Gzund: " + und.module_elasticite(getTemperature())
							/ (2 * (1 + Underfill.coefficientPoisson)));
			System.out.println("CTEund: " + und.coef_dil_xy(getTemperature()));
			break;

		case "Die Attach":
			DieAttach die = new DieAttach();
			System.out.println(
					"Edie: " + die.module_elasticite(getTemperature()));
			System.out.println(
					"Gzdie: " + die.module_elasticite(getTemperature())
							/ (2 * (1 + DieAttach.coefficientPoisson)));
			System.out.println("CTEdie: " + die.coef_dil_xy(getTemperature()));
			break;

		default:
			System.out.println("Liaison Puce inconnue E=0 ");
			System.out.println("Liaison Puce inconnue Gz=0");
			System.out.println("Liaison Puce inconnue CTE=0");
		}

		System.out.println("E4Flexion: " + calcul_e4_flexion());
		System.out.println("CTE4: " + calcul_cte4());
		System.out.println("G4z: " + calcul_g4_z());

		System.out.println("CTE 2+1 x: " + calcul_cte2Plus1_x());
		System.out.println("CTE 2+1 y: " + calcul_cte2Plus1_y());
		System.out.println("CTE 2+1 xy: " + calcul_cte2Plus1_xy());

		System.out.println("ea zoneA: " + ea_zone_a());
		System.out.println("Gza zoneA: " + gza_zone_a());
		System.out.println("CTE Silicium zoneA: " + cte_silicium_zone_a());

		if (composant instanceof ComposantSBGA) {
			System.out.println(
					"CTE CuivreC19400 zoneA: " + cte_cuivreC19400_zone_a());
		} else {
			System.out.println("CTE Resine zoneA: " + cte_resine_zone_a());
		}

		System.out.println("CTE A: " + cte_a_zone_a());
		System.out.println("T1 zone1: " + t1_zone1());
		System.out.println("T2 zone1: " + t2_zone1());
		System.out.println("T3 zone1: " + t3_zone1());
		System.out.println("Alpha a zone2: " + alpha_a_zone2());
		System.out.println("T1 zone2: " + t1_zone2());
		System.out.println("T2 zone2: " + t2_zone2());
		System.out.println("T3 zone2: " + t3_zone2());
		System.out.println("\n\n");

	}

	public double getE1_traction() {
		return e1_traction;
	}

	public void setE1_traction(double e1_traction) {
		this.e1_traction = e1_traction;
	}

	public double getE2_traction() {
		return e2_traction;
	}

	public void setE2_traction(double e2_traction) {
		this.e2_traction = e2_traction;
	}

	public double getE1_flexion() {
		return e1_flexion;
	}

	public void setE1_flexion(double e1_flexion) {
		this.e1_flexion = e1_flexion;
	}

	public double getE2_flexion() {
		return e2_flexion;
	}

	public void setE2_flexion(double calcul) {
		this.e2_flexion = calcul;
	}

	public double getCte1() {
		return cte1;
	}

	public void setCte1(double cte1) {
		this.cte1 = cte1;
	}

	public double getCte2() {
		return cte2;
	}

	public void setCte2(double cte) {
		this.cte2 = cte;
	}

	public double getCte2Plus1() {
		return cte2Plus1;
	}

	public void setCte2Plus1(double cte) {
		this.cte2Plus1 = cte;
	}

	public double get_double_temperature() {
		return getTemperature();
	}

	public String toString() {
		return getTemperature() + ";" + getE1_traction() + ";" + getE1_flexion()
				+ ";" + getCte1() + ";" + getE2_traction() + ";"
				+ getE2_flexion() + ";" + getCte2();
	}

}

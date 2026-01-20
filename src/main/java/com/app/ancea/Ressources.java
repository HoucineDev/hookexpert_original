package com.app.ancea;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Ressources {

	// Classe contenant toutes nos constantes de calculs et nos constantes
	// graphiques(comme l'adresse des images,
	// la taille du Texte, etc..)

	// Constantes de calculs
	public static final double VERNI_EPARGNE = 0.05;
	public static final double NOMBRE_TROU_TRAVERSANT = 5;
	public static final double DIAMETRE_PERCAGE = 0.35;
	public static final double P_CUIVRE = 8.94;
	public static final double Y_CUIVRE = 384;
	public static final double E_CUIVRE = 110000;
	public static final double CTE_CUIVRE = 17E-06;
	public static final double COEF_POISSON = 0.33;
	public static final double G_CUIVRE = 110000 / 2.66;

	public static final double EV1 = 0.005;
	public static final double EC1 = 0.005;
	public static final double EC2 = 0.045;
	public static final double SMD_EC2 = 0.015;
	public static final double EV2 = 0.015;

	public static final double COEFFICIENT_FATIGUE_A = 1.1858;
	public static final double COEFFICIENT_FATIGUE_B = -0.481;
	public static final int RAISON = -1;
	public static final double PCB_v = 0.3;
	public static final double COMPOSANT_v = 0.25;

	public static final double SAC305_K1_TRACTION = 3.4;
	public static final double SAC305_K2_TRACTION = 52.47;
	public static final double SAC305_K3_TRACTION = 0.001939649;
	public static final double SAC305_K4_TRACTION = -0.42984391;
	public static final double SAC305_K5_TRACTION = 23.974;
	public static final double SAC305_K6_TRACTION = -0.1443;
	public static final double SAC305_K7_TRACTION = 3.7450E-3;
	public static final double SAC305_v_TRACTION = 0.4;
	public static final double SAC305_K8_TRACTION = 4.33;
	public static final double SAC305_K9_TRACTION = 69.08;
	public static final double SAC305_K10_TRACTION = 0.0008;
	public static final double SAC305_K11_TRACTION = -0.1876;
	public static final double SAC305_K12_TRACTION = 30.884;
	public static final double SAC305_K13_TRACTION = 18.131;
	public static final double SAC305_K14_TRACTION = 0.0062;

	public static final double SAC305_K3_CISAILLEMENT = 0.001119857; //0.001120;
	public static final double SAC305_K4_CISAILLEMENT = -0.2481705; //-0.2482;
	public static final double SAC305_K5_CISAILLEMENT = 15.4251908; //15.425;
	public static final double SAC305_K6_CISAILLEMENT = -0.14431189; //-0.1443;
	public static final double SAC305_K7_CISAILLEMENT = 0.00374498013; //3.7450E-3;
	public static final double SAC305_v_CISAILLEMENT = 0.4;
	public static final double SAC305_K10_CISAILLEMENT = 0.0003;
	public static final double SAC305_K11_CISAILLEMENT = -0.067;
	public static final double SAC305_K12_CISAILLEMENT = 11.03;

	public static final String VALEURCLE1 = "-55";
	public static final String VALEURCLE2 = "-45";
	public static final String VALEURCLE3 = "-40";
	public static final String VALEURCLE4 = "-20";
	public static final String VALEURCLE5 = "0";
	public static final String VALEURCLE6 = "20";
	public static final String VALEURCLE7 = "65";
	public static final String VALEURCLE8 = "85";
	public static final String VALEURCLE9 = "105";
	public static final String VALEURCLE10 = "125";

	public static final int[] tableau_temperatures = new int[] {
			-55, -45, -40, -20, 0, 20, 65, 85, 105, 125
	};

	public static final String[] TAB_VALEURS_CLE = new String[] {
			VALEURCLE1, VALEURCLE2, VALEURCLE3, VALEURCLE4, VALEURCLE5,
			VALEURCLE6, VALEURCLE7, VALEURCLE8, VALEURCLE9, VALEURCLE10
	};

	// Constantes graphiques

	public static final String COURBES_STYLE = "styles/application.css";

	public static final String WRONG_STYLE = "-fx-font: 8 italic;-fx-background-color:  red";
	public static final String RIGHT_STYLE = "-fx-font: 8 italic;-fx-background-color:  white";

	public static final String LOGO_ANCEA = "images/logo_AnCEA.png";
	public static final String LOGO_SAFRAN = "images/mini_logo_safran.png";
	public static final String LOGO_SAFRAN2 = "images/logo_Safran2.png";
	public static final String PREIMPREGNE = "preimpregne";
	public static final String STRATIFIE = "stratifie";

	public static final String COUCHE_ELECTRIQUE_STYLE = "-fx-background-color: #ffa500";
	public static final double COUCHE_ELECTRIQUE_WIDTH = 600;
	public static final double COUCHE_ELECTRIQUE_HEIGHT = 10;
	public static final double EPAISSEUR_CUIVRE_RECHARGE = 30;
	public static final double EPAISSEUR_CUIVRE_RECHARGE_VIALASER = 15;

	public static final String COUCHE_ISOLANTE_PREIMPREGNE_STYLE = "-fx-background-color: #90EE90";
	public static final String COUCHE_ISOLANTE_STRATIFIE_STYLE = "-fx-background-color:#006400";
	public static final double COUCHE_ISOLANTE_WIDTH = 600;
	public static final double COUCHE_ISOLANTE_HEIGHT = 10;
	public static final String NOMBRE_COUCHE_ISOLANTE_DEFAUT = "1";

	public static final String TEXTFIELD_STYLE = "-fx-font: 8 italic";
	public static final String TEXTFIELD_VALEURE_DEFAUT = "0";
	public static final double TEXTFIELD_WIDTH = 40;
	public static final double TEXTFIELD_HEIGHT = 10;

	public static final double TROU_WIDTH = 15;
	public static final double TROU_HEIGHT = 25;

	public static final String RESINE1755VPANASONIC = "Resine1755VPanasonic";
	public static final String RESINEPCL370ISOLA = "ResinePCL370Isola";
	public static final String RESINE700GHITACHI = "Resine700GHitachi";
	public static final String RESINEMCLE679FGHITACHI = "ResineMCLE679FGHitachi";
	public static final String RESINEIS400 = "ResineIS400";

	public static final String COMPOSANT_WLP = "WLP";
	public static final String IMAGE_COMPOSANT_WLP = "/application/images/WLP.png";
	// public static final String IMAGE_BRASURE_WLP =
	// "/application/BrasureWLP.png";

	public static final String LOGO_HE_FULL= "images/HookExpert_LOGO.png";

	public static final String COMPOSANT_BGA = "BGA";
	public static final String IMAGE_COMPOSANT_BGA = "/application/images/BGA.png";
	public static final String IMAGE_BRASURE_BGA = "/application/images/BrasureBGA.png";

	public static final String COMPOSANT_SBGA = "SBGA";
	public static final String COMPOSANT_CBGA = "CBGA";
	public static final String COMPOSANT_PCBGA = "PCBGA";

	public static final String COMPOSANT_CAPACITE = "Capacité";
	public static final String COMPOSANT_RESISTANCE = "Résistance";
	public static final String COMPOSANT_QFN = "QFN";
	public static final String COMPOSANT_TSOP = "TSOP";
	public static final String COMPOSANT_lCC = "LCC";

	public static final String COMPOSANT_CSP = "CSP";
	public static final String IMAGE_COMPOSANT_CSP = "/application/images/CSP.png";

	public static final String IMAGE_CYCLE = "/application/images/cycle.png";
	public static final String IMAGE_MATRICE_BGA = "/application/images/matriceBGA.png";
	public static final String IMAGE_MATRICE_WLP = "/application/images/matriceWLP.png";

	public static final String[] RESINES = new String[] {
			RESINE1755VPANASONIC, RESINEPCL370ISOLA, RESINE700GHITACHI,
			RESINEMCLE679FGHITACHI
	};

	public static final String[] COMPOSANTS = new String[] {
			COMPOSANT_BGA, COMPOSANT_WLP, COMPOSANT_CSP, COMPOSANT_SBGA,
			COMPOSANT_CBGA, COMPOSANT_PCBGA, COMPOSANT_lCC, COMPOSANT_CAPACITE,
			COMPOSANT_RESISTANCE, COMPOSANT_QFN, COMPOSANT_TSOP
	};
	
	public static final String[] NOUVEAU_COMPOSANTS = new String[] {
			COMPOSANT_BGA, COMPOSANT_WLP, COMPOSANT_CSP, COMPOSANT_SBGA,
			COMPOSANT_CBGA, COMPOSANT_PCBGA, COMPOSANT_lCC
	};

	public static final String[] liaisonPuceBGA = new String[] {
			"Underfill", "Die Attach"
	};
	public static final String[] EPAISSEUR_CUIVRE = new String[] {
			"0", "5", "9", "12", "17.5", "35", "70", "105"
	};

	public static final String[] TISSUS = new String[] {
			"1037", "106", "1078", "1080", "2113", "2116", "1506", "7628"
	};
	public static final String[] TISSUS_COUCHE_VIA = new String[] {
			"1037", "106", "1078", "1080"
	};
	public static final String TISSUS_COUCHE_DEFAUT = "2116";
	public static final String TISSUS_COUCHE_VIA_DEFAUT = "106";

	public static final int NOMBRE_CHIFFRE_APRES_VIRGULE = 3;

	public static final double[] POURCENTAGE_DEFAILLANCE = {
			0.95, 0.9, 0.85, 0.8, 0.75, 0.7, 0.65, 0.6, 0.55, 0.5, 0.45, 0.4,
			0.35, 0.3, 0.25, 0.2, 0.15, 0.1, 0.05, 0.04, 0.03, 0.02, 0.01,
			0.005, 0.002, 0.001, 0.0005, 0.0002, 0.0001, 0.00005, 0.00002,
			0.00001, 0.000005, 0.000002, 0.000001
	};


	// Fallback: if property missing, use current dir/sibling folder name
	private static final String BASE_PROP = System.getProperty("app.baseDir");
	private static final Path BASE_PATH = (BASE_PROP != null && !BASE_PROP.isBlank())
			? Paths.get(BASE_PROP)
			: Paths.get("").toAbsolutePath().resolve("Stockage de données de calculs");

	public static final Path PATH_PCB = subPath("PCB");
	public static final Path PATH_COMPOSANTS = subPath("Composants");
	public static final Path PATH_PROFILS = subPath("Profils");
	public static final Path PATH_BRASURES = subPath("Brasures");
	public static final Path PATH_BRASURES_TEST = subPath("Brasures", "Brasures tests");
	public static final Path PATH_CARTE = subPath("Cartes");
	public static final Path PATH_COMPAGNE = subPath("Campagne Tests");
	public static final Path PATH_EXCEL = subPath("Excel");
	public static final Path PATH_LOGS = subPath("Logs");

	public static Path subPath(String... subDirs) {
		return BASE_PATH.resolve(Paths.get("", subDirs));
	}

	public static void ensureDirs() {
		try {
			for (Path p : new Path[]{
					PATH_PCB, PATH_COMPOSANTS, PATH_PROFILS, PATH_BRASURES,
					PATH_BRASURES_TEST, PATH_CARTE, PATH_COMPAGNE, PATH_EXCEL, PATH_LOGS
			}) {
				Files.createDirectories(p);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to create data directories under " + BASE_PATH, e);
		}
	}

	// Centralized utility to join a base path with a filename/subpath, handling separators automatically
	public static String joinPath(Path basePath, String fileName) {
		Objects.requireNonNull(basePath, "Base path cannot be null");
		Objects.requireNonNull(fileName, "File name cannot be null");
		return basePath.resolve(fileName).normalize().toAbsolutePath().toString();
	}

	// If you must keep string constants, append File.separator for safe concat (but prefer Path + joinPath above)
	public static final String pathPCB = PATH_PCB.toAbsolutePath().toString() + File.separator;
	public static final String pathComposants = PATH_COMPOSANTS.toAbsolutePath().toString() + File.separator;
	public static final String pathProfils = PATH_PROFILS.toAbsolutePath().toString() + File.separator;
	public static final String pathBrasures = PATH_BRASURES.toAbsolutePath().toString() + File.separator;
	public static final String pathBrasuresTests = PATH_BRASURES_TEST.toAbsolutePath().toString() + File.separator;
	public static final String pathCartes = PATH_CARTE.toAbsolutePath().toString() + File.separator;
	public static final String pathCampagne = PATH_COMPAGNE.toAbsolutePath().toString() + File.separator;
	public static final String pathExcel = PATH_EXCEL.toAbsolutePath().toString() + File.separator;
	public static final String pathLogs = PATH_LOGS.toAbsolutePath().toString() + File.separator;

	}


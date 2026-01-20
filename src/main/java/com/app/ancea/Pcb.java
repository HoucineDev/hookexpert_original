package com.app.ancea;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Pcb {

	// classe représentant le PCB qui est graphiquement affiché et dont on
	// calcule
	// les propriétés dans OngletPcb

	public ModelPhysiqueStatique resine;

	ArrayList<CouchePcb> al;// Tableau représentant le Circuit imprimé lui meme
	int nombre_couche;
	int nombre_vialaser;
	int trou_enterre;

	public Pcb() {
		al = new ArrayList<CouchePcb>();
	}

	public Pcb(int i1, int i2, int i3) {// ajouter un parametre pour le type de
										// résine utilisé

		nombre_couche = i1;
		nombre_vialaser = i2;
		trou_enterre = i3;

		al = new ArrayList<CouchePcb>();

		for (int i = 0; i < i1 - 1; i++) {
			al.add(new CoucheElectriquePcb());
			al.add(new CoucheIsolantePcb());
		}
		// Ajout de la dernière couche électrique
		CouchePcb nce = new CoucheElectriquePcb();

		al.add(nce);

		init_cuivre_recharge();
		symetrie_circuit();
		init_tissus();
		symetrie_stratifie();

		if (this.nombre_vialaser > 0 && ((this.nombre_vialaser * 2) < this.nombre_couche)) {
			// Si le nombre de via laser est cohérent, on en met
			put_vialaser(this.nombre_vialaser);
		}

		if (this.trou_enterre_valide()) {
			put_trou_enterre();
		}

	}

	public void clear() {
		this.al.clear();
	}

	public ArrayList<CouchePcb> get_liste_couches_pcb() {
		return this.al;
	}

	public void save_Circuit() {// pour réouvrir le circuit , il ne faut pas
								// oublier de nettoyer
		// la vbox courrante avant de lui mettre le pcb que l'on va ouvrir

		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"PCB (*.pcb)",
				"*.pcb");
		fileChooser.getExtensionFilters().add(extFilter);
		// fileChooser.setInitialDirectory("CiuCats");
		// Show save file dialog
		File file = fileChooser.showSaveDialog(new Stage());

		if (file != null) {

			try {

				PrintWriter pw = new PrintWriter(
						new BufferedWriter(new FileWriter(file)));
				for (int i = 0; i < al.size(); i++) {

					pw.println((al.get(i).toString()));
				}

				pw.close();
			} catch (IOException ex) {
				Logger.getLogger(OngletPcb.class.getName())
						.log(Level.SEVERE, null, ex);
			}
		}

	}

//	public void load_Circuit(String fileName) {
//		// pour réouvrir le circuit , il
//												// ne faut pas oublier de
//												// nettoyer
//		// la vbox courrante avant de lui ajouter le circuit que l'on va ouvrir
//		// pour mettre cette fonction dans une classe ciu , il faudrait mettre
//		// le
//		// boutton dans le constructeur de Circuit
//		// et faire appelle à la fonction Buton8load8circuit dans le setonaction
//
////		FileChooser fileChooser = new FileChooser();
////		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
////		fileChooser.getExtensionFilters().add(extFilter);
////
////		// fileChooser.setInitialDirectory("CiuCats");
////		// Show save file dialog
////		File file = fileChooser.showOpenDialog(new Stage());
//		try {
//			FileReader fr = new FileReader(fileName);
//			BufferedReader br = new BufferedReader(fr);
//
//			try { // commencer par lire la premiere ligne qui est le type de
//					// résine utilisé
//				ArrayList<String> als = new ArrayList<String>();
//				String line = br.readLine();
//
//				// int i=0;
//				while (line != null) {
//
//					als.add(line);
//					line = br.readLine();
//
//				}
//				int nb_vialaser = 0;
//
//				for (int i = 0; i < als.size(); i++) {
//
//					if (i == 0) {
//						if (als.get(i)
//								.equals(Ressources.RESINE1755VPANASONIC)) {
//							this.resine = new Resine1755VPanasonic();
//						} else if (als.get(i)
//								.equals(Ressources.RESINEPCL370ISOLA)) {
//							this.resine = new ResinePCL370Isola();
//						} else if (als.get(i)
//								.equals(Ressources.RESINE700GHITACHI)) {
//							this.resine = new Resine700GHitachi();
//						} else if (als.get(i)
//								.equals(Ressources.RESINEMCLE679FGHITACHI)) {
//							this.resine = new ResineMCLE679FGHitachi();
//						}
//
//						else {
//							this.resine = new Resine1755VPanasonic();
//						}
//						continue;
//					}
//
//					String[] tabs = als.get(i).split(";");
//
//					// chargement du pcb en fonction de la couche (cuivre ou
//					// isolant)
//					if (i % 2 != 0) {
//
//						CoucheElectriquePcb ce = new CoucheElectriquePcb();
//						ce.set_trou_traversant(tabs[0]);
//						ce.set_epaisseur_cuivre(tabs[1]);
//						ce.set_epaisseur_cuivre_recharge(tabs[2]);
//						ce.set_taux_remplissage(tabs[3]);
//						ce.set_epaisseur_cuivre_equivalent(tabs[4]);
//						al.add(ce);
//					} else {
//						CoucheIsolantePcb ci = new CoucheIsolantePcb();
//						ci.set_trou_traversant(tabs[0]);
//						ci.set_trou_enterre_visibilite(tabs[1]);
//						ci.set_vialaser1(tabs[2]);
//						ci.set_vialaser2(tabs[3]);
//						ci.set_choix_isolant(tabs[4]);
//						ci.set_choix_tissus(tabs[5]);
//						ci.set_nombre(tabs[6]);
//						ci.set_epaisseur_apres_pressage(tabs[7]);
//						ci.set_taux_resine(tabs[8]);
//						if (ci.get_vialaser()) {
//							nb_vialaser++;
//						}
//						al.add(ci);
//
//					}
//
//				}
//				this.nombre_vialaser = (nb_vialaser / 2);
//				this.nombre_couche = (als.size() / 2) + 1;
//
//				br.close();
//				fr.close();
//			} catch (IOException exception) {
//				System.out.println(
//						"Erreur lors de la lecture : "
//								+ exception.getMessage());
//			}
//		} catch (FileNotFoundException exception) {
//			System.out.println(
//					"Le fichier n'a pas été trouvé.\nChemin: " + fileName);
//		}
//
//		symetrie_circuit();
//		symetrie_stratifie();
//
//	}

public void load_Circuit(String fileName) {
	// Method 1: Using Thread.currentThread().getStackTrace() (Most common)
	StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
	// stackTrace[0] = getStackTrace()
	// stackTrace[1] = load_Circuit() (current method)
	// stackTrace[2] = calling method
	if (stackTrace.length > 2) {
		String callerClassName = stackTrace[2].getClassName();
		String callerMethodName = stackTrace[2].getMethodName();
		int lineNumber = stackTrace[2].getLineNumber();

		System.out.println("load_Circuit called by: " + callerClassName +
				"." + callerMethodName + "() at line " + lineNumber);
	}

	// Method 2: Using Exception stack trace (Alternative approach)
	String callerClass = new Exception().getStackTrace()[1].getClassName();
	System.out.println("Called from class: " + callerClass);

	// Method 3: More detailed caller information
	printCallerInfo();

	// Your existing code starts here...
	try {
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);

		try {
			ArrayList<String> als = new ArrayList<String>();
			String line = br.readLine();

			while (line != null) {
				als.add(line);
				line = br.readLine();
			}

			int nb_vialaser = 0;

			for (int i = 0; i < als.size(); i++) {
				if (i == 0) {
					if (als.get(i).equals(Ressources.RESINE1755VPANASONIC)) {
						this.resine = new Resine1755VPanasonic();
					} else if (als.get(i).equals(Ressources.RESINEPCL370ISOLA)) {
						this.resine = new ResinePCL370Isola();
					} else if (als.get(i).equals(Ressources.RESINE700GHITACHI)) {
						this.resine = new Resine700GHitachi();
					} else if (als.get(i).equals(Ressources.RESINEMCLE679FGHITACHI)) {
						this.resine = new ResineMCLE679FGHitachi();
					} else {
						this.resine = new Resine1755VPanasonic();
					}
					continue;
				}

				String[] tabs = als.get(i).split(";");

				if (i % 2 != 0) {
					CoucheElectriquePcb ce = new CoucheElectriquePcb();
					ce.set_trou_traversant(tabs[0]);
					ce.set_epaisseur_cuivre(tabs[1]);
					ce.set_epaisseur_cuivre_recharge(tabs[2]);
					ce.set_taux_remplissage(tabs[3]);
					ce.set_epaisseur_cuivre_equivalent(tabs[4]);
					al.add(ce);
				} else {
					CoucheIsolantePcb ci = new CoucheIsolantePcb();
					ci.set_trou_traversant(tabs[0]);
					ci.set_trou_enterre_visibilite(tabs[1]);
					ci.set_vialaser1(tabs[2]);
					ci.set_vialaser2(tabs[3]);
					ci.set_choix_isolant(tabs[4]);
					ci.set_choix_tissus(tabs[5]);
					ci.set_nombre(tabs[6]);
					ci.set_epaisseur_apres_pressage(tabs[7]);
					ci.set_taux_resine(tabs[8]);
					if (ci.get_vialaser()) {
						nb_vialaser++;
					}
					al.add(ci);
				}
			}

			this.nombre_vialaser = (nb_vialaser / 2);
			this.nombre_couche = (als.size() / 2) + 1;

			br.close();
			fr.close();

		} catch (IOException exception) {
			System.out.println("Erreur lors de la lecture : " + exception.getMessage());
		}
	} catch (FileNotFoundException exception) {
		System.out.println("Le fichier n'a pas été trouvé.\nChemin: " + fileName);
	}

	symetrie_circuit();
	symetrie_stratifie();
}

	// Helper method for detailed caller information
	private void printCallerInfo() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		if (stackTrace.length > 2) {
			StackTraceElement caller = stackTrace[2];

			String fullClassName = caller.getClassName();
			String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
			String methodName = caller.getMethodName();
			String fileName = caller.getFileName();
			int lineNumber = caller.getLineNumber();

			System.out.println("=== Caller Information ===");
			System.out.println("Full class name: " + fullClassName);
			System.out.println("Simple class name: " + simpleClassName);
			System.out.println("Method name: " + methodName);
			System.out.println("File name: " + fileName);
			System.out.println("Line number: " + lineNumber);
			System.out.println("========================");
		}
	}

	// Alternative: Create a utility method that can be reused
	public static void printMethodCaller(String currentMethodName) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		if (stackTrace.length > 3) {
			StackTraceElement caller = stackTrace[3]; // Skip getStackTrace, printMethodCaller, and current method

			String callerClass = caller.getClassName();
			String callerMethod = caller.getMethodName();
			int lineNumber = caller.getLineNumber();

			System.out.println(currentMethodName + " called by: " +
					callerClass.substring(callerClass.lastIndexOf('.') + 1) +
					"." + callerMethod + "() at line " + lineNumber);
		}
	}

	public void symetrie_stratifie() { // essayer de faire entrer cette fct dans
										// la fct sysmetrie_circuit()
		for (int j = 0; j < (al.size() / 2); j++) {

			if (j % 2 == 0) {
				if (((CoucheIsolantePcb) al.get(j + 1)).isStratifie()) {
					((CoucheElectriquePcb) al.get(j)).epaisseur_cuivre
							.valueProperty().bindBidirectional(
									(((CoucheElectriquePcb) al
											.get((j + 2))).epaisseur_cuivre
													.valueProperty()));
				}
			}

		}

	}

	public void symetrie_circuit() {// Symétrie des couches Electriques et
									// Isolantes (binding)
		for (int j = 0; j < (al.size() / 2); j++) {

			if (j % 2 == 0) { // si c'est une couche paire donc c'est une couche
								// électrique

				((CoucheElectriquePcb) al.get(j)).epaisseur_cuivre
						.valueProperty().bindBidirectional(
								(((CoucheElectriquePcb) al.get(
										(al.size() - (j + 1)))).epaisseur_cuivre
												.valueProperty()));

				// On veut désormais rentrer le taux de remplissage couches par
				// couches
				// ((CoucheElectriquePcb) al.get(j)).taux_remplissage
				// .textProperty().bindBidirectional(
				// (((CoucheElectriquePcb) al.get(
				// (al.size() - (j + 1)))).taux_remplissage
				// .textProperty()));

				((CoucheElectriquePcb) al.get(j)).epaisseur_cuivre_recharge
						.textProperty().bindBidirectional(
								(((CoucheElectriquePcb) al.get(
										(al.size() - (j
												+ 1)))).epaisseur_cuivre_recharge
														.textProperty()));

				((CoucheElectriquePcb) al.get(j)).epaisseur_cuivre_recharge
						.editableProperty().bindBidirectional(
								(((CoucheElectriquePcb) al.get(
										(al.size() - (j
												+ 1)))).epaisseur_cuivre_recharge
														.editableProperty()));

				// ce paramètre dépendant du taux de remplisage ne peut plus
				// être symétrique
				// ((CoucheElectriquePcb) al.get(j)).epaisseur_cuivre_equivalent
				// .textProperty().bindBidirectional(
				// (((CoucheElectriquePcb) al.get(
				// (al.size() - (j
				// + 1)))).epaisseur_cuivre_equivalent
				// .textProperty()));
			}

			else { // si c'est une couche impaire c'est une couche isolante

				((CoucheIsolantePcb) al.get(j)).choix_isolant.valueProperty()// get
																				// choix
																				// isolant
																				// aulieu
																				// de
																				// choix
																				// isolant
						.bindBidirectional(
								(((CoucheIsolantePcb) al.get(
										(al.size() - (j + 1)))).choix_isolant
												.valueProperty()));

				((CoucheIsolantePcb) al.get(j)).choix_tissus.valueProperty()
						.bindBidirectional(
								(((CoucheIsolantePcb) al.get(
										(al.size() - (j + 1)))).choix_tissus
												.valueProperty()));

				((CoucheIsolantePcb) al.get(j)).nombre.textProperty()
						.bindBidirectional(
								(((CoucheIsolantePcb) al
										.get((al.size() - (j + 1)))).nombre
												.textProperty()));

				((CoucheIsolantePcb) al.get(j)).vialaser1.visibleProperty()
						.bindBidirectional(
								(((CoucheIsolantePcb) al
										.get((al.size() - (j + 1)))).vialaser1
												.visibleProperty()));

				((CoucheIsolantePcb) al.get(j)).vialaser2.visibleProperty()
						.bindBidirectional(
								(((CoucheIsolantePcb) al
										.get((al.size() - (j + 1)))).vialaser2
												.visibleProperty()));

				((CoucheIsolantePcb) al.get(j)).epaisseur_apres_pressage
						.textProperty().bindBidirectional(
								(((CoucheIsolantePcb) al.get(
										(al.size() - (j
												+ 1)))).epaisseur_apres_pressage
														.textProperty()));

				((CoucheIsolantePcb) al.get(j)).epaisseur_apres_pressage
						.styleProperty().bindBidirectional(
								(((CoucheIsolantePcb) al.get(
										(al.size() - (j
												+ 1)))).epaisseur_apres_pressage
														.styleProperty()));

				((CoucheIsolantePcb) al.get(j)).taux_resine.textProperty()
						.bindBidirectional(
								(((CoucheIsolantePcb) al
										.get((al.size() - (j + 1)))).taux_resine
												.textProperty()));

				((CoucheIsolantePcb) al.get(j)).trou_enterre.visibleProperty()
						.bindBidirectional(
								(((CoucheIsolantePcb) al.get(
										(al.size() - (j + 1)))).trou_enterre
												.visibleProperty()));

				/*
				 * ((CoucheIsolante)al.get((al.size()-(j+1)))).
				 * set_epaisseur_stratifié((( CoucheIsolante)
				 * al.get(j)).get_epaisseur_stratifié());
				 * System.out.println("epaisseur strat " + ((CoucheIsolante)
				 * al.get(j)).get_epaisseur_stratifié());
				 */
			}

		}
	}

	public void init_cuivre_recharge() {
		// Car la première et la dernière couche de cuivre ont 30 µm de cuivre de recharge
		// Remplacer 30 par une valeur de la classe statique
		((CoucheElectriquePcb) al.getFirst()).set_cuivre_recharge(Ressources.EPAISSEUR_CUIVRE_RECHARGE);
		((CoucheElectriquePcb) al.getLast()).set_cuivre_recharge(Ressources.EPAISSEUR_CUIVRE_RECHARGE);

		// les couches supérieurs (en cuivre) des couches isolantes avec
		// vialaser on
		// 15um de cuivre de recharge
		// sauf si ce sont les premiere et derniere couches qui elles en ont
		// 30(um)

	}

	public boolean trou_enterre_valide() {
		return (this.trou_enterre > 1
				&& !(this.trou_enterre > this.nombre_vialaser + 1)
				&& ((this.trou_enterre * 2 < (this.al.size() / 2))));
	}

	public boolean vialaser_valide() {
		return (this.nombre_vialaser > 0)
				&& ((this.nombre_vialaser * 2) < this.nombre_couche);
	}

	public void init_tissus() {// Placer automatiquement les isolants en
								// préimprégné / stratifié
		boolean b = (!((this.nombre_vialaser > 0)
				&& ((this.nombre_vialaser * 2) < this.nombre_couche)));

		int ref_position_tissus = 0;

		if (this.trou_enterre_valide() && vialaser_valide()) {
			ref_position_tissus = Math
					.max(this.nombre_vialaser, this.trou_enterre);
		} else if (vialaser_valide()) {
			ref_position_tissus = this.nombre_vialaser;
		}

		for (int j = (ref_position_tissus * 2); j < (al.size() / 2) + 1; j++) {

			if (j % 2 != 0) {
				if (b) {
					((CoucheIsolantePcb) this.al.get(j))
							.set_choix_isolant(Ressources.PREIMPREGNE);
				}

				else {
					((CoucheIsolantePcb) this.al.get(j))
							.set_choix_isolant(Ressources.STRATIFIE);
				}
				b = (b) ? false : true;
			}

		}

	}

	public void put_vialaser(int i) {
		boolean decalage = true;
		for (int j = 0; j < i * 2; j = j + 2) {

			((CoucheIsolantePcb) al.get(((al.size() - 2) - (j))))
					.setVialaser_position(decalage);

			((CoucheIsolantePcb) al.get(j + 1)).setVialaser_position(decalage);
			if (j != 0) {
				((CoucheElectriquePcb) al.get(j)).set_cuivre_recharge(
						Ressources.EPAISSEUR_CUIVRE_RECHARGE_VIALASER);

			}

			decalage = (decalage) ? false : true;

		}

	}

	public void put_trou_enterre() {

		for (int j = 0; j < (al.size() / 2) + 1; j++) {

			if (j % 2 != 0) {// si c'est une couche isolante

				if ((j >= (this.trou_enterre * 2) - 1)
						&& this.trou_enterre > 0) {
					((CoucheIsolantePcb) this.al.get(j))
							.set_trou_enterre_visibilite(true);

				}

			}

		}
		((CoucheElectriquePcb) this.al.get(((2 * trou_enterre) - 2))).set_cuivre_recharge(Ressources.EPAISSEUR_CUIVRE_RECHARGE_VIALASER);

	}

}

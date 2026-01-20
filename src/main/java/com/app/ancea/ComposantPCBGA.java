package com.app.ancea;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;

public class ComposantPCBGA extends ComposantBilles
		implements InterfaceComposant {

	private StringProperty composantZ;
	private TableView<Resultat_temperature_PCBGA> tv;

	protected OngletPcb pcb;
	protected Propriete pcb_x;
	protected Propriete pcb_y;
	protected Propriete pcb_z;

	protected CoucheElectriquePcb ce;
	protected CoucheIsolantePcb ci;

	public static final ImageView image = new ImageView(
			ComposantPCBGA.class.getResource("images/PCBGA.png")
					.toExternalForm());

	public static final double vernis = 1;
	public static final double beta = 8;

	public Propriete composant_x, composant_y, composant_z, p_pas, p_resine,
			p_puce_x, p_puce_y, p_puce_z;
	public Propriete poids, masse_volumique, p_nombre_billes_x,
			p_nombre_billes_y;

	public ObservableList<Propriete> proprietes;
	public ObservableList<Resultat_temperature_PCBGA> paliers_temperatures;

	// Constructeur pour la création d'un PCBGA en connaissant tous les
	// paramètres.( Puce_x,Puce_y,Puce_z --> = 0 !)

	public ComposantPCBGA(
			OngletPcb pcb,
			String nom,
			double Puce_x,
			double Puce_y,
			double Puce_z,
			double pas,
			boolean billes_angles,
			int pcb_x,
			int pcb_y,
			int nombre_billes_x,
			int nombre_billes_y) {

		super(nom, Puce_x, Puce_y, Puce_z, pas, billes_angles);

		this.pcb = pcb;

		this.pcb_x = new Propriete("PCB x", pcb_x);
		this.pcb_y = new Propriete("PCB y", pcb_y);
		this.pcb_z = new Propriete("PCB z", pcb.get_totale_nominal_reel());

		this.nombre_billes_x = new SimpleIntegerProperty(nombre_billes_x);
		this.nombre_billes_y = new SimpleIntegerProperty(nombre_billes_y);

		composant_x = new Propriete("Composant x");
		composant_y = new Propriete("Composant y");
		composant_z = new Propriete("Composant z");

		composantZ = new SimpleStringProperty();

		poids = new Propriete("Poids");
		masse_volumique = new Propriete("Masse volumique");

		p_nombre_billes_x = new Propriete(
				"Nombre de billes x",
				nombre_billes_x);
		p_nombre_billes_y = new Propriete(
				"Nombre de billes y",
				nombre_billes_y);

		p_pas = new Propriete("Pas", pas);

		p_puce_x = new Propriete("Puce x", Puce_x);
		p_puce_y = new Propriete("Puce y", Puce_y);
		p_puce_z = new Propriete("Puce z", Puce_z);

		Propriete blank = new Propriete("");
		blank.set_resultat(" ");

		proprietes = FXCollections.observableArrayList(
				composant_x,
				composant_y,
				composant_z,
				blank,
				this.pcb_x,
				this.pcb_y,
				this.pcb_z,
				blank,
				pcb.p_epaisseur_totale_nominale,
				blank,
				p_pas,
				blank,
				p_nombre_billes_x,
				p_nombre_billes_y,
				blank,
				p_puce_x,
				p_puce_y,
				p_puce_z);

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature_PCBGA(this, d));
		}

		calcul_poids();

		calcul_pcb_z();

		calcul_composant_z();

		calcul_composant_y();
		calcul_composant_x();

	}

	/**
	 * constructeur d'un BGA en ne connaissant pas à l'avance les paramètres. Il
	 * faudra par la suite les modifier via les setteurs.
	 * 
	 * @param pcb PCB du BGA.
	 */
	public ComposantPCBGA(OngletPcb pcb) {
		super();
		this.pcb = pcb;

		this.pcb_x = new Propriete("PCB x", 0);
		this.pcb_y = new Propriete("PCB y", 0);
		this.pcb_z = new Propriete("PCB z", 0);

		this.nombre_billes_x = new SimpleIntegerProperty(0);
		this.nombre_billes_y = new SimpleIntegerProperty(0);

		composant_x = new Propriete("Composant x");
		composant_y = new Propriete("Composant y");
		composant_z = new Propriete("Composant z");

		composantZ = new SimpleStringProperty();

		poids = new Propriete("Poids");
		masse_volumique = new Propriete("Masse volumique");

		p_resine = new Propriete("Resine");

		p_nombre_billes_x = new Propriete("Nombre de billes x");
		p_nombre_billes_y = new Propriete("Nombre de billes y");

		p_pas = new Propriete("Pas");

		p_puce_x = new Propriete("Puce x");
		p_puce_y = new Propriete("Puce y");
		p_puce_z = new Propriete("Puce z");

		Propriete blank = new Propriete("");
		blank.set_resultat(" ");

		proprietes = FXCollections.observableArrayList(
				composant_x,
				composant_y,
				composant_z,
				blank,
				this.pcb_x,
				this.pcb_y,
				this.pcb_z,
				blank,
				pcb.p_epaisseur_totale_nominale,
				blank,
				p_pas,
				blank,
				p_nombre_billes_x,
				p_nombre_billes_y,
				blank,
				p_puce_x,
				p_puce_y,
				p_puce_z);

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature_PCBGA(this, d));
		}

		calcul_poids();

		calcul_pcb_z();

		calcul_composant_z();

		calcul_composant_y();
		calcul_composant_x();

		// pcb.tous_calculs();
	}

	/**
	 * Constructeur servant à charger un BGA en fonction d'un path
	 * 
	 * @param pcb  OngletPcb à charger
	 * @param path Chemin du composant BGA à charger
	 */
	public ComposantPCBGA(OngletPcb pcb, String path) {

		this.pcb = pcb;

		this.pcb_x = new Propriete("PCB x", 0);
		this.pcb_y = new Propriete("PCB y", 0);
		this.pcb_z = new Propriete("PCB z", 0);

		this.nombre_billes_x = new SimpleIntegerProperty(0);
		this.nombre_billes_y = new SimpleIntegerProperty(0);

		this.composant_x = new Propriete("Composant x");
		this.composant_y = new Propriete("Composant y");
		this.composant_z = new Propriete("Composant z");

		this.poids = new Propriete("Poids");
		this.masse_volumique = new Propriete("Masse volumique");

		this.p_nombre_billes_x = new Propriete("Nombre de billes x");
		this.p_nombre_billes_y = new Propriete("Nombre de billes y");

		this.p_pas = new Propriete("Pas");
		this.p_resine = new Propriete("Resine");

		this.p_puce_x = new Propriete("Puce x");
		this.p_puce_y = new Propriete("Puce y");
		this.p_puce_z = new Propriete("Puce z");

		Propriete blank = new Propriete("");
		blank.set_resultat(" ");

		proprietes = FXCollections.observableArrayList(
				composant_x,
				composant_y,
				composant_z,
				blank,
				this.pcb_x,
				this.pcb_y,
				this.pcb_z,
				blank,
				pcb.p_epaisseur_totale_nominale,
				blank,
				p_pas,
				blank,
				p_nombre_billes_x,
				p_nombre_billes_y,
				blank,
				p_puce_x,
				p_puce_y,
				p_puce_z,
				blank);

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature_PCBGA(this, d));
		}

		// chargement d'un BGA
		try {
			FileReader fr = new FileReader(path);
			BufferedReader br = new BufferedReader(fr);

			ArrayList<String> als = new ArrayList<String>();
			ArrayList<String> als2 = new ArrayList<String>();
			String line = br.readLine();

			while ((line != null) && (!line.equals("Matrice Billes:"))) {
				als.add(line);
				line = br.readLine();

			}

			for (int i = 0; i < als.size() - 1; i++) {
				if (i == 0) {
					if (als.get(i).contains(Ressources.RESINE1755VPANASONIC)) {
						pcb.resine = new Resine1755VPanasonic();
					} else if (als.get(i)
							.contains(Ressources.RESINEPCL370ISOLA)) {
						pcb.resine = new ResinePCL370Isola();
					} else if (als.get(i)
							.contains(Ressources.RESINE700GHITACHI)) {
						pcb.resine = new Resine700GHitachi();
					} else if (als.get(i)
							.contains(Ressources.RESINEMCLE679FGHITACHI)) {
						pcb.resine = new ResineMCLE679FGHitachi();
					}

					else {
						pcb.resine = new Resine1755VPanasonic();
					}
					p_resine.set_resultat(pcb.resine.toString());
					continue;
				}
				String[] tabs = als.get(i).split(";");

				if (i % 2 != 0) {
					ce = new CoucheElectriquePcb();
					ce.set_trou_traversant(tabs[0]);
					ce.set_epaisseur_cuivre(tabs[1]);
					ce.set_epaisseur_cuivre_recharge(tabs[2]);
					ce.set_taux_remplissage(tabs[3]);
					ce.set_epaisseur_cuivre_equivalent(tabs[4]);
					pcb.get_couches_pcb().add(ce);

				} else {
					ci = new CoucheIsolantePcb();
					ci.set_trou_traversant(tabs[0]);
					ci.set_trou_enterre_visibilite(tabs[1]);
					ci.set_vialaser1(tabs[2]);
					ci.set_vialaser2(tabs[3]);
					ci.set_choix_isolant(tabs[4]);
					ci.set_choix_tissus(tabs[5]);
					ci.set_nombre(tabs[6]);
					ci.set_epaisseur_apres_pressage(tabs[7]);
					ci.set_taux_resine(tabs[8]);

					pcb.get_couches_pcb().add(ci);

				}
			}

			String[] tabs = als.get(als.size() - 1).split(";");
			this.setPas(Outils.StringWithCommaToDouble(tabs[0]));
			this.setNombre_billes_x(
					((int) Outils.StringWithCommaToDouble(tabs[1])));
			this.setNombre_billes_y(
					((int) Outils.StringWithCommaToDouble(tabs[2])));
			this.setPresence_billes_angles(Boolean.parseBoolean((tabs[3])));

			this.setPuce_x(0);
			this.setP_puce_x(0);

			this.setPuce_y(0);
			this.setP_puce_y(0);

			this.setPuce_z(0);
			this.setP_puce_z(0);

			// this.setResine_x((Outils.StringWithCommaToDouble(tabs[7])));
			// this.setResine_y((Outils.StringWithCommaToDouble(tabs[8])));
			// this.setResine_z((Outils.StringWithCommaToDouble(tabs[9])));
			this.setPcb_x((Outils.StringWithCommaToDouble(tabs[10])));
			this.setPcb_y((Outils.StringWithCommaToDouble(tabs[11])));
			// this.setLiaisonPuce(tabs[12]);
			// this.setEpaisseurLiaison(
			// (Outils.StringWithCommaToDouble(tabs[13])));

			while (line != null) {
				als2.add(line);
				line = br.readLine();

			}

			super.matriceBilles = new boolean[this.getNombre_billes_y()][this
					.getNombre_billes_x()];

			for (int i = 0; i < this.getNombre_billes_y(); i++) {
				String[] tabs2 = als2.get(i + 1).split(";");
				for (int j = 0; j < this.getNombre_billes_x(); j++) {
					super.matriceBilles[i][j] = Boolean.valueOf(tabs2[j]);
				}
			}

			this.pcb.tous_calculs();

			calcul_poids();

			calcul_pcb_z();
			calcul_composant_x();
			calcul_composant_y();

			calcul_composant_z();

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void setPas(double pas) {
		p_pas.set_resultat(String.valueOf(pas));
		this.pas.set(pas);
	}

	public void setP_puce_x(double puce_x) {
		p_puce_x.set_resultat(String.valueOf(puce_x));
		this.Puce_x.set(puce_x);
	}

	public void setP_puce_y(double puce_y) {
		p_puce_y.set_resultat(String.valueOf(puce_y));
		this.Puce_y.set(puce_y);
	}

	public void setP_puce_z(double puce_z) {
		p_puce_z.set_resultat(String.valueOf(puce_z));
		this.Puce_z.set(puce_z);
	}

	public double calcul_poids() {
		double calcul = 0;
		// 0.001 pour la conversion mm3 en cm3
		calcul = getPcb_x() * getPcb_y() * getPcb_z()
				* this.pcb.get_masse_volumique() * 0.001;

		setPoids(calcul);
		return calcul;

	}

	protected double getComposantZ() {
		double calcul = 0;

		calcul = this.getPuce_z() + this.getPcb_z();

		return calcul;
	}

	public void calcul_composant_z() {
		composant_z.set_resultat(String.valueOf(getComposantZ()));
	}

	public void calcul_pcb_z() {
		this.pcb_z.set_resultat(pcb.totale_nominal_reel.getText());
	}

	public void calcul_composant_x() {
		this.composant_x.set_resultat(String.valueOf(this.getPcb_x()));
	}

	public void calcul_composant_y() {

		this.composant_y.set_resultat(String.valueOf(this.getPcb_y()));

	}

	public double getPoids() {
		return Outils.StringWithCommaToDouble(poids.resultat.getValue());
	}

	public void setPoids(double poids) {
		this.poids.set_resultat((Outils.doubleToString(poids, 2)));
	}

	public int getNombre_billes_x() {
		return Integer.parseInt(p_nombre_billes_x.get_resultat().get());
	}

	public void setNombre_billes_x(int nombre_billes_x) {
		this.nombre_billes_x.set(nombre_billes_x);
		p_nombre_billes_x.set_resultat(String.valueOf(nombre_billes_x));
	}

	@Override
	public int getNombre_billes_y() {
		return Integer.parseInt(p_nombre_billes_y.get_resultat().get());
	}

	public void setNombre_billes_y(int nombre_billes_y) {
		this.nombre_billes_y.set(nombre_billes_y);
		p_nombre_billes_y.set_resultat(String.valueOf(nombre_billes_y));
	}

	public double getPcb_x() {
		return Double.valueOf(pcb_x.get_resultat().get());
	}

	public void setPcb_x(double pcb_x) {
		this.pcb_x.set_resultat(pcb_x);
	}

	public double getPcb_y() {
		return Double.valueOf(pcb_y.get_resultat().get());
	}

	public void setPcb_y(double pcb_y) {
		this.pcb_y.set_resultat(pcb_y);
	}

	public double getPcb_z() {
		return Double.valueOf(pcb.totale_nominal_reel.getText());
	}

	public void setPcb_z(double pcb_z) {
		this.pcb_z.set_resultat(pcb_z);
	}

	double position_gravite_silicium_axe_z_zone1() {
		double calcul = 0;
		calcul = (this.getPuce_z() / 2) + this.getPcb_z();

		return calcul;
	}

	public TableView<
			? extends Resultat_temperature_pcb> get_tableView_temperature_pcb_composant() {

		return pcb.tb_Resultat_temperature_pcb;
	}

	@SuppressWarnings("unchecked")
	public TableView<
			? extends Resultat_temperature> get_tableView_temperature() {

		for (Resultat_temperature_PCBGA resultat : paliers_temperatures) {
			resultat.calcul();
		}

		tv = new TableView<Resultat_temperature_PCBGA>();
		tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<
				Resultat_temperature_PCBGA,
				Number> temperature = new TableColumn<>("Temperatures (°C)");
		temperature.setCellValueFactory(
				c -> new SimpleDoubleProperty(c.getValue().getTemperature()));
		temperature.setMinWidth(80);

		TableColumn<
				Resultat_temperature_PCBGA,
				Number> e1_traction = new TableColumn<>("E1 x (MPa)");
		e1_traction.setCellValueFactory(
				c -> new SimpleIntegerProperty(
						(int) c.getValue().getE1_traction()));
		e1_traction.setMinWidth(100);

		TableColumn<
				Resultat_temperature_PCBGA,
				Number> e1_flexion = new TableColumn<>("E1f (MPa)");
		e1_flexion.setCellValueFactory(
				c -> new SimpleIntegerProperty(
						(int) c.getValue().getE1_flexion()));
		e1_flexion.setMinWidth(100);

		TableColumn<
				Resultat_temperature_PCBGA,
				String> cte1 = new TableColumn<>("CTE (m/m/°C)");
		cte1.setCellValueFactory(
				c -> new SimpleStringProperty(
						Outils.nombreChiffreScientifiqueApresVirgule(
								c.getValue().getCte1())));
		cte1.setMinWidth(20);

		tv.getColumns().addAll(temperature, e1_traction, e1_flexion, cte1);

		for (TableColumn<Resultat_temperature_PCBGA, ?> res : tv.getColumns()) {
			res.setStyle("-fx-alignment: CENTER;");
		}

		tv.setItems(paliers_temperatures);

		return tv;
	}

	@SuppressWarnings("unchecked")
	public TableView<Propriete> get_tableView_proprietes() {

		TableView<Propriete> tvProprietes = new TableView<Propriete>();
		tvProprietes.setEditable(true);

		TableColumn<
				Propriete,
				String> colonneVariable = new TableColumn<Propriete, String>(
						"Proprietes");

		colonneVariable.setMinWidth(120);
		colonneVariable
				.setCellValueFactory(c -> c.getValue().get_nom_variable());

		TableColumn<
				Propriete,
				String> colonneResultat = new TableColumn<Propriete, String>(
						"Resultats");
		colonneResultat.setEditable(true);
		colonneResultat.setMinWidth(90);
		colonneResultat.setCellValueFactory(c -> c.getValue().get_resultat());
		colonneResultat.setCellFactory(TextFieldTableCell.forTableColumn());
		colonneResultat
				.setOnEditCommit((CellEditEvent<Propriete, String> event) -> {
					TablePosition<
							Propriete,
							String> position = event.getTablePosition();

					String nouvelleValeur = event.getNewValue();

					int row = position.getRow();
					event.getTableView().getItems().get(row)
							.set_resultat(nouvelleValeur);

					this.pcb.tous_calculs();

					calcul_poids();
					calcul_pcb_z();
					calcul_composant_x();
					calcul_composant_y();
					calcul_composant_z();

					for (Resultat_temperature_PCBGA resultat : paliers_temperatures) {
						resultat.calcul();
					}

					for (Resultat_temperature_pcb resultat : pcb.data) {
						resultat.setCalculs();
					}

					this.tv.refresh();
				});

		tvProprietes.getColumns().addAll(colonneVariable, colonneResultat);
		tvProprietes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		tvProprietes.setItems(proprietes);

		return tvProprietes;
	}

	public String getComposant_x() {
		return composant_x.get_resultat().getValue();
	}

	public void setComposant_x(Propriete composant_x) {
		this.composant_x = composant_x;
	}

	public String getComposant_y() {
		return composant_y.get_resultat().getValue();
	}

	public void setComposant_y(Propriete composant_y) {
		this.composant_y = composant_y;
	}

	public String getComposant_z() {
		return composant_z.get_resultat().getValue();
	}

	public void setComposant_z(Propriete composant_z) {
		this.composant_z = composant_z;
	}

	public double getMasse_volumique() {
		return pcb.getMasseVolumique();
	}

	public void setMasse_volumique(Propriete masse_volumique) {
		this.masse_volumique = masse_volumique;
	}

	public ImageView getImage() {
		return image;
	}

	public void charger_pcbga(String path) {

		pcb.vb_pcb.getChildren().clear();
		try {
			FileReader fr = new FileReader(path);
			BufferedReader br = new BufferedReader(fr);

			ArrayList<String> als = new ArrayList<String>();
			String line = br.readLine();

			while (line != null) {
				als.add(line);
				line = br.readLine();

			}

			for (int i = 0; i < als.size() - 1; i++) {

				if (i == 0) {
					if (als.get(i).equals(Ressources.RESINE1755VPANASONIC)) {
						pcb.resine = new Resine1755VPanasonic();
					} else if (als.get(i)
							.equals(Ressources.RESINEPCL370ISOLA)) {
						pcb.resine = new ResinePCL370Isola();
					} else if (als.get(i)
							.equals(Ressources.RESINE700GHITACHI)) {
						pcb.resine = new Resine700GHitachi();
					} else if (als.get(i)
							.equals(Ressources.RESINEMCLE679FGHITACHI)) {
						pcb.resine = new ResineMCLE679FGHitachi();
					}

					else {
						pcb.resine = new Resine1755VPanasonic();
					}
					p_resine.set_resultat(pcb.resine.toString());
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
					pcb.get_couches_pcb().add(ce);

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

					pcb.get_couches_pcb().add(ci);
				}
			}

			pcb.vb_pcb.getChildren().addAll(pcb.get_couches_pcb());
			String[] tabs = als.get(als.size() - 1).split(";");

			this.setPas((int) Outils.StringWithCommaToDouble(tabs[0]));
			this.setNombre_billes_x(
					((int) Outils.StringWithCommaToDouble(tabs[1])));
			this.setNombre_billes_y(
					((int) Outils.StringWithCommaToDouble(tabs[2])));
			this.setPresence_billes_angles(Boolean.parseBoolean((tabs[3])));
			this.setPuce_x((Outils.StringWithCommaToDouble(tabs[4])));
			this.setPuce_y((Outils.StringWithCommaToDouble(tabs[5])));
			this.setPuce_z((Outils.StringWithCommaToDouble(tabs[6])));
			// this.setResine_x((Outils.StringWithCommaToDouble(tabs[7])));
			// this.setResine_y((Outils.StringWithCommaToDouble(tabs[8])));
			// this.setResine_z((Outils.StringWithCommaToDouble(tabs[9])));
			this.setPcb_x((Outils.StringWithCommaToDouble(tabs[10])));
			this.setPcb_y((Outils.StringWithCommaToDouble(tabs[11])));
			// this.setLiaisonPuce(tabs[12]);
			// this.setEpaisseurLiaison(
			// (Outils.StringWithCommaToDouble(tabs[13])));

			calcul_poids();
			calcul_pcb_z();
			calcul_composant_x();
			calcul_composant_y();
			calcul_composant_z();

			pcb.tous_calculs();

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	@Override
	public double getPas() {
		return Double.valueOf(p_pas.get_resultat().get());
	}

	public double getE1Traction(double temperature) {

		double e1_traction = 0;

		for (Resultat_temperature_PCBGA resultat : paliers_temperatures) {
			if (resultat.getTemperature() == temperature) {
				return resultat.calcul_e1_traction();
			}
		}

		Resultat_temperature_PCBGA resultat = new Resultat_temperature_PCBGA(
				this,
				temperature);
		e1_traction = resultat.calcul_e1_traction();

		paliers_temperatures.add(resultat);

		return e1_traction;
	}

	public double getCte(double temperature) {

		double cte = 0;

		for (Resultat_temperature_PCBGA resultat : paliers_temperatures) {
			if (resultat.getTemperature() == temperature)
				return resultat.calcul_cte1();
		}
		Resultat_temperature_PCBGA resultat = new Resultat_temperature_PCBGA(
				this,
				temperature);
		cte = resultat.calcul_cte1();
		paliers_temperatures.add(resultat);

		return cte;
	}

	@Override
	public double getCoefficientFatigueA() {
		return 0.9492;
	}

	@Override
	public double getCoefficientFatigueB() {
		return -0.466;
	}

	public String toString() {
		return getPas() + ";" + getNombre_billes_x() + ";"
				+ getNombre_billes_y() + ";" + getPresence_billes_angles() + ";"
				+ getPuce_x() + ";" + getPuce_y() + ";" + getPuce_z() + ";" + 0
				+ ";" + 0 + ";" + 0 + ";" + getPcb_x() + ";" + getPcb_y();

	}

}

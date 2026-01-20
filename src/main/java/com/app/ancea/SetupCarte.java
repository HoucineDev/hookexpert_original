package com.app.ancea;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;

public class SetupCarte {

	private ArrayList<SetupComposantCarte> composantsCarte;
	private HashMap<String, Integer>composants ; 
	private int mois;
	private CycleThermique cycle;
	private LineChart <Number, Number> lineChartProbabiliteDeDefaillance, lineChartDensiteDeProbabilite;
	private double[] densitesDeProbabilite, densitesDeProbabilite2, probabilitesDeDefaillance, sommeDensitesDeProbabilite, sommeProbabilitesDefaillance;
	private boolean vernis;
	
	public XYChart.Series<Number, Number> series, series2;
	
	public SetupCarte(HashMap<String, Integer>composants , String cheminProfilDeVie, String cheminPCB, CycleThermique cycle, int mois, boolean vernis) {
		
		this.composantsCarte = new ArrayList<SetupComposantCarte>();
		this.composants = composants;
		this.vernis = vernis;
		this.mois = mois;
		this.cycle = cycle;
		createSetupCarte(composants, cheminProfilDeVie, cheminPCB);
	}

	public void createSetupCarte(HashMap<String, Integer>composants , String cheminProfilDeVie, String cheminPCB) {
		
		// Permet de convertir un double en ecriture scientifique
		NumberFormat format = new DecimalFormat("#.##E0");
		
		calculsProbabiliteEtDensite(cheminProfilDeVie, cheminPCB, composants);
		

		/* Probabilite De Defaillance */
		// creation des line chart
		NumberAxis axeXProbabiliteDeDefaillance = new NumberAxis();
		NumberAxis axeYProbabiliteDeDefaillance = new NumberAxis();
		
		axeXProbabiliteDeDefaillance.setLabel("Durée (Mois)");
		axeYProbabiliteDeDefaillance.setLabel("Probabilité de défaillance");
		
		//formatage des axes des ordonnees pour avoir une écriture scientifique
		axeYProbabiliteDeDefaillance.setTickLabelFormatter(new StringConverter<Number>() {
			
			@Override
			public String toString(Number number) {
				return format.format(number.doubleValue());
			}
			
			@Override
			public Number fromString(String arg0) {
				try {
					return format.parse(arg0);
				}catch (ParseException e) {
					e.printStackTrace();
					return 0;
				}
			}
		});
		
		lineChartProbabiliteDeDefaillance = new LineChart<Number, Number>(axeXProbabiliteDeDefaillance, axeYProbabiliteDeDefaillance);
		lineChartProbabiliteDeDefaillance.setCreateSymbols(false);
		lineChartProbabiliteDeDefaillance.setAnimated(false);
		
		series = new XYChart.Series<>();
		
		
		/* Densité de Probabilité */ 
		
		NumberAxis axeXDensiteDeProbabilite = new NumberAxis();
		NumberAxis axeYDensiteDeProbabilite = new NumberAxis();
		
		axeXDensiteDeProbabilite.setLabel("Durée (Mois)");
		axeYDensiteDeProbabilite.setLabel("Densité de Probabilité");
		//formatage des axes des ordonnees pour avoir une écriture scientifique
		axeYDensiteDeProbabilite.setTickLabelFormatter(new StringConverter<Number>() {
			
			@Override
			public String toString(Number number) {
				return format.format(number.doubleValue());
			}
			
			@Override
			public Number fromString(String arg0) {
				try {
					return format.parse(arg0);
				}catch (ParseException e) {
					e.printStackTrace();
					return 0;
				}
			}
		});
		
		lineChartDensiteDeProbabilite = new LineChart<Number, Number>(axeXDensiteDeProbabilite, axeYDensiteDeProbabilite);
		lineChartDensiteDeProbabilite.setCreateSymbols(false);
		lineChartDensiteDeProbabilite.setAnimated(false);
		
		series2 = new XYChart.Series<>();
		
		refreshLineChart();
		
	}

	/**
	 * Rafraichissement du graphique de la densite de probabilite et de la probabilite de defaillance.
	 */
	protected void refreshLineChart() {
		
		series.getData().clear();
		series2.getData().clear();
		
		lineChartProbabiliteDeDefaillance.getData().clear();
		lineChartDensiteDeProbabilite.getData().clear();
		
		
		// mise a jour des calculs de la densite de probabilite et de la probabilite de defaillance par rapport au mois
		for(int i = 1; i <= this.mois; i++) {
			double resultatDensiteDeProbabilite = densitesDeProbabilite[i] * Math.exp(densitesDeProbabilite2[i]);
			sommeDensitesDeProbabilite[i] =  resultatDensiteDeProbabilite;
			sommeProbabilitesDefaillance[i] = 1 - sommeProbabilitesDefaillance[i];
			series.getData().add(new XYChart.Data(i, sommeProbabilitesDefaillance[i]));
			series2.getData().add(new XYChart.Data(i, sommeDensitesDeProbabilite[i]));

		}
		
		lineChartProbabiliteDeDefaillance.getData().add(series);
		setLineChartProbabiliteDeDefaillance(lineChartProbabiliteDeDefaillance);
		
		lineChartDensiteDeProbabilite.getData().add(series2);
		setLineChartDensiteDeProbabilite(lineChartDensiteDeProbabilite);
	}

	/**
	 * @param cheminProfilDeVie Chemin du profil de vie
	 * @param cheminPCB Chemin du PCB
	 * @param composants hashmap contenant les noms des composants et le nombre de composant
	 */
	protected void calculsProbabiliteEtDensite(String cheminProfilDeVie, String cheminPCB, HashMap<String, Integer> composants) {
		
		double densiteDeProbabilite = 0;
		double densiteDeProbabilite2 = 0;
		double probabiliteDeDefaillance = 0;
		double sommePartielProbabiliteDeDefaillance = 0;
		
		
		/* Les valeurs sont stocker dans un tableau car on connait à l'avance le nombre de mois
		 * et l'acces et moins couteux
		*/
		densitesDeProbabilite = new double[this.mois+1];
		densitesDeProbabilite2 = new double[this.mois+1];
		probabilitesDeDefaillance = new double[this.mois+1];
		sommeDensitesDeProbabilite = new double[this.mois+1];
		sommeProbabilitesDefaillance = new double[this.mois+1];
		
		// initialisation du tableau a 1 pour ne pas multiplier par 0
		for(int i = 1; i < probabilitesDeDefaillance.length; i++) {
			sommeProbabilitesDefaillance[i] = 1;
		}
		
		composantsCarte.clear();
		for(String nomComposant: composants.keySet()) {
			SetupComposantCarte composantCarte = new SetupComposantCarte(cheminProfilDeVie, cheminPCB, nomComposant, composants.get(nomComposant), cycle, mois, vernis);
			composantsCarte.add(composantCarte);
			
			for(int i = 1; i <= mois; i++) {
				densiteDeProbabilite = composantCarte.getDensiteProbabilite(i);
				densiteDeProbabilite += densitesDeProbabilite[i];
				densitesDeProbabilite[i] = densiteDeProbabilite;
				
				densiteDeProbabilite2 = composantCarte.getDensiteProbabilite2(i);
				densiteDeProbabilite2 += densitesDeProbabilite2[i];
				densitesDeProbabilite2[i] = densiteDeProbabilite2;
				
				probabiliteDeDefaillance = composantCarte.getProbabiliteDeDefaillance(i);
				sommePartielProbabiliteDeDefaillance = Math.pow(1 - probabiliteDeDefaillance, composants.get(nomComposant));	
				sommeProbabilitesDefaillance[i] *= sommePartielProbabiliteDeDefaillance;	
				
			}
		}
		
	}

	public LineChart<Number, Number> getLineChartProbabiliteDeDefaillance() {
		return lineChartProbabiliteDeDefaillance;
	}

	public void setLineChartProbabiliteDeDefaillance(LineChart<Number, Number> lineChartProbabiliteDeDefaillance) {
		this.lineChartProbabiliteDeDefaillance = lineChartProbabiliteDeDefaillance;
	}

	public LineChart<Number, Number> getLineChartDensiteDeProbabilite() {
		return lineChartDensiteDeProbabilite;
	}

	public void setLineChartDensiteDeProbabilite(LineChart<Number, Number> lineChartDensiteDeProbabilite) {
		this.lineChartDensiteDeProbabilite = lineChartDensiteDeProbabilite;
	}

	public ArrayList<SetupComposantCarte> getComposantsCarte() {
		return composantsCarte;
	}

	public void setComposantsCarte(ArrayList<SetupComposantCarte> composantsCarte) {
		this.composantsCarte = composantsCarte;
	}

	public HashMap<String, Integer> getComposants() {
		return composants;
	}

	public void setComposants(HashMap<String, Integer> composants) {
		this.composants = composants;
	}
}

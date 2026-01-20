package com.app.ancea;


import javafx.scene.layout.HBox;

public abstract class CouchePcb extends HBox{
	
	//classe mère, commune des couches composants le pcb 

	public abstract void calcul();
	public abstract void calcul(CoucheElectriquePcb c1,CoucheElectriquePcb c2) ;
	public abstract double epaisseur_totale_couche();
	public abstract String toString();
	
}

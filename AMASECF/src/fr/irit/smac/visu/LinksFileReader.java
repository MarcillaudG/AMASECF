package fr.irit.smac.visu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.irit.smac.core.Links;
import fr.irit.smac.model.Entity;
import fr.irit.smac.model.Relation;
import fr.irit.smac.model.Snapshot;


public class LinksFileReader {


	private BufferedReader reader;

	private String expeName;

	public LinksFileReader(String expeName) {
		this.expeName = expeName;
		try {
			this.reader = new BufferedReader(new FileReader(new File(expeName)));
		} catch (FileNotFoundException e) {
			System.err.println("ERROR READER");
		}
		String line;
		Links links = new Links(expeName,false);
		links.dropExperiment(expeName);
		try {
			Snapshot snap = new Snapshot();
			String nameOfEntity = "";
			boolean fixes = true;
			while(!(line = this.reader.readLine()).contains("EXPE")) {
				if(line.contains("AGFunction")) {
					nameOfEntity = line.split(",")[1];
					snap.addEntity(nameOfEntity, "AGFunction");
				}
				if(line.contains("Fixes")) {
					fixes = true;
				}
				if(line.contains("Variables")) {
					fixes = false;
				}
				if(line.contains("Type")) {
					String[] varSplit = line.split(",");
					if(fixes) {
						snap.getEntity(nameOfEntity).addOneAttribute("Fixes", varSplit[1], Double.parseDouble(varSplit[2]));
					}
					else {
						snap.getEntity(nameOfEntity).addOneAttribute("Variables", varSplit[1], Double.parseDouble(varSplit[2]));
					}
				}
			}
			links.addSnapshot(snap);
			while((line = this.reader.readLine()) != null) {
				if(line.contains("END Cycle")) {
					links.addSnapshot(snap);
				}
				if(line.contains("Cycle :")) {
					List<String> toDelete = new ArrayList<String>();
					for(Relation r : snap.getRelations()) {
						toDelete.add(r.getName());
					}
					for(String s : toDelete) {
						snap.removeRelation(s);
					}
					toDelete = new ArrayList<String>();
					for(Entity e : snap.getEntityList()) {
						if(e.getType().equals("Variable")) {
							toDelete.add(e.getName());
						}
					}
					for(String s : toDelete) {
						snap.removeEntity(s);
					}
					for(Entity e : snap.getEntityList()) {
						if(e.getType().equals("Variable")) {
							System.out.println(e.getName());
						}
					}
				}
				if(line.contains("Fixe")) {
					String strsplit[] = line.split(",");
					snap.getEntity(strsplit[1]).addOneAttribute("Fixes", strsplit[2], Double.parseDouble(strsplit[3]));
				}
				if(line.contains("VAR")) {
					String strsplit[] = line.split(",");
					if(!snap.containsEntity(strsplit[1])) {
						snap.addEntity(strsplit[1], "Variable");
					}
					line = this.reader.readLine();
					strsplit = line.split(",");
					snap.addRelation(strsplit[0],strsplit[1], strsplit[0] + " Communicate "+ strsplit[1], true, "Communication");
				}
				if(line.contains("REL")) {
					String strsplit[] = line.split(",");
					snap.addRelation(strsplit[1], strsplit[2], strsplit[1]+ "is received by "+strsplit[2], true, "Reception");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("LINKS Ended well");
	}
	
	public static void main(String args[]) {
		LinksFileReader lfr = new LinksFileReader("linksexpe.txt");
	}


}

package pl.agh.bgrochal.physics;

import javax.swing.SwingUtilities;
import java.util.*;
import java.io.*;

import pl.agh.bgrochal.physics.controllers.*;
import pl.agh.bgrochal.physics.models.*;
import pl.agh.bgrochal.physics.views.*;

public class MainApplication {

	public static void main(String[] args) {
		
		/* CONSOLE VERSION */
    	
		BufferedReader buff = null;
    	String line;
    	String arg = "";
    	
    	try {
    		arg = args[0];
		}
    	catch(ArrayIndexOutOfBoundsException e) {							/* If runs without arguments */
    		openGUI();
    	}
    	
    	ParticleModel part = new ParticleModel();							/* Creating particle model to compute values */
    	
    	
    	try {
    		buff = new BufferedReader(new FileReader(arg));
    		
    		while ((line=buff.readLine()) != null) {
    			
    			String [] inlineValues = new String[4];
    			int counter = 0;
    			
    			line = line.replaceAll(",", ", ");							/* Now we have always four arguments in CSV File - StringTokenizer does not overlook white spaces, which we will remove using trim() */
    			StringTokenizer token = new StringTokenizer(line, ",");		/* Parsing */
    			while(token.hasMoreTokens())
    				inlineValues[counter++] = token.nextToken().trim();
    				
    			try {														/* Generally unnecessary, just in case */
	    			if(!part.compareName(inlineValues[0])) {
	    				System.out.println("Invalid input");
	    				continue;
	    			}
	    			part.setName(inlineValues[0]);
	    			
	    			if(!part.compareUnit(inlineValues[2])) {
	    				System.out.println("Invalid input");
	    				continue;
	    			}
	    			part.setUnit(inlineValues[2]);
	    			
	    			try {
	    				double energy = Double.parseDouble(inlineValues[1]);
	    				part.setEnergy(energy);
	    			}
	    			catch(NumberFormatException e) {
	    				part.setEnergy(-1);
	    			}
	    			finally {
	    				
	    				try {
		    				double velocity = Double.parseDouble(inlineValues[3]);
		    				part.setRelativeVelocity(velocity);
		    			}
		    			catch(NumberFormatException exc) {
		    				part.setRelativeVelocity(-1);
		    			}
	    				finally {
	    					
	    					if(part.getEnergy() > 0 && part.getRelativeVelocity() > 0) {				/* If both velocity and energy is set */
	    						System.out.println("Invalid input");
	    	    				continue;
	    					}
	    					
	    					if(part.getEnergy() == -1) {												/* If energy is not set */
	    						if(part.getRelativeVelocity() < 0 || part.getRelativeVelocity() > 1) {	/* If velocity is not set or value is invalid */
	    							System.out.println("Invalid input");
		    	    				continue;
	    						}
	    						
	    						part.countEnergy();
	    						System.out.println(part.getEnergy());
	    	    			}
	    					else if(part.getRelativeVelocity() == -1) {
	    						if(part.getEnergy() < 0) {												/* If energy is not set or value is invalid */
	    							System.out.println("Invalid input");
		    	    				continue;
	    						}
	    						
	    						part.countRelativeVelocity();
	    						System.out.println(part.getRelativeVelocity());
	    					}
	    				}
	    			}
    			}
    			catch(ArrayIndexOutOfBoundsException exc) {
    				System.out.println("Invalid Input");
    				continue;
    			}
    		}
     
    	} 
    	catch (FileNotFoundException e) {} 
    	catch (IOException e) {}
    	finally {
    		if (buff != null) {
    			try {
    				buff.close();
    			} catch (IOException e) {}
    		}
    	}
    	
    	
	}
	
	public static void openGUI() {
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	
            	/* VIEWS  */
            	MainApplicationView mainView = new MainApplicationView();
            	RelativeVelocityView velocityView = new RelativeVelocityView(mainView);
            	EnergyView energyView = new EnergyView(mainView);
            	StatisticsView statView = new StatisticsView();
                GraphView graphView = new GraphView(mainView);
                
                mainView.logAppend("application", "Application started.\n");
                
                /* MODELS */
                StatisticsModel statModel = new StatisticsModel();
            	ParticleModel particleModel = new ParticleModel();
            	GraphModel graphModel = new GraphModel(particleModel, graphView, mainView);
                
                /* CONTROLLERS */
            	MainApplicationController applicationController = new MainApplicationController(mainView,statModel,statView);
                RelativeVelocityController velocityController = new RelativeVelocityController(particleModel,velocityView, energyView, mainView,statModel);
                EnergyController energyController = new EnergyController(particleModel,velocityView,energyView,mainView,statModel);
                GraphController graphController = new GraphController(particleModel, graphModel, velocityView, energyView, graphView,mainView,statModel);
                
                applicationController.control();
                velocityController.control();
                energyController.control();
                graphController.control();
            }
        });
	}

}

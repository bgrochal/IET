package pl.agh.bgrochal.physics.models;

import java.io.*;

import pl.agh.bgrochal.physics.views.*;

public class WriterCSV implements Runnable {

	MainApplicationView mainView;
	GraphModel model;
	GraphView view;
	
	public WriterCSV(GraphModel model, GraphView view, MainApplicationView mainView) {
		this.mainView = mainView;
		this.model = model;
		this.view = view;
	}
	
	@Override
	public void run() {
		view.setProcessingLabel("Processing...");		/* !Model directly communicates with View when changes its state */
		view.getPrintCSVButton().setEnabled(false);
		view.getEnergyGraphButton().setEnabled(false);
		view.getChangeScaleButton().setEnabled(false);
		view.getVelocityGraphButton().setEnabled(false);
		
		try {
			FileWriter file = new FileWriter(model.getPathCSV());
			
			for(Double key : model.getGraphSeries().keySet())
				file.append(key.toString() + ", " + model.getGraphSeries().get(key) + "\n");
			
			file.flush();
		    file.close();
		    
		    mainView.logAppend("info", "CSV File written to: " + model.getPathCSV() + ".\n");
		    
		}
		catch(IOException e) {
			view.setProcessingLabel("Saving error.");
			mainView.logAppend("error", "Unable to write: " + model.getPathCSV() + ".\n");
			try {
				Thread.sleep(1000);
			}
			catch(InterruptedException exc) {}
		}
		
		view.setProcessingLabel("");
		view.getPrintCSVButton().setEnabled(true);
		view.getEnergyGraphButton().setEnabled(true);
		view.getChangeScaleButton().setEnabled(true);
		view.getVelocityGraphButton().setEnabled(true);
		
		
	}

}

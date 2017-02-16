package pl.agh.bgrochal.physics.controllers;

import org.jfree.chart.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.awt.*;

import pl.agh.bgrochal.physics.models.*;
import pl.agh.bgrochal.physics.views.*;

public class GraphController {
	
	private ActionListener velocityButtonListener;
	private ActionListener energyButtonListener;
	private ActionListener changeScaleListener;
	private ActionListener CSVButtonListener;
	private FocusListener focusListener;
	
	private ParticleModel particleModel;
	private GraphModel graphModel;
	
	private RelativeVelocityView velocityView;
	private MainApplicationView mainView;
	private StatisticsModel statModel;
	private EnergyView energyView;
	private GraphView graphView;
	
	private JFileChooser fileDialog;
	
	
	public GraphController(ParticleModel particleModel, GraphModel graphModel, RelativeVelocityView velocityView, EnergyView energyView, GraphView graphView, MainApplicationView mainView, StatisticsModel statModel) {
		this.particleModel = particleModel;
		this.graphModel = graphModel;
		
		this.velocityView = velocityView;
		this.energyView = energyView;
		this.statModel = statModel;
		this.graphView = graphView;
		this.mainView = mainView;
	}
	
	public void control() {
		
		energyButtonListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				statModel.mapValueIncrement("\"Draw graph E(v)!\"", "button");
				graphButtonOnClick();
				
				try {
					if(graphModel.getMinimum() >= 1 || graphModel.getMinimum() < 0)
						throw new NumberFormatException();
				}
				catch(NumberFormatException e) {
					graphView.setMinScale("Invalid input!");
					graphView.changeColorErrorMin(new Color(255,70,70));
					graphModel.setMinimum(-1);
					mainView.logAppend("error", "Invalid input.\n");
					statModel.mapValueIncrement("Graph minimum field", "field");
					return;
				}

				try {
					if(graphModel.getMaximum() >= 1 || graphModel.getMaximum() < 0)
						throw new NumberFormatException();
				}
				catch(NumberFormatException e) {
					graphView.setMaxScale("Invalid input!");
					graphView.changeColorErrorMax(new Color(255,70,70));
					graphModel.setMaximum(-1);
					mainView.logAppend("error", "Invalid input.\n");
					statModel.mapValueIncrement("Graph maximum field", "field");
					return;
				}
				
				try {
					if(graphModel.getPrecision() <= 0)
						throw new NumberFormatException();
				}
				catch(NumberFormatException e) {
					graphView.setPrecision("Invalid input!");
					graphView.changeColorErrorPrec(new Color(255,70,70));
					graphModel.setPrecison(0);
					mainView.logAppend("error", "Invalid input.\n");
					statModel.mapValueIncrement("Graph precision field", "field");
					return;
				}
				
				graphModel.setGraph("velocityToEnergy");
				graphModel.collectData();
				mainView.logAppend("info", "Graph E(v) drawn.\n");
			}
		};
		graphView.getEnergyGraphButton().addActionListener(energyButtonListener);		
		
		velocityButtonListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				statModel.mapValueIncrement("\"Draw graph v(E)!\"", "button");
				graphButtonOnClick();
				
				try {
					if(graphModel.getMinimum() < 0)
						throw new NumberFormatException();
				}
				catch(NumberFormatException e) {
					graphView.setMinScale("Invalid input!");
					graphView.changeColorErrorMin(new Color(255,70,70));
					graphModel.setMinimum(-1);
					mainView.logAppend("error", "Invalid input.\n");
					statModel.mapValueIncrement("Graph minimum field", "field");
					return;
				}

				try {
					if(graphModel.getMaximum() < 0)
						throw new NumberFormatException();
				}
				catch(NumberFormatException e) {
					graphView.setMaxScale("Invalid input!");
					graphView.changeColorErrorMax(new Color(255,70,70));
					graphModel.setMaximum(-1);
					mainView.logAppend("error", "Invalid input.\n");
					statModel.mapValueIncrement("Graph maximum field", "field");
					return;
				}
				
				try {
					if(graphModel.getPrecision() <= 0)
						throw new NumberFormatException();
				}
				catch(NumberFormatException e) {
					graphView.setPrecision("Invalid input!");
					graphView.changeColorErrorPrec(new Color(255,70,70));
					graphModel.setPrecison(0);
					mainView.logAppend("error", "Invalid input.\n");
					statModel.mapValueIncrement("Graph precision field", "field");
					return;
				}
				
				graphModel.setGraph("energyToVelocity");
				graphModel.collectData();
				mainView.logAppend("info", "Graph v(E) drawn.\n");
			}
		};
		graphView.getVelocityGraphButton().addActionListener(velocityButtonListener);
		
		CSVButtonListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				statModel.mapValueIncrement("\"Print to CSV!\"", "button");
				
				fileDialog = new JFileChooser();
				if(fileDialog.showSaveDialog(graphView.getMainWindow()) == JFileChooser.APPROVE_OPTION) {
					File temp = fileDialog.getSelectedFile();
					graphModel.setPathCSV(temp.getPath()+".csv");
					graphModel.writeCSV();
				}
			}
		};
		graphView.getPrintCSVButton().addActionListener(CSVButtonListener);
		
		changeScaleListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				statModel.mapValueIncrement("\"Change axis!\"", "button");
				
				JFreeChart temp = graphView.getAppChart();
				graphView.setAppChart(graphModel.getSecondChart());
				graphModel.setSecondChart(temp);
				mainView.logAppend("info", "Graph scale changed.\n");
			}
		};
		graphView.getChangeScaleButton().addActionListener(changeScaleListener);
		
		focusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            	graphView.changeColorErrorMin(Color.WHITE);
            	graphView.changeColorErrorMax(Color.WHITE);
            	graphView.changeColorErrorPrec(Color.WHITE);
            }
            @Override
            public void focusLost(FocusEvent e) {}
        };
        graphView.getJumpArea().addFocusListener(focusListener);
        graphView.getMinScaleArea().addFocusListener(focusListener);
        graphView.getMaxScaleArea().addFocusListener(focusListener);
	}
	
	public void graphButtonOnClick() {
		
		particleModel.setName(velocityView.getSelectedParticle());
		particleModel.setUnit(energyView.getSelectedUnit());
		
		try {
			double minimal = Double.parseDouble(graphView.getMinScale());
			graphModel.setMinimum(minimal);
		}
		catch(NumberFormatException e) {
			graphView.setMinScale("Invalid input!");
			graphView.changeColorErrorMin(new Color(255,70,70));
			graphModel.setMinimum(-1);
			return;
		}
		
		try {
			double maximal = Double.parseDouble(graphView.getMaxScale());
			graphModel.setMaximum(maximal);
		}
		catch(NumberFormatException e) {
			graphView.setMaxScale("Invalid input!");
			graphView.changeColorErrorMax(new Color(255,70,70));
			graphModel.setMaximum(-1);
			return;
		}
		
		try {
			double precision = Double.parseDouble(graphView.getPrecision());
			graphModel.setPrecison(precision);
		}
		catch(NumberFormatException e) {
			graphView.setPrecision("Invalid input!");
			graphView.changeColorErrorPrec(new Color(255,70,70));
			graphModel.setPrecison(0);
			return;
		}
	}
	
}

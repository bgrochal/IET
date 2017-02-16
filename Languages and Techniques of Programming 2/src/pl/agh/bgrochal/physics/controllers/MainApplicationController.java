package pl.agh.bgrochal.physics.controllers;

import java.awt.event.*;

import pl.agh.bgrochal.physics.models.*;
import pl.agh.bgrochal.physics.views.*;

public class MainApplicationController {
	
	private ActionListener aboutActionListener;
	private ActionListener statsActionListener;
	private ActionListener exitActionListener;
	private ActionListener logActionListener;
	
	private StatisticsModel statModel;
	private StatisticsView statView;
	private MainApplicationView view;
	
	public MainApplicationController(MainApplicationView view, StatisticsModel statModel, StatisticsView statView) {
		this.statModel = statModel;
		this.statView = statView;
		this.view = view;
	}
	
	public void control() {
		
		exitActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				System.exit(0);
			}
		};
		view.getFileExit().addActionListener(exitActionListener);
		
		aboutActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				new AboutWindowView().showDialog();
				view.logAppend("info", "\"About\" dialog opened.\n");
			}
		};
		view.getHelpAbout().addActionListener(aboutActionListener);
		
		logActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvenet) {
				if(view.getDebugLog().getText()=="Show Log") {
					view.getScrollPane().setVisible(true);
					view.getDebugLog().setText("Hide Log");
					view.logAppend("info", "Log area is shown.\n");
				}
				else {
					view.getScrollPane().setVisible(false);
					view.getDebugLog().setText("Show Log");
					view.logAppend("info", "Log area is hidden.\n");
				}
			}
		};
		view.getDebugLog().addActionListener(logActionListener);
		
		statsActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				
				view.logAppend("info", "\"Statistics\" dialog opened.\n");
				statView.showDialog();

				for(String key : statModel.getButtonClicks().keySet())
					statView.setButtonStats(key + ": " + statModel.getButtonClicks().get(key) + " / " + (statModel.getButtonClicks().get(key)*100 / statModel.getOverallButtonClicks()) + "%");
				
				for(double key: statModel.getButtonTime().keySet())
					statView.setTimeStats(statModel.getButtonTime().get(key) + ": " + key);
				
				for(String key : statModel.getFieldValues().keySet())
					statView.setFieldStats(key + ": " + statModel.getFieldValues().get(key));
				
			}
		};
		view.getDebugStats().addActionListener(statsActionListener);
		
	}
	
}

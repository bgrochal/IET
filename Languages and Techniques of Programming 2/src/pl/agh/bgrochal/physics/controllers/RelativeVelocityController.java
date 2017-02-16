package pl.agh.bgrochal.physics.controllers;

import java.awt.event.*;
import java.awt.*;

import pl.agh.bgrochal.physics.models.*;
import pl.agh.bgrochal.physics.views.*;

public class RelativeVelocityController {

	private ActionListener actionListener;
	private FocusListener focusListener;
	
	private RelativeVelocityView velocityView;
	private MainApplicationView mainView;
	private StatisticsModel statModel;
	private EnergyView energyView;
	private ParticleModel model;
	
	
	public RelativeVelocityController(ParticleModel model, RelativeVelocityView velocityView, EnergyView energyView, MainApplicationView mainView, StatisticsModel statModel) {
		this.velocityView = velocityView;
		this.energyView = energyView;
		this.statModel = statModel;
		this.mainView = mainView;
		this.model = model;
	}
	
	public void control() {
		actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				statModel.mapValueIncrement("\"Energy to Velocity!\"", "button");
				buttonOnClick();
			}
		};
		velocityView.getEnergyToVelocityButton().addActionListener(actionListener);
		
		focusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            	velocityView.changeColorError(Color.WHITE);
            	energyView.changeColorError(Color.WHITE);
            	energyView.setEnergy("");
            }
            @Override
            public void focusLost(FocusEvent e) {}
        };
        velocityView.getParticleVelocityArea().addFocusListener(focusListener);
	}
	
	private void buttonOnClick() {		
		try {
			double energy = Double.parseDouble(energyView.getParticleEnergy());
			if(energy < 0)
				throw new NumberFormatException();
			model.setEnergy(energy);
			mainView.logAppend("info", "Energy converted to Velocity.\n");
		} catch(NumberFormatException e) {
			statModel.mapValueIncrement("Energy field", "field");
			velocityView.setVelocity("Invalid input!");
			energyView.changeColorError(new Color(255,70,70));
			mainView.logAppend("error", "Invalid data input.\n");
			return;
		}
		
		model.setName(velocityView.getSelectedParticle());
		model.setUnit(energyView.getSelectedUnit());
		model.countRelativeVelocity();
		velocityView.setVelocity(model.getRelativeVelocity().toString());	
	}
	
}

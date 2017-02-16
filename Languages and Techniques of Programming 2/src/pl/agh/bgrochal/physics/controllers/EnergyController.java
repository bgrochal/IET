package pl.agh.bgrochal.physics.controllers;

import java.awt.event.*;
import java.awt.*;

import pl.agh.bgrochal.physics.models.*;
import pl.agh.bgrochal.physics.views.*;

public class EnergyController {
	
	private FocusListener areaFocusListener;
	private FocusListener boxFocusListener;
	private ActionListener actionListener;
	
	private RelativeVelocityView velocityView;
	private MainApplicationView mainView;
	private StatisticsModel statModel;
	private EnergyView energyView;
	private ParticleModel model;
	
	
	public EnergyController(ParticleModel model, RelativeVelocityView velocityView, EnergyView energyView, MainApplicationView mainView, StatisticsModel statModel) {
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
				statModel.mapValueIncrement("\"Velocity to Energy!\"", "button");
				buttonOnClick();
			}
		};
		energyView.getVelocityToEnergyButton().addActionListener(actionListener);
		
		areaFocusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            	velocityView.changeColorError(Color.WHITE);
            	energyView.changeColorError(Color.WHITE);
            	velocityView.setVelocity("");
            }
            @Override
            public void focusLost(FocusEvent e) {}
        };
        energyView.getParticleEnergyArea().addFocusListener(areaFocusListener);

        boxFocusListener = new FocusListener() {
        	@Override
        	public void focusGained(FocusEvent e) {
        		energyView.setEnergy("");
        	}
        	@Override
        	public void focusLost(FocusEvent e) {}
        };
        energyView.getEnergyUnitsList().addFocusListener(boxFocusListener);

	}
	
	public void buttonOnClick() {
		try {
			double velocity = Double.parseDouble(velocityView.getParticleVelocity());
			if(velocity < 0 || velocity > 1)
				throw new NumberFormatException();
			model.setRelativeVelocity(velocity);
			mainView.logAppend("info", "Velocity converted to energy.\n");
		} catch(NumberFormatException e) {
			statModel.mapValueIncrement("Velocity field", "field");
			energyView.setEnergy("Invalid input!");
			velocityView.changeColorError(new Color(255,70,70));
			mainView.logAppend("error", "Invalid data input.\n");
			return;
		}
		
		model.setName(velocityView.getSelectedParticle());
		model.setUnit(energyView.getSelectedUnit());
		model.countEnergy();
		energyView.setEnergy(model.getEnergy().toString());	
	}
	
}

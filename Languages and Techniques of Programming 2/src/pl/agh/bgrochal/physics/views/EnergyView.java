package pl.agh.bgrochal.physics.views;

import java.awt.Color;
import javax.swing.*;

public class EnergyView {
	
	private JFrame mainWindow;

	private JTextField particleEnergyArea;
	private JButton velocityToEnergyButton;
	private JComboBox<String> energyUnitsList;

	private String [] energyUnits = {"eV", "keV", "MeV", "GeV", "TeV"};
	
	
	public EnergyView(MainApplicationView mainView) {
		this.mainWindow = mainView.mainWindow;
		
		createNewEnergyView();
	}
	
	public void createNewEnergyView() {
		
		JLabel particleEnergyLabel = new JLabel("Particle energy:");
		particleEnergyLabel.setBounds(5,190,240,20);
		particleEnergyLabel.setVisible(true);
		mainWindow.getContentPane().add(particleEnergyLabel);
		
		energyUnitsList = new JComboBox<String>(energyUnits);
		energyUnitsList.setBounds(5,220,240,20);
		energyUnitsList.setSelectedIndex(2);
		energyUnitsList.setBackground(Color.white);
		energyUnitsList.setVisible(true);
		mainWindow.getContentPane().add(energyUnitsList);
		
		particleEnergyArea = new JTextField();
		particleEnergyArea.setBounds(5,250,240,20);
		particleEnergyArea.setVisible(true);
		mainWindow.getContentPane().add(particleEnergyArea);
		
		velocityToEnergyButton = new JButton("Velocity to Energy!");
		velocityToEnergyButton.setBounds(45, 410, 160, 40);
		mainWindow.getContentPane().add(velocityToEnergyButton);
		
	}
	
	public void changeColorError(Color color) {
		this.particleEnergyArea.setBackground(color);
	}
	
	public JButton getVelocityToEnergyButton() {
		return this.velocityToEnergyButton;
	}
	
	public JTextField getParticleEnergyArea() {
		return this.particleEnergyArea;
	}
	
	public String getParticleEnergy() {
		return this.particleEnergyArea.getText();
	}
	
	public String getSelectedUnit() {
		return this.energyUnitsList.getSelectedItem().toString();
	}
	
	public JComboBox<String> getEnergyUnitsList() {
		return this.energyUnitsList;
	}
	
	public void setEnergy(String energy) {
		this.particleEnergyArea.setText(energy);
	}
	
}

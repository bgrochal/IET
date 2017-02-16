package pl.agh.bgrochal.physics.views;

import javax.swing.*;
import java.awt.*;

public class RelativeVelocityView {

	private JFrame mainWindow;
	private JButton energyToVelocityButton;
	private JTextField particleVelocityArea;
	
	private MainApplicationView mainView;
	
	
	public RelativeVelocityView(MainApplicationView mainView) {
		this.mainView = mainView;
		this.mainWindow = mainView.mainWindow;
		
		createNewVelocityView();
	}
	
	private void createNewVelocityView() { 	
		
		JLabel particleVelocityLabel = new JLabel("Particle relative velocity:");
		particleVelocityLabel.setBounds(5, 290, 240, 20);
		particleVelocityLabel.setVisible(true);
		this.mainWindow.getContentPane().add(particleVelocityLabel);
		
		particleVelocityArea = new JTextField();
		particleVelocityArea.setBounds(5,320,240,20);
		particleVelocityArea.setVisible(true);
		mainWindow.getContentPane().add(particleVelocityArea);
		
		energyToVelocityButton = new JButton("Energy to Velocity!");
		energyToVelocityButton.setBounds(45, 360, 160, 40);
		mainWindow.getContentPane().add(energyToVelocityButton);
		
	}
	
	public void changeColorError(Color color) {
		this.particleVelocityArea.setBackground(color);
	}
	
	public JButton getEnergyToVelocityButton() {
		return this.energyToVelocityButton;
	}
	
	public JTextField getParticleVelocityArea() {
		return this.particleVelocityArea;
	}
	
	public String getParticleVelocity() {
		return this.particleVelocityArea.getText();
	}
	
	public String getSelectedParticle() {
		return this.mainView.availableParticlesList.getSelectedValue().toString();
	}
	
	public void setVelocity(String velocity) {
		this.particleVelocityArea.setText(velocity);
	}
	
}

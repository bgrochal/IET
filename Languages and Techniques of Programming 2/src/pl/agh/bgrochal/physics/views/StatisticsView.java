package pl.agh.bgrochal.physics.views;

import javax.swing.*;

public class StatisticsView {
	
	private JTextArea buttonStats;
	private JTextArea fieldStats;
	private JTextArea timeStats;
	
	
	public void showDialog(){
		JFrame statWindow = new JFrame();
		statWindow.setVisible(true);
		statWindow.getContentPane().setLayout(null);
		
		statWindow.setSize(360, 480);
		statWindow.setTitle("Statistics");
		statWindow.setLocationRelativeTo(null);
		
		JLabel buttonLabel = new JLabel("Amount of clicks per button / overall percentage:");
		buttonLabel.setBounds(5,5,330,20);
		buttonLabel.setVisible(true);
		statWindow.add(buttonLabel);
		
		buttonStats = new JTextArea();
		buttonStats.setBounds(10,30,320,150);
		buttonStats.setEditable(false);
		buttonStats.setVisible(true);
		statWindow.add(buttonStats);
		
		JLabel timeLabel = new JLabel("Button click time:");
		timeLabel.setBounds(5,190,330,20);
		timeLabel.setVisible(true);
		statWindow.add(timeLabel);
		
		timeStats = new JTextArea();
		timeStats.setLineWrap(true);
		timeStats.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(timeStats);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10,220,320,80);
		scrollPane.setVisible(true);
		statWindow.add(scrollPane);
		
		JLabel fieldLabel = new JLabel("Amount of invalid inputs per field:");
		fieldLabel.setBounds(5,310,330,20);
		fieldLabel.setVisible(true);
		statWindow.add(fieldLabel);
		
		fieldStats = new JTextArea();
		fieldStats.setBounds(10,340,320,90);
		fieldStats.setEditable(false);
		fieldStats.setLineWrap(true);
		fieldStats.setVisible(true);
		statWindow.add(fieldStats);
	}
	
	public void setButtonStats(String newLine) {
		this.buttonStats.setText(this.buttonStats.getText() + newLine + "\n");
	}
	
	public void setTimeStats(String newLine) {
		this.timeStats.setText(this.timeStats.getText() + newLine + "\n");
	}
	
	public void setFieldStats(String newLine) {
		this.fieldStats.setText(this.fieldStats.getText() + newLine + "\n");
	}

}

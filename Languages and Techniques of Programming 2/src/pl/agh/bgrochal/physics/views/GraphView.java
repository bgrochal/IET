package pl.agh.bgrochal.physics.views;

import javax.swing.*;
import java.awt.Color;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class GraphView {

	private JFrame mainWindow;
	private JTextField jumpArea;
	private JTextField minScaleArea;
	private JTextField maxScaleArea;
	
	private JButton printCSVButton;
	private JButton changeScaleButton;
	private JButton energyGraphButton;
	private JButton velocityGraphButton;
	
	private ChartPanel chartFrame;
	private JLabel processingLabel;
	
	
	public GraphView(MainApplicationView mainView) {
		this.mainWindow = mainView.mainWindow;
		
		createNewGraphView();
	}
	
	public void createNewGraphView() {
		
		JLabel graphLabel = new JLabel("Graph:");
		graphLabel.setBounds(295,0,430,20);
		graphLabel.setVisible(true);
		mainWindow.getContentPane().add(graphLabel);
		
		JFreeChart appChart = null;
		chartFrame = new ChartPanel(appChart);
		chartFrame.setBounds(275,30,410,240);
		chartFrame.setVisible(true);
		mainWindow.getContentPane().add(chartFrame);
		
		JLabel minLabel = new JLabel("Minimal value:");
		minLabel.setBounds(295,290,100,20);
		minLabel.setVisible(true);
		mainWindow.getContentPane().add(minLabel);
		
		minScaleArea = new JTextField();
		minScaleArea.setBounds(295,320,100,20);
		minScaleArea.setVisible(true);
		mainWindow.getContentPane().add(minScaleArea);
		
		JLabel maxLabel = new JLabel("Maximal value:");
		maxLabel.setBounds(428,290,100,20);
		maxLabel.setVisible(true);
		mainWindow.getContentPane().add(maxLabel);
		
		maxScaleArea = new JTextField();
		maxScaleArea.setBounds(428,320,100,20);
		maxScaleArea.setVisible(true);
		mainWindow.getContentPane().add(maxScaleArea);
		
		JLabel jumpLabel = new JLabel("Precision:");
		jumpLabel.setBounds(558,290,100,20);
		jumpLabel.setVisible(true);
		mainWindow.getContentPane().add(jumpLabel);
		
		jumpArea = new JTextField();
		jumpArea.setBounds(558,320,100,20);
		jumpArea.setVisible(true);
		mainWindow.getContentPane().add(jumpArea);
		
		energyGraphButton = new JButton("Draw graph E(v)!");
		energyGraphButton.setBounds(295, 360, 160, 40);
		mainWindow.getContentPane().add(energyGraphButton);
		
		velocityGraphButton = new JButton("Draw graph v(E)!");
		velocityGraphButton.setBounds(500, 360, 160, 40);
		mainWindow.getContentPane().add(velocityGraphButton);
		
		changeScaleButton = new JButton("Change axis!");
		changeScaleButton.setBounds(295, 410, 160, 40);
		mainWindow.getContentPane().add(changeScaleButton);
		
		printCSVButton = new JButton("Print to CSV!");
		printCSVButton.setBounds(500, 410, 160, 40);
		mainWindow.getContentPane().add(printCSVButton);
		
		processingLabel = new JLabel("");
		processingLabel.setForeground(Color.RED);
		processingLabel.setBounds(300,460,100,20);
		processingLabel.setVisible(true);
		mainWindow.getContentPane().add(processingLabel);
	}
	
	public void changeColorErrorMin(Color color) {
		this.minScaleArea.setBackground(color);
	}
	
	public void changeColorErrorMax(Color color) {
		this.maxScaleArea.setBackground(color);
	}
	
	public void changeColorErrorPrec(Color color) {
		this.jumpArea.setBackground(color);
	}
	
	public String getMinScale() {
		return this.minScaleArea.getText();
	}
	
	public String getMaxScale() {
		return this.maxScaleArea.getText();
	}
	
	public String getPrecision() {
		return this.jumpArea.getText();
	}
	
	public JButton getVelocityGraphButton() {
		return this.velocityGraphButton;
	}
	
	public JButton getEnergyGraphButton() {
		return this.energyGraphButton;
	}
	
	public JButton getChangeScaleButton() {
		return this.changeScaleButton;
	}
	
	public JButton getPrintCSVButton() {
		return this.printCSVButton;
	}
	
	public JTextField getMinScaleArea() {
		return this.minScaleArea;
	}
	
	public JTextField getMaxScaleArea() {
		return this.maxScaleArea;
	}
	
	public JTextField getJumpArea() {
		return this.jumpArea;
	}
	
	public JFrame getMainWindow() {
		return this.mainWindow;
	}
	
	public JFreeChart getAppChart() {
		return this.chartFrame.getChart();
	}
	
	public void setAppChart(JFreeChart appChart) {
		this.chartFrame.setChart(appChart);
	}
	
	public void setMinScale(String minScale) {
		this.minScaleArea.setText(minScale);
	}
	
	public void setMaxScale(String maxScale) {
		this.maxScaleArea.setText(maxScale);
	}
	
	public void setPrecision(String precision) {
		this.jumpArea.setText(precision);
	}
	
	public void setProcessingLabel(String text) {
		this.processingLabel.setText(text);
	}
	
}

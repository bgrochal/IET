package pl.agh.bgrochal.physics.views;

import java.awt.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

public class MainApplicationView {
	
	private JMenu helpMenu;
	private JMenu fileMenu;
	private JMenu debugMenu;
	private JMenuBar menuBar;
	private JTextArea logArea;
	private JMenuItem fileExit;
	private JMenuItem debugLog;
	private JMenuItem helpAbout;
	private JMenuItem debugStats;
	private JScrollPane scrollPane;
	
	protected JFrame mainWindow;
	protected JLabel particleVelocity;				/* Protected to use in other classes without extra getters and setters */
	protected JList<String> availableParticlesList;		
	private String [] availableParticles = {"Helium", "Proton", "Carbon", "Lithium", "Neutron"};
	
	
	public MainApplicationView() {
		mainWindow = new JFrame();
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setVisible(true);
		
		createNewView();
	}
	
	private void createNewView() {
		
		mainWindow.setSize(700, 650);				/* General window view */
		mainWindow.setTitle("Subatomic particles");
		mainWindow.setLocationRelativeTo(null);
		
		mainWindow.getContentPane().setLayout(null);
		
		menuBar = new JMenuBar();					/* Menu bar */
		mainWindow.setJMenuBar(menuBar);
		
		fileMenu = new JMenu("File");				/* Main options in menu bar */
		debugMenu = new JMenu("Debug");
		helpMenu = new JMenu("Help");
		fileMenu.setVisible(true);
		helpMenu.setVisible(true);
		menuBar.add(fileMenu);
		menuBar.add(debugMenu);
		menuBar.add(helpMenu);
		
		helpAbout = new JMenuItem("About");			/* Options in "Help" subMenu */
		helpAbout.setVisible(true);
		helpMenu.add(helpAbout);
		
		debugLog = new JMenuItem("Show Log");		/* Options in "Debug" subMenu */
		debugLog.setVisible(true);
		debugMenu.add(debugLog);
		
		debugStats = new JMenuItem("Show statistics");
		debugStats.setVisible(true);
		debugMenu.add(debugStats);
		
		fileExit = new JMenuItem("Exit");			/* Options in "File" subMenu */
		fileExit.setVisible(true);
		fileMenu.add(fileExit);
		
		JLabel particlesListLabel = new JLabel("Available particles:");					/* Label of particles list */
		particlesListLabel.setBounds(5,0,240,20);
		particlesListLabel.setVisible(true);
		mainWindow.getContentPane().add(particlesListLabel);
		
		availableParticlesList = new JList<String>(availableParticles);					/* Particles list */
		availableParticlesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		availableParticlesList.setBounds(5,30,240,150);
		availableParticlesList.setSelectedIndex(0);
		availableParticlesList.setVisible(true);
		mainWindow.getContentPane().add(availableParticlesList);
		
		logArea = new JTextArea();
		logArea.setLineWrap(true);
		logArea.setEditable(false);
		logArea.setForeground(Color.BLACK);
		
		scrollPane = new JScrollPane(logArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(40,500,600,70);
		scrollPane.setVisible(false);
		mainWindow.getContentPane().add(scrollPane);
		
	}
	
	public void logAppend(String kind, String text) {
		String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		
		logArea.setText(logArea.getText() + "[" + time + "] " + kind.toUpperCase() + ": " + text);
	}
	
	public JMenuItem getFileExit() {
		return this.fileExit;
	}
	
	public JMenuItem getHelpAbout() {
		return this.helpAbout;
	}
	
	public JMenuItem getDebugStats() {
		return this.debugStats;
	}
	
	public JMenuItem getDebugLog() {
		return this.debugLog;
	}
	
	public JScrollPane getScrollPane() {
		return this.scrollPane;
	}
	
}

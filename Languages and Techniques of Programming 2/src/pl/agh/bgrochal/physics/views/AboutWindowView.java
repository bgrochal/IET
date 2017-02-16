package pl.agh.bgrochal.physics.views;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class AboutWindowView {

	public void showDialog(){
		JFrame aboutWindow = new JFrame();
		aboutWindow.setVisible(true);
		aboutWindow.getContentPane().setLayout(null);
		
		aboutWindow.setSize(180, 240);
		aboutWindow.setTitle("About");
		aboutWindow.setLocationRelativeTo(null);
		
		JLabel versionLabel = new JLabel("Version: 1.0");
		versionLabel.setBounds(5,5,170,20);
		versionLabel.setVisible(true);
		aboutWindow.add(versionLabel);
		
		JLabel authorLabel = new JLabel("Author: Bartlomiej Grochal");
		authorLabel.setBounds(5,35,170,20);
		authorLabel.setVisible(true);
		aboutWindow.add(authorLabel);
	}
	
}

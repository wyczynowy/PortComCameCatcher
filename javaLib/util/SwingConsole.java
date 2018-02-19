package util;

import javax.swing.*;


public class SwingConsole {
	public static void run(final JFrame f, final int width, final int height) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run( ){
				f.setTitle(f.getClass().getSimpleName());
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(width, height);
				f.setVisible(true);
			}
		});	
	}
	
	public static void runWithName(final JFrame f, final int width, final int height, final String name) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run( ){
				f.setTitle(name);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(width, height);
				f.setVisible(true);
			}
		});	
	}
	
	public static void runWithNameAndVersion(final JFrame f, final int width, final int height, final String name, final String version) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run( ){
				f.setTitle(name  + " ver: " + version);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(width, height);
				f.setVisible(true);
				ImageIcon iconImage = new ImageIcon("BatiLogo.png");
				f.setIconImage(iconImage.getImage());
			}
		});	
	}
	
	public static void runWithNameVersionAndLogo(final JFrame f, final int width, final int height, final String name, final String version, final String logoUrl) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run( ){
				f.setTitle(name  + " ver: " + version);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(width, height);
				f.setVisible(true);
				
				f.setIconImage(new javax.swing.ImageIcon(logoUrl).getImage());
				
//				ImageIcon iconImage = new ImageIcon(logoUrl);
//				f.setIconImage(iconImage.getImage());
			}
		});	
	}
}
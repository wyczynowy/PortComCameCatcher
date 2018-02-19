package app;

import javax.swing.JFrame;
import javax.swing.UIManager;

import static util.SwingConsole.runWithNameVersionAndLogo;

public class Main extends JFrame {

	private static final long serialVersionUID = -130737910397146677L;
	
//	private JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

	
	public Main() {
		add(new FirstTabContainer());
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			throw new RuntimeException();
		}
		runWithNameVersionAndLogo(new Main(), 500, 500, "Port Come Frame Catcher", "0.002", "LogoEL_blue.png");
	}
}

package app;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import com.fazecast.jSerialComm.SerialPort;

import net.miginfocom.swing.MigLayout;

class JTextFieldChecker implements Runnable {
	
	JTextField jtfToCheck = new JTextField();
	JToggleButton jtbToControl = new JToggleButton();
	
	JTextFieldChecker(JTextField jtfToCheck, JToggleButton jtbToControl) {
		this.jtfToCheck = jtfToCheck;
		this.jtbToControl = jtbToControl;
	}

	@Override
	public void run() {
		while(true) {
			try {
				StringParseToHexArray.parse(jtfToCheck.getText());

				jtbToControl.setEnabled(true);
				jtbToControl.setText("Find frame ");
			} catch(NumberFormatException e1) {
				jtbToControl.setEnabled(false);
				jtbToControl.setSelected(false);
				jtbToControl.setText("Can't parse");
			}
	        try {
	            TimeUnit.MILLISECONDS.sleep(1);
	          } catch(InterruptedException e) {
	            System.out.println("InterruptException -> secondTab.SendData");
	            return;
	          }
		}
		
	}
	
}

public class FirstTabContainer extends Container {
	private static final long serialVersionUID = -9041378035909045427L;
	private SerialPort[] ports;
	public static SerialPort usbPort;
	private JComboBox<String> portList = new JComboBox<String>();
	private JToggleButton connectButton = new JToggleButton("Connect");
	public static JToggleButton searchButton = new JToggleButton("Find frame ");
	private JLabel connection1InfoLabel = new JLabel("Niepodłączony");
	private JTextField frameToFind = new JTextField(40);
	public static JTextArea consoleArea = new JTextArea(20, 35);
	public static List<Integer> intFrameToFind = new ArrayList<Integer>();
	private JButton cleanConsoleAreaButton = new JButton("Clear");
	
	private ExecutorService executor1 = Executors.newSingleThreadExecutor();
	private ExecutorService executor2 = Executors.newSingleThreadExecutor();
	private ExecutorService executor3 = Executors.newSingleThreadExecutor();
	
	ReceiveData receiveData = new ReceiveData();								// Zadanie odbioru danych z portu COM
	FindFrame findFrame = new FindFrame();
	JTextFieldChecker jtfc = new JTextFieldChecker(frameToFind, searchButton);
	
	private ActionListener cleanConsoleAreaButtonListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			consoleArea.setText("");
			
		}
	};
	
	private ActionListener connectButtonListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			usbPort = ports[portList.getSelectedIndex()];
			if(connectButton.isSelected()) {
				if(usbPort.openPort()) {
					usbPort.setComPortParameters(115200, 8, 8, 0);
					connection1InfoLabel.setText("Podłączony");
					executor1.execute(receiveData);					// Rozpoczecie zadania odbioru danych z portu COM
					executor2.execute(findFrame);
				} else {
					connection1InfoLabel.setText("Niepodłączony");
					connectButton.setSelected(false);
				}
			} else {
				usbPort.closePort();
				connection1InfoLabel.setText("Rozłączony");
			}
			
		}
		
	};
	
	private ActionListener searchButtonListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			intFrameToFind.clear();
			try {
				intFrameToFind.addAll(StringParseToHexArray.parse(frameToFind.getText()));
			} catch(NumberFormatException e1) {
				System.out.println("Blad w searchButtonListener");
			}
		}
		
	};
	
	private ActionListener frameToFindListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				/*intFrameToFind.addAll(*/StringParseToHexArray.parse(frameToFind.getText())/*)*/;
			} catch(NumberFormatException e1) {
				System.out.println("============================= Blad parsowania ==========================");
			}
			
		}
	};
	
	public FirstTabContainer() {
		executor3.execute(jtfc);
		setLayout(new MigLayout("", "", ""));
		
		ports = SerialPort.getCommPorts();
		for(SerialPort port : ports)
			portList.addItem(port.getSystemPortName());
			
		connectButton.addActionListener(connectButtonListener);
		searchButton.addActionListener(searchButtonListener);
		cleanConsoleAreaButton.addActionListener(cleanConsoleAreaButtonListener);
		frameToFind.addActionListener(frameToFindListener);
		
		consoleArea.setFont(new Font("Arial", 0, 15));;
		
		add(portList);
		add(connectButton);
		add(connection1InfoLabel, "wrap");
		add(new JLabel("Ramka do wyszukania: "), "span 3, wrap");
		add(searchButton);
		Container container1 = new Container();
		container1.setLayout(new MigLayout());
		container1.add(new JLabel("0x55"));
		container1.add(frameToFind);
		container1.add(new JLabel("0x56"));
		add(container1, "span 2, wrap");
		JScrollPane jsc = new JScrollPane(consoleArea);
//		add(consoleArea, "span 3");
		add(jsc, "span, wrap");
		add(cleanConsoleAreaButton, "wrap");
		
		
	}
	
}
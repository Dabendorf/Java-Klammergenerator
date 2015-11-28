package klammergenerator;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;

/**
 * Dies ist die Hauptklasse des nicht-deterministischen Klammergenerators. Sie steuert die graphische Oberflaeche und auch die Ausfuehrung der Klammerregeln.
 * 
 * @author Lukas Schramm
 * @version 1.0
 *
 */
public class Klammern {
	
	private JFrame frame1 = new JFrame("Klammergenerator");
	private NumberFormat format1 = NumberFormat.getInstance(); 
	private NumberFormatter formatter1 = new NumberFormatter(format1);
	private JLabel labelAnzZeichen = new JLabel("Zeichenanzahl: ");
	private JFormattedTextField numZeichen = new JFormattedTextField(formatter1);
	private JLabel labelEingabeZeichen = new JLabel("Zeichen: ");
	private JTextField eingabeZeichen = new JTextField();
	private JButton buttonRegelnAnwenden = new JButton("Ausführen");
	private String umbruch = System.getProperty("line.separator");
	private JTextArea regelArea = new JTextArea();
	private JScrollPane regelAreaScrollPane = new JScrollPane(regelArea);
	private JTextArea ausgabeArea = new JTextArea();
	private JScrollPane ausgabeAreaScrollPane = new JScrollPane(ausgabeArea);
	private String eingabe = "";
	private char grammatikZeichen = '☭';
	private int anzChars = 0;
	private int anzS = 0;
	
	public Klammern() {
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.setPreferredSize(new Dimension(300,300));
		frame1.setMinimumSize(new Dimension(300,300));
		frame1.setMaximumSize(new Dimension(500,500));
		frame1.setResizable(true);
		
		Container cp = frame1.getContentPane();
		cp.setLayout(new GridBagLayout());
		JPanel eingabeFlaeche = new JPanel();
		eingabeFlaeche.setLayout(new BorderLayout());
		eingabeFlaeche.add(labelAnzZeichen, BorderLayout.WEST);
		eingabeFlaeche.add(numZeichen, BorderLayout.CENTER);
		JPanel eingabeZeichenFlaeche = new JPanel();
		eingabeZeichenFlaeche.setLayout(new BorderLayout());
		eingabeZeichenFlaeche.add(labelEingabeZeichen, BorderLayout.WEST);
		eingabeZeichenFlaeche.add(eingabeZeichen, BorderLayout.CENTER);
		cp.add(eingabeFlaeche, new GridBagFelder(0, 0, 1, 1, 1, 0.1));
		cp.add(eingabeZeichenFlaeche, new GridBagFelder(0, 1, 1, 1, 1, 0.1));
		cp.add(regelAreaScrollPane, new GridBagFelder(0, 2, 1, 1, 1, 0.2));
		cp.add(buttonRegelnAnwenden, new GridBagFelder(0, 3, 1, 1, 1, 0.1));
		cp.add(ausgabeAreaScrollPane, new GridBagFelder(0, 4, 1, 1, 1, 0.4));
		eingabeFlaeche.setPreferredSize(new Dimension(0,0));
		eingabeZeichenFlaeche.setPreferredSize(new Dimension(0,0));
		regelAreaScrollPane.setPreferredSize(new Dimension(0,0));
		buttonRegelnAnwenden.setPreferredSize(new Dimension(0,0));
		ausgabeAreaScrollPane.setPreferredSize(new Dimension(0,0));
		
		buttonRegelnAnwenden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ausgabe();
			}
		});
		regelArea.setText("(1) "+grammatikZeichen+"-->()"+umbruch+"(2) "+grammatikZeichen+"-->("+grammatikZeichen+")"+umbruch+"(3) "+grammatikZeichen+"-->("+grammatikZeichen+""+grammatikZeichen+")");
		regelArea.setLineWrap(true);
		regelArea.setWrapStyleWord(true);
		regelArea.setToolTipText("Regeln");
		regelArea.setEditable(false);
		ausgabeArea.setText("");
		ausgabeArea.setLineWrap(true);
		ausgabeArea.setWrapStyleWord(true);
		ausgabeArea.setToolTipText("Umgewandelter Inhalt");
		ausgabeArea.setEditable(false);
		numZeichen.setText("5");
		numZeichen.setHorizontalAlignment(SwingConstants.RIGHT);
		eingabeZeichen.setText(String.valueOf(grammatikZeichen));
		eingabeZeichen.setHorizontalAlignment(SwingConstants.RIGHT);
		
		format1.setGroupingUsed(false);
	    formatter1.setAllowsInvalid(false);
		frame1.pack();
		frame1.setLocationRelativeTo(null);
		frame1.setVisible(true);
	}
	
	/**
	 * Diese Methode wird beim Klicken auf den JButton ausgeloest. Sie setzt alle Werte auf Anfang zurueck und liest die Anzahl der gewollten Zeichen sowie das Hauptzeichen ein.
	 * Sie gibt eine Fehlermeldung aus, wenn hier kein Integer eingelesen werden kann.
	 */
	private void ausgabe() {
		try {
			eingabe = "";
			anzChars = 0;
			anzS = 0;
			grammatikZeichen = eingabeZeichen.getText().charAt(0);
			regelArea.setText("(1) "+grammatikZeichen+"-->()"+umbruch+"(2) "+grammatikZeichen+"-->("+grammatikZeichen+")"+umbruch+"(3) "+grammatikZeichen+"-->("+grammatikZeichen+""+grammatikZeichen+")");
			generieren(Integer.valueOf(numZeichen.getText()));
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Du hast falsche Werte eingetragen."+System.getProperty("line.separator")+"Wenn Du dies nicht korrigierst"+System.getProperty("line.separator")+"bekommst Du kein Ergebnis!", "Falscheingabe", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * Diese Methode nimmt die Zeichenanzahl entgegen und generiert die Anfangskette. Ausserdem enthaelt sie die while-Schleife, die arbeitet, bis alle Regeln abgearbeitet wurden.
	 * Sie gibt ausserdem das Ergebnis in der TextArea aus.
	 * @param anz Nimmt die Zeichenanzahl entgegen.
	 */
	private void generieren(int anz) {
		if(anz>0) {
			anzChars = anz;
			anzS = anz;
			for(int i=0;i<anz;i++) {
		    	eingabe += grammatikZeichen;
		    }
			while(anzS>0) {
				anzChars = eingabe.length();
				regel();
			}
			ausgabeArea.setText(eingabe);
		} else {
			JOptionPane.showMessageDialog(null, "Bitte gib bei der Zeichenanzahl eine positive Zahl ein!", "Falscheingabe", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * Diese Methode lost eine Regel aus, die sofort an einem Zeichen ausgefuehrt wird.
	 */
	private void regel() {
		Random r = new Random();
		int regelNum = r.nextInt(30);
		ArrayList<Character> eingabeChars = new ArrayList<Character>();
		for(char c:eingabe.toCharArray()) {
			eingabeChars.add(c);
		}
		if(regelNum<15) {
			for(int i=0;i<anzChars;i++) {
				ArrayList<Character> eingabeCharsTemp = eingabeChars;
				if(eingabeCharsTemp.get(i)==grammatikZeichen) {
					eingabeChars.set(i,'(');
					eingabeChars.add(i+1,')');
					anzChars++;
					anzS--;
					break;
				}
			}
			eingabe = "";
			for(char c:eingabeChars) {
				eingabe += c;
			}
		} else if(regelNum>28) {
			for(int i=0;i<anzChars;i++) {
				ArrayList<Character> eingabeCharsTemp = eingabeChars;
				if(eingabeCharsTemp.get(i)==grammatikZeichen) {
					eingabeChars.set(i,'(');
					eingabeChars.add(i+1,grammatikZeichen);
					eingabeChars.add(i+2,grammatikZeichen);
					eingabeChars.add(i+3,')');
					anzChars += 3;
					anzS++;
					break;
				}
			}
			eingabe = "";
			for(char c:eingabeChars) {
				eingabe += c;
			}
		} else {
			for(int i=0;i<anzChars;i++) {
				ArrayList<Character> eingabeCharsTemp = eingabeChars;
				if(eingabeCharsTemp.get(i)==grammatikZeichen) {
					eingabeChars.set(i,'(');
					eingabeChars.add(i+1,grammatikZeichen);
					eingabeChars.add(i+2,')');
					anzChars += 2;
					break;
				}
			}
			eingabe = "";
			for(char c:eingabeChars) {
				eingabe += c;
			}
		}
	}
	
	public static void main(String[] args) {
		new Klammern();
	}
}
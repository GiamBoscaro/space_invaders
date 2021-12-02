import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class HelpPanel extends JPanel{
	
	//LA GESTIONE DI QUESTO PANNELLO E' UGUALE A TUTTI GLI ALTRI. PER INFORMAZIONI CONTROLLA I COMMENTI DEGLI ALTRI PANNELLI
	private static final long serialVersionUID = 7005669733605470138L;
	
	private ImageIcon retIcon, goIcon;	//icone dei pulsanti
	private BufferedImage background;	//immagini di background
	private JButton ret, go;	//bottoni
	private Main main;	//gestore dell'applicazione
	private JLabel tutorial, title;	//labels
	private Font font = new Font ("Razer Regular", Font.BOLD , 20);	//font utilizzato
	private String textTutorial = "<html>A/S or LEFT/RIGHT to move <br> SPACE to shoot<br> F to use PowerUp <br> R to restart game when you die <br>ESC to pause your game";	//stringa descrizione comandi

	public HelpPanel(Main m, File f){
		
		main = m;	//imposto il gestore
		
		try{background = ImageIO.read(f);}catch(Exception ex){}	//imposto l'immagine di background
		
		initLabels();		//inizializzo i label
		initButtons();	//inizializzo i bottoni

	}	
	
	private void initButtons() {
		
		Border line = new LineBorder(Color.WHITE);	//colore dei contorni dei bottoni
		Border margin = new EmptyBorder(5, 15, 5, 15);	//impostazioni dei margini
		Border compound = new CompoundBorder(line, margin);	//creazione di un oggetto comprendente le due precedenti definizioni

		ret = new JButton ();	
		
		retIcon = new ImageIcon("src/img/return.png","");	//setting dell'icona
			
		ret.addActionListener(new ActionListener() {	//ascoltatore del pulsante
		    public void actionPerformed(ActionEvent e){			    	
		    	main.showMenu();	//mostra il menu
		    }
		});
		
		ret.setLocation(552, 330);	//posizione nel frame
		ret.setSize(41,41);	//dimensioni del pulsante
		ret.setBackground(Color.blue);	//colore di background del pulsante
		ret.setBorder(compound);		
		ret.setIcon(retIcon);
		
		go = new JButton ();	
		
		goIcon = new ImageIcon("src/img/go.png","");
			
		go.addActionListener(new ActionListener() {					
		    public void actionPerformed(ActionEvent e){			    	
		    	main.showWeapon();
		    }
		});
		
		go.setLocation(1, 330);
		go.setSize(41,41);
		go.setBackground(Color.blue);
		go.setBorder(compound);
		
		go.setIcon(goIcon);

		add(ret);
		add(go);
	}

	
	private void initLabels() {
		
		tutorial = new JLabel("", JLabel.CENTER);	//creazione del label con testo al centro
		
		tutorial.setLocation(100,20);	//posizione nel frame
		tutorial.setSize(400, 300);	//dimensioni
		
		tutorial.setText(textTutorial);		//aggiungo il testo del tutorial al laber
		tutorial.setForeground(Color.white);		//colore del font
		tutorial.setFont(font);	//set del font
		
		tutorial.setVisible(true);	//rendo visibile il tutto
			
		Font font = new Font ("Razer Regular", Font.BOLD , 35);	//cambio della dimensione del font
		
		title = new JLabel("Tutorial", JLabel.CENTER);	//label del titolo
			
		title.setLocation(100,20);	//posizione nel frame
		title.setSize(400, 75);	//dimensioni
		
		title.setForeground(Color.red);		//colore del font
		title.setFont(font);	//set del font
		
		title.setVisible(true);	//rendo visibile il tutto
		
		add(title);	//aggiungo i componenti
		add(tutorial);				
	}
	
	public void paintComponent(Graphics g){		//applicazione del background
		
		setOpaque(false);
		super.paintComponent(g);		
		
		g.drawImage(background, 0, 0, 800, 600, null);
	}
	

}

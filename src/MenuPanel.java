import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MenuPanel extends JPanel{
	
	//LA GESTIONE DI QUESTO PANNELLO E' UGUALE A TUTTI GLI ALTRI. PER INFORMAZIONI CONTROLLA I COMMENTI DEGLI ALTRI PANNELLI
	private static final long serialVersionUID = 917621452800933140L;
	
	private Border line, margin, compound;	
	private JToggleButton toggleSounds, toggleAuto, toggleArduino;
	private ImageIcon infoIcon, startIcon, exitIcon, musicIcon, autoIcon, resetIcon, ardIcon, scoreIcon;
	private BufferedImage background;
	private JButton exit, start, info, reset, score;
	private Main main;
	private JLabel title;
	private JTextField comPorts, user;
	private MaxLengthTextDocument maxLength, maxLengthUser;
	private Font font = new Font ("Razer Regular", Font.BOLD , 35);
	private Font fontmin = new Font ("Razer Regular", Font.BOLD , 25);	
	private Font fontard = new Font ("Razer Regular", Font.BOLD , 15);	

	public MenuPanel(Main m, File f){
		
		main = m;
		
		try{background = ImageIO.read(f);}catch(Exception ex){}		
		
		initStyles();
		initButtons();
		initLabels();
		initToggles();
		initTextFields();
		
	}
	
	public void check(){	//setto la posizione dei pulsanti in base ai flag
		
		invalidate();		
		toggleSounds.setSelected(main.isMuted());
		toggleAuto.setSelected(main.isAuto());		
		toggleArduino.setSelected(main.isArduino());	
		comPorts.setText(main.getComPort());
		user.setText(main.getUser());
		validate();
	}
	
	private void initTextFields() { //aree di inserimento dei nomi dei giocatori
		
		comPorts = new JTextField();
		user = new JTextField();
		
		maxLength = new MaxLengthTextDocument();
		maxLength.setMaxChars(3);		 
		
		maxLengthUser = new MaxLengthTextDocument();
		maxLengthUser.setMaxChars(10);	
		
		comPorts.setLocation(126, 330); //posizione nel pannello
		comPorts.setSize(54, 41); //dimensione 
		comPorts.setDocument(maxLength); //allego documento che permette di bloccare i caratteri max dei textfields a 2
		comPorts.setText("3"); //COMPORT visualizzata all'avvio
		comPorts.setEditable(true); //il contenuto del textfield è modificabile
		comPorts.setBackground(Color.blue);
		comPorts.setForeground(Color.white);
		comPorts.setBorder(compound);
		comPorts.setFont(fontard);
		
		user.setLocation(181, 330); //posizione nel pannello
		user.setSize(132, 41); //dimensione 
		user.setDocument(maxLengthUser); //allego documento che permette di bloccare i caratteri max dei textfields a 9
		user.setText("DefPlayer"); //Nome giocatore visualizzata all'avvio
		user.setEditable(true); //il contenuto del textfield è modificabile
		user.setBackground(Color.blue);
		user.setForeground(Color.white);
		user.setBorder(compound);
		user.setFont(fontard);
		
		this.add(user);
		this.add(comPorts); //aggiungo i textfields al pannello
	}

	private void initStyles() {
		
		line = new LineBorder(Color.WHITE);
		margin = new EmptyBorder(5, 15, 5, 15);
		compound = new CompoundBorder(line, margin);
		
	}

	private void initToggles() {		
		
		musicIcon = new ImageIcon("src/img/music.png","");
		
		toggleSounds = new JToggleButton();
		toggleSounds.setSize(41,41);
		toggleSounds.setLocation(0, 330);
		
		toggleSounds.setFont(fontmin);
		toggleSounds.setIcon(musicIcon);
		toggleSounds.setBackground(Color.blue);
		toggleSounds.setForeground(Color.white);
		toggleSounds.setBorder(compound);
		
		toggleSounds.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e){
		    	main.setSounds(toggleSounds.isSelected());
		    }		    
		});		
		
		autoIcon = new ImageIcon("src/img/a.png","");
		
		toggleAuto = new JToggleButton();		
		
		toggleAuto.setSize(41,41);
		toggleAuto.setLocation(42, 330);
		
		toggleAuto.setFont(fontmin);
		toggleAuto.setIcon(autoIcon);
		toggleAuto.setBackground(Color.blue);
		toggleAuto.setForeground(Color.white);
		toggleAuto.setBorder(compound);
		
		toggleAuto.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e){
		    	main.setAuto(toggleAuto.isSelected());
		    }		    
		});		
		
		ardIcon = new ImageIcon("src/img/arduino.png","");
		
		toggleArduino = new JToggleButton();		
		
		toggleArduino.setSize(41,41);
		toggleArduino.setLocation(84, 330);
		
		toggleArduino.setFont(fontmin);
		toggleArduino.setIcon(ardIcon);
		toggleArduino.setBackground(Color.blue);
		toggleArduino.setForeground(Color.white);
		toggleArduino.setBorder(compound);
		
		toggleArduino.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e){
		    	main.setArduino(toggleArduino.isSelected());
		    }		    
		});		
		
		add(toggleSounds);
		add(toggleAuto);
		add(toggleArduino);
		
	}

	private void initButtons() {

		exit = new JButton("EXIT");
		start = new JButton("START");	
		info = new JButton ();
		reset = new JButton();
		score = new JButton();
		
		start.addActionListener(new ActionListener() {					
		    public void actionPerformed(ActionEvent e){  
		    	int port = Integer.parseInt(comPorts.getText());
		    	String name = user.getText();
		    	if(port >= 1 && port <= 20 && name != "")
		    		main.startGame(comPorts.getText(), name);	
		    	else		    		
		    		JOptionPane.showMessageDialog(main, "Wrong COM Port number or Username empty");
		    }
		});
		
		startIcon = new ImageIcon("src/img/play.png","START");
		start.setLocation(180,130);
		start.setSize(240,50);
		start.setIcon(startIcon);
		start.setFont(fontmin);
		start.setBackground(Color.blue);
		start.setForeground(Color.white);
		start.setBorder(compound);
		
		exit.addActionListener(new ActionListener() {					
		    public void actionPerformed(ActionEvent e){			    	
		    	System.exit(0);
		    }
		});		
		
		exitIcon = new ImageIcon("src/img/exit.png","EXIT");
		exit.setLocation(180,220);
		exit.setSize(240,50);	
		exit.setIcon(exitIcon);
		exit.setFont(fontmin);
		exit.setBackground(Color.blue);
		exit.setForeground(Color.white);
		exit.setBorder(compound);
		
		info.addActionListener(new ActionListener() {					
		    public void actionPerformed(ActionEvent e){			    	
		    	main.showInfo();
		    }
		});
		
		infoIcon = new ImageIcon("src/img/question.png","");
		info.setLocation(552, 330);
		info.setSize(41,41);
		info.setIcon(infoIcon);
		info.setBackground(Color.blue);
		info.setBorder(compound);	
		
		score.addActionListener(new ActionListener() {					
		    public void actionPerformed(ActionEvent e){			    	
		    	main.showScore();
		    }
		});
		
		scoreIcon = new ImageIcon("src/img/sc.png","");
		score.setLocation(510, 330);
		score.setSize(41,41);
		score.setIcon(scoreIcon);
		score.setBackground(Color.blue);
		score.setBorder(compound);	

		
		reset.addActionListener(new ActionListener() {					
		    public void actionPerformed(ActionEvent e){			    	
		    	main.reset();
		    }
		});
		
		resetIcon = new ImageIcon("src/img/reset.png","");
		reset.setLocation(468, 330);
		reset.setSize(41,41);
		reset.setIcon(resetIcon);
		reset.setBackground(Color.blue);
		reset.setBorder(compound);
	
		add(exit);
		add(start);
		add(info);
		add(score);
		add(reset);
	}
	
	private void initLabels() {
		
		title = new JLabel("Space Invaders", JLabel.CENTER);
		
		title.setLocation(100,20);
		title.setSize(400, 75);	
		
		title.setForeground(Color.red);		
		title.setFont(font);
		
		title.setVisible(true);
		
		add(title);		
	}
	
	public void paintComponent(Graphics g){
		
		setOpaque(false);
		super.paintComponent(g);		
		
		g.drawImage(background, 0, 0, 800, 600, null);
	}
	

}

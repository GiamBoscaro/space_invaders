import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class PausePanel extends JPanel{

	//LA GESTIONE DI QUESTO PANNELLO E' UGUALE A TUTTI GLI ALTRI. PER INFORMAZIONI CONTROLLA I COMMENTI DEGLI ALTRI PANNELLI
	private static final long serialVersionUID = -371387853631326049L;
	
	private ImageIcon resumeIcon, exitIcon, menuIcon, musicIcon;
	private BufferedImage background;
	private JButton exit, resume, menu;
	private JToggleButton toggleSounds;
	private Main main;
	private JLabel title;	
	private Font font = new Font ("Razer Regular", Font.BOLD , 35);
	private Font fontmin = new Font ("Razer Regular", Font.BOLD , 25);
	private Border line, margin, compound;	
	private Action escAct;

	public PausePanel(Main m, File f){
		
		main = m;
		
		try{background = ImageIO.read(f);}catch(Exception ex){}
		
		escAct = new EscAction();
		
		getInputMap().put(KeyStroke.getKeyStroke("ESCAPE") ,"escPressed");
		getActionMap().put( "escPressed", escAct);
		
		initStyles();
		initButtons();
		initLabels();
		initToggles();
		
	}
	
	class EscAction extends AbstractAction{
		
		private static final long serialVersionUID = 5105285857544616683L;

		public void actionPerformed(ActionEvent ev) {
	    	main.resumeGame();
		}			
	}
	
	public void check(){
		
		invalidate();		
		toggleSounds.setSelected(main.isMuted());		
		validate();		
		requestFocusInWindow();
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
		toggleSounds.setLocation(552, 330);
		
		toggleSounds.setFont(fontmin);
		toggleSounds.setIcon(musicIcon);
		toggleSounds.setBackground(Color.blue);
		toggleSounds.setForeground(Color.white);
		toggleSounds.setBorder(compound);		
		
		toggleSounds.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e){
		    	main.setSounds(toggleSounds.isSelected());
				requestFocusInWindow();
		    }		    
		});		
		
		add(toggleSounds);
		
	}

	private void initButtons() {

		exit = new JButton("EXIT");
		resume = new JButton("RESUME");	
		menu = new JButton("MENU");		
		
		resume.addActionListener(new ActionListener() {					
		    public void actionPerformed(ActionEvent e){   	
		    	main.resumeGame();		    	
		    }
		});
		
		resumeIcon = new ImageIcon("src/img/play.png","RESUME");
		resume.setLocation(180,100);
		resume.setSize(240,50);
		resume.setIcon(resumeIcon);
		resume.setFont(fontmin);
		resume.setBackground(Color.blue);
		resume.setForeground(Color.white);
		resume.setBorder(compound);
		
		exit.addActionListener(new ActionListener() {					
		    public void actionPerformed(ActionEvent e){			    	
		    	System.exit(0);
		    }
		});		
		
		exitIcon = new ImageIcon("src/img/exit.png","EXIT");
		exit.setLocation(180,280);
		exit.setSize(240,50);	
		exit.setIcon(exitIcon);
		exit.setFont(fontmin);
		exit.setBackground(Color.blue);
		exit.setForeground(Color.white);
		exit.setBorder(compound);
		
		menu.addActionListener(new ActionListener() {					
		    public void actionPerformed(ActionEvent e){	
		    	main.backgroundMusic();
		    	main.goMenu();
		    }
		});		
		
		menuIcon = new ImageIcon("src/img/menu.png","MENU");
		menu.setLocation(180,190);
		menu.setSize(240,50);	
		menu.setIcon(menuIcon);
		menu.setFont(fontmin);
		menu.setBackground(Color.blue);
		menu.setForeground(Color.white);
		menu.setBorder(compound);

		add(exit);
		add(resume);
		add(menu);
	}
	
	private void initLabels() {
		
		title = new JLabel("PAUSED", JLabel.CENTER);
		
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
		
		g.drawImage(background, 0, 0, null);
	}
	

}

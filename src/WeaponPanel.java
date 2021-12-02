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

public class WeaponPanel extends JPanel{
	
	//LA GESTIONE DI QUESTO PANNELLO E' UGUALE A TUTTI GLI ALTRI. PER INFORMAZIONI CONTROLLA I COMMENTI DEGLI ALTRI PANNELLI
	private static final long serialVersionUID = -156697011868752139L;
	
	private ImageIcon retIcon, backIcon;
	private Thumbnails alien1, alien2, alien3, yellow, green, blue, black, red;
	private BufferedImage background;
	private JButton ret, back;
	private Main main;
	private JLabel title, tutorial, scores;	
	private Font font = new Font ("Razer Regular", Font.BOLD , 20);
	private String textTutorial = "<html>Yellow = 2 dmg <br> Green = 5 dmg <br> Blue = 10 dmg <br> Black = 15 dmg <br> Red = 100 dmg";

	public WeaponPanel(Main m, File f){
		
		main = m;
		
		try{background = ImageIO.read(f);}catch(Exception ex){}
		
		initLabels();		
		initButtons();
		initImages();

	}	
	
	private void initImages() {
		
		alien1 = new Thumbnails(new File("src/img/alien00.png"));
		alien2 = new Thumbnails(new File("src/img/alien10.png"));
		alien3 = new Thumbnails(new File("src/img/alien20.png"));		
		
		alien1.setLocation(235,250);
		alien1.setSize(30,15);
		alien2.setLocation(285,250);
		alien2.setSize(30,15);
		alien3.setLocation(335,250);
		alien3.setSize(30,15);
		
		yellow = new Thumbnails(new File("src/img/laser_yellow_0.png"));
		green = new Thumbnails(new File("src/img/laser_green_0.png"));
		blue = new Thumbnails(new File("src/img/laser_blue_1.png"));
		black = new Thumbnails(new File("src/img/laser_black_0.png"));
		red = new Thumbnails(new File("src/img/laser_red_0.png"));
		
		yellow.setLocation(190,115);
		yellow.setSize(10,15);
		green.setLocation(190,142);
		green.setSize(10,15);
		blue.setLocation(190,165);
		blue.setSize(10,15);
		black.setLocation(190,190);
		black.setSize(10,15);
		red.setLocation(190,215);
		red.setSize(10,15);
		
		add(alien1);
		add(alien2);
		add(alien3);
		
		add(yellow);
		add(green);
		add(blue);
		add(black);
		add(red);
		
	}

	private void initButtons() {
		
		Border line = new LineBorder(Color.WHITE);
		Border margin = new EmptyBorder(5, 15, 5, 15);
		Border compound = new CompoundBorder(line, margin);

		ret = new JButton ();	
		
		retIcon = new ImageIcon("src/img/return.png","");
			
		ret.addActionListener(new ActionListener() {					
		    public void actionPerformed(ActionEvent e){			    	
		    	main.showMenu();
		    }
		});
		
		ret.setLocation(552, 330);
		ret.setSize(41,41);
		ret.setBackground(Color.blue);
		ret.setBorder(compound);
		
		ret.setIcon(retIcon);

		add(ret);
		
		back = new JButton ();	
		
		backIcon = new ImageIcon("src/img/back.png","");
			
		back.addActionListener(new ActionListener() {					
		    public void actionPerformed(ActionEvent e){			    	
		    	main.showInfo();
		    }
		});
		
		back.setLocation(1, 330);
		back.setSize(41,41);
		back.setBackground(Color.blue);
		back.setBorder(compound);		
		back.setIcon(backIcon);

		add(back);

		
	}

	
	private void initLabels() {
		
		scores = new JLabel("",JLabel.CENTER);
		
		scores.setLocation(200,235);
		scores.setSize(200, 100);	
		
		scores.setText("1pt 2pt 3pt");
		scores.setForeground(Color.white);		
		scores.setFont(font);
		scores.setVisible(true);		
		
		tutorial = new JLabel("", JLabel.CENTER);
		
		tutorial.setLocation(100,20);
		tutorial.setSize(400, 300);	
		
		tutorial.setText(textTutorial);		
		tutorial.setForeground(Color.white);		
		tutorial.setFont(font);
		
		tutorial.setVisible(true);
		
		Font font = new Font ("Razer Regular", Font.BOLD , 35);
		
		title = new JLabel("Weapons & Points", JLabel.CENTER);
			
		title.setLocation(100,20);
		title.setSize(400, 75);	
		
		title.setForeground(Color.red);		
		title.setFont(font);
		
		title.setVisible(true);
		
		add(title);		
		add(tutorial);
		add(scores);
	}
	
	public void paintComponent(Graphics g){
		
		setOpaque(false);
		super.paintComponent(g);		
		
		g.drawImage(background, 0, 0, 800, 600, null);
	}
	

}

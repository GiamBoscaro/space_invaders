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


public class ScorePanel extends JPanel{
	
	//LA GESTIONE DI QUESTO PANNELLO E' UGUALE A TUTTI GLI ALTRI. PER INFORMAZIONI CONTROLLA I COMMENTI DEGLI ALTRI PANNELLI
	
	private static final long serialVersionUID = -6143093717159555961L;
	
	String score;
	private ImageIcon retIcon;
	private BufferedImage background;
	private JButton ret;
	private Main main;
	private JLabel title, scoreboard;	
	private Font font = new Font ("Razer Regular", Font.BOLD , 18);

	public ScorePanel(Main m, File f, String scr){
		
		main = m;
		score = scr;
		
		try{background = ImageIO.read(f);}catch(Exception ex){}

		initLabels();		
		initButtons();
	}	
	
	public void update(String str){
		
		score = str;		
		invalidate();
		remove(scoreboard);
		remove(title);
		initLabels();
		validate();		
	}
	
	private String getScoreText(){

		int max = 10;
		String str = "<html>Pos. - Name - Score - Date<br><br>";
		String[] v = score.split("%");		
		String[] temp = new String[3];
		
		if(v.length < 10){
			if(v.length != 1)
				max = v.length;
			else if(v.length == 1 && v[0].length()>0)
				max = 1;
			else			
				max = 0;
		}
		
		for(int i = 0; i < max; i++){
			temp = v[i].split(",");
			str = str + (i+1) + "° - " + temp[0] + " - " + temp[1] + " - " + temp[2] + "<br>";
		}
		
		if(str == "<html>Pos. - Name - Score - Date<br><br>")
			str = "None";
		
		return str;
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
		
	}
	
	private void initLabels() {
		
		scoreboard = new JLabel("",JLabel.CENTER);
		
		scoreboard.setLocation(50,50);
		scoreboard.setSize(500, 300);	
		
		scoreboard.setText(getScoreText());
		scoreboard.setForeground(Color.white);		
		scoreboard.setFont(font);
		scoreboard.setVisible(true);	
		
		Font font = new Font ("Razer Regular", Font.BOLD , 35);
		
		title = new JLabel("Scores", JLabel.CENTER);
			
		title.setLocation(100,0);
		title.setSize(400, 75);	
		
		title.setForeground(Color.red);		
		title.setFont(font);
		
		title.setVisible(true);
		
		add(title);	
		add(scoreboard);
	}
	
	public void paintComponent(Graphics g){
		
		setOpaque(false);
		super.paintComponent(g);		
		
		g.drawImage(background, 0, 0, 800, 600, null);
	}
	

}

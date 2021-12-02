import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;


public class Alien {
	
	public int init_x;	//coordinate iniziali dell'alieno
	public int init_y;	
	
	public int x;	//coordinate attuali
	public int y;
	
	public int dim_x;	//dimensioni 
	public int dim_y;
	
	public int number;	//numero dell'alieno (per gestione immagini)
	
	public int score;	//punteggio che restituisce
	
	private int life;	//vita attuale
	
	public Color color;	//colore associato	(*DEBUG*)
	public BufferedImage img;	//immagine utilizzata
	public String snd_path;	//percorso per il suono associato
	
	private boolean dead;	//flag per indicare la morte e l'avvenuta esplosione
	private boolean exploded;
	private int dur;	//durate dell'esplosione in game
	
	public Alien(int init_x, int init_y, int dim_x, int dim_y, int life, int score, Color color, String img, String snd_path, int number){
		
		//costruttore con assegnazione delle variabili
		this.number = number;
		this.init_x = init_x;
		this.init_y = init_y;
		x = init_x;
		y = init_y;
		this.score = score;
		dead = false;
		this.dim_x = dim_x;
		this.dim_y = dim_y;
		this.life = life;
		this.color = color;
		this.snd_path = snd_path;
		try{ this.img = ImageIO.read(new File(img)); }catch(Exception ex){}
		exploded = true;
		dur = 0;
	}
	
	public void shotted(int dmg){	//metodo per quando l'alieno viene colpito
		life -= dmg;	//sottraggo dalla vita e se è morto indico la morte ma non l'avvenuta esplosione
		dead = life <= 0;
		if(dead)
			exploded = false;
		
	}
	
	public boolean isDead(){	//check della morte
		return dead;
	}
	
	public boolean isExploded(){	//check dell'esplosione
		return exploded;
	}
	
	public void setExplosion(){	//setter dell'esplosione
		dur++;
		exploded = dur>=10;	//l'esplosione è visualizzata per 10 cicli di gioco, circa mezzo secondo
	}
	
	public void setImage(String img){	//cambio dell'immagine (per animazioni)
		try{ this.img = ImageIO.read(new File(img)); }catch(Exception ex){}
	}

}

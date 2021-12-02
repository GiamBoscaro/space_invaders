import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Thumbnails extends JPanel{	//semplicemente un pannelo con un immagine di background associata

	private static final long serialVersionUID = 1555720904018893868L;
	
	BufferedImage img;
	  int width;
	  int height;
	
	  public Thumbnails(File f){
	    super(true);
	    try{
	      setImage(ImageIO.read(f));	//assegnazione dell'immagine
	    }catch(Exception e) {}
	  }
	
	  public void setImage(BufferedImage img){
	    this.img = img;
	    width = img.getWidth();
	    height = img.getHeight();
	    repaint();
	  }
	  
	  public void paintComponent(Graphics g){	//disegno l'immagine nello sfondo
		setOpaque(false);
	    super.paintComponent(g);	    
	    g.drawImage(img, 0, 0, null);
	  }		
}

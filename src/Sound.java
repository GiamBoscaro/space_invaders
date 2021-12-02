import java.io.File;
import javax.sound.sampled.*;

public class Sound {
	
	public Clip activeClip = null; //suono attivo
	boolean muted = false;	//flag del mute
	public String lastPath = "";	//ultimo suono eseguito
	private int nloop = 1;	//numero di ripetizioni del suono
	
	public boolean isPlaying(){	//check se sto riproducendo un suono
		
		return activeClip != null;
	}
	
	public void stop(){	//stop del suono in riproduzione

		if(isPlaying())
			activeClip.stop();	
	}
	
	public void setLoop(int n){	//setter per il numero di loop
		
		nloop = n;
	}
	
	
	public void mute(){	//comando di mute
		
		muted = true;
		stop();		
		
	}
	
	public void unmute(){	//comando di unmute
		
		muted = false;
		
	}

    public void PlaySound(String path) {	//esecuzione del suono
    	
    	if(!muted){	//se non è mutato
	        try {
	        	lastPath = path;	//salvo il percorso
	            final File SoundFile = new File(path);	//importazione del file di suono
	            
	            //codice standard per l'esecuzione
	            AudioInputStream Sound = AudioSystem.getAudioInputStream(SoundFile);	
	            DataLine.Info info = new DataLine.Info(Clip.class, Sound.getFormat());
	            Clip clip = (Clip) AudioSystem.getLine(info);
	            clip.loop(nloop);
	            clip.open(Sound);
	            activeClip = clip;
	
	            clip.addLineListener(new LineListener() {
	                public void update (LineEvent event) {
	                    if (event.getType() == LineEvent.Type.STOP) {
	                        event.getLine().close();
	                    }
	                }
	            });
	            clip.start();
	        } catch (Exception e) {}
	    }

    }
	
}
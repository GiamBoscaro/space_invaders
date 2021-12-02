import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class SpacePanel extends JPanel implements Runnable {

	private static final long serialVersionUID = -2314529260788492470L; //aggiunto da eclipse automaticamente
	
	private final Font font = new Font ("Razer Regular", Font.BOLD , 20); //font utilizzato
	private final String invaderKill_path = "src/sound/invaderkilled.wav", //percorsi relativi dei suoni
						 death_path = "src/sound/explosion.wav", 
						 shot_path = "src/sound/shoot.wav", 	
						 menubkg_path = "src/sound/menu.wav", 
						 win_path = "src/sound/win.wav",
						 barr_path = "src/sound/barrier.wav",
						 ingame_path = "src/sound/game.wav";
	
	private int right = 5, left = -5; //distanza di un movimento della navicella in pixel
	private final long powerUpDuration = 4000, powerUpSleep = 15000, alienTic = 1000; //durata in ms dei powerUp, del suo cooldown e dei Tic nel movimento degli alieni
	private final Score scores = new Score(); //gestore delle highscores
	private final Sound sounds = new Sound(), music = new Sound(); //gestore degli effetti sonori e delle musiche
	
	//variabili primitive
	private int player1X, player1Y, shootX, shootY, laserX, laserY, alienShot1X, alienShot2X, alienShot1Y, alienShot2Y, //coordinate del giocatore, del cannone, del laser sparato da giocatore e alieni
				damage, score, deadCounter, nAlien, anim_aliens, anim_laser, anim_barrier, record, serialRead ;	//danno, punteggio, numero di alieni morti e numero di alieni totali, record ed altri valori utili per visualizzare le animazioni
	
	private boolean game, auto, arduino, shotready, playerFlagSx, playerFlagDx, activedPowerUP, powerUpReady, hasChanged, derLeft, shot1Gone, shot2Gone, scoreAdded;	//valori per conoscere lo stato del giocatore e degli alieni e gestire le loro azioni	

	private long starTime, nowTime, powerUpSpawnTime, powerUpVanish, timeToMove; //variabili utilizzate per ricordare in che orario è stata compiuta un azione. In questo modo si può gestire durata e ritardi di alcuni eventi
	
	private String port, playerName;
	
	//oggetti
	private Alien[] aliensList; //array contenente gli alieni
	private AlienCreator creator; //oggetto che prepara il campo di gioco
	private Alien last; //ultimo alieno vivo in campo (il più vicino al giocatore)
	
	private Color laserColor, lastColor;//colore del laser attuale e ultimo usato (utile quando viene attivato il powerUp)
	private BufferedImage background, player, explosion; //immagini di sfondo, del giocatore e delle esplosioni
	private BufferedImage[] laser_yellow_anim, laser_red_anim, laser_green_anim, laser_blue_anim, laser_black_anim, barrier_anim, actual_laser; //array di immagini che servono per creare le animazioni
	
	private Thread thread; //thread principale del gioco		
	private Action leftAct, rightAct, leftActRel, rightActRel, rAct, fAct, spaceAct, escAct; //oggetti azione, indicano cosa accade alla pressione o rilascio di un tasto	
	
	private Main manager; //contiene il riferimento alla classe main che gestisce l'intero programma
	private Serial Serial;
	
	public SpacePanel(){	
		
		initialize(); //il costruttore inizializza il pannello di gioco		
	}	
	
	public String getScoreList(){
		
		String str = "";
		
		try {
			
			str = scores.getScores();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}	
		return str;
	}

	public void setSound(boolean s){ //gestione dei suoni e della musica in base al togglebutton del suono nel menu
		
		music.stop(); //effetti sonori e musiche sono fermati
		sounds.stop();
		
		if(!s){	
			sounds.mute(); //se il pulsante è attivato, l'audio viene mutato
			music.mute();
		}
		else{
			sounds.unmute(); //altrimenti l'audio è riattivato e ripartono le musiche in base al contesto (menu o in gioco)
			music.unmute();
			if(game)
				music.PlaySound(ingame_path);
			else
				music.PlaySound(menubkg_path);
		}
		
	}
	
	public void resetScore(){	//reset dei record
		
		record = 0;
		scores.reset();

		JOptionPane.showMessageDialog(this, "Score Database Successfully Cleared");
	}
	
	public void inGameMusic(){	//attivazione della musica in gioco
		
		music.stop();
		music.PlaySound(ingame_path);
	}
	
	public void backgroundMusic(){		//attivazione della musica del menu
		
		music.stop();
		music.PlaySound(menubkg_path);
	}
	
	public void setManager(Main m){	//passaggio del gestore Main all'interno del pannello
		
		manager = m;
	}
	
	public void ready() {	//il gioco viene fatto partire

		if(arduino)
			initSerial();
		game = true;		
		requestFocusInWindow(); //richiesta del focus sul pannello del gioco, in questo modo si attivano i keybinding a lui abbinati		
		initTiming();	//salvo i riferimenti temporali		
		thread = new Thread(this);			
		thread.start();	
		
	}
	
	@SuppressWarnings("deprecation")
	public void stop() {	//stop del thread nel caso di ritorno al menu
		if(arduino)
			Serial.close();
		initialize(); //reset dell'area di gioco		
		game = false;		
		thread.stop();		
	}
	
	@SuppressWarnings("deprecation")
	public void pause(){	//pausa del gioco
		
		thread.suspend();	//sospensione del processo
		manager.pauseGame();	//visualizzazione del menu di pausa
	}
	
	@SuppressWarnings("deprecation")
	public void resume(){	//ripresa del gioco 
		
		requestFocusInWindow();		//richiesta del focus
		thread.resume();	//ripresa del processo
	}	
	
	public void setAuto(boolean auto) {	//setter per l'autoshot
		
		this.auto = auto;		
	}
	
	public void setArduino(boolean arduino) {
		
		this.arduino = arduino;
	}
	
	public void setComPort(String comPort) {
		
		port = comPort;		
	}
	
	public void setUser(String user) {
		
		playerName = user;		
	}
	
	private void initTiming(){		//inizializzazione dei riferimenti temporali
		
		timeToMove = nowTime = starTime = powerUpSpawnTime = System.currentTimeMillis();	
		powerUpVanish = nowTime - powerUpSleep;
	}
	
	private void initNum(){		//inizializzazione delle coordinate ed altri valori numerici
		
		nAlien = 30;	//numero massimo alieni
		player1X=100;	//coordinate giocatore
		player1Y = 340;
		shootX = 115;	//coordinate cannone
		shootY = 342;
		damage = 2;	//danno dell'arma
		anim_aliens = 1;	//utilizzato per le animazioni
		anim_laser = anim_barrier = record = deadCounter = score = laserX = laserY = 0;	//altre variabili per le animazioni, punteggio o coordinate
		
	}
	private void initString(){
		
			port = "3";			
	}
	private void initBool(){	//inizializzazione dei flag
		
		activedPowerUP = hasChanged = derLeft = shot1Gone = shot2Gone = scoreAdded = false; 
		powerUpReady = shotready = true;
	}
	
	private void initGraph(){	//inizializzazione delle variabili di riferimento per la grafica
		
		laserColor = Color.yellow;	//colore del laser attuale e ultimo utilizzato
		lastColor = Color.yellow;
		
		try{background = ImageIO.read(new File("src/img/bkg.jpg"));}catch(Exception e){}	//try catch per inserimento di immagini di sfondo e giocatore
		try{player = ImageIO.read(new File("src/img/ship.png"));}catch(Exception e){}		
		
	}
	
	private void initAnims() {	//inizializzazione delle strutture necessarie per le animazioni
		
		laser_yellow_anim = new BufferedImage[3];	//array di immagini per le animazioni dei laser e della barriera
		laser_red_anim = new BufferedImage[3];
		laser_green_anim = new BufferedImage[3];
		laser_blue_anim = new BufferedImage[3];
		laser_black_anim = new BufferedImage[3];
		barrier_anim = new BufferedImage[3];
		
		for(int i = 0; i < laser_yellow_anim.length; i++)	//inserimento delle immagini nell'array
			try{laser_yellow_anim[i] = ImageIO.read(new File("src/img/laser_yellow_"+i+".png"));}catch(Exception e){}
		for(int i = 0; i < laser_red_anim.length; i++)
			try{laser_red_anim[i] = ImageIO.read(new File("src/img/laser_red_"+i+".png"));}catch(Exception e){}
		for(int i = 0; i < laser_green_anim.length; i++)
			try{laser_green_anim[i] = ImageIO.read(new File("src/img/laser_green_"+i+".png"));}catch(Exception e){}
		for(int i = 0; i < laser_blue_anim.length; i++)
			try{laser_blue_anim[i] = ImageIO.read(new File("src/img/laser_blue_"+i+".png"));}catch(Exception e){}
		for(int i = 0; i < laser_black_anim.length; i++)
			try{laser_black_anim[i] = ImageIO.read(new File("src/img/laser_black_"+i+".png"));}catch(Exception e){}
		for(int i = 0; i < barrier_anim.length; i++)
			try{barrier_anim[i] = ImageIO.read(new File("src/img/barrier"+i+".png"));}catch(Exception e){}		
		actual_laser = laser_yellow_anim;
		
	}
	
	private void initAliens(){
		
		creator = new AlienCreator(nAlien, 600);	//creazione degli alieni
		aliensList = creator.createArray();		//passaggio degli alieni sottoforma di array di elementi
		last = aliensList[nAlien-1];	//alieno più vicino al giocatore
		
		alienShot1X = aliensList[1].x;	//inizializzazione delle coordinate dei laser degli alieni
		alienShot2X = aliensList[2].y;
		alienShot1Y = aliensList[1].x;
		alienShot2Y = aliensList[2].y;
	}
	
	private void initScore(){
		
		try {
			
			record = scores.getRecord(); //prendo il punteggio più alto
		} catch (SQLException e) {
			
			e.printStackTrace();
		}	
	}	
	
	private void initBinds(){
		
		leftAct = new LeftAction();	//oggetti azioni per ogni azione da compiere con la tastiera
		rightAct = new RightAction();
		leftActRel = new LeftActionRel();
		rightActRel = new RightActionRel();
		escAct = new EscAction();
		rAct = new RAction();
		fAct = new FAction();
		spaceAct = new SpaceAction();
		
		getInputMap().put(KeyStroke.getKeyStroke("LEFT") ,"leftPressed");	//alla key associo un evento col nome personalizzato
		getActionMap().put( "leftPressed", leftAct);	//all'evento associo un azione
		getInputMap().put(KeyStroke.getKeyStroke("A") ,"APressed");
		getActionMap().put( "APressed", leftAct);
		getInputMap().put(KeyStroke.getKeyStroke("RIGHT") ,"rightPressed");
		getActionMap().put( "rightPressed", rightAct);
		getInputMap().put(KeyStroke.getKeyStroke("D") ,"DPressed");
		getActionMap().put( "DPressed", rightAct);
		
		getInputMap().put(KeyStroke.getKeyStroke("ESCAPE") ,"escPressed");
		getActionMap().put( "escPressed", escAct);
		getInputMap().put(KeyStroke.getKeyStroke("R") ,"rPressed");
		getActionMap().put( "rPressed", rAct);
		getInputMap().put(KeyStroke.getKeyStroke("F") ,"fPressed");
		getActionMap().put( "fPressed", fAct);
		getInputMap().put(KeyStroke.getKeyStroke("SPACE") ,"spacePressed");
		getActionMap().put( "spacePressed", spaceAct);
		
		getInputMap().put(KeyStroke.getKeyStroke("released LEFT") ,"leftReleased");
		getActionMap().put( "leftReleased", leftActRel);
		getInputMap().put(KeyStroke.getKeyStroke("released RIGHT") ,"rightReleased");
		getActionMap().put( "rightReleased", rightActRel);
		getInputMap().put(KeyStroke.getKeyStroke("released A") ,"AReleased");
		getActionMap().put( "AReleased", leftActRel);
		getInputMap().put(KeyStroke.getKeyStroke("released D") ,"DReleased");
		getActionMap().put( "DReleased", rightActRel);		
		
	}	
	
	private void initialize(){	//inizializzazione dell'area di gioco

		//inizializzazione dei vari componenti
		initNum();		
		initString();
		initBool();		
		initGraph();
		initAnims();	
		initAliens();
		initScore();
		initBinds();		
	}	
	
	private void initSerial() {
		
		Serial = new Serial(port, this);
	}
	
	//CLASSI AZIONE PER OGNI KEY 
	
	class LeftAction extends AbstractAction{
			
			private static final long serialVersionUID = 5105285857544616683L;  //aggiunto da eclipse automaticamente

			public void actionPerformed(ActionEvent ev) {
				playerFlagSx = true;	//il giocatore va verso sinistra
	        	playerFlagDx = false;
			}
	}

	 class RightAction extends AbstractAction{
			
			private static final long serialVersionUID = 5105285857544616683L;  //aggiunto da eclipse automaticamente

			public void actionPerformed(ActionEvent ev) {
				playerFlagSx = false;
	        	playerFlagDx = true;	//il giocatore va verso destra
			}
	}
	 
	 class LeftActionRel extends AbstractAction{
			
			private static final long serialVersionUID = 5105285857544616683L;  //aggiunto da eclipse automaticamente

			public void actionPerformed(ActionEvent ev) {
				playerFlagSx = false;
	        	playerFlagDx = false;	//giocatore fermo
			}
	}
	 
	 class RightActionRel extends AbstractAction{
			
			private static final long serialVersionUID = 5105285857544616683L;  //aggiunto da eclipse automaticamente

			public void actionPerformed(ActionEvent ev) {
				playerFlagSx = false;
	        	playerFlagDx = false; //giocatore fermo
			}
	}
	 
	 class EscAction extends AbstractAction{
			
			private static final long serialVersionUID = 5105285857544616683L;  //aggiunto da eclipse automaticamente

			public void actionPerformed(ActionEvent ev) {
				
				pause();	//gioco in pausa
			}			
	}
	 class FAction extends AbstractAction{
			
			private static final long serialVersionUID = 5105285857544616683L;  //aggiunto da eclipse automaticamente

			public void actionPerformed(ActionEvent ev) {
				
				if(powerUpReady && !activedPowerUP){	//se è pronto il powerup e non è in uso
					sounds.PlaySound(barr_path);	
					powerUpSpawnTime = System.currentTimeMillis();	//salvataggio dell'istante in cui viene attivato
			    	activedPowerUP = true;
			    	damage = 100;	//danno del laser aumentato
			    	lastColor = laserColor;	//salvataggio dell'ultimo laser usato
			    	laserColor = Color.red;	//cambio del colore del laser
			    	actual_laser = laser_red_anim;	//cambio delle animazioni
			    	anim_barrier = 0; //reset della variabile per l'animazione della barriera (per evitare overflow a lungo andare)
		    	}
			}
	}
	 class RAction extends AbstractAction{
			
			private static final long serialVersionUID = 5105285857544616683L;  //aggiunto da eclipse automaticamente

			public void actionPerformed(ActionEvent ev) {
				
				if(arduino)
					Serial.close();
				if(!game){	//se non è in gioco
	    			try{
	    				Thread.sleep(50);	//sospendo il thread
	    			}catch(Exception ex){}
	    			initialize();	//per dare il tempo di inizializzare di nuovo e resettare il gioco
	    			ready();	    //ripresa del processo
	    		}
			}
	}
	 
	 class SpaceAction extends AbstractAction{
			
			private static final long serialVersionUID = 5105285857544616683L;  //aggiunto da eclipse automaticamente

			public void actionPerformed(ActionEvent ev) {
				if(!auto)	//se non c'è l'autoshot, spara
					shot();
			}
	}	

	public void paintComponent(Graphics gc){
		
		setOpaque(false); //tutti i componenti utilizzano la trasparenza se disponibili (es immagini png)
		super.paintComponent(gc);
		
		gc.setFont(font); //setto il font
		
		gc.drawImage(background,0,0,null);	//applicazione del background
		
		
		for(int i = 0; i < aliensList.length; i++){ //controllo gli alieni
			if(!aliensList[i].isDead())//se non è morto
				gc.drawImage(aliensList[i].img, aliensList[i].x,aliensList[i].y ,aliensList[i].dim_x , aliensList[i].dim_y, null);			
			if(!aliensList[i].isExploded()){ //se è morto e non ancora esploso visualizza l'animazione di esplosione
				try{explosion = ImageIO.read(new File("src/img/explosion" + aliensList[i].number +".png"));}catch(Exception e){}	
				gc.drawImage(explosion, aliensList[i].x - 17,aliensList[i].y - 10 ,75,75 , null); 
				aliensList[i].setExplosion(); //segnalo che è esploso e non deve essere più disegnato
			}
		}		
		
		gc.drawImage(actual_laser[anim_laser%3], laserX,laserY ,10 , 15, null);	//disegno delle animazioni del laser del giocatore e degli alieni
		gc.drawImage(laser_yellow_anim[anim_laser%3], alienShot1X,alienShot1Y ,10 , 15, null);	//la variabile di supporto per l'animazione da sempre un numero compreso tra 0 e 2 
		gc.drawImage(laser_yellow_anim[anim_laser%3], alienShot2X,alienShot2Y ,10 , 15, null);	//cosi l'array scorre e ricomincia autonomamente ed ogni ciclo l'immagine cambia producendo
		anim_laser ++;		//incremento della variabile per le animazione						//l'animazione
    	gc.setColor(laserColor);	//colore del cannone laser dell'astronave
		gc.fillRect(shootX,shootY,10, 28); //disegno un rettangolo
		gc.drawImage(player, player1X,player1Y ,40 , 30, null);	//disegno sopra l'astronave del giocatore. Il colore del cannone si vede in trasparenza solo al centro
		
	
		if(activedPowerUP){	//se c'è il powerUp
			gc.drawImage(barrier_anim[anim_barrier%3], player1X - 17,shootY - 10 ,75 , 75, null);	//gestione delle animazioni della barriera
			anim_barrier++;
		}
		
		gc.setColor(Color.white);	//colore del font
		if(score > record)
			gc.drawString("Record: "+score, 10, 15);	//se il punteggio supera il record, il punteggio attuale è anche il record
		else
			gc.drawString("Record: "+record, 10, 15);	//senno è disegnato il record
		gc.drawString("Score: "+score, 250, 15);	
		gc.drawString("Time: "+(nowTime - starTime) / 1000, 475, 15);	//visualizzazione della stringa del tempo e dei punteggi
		if(powerUpReady && !activedPowerUP)	//se il powerup è pronto e non utilizzato viene visualizzata una stringa di notifica
			gc.drawString("PowerUP Ready", 210, 35);

		if(!game){	//se il gioco è finito
			gc.setColor(Color.red);
			if(deadCounter >= nAlien){	//se sono stati eliminati tutti gli alieni l'utente ha vinto
				gc.drawString("You Win", 240, 180);
				sounds.PlaySound(win_path);
			}else{
				gc.drawString("Game Over", 240, 180);
				int random = (int)(Math.random() * 3);
				try{explosion = ImageIO.read(new File("src/img/explosion" + random +".png"));}catch(Exception e){}	
				gc.drawImage(explosion, player1X - 17,player1Y - 10 ,75 , 75, null);
				sounds.PlaySound(death_path);	//altrimenti il giocatore muore e viene attivato l'effetto sonoro di morte
			}			
			if(!scoreAdded){
				try {
					scores.addScore(score,playerName);//salvo il record	
					scoreAdded = true;
					record = scores.getRecord();				
				} catch (SQLException e) {	
					e.printStackTrace();
				}	
			}
			
			gc.drawString("Press R to restart", 170, 210);	//notifico come è possibile ricominciare la partita
		}		
	}
 
    private void shot(){	//gestione del cannone laser 
    	
    	if((shotready && !auto) || auto){ //se c'è l'autoshot o se non c'è ma il laser è pronto
    		shotready = false;	//il laser non è più pronto
	    	sounds.PlaySound(shot_path);	//riproduzione del suono di sparo
	    	laserX = shootX;	//coordinate del laser che esce dal cannone
	    	laserY = shootY;    	
    	}
    }
    
    private void alienShot(Alien a){	//gestione del laser degli alieni
    	
    	if((int)(Math.random()*30) +1 == 5){	//l'alieno è scelto casualmente con probabilita di 1/6
    		
    		if(shot1Gone){    	//se un laser è pronto
    			shot1Gone = false;	//laser non più disponibili
	    		alienShot1X = a.x;	//coordinate iniziali del laser
	    		alienShot1Y = a.y;   
    		}else if(shot2Gone){
    			shot2Gone = false;	//idem vedi sopra
	    		alienShot2X = a.x;
	    		alienShot2Y = a.y;  
    		}
    	}    	
    }
    
    private void powerUp(){	//gestione del powerup
    	
    	if(!activedPowerUP){	//se non è attivo il potenziamento
    	
	    	if(score >=15){	//in base al punteggio vi è un cambiamento di colore del laser e quindi delle animazioni e anche un aumento di danno
	    		
	    		damage = 15; 
	    		laserColor = Color.black;
	    		actual_laser = laser_black_anim;

	    	}else if(score >=10){
	    		
	    		damage = 10; 
	    		laserColor = Color.blue;
	    		actual_laser = laser_blue_anim;

	    	}else if(score >=5){
	    		
	    		damage = 5;
	    		laserColor = Color.green;
	    		actual_laser = laser_green_anim;

	    	}    	
    	}

    }
    
    private void moveAliens(){	//movimento della griglia di alieni
    	
    	if(nowTime - timeToMove >= alienTic){	//se è passato 1 secondo dall'ultimo movmento
    		timeToMove = System.currentTimeMillis();
    	
	    	if(aliensList[aliensList.length-1].x >= this.getWidth() - 40){	//se la griglia è quasi fuori dai bordi
	    		derLeft = true; //cambio di direzione del movimento
	    		hasChanged = true;    //segnale che la direzione è cambiata
	    	}
	    	else if(aliensList[0].x <= 10){
	    		derLeft = false; 	//idem vedi sopra
	    		hasChanged = true;  
	    	}
	    	
	    	for(int i = 0; i < aliensList.length; i++){
	    		
	    		aliensList[i].setImage("src/img/alien"+ aliensList[i].number + anim_aliens%2 +".png");	//cambio delle immagini degli alieni per creare un animazione
	    		
	    		if(derLeft)
	    			aliensList[i].x-=10;	//cambio di coordinate orizzontali in base alla direzione dello spostamento
	    		else
	    			aliensList[i].x+=10;
	    		if(hasChanged)
	    			aliensList[i].y += 15; //cambio di coordinate verticali se ho raggiunto il bordo
	    			
	    	}
	    	
	    	anim_aliens++;	//incremento della variabile di supporto per le animazioni degli alieni
	    	
	    	hasChanged = false; 
    	}
    }
    
    private void lasers(){
    	
    	if(shotready)	//se il colpo è pronto
    		laserX = laserY = -10;	//il laser non deve esserci su schermo, viene posizionato fuori dall'area visibili
    	
    	if((!shotready && !auto) || auto)	//se il laser è stato sparato
    		laserY-=5;	//movimento in verticale del laser
    	
   		alienShot1Y +=5;	//movimento verticale dei laser degli alieni ma nella direzione contraria
		alienShot2Y +=5;
		
    	if(laserY <= 5){	//se ho raggiunto il limite superiore della finestra
    		
    		if(auto){  //se c'è l'autoshot
    			anim_laser = 0; //reset delle animazioni
	    		shot();	//risparo il laser
    		}
    		else	//altrimenti segnalo che è pronto a sparare
    			shotready = true;
    	}    	

		shot1Gone = alienShot1Y >= this.getHeight();	//i laser degli alieni saranno di nuovo disponibili quando usciranno dallo schermo
		shot2Gone = alienShot2Y >= this.getHeight();   	
    	
    }
    
    private void powerEnd(){	//controllo se il power up è finito
    	
    	if((nowTime - powerUpSpawnTime >= powerUpDuration) && activedPowerUP){ //se è passato il giusto tempo
    		powerUpVanish = System.currentTimeMillis();	//salvo quando il potenziamento è scaduto
    		activedPowerUP = false;	 //il powerUp non è attivo
    		damage = 2;	 //il danno torna normale
    		laserColor = lastColor;	 //il laser e le animazioni tornano normali
    		actual_laser = laser_yellow_anim;
    	}
    }
    
    private void checkAliens(){    	
    	
    	for(int i = 0; i < aliensList.length; i++){ //accesso sequanziale all'array di alieni
    		boolean temp = aliensList[i].isDead();	//controllo se l'alieno è morto
    		if(!temp)	//se non è, provo a fargli sparare
    			alienShot(aliensList[i]);	//se un laser raggiungie l'alieno (laser del giocatore colpisce la HITBOX)
    		if(!temp && (laserX >= (aliensList[i].x - aliensList[i].dim_x /2) && laserX <= (aliensList[i].x + aliensList[i].dim_x)) && (laserY >= (aliensList[i].y - aliensList[i].dim_y /2) && laserY <= (aliensList[i].y + aliensList[i].dim_y))){
    			aliensList[i].shotted(damage);  // alieno colpito con un certo danno
    			temp = aliensList[i].isDead();	//ricontrollo ora se è morto
    			if(temp){    		//se è morto		
    				sounds.PlaySound(invaderKill_path);	//riproduzione di un suono
    				score += aliensList[i].score;	//aumento del punteggio
    				deadCounter++;	//segnalo che c'è un alieno morto in più
    			}
    			if(auto)
    				shot(); //se c'è l'autoshot risparo
    			else
    				shotready = true; //senno segnalo che il cannone è pronto
    		}
    		if(!temp)
    			last = aliensList[i];  //aggiorno l'ultimo alieno vivo della griglia
    	}
    }
    
    private void controls(){	//controlli generali del gioco
    	
    	nowTime = System.currentTimeMillis();	//salvataggio dell'istante attuale
    	powerUpReady = nowTime - powerUpVanish >= powerUpSleep;	//controllo se il powerUp è pronto
    	
    	lasers();
    	powerEnd();
    	checkAliens();
    	
    	//il gioco dura finchè tutti gli alieni sono morti, o finchè non si viene colpiti dal laser o dall'alieno stesso che raggiungie terra
    	game = (deadCounter < nAlien) && (last.y <= 315) && !((alienShot2X >= player1X - 5 && alienShot2X <= player1X + 35 && alienShot2Y >= player1Y && alienShot2Y <= player1Y + 35) || (alienShot1X >= player1X - 5 && alienShot1X <= player1X + 35 && alienShot1Y >= player1Y && alienShot1Y <= player1Y + 35));
    	
		powerUp();
		moveAliens();
    }

	private void move(){
		
        if (playerFlagSx && player1X >= 5){	//movimento del giocatore e del cannone in base ai flag modificati dai key bindings
            player1X += left; 
            shootX += left;
        }
        
        if (playerFlagDx && player1X <= this.getWidth()-40){
            player1X += right; 
            shootX += right;
        }
    }
	
	private void write(){ //funzione per l'invio di dati ad Arduino
		
		if(arduino)
			Serial.send(score);	//invio punteggio attuale ad Arduino		
	}
	
	public void read(String data){ //funzione per la lettura dei dati di Arduino. E' richiamata da Serial
		
		serialRead = Integer.parseInt(data);
		if(serialRead == 255) //se il valore è 255 allora il pulsante è stato premuto
			shot();	
		else
			gyro(); //nell'altro caso il dato rilevato indica la pendenza del giroscopio
	}
	
	private void gyro(){

		if(serialRead < 0){ //se la lettura è negativa
			
			playerFlagSx = true; //mi sposto a sinistra
			playerFlagDx = false;	
			left = serialRead;
			
		}else if(serialRead > 0){ //se la lettura è positiva
			
			playerFlagSx = false;	 //mi sposto a destra
			playerFlagDx = true;	
			right = serialRead;
		}
		else
			playerFlagSx = playerFlagDx = false; //se il valore è 0 sto fermo
	}	

	public void run() { //loop di gioco

		while(game){		//finchè sono in gioco

			controls();	//controlli del gioco
			
			try{
				Thread.sleep(35);
			}catch(InterruptedException ex){}
			
			move();	//muovo il giocatore	
			repaint(); //ridisegno la grafica
			write(); //controllo le operiazioni con arduino
			
		}
	}
}
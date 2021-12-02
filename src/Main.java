import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Main extends JFrame {

	private static final long serialVersionUID = 4307053931164271156L; // aggiunto da eclipse automaticamente

	private static final String version = "v. 1.9.0.2"; // versione del gioco

	private SpacePanel panel; // pannello del gioco
	private MenuPanel menu; // pannello del menu
	private HelpPanel tutorial; // panello del tutorial pulsanti
	private PausePanel pause; // panello del menu di pausa
	private WeaponPanel weapon; // panello del tutorial armi e alieni
	private ScorePanel score; // panello della scoreboard
	private boolean mute, auto, arduino; // flag di muto, arduino e autoshot
	private String comPort, user; // nome utente e porta utilizzata
	protected CardLayout layout; // layout del frame

	public Main() {

		super();

		initialize(); // inizializzazione del frame
	}

	private void initialize() {

		this.setResizable(false); // non � possibile modificare le dimensioni del frame
		this.setBounds(new Rectangle(600, 300, 600, 400)); // il frame � rettangolare posizionato nelle coordinate
															// (600,300) dello schermo e di dimensioni 600x400
		this.setTitle("Space Invaders " + version); // titolo dell'applicazione

		mute = auto = arduino = false; // inizializzazione dei flag
		comPort = "3";

		Container pane = getContentPane(); // contenitore di tutti gli oggetti del frame
		layout = new CardLayout();
		pane.setLayout(layout); // setting del layout

		pane.setFocusable(true);
		pane.requestFocusInWindow(); // richiedo il focus

		// inizializzazione dei pannelli
		menu = new MenuPanel(this, new File("src/img/menu.jpg")); // inizializzazione del panello del menu, passo il
																	// Main e un immagine di background
		menu.setLayout(new BorderLayout()); /// inserimento di una layout per il menu

		panel = new SpacePanel();
		panel.setLayout(new BorderLayout());

		tutorial = new HelpPanel(this, new File("src/img/menu.jpg"));
		tutorial.setLayout(new BorderLayout());

		weapon = new WeaponPanel(this, new File("src/img/menu.jpg"));
		weapon.setLayout(new BorderLayout());

		pause = new PausePanel(this, new File("src/img/bkg.jpg"));
		pause.setLayout(new BorderLayout());

		score = new ScorePanel(this, new File("src/img/menu.jpg"), panel.getScoreList());
		score.setLayout(new BorderLayout());

		// aggiunta degli oggetti nel contenitore
		pane.add(menu, "Menu");
		pane.add(panel, "Game");
		pane.add(tutorial, "Tutorial");
		pane.add(pause, "Pause");
		pane.add(weapon, "Weapon");
		pane.add(score, "Score");
		validate();

		layout.show(this.getContentPane(), "Menu"); // mostro il menu (metodi di CardLayout)

		backgroundMusic(); // attivo la musica di background
	}

	public void startGame(String comPort, String user) { // inizio del gioco

		setComPort(comPort); // setto il numero di porta
		setUser(user); // setto il numero di porta
		panel.setManager(this); // passo il Main al panello di gioco
		layout.show(this.getContentPane(), "Game"); // mostro il pannello
		panel.inGameMusic(); // riproduto la musica di background in gioco
		panel.ready(); // faccio partire il thread

	}

	public void resumeGame() { // ripresa del gioco dal menu di pausa

		layout.show(this.getContentPane(), "Game"); // mostro il gioco
		panel.resume(); // il processo riprende
	}

	public void pauseGame() { // pausa del gioco

		layout.show(this.getContentPane(), "Pause"); // mostro il menu di pausa
		pause.check(); // ricontrollo lo stato dei flag e dei pulsanti precedentemente attivati
	}

	public void showMenu() { // ritorno al menu dal tutorial

		menu.check(); // check dei pulsanti

		if (!mute) // se non � mutato
			panel.backgroundMusic(); // riprendo la musica di background del menu

		layout.show(this.getContentPane(), "Menu"); // mostro il menu
	}

	public void goMenu() { // ritorno al menu dal menu di pausa

		showMenu(); // mostro il menu
		score.update(panel.getScoreList());
		panel.stop(); // chiudo il processo del gioco
	}

	public void showInfo() { // visualizzazione del tutorial

		layout.show(this.getContentPane(), "Tutorial"); // mostro il menu del tutorial
	}

	public void showWeapon() { // visualizzazione del armi

		layout.show(this.getContentPane(), "Weapon"); // mostro il menu del tutorial delle armi e alieni
	}

	public void showScore() { // visualizzazione del tutorial

		layout.show(this.getContentPane(), "Score"); // mostro il menu del tutorial
	}

	public void setSounds(boolean mute) { // setter del suono

		this.mute = mute; // setto il flag
		panel.setSound(!mute); // passo i dati al panello di gioco
	}

	public void setAuto(boolean auto) { // setter dell'autoshot

		this.auto = auto; // setto il flag
		panel.setAuto(auto); // passo i dati al panello di gioco
	}

	public void setArduino(boolean arduino) {

		this.arduino = arduino; // setto il flag
		panel.setArduino(arduino); // passo i dati al panello di gioco
	}

	public void setComPort(String comPort) {

		this.comPort = comPort; // setto la porta
		panel.setComPort(comPort); // passo i dati al panello di gioco
	}

	public void setUser(String user) {

		this.user = user; // setto il nome del giocatore
		panel.setUser(user); // passo i dati al panello di gioco
	}

	public void backgroundMusic() { // riproduzione della musica di background del menu

		panel.backgroundMusic();
	}

	public boolean isMuted() { // check del muto

		return mute;
	}

	public boolean isAuto() { // check dell'autoshot

		return auto;
	}

	public boolean isArduino() { // check di arduino

		return arduino;
	}

	public String getComPort() { // check della porta

		return comPort;
	}

	public String getUser() { // check dell'username

		return user;
	}

	public void reset() { // reset dei record

		panel.resetScore();
		score.update(panel.getScoreList());
	}

	public static void main(String[] args) { // main dell'intera applicazione
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Main thisClass = new Main(); // istanzio il gestore dell'applicazione
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // operazione di base quando il frame viene
																			// chiuso (in questo caso interruzione del
																			// processo)
				thisClass.setVisible(true); // il frame � visibile
			}
		});
	}
}
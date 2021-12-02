import java.awt.Color;

public class AlienCreator {
	
	private int length;	//numero massimo alieni
	private int width; //altezza massima del pannello
	private int[] lifes = {10, 15, 25};	//vite degli alieni in base al tipo

	public AlienCreator(int l, int w){		
		
		//costruttore standard
		length = l;
		width = w;

	}
	
	public Alien[] createArray(){	//creazione della griglia degli alieni
		
		Alien[] a = new Alien[length];  
		int x = 75;	//coordinate del primo alieno
		int y = 75;
		int random = 0;
	
		for(int i = 0; i < length; i++){
			
			random = (int)(Math.random() * 3);	//selezione random dell'alieno
			
			//assegnazione alla cella dell'array un alieno con vita e immagine in base al risultato del random
			a[i] = new Alien(x, y, 30,  15, lifes[random], random+1, Color.red, "src/img/alien" + random + "0.png", null, random);
			x += 67;			
			if((x+67 >= width - 75)){ //se rischio di superare il bordo destro
				x = 75;	//mi abbasso di una riga e riparto dall'inizio
				y += 40;
			}
		}
		
		return a;
		
	}
}
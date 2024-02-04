package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Bird extends ImageView {
	
	  private Image[] sprites;  // zoznam obrazkov pre animaciu
	  private Image[] killed; // zoznam obrazkov pre zostreleneho vtaka (dva smery)
	  private double maxWidth, maxHeight, speed, w, h; // rozmery hry a rozmery vtaka
	  int actImage = 0; // aktualne zobrazovany obrazok
	  int killedImage = 0; // obrazok zostreleneho vtaka (0-dolava, 1-doprava)
	  int state = 0; // 0-zivy, 1-zostreleny, 2-preletel za okraj
	  int direction;
	  
	  public Bird(String SpriteName, int numberOfSprites, double w, double h, double maxw, double maxh) {
		  super();
		  maxWidth = maxw; maxHeight = maxh; this.w = w; this.h = h;
		  
		  // naplnenie zoznamu obrazkov prislusnymi obrazkami 
		  sprites = new Image[numberOfSprites];
		  for(int i = 0; i < numberOfSprites; i++) {
			  sprites[i] = new Image(SpriteName+i+".png", w, h, false, false);
		  }
		  killed = new Image[]{
		            new Image("file:resources/birds/killed0.png", w, h, false, false),
		            new Image("file:resources/birds/killed1.png", w, h, false, false)
		        };
		  
		  do { 
		      speed = (int)(-5 + Math.random() * 11) * 30; // vygeneruje nahodnu rychlost roznu od 0
		   } while (speed == 0);
		    // podla znamienka sa nastavi smer a startovacia poloha
		   if (speed<0) {
			   direction = 0; // doprava
			   setLayoutX(maxWidth);
			   setImage(sprites[24]); // obrazky 24-47 su pre smer dolava
		   }
		      else {
		    	  direction = 1; // dolava
		    	  setLayoutX(-w); 
		    	  setImage(sprites[0]); // obrazky 0-23 su pre smer doprava
		      }
		   setLayoutY(100 + (int) (Math.random() * (maxHeight-300))); // nahodna vyska   
	  }  
	  
	  private void nextImage(){
		  // striedanie obrazkov podla smeru
		  if (direction == 1) actImage = (actImage + 1) % 24; // 0-23
	      if (direction == 0) actImage = 24 + (actImage + 1) % 24; // 24-47
	  }    
	  
	  public void draw() {
		  nextImage();
		  if (state == 0) setImage(sprites[actImage]);  // lietajuci
		  if (state == 1) setImage(killed[direction]);        // zostreleny 
		  if (state == 2) setImage(null);          // na vymazanie
	  }
	  
	  public void move(double deltaTime) {
		  if (state != 1) { // pohyb dopredu pre lietajuceho vtaka
			   setLayoutX(getLayoutX()+speed*deltaTime);
			   setLayoutY(getLayoutY()-5+(int)(Math.random()*11));
			   if ((getLayoutX()<-w) || (getLayoutX()>maxWidth)) state = 2; // ak prejde za scenu, nastavi sa stav na vymazanie
		  } else { // pohyb padania pre zostreleneho vtaka
			  setLayoutY(getLayoutY() + 5);
		  }
		  draw();
	  }
	  
	  public double getState() {
	       return state;
	  }
	  
	  public void setState(int state) {
		  this.state = state;
	  }
	  
	  public double getSize() {
		  return w;
	  }

}

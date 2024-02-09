package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Energy extends ImageView {
	
	  private Image[] sprites;  // images for flying energies
	  private Image killed; 
	  private double maxWidth, maxHeight, speed, w, h; // game and energy dimensions
	  int actImage = 0; // displayed image
	  int state = 0; // 0-flying, 1-killed, 2-out of screen
	  int direction;
	  
	  public Energy(String SpriteName, int numberOfSprites, double w, double h, double maxw, double maxh) {
		  super();
		  maxWidth = maxw; maxHeight = maxh; this.w = w; this.h = h;
		  
		  // create array of images for animation
		  sprites = new Image[numberOfSprites];
		  for(int i = 0; i < numberOfSprites; i++) {
			  sprites[i] = new Image(getClass().getResource(SpriteName+i+".png").toExternalForm(), w, h, false, false);
		  }
		  killed = new Image(getClass().getResource("/images/killed.png").toExternalForm(), w, h, false, false);    
		  
		  do { 
		      speed = (int)(-5 + Math.random() * 11) * 30; // random speed > 0
		   } while (speed == 0);
		   if (speed<0) {
			   direction = 0; // right
			   setLayoutX(maxWidth);
			   setImage(sprites[0]); 
		   }
		      else {
		    	  direction = 1; // left
		    	  setLayoutX(-w); 
		    	  setImage(sprites[0]);
		      }
		   setLayoutY(100 + (int) (Math.random() * (maxHeight-300))); // random height
	  }  
	  
	  private void nextImage(){
		  actImage = (actImage + 1) % 24;
	  }    
	  
	  public void draw() {
		  nextImage();
		  if (state == 0) setImage(sprites[actImage]);  
		  if (state == 1) setImage(killed);      
		  if (state == 2) setImage(null);          
	  }
	  
	  public void move(double deltaTime) {
		  if (state != 1) { // movement for flying energy
			   setLayoutX(getLayoutX()+speed*deltaTime);
			   setLayoutY(getLayoutY()-5+(int)(Math.random()*11));
			   if ((getLayoutX()<-w) || (getLayoutX()>maxWidth)) state = 2;
		  } else { // movement for killed energy
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

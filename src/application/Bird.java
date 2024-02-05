package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Bird extends ImageView {
	
	  private Image[] sprites;  // images for flying birds
	  private Image[] killed; // images for dead birds
	  private double maxWidth, maxHeight, speed, w, h; // game and bird dimensions
	  int actImage = 0; // displayed image
	  int killedImage = 0; // displayed dead bird image (0-left, 1-right)
	  int state = 0; // 0-flying, 1-dead, 2-out of screen
	  int direction;
	  
	  public Bird(String SpriteName, int numberOfSprites, double w, double h, double maxw, double maxh) {
		  super();
		  maxWidth = maxw; maxHeight = maxh; this.w = w; this.h = h;
		  
		  // create array of images
		  sprites = new Image[numberOfSprites];
		  for(int i = 0; i < numberOfSprites; i++) {
			  sprites[i] = new Image(SpriteName+i+".png", w, h, false, false);
		  }
		  killed = new Image[]{
		            new Image("file:resources/birds/killed0.png", w, h, false, false),
		            new Image("file:resources/birds/killed1.png", w, h, false, false)
		        };
		  
		  do { 
		      speed = (int)(-5 + Math.random() * 11) * 30; // random speed > 0
		   } while (speed == 0);
		   if (speed<0) {
			   direction = 0; // right
			   setLayoutX(maxWidth);
			   setImage(sprites[24]); 
		   }
		      else {
		    	  direction = 1; // left
		    	  setLayoutX(-w); 
		    	  setImage(sprites[0]);
		      }
		   setLayoutY(100 + (int) (Math.random() * (maxHeight-300))); // random height
	  }  
	  
	  private void nextImage(){
		  if (direction == 1) actImage = (actImage + 1) % 24; // 0-23
	      if (direction == 0) actImage = 24 + (actImage + 1) % 24; // 24-47
	  }    
	  
	  public void draw() {
		  nextImage();
		  if (state == 0) setImage(sprites[actImage]);  
		  if (state == 1) setImage(killed[direction]);      
		  if (state == 2) setImage(null);          
	  }
	  
	  public void move(double deltaTime) {
		  if (state != 1) { // movement for flying bird
			   setLayoutX(getLayoutX()+speed*deltaTime);
			   setLayoutY(getLayoutY()-5+(int)(Math.random()*11));
			   if ((getLayoutX()<-w) || (getLayoutX()>maxWidth)) state = 2;
		  } else { // movement for dead bird
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

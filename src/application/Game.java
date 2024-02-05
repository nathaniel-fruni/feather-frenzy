package application;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Game extends Group {
	
    final int MAXBIRD = 10; // max number of birds on scene
    private int SCORE = 0;
    private int BULLETS = 10; // max number of bullets
    private int[] TIME = {60}; // time in game 
    private int BULLET_SIZE = 50;
    private double maxWidth, maxHeight; // game dimensions
    private LinkedList<Bird> birdList; // birds on scene
    private LinkedList<ImageView> bulletList; // bullets on scene
    private ImageView background;
    // Texts
    private Text score;
    private Text time; 
    private Text pause;
    private Text SpaceReload;
    // Music and sounds
    private MediaPlayer gunshot;
    private MediaPlayer backgroundMusic;
    private MediaPlayer gunReload;
    private MediaPlayer emptyGun;
    private MediaPlayer bird;
    // Timers
    private Timeline timeline; // for in game time
    private AnimationTimer timer; // for game updating
    // Root, Scene and screens
    protected Group root;
    private TryAgainScreen tryAgainScreen;
    private PauseScreen pauseScreen;
        
    public Game(double w, double h, String bgd, boolean executeAll, Group root) {
    	// for Game class and its subclasses
    	maxWidth = w; maxHeight = h; this.root = root;
        Image bg = new Image(bgd, w, h, false, false);
        background = new ImageView(bg);
        getChildren().add(background);
        // to be able to reload using space key
        setFocusTraversable(true);
        requestFocus();      
        
        // only for Game class
        if(executeAll) {
        	// Texts
        	time = new Text(maxWidth/2.25, maxHeight/20, "TIME: " + formatTime(TIME[0])); time.getStyleClass().add("text");
        	score = new Text(maxWidth/30, maxHeight/20, "SCORE: " + SCORE); score.getStyleClass().add("text");
	        pause = new Text((maxWidth/30)*27, maxHeight/20, "PAUSE"); pause.getStyleClass().add("text");
	        SpaceReload = new Text(maxWidth/2.5, (maxHeight/8)*7, "PRESS SPACE TO RELOAD"); SpaceReload.getStyleClass().add("text");
	        
	        pause.setOnMousePressed(e -> pauseClick());
	        pause.setOnMouseEntered(e -> pause.getStyleClass().add("hovered"));
	        pause.setOnMouseExited(e -> pause.getStyleClass().remove("hovered"));
	        
            getChildren().addAll(time, score, pause);
            
            // Pause screen a Try Again screen
            pauseScreen = new PauseScreen(w, h, "file:resources/screens/pause_screen.png", false, root, this);
            tryAgainScreen = new TryAgainScreen(w, h, "file:resources/screens/try_again_screen.png", false, root, this);
	        
            // timer for in game play time 
	        timeline = new Timeline();
	        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	                TIME[0]--;
	                time.setText("TIME: " + formatTime(TIME[0]));
	                // if time runs out, both timers stop, Try Again screen is added to scene and score is set
	                if (TIME[0] <= 0) {
	                    timeline.stop();
	                    timer.stop();
	                    tryAgainScreen.setScore(SCORE);
	                    root.getChildren().add(tryAgainScreen);
	                }
	            }
	        });
	        timeline.getKeyFrames().add(keyFrame);
	        timeline.setCycleCount(Timeline.INDEFINITE);
	        
	        // timer for game updating
	        timer = new Timer(this);

	        // list inicialization and bullet display
            birdList = new LinkedList<>();
            bulletList = new LinkedList<>();
            CreateBullets();
            
            // Music and sounds
            Media backgroundSound = new Media(new File("resources/sound/background_music.mp3").toURI().toString());
            backgroundMusic = new MediaPlayer(backgroundSound);
            backgroundMusic.setAutoPlay(true);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE); 
            
            Media gunshotSound = new Media(new File("resources/sound/gunshot.mp3").toURI().toString());
            gunshot = new MediaPlayer(gunshotSound);
            
            Media gunReloadSound = new Media(new File("resources/sound/gun_reload.mp3").toURI().toString());
            gunReload = new MediaPlayer(gunReloadSound);
            
            Media emptyGunSound = new Media(new File("resources/sound/empty_gun.mp3").toURI().toString());
            emptyGun = new MediaPlayer(emptyGunSound);
            
            Media birdSound = new Media(new File("resources/sound/bird_sound.mp3").toURI().toString());
            bird = new MediaPlayer(birdSound);
            
            setOnKeyPressed(e -> reload(e));
            setOnMousePressed(e -> onClick(e));
        }
    }
    
    public void startGame() {
    	// method that is called in StartScreen after clicking on START THE GAME button
    	timer.start();
 	    timeline.play();
    }
    
    private void pauseClick() {
    	// method that is called after clicking on PAUSE button
    	timeline.stop();
        timer.stop();
        root.getChildren().add(pauseScreen);
    }
    
    public void resumeGame() {
    	// method that is called in PauseScreen after clicking on RESUME button
    	timer.start();
    	timeline.play();
    }
    
    public void resetGame() {
    	// method that is called in TryAgainScreen after clicking on TRY AGAIN button
    	SCORE = 0;
    	TIME[0] = 60;
   	    score.setText("SCORE: " + SCORE);
   	    time.setText("TIME: " + formatTime(TIME[0]));
  	    if (getChildren().contains(SpaceReload)) getChildren().remove(SpaceReload);
  	   
  	    // delete birds that remained on scene
  	    Iterator<Bird> iterator = birdList.iterator();
  	    while (iterator.hasNext()) {
  	    	Bird element = iterator.next();
  	    	iterator.remove();
		    getChildren().remove(element);
	    }
  	    
  	    // delete remaining bullets and reload
     	Iterator<ImageView> iteratorBullets = bulletList.iterator();
  	    while (iteratorBullets.hasNext()) {
  	    	ImageView element = iteratorBullets.next();
  		    iteratorBullets.remove();
  	        getChildren().remove(element);
  	    }
  	    BULLETS = 10;
  	    CreateBullets();
  	   
  	    // start the timers
  	    startGame(); 
     }
    
    public void updateGame(double deltaTime) {
    	CreateBird();
        MoveBird(deltaTime/1000000000);
        DeleteBirds(); // delete off screen birds
        // space reload info text
        if (BULLETS == 0 && (!getChildren().contains(SpaceReload))) {
        	getChildren().add(SpaceReload);
        } 
    }
    
    private void CreateBullets() {
    	Image img = new Image("file:resources/other/bullet.png", BULLET_SIZE, BULLET_SIZE, false, false);
    	
    	int initialX = (int) maxWidth - BULLET_SIZE;
    	
    	// display 10 bullets in a row
    	for (int i = initialX; i >= initialX - (9 * BULLET_SIZE); i-= BULLET_SIZE) {
    		ImageView blt = new ImageView(); blt.setImage(img);
    		blt.setLayoutX(i); blt.setLayoutY((this.maxHeight/8)*7);
        	bulletList.add(blt);
        	getChildren().add(blt);
    	}
    }
    
    private void CreateBird() {
    	if (birdList.size() < MAXBIRD) {
    		if (Math.random() < 0.3) {
    			double[] birdSizes = {this.maxWidth/20, this.maxWidth/15, this.maxWidth/10}; 
    			// generate random bird size
    	        int randomIndex = new Random().nextInt(3);
    	        double randomSize = birdSizes[randomIndex]; 
    	        Bird b = new Bird("file:resources/birds/bird", 48,randomSize, randomSize, maxWidth, maxHeight);
    	        birdList.add(b);                
    	        getChildren().add(b);          
    	    }
    	}
    }
    
    private void MoveBird(double delta) {
    	Iterator<Bird> iterator = birdList.iterator();
    	while (iterator.hasNext()) {
    		Bird element = iterator.next();
    	    element.move(delta);
    	}
    }
    
    private void DeleteBirds() {
    	// delete all birds that are off screen
    	Iterator<Bird> iterator = birdList.iterator();
    	while (iterator.hasNext()) {
    		Bird element = iterator.next();
    		if (element.getState() == 2) {
    			iterator.remove();
    			getChildren().remove(element); 
    		} 
    	}
    }
    
   private void onClick(MouseEvent evt) {
	   // clicked position
	   double mouseX = evt.getX();
	   double mouseY = evt.getY();
	   
	   // birds, if clicked on
	   if(BULLETS > 0) { // if the gun was loaded
		   // iterate over all birds and check if the bird was clicked on
		   Iterator<Bird> iterator = birdList.iterator();
		   while (iterator.hasNext()) {
			   Bird element = iterator.next();
			   
			   if (element.getBoundsInParent().contains(mouseX, mouseY)) {
				   
				   // bird sound
	 	   		   if(element.getState() != 1) {
	 	   			   bird.seek(Duration.ZERO); // reset the sound to begining
		 	   		   bird.play();
	 	   		   }
				   
				   element.setState(1); // set state to dead
				   // points based on size
	 	 	       if (element.getSize() == this.maxWidth/20) SCORE += 25;
	 	   		   if (element.getSize() == this.maxWidth/15) SCORE += 15;
	 	   		   if (element.getSize() == this.maxWidth/10) SCORE += 5;
	 	   		   score.setText("SCORE: " + SCORE);

	 	 	       // delete dead bird after 3 seconds
	 	   		   PauseTransition pause = new PauseTransition(Duration.seconds(3));
	 	           pause.setOnFinished(e -> {
	 	        	   birdList.remove(element);
	 	        	   getChildren().remove(element);
	 	           });
	 	           pause.play();

	 	           break;
	 	 	  }
	 	   }
	   }
	   
	   // Bullets
	   // play gunshot or empty gun based on bullet count
 	   if(BULLETS > 0) {
 		   gunshot.seek(Duration.ZERO);
 	 	   gunshot.play();
 	   } else {
 		   emptyGun.seek(Duration.ZERO); 
		   emptyGun.play();
 	   }
	   // gunshot and bullet behaviour
	   BULLETS--; 
 	   if (!bulletList.isEmpty()) {
 		   getChildren().remove(bulletList.removeLast());
 	   }
   }
   
   private void reload(KeyEvent e) {
	   KeyCode key = e.getCode();
   	   if (key == KeyCode.SPACE) {
   		   BULLETS = 10;
   		   gunReload .seek(Duration.ZERO); 
   		   gunReload.play();
   	       getChildren().remove(SpaceReload);
   	   }
   	   // delete old list of bullets and fill it up again (in case of reloading not empty gun)
   	   Iterator<ImageView> iterator = bulletList.iterator();
   	   while (iterator.hasNext()) {
   		   ImageView element = iterator.next();
   		   iterator.remove();
           getChildren().remove(element);
   	   }
   	   CreateBullets();
   }
   
   private String formatTime(int seconds) {
       int minutes = seconds / 60;
       int Remainingseconds = seconds % 60;
       return String.format("%d:%02d", minutes, Remainingseconds);
   }
    
}
    

package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Game extends Group {
	
    final int MAXBIRD = 10; // max number of birds on scene
    private int SCORE = 0;
    private int HIGH_SCORE;
    private int BULLETS = 10; // max number of bullets
    private int[] TIME = {45}; // time in game 
    private int BULLET_SIZE = 50;
    private double maxWidth, maxHeight; // game dimensions
    private LinkedList<Energy> birdList; // birds on scene
    private LinkedList<ImageView> bulletList; // bullets on scene
    private ImageView background;
    // Texts
    private Text score;
    private Text time; 
    private Text pause;
    private StackPane SpaceReload;
    // Music and sounds
    private MediaPlayer gunshot;
    private MediaPlayer backgroundMusic;
    private MediaPlayer backgroundMusic2;
    private MediaPlayer gameOver;
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
        
    public Game(double w, double h, String bgd, Group root) {
    	maxWidth = w; maxHeight = h; this.root = root;
        Image bg = new Image(bgd, w, h, false, false);
        background = new ImageView(bg);
        getChildren().add(background);      
        
        // Displayed texts
        initializeTexts();
        
        // Pause screen a Try Again screen
        pauseScreen = new PauseScreen(w, h, "file:resources/screens/pause_screen.png", root, this);
        tryAgainScreen = new TryAgainScreen(w, h, "file:resources/screens/try_again_screen.png", root, this);
        
        initializeTimers();

        // list initialization and bullet display
        birdList = new LinkedList<>();
        bulletList = new LinkedList<>();
        CreateBullets();
        
        // Music and sounds
        initializeMusic();
       
        setFocusTraversable(true);
        requestFocus();
        setOnKeyPressed(e -> reload(e));
        setOnMousePressed(e -> onClick(e));
        
    }
    
    private void initializeHighScore() {
    	boolean write = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("data/high-score.txt"))) {
            String high_score = reader.readLine();

            if (high_score != null) {
                if (Integer.parseInt(high_score) < SCORE) {
                    write = true;
                    HIGH_SCORE = SCORE;
                } else {
                	HIGH_SCORE = Integer.parseInt(high_score);
                }
            } else {
                write = true;
                HIGH_SCORE = SCORE;
            }

        } catch (Exception e) {
            System.err.println("Error reading from the file: " + e.getMessage());
        }

        if (write) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/high-score.txt"))) {
                writer.write(String.valueOf(SCORE));
            } catch (Exception e) {
                System.err.println("Error writing to the file: " + e.getMessage());
            }
        }
    }
    
    private void initializeMusic() {
    	Media backgroundSound = new Media(new File("resources/sound/background_music.mp3").toURI().toString());
        backgroundMusic = new MediaPlayer(backgroundSound);
        
        Media backgroundSound2 = new Media(new File("resources/sound/background_music2.mp3").toURI().toString());
        backgroundMusic2 = new MediaPlayer(backgroundSound2);
        backgroundMusic2.setAutoPlay(true);
        backgroundMusic2.setCycleCount(MediaPlayer.INDEFINITE);
        
        Media gameOverSound = new Media(new File("resources/sound/game_over.mp3").toURI().toString());
        gameOver = new MediaPlayer(gameOverSound);
        
        Media gunshotSound = new Media(new File("resources/sound/gunshot.mp3").toURI().toString());
        gunshot = new MediaPlayer(gunshotSound);
        
        Media gunReloadSound = new Media(new File("resources/sound/gun_reload.mp3").toURI().toString());
        gunReload = new MediaPlayer(gunReloadSound);
        
        Media emptyGunSound = new Media(new File("resources/sound/empty_gun.mp3").toURI().toString());
        emptyGun = new MediaPlayer(emptyGunSound);
        
        Media birdSound = new Media(new File("resources/sound/bird_sound.mp3").toURI().toString());
        bird = new MediaPlayer(birdSound);
    }
    
    private void initializeTexts() {
        BorderPane textContainer = new BorderPane();
        textContainer.setPrefWidth(maxWidth);

        time = new Text("TIME: " + formatTime(TIME[0])); time.getStyleClass().add("text");
        score = new Text("SCORE: " + SCORE); score.getStyleClass().add("text");
        pause = new Text("PAUSE"); pause.getStyleClass().add("text");

        BorderPane.setMargin(score, new Insets(15, 15, 0, 15)); 
        BorderPane.setMargin(time, new Insets(15, 0, 0, 0));
        BorderPane.setMargin(pause, new Insets(15, 15, 0, 15)); 

        textContainer.setLeft(score);
        textContainer.setCenter(time);
        textContainer.setRight(pause);

        pause.setOnMousePressed(e -> pauseClick());
        pause.setOnMouseEntered(e -> pause.getStyleClass().add("hovered"));
        pause.setOnMouseExited(e -> pause.getStyleClass().remove("hovered"));

        getChildren().add(textContainer);
        
        SpaceReload = new StackPane();
        SpaceReload.setPrefWidth(maxWidth);
        Text SpaceReloadText = new Text("PRESS SPACE TO RELOAD"); SpaceReloadText.getStyleClass().add("text");
        SpaceReload.getChildren().add(SpaceReloadText);
        StackPane.setAlignment(SpaceReloadText, Pos.CENTER);
        SpaceReload.setLayoutY((maxHeight/8)*7);
    }


    
    private void initializeTimers() {
    	// timer for in game play time 
        timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TIME[0]--;
                time.setText("TIME: " + formatTime(TIME[0]));
                // when game time runs out
                if (TIME[0] <= 0) {
                    timeline.stop();
                    timer.stop();
                    backgroundMusic.stop();
                    gameOver.seek(Duration.ZERO);
                    gameOver.play();
                    backgroundMusic2.play();
                    initializeHighScore();
                    tryAgainScreen.setScore(SCORE);
                    tryAgainScreen.setHighScore(HIGH_SCORE);
                    root.getChildren().add(tryAgainScreen);
                }
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        
        // timer for game updating
        timer = new Timer(this);
    }
    
    public void startGame() {
    	// method that is called in StartScreen after clicking on START THE GAME button
    	timer.start();
 	    timeline.play();
 	    backgroundMusic2.stop();
 	    backgroundMusic.setAutoPlay(true);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE); 
    }
    
    private void pauseClick() {
    	// method that is called after clicking on PAUSE button
    	timeline.stop();
        timer.stop();
        backgroundMusic.pause();
        backgroundMusic2.play();
        root.getChildren().add(pauseScreen);
    }
    
    public void resumeGame() {
    	// method that is called in PauseScreen after clicking on RESUME button
    	timer.start();
    	timeline.play();
    	backgroundMusic2.stop();
    	backgroundMusic.play();
    }
    
    public void resetGame() {
    	// method that is called in TryAgainScreen after clicking on TRY AGAIN button
    	SCORE = 0;
    	TIME[0] = 45;
   	    score.setText("SCORE: " + SCORE);
   	    time.setText("TIME: " + formatTime(TIME[0]));
  	    if (getChildren().contains(SpaceReload)) getChildren().remove(SpaceReload);
  	    backgroundMusic2.stop();
  	    backgroundMusic.play();
  	   
  	    // delete birds that remained on scene
  	    Iterator<Energy> iterator = birdList.iterator();
  	    while (iterator.hasNext()) {
  	    	Energy element = iterator.next();
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
    	
    	int initialX = (int) maxWidth - BULLET_SIZE - 10;
    	
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
    			double[] birdSizes = {this.maxWidth/30, this.maxWidth/25, this.maxWidth/20}; 
    			// generate random bird size
    	        int randomIndex = new Random().nextInt(3);
    	        double randomSize = birdSizes[randomIndex]; 
    	        Energy b = new Energy("file:resources/energy-orbs/energy", 24, randomSize, randomSize, maxWidth, maxHeight);
    	        birdList.add(b);                
    	        getChildren().add(b);          
    	    }
    	}
    }
    
    private void MoveBird(double delta) {
    	Iterator<Energy> iterator = birdList.iterator();
    	while (iterator.hasNext()) {
    		Energy element = iterator.next();
    	    element.move(delta);
    	}
    }
    
    private void DeleteBirds() {
    	// delete all birds that are off screen
    	Iterator<Energy> iterator = birdList.iterator();
    	while (iterator.hasNext()) {
    		Energy element = iterator.next();
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
		   Iterator<Energy> iterator = birdList.iterator();
		   while (iterator.hasNext()) {
			   Energy element = iterator.next();
			   
			   if (element.getBoundsInParent().contains(mouseX, mouseY)) {
				   
				   // bird sound
	 	   		   if(element.getState() != 1) {
	 	   			   bird.seek(Duration.ZERO);
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
    

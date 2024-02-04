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
	
    final int MAXBIRD = 10; // maximalny pocet vtakov na scene
    private int SCORE = 0;
    private int BULLETS = 10; // pocet dostupnych nabojov
    private int[] TIME = {75}; // cas v hre (pouzite pole, pretoze ho posuvam do funkcii, int by nefungoval pretoze by sa vytvarala len jeho kopia)
    private double maxWidth, maxHeight; // rozmer plochy hry
    private LinkedList<Bird> birdList; // zoznam vtakov na scene
    private LinkedList<ImageView> bulletList; // zoznam nabojov (obrazky)
    private ImageView background;
    // Texty
    private Text score; // vypisovanie skore
    private Text time; // vypisovanie casu
    private Text pause; // text ako tlacidlo PAUSE
    private Text SpaceReload; // informacny text ako nabit zbran po minuti nabojov
    // Hudba a zvuky
    private MediaPlayer gunshot;
    private MediaPlayer backgroundMusic;
    private MediaPlayer gunReload;
    private MediaPlayer emptyGun;
    private MediaPlayer bird;
    // Casovace
    private Timeline timeline; // casovac pre odpocitavanie casu v hre
    private AnimationTimer timer; // casovac aktualizovania sceny hry
    // Root, Scene a obrazovky
    protected Group root;
    private TryAgainScreen tryAgainScreen;
    private PauseScreen pauseScreen;
        
    public Game(int w, int h, String pozadie, boolean executeAll, Group root) {
    	// prikazy pre hru a aj pre jej subclassy
    	maxWidth = w; maxHeight = h; this.root = root;
        Image bg = new Image(pozadie, w, h, false, false);
        background = new ImageView(bg);
        getChildren().add(background);
        // fokus pre to, aby sa pomocou stlacenia SPACE dalo dobijat
        setFocusTraversable(true);
        requestFocus();      
        
        // prikazy len pre hru
        if(executeAll) {
        	//Texty
        	time = new Text(450, 30, "TIME: " + formatTime(TIME[0])); time.getStyleClass().add("text");
        	score = new Text(13, 30, "SCORE: " + SCORE); score.getStyleClass().add("text");
	        pause = new Text(940,30, "PAUSE"); pause.getStyleClass().add("text");
	        SpaceReload = new Text(400, 450, "PRESS SPACE TO RELOAD"); SpaceReload.getStyleClass().add("text");
	        
	        pause.setOnMousePressed(e -> pauseClick());
	        pause.setOnMouseEntered(e -> pause.getStyleClass().add("hovered"));
	        pause.setOnMouseExited(e -> pause.getStyleClass().remove("hovered"));
	        
            getChildren().addAll(time, score, pause);
            
            //Pause obrazovka a Try Again obrazovka
            pauseScreen = new PauseScreen(1024, 500, "file:resources/screens/pause_screen.png", false, root, this);
            tryAgainScreen = new TryAgainScreen(1024, 500, "file:resources/screens/try_again_screen.png", false, root, this);
	        
            // Casovac pre odpocitavanie casu v hre
	        timeline = new Timeline();
	        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	                TIME[0]--;
	                time.setText("TIME: " + formatTime(TIME[0]));
	                // ak vyprsi cas, zastavia sa oba casovace hry a na scenu sa prida Try Again Screen a nastavi sa do nej nahrate skore
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
	        
	        // Casovac aktualizovania sceny hry
	        timer = new Timer(this);

	        // Inicializácia List-ov a vytvorenie nábojov
            birdList = new LinkedList<>();
            bulletList = new LinkedList<>();
            CreateBullets();
            
            // Hudba a zvuky
            Media backgroundSound = new Media(new File("resources/sound/background_music.mp3").toURI().toString());
            backgroundMusic = new MediaPlayer(backgroundSound);
            backgroundMusic.setAutoPlay(true); // aby sa spustila automaticky pri zapnuti aplikacie
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE); // aby hrala stale
            
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
    	// metoda, ktoru sa vola po kliknuti na START THE GAME na Start Screen
    	timer.start();
 	    timeline.play();
    }
    
    private void pauseClick() {
    	// metoda, ktora sa vola po kliknuti na PAUSE
    	timeline.stop();
        timer.stop();
        root.getChildren().add(pauseScreen);
    }
    
    public void resumeGame() {
    	// metoda, ktora sa vola po kliknuti na RESUME na Pause Screen
    	timer.start();
    	timeline.play();
    }
    
    public void resetGame() {
    	// metoda, ktora sa vola po kliknuti na TRY AGAIN na Try Again Screen
    	SCORE = 0;
    	TIME[0] = 75;
   	    score.setText("SCORE: " + SCORE);
   	    time.setText("TIME: " + formatTime(TIME[0]));
  	    if (getChildren().contains(SpaceReload)) getChildren().remove(SpaceReload);
  	   
  	    // vymazanie vtakov, ktory zostali na scene
  	    Iterator<Bird> iterator = birdList.iterator();
  	    while (iterator.hasNext()) {
  	    	Bird element = iterator.next();
  	    	iterator.remove();
		    getChildren().remove(element);
	    }
  	    
  	    // vymazanie nabojov, ktore zostali a obnovenie do stavu 10
     	Iterator<ImageView> iteratorBullets = bulletList.iterator();
  	    while (iteratorBullets.hasNext()) {
  	    	ImageView element = iteratorBullets.next();
  		    iteratorBullets.remove();
  	        getChildren().remove(element);
  	    }
  	    BULLETS = 10;
  	    CreateBullets();
  	   
  	    // spustenie casovacov
  	    startGame(); 
     }
    
    public void updateGame(double deltaTime) {
    	CreateBird(); // vytvaranie vtakov
        MoveBird(deltaTime/1000000000); // pohyb vtakov
        DeleteBirds(); // premazanie vakov
        // vypisanie informacneho textu o nabijani
        if (BULLETS == 0 && (!getChildren().contains(SpaceReload))) {
        	getChildren().add(SpaceReload);
        } 
    }
    
    private void CreateBullets() {
    	Image img = new Image("file:resources/other/bullet.png", 50, 50, false, false);
    	// zobrazenie 10 nabojov za sebou
    	for (int i = 965; i >= 515; i-= 50) {
    		ImageView blt = new ImageView(); blt.setImage(img);
    		blt.setLayoutX(i); blt.setLayoutY(440);
        	bulletList.add(blt);
        	getChildren().add(blt);
    	}
    }
    
    private void CreateBird() {
    	if (birdList.size() < MAXBIRD) {
    		if (Math.random() < 0.3) {
    			int[] values = {50, 70, 105}; // velkosti vtakov 
    			// vygenerovanie nahodnej velkosti
    	        int randomIndex = new Random().nextInt(3);
    	        int randomSize = values[randomIndex]; 
    	        Bird b = new Bird("file:resources/birds/bird", 48,randomSize, randomSize, maxWidth, maxHeight);
    	        birdList.add(b);                
    	        getChildren().add(b);          
    	    }
    	}
    }
    
    private void MoveBird(double delta) {
    	// pohnutie kazdeho vtaka v liste
    	Iterator<Bird> iterator = birdList.iterator();
    	while (iterator.hasNext()) {
    		Bird element = iterator.next();
    	    element.move(delta);
    	}
    }
    
    private void DeleteBirds() {
    	// vymazanie vtakov ak su v stave 2, teda presli za okraj sceny
    	Iterator<Bird> iterator = birdList.iterator();
    	while (iterator.hasNext()) {
    		Bird element = iterator.next();
    		if (element.getState() == 2) {
    			iterator.remove(); // zo zoznamu
    			getChildren().remove(element); // zo sceny
    		} 
    	}
    }
    
   private void onClick(MouseEvent evt) {
	   // pozicia, kam sa kliklo
	   double mouseX = evt.getX();
	   double mouseY = evt.getY();
	   
	   // Vtaky ak sa na nich kliklo
	   if(BULLETS > 0) { // overenie ci pri kliknuti bola nabita zbran
		   // prechod po zozname vtakov a identifikovanie, ci sa na neho kliklo
		   Iterator<Bird> iterator = birdList.iterator();
		   while (iterator.hasNext()) {
			   Bird element = iterator.next();
			   
			   if (element.getBoundsInParent().contains(mouseX, mouseY)) {
				   
				   // zvuk vtaka
	 	   		   if(element.getState() != 1) {
	 	   			   bird.seek(Duration.ZERO); // resetuje zvuk na zaciatok
		 	   		   bird.play();
	 	   		   }
				   
				   element.setState(1); // nastavi stav na zostreleny
				   // prideli pocet bodov na zaklade velkosti
	 	 	       if (element.getSize() == 35) SCORE += 25;
	 	   		   if (element.getSize() == 70) SCORE += 15;
	 	   		   if (element.getSize() == 105) SCORE += 5;
	 	   		   score.setText("SCORE: " + SCORE);

	 	 	       // odstranenie vtaka po 2 sekundach
	 	   		   PauseTransition pause = new PauseTransition(Duration.seconds(2));
	 	           pause.setOnFinished(e -> {
	 	        	   birdList.remove(element);
	 	        	   getChildren().remove(element);
	 	           });
	 	           pause.play();

	 	           break;
	 	 	  }
	 	   }
	   }
	   
	   // Naboje
	   // prehratie zvuku vystrelu alebo prazdnej zbrane podla stavu nabojov
 	   if(BULLETS > 0) {
 		   gunshot.seek(Duration.ZERO); // resetuje zvuk na zaciatok
 	 	   gunshot.play();
 	   } else {
 		   emptyGun.seek(Duration.ZERO); // resetuje zvuk na zaciatok
		   emptyGun.play();
 	   }
	   // odpocitanie naboja a vymazanie naboja z listu a zo sceny
	   BULLETS--; 
 	   if (!bulletList.isEmpty()) {
 		   getChildren().remove(bulletList.removeLast());
 	   }
   }
   
   private void reload(KeyEvent e) {
	   KeyCode key = e.getCode();
   	   if (key == KeyCode.SPACE) {
   		   BULLETS = 10;
   		   gunReload .seek(Duration.ZERO); //resetuje zvuk na zaciatok
   		   gunReload.play();
   	       getChildren().remove(SpaceReload);
   	   }
   	   // vymazanie stareho Listu nabojov a opätovne naplnenie (osetrenie pre pripad nabijania, ked este nie su minute vsetky naboje)
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
    

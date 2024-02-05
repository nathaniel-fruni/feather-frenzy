package application;
	
import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Group root = new Group();
			
			Screen screen = Screen.getPrimary();
	        double screenWidth = screen.getBounds().getWidth();
	        double screenHeight = screen.getBounds().getHeight();
	        double gameWidth = screenWidth * 0.8;
	        double gameHeight = screenHeight * 0.8;
	        
	        Scene scene = new Scene(root, gameWidth, gameHeight);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			Image cursorImage = new Image("file:resources/other/aim.png");
			scene.setCursor(new ImageCursor(cursorImage, cursorImage.getWidth() / 2, cursorImage.getHeight() / 2));
			
	        Game game = new Game(gameWidth, gameHeight, "file:resources/screens/background.png", true, root);
	        
	        StartScreen strS = new StartScreen(gameWidth, gameHeight, "file:resources/screens/start_screen.png", false, root, game);
	        root.getChildren().add(strS);
	        
	        primaryStage.setScene(scene); 
	        primaryStage.setTitle("Feather Frenzy");
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
		
}

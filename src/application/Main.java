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
	        
	        Scene scene = new Scene(root, screenWidth, screenHeight);
			scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
			
			Image cursorImage = new Image(getClass().getResource("/images/aim.png").toExternalForm());
			scene.setCursor(new ImageCursor(cursorImage, cursorImage.getWidth() / 2, cursorImage.getHeight() / 2));
			
	        Game game = new Game(screenWidth, screenHeight, "/images/background.jpg", root);
	        
	        StartScreen strS = new StartScreen(screenWidth, screenHeight, "/images/start_screen.jpg", root, game);
	        root.getChildren().add(strS);
	        
	        primaryStage.setFullScreen(true);
	        primaryStage.setScene(scene); 
	        primaryStage.setTitle("Lumina Impact");
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
		
}

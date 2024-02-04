package application;
	
import javafx.application.Application;
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
			Scene scene = new Scene(root, 1024, 500);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			Image cursorImage = new Image("file:resources/other/aim.png");
	        scene.setCursor(new ImageCursor(cursorImage));
			
	        Game game = new Game(1024, 500, "file:resources/screens/background.png", true, root);
	        
	        StartScreen strS = new StartScreen(1024, 500, "file:resources/screens/start_screen.png", false, root, game);
	        root.getChildren().add(strS);
	        
	        primaryStage.setScene(scene); 
	        primaryStage.setTitle("Moorhuhn");
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
		
}

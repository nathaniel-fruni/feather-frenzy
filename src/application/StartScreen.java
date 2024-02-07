package application;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class StartScreen extends Group{
	
	private Text exit;
	private Text start;
	private Game game;
	private Group root;
	private double width, height;

	public StartScreen(double w, double h, String background, Group root, Game game) {
		
        Image bg = new Image(background, w, h, false, false);
        ImageView bgd = new ImageView(bg);
        getChildren().add(bgd);
		this.game = game;
		this.root = root;
		this.width = w; this.height = h;
		
		initializeTexts();
	}
	
	private void initializeTexts() {
		start = new Text(width/4.25, height/2.65, "START THE GAME"); start.getStyleClass().add("text2");
		exit = new Text(width/2.5, (height/3)*2.1, "EXIT"); exit.getStyleClass().add("text5");
		
		start.setOnMousePressed(e -> onClickStart());
		start.setOnMouseEntered(e -> start.getStyleClass().add("hovered"));
		start.setOnMouseExited(e -> start.getStyleClass().remove("hovered"));
		
		exit.setOnMousePressed(e -> onClickExit());
		exit.setOnMouseEntered(e -> exit.getStyleClass().add("hovered"));
		exit.setOnMouseExited(e -> exit.getStyleClass().remove("hovered"));
		
		getChildren().addAll(start, exit);
	}
	
	private void onClickStart() {
		root.getChildren().remove(this);
		root.getChildren().add(game);
		game.startGame();
	}
	
	private void onClickExit() {
		System.exit(0);
	}

}

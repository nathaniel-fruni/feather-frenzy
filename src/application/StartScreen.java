package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class StartScreen extends StackPane{
	
	private Text title;
	private Text start;
	private Text exit;
	private Game game;
	private Group root;
	private ImageView bgd;

	public StartScreen(double w, double h, String background, Group root, Game game) {
		Image bg = new Image(getClass().getResource(background).toExternalForm(), w, h, false, false);
        bgd = new ImageView(bg);
        getChildren().add(bgd);
		this.game = game;
		this.root = root;
		
		initializeTexts();
	}
	
	private void initializeTexts() {
		title = new Text("L U M I N A  I M P A C T"); title.getStyleClass().add("title-text");
		start = new Text("S T A R T  T H E  G A M E"); start.getStyleClass().add("text2");
		exit = new Text("E X I T"); exit.getStyleClass().add("text5");
		
		setAlignment(title, Pos.TOP_CENTER);
		setAlignment(start, Pos.CENTER);
        setAlignment(exit, Pos.CENTER);
        
        setMargin(title, new Insets(70,0,0,0));
        setMargin(exit, new Insets(200,0,0,0));
		
		start.setOnMousePressed(e -> onClickStart());
		start.setOnMouseEntered(e -> start.getStyleClass().add("hovered"));
		start.setOnMouseExited(e -> start.getStyleClass().remove("hovered"));
		
		exit.setOnMousePressed(e -> onClickExit());
		exit.setOnMouseEntered(e -> exit.getStyleClass().add("hovered"));
		exit.setOnMouseExited(e -> exit.getStyleClass().remove("hovered"));
		
		getChildren().addAll(title, start, exit);
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

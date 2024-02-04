package application;

import javafx.scene.Group;
import javafx.scene.text.Text;

public class StartScreen extends Game{
	
	private Text exit;
	private Text start;
	private Game game;

	public StartScreen(int w, int h, String pozadie, boolean executeAll, Group root, Game game) {
		super(w, h, pozadie, executeAll, root);
		this.game = game;
		
		start = new Text(240, 193, "START THE GAME"); start.getStyleClass().add("text2");
		exit = new Text(410, 352, "EXIT"); exit.getStyleClass().add("text5");
		
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

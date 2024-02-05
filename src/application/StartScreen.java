package application;

import javafx.scene.Group;
import javafx.scene.text.Text;

public class StartScreen extends Game{
	
	private Text exit;
	private Text start;
	private Game game;

	public StartScreen(double w, double h, String background, boolean executeAll, Group root, Game game) {
		super(w, h, background, executeAll, root);
		this.game = game;
		
		start = new Text(w/4.25, h/2.65, "START THE GAME"); start.getStyleClass().add("text2");
		exit = new Text(w/2.5, (h/3)*2.1, "EXIT"); exit.getStyleClass().add("text5");
		
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

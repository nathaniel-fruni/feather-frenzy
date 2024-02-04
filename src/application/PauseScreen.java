package application;

import javafx.scene.Group;
import javafx.scene.text.Text;

public class PauseScreen extends Game{
	
	private Text resume;
	private Text exit;
	private Game game;

	public PauseScreen(int w, int h, String pozadie, boolean executeAll, Group root, Game game) {
		super(w, h, pozadie, executeAll, root);
		this.game = game;
		
        resume = new Text(380, 200, "RESUME"); resume.getStyleClass().add("text4");
        exit = new Text(470, 300, "EXIT"); exit.getStyleClass().add("text5");
        
        resume.setOnMousePressed(e -> onClickResume());
		resume.setOnMouseEntered(e -> resume.getStyleClass().add("hovered"));
		resume.setOnMouseExited(e -> resume.getStyleClass().remove("hovered"));
        
		exit.setOnMousePressed(e -> onClickExit());
		exit.setOnMouseEntered(e -> exit.getStyleClass().add("hovered"));
		exit.setOnMouseExited(e -> exit.getStyleClass().remove("hovered"));
		
		getChildren().addAll(resume, exit);
	}
	
	private void onClickResume() {
		root.getChildren().remove(this);
		game.resumeGame();
	}
	
	private void onClickExit() {
		System.exit(0);
	}

}

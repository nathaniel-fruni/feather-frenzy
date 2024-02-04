package application;

import javafx.scene.Group;
import javafx.scene.text.Text;

public class TryAgainScreen extends Game{
	
	private Text exit;
	private Text end_score;
	private Text tryAgain;
	private Game g;

	public TryAgainScreen(int w, int h, String pozadie, boolean executeAll, Group root, Game g) {
		super(w, h, pozadie, executeAll, root);
		this.g = g;
		
		end_score = new Text(15, 470, "YOUR SCORE: "); end_score.getStyleClass().add("text");
        tryAgain = new Text(360, 200, "TRY AGAIN"); tryAgain.getStyleClass().add("text3");
        exit = new Text(850, 470, "EXIT THE GAME"); exit.getStyleClass().add("text");
        
        tryAgain.setOnMousePressed(e -> reset());
        tryAgain.setOnMouseEntered(e -> tryAgain.getStyleClass().add("hovered"));
		tryAgain.setOnMouseExited(e -> tryAgain.getStyleClass().remove("hovered"));
		
		exit.setOnMousePressed(e -> onClick());
		exit.setOnMouseEntered(e -> exit.getStyleClass().add("hovered"));
		exit.setOnMouseExited(e -> exit.getStyleClass().remove("hovered"));
		
		getChildren().addAll(tryAgain, end_score,exit);
	}

	protected void reset() {
		root.getChildren().remove(this);
		g.resetGame();
	}
	
	private void onClick() {
		System.exit(0);
	}
	
	public void setScore(int score) {
		end_score.setText("YOUR SCORE: " + score);
	}
	
}

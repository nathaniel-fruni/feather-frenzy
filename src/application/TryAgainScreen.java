package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class TryAgainScreen extends StackPane {
	
	private Text tryAgain;
	private Text exit;
	private Text high_score;
	private Text end_score;
	private Game game;
	private Group root;

	public TryAgainScreen(double w, double h, String background, Group root, Game game) {
		Image bg = new Image(background, w, h, false, false);
        ImageView bgd = new ImageView(bg);
        getChildren().add(bgd);
		this.game = game;
		this.root = root;
		
		initializeTexts();
	}
	
	private void initializeTexts() {
		tryAgain = new Text("T R Y  A G A I N"); tryAgain.getStyleClass().add("text3");
        exit = new Text("E X I T  T H E  G A M E"); exit.getStyleClass().add("text");
        high_score = new Text("H I G H  S C O R E :  "); high_score.getStyleClass().add("text");
		end_score = new Text("Y O U R  S C O R E :  "); end_score.getStyleClass().add("text");
        
        setAlignment(tryAgain, Pos.CENTER);
        setAlignment(exit, Pos.BOTTOM_RIGHT);
        setAlignment(end_score, Pos.TOP_CENTER);
        setAlignment(high_score, Pos.BOTTOM_LEFT);
        
        setMargin(exit, new Insets(15));
        setMargin(end_score, new Insets(15, 0, 0, 0));
        setMargin(high_score, new Insets(15));
        
        tryAgain.setOnMousePressed(e -> reset());
        tryAgain.setOnMouseEntered(e -> tryAgain.getStyleClass().add("hovered"));
		tryAgain.setOnMouseExited(e -> tryAgain.getStyleClass().remove("hovered"));
		
		exit.setOnMousePressed(e -> onClick());
		exit.setOnMouseEntered(e -> exit.getStyleClass().add("hovered"));
		exit.setOnMouseExited(e -> exit.getStyleClass().remove("hovered"));
		
		getChildren().addAll(tryAgain, end_score, high_score, exit);
	}

	protected void reset() {
		root.getChildren().remove(this);
		game.resetGame();
	}
	
	private void onClick() {
		System.exit(0);
	}
	
	public void setScore(int score) {
		end_score.setText("Y O U R  S C O R E :  " + score);
	}
	
	public void setHighScore(int high_score) {
		this.high_score.setText("H I G H  S C O R E :  " + high_score);
	}
	
}

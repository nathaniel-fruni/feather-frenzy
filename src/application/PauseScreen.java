package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class PauseScreen extends StackPane{
	
	private Text resume;
	private Text exit;
	private Game game;
	private Group root;

	public PauseScreen(double w, double h, String background, Group root, Game game) {
		Image bg = new Image(background, w, h, false, false);
        ImageView bgd = new ImageView(bg);
        getChildren().add(bgd);
		this.game = game;
		this.root = root;
		
		initializeTexts();
	}
	
	private void initializeTexts() {
		resume = new Text("R E S U M E"); resume.getStyleClass().add("text4");
        exit = new Text("E X I T"); exit.getStyleClass().add("text5");
        
        setAlignment(resume, Pos.CENTER);
        setAlignment(exit, Pos.CENTER);
        
        setMargin(exit, new Insets(200,0,0,0));
        
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

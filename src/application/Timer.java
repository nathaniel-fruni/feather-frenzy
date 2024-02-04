package application;

import javafx.animation.AnimationTimer;

public class Timer extends AnimationTimer {
	
    private Game myGame;
    private long lastNanoTime;

    public Timer(Game mg) {
        myGame = mg;
    }

    @Override
    public void handle(long now) {
        myGame.updateGame(now - lastNanoTime);
        lastNanoTime = now;
        try {
            Thread.sleep(50);
        } catch (Exception e)  {}
    }

    public void start() {
        lastNanoTime =  System.nanoTime();
        super.start();
    }

}


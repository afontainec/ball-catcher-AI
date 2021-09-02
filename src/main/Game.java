package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Game {
	
	public Paddle player;
	public Ball ball;
	
	private int length = 12;
	private int height = 15;
	private int score = 0;
	private ArrayList<String> scoreRecord = new ArrayList<String>();
	private ArrayList<Movement> movements = new ArrayList<Movement>();
	private int counter = 0;
	private int GAME_LENGTH = 1500;
	private int ballCount = 1;
	private int PAUSE_BETWEEN = 0;
	private Boolean verbose = false;
	
	public Game() {
		this.ball = new Ball(5);
		this.player = new Paddle(this.length /2, this.height, this.length);
	}
	
	public void setupBall() {
		ballCount++;
		int start = ThreadLocalRandom.current().nextInt(0, length);
		this.ball = new Ball(start);

	}
	
	public double start() throws InterruptedException, IOException {
		counter = 0;
		
		while(counter < GAME_LENGTH) {
			if(counter % 2 * height == 0) scoreRecord.add(String.valueOf(score));
//			if(scoreRecord.size() % 10 == 0) this.printRecord();
			this.ball.move();
			int playerStartingPoint = this.player.position;
			String direction = this.player.move(this.ball.row, this.ball.column);
			if(this.verbose) System.out.println("it: " + counter + "; score: " + score + "/" + this.ballCount + " (" + this.player.position + "-" + this.ball.row + "," + this.ball.column  + ")");
			this.draw();
			TimeUnit.MILLISECONDS.sleep(PAUSE_BETWEEN);
			counter++;
			this.computeEndOfIteration(playerStartingPoint, direction);
		}
		this.printRecord();
		this.player.brain.registerChances();
		return score;
//        System.out.println("Hope to see you soon! score: " + score + "/" + ballCount);
	}
	
	public void printRecord() {
		if(!this.verbose) return;
		System.out.println("SCORE RECORD");        
		for (int i = 0; i < this.scoreRecord.size(); i++) {
			System.out.println(this.scoreRecord.get(i));        
	    }
	}
	
	public void draw() throws IOException {
		if(!this.verbose) return;
		this.drawWall(false);
		this.drawInner();
		this.drawWall(true);
	}
	
	
	private void drawWall(Boolean hasPaddle) {
		String wall = "";
		for(int j = 0; j < this.length; j++) {
			if(hasPaddle && this.isPaddleOnCell(j)) {wall += "v"; }
			else wall += "-";
		}
		System.out.println(wall);
	}
	
	private void drawInner() {
		for(int i = 0; i < this.height; i++) {
			String line = "";
			for(int j = 0; j < this.length; j++) {
				if(this.isBallOnCell(i, j)) {line += "o"; }
				else if(this.isLimit(j)) { line += "|"; } 
				else { line += " "; }
			}
			System.out.println(line);
		}
	}
	
	private Boolean isBallOnCell(int i, int j) {
		return this.ball.column == j && this.ball.row == i;
	}
	
	private Boolean isPaddleOnCell(int j) {
		return this.player.position == j;
	}
	
	private Boolean isLimit(int j) {
		return j == 0 || j == this.length - 1;
	}
	
	
	private void computeEndOfIteration(int startingPoint, String direction) {
		movements.add(new Movement(this.ball.column, this.ball.row, startingPoint, direction));
		if(this.isBallAtTheEnd()) {
			if(this.ball.column == this.player.position) {
				score++;
				this.player.brain.rewardMovements(movements, counter);
			} else {
				this.player.brain.punishMovements(movements, counter);
			}
			this.movements.clear();
			this.setupBall();
		}

	}
	
	private Boolean isBallAtTheEnd() {
		return this.ball.row == this.height - 1;
	}
}



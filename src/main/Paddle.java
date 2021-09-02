package main;

public class Paddle {
	
	
	public int position;
	public PaddleBrain brain;
	
	public Paddle(int pos, int height, int length) {
		this.position = pos;
		this.brain = new PaddleBrain(height, length);
	}
	
	public String move(int i, int j) {
		if(this.brain.shouldStayIdle(i, j, this.position)) return "idle";
		Boolean movedRight = this.brain.shouldMoveToTheRight(i, j, this.position);
		if(movedRight) this.position++;
		else this.position--;
		return movedRight ? "right" : "left";
	}


}

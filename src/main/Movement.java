package main;

public class Movement {
	
	public int paddlePos;
	public int column;
	public int row;
	public String direction;

	public Movement(int column, int row, int paddlePos, String movement) {
		this.column = column;
		this.row = row;
		this.paddlePos = paddlePos;
		this.direction = movement;
	}
}

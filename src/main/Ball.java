package main;

public class Ball {
	
	public int column;
	public int row;
	
	
	public Ball(int start) {
		this.column = start;
		this.row = -1;
	}
	
	public void move() {
		this.row++;
	}

}

package main;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;  // Import the File class
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;  // Import the IOException class to handle errors

public class PaddleBrain {
	
	private double[][][] chanceToMoveRight;
	private double[][][] chanceToStayIdle;

	private int[][][] timesOnThisPosition;
	private int MIN_POSITION = 0;
	private int MAX_POSITION;
	
	private String MEMORY_FILENAME = "paddle-memory.txt";
	private Boolean hasMemory = true;
	
	public  PaddleBrain(int height, int length) {
		this.MAX_POSITION = length - 1;
		this.initializeDefaultChances(height, length);
		if(hasMemory) this.overwriteWithMemory();
	}
	
	private void initializeDefaultChances(int height, int length) {
		this.chanceToMoveRight = new double[height][length][length];
		this.chanceToStayIdle = new double[height][length][length];
		this.timesOnThisPosition = new int[height][length][length];
		for(int i =0; i < this.chanceToMoveRight.length; i++) {
			for(int j =0; j < this.chanceToMoveRight[i].length; j++) {
				for(int pos =0; pos < this.chanceToMoveRight[i][j].length; pos++) {
					if(pos <= this.MIN_POSITION) this.chanceToMoveRight[i][j][pos] = 1;
					else if(pos >= this.MAX_POSITION) this.chanceToMoveRight[i][j][pos] = 0;
					else this.chanceToMoveRight[i][j][pos] = 0.5;
					this.timesOnThisPosition[i][j][pos] = 2;
					this.chanceToStayIdle[i][j][pos] = 0.5;
				}	
			}	
		}
	}
	
	private void overwriteWithMemory() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(MEMORY_FILENAME));
		    String line =  br.readLine();
		    while ((line = br.readLine()) != null) {
		        String[] input = line.split(" ");
		        int i = Integer.parseInt(input[0]);
		        int j = Integer.parseInt(input[1]);
		        int pos = Integer.parseInt(input[2]);
		        double chance = Double.parseDouble(input[3]);
		        double idleChance = Double.parseDouble(input[4]);
		        int times = Integer.parseInt(input[5]);
		        this.chanceToMoveRight[i][j][pos] = chance;
		        this.timesOnThisPosition[i][j][pos] = Math.min(times, 2);
//		        this.timesOnThisPosition[i][j][pos] = times;
				this.chanceToStayIdle[i][j][pos] = idleChance;

		    }
		    br.close();
		} catch(Exception e) {
//	        System.out.println("An error occurred.");
	        e.printStackTrace();
		}
	}
	
	public Boolean shouldMoveToTheRight(int i, int j, int pos) {
		return Math.random() < this.chanceToMoveRight[i][j][pos];
	}
	
	public Boolean shouldStayIdle(int i, int j, int pos) {
		return Math.random() < this.chanceToStayIdle[i][j][pos];
	}
	
	public void rewardMovements(ArrayList<Movement> movements, int n) {
		this.learnFromMovements(movements, false);
	}
	
	public void punishMovements(ArrayList<Movement> movements, int n) {
		this.learnFromMovements(movements, true);
	}
	
	private void learnFromMovements(ArrayList<Movement> movements, Boolean isPunishment) {
		for (int i = 0; i < movements.size(); i++) {
			Movement mov = movements.get(i);
			int col = mov.column;
			int row = mov.row;
			int pos = mov.paddlePos;
			this.timesOnThisPosition[row][col][pos]++;
			if(mov.direction == "idle") {
				int currentValue = this.getValueForIdle(isPunishment);
				int times = this.timesOnThisPosition[row][col][pos];
				this.chanceToStayIdle[row][col][pos] = this.incrementalAvg(this.chanceToStayIdle[row][col][pos], currentValue, times);
			} else {
				int currentValue = this.getValueForMovement(mov.direction, isPunishment);
				if(pos == this.MAX_POSITION || pos == this.MIN_POSITION) continue;
				int times = this.timesOnThisPosition[row][col][pos];
				this.chanceToMoveRight[row][col][pos] = this.incrementalAvg(this.chanceToMoveRight[row][col][pos], currentValue, times);
			}

	    }
	}
	
	
	
	private int getValueForMovement(String direction, Boolean isPunishment) {
		Boolean isToTheRight = direction == "right";
		if(isPunishment) return isToTheRight ? 0 : 1;
		return isToTheRight ? 1 : 0;
	}
	
	private int getValueForIdle(Boolean isPunishment) {
		if(isPunishment) return 0;
		return 1;
	}
	
	public double incrementalAvg(double currentAvg, double newValue, double n) {
		return currentAvg + (newValue - currentAvg) / n;
	}
	


	public void registerChances () {
//        System.out.println("File created: ");
		String filename = this.MEMORY_FILENAME;
		this.createMemoryFile(filename);
		this.registerChancesInMemory(filename);
	}
	
	public void createMemoryFile(String filename) {
		try {
	      File myObj = new File(filename);
	      if (myObj.createNewFile()) {
//	        System.out.println("File created: " + myObj.getName());
	      } else {
//	        System.out.println("File already exists.");
	      }
	    } catch (IOException e) {
//	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
	}
	
	public void registerChancesInMemory(String filename) {
	    try {
	        FileWriter myWriter = new FileWriter(filename);
	        BufferedWriter out = new BufferedWriter(myWriter);
			out.newLine();
			for(int i =0; i < this.chanceToMoveRight.length; i++) {
				for(int j =0; j < this.chanceToMoveRight[i].length; j++) {
					for(int pos =0; pos < this.chanceToMoveRight[i][j].length; pos++) {
						out.write(i + " " + j + " " + pos + " " + this.chanceToMoveRight[i][j][pos] + " " + this.chanceToStayIdle[i][j][pos] + " " + this.timesOnThisPosition[i][j][pos]);
						out.newLine();
					}	
				}	
			}
			out.close();
//	        System.out.println("Successfully wrote to the file.");
	    } catch (IOException e) {
//	        System.out.println("An error occurred.");
	        e.printStackTrace();
	       }
	}
	
	

}

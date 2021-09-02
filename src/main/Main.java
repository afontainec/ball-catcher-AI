package main;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int GAMES_TO_PLAY = 1000;
		for(int i = 0; i < GAMES_TO_PLAY; i++) {		
			try {
				Game game = new Game();
				double successRate = game.start();
				System.out.println(successRate);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("FIN");
		


	}

}

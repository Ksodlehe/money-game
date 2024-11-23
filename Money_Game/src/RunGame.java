import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.PriorityQueue;

public class RunGame {
	public static void main(String[] args) {
		Game game = new Game(); 
		// I chose to use two large prime numbers simply to show that the code works even if the rounds and players are 
		// not multiples of one another.
		int players = 10; // Denoted as p
		int rounds = 15; // Denoted as n
		
		run(players, rounds);
	}
	
	/**
	 * Please note that if you have a really high player and round count, the brute force method will begin to fail.
	 * If you would like to learn why this happens please read the documentation at {@linkplain Game#runNRounds(PriorityQueue, int)}
	 */
	private static void run(int players, int rounds) {
		Game game = new Game();
		System.out.println("Number of players: " + players);
		System.out.println("Number of rounds : " + rounds);
		
		// Note that the generated player queue will always be the exact same.
		PriorityQueue<Player> pq = game.genPlayerQueue(players);
		//write(pq, "text2");
		// BRUTE FORCE
		System.out.println("\nBrute force method:");
		System.out.println("--------------------------------------------------------------------");
		// Simulate n rounds on player queue
		long start = System.nanoTime();
		PriorityQueue<Player> tmp = game.runNRounds(pq, rounds); // O(nlogn) (?)
		long end = System.nanoTime();
		printMaxMin(tmp);
		
		System.out.printf("End of brute force calculation. Time taken = %.5f ms\n", (double) (end - start)/1000000 );
		System.out.println("--------------------------------------------------------------------\n");
		
		// MY ALGORITHM
		System.out.println("Using Math algorithms to determine the winner:");
		System.out.println("--------------------------------------------------------------------");
		// Generate player queue
		pq = game.genPlayerQueue(players);
		
		// Get the winner
		start = System.nanoTime();
		Player winner = game.getWinner(pq, rounds); // O(p) worst
		end = System.nanoTime();
		System.out.printf("Time to find winner = %.5f ms\n",(double) (end - start)/1000000 );
		
		// Get the amount the winner wins
		start = System.nanoTime();
		double amt_won = game.getMoney(pq, winner, rounds); // O(n / p) best, worst, expected
		end = System.nanoTime();
		System.out.printf("Time to find money  = %.5f ms\n",(double) (end - start)/1000000 );
		
		// Print out the results
		System.out.println("\nResult:");
		System.out.printf("The winner after %s rounds is %s with %.10f dollars!\n",rounds, winner.getName(), amt_won);
		System.out.println("--------------------------------------------------------------------");
		
		// Time expectancy for brute force    : O(n log(n))
		// Total time expectancy for my method: O(p + n / p)
	}
	
	private static void write(PriorityQueue<Player> pq, String filename) {
		try {
			File f = new File("../" + filename + ".txt");
			if(!f.exists()) f.createNewFile();
			
			FileOutputStream fos = new FileOutputStream(f);
			DataOutputStream dos = new DataOutputStream(fos);
			
			while(!pq.isEmpty()) {
				String line = pq.poll().toString() + "\n";
				dos.writeUTF(line);
			}
		}
		catch(Exception e) {
			
		}
	}
	
	private static void printMaxMin(PriorityQueue<Player> pq) {
		PriorityQueue<Player> pqc = new PriorityQueue<Player>(pq);
		System.out.println( "Max: " + pqc.poll().toString() );
		while (pqc.size() > 1)
			pqc.poll();
		if(pq.size() >= 2)
			System.out.println( "|=> Min: " + pqc.poll().toString() + "\n");
	}
	
	private static void printQueue(PriorityQueue<Player> pq) {
		PriorityQueue<Player> pqc = new PriorityQueue<Player>(pq);

		System.out.println("-------------------------------------");
		while(!pqc.isEmpty())
			System.out.println( pqc.poll().toString() );

		System.out.println("-------------------------------------\n");
	}
}

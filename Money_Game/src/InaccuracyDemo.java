import java.util.PriorityQueue;

public class InaccuracyDemo {
	public static void main(String[] args) {
		int num_players = 5_000;
		int num_rounds = 1_078;
		
		//printAccuracyDiscrepancy(num_players, num_rounds);
		printLastNPlayersDiscrepancy(num_players, num_rounds, 5);
	}
	
	public static void printAccuracyDiscrepancy(int num_players, int num_rounds) {
		Game game = new Game();
		
		System.out.printf("Running %s rounds on %s players:\n", num_rounds, num_players);
		PriorityQueue<Player> players = game.genPlayerQueue(num_players);
		
		double init_sum = getSum(players);
		double after_sum = getSum( game.runNRounds(players, num_rounds) );
				
		String txt1 = "  Sum of player money before running any rounds";
		String txt2 = "  Sum of player money after running " + num_rounds + " rounds";
		String before = String.format("%-"+txt2.length()+"s = %s", txt1, init_sum);
		String after = String.format("%-46s = %s", txt2, after_sum);
		String spacer = after.length() >= before.length() ? "-".repeat(after.length() + 2) : "-".repeat(before.length() + 2);
		String accuracy = String.format("  Total accuracy = %.14f%% ", 100 * (double) (after_sum / init_sum));
		
		System.out.println(spacer);
		System.out.println(before);
		System.out.println(after);
		System.out.println(accuracy);
		System.out.println(spacer);
	}
	
	public static void printLastNPlayersDiscrepancy(int num_players, int num_rounds, int n) {
		Game game = new Game();
		PriorityQueue<Player> players = game.genPlayerQueue(num_players);
		
		players = game.runNRounds(players, num_rounds - 2);
		System.out.printf("Last %s players after running %s rounds with %s players:\n", n, num_rounds - 2, num_players);
		System.out.println( lastNPlayers( players, n ) );
		
		players = game.runRound(players);
		System.out.printf("\nLast %s players after running %s rounds with %s players:\n", n, num_rounds - 1, num_players);
		System.out.println( lastNPlayers( players, n ) );
		
		players = game.runRound(players);
		System.out.printf("\nLast %s players after running %s rounds with %s players:\n", n, num_rounds, num_players);
		System.out.println( lastNPlayers( players , n ) );		
		
		players = game.runRound(players);
		System.out.printf("\nLast %s players after running %s rounds with %s players:\n", n, num_rounds + 1, num_players);
		System.out.println( lastNPlayers( players, n ) );
		
		players = game.runRound(players);
		System.out.printf("\nLast %s players after running %s rounds with %s players:\n", n, num_rounds + 2, num_players);
		System.out.println( lastNPlayers( players, n ) );
	}
	
	private static String lastNPlayers(PriorityQueue<Player> players, int number) {
		PriorityQueue<Player> pq = new PriorityQueue<>(players);
		String s = "";
		int i = pq.size();
		while( !pq.isEmpty() ) {
			if( i-- <= number ) {
				s += pq.peek().toString() + "\n";
			}
			pq.poll();
		}
		return s.substring(0, s.length() - 1);
	}

	private static double getSum(PriorityQueue<Player> players) {
		PriorityQueue<Player> clone = new PriorityQueue<>(players);
		double sum = 0;
		while( !clone.isEmpty() ) {
			sum += clone.poll().getMoney();
		}
		return sum;
	}
	
	private static void printMinPlayer(PriorityQueue<Player> players) {
		PriorityQueue<Player> clone = new PriorityQueue<>(players);
		Player p = null;
		while( !clone.isEmpty() ) {
			p = clone.poll();
		}
		System.out.println("Player with least amount of money: " + p.toString());
		System.out.println("Amount of money: " + p.getMoney());
	}
}

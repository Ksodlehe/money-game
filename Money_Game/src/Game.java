import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

public class Game {
	private final long seed;	
	
	/**
	 * 	Lets you set the seed used in the randomness function.
	 * 	<p>
	 *  This class aims to simulate two alternative method of running a set amount of rounds in a proposed game. 
	 *  <br>
	 *  The first of these methods is the brute-force method:
	 *  <ul> <li> {@linkplain #runNRounds(PriorityQueue, int)} </li> </ul>
	 *  
	 *  The second of these methods is my own algorithms
	 *  <ul>
	 *  	<li> {@linkplain #getWinner(PriorityQueue, int)} </li>
	 *  	<li> {@linkplain #getMoney(PriorityQueue, Player, int)} </li>
	 *  </ul>
	 *  
	 *  The game has one simple rule: <br>
	 *  Given a list of players, each of whom has a given amount of money, the must all give HALF of said
	 *  money to the player that has the least amount of money.
	 *  <p>
	 *  Example: <br>
	 *  Let {@code a = 0, b = 1, c = 2}. ie) <br>
	 *  Round 0: {@code a = 0, b = 1, c = 2} On round 1 we know that c and b give half their money to a, which gives us <br>
	 *  Round 1: {@code a = 1.5, b = 0.5, c = 1}. <br>
	 *  Round 2: {@code a = 0.75, b = 1.75, c = 0.5} <br>
	 *  Round 3: {@code a = 0.375, b = 0.875, c = 1.75} <br>
	 *  and so on.
	 * 
	 * @param seed A seed for the class. Only affects {@linkplain #genPlayerQueue(int)}
	 */
	public Game(long seed) {
		this.seed = seed;
	}
	
	/**
	 *  This class aims to simulate two alternative method of running a set amount of rounds in a proposed game. 
	 *  <br>
	 *  The first of these methods is the brute-force method:
	 *  <ul> <li> {@linkplain #runNRounds(PriorityQueue, int)} </li> </ul>
	 *  
	 *  The second of these methods is my own algorithms
	 *  <ul>
	 *  	<li> {@linkplain #getWinner(PriorityQueue, int)} </li>
	 *  	<li> {@linkplain #getMoney(PriorityQueue, Player, int)} </li>
	 *  </ul>
	 * 
	 *  The game has one simple rule: <br>
	 *  Given a list of players, each of whom has a given amount of money, the must all give HALF of said
	 *  money to the player that has the least amount of money.
	 *  <p>
	 *  Example: <br>
	 *  Let {@code a = 0, b = 1, c = 2}. ie) <br>
	 *  Round 0: {@code a = 0, b = 1, c = 2} On round 1 we know that c and b give half their money to a, which gives us <br>
	 *  Round 1: {@code a = 1.5, b = 0.5, c = 1}. <br>
	 *  Round 2: {@code a = 0.75, b = 1.75, c = 0.5} <br>
	 *  Round 3: {@code a = 0.375, b = 0.875, c = 1.75} <br>
	 *  and so on.
	 *  
	 *  @see #Game(long)
	 */
	public Game() {
		this.seed = 1l;
	}
	
	// MY METHODS OF DETERMINING WINNER + MONEY:
	/**
	 * This method will find the winning player in the provided heap after a given amount of rounds.
	 * 
	 * <p>
	 * Logic: <br>
	 * This method works under the simple observation that after each round, the players in the min heap shift
	 * where the lowest player goes to the top, and every other player goes down one.
	 * <p>
	 * Time: O(p) worst <br>
	 * Space: O(1) <br>
	 * where p = number of players in the heap.
	 * @param player_queue Your max heap of players.
	 * @param rounds The number of rounds run
	 * @return The winning player
	 * @see #getMoney(PriorityQueue, Player, int)
	 */
	public Player getWinner(PriorityQueue<Player> player_queue, int rounds) {
		if (player_queue.size() == 0) return null;
		if (rounds <= 0) return player_queue.peek();
		/// number of players
		int players = player_queue.size();
		
		// Winner is the player in v THIS v position in the queue.
		int winner_index = (players - rounds % players) % players;
		/* 
		 * ^^^ This single line is what makes getting an O(player) time possible.
		 * The reason for this is that essentially each round shifts the person with the most amount of money to 
		 * the player with the second most, where after [l*player] rounds the order of players by money is the same as if nothing happened.
		 * that means that after [k] rounds, the player with the most amount of money will be the [k]th player in the queue. Thus to calculate
		 * who the winner after n rounds is, we need to find the nth richest player.
		 * 
		 * This will be explained more in a visualization.
		 */
		
		return getQueueIndex(player_queue, winner_index);
	}
	
	/**
	 * This method runs the {@linkplain #getSum(PriorityQueue)} method, however if you already KNOW the sum,
	 * please use {@linkplain #getMoney(Player, int, double, int)}, as it is much faster (especially for a
	 * large number of players, because re-running the sum takes O(p) best)
	 * 
	 * <p>
	 * Logic: <br>
	 * In the brute force method we know that for each round all the players have half their money given to
	 * the player in last place. However an important insight to note is that you DONT need to manually sum
	 * each players money every round; in fact, there is a very simple algorithm to find the final money after
	 * n rounds. It goes as follows: <p>
	 * If we had 10 player, 7 rounds, then from {@linkplain #getWinner(PriorityQueue, int)} we know that the winner
	 * is the 6th player from the bottom of the heap. This also means that there are 6 rounds where this player has
	 * their money halved, and one round where they get half of all players money (ie. sum / 2). Thus we can calculate
	 * this players final money to be <br>
	 * {@code final_money = (initial_money / 2^6) + sum/2} <br>
	 * from this, we can use a little bit of insight to realize that for any number of players (p) and rounds (n),
	 * we can calculate the final money of a player to be something along the lines of <br>
	 * <pre>
	 * t = n mod(p)  <- [n % p]
	 * money = (money / 2^t) + (sum / 2)
	 * n = n - t
	 * </pre>
	 * The above makes it so rounds (n) is a multiple of the players (p).
	 * <pre>
	 * while(n > 0){
	 *    money = (money / 2^p) + (sum / 2)
	 *    n = n - p
	 * }
	 * </pre>
	 * And this loops until the sum from all the rounds has been calculated.
	 * 
	 * <p>
	 * Time to sum = O(p) best, worst, expected <br>
	 * Time to calculate money = O(n / p) = O(k) worst <br>
	 * Space = O(1) <br>
	 * where p = number of players in the heap, and n = number of rounds run.
	 * @param player_queue Your max heap of players.
	 * @param winner The winning player gotten from {@linkplain #getWinner(PriorityQueue, int)}
	 * @param rounds The number of rounds to be run
	 * @return The amount money the winning player will have after the given number of rounds
	 * @see #getMoney(Player, int, double, int)
	 * @see #getWinner(PriorityQueue, int)
	 */
	public double getMoney(PriorityQueue<Player> player_queue, Player winner, int rounds) {
		double sum = getSum(player_queue);
		int players = player_queue.size();
		
		return getMoney(winner, players, sum, rounds);
	}
	/**
	 * This method 
	 * <p>
	 * Logic: <br>
	 * In the brute force method we know that for each round all the players have half their money given to
	 * the player in last place. However an important insight to note is that you DONT need to manually sum
	 * each players money every round; in fact, there is a very simple algorithm to find the final money after
	 * n rounds. It goes as follows: <p>
	 * If we had 10 player, 7 rounds, then from {@linkplain #getWinner(PriorityQueue, int)} we know that the winner
	 * is the 6th player from the bottom of the heap. This also means that there are 6 rounds where this player has
	 * their money halved, and one round where they get half of all players money (ie. sum / 2). Thus we can calculate
	 * this players final money to be <br>
	 * {@code final_money = (initial_money / 2^6) + sum/2} <br>
	 * from this, we can use a little bit of insight to realize that for any number of players (p) and rounds (n),
	 * we can calculate the final money of a player to be something along the lines of <br>
	 * <pre>
	 * t = n mod(p)  <- [n % p]
	 * money = (money / 2^t) + (sum / 2)
	 * n = n - t
	 * </pre>
	 * The above makes it so rounds (n) is a multiple of the players (p).
	 * <pre>
	 * while(n > 0){
	 *    money = (money / 2^p) + (sum / 2)
	 *    n = n - p
	 * }
	 * </pre>
	 * And this loops until the sum from all the rounds has been calculated.
	 * 
	 * <p>
	 * Time = O(n / p) = O(k) worst <br>
	 * Space = O(1) <br>
	 * where p = number of players in the heap, and n = number of rounds run. We can always calculate p rounds at the
	 * same time because we already made the number of rounds a multiple of p.
	 * 
	 * @param winner The winning player gotten from {@linkplain #getWinner(PriorityQueue, int)}
	 * @param num_players The number of players in the heap
	 * @param sum_money The sum of all players money
	 * @param rounds The number of rounds to run
	 * @return The amount money the winning player will have after the given number of rounds
	 * @see #getMoney(PriorityQueue, Player, int)
	 */
	public double getMoney(Player winner, int num_players, double sum_money, int rounds) {
		double winner_money = winner.getMoney();
		
		// If the number of rounds is not a valid amount, return the amount of money the player already has.
		if(rounds <= 0) return winner_money;
		
		/*
		 * The main logic behind this is that, for any n rounds, because the players essentially just shift
		 * around whos winning, we know that for a subset of k rounds, a player p will have their money divided 
		 * a set amount of times until they reach the lowest amount of money, then they will get half the sum added
		 * to their money, and this repeats.
		 * 
		 * We can use this fact to realize that if we KNEW the winning player, and their starting money, then we could
		 * calculate their ending money using the knowledge that until the players position = 0 we divide their money, 
		 * and that once it IS 0 we add half the sum, and repeat. This completely eliminates the need to check every single
		 * players money each round, and allows you to compress the algorithm in a much faster way, which is shown below.
		 */

		// For any rounds = n*players + c, we do the following to make it so rounds = n * players.
		// This makes it so we can calculate [player] number of rounds at the same time.
		int leftover = rounds % num_players;
		rounds -= leftover;
		
		// If leftover = 0 then the player gets the sum of money added twice, so only execute this if it ISNT 0 (and also not somehow negative).
		if (leftover > 0) {
			// Dividing money by 2^n is the same as running n rounds where this specific player is not the one with the least amount of money
			winner_money /= Math.pow(2, leftover);
			// Adding sum/2 is the same as running the round where this player IS the one with the least money.
			winner_money += sum_money/2;
		}
		
		// loop round/players times, repeating the above steps each time except with leftover -> players.
		for(;rounds > 0; rounds -= num_players) {
			winner_money /= Math.pow(2, num_players);
			winner_money += sum_money/2;
		}
		return winner_money;
	}
	
	// This method is essentially redundant. In a real application we would actually calculate the sum of the 
	// money of all players while finding the winner. That would be done where instead of ending the while loop
	// as soon as we find the winning player, we continue until we have seen every player and add up the sum
	// of their money.
	/**
	 * Acquire the sum of player money in the heap.
	 * <p>
	 * It should be noted that in a real implementation, we would completely remove this and instead calculate the
	 * sum while getting the player in {@linkplain #getWinner(PriorityQueue, int)}. That is, instead of ending 
	 * the {@code while} loop early as SOON as we find the winner, we would instead continue the {@code while} 
	 * loop until all players have been seen, summing each of their money and saving it to a single {@code sum}
	 * variable. Doing this would increase the time from O(p) worst to O(p) web , but thats 
	 * better than having an O(p) worst method run, and then running a second O(p) web method.
	 * <p>
	 * Time: O(p) web (worst, expected, best) <br>
	 * where p = number of players in heap.
	 * 
	 * @param player_queue
	 * @return The sum of money for all players
	 */
	public double getSum(PriorityQueue<Player> player_queue) {
		// Clones the current priority queue so it is not harmed.
		PriorityQueue<Player> pq_clone = new PriorityQueue<Player>(player_queue);
		double sum = 0;
		while(!pq_clone.isEmpty())
			sum += pq_clone.poll().getMoney();
		return sum;
	}
	
	// BRUTE FORCE METHOD
	// Simulate a given number of rounds by looping over a priority queue a bunch of times.
	/**
	 * Manually simulate every single round up to and including round n.
	 * 
	 * <p>
	 * DISCLAIMER:<br>
	 * This method actually BREAKS if you have a very high number of players and rounds (in my testing it first
	 * breaks with 2003 players and >1079 rounds) <br>
	 * The REASON this happens, is that the brute force method needs to manually divide all the players money
	 * by two and then re-add it to said queue. However, if anything prevents the priority queue from comparing
	 * two players it just simply wont add one. And what this results in is because the divisions end up making 
	 * the players money approach the Double.minValue() [You can see this by exporting the queues contents to a
	 * text file after running it for a load of rounds], then eventually java cant properly divide said values by
	 * two, and they end up moving up or down in the queue due to floating point errors.
	 * 
	 * @param player_queue The heap of players
	 * @param rounds Number of rounds to be run
	 * @return The updated heap
	 */
	public PriorityQueue<Player> runNRounds(PriorityQueue<Player> player_queue, int rounds) {
		if (player_queue.size() == 0) return null;
		if (rounds <= 0) return null;
		
		PriorityQueue<Player> pq_clone = new PriorityQueue<Player>(player_queue);
		
		for(long i = 0; i < rounds; i++) {
			// Run the current round
			pq_clone = runRound(pq_clone);
		}
		return pq_clone;
	}
	
	// Run a single round.
	/**
	 * Simulate a single round of the game.
	 * 
	 * @param player_queue The player heap
	 * @return The updated player heap.
	 */
	public PriorityQueue<Player> runRound(PriorityQueue<Player> player_queue) {
		PriorityQueue<Player> updated_queue = new PriorityQueue<Player>(new PlayerComp());
		PriorityQueue<Player> old_queue = new PriorityQueue<Player>(player_queue);
		
		double money = 0;
		double pmoney = 0;
		
		// Iterate over pq
		while(! old_queue.isEmpty() ) {
			// Get current player and divide their money by half
			Player p = old_queue.poll();
			pmoney = p.getMoney() / 2;
			p.remMoney(pmoney);
			money += pmoney;
			
			// If the player is last in the queue (ie. has the least money) add the money sum to them.
			// Note: although the question says we DONT divide the player with the least amount of money by 1/2,
			// its ok if we do so because the money is re-added here.
			if( old_queue.size() == 0) {
				p.addMoney(money);
				money = 0;
			}
			// Add each player to a new updated queue so we can run another round on it.
			updated_queue.add(p);
		}
		
		return updated_queue;
	}
	
	// Simple method to return the player at a given index without interfering with queue
	/**
	 * Obtain the player at a specified index of a heap without wrecking said heap.
	 * 
	 * @param player_queue The player heap
	 * @param index Index you want to return
	 * @return The player at the specified index
	 */
	public Player getQueueIndex(PriorityQueue<Player> player_queue, int index) {
		PriorityQueue<Player> pq_clone = new PriorityQueue<Player>(player_queue);
		
		int i = 0;
		// iterate over the heap.
		while( !pq_clone.isEmpty() ) {
			// If the current index == index, return the winner.
			if (i++ == index)
				return pq_clone.poll();
			pq_clone.poll();
		}
		// If there is no player at the specified index, return null;
		return null;
		
	}
	
	// This generates a priority queue that is ALWAYS THE SAME.
	/**
	 * Generate a heap with 'random' money values for all the players inside. <br>
	 * It should be noted that this method will always return the same queue no matter 
	 * how many times its ran, and that is because although the money values for each 
	 * player is randomized, its entirely based upon the seed used to initialize this
	 * class. As such, each time this method is ran, a new random function is initialized
	 * using the seed, which means the random function will always give the same results. 
	 * 
	 * @param num_players The number of players you want the heap to have
	 * @return A max heap of players based on the amount of money they have.
	 */
	public PriorityQueue<Player> genPlayerQueue(int num_players) {
		// Use random numbers to generate values for player money. Set seed
		Random rnd = new Random();
		rnd.setSeed(seed);
		
		PriorityQueue<Player> pq = new PriorityQueue<>(new PlayerComp());
		// The reason I purposefully prevent players from having the same amount of money is that
		// it seems to make both my method and the brute force method wildly inconsistent between runs,
		// as the heap will order two players differently between runs if they have the same money.
		Map<Integer, Boolean> seen = new HashMap<>();
		Integer money = 0;
		for (int i = 0; i < num_players; i++) {
			money = rnd.nextInt(0, 10 * num_players);
			
			while(seen.getOrDefault(money, false))
				money = rnd.nextInt(0, 10 * num_players);
			seen.put(money, true);
			
			pq.add( new Player("Player " + i, money));
		}
		return pq;
	}
}

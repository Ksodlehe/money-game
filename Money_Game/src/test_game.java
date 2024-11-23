import static org.junit.jupiter.api.Assertions.*;

import java.util.PriorityQueue;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class test_game {
	Game game = new Game();

	static Random rnd = new Random();
	@BeforeAll
	static void init() {
		rnd.setSeed(0);
	}
	
	/**
	 * Check that all the methods in the player class work. <br>
	 * Why: To ensure that all operations used in testing work.
	 */
	@Test
	void test_player_methods() {
		Player a = new Player("a", 0);
		Player b = new Player("a", 0);
	
		a.addMoney(1.75);
				
		assertEquals(a.getName(), "a");
		assertEquals(a.getMoney(), 1.75);
		
		a.remMoney(1.75);
		
		assertEquals(a.getMoney(), 0);
		assertTrue(a.equals(b));
	}
	
	/**
	 * Check that the player comparator works as expected. <br>
	 * Why: Otherwise any heap that uses it will give us unexpected results.
	 */
	@Test
	void test_player_comparator() {
		Player a = new Player("a", 0);
		Player b = new Player("b", 1);
		Player c = new Player("c", 1);
		
		PlayerComp pc = new PlayerComp();
		
		assertEquals(pc.compare(a, b), 1);
		assertEquals(pc.compare(b, a), -1);
		assertEquals(pc.compare(c, b), 0);
	}
	
	/**
	 * Check that that the index of a specific queue gotten by a method is the same as what we would expect. <br>
	 * Why: The {@link Game#getWinner(PriorityQueue, int)} method uses the {@link Game#getQueueIndex(PriorityQueue, int)} method.
	 */
	@Test
	void test_queue_index() {
		PriorityQueue<Player> player_queue = new PriorityQueue<>(new PlayerComp());
		
		player_queue.add( new Player("a", 0) );
		player_queue.add( new Player("b", 1) );
		player_queue.add( new Player("c", 2) );
		player_queue.add( new Player("d", 3) );
		player_queue.add( new Player("e", 4) );
		
		int index = 1;
		boolean index_equals_d = game.getQueueIndex(player_queue, index).getMoney() == 3;
		
		assertTrue(index_equals_d);
	}
	
	/**
	 * Check that two queues of equal size are equivalent if generated back-to-back <br>
	 * Why: To show that our next tests will be correct regardless of queue generation
	 */
	@Test
	void test_generate_equal_queues() {
		int queue_size = 27;
		PriorityQueue<Player> player_queue_1 = game.genPlayerQueue(queue_size);
		PriorityQueue<Player> player_queue_2 = game.genPlayerQueue(queue_size);
		
		// Check that every index in queue have the same elements
		for(int i = 0; i < queue_size; i++) {
			Player player_1 = player_queue_1.poll();
			Player player_2 = player_queue_2.poll();
			assertTrue(player_1.equals(player_2));
		}
	}
	
	/**
	 * Check that two generated queues of equal size are also equal, even if another queue is generated between them <brs>
	 * Why: To show that our next tests will be correct regardless of queue generation
	 */
	@Test
	void test_generate_equal_queues_2() {
		int queue_size = 27;
		int burn_size = 10;
		PriorityQueue<Player> player_queue_1 = game.genPlayerQueue(queue_size);
		
		// Burn a queue generation
		game.genPlayerQueue(burn_size);
		
		PriorityQueue<Player> player_queue_2 = game.genPlayerQueue(queue_size);
		
		// Check that every index in queue have the same elements
		for(int i = 0; i < queue_size; i++) {
			Player player_1 = player_queue_1.poll();
			Player player_2 = player_queue_2.poll();
			assertTrue(player_1.equals(player_2));
		}
	}

	@Test
	void test_brute_run_one_round() {
		PriorityQueue<Player> player_queue = new PriorityQueue<>(new PlayerComp());
		
		player_queue.add( new Player("a", 0) );
		player_queue.add( new Player("b", 1) );
		player_queue.add( new Player("c", 2) );
		player_queue.add( new Player("d", 3) );
		player_queue.add( new Player("e", 4) );		
		
		Player winner = game.runNRounds(player_queue, 1).peek();
		
		assertTrue( winner.getName().equals("a") );
	}
	
	@Test
	void test_brute_run_two_rounds() {
		PriorityQueue<Player> player_queue = new PriorityQueue<>(new PlayerComp());
		
		player_queue.add( new Player("a", 0) );
		player_queue.add( new Player("b", 1) );
		player_queue.add( new Player("c", 2) );
		player_queue.add( new Player("d", 3) );
		player_queue.add( new Player("e", 4) );		
		
		Player winner = game.runNRounds(player_queue, 2).peek();
		
		assertTrue( winner.getName().equals("b") );
	}
	
	@Test
	void test_brute_run_eleven_rounds() {
		PriorityQueue<Player> player_queue = new PriorityQueue<>(new PlayerComp());
		
		player_queue.add( new Player("a", 0) );
		player_queue.add( new Player("b", 1) );
		player_queue.add( new Player("c", 2) );
		player_queue.add( new Player("d", 3) );
		player_queue.add( new Player("e", 4) );		
		
		Player winner = game.runNRounds(player_queue, 11).peek();
		
		assertTrue( winner.getName().equals("a") );
	}
	
	@Test
	void test_zero_rounds() {
		// Brute force method doesnt work for 0 rounds so I just compare my method to poll
		int queue_size = 10;
		PriorityQueue<Player> player_queue = game.genPlayerQueue(queue_size);
		
		Player winner = game.getWinner(player_queue, 0);
		
		assertTrue( winner.equals(player_queue.poll()) );
	}
	
	@Test
	void test_compare_one_rounds() {
		int queue_size = 10;
		int rounds = 1;
		PriorityQueue<Player> player_queue = game.genPlayerQueue(queue_size);
		PriorityQueue<Player> player_queue_brute = game.genPlayerQueue(queue_size);
		
		Player winner = game.getWinner(player_queue, rounds);
		double winner_money = game.getMoney(player_queue, winner, rounds);
		winner.setMoney(winner_money);
		
		player_queue_brute = game.runNRounds(player_queue_brute, rounds);
		Player winner_brute = player_queue_brute.peek();
		
		// The reason I compare the names rather than using .equals on the players themselves is because there can end up being
		// floating point errors in my method, and that would make the equals command return as false. So, instead I check the names.
		assertTrue( winner.getName().equals(winner_brute.getName()) );
	}
	
	@Test
	void test_compare_eleven_rounds() {
		int queue_size = 10;
		int rounds = 11;
		PriorityQueue<Player> player_queue = game.genPlayerQueue(queue_size);
		PriorityQueue<Player> player_queue_brute = game.genPlayerQueue(queue_size);
		
		Player winner = game.getWinner(player_queue, rounds);
		double winner_money = game.getMoney(player_queue, winner, rounds);
		winner.setMoney(winner_money);
		
		player_queue_brute = game.runNRounds(player_queue_brute, rounds);
		Player winner_brute = player_queue_brute.peek();
		
		assertTrue( winner.getName().equals(winner_brute.getName()) );
	}
	
	@Test
	void test_compare_random_rounds() {
		int queue_size = 10;
		int rounds = rnd.nextInt(0, 10000); // Upper limit bc brute takes a very long time.
		
		PriorityQueue<Player> player_queue = game.genPlayerQueue(queue_size);
		PriorityQueue<Player> player_queue_brute = game.genPlayerQueue(queue_size);
		
		Player winner = game.getWinner(player_queue, rounds);
		double winner_money = game.getMoney(player_queue, winner, rounds);
		winner.setMoney(winner_money);
		
		player_queue_brute = game.runNRounds(player_queue_brute, rounds);
		Player winner_brute = player_queue_brute.peek();
		
		assertTrue( winner.getName().equals(winner_brute.getName()) );
	}
	
	@Test
	void test_money() {
		
	}
}

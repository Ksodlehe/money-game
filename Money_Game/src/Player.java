
public class Player {
	private double money;
	private String name;
	Player(String name, double money){
		this.money = money;
		this.name = name;
	}
	
	double setMoney(double amt) {
		return money = amt;
	}
	double addMoney(double amt) {
		return money += amt;
	}
	double remMoney(double amt) {
		return money -= amt;
	}
	
	double getMoney() {
		return money;
	}
	String getName() {
		return name;
	}
	
	boolean equals(Player p) {
		// If name and money are equal
		if(this.getMoney() == p.getMoney() && this.getName().equals(p.getName())) 
			return true;
		return false;
	}
	
	public String toString() {
		return name + " - " + money;
	}
}

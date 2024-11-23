import java.util.Comparator;

public class PlayerComp implements Comparator<Player>{
	public int compare(Player o1, Player o2) {
		if(o1.getMoney() < o2.getMoney())
			return 1;
		else if (o1.getMoney() > o2.getMoney())
			return -1;
		return 0;
	}
}

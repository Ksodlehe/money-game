
# The Money Game
The money game is a very simplistic game, where every round each player must give half their money to the player in last (ie. player that has the least money). The winner of the money game is the player that has the most money at the end of the end of the rounds.

## Determining the Winner
### Simple Method
The simplest way to calculate the winner is to simply loop through a heap of all the players each round, summing up half their money and then adding said money to the player in last. We then repeat n times until we have simulated all the rounds. This method, however, is extremely slow, and there is a much better method to determining the winner.

### The Better Method
A much better way of finding the winner is to notice that each round essentially shifts the players in the heap down by 1 (except the player in last, which goes to the top). This means that we can actually write a single mathematical formula to determine which index of the heap will have the winning player. To find this function, notice how the below max heap shifts:

![App Screenshot](https://github.com/Ksodlehe/money-game/blob/main/Images/Heap%20Shift.png?raw=true)

We can see (as previously stated) that the players shift down by 1. However we can also see that the winners for all rounds n > 0 appear to be the player that WAS at the 5 - n index in the original max heap. Now notice how the shifts affects a heap when n > p (number of players):

![App Screenshot](https://github.com/Ksodlehe/money-game/blob/main/Images/Heap%20Shift%202.png?raw=true)

we see here that the order of round 5 is actually equivalent to rounds 0, and that round 6 = round 1. Thus, we can say that the winner of any round n is the player located at the index 
```
(5 - n mod(5)) mod(5) <=> (5 - n%5)%5
```
Extending this to any number of players p, we get 
```
(p - n mod(p)) mod(p) <=> (p - n%p)%p
```
## Calculating the Winners Money
### Simple Method
The way of calculating the winners money is extremely simple in the simple method, as finding the winner is directly tied to finding the money they end with

### The Better Method
Because our method does not calculate any money to find the winner, we need to calculate it seperately. Luckily, this is fairly trivial. Simply notice that for any number of rounds n = c*p (where c is an integer), then the winner must be the player that STARTS with the most money. Thus, we can calculate the money they have after p rounds using
```
money = (money / 2^p) + sum/2
```
where sum is the total sum of money for all players. It should be fairly obvious that this equation is the same as running the simulation for p rounds, as the players money would get divided by 2 p times, then it would have half the total sum added to it. And because n is a multiple of p, we simply do this entire proccess n/p times inside of a while loop.

However, what if n is NOT a multiple of p? ie) what if n = c*p + k? Well then all we have to do is remove the + k by doing:
```
money = (money / 2^k) + sum/2
n = n - k
```
and then use the process previously shown to calculate the rest. It should be known that we can find k via k = n % p.
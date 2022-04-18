
package goldfish4mongus;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Gofish4Mongus {
    
    static final Random rng = new Random();
    static private ArrayList<Dealer> cards;
    static public Player[] Players;
 
    public static Dealer draw()
	{
		return cards.remove(rng.nextInt(cards.size()));
	}
 
	public static int deckSize()
	{
		return cards.size();
    }

    public static void main(String[] args) {
        cards = new ArrayList<Dealer>();
        for(int i=0;i<4;i++)
            for(Dealer c: Dealer.values())
                cards.add(c);
        Player human = new HumanPlayer();
        Player computer = new ComputerPlayer();
        Players = new Player[] {human, computer};
 
        while(Players[0].getNumSet() + Players[1].getNumSet() < 13)
        {
            Players[0].haveTurn();
            System.out.println("----------");
            Players[1].haveTurn();
            System.out.println("----------");
        }
 
        int HumanScore = Players[0].getNumSet(); int CompScore = Players[1].getNumSet();
        if (HumanScore > CompScore)
            System.out.println("Congratulations, you win "+ HumanScore + " to "+ CompScore +"!");
        else if (CompScore > HumanScore)
            System.out.println("Sorry, the computer has beaten you: "+ HumanScore + " to "+ CompScore);
        else
            System.out.println("Tie game: "+HumanScore+" each!");
    }
}
 
enum Dealer
{
    ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING;
}
 
abstract class Player
{
    protected ArrayList<Dealer> hand = new ArrayList<Dealer>();
    private int numSet;
 
    public Player()
    {
        for(int i=0;i<8;i++)
            fish();
    }
 
    public boolean drawCards (Dealer cType)
    {
        return hand.contains(cType);
    }
 
    public ArrayList<Dealer> getRequestCards(Dealer cType)
    {
        ArrayList<Dealer> x = new ArrayList<Dealer>(); //Complicated because simply taking the cards as they
        for(int i=0;i<hand.size();i++)            //are found would mess up the traversing of the hand
            if (hand.get(i) == cType)
              x.add(hand.get(i));
        for(int c=0;c<x.size();c++)
            hand.remove(cType);
        return x;
    }
 
    protected boolean requestCards(Dealer cType)
    {
        int tmp = 0;
        if (this instanceof HumanPlayer)
            tmp = 1;
        Player other = Gofish4Mongus.Players[tmp];

        if (tmp==1)
            ((ComputerPlayer) other).queries.add(cType);

        if (other.drawCards(cType))
        {
            for(Dealer c: other.getRequestCards(cType))
                hand.add(c);
            return true;
        }
        else
        {
            return false;
        }
    }
 
    protected void fish()
	    {
	        if (Gofish4Mongus.deckSize() > 0)
	        	hand.add(Gofish4Mongus.draw());
	        else
	        	System.out.println("But that's impossible since the deck is empty.");
    }
 
    public int getNumSet()
    {
        return numSet;
    }
 
    protected Dealer checkSuit()
    {
        for(Dealer c: hand) //Not very elegant!
        {
            int num = 0;
            for(Dealer d: hand)
              if (c == d)
                  num++;
            if (num == 4)
            {
                for(int i=0;i<4;i++)
                    hand.remove(c);
                numSet++;
                return c;
            }
        }
        return null;
 
 
    }
 
    public abstract void haveTurn();
 
}
 
class HumanPlayer extends Player
{
    public void haveTurn()
    {
        Scanner scn = new Scanner(System.in);
        boolean playing = true;
        do{
            Dealer set = checkSuit();
            if(set != null)
                System.out.println("You got a set of " + set + "s!");
 
            if (hand.size() == 0)
            {
                System.out.print("Your hand is empty, you must "); //"Go fish!"
                break;
            }
            else
            {
                System.out.print("Your hand:");
                for(Dealer c: hand)
                    System.out.print(c + " ");
                System.out.println();
            }
 
            System.out.println("Ask opponent for what card?");
 
            Dealer req;
            try{
                req = Dealer.valueOf(scn.next().toUpperCase());
            }
            catch(IllegalArgumentException e){ //If what you said is not in Card
                System.out.println("Card not present in this deck. Try again:");
                continue;
            }
 
            if(!hand.contains(req))
            {
                System.out.println("You may not ask for a card you have none of. Try again:");
                continue;
            }
 
            System.out.println("You ask for a " + req);
            playing = requestCards(req); //If you get card(s), askFor returns true and loops
        } while(playing);
        System.out.println("Go fish!");
        fish();
    }
}
 
class ComputerPlayer extends Player
{
    public ArrayList<Dealer> queries = new ArrayList<Dealer>();
    private int age = 0;
 
    public void haveTurn()
    {
        boolean playing;
        do{
            Dealer set = checkSuit();
            if(set != null)
                System.out.println("Your opponent got a set of " + set + "'s");
            if (hand.size() == 0)
            {
                System.out.print("Your opponent's hand is empty.");
                break;
            }
            Dealer req = compReq();
            System.out.println("Your opponent asks for cards by the name of " + req);
            playing = requestCards(req);
            age++;
        } while(playing);
        System.out.println("Your opponent goes fishing.");
        fish();
    }
 
    private Dealer compReq()
    {
        if (age>2)
        {
            queries.remove(queries.size()-1);
            age=0;                           
        }
        for(int i=queries.size()-1; i>-1; i--)
            if (hand.contains(queries.get(i)))
            {
                return queries.remove(i);
            }                            
        return hand.get(Gofish4Mongus.rng.nextInt(hand.size()));
    }
}        

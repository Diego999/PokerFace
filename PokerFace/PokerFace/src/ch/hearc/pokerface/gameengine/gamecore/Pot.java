
package ch.hearc.pokerface.gameengine.gamecore;


public class Pot
{
	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private int stateTotal;//Somme des enchères de l'état
	private int turnTotal;//Somme total du tour
	private int bet;//Somme minimale pour que le joueur puisse continuer à jouer

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public Pot()
	{
		initialize();
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public void initialize()
	{
		stateTotal = turnTotal = bet = 0;
	}

	public void nextState()
	{
		turnTotal += stateTotal;
		stateTotal = bet = 0;
	}

	public void addStateTotal(int amount)
	{
		if(amount > bet)
		{
			bet = amount;
		}
		stateTotal += amount;
	}

	public int nextTurn()
	{
		int total = turnTotal + stateTotal;
		bet = turnTotal = stateTotal = 0;
		return total;
	}

	public void removeAmount(int amount)
	{
		turnTotal -= amount;
	}
	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	public int getStateTotal()
	{
		return stateTotal;
	}

	public int getTurnTotal()
	{
		return turnTotal;
	}

	public int getBet()
	{
		return bet;
	}
}


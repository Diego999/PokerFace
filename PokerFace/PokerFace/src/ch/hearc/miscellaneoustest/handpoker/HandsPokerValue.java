
package ch.hearc.miscellaneoustest.handpoker;

public class HandsPokerValue implements Comparable<HandsPokerValue>
{
	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private int		rank;
	private String	handName;

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public HandsPokerValue(int rank, String handName)
	{
		this.rank = rank;
		this.handName = handName;
	}

	public HandsPokerValue(HandsPokerValue handsPokerValue)
	{
		this(handsPokerValue.rank, handsPokerValue.handName);
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	/**
	 * Lower rank is better !
	 */
	@Override
	public int compareTo(HandsPokerValue other)
	{
		if (other == null || rank < other.rank) { return 1; }
		if (rank == other.rank)
		{
			return 0;
		}
		else
		{
			return -1;
		}
	}

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	public int getRank()
	{
		return this.rank;
	}

	public String getHandName()
	{
		return this.handName;
	}
}
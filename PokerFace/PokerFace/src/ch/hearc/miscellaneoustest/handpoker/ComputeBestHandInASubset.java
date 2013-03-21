
package ch.hearc.miscellaneoustest.handpoker;

import java.util.Arrays;

public class ComputeBestHandInASubset
{
	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	//Poker with 5 cards
	private static final int			LENGTH_HAND		= 5;
	private static final String			EMPTY			= "";
	private static final HandsPokerMap	HANDS_POKER_MAP	= HandsPokerMap.getInstance();

	private String[]					available_characters;
	private String						highestHand;

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public ComputeBestHandInASubset(String[] actualHand)
	{
		highestHand = "";
		available_characters = new String[actualHand.length];

		for(int i = 0; i < actualHand.length; ++i)
		{
			available_characters[i] = new String(actualHand[i]);
		}
	}

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	public String getHighestHand()
	{
		String[] hand = new String[LENGTH_HAND];
		searchHighestHand(0, 0, hand);
		return highestHand;
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private void searchHighestHand(int indexLastValueTaken, int indexCurrentHand, String[] hand)
	{
		if (indexCurrentHand < LENGTH_HAND)
		{
			for(int i = 0; i < available_characters.length; ++i)
			{
				if (available_characters[i] != EMPTY)
				{
					indexLastValueTaken = i;
					hand[indexCurrentHand] = available_characters[i];
					available_characters[i] = EMPTY;

					searchHighestHand(0, indexCurrentHand + 1, hand);

					available_characters[indexLastValueTaken] = hand[indexCurrentHand];
				}
			}
		}
		else
		{
			String[] handOrdered = new String[hand.length];
			for(int i = 0; i < LENGTH_HAND; ++i)
			{
				handOrdered[i] = hand[i];
			}

			Arrays.sort(handOrdered);

			StringBuilder handStr = new StringBuilder("");
			for(int i = 0; i < LENGTH_HAND; ++i)
			{
				handStr.append(handOrdered[i]);
			}

			try
			{
				if (HANDS_POKER_MAP.getHand(handStr.toString()).compareTo(HANDS_POKER_MAP.getHand(highestHand)) > 0)
				{
					highestHand = handStr.toString();
				}
			}
			catch (NullPointerException e)
			{
			}
		}
	}
}

package ch.hearc.pokerface.gameengine.compute;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import ch.hearc.pokerface.gameengine.subsets.Hand;

public class HandsPokerMap
{
	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private static final String				FILENAME				= "datas/pokerHands.txt";
	private static final String				SEPARATOR				= ";";
	private static final int				INDEX_RANK				= 0;
	private static final int				INDEX_KEY				= 1;
	private static final int				INDEX_SHORT_HAND_NAME	= 2;
	private static final int				INDEX_HAND_NAME			= 3;

	private static HandsPokerMap			instance;

	private Map<String, HandsPokerValue>	hands;

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	/**
	 * Represents the map that contains all the unique hands in the poker
	 */
	private HandsPokerMap()
	{
		hands = new HashMap<String, HandsPokerValue>();
		fill();
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	/*------------------------------*\
	|*			  Static			*|
	\*------------------------------*/

	public synchronized static HandsPokerMap getInstance()
	{
		if (instance == null)
		{
			instance = new HandsPokerMap();
		}
		return instance;
	}

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	/**
	 * Return the corresponding value of the hand with the given hand
	 * @param key : Hand that we want the HandsPokerValue
	 * @return
	 */
	public HandsPokerValue getHand(String key)
	{
		return hands.get(key);
	}

	/**
	 * Overload of the function getHand(String key)
	 * @param hand : Hand that we want the HandsPokerValue
	 * @return
	 */
	public HandsPokerValue getHand(Hand hand)
	{
		return hands.get(hand.getKey());
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	/**
	 * Fill the map with the file which contains all the possibile hands
	 */
	private void fill()
	{
		InputStreamReader isr;
		BufferedReader br;
		try
		{
			isr = new InputStreamReader(ClassLoader.getSystemResourceAsStream(FILENAME));
			br = new BufferedReader(isr);

			String line;

			while((line = br.readLine()) != null)
			{
				String[] values = line.split(SEPARATOR);

				hands.put(values[INDEX_KEY], new HandsPokerValue(Integer.valueOf(values[INDEX_RANK]), values[INDEX_HAND_NAME], values[INDEX_SHORT_HAND_NAME]));
			}

			br.close();
			isr.close();
		}
		catch (FileNotFoundException e)
		{
			System.err.println("File not found !");
		}
		catch (IOException e)
		{
			System.err.println("Error during the reading process !");
		}
	}
}

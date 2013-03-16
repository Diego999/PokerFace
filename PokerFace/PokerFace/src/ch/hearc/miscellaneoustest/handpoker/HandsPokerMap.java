
package ch.hearc.miscellaneoustest.handpoker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HandsPokerMap
{
	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private static final String				FILENAME		= "pokerHands.txt";
	private static final String				SEPARATOR		= ";";
	private static final int				INDEX_KEY		= 0;
	private static final int				INDEX_RANK		= 1;
	private static final int				INDEX_HAND_NAME	= 2;
	private static HandsPokerMap			instance;

	private Map<String, HandsPokerValue>	hands;

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public HandsPokerValue getHand(String key)
	{
		try
		{
			return hands.get(key);
		}
		catch (NullPointerException e)
		{
			return null;
		}
	}

	/*------------------------------*\
	|*			  Static			*|
	\*------------------------------*/

	public static HandsPokerMap getInstance()
	{
		if (instance == null)
		{
			instance = new HandsPokerMap();
		}
		return instance;
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private void fill()
	{
		FileReader fr;
		BufferedReader br;
		try
		{
			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			String line;

			while((line = br.readLine()) != null)
			{
				String[] values = line.split(SEPARATOR);

				hands.put(values[INDEX_KEY], new HandsPokerValue(Integer.valueOf(values[INDEX_RANK]), values[INDEX_HAND_NAME]));
			}

			br.close();
			fr.close();
		}
		catch (FileNotFoundException e)
		{
			System.err.println("File not found !");
		}
		catch (IOException e)
		{
			System.err.println("Error during reading file !");
		}

	}

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	private HandsPokerMap()
	{
		hands = new HashMap<String, HandsPokerValue>();
		fill();
	}
}

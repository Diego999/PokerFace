
package ch.hearc.miscellaneoustest.handpoker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ch.hearc.miscellaneoustest.handpoker.cards.Card;

public class HandsPokerMap
{
	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private static final String				FILENAME		= "pokerHands.txt";
	private static final String				SEPARATOR		= ";";
	private static final int				INDEX_KEY		= 1;
	private static final int				INDEX_RANK		= 0;
	private static final int				INDEX_HAND_NAME	= 2;
	private static HandsPokerMap			instance;

	private Map<String, HandsPokerValue>	hands;

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public HandsPokerValue getHand(String key)
	{
		char[] chars = key.toCharArray();
		Arrays.sort(chars);
		key = new String(chars);

		try
		{
			return hands.get(key);
		}
		catch (NullPointerException e)
		{
			return null;
		}
	}

	public HandsPokerValue getHand(CardSubset cardSubset)
	{
		char[] key = cardSubset.getKey().toCharArray();
		Arrays.sort(key); //TODO: test si nescaire

		try
		{
			return hands.get(String.valueOf(key));
		}
		catch (NullPointerException e)
		{
			return null;
		}
	}

	public Map<String, HandsPokerValue> getOuts(CardSubset board, CardSubset pocket)
	{
		Deck deck = new Deck();
		deck.sub(board);
		deck.sub(pocket);

		Map<String, HandsPokerValue> out = new HashMap<String, HandsPokerValue>();
		int rank = getHand(CardSubset.union(pocket, board)).getRank();

		for(Card card:deck)
		{
			for(int i = 0; i < board.size(); ++i)
			{
			}
		}

		//		for(int i = 0; i < keys.length; ++i)
		//		{
		//			String newHandString = new ComputeBestHandInASubset((pocket + board + keys[i]).split("")).getHighestHand();
		//			HandsPokerValue newHand = getHand(newHandString);
		//
		//			if (newHand.getRank() < rank && !newHand.getHandName().equals(getHand(pocket + board).getHandName()))
		//			{
		//				System.out.println(keys[i] + "->" + newHand.getRank() + " --> " + rank);
		//				//out.put(hand.getKey(), new HandsPokerValue(hand.getValue()));
		//				//System.out.println(keys[i]);
		//			}
		//
		//		}

		//		Map<String, HandsPokerValue> out = new HashMap<String, HandsPokerValue>();
		//		int rank = getHand(cards).getRank();
		//		//String regex = "";
		//		//Pattern pattern = Pattern.compile("^(?=.*[" + cards + "]{3,})(?=.*[[^\1]&&" + cards + "]{3,})(?=.*[[^\1\2]&&" + cards + "]{3,})(?=.*[[^\1\2\3]&&" + cards + "]{3,})(?=.*[[^\1\2\3\4]&&" + cards + "]{3,}).*$");
		//		//pattern.matcher(/*hand*/).matches();
		//		String board = cards.replaceFirst(String.valueOf(pocket.charAt(0)), "");
		//		board = board.replaceFirst(String.valueOf(pocket.charAt(1)), "");
		//
		//		Set<Entry<String, HandsPokerValue>> handsSet = hands.entrySet();
		//		for(Entry<String, HandsPokerValue> hand:handsSet)
		//		{
		//			if (rank < hand.getValue().getRank()) // "<" car notre main actuel ne fait pas mieux quelle m�me
		//			{
		//				if (hand.getKey().contains(String.valueOf(pocket.charAt(0))))
		//				{
		//					if (hand.getKey().contains(String.valueOf(pocket.charAt(1))))
		//					{
		//						if (hand.getKey().contains(String.valueOf(cards.charAt(0))) || hand.getKey().contains(String.valueOf(cards.charAt(1))) || hand.getKey().contains(String.valueOf(cards.charAt(2))))
		//						{
		//							out.put(hand.getKey(), new HandsPokerValue(hand.getValue()));
		//							System.out.println(hand.getKey());
		//						}
		//					}
		//				}
		//			}
		//		}
		//
		//		System.out.println(out.size());

		return out;
	}

	//	private boolean contains3(String cards, String key)
	//	{
	//		int i = 0;
	//		for(char c:cards.toCharArray())
	//		{
	//			if (key.contains(String.valueOf(c)))
	//			{
	//				key.replaceFirst(String.valueOf(c), "");
	//				if (++i >= 3) { return true; }
	//			}
	//		}
	//
	//		return false;
	//	}

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
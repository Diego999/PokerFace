
package ch.hearc.miscellaneoustest.handpoker;

import java.util.HashMap;
import java.util.Map;

public class UseHandPokerMap
{
	private static final int	NB_SIMULATION		= 1;
	private static final int	NB_CARDS_IN_BOARD	= 7;

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public static void main(String[] args)
	{
		main();
	}

	public static void main()
	{
		String[] keys = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K" };

		Map<String, Integer> map = new HashMap<String, Integer>();

		map.put("A", 0);
		map.put("2", 0);
		map.put("3", 0);
		map.put("4", 0);
		map.put("5", 0);
		map.put("6", 0);
		map.put("7", 0);
		map.put("8", 0);
		map.put("9", 0);
		map.put("T", 0);
		map.put("J", 0);
		map.put("Q", 0);
		map.put("K", 0);

		for(int i = 0; i < NB_SIMULATION; ++i)
		{
			String[] hand = new String[NB_CARDS_IN_BOARD];
			for(int j = 0; j < NB_CARDS_IN_BOARD; ++j)
			{
				do
				{
					hand[j] = keys[(int)(Math.random() * (keys.length))];
				} while(map.get(hand[j]) > 4);
			}

//			System.out.print("CARDS : ");
//			System.out.println(Arrays.toString(hand));
//			System.out.print("BEST : ");
			HandsPokerMap hpm = HandsPokerMap.getInstance();
			String bestHand = new ComputeBestHandInASubset(hand).getHighestHand();
			HandsPokerValue hpv = hpm.getHand(bestHand);
//			System.out.println(bestHand + " -> " + hpv.getRank() + " " + hpv.getHandName());
//			System.out.println();
			//hpm.getOuts("67T", "44");
			hpm.getOuts("84J", "9T");
		}
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

}

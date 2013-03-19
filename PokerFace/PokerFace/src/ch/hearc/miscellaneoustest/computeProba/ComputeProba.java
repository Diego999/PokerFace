
package ch.hearc.miscellaneoustest.computeProba;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.hearc.miscellaneoustest.handpoker.ComputeBestHandInASubset;
import ch.hearc.miscellaneoustest.handpoker.HandsPokerMap;


public class ComputeProba
{
	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private String[] hand;
	private Subset subset;
	private HandsPokerMap map = HandsPokerMap.getInstance();

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public ComputeProba(String[] hand)
	{
		subset = new Subset();
		this.hand = new String[hand.length];
		String[] handTemp = new String[hand.length+2];
		for(int i = 0;i < hand.length; ++i)
		{
			this.hand[i] = hand[i].substring(0,1);
			handTemp[i] = this.hand[i];
			subset.remove(hand[i]);
		}

		boolean same = true;

		for(int i = 0; same && i < hand.length - 1; ++i)
		{
			if (hand[i].substring(1) != hand[i+1].substring(1))
			{
				same = false;
			}
		}

		StringBuilder key = new StringBuilder("");
		Arrays.sort(this.hand);

		for(String string:this.hand)
		{
			key.append(string);
		}
		if(same)
		{
			key.append("s");
		}

		double sf = 0;//9
		double k3 = 0;//4
		double k4 = 0;//8
		double p1 = 0;//2
		double p2 = 0;//3
		double f = 0;//6
		double fh = 0;//7
		double hc = 0;//1
		double s = 0;//5

		String handname = (map.getHand(key.toString())).getHandName();

		int choix = 0;
		if(handname.equals("HC"))
		{
			choix = 1;
		}
		else if(handname.equals("1P"))
		{
			choix = 2;
		}
		else if(handname.equals("2P"))
		{
			choix = 3;
		}
		else if(handname.equals("3K"))
		{
			choix = 4;
		}
		else if(handname.equals("S"))
		{
			choix = 5;
		}
		else if(handname.equals("F"))
		{
			choix = 6;
		}
		else if(handname.equals("FH"))
		{
			choix = 7;
		}
		else if(handname.equals("4K"))
		{
			choix = 8;
		}
		else if(handname.equals("SF"))
		{
			choix = 9;
		}

		switch(choix)
		{
			case 1:

			case 2:

			case 3:

			case 4:

			case 5:

			case 6:

			case 7:

			case 8:

			case 9:
				break;
		}

		Iterator<String> it1 = subset.iterator();

		Map<String,List<String>> mapOut = new HashMap<String,List<String>>();

		while(it1.hasNext())
		{
			Iterator<String> it2 = subset.iterator();
			it2.next();
			handTemp[5] = it1.next().substring(0,1);

			while(it2.hasNext())
			{
				if(it1 != it2)
				{
					handTemp[6] = it2.next().substring(0,1);
					ComputeBestHandInASubset comp = new ComputeBestHandInASubset(handTemp);
					String name = map.getHand(comp.getHighestHand()).getHandName();

					try
					{
						mapOut.get(name);
					}
					catch (NullPointerException e)
					{
						mapOut.put(name, new ArrayList<String>());
					}

					StringBuilder temp = new StringBuilder("");

					//String[] handTemp
					for(int i = 0;i < handTemp.length; ++i)
						{

						}
				}
			}
		}


		System.out.println("HC : " + hc);
		System.out.println("1P : " + p1);
		System.out.println("2P : " + p2);
		System.out.println("K3 : " + k3);
		System.out.println("S : " + s);
		System.out.println("F : " + f);
		System.out.println("FH : " + fh);
		System.out.println("K4 : " + k4);
		System.out.println("SF : " + sf);
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	/*------------------------------*\
	|*				Set				*|
	\*------------------------------*/

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/


}


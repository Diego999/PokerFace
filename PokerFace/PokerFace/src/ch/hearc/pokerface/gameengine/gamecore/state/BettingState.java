
package ch.hearc.pokerface.gameengine.gamecore.state;

import ch.hearc.pokerface.gameengine.gamecore.GameEngine;

public class BettingState extends State
{
	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private final static int	NB_TURN_MAX	= 4;

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	@Override
	public void addCads(GameEngine ge)
	{

	}

	@Override
	public void bet(GameEngine ge)
	{
		try
		{
			boolean allChecked = false;
			for(int i = 0; i < NB_TURN_MAX && !allChecked; ++i)
			{
				int betSpend = ge.getPot().getBet();
				int nbUnfoldedPlayer = ge.getUnfoldedPlayer();
				for(int j = 0; j < nbUnfoldedPlayer; ++j)
				{
					wait();
					ge.changeCurrentPlayer();
				}
				allChecked = (ge.getPot().getBet() == betSpend);
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void nextSate(GameEngine ge)
	{
		switch(ge.getOldState())
		{
			case PreFlopState:
				ge.setState(new FlopState());
				break;

			case FlopState:
				ge.setState(new TurnState());
				break;

			case TurnState:
				ge.setState(new RiverState());
				break;

			case RiverState:
				ge.showdown();
				ge.setState(null);
				break;

			default:
				break;
		}

	}
}

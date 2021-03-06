
package ch.hearc.pokerface.gameengine.player;

import java.util.Observable;
import java.util.Observer;

import javax.swing.SwingUtilities;

import ch.hearc.pokerface.gameengine.cards.Card;
import ch.hearc.pokerface.gameengine.gamecore.GameEngine;
import ch.hearc.pokerface.gameengine.gamecore.state.StateType;
import ch.hearc.pokerface.gameengine.player.profile.Profile;
import ch.hearc.pokerface.gameengine.statistics.StatisticValue;
import ch.hearc.pokerface.gameengine.statistics.Statistics;
import ch.hearc.pokerface.gameengine.subsets.Pocket;
import ch.hearc.pokerface.tools.Pair;

public class Player extends Observable implements Observer
{
	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	/**
	 * @contains: the current amount of money that the player has.
	 */
	protected int				bankroll;

	/**
	 * @contains: the current amount of money that the player has spent during the current turn.
	 */
	protected int				turnSpending;

	/**
	 * @contains: the current amount of money that the player has spent during the current bet state.
	 */
	protected int				betSpending;

	/**
	 * @contains: the number of times that the player has done a action during the current turn.
	 */
	protected int				nbTurnBet;
	protected boolean			folded;
	protected boolean			dead;
	protected boolean			hasWon;

	/**
	 * @contains: the statistic value for the PreFlop.
	 */
	protected StatisticValue	svPreFlop;

	/**
	 * @contains: the statistic value for the Flop.
	 */
	protected StatisticValue	svFlop;

	/**
	 * @contains: the statistic value for the Turn.
	 */
	protected StatisticValue	svTurn;

	/**
	 * @contains: the statistic value for the River.
	 */
	protected StatisticValue	svRiver;

	/**
	 * @contains: the current pocket of the player(the two cards).
	 */
	protected Pocket			pocket;
	protected Profile			profile;

	/**
	 * @contains: the role of the player during this turn.
	 */
	protected Role				role;
	protected GameEngine		gameEngine;

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	/**
	 * Player
	 *
	 * @param profile: The player's profile
	 * @param bankroll: The value of the bankroll
	 * @param gameEngine: The GameEngine
	 */
	public Player(Profile profile, int bankroll, GameEngine gameEngine)
	{
		this.profile = profile;
		this.bankroll = bankroll;
		this.gameEngine = gameEngine;
		this.nbTurnBet = 1;

		newTurn();
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	/**
	 * Wait an action triggered by the GUI with notifiy
	 *
	 * @throws InterruptedException
	 */
	public void doAction()
	{
		nbTurnBet++;
		gameEngine.updateGUI();
		stopCurrentSimulation(true);

		try
		{
			synchronized (this)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						gameEngine.updateStatePlayerButtons();
					}
				});
				wait();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Initialize all the values for a new turn
	 */
	public void newTurn()
	{
		this.pocket = new Pocket();
		role = Role.Nothing;

		this.turnSpending = 0;
		this.betSpending = 0;
		this.folded = false;
		this.dead = false;
		this.hasWon = false;

		svPreFlop = null;
		svFlop = null;
		svTurn = null;
		svRiver = null;
	}

	/**
	 * Set the betSpend to zero
	 */
	public void endBettingState()
	{
		this.nbTurnBet = 1;
		this.betSpending = 0;
	}

	/**
	 * Add money to the player's bankroll
	 *
	 * @param money: Money to give
	 */
	public void giveMoney(int money)
	{
		bankroll += money;
	}

	/**
	 * Take money from the player's bankrool
	 *
	 * @param money: Money to take
	 */
	public void takeMoney(int money)
	{
		turnSpending += money;
		betSpending += money;
		bankroll -= money;
	}

	/**
	 * The player calls first and then bet all his bankroll
	 */
	public void allIn()
	{
		stopAllSimulation();
		gameEngine.allin(this);
	}

	/**
	 * Player checks (bet 0)
	 */
	public void check()
	{
		gameEngine.check(this);
	}

	/**
	 * The player bets the amount
	 *
	 * @param amount: Amount to bet
	 */
	public void bet(int amount)
	{
		gameEngine.bet(this, amount);
	}

	/**
	 * The player bets the small blind.
	 *
	 * @param amount: Amount to bet
	 */
	public void betSmallBlind()
	{
		gameEngine.betBlind(this, true);
	}

	/**
	 * The player bets the big blind.
	 *
	 * @param amount: Amount to bet
	 */
	public void betBigBlind()
	{
		gameEngine.betBlind(this, false);
	}

	/**
	 * The player pays the amount to continue the game
	 */
	public void call()
	{
		gameEngine.call(this);
	}

	/**
	 * The player raises, the call value will be automaticly computed
	 *
	 * @param amount: Money for the raise (bet)
	 */
	public void raise(int amount)
	{
		gameEngine.raise(this, amount);
	}

	/**
	 * The folded attribut to true when the player folds
	 */
	public void fold()
	{
		stopAllSimulation();
		svPreFlop = svFlop = svTurn = svRiver = null;
		gameEngine.fold(this);
		folded = true;
	}

	/**
	 * The player has a bankroll empty, so he's ejected of the board. Set the attribut dead to true
	 */
	public void kill()
	{
		role = Role.Nothing;
		dead = true;
	}

	/**
	 * The player has won the turn !
	 */
	public void win()
	{
		hasWon = true;
	}

	/**
	 * Add new card to the player's pocker
	 *
	 * @param card: the new card
	 */
	public void addCard(Card card)
	{
		pocket.add(card);
		if (pocket.size() == Pocket.NUMBER_OF_CARDS)
		{
			svPreFlop = Statistics.getPreFlopValues(pocket, gameEngine.getNbPlayers());
		}
	}

	/**
	 * Remove the amount to the player's turningSpend
	 *
	 * @param amount: Money to take
	 */
	public void removeTurningSpend(int amount)
	{
		turnSpending = (turnSpending - amount > 0) ? turnSpending - amount : 0;
	}

	/**
	 * Observer use for the pattern Observer
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public void update(Observable o, Object arg)
	{
		Pair<StateType, StatisticValue> response = (Pair<StateType, StatisticValue>)arg;
		StateType stateType = response.getKey();

		if (stateType == StateType.FlopState)
		{
			svFlop = response.getValue();
		}
		else if (stateType == StateType.TurnState)
		{
			svTurn = response.getValue();
		}
		else
		{
			svRiver = response.getValue();
		}
	}

	/**
	 * Stop the simulation of the state. If all = true, stop all the simulation
	 *
	 * @param Current: true -> current simulation, false -> All simulations
	 */
	public void stopCurrentSimulation(boolean current)
	{
		if (current)
		{
			StateType state = gameEngine.getOldState();
			if ((state == StateType.FlopState && svFlop == null) || (state == StateType.TurnState && svTurn == null) || (state == StateType.RiverState && svRiver == null))
			{
				setChanged();
				notifyObservers(state);
			}
		}
		else
		{
			stopAllSimulation();
		}
	}

	/*------------------------------*\
	|*				Set				*|
	\*------------------------------*/

	/**
	 * Set the role of the player
	 *
	 * @param r: The role
	 */
	public void setRole(Role r)
	{
		role = r;
	}

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	public boolean getHasWon()
	{
		return hasWon;
	}

	public int getCallValue()
	{
		return gameEngine.getBet() - betSpending;
	}

	public Role getRole()
	{
		return role;
	}

	public Profile getProfile()
	{
		return profile;
	}

	public StatisticValue getPreFlopValues()
	{
		return svPreFlop;
	}

	public StatisticValue getFlopValues()
	{
		return svFlop;
	}

	public StatisticValue getTurnValues()
	{
		return svTurn;
	}

	public StatisticValue getRiverValues()
	{
		return svRiver;
	}

	public int getBankroll()
	{
		return this.bankroll;
	}

	public int getTurnSpending()
	{
		return this.turnSpending;
	}

	public int getBetSpending()
	{
		return this.betSpending;
	}

	public Pocket getPocket()
	{
		return this.pocket;
	}

	public int getNbTurnBet()
	{
		return this.nbTurnBet;
	}

	/*------------------------------*\
	|*				Is				*|
	\*------------------------------*/

	public boolean isFolded()
	{
		return this.folded;
	}

	public boolean isDead()
	{
		return this.dead;
	}

	/*------------------------------------------------------------------*\
	|*						Methodes Protected							*|
	\*------------------------------------------------------------------*/

	/**
	 * Stop the current simulation
	 */
	protected void stopAllSimulation()
	{
		setChanged();
		notifyObservers(StateType.FlopState);
		notifyObservers(StateType.TurnState);
		notifyObservers(StateType.RiverState);
	}
}

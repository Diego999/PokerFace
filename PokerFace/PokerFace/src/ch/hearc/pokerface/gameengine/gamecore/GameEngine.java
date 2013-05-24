
package ch.hearc.pokerface.gameengine.gamecore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JTextArea;

import ch.hearc.pokerface.gameengine.cards.Card;
import ch.hearc.pokerface.gameengine.compute.ComputeBestHand;
import ch.hearc.pokerface.gameengine.compute.HandsPokerMap;
import ch.hearc.pokerface.gameengine.compute.HandsPokerValue;
import ch.hearc.pokerface.gameengine.gamecore.state.PreFlopState;
import ch.hearc.pokerface.gameengine.gamecore.state.State;
import ch.hearc.pokerface.gameengine.gamecore.state.StateType;
import ch.hearc.pokerface.gameengine.player.AI;
import ch.hearc.pokerface.gameengine.player.Player;
import ch.hearc.pokerface.gameengine.player.Role;
import ch.hearc.pokerface.gameengine.player.profile.Profile;
import ch.hearc.pokerface.gameengine.player.profile.tools.AIProfileGenerator;
import ch.hearc.pokerface.gameengine.statistics.Statistics;
import ch.hearc.pokerface.gameengine.subsets.Board;
import ch.hearc.pokerface.gameengine.subsets.CardSubset;
import ch.hearc.pokerface.gameengine.subsets.Deck;
import ch.hearc.pokerface.gui.gamescreen.table.board.JPanelGameBoard;
import ch.hearc.pokerface.tools.Pair;
import ch.hearc.pokerface.tools.Triple;

public class GameEngine
{
	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private StateType			oldState;
	private State				state;
	private int					indexPlayer;
	private int					indexDealer;
	private int					indexSmallBlind;
	private int					indexBigBlind;
	private int					indexLastRaise;
	private int					magicIndex;
	private int					nbTurn;
	private boolean				isFinished;
	private Pot					pot;
	private Board				board;
	private List<Player>		players;
	private Deck				deck;
	private SoundEngine			soundEngine;
	private HandsPokerMap		handsPokerMap;
	private int					smallBlind;
	private int					bigBlind;
	private Card[]				futureBoard;
	private JPanelGameBoard		panelGameBoard;

	private JTextArea			logger;

	/*------------------------------*\
	|*			  Static			*|
	\*------------------------------*/

	public static Player		HUMAN_PLAYER;
	private static final int	NB_TURN_BEFORE_CHANGE_BLIND	= 8;
	private static final long	DELAY_BETWEEN_EACH_TURN		= 3000;

	// initialize to nbPlayer*(-2) and incremented each draw of card. After the players have received theirs cards, we use it to define the correct card in the variable futureBoard to get
	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public GameEngine(int smallBlind, int nbPlayer, Profile profile, int bankroll, JPanelGameBoard panelGameBoard)
	{
		this.panelGameBoard = panelGameBoard;
		logger = null;

		nbTurn = 0;
		pot = new Pot();
		soundEngine = SoundEngine.getInstance();
		futureBoard = new Card[Board.NUMBER_CARDS];
		handsPokerMap = HandsPokerMap.getInstance();

		this.smallBlind = smallBlind;
		bigBlind = 2 * smallBlind;

		players = new ArrayList<Player>();
		players.add(new Player(profile, bankroll, this));
		profile.setCapital(profile.getCapital() - bankroll);

		HUMAN_PLAYER = players.get(0);

		for(int i = 1; i < nbPlayer; ++i)
		{
			players.add(new AI(AIProfileGenerator.fetchAIProfile(), bankroll, this));
		}
		indexPlayer = (int)(Math.random() * nbPlayer);
		indexDealer = getPreviousIndex(indexPlayer);

		initialize();
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public void run()
	{
		while(!isFinished)
		{
			state.addCads(this);
			state.bet(this);
			state.nextSate(this);
		}
	}

	public int changeCurrentPlayer()
	{
		do
		{
			indexPlayer = getNextIndex(indexPlayer);
		} while(getCurrentPlayer().isFolded());
		return indexPlayer;
	}

	public Card drawCard()
	{
		if (magicIndex++ < 0)
		{
			Card card = deck.getNewCard();
			if (magicIndex == 0)
			{
				for(int i = 0; i < Board.NUMBER_CARDS; ++i)
				{
					futureBoard[i] = deck.getNewCard();
				}
				//We run the simulation for the state FLOP, we're currently at the end of the state PREFLOP.
				//So, we have all the betting state to compute these values
				runSimulationPlayer();
			}
			return card;
		}
		else
		{
			return futureBoard[magicIndex - 1];
		}
	}

	public void bet(int amount)
	{
		Player player = players.get(indexPlayer);

		if (amount > pot.getBet())
		{
			pot.setBet(amount);
			indexLastRaise = indexPlayer;
		}

		player.takeMoney(amount);
		pot.addStateTotal(amount);
	}

	public void betSmallBlind()
	{
		logPlayerAction(getCurrentPlayer(), Action.PostSmallBlind, (smallBlind > players.get(indexSmallBlind).getBankroll()) ? players.get(indexSmallBlind).getBankroll() : smallBlind);
		players.get(indexSmallBlind).bet((smallBlind > players.get(indexSmallBlind).getBankroll()) ? players.get(indexSmallBlind).getBankroll() : smallBlind);
	}

	public void betBigBlind()
	{
		logPlayerAction(getCurrentPlayer(), Action.PostBigBlind, (bigBlind > players.get(indexBigBlind).getBankroll()) ? players.get(indexBigBlind).getBankroll() : bigBlind);
		players.get(indexBigBlind).bet((bigBlind > players.get(indexBigBlind).getBankroll()) ? players.get(indexBigBlind).getBankroll() : bigBlind);
	}

	public void showdown()
	{
		List<Pair<HandsPokerValue, Player>> handsValues = new ArrayList<Pair<HandsPokerValue, Player>>();

		//Compte the value of each player's hand
		for(Player player:players)
		{
			if (!player.isFolded())
			{
				ComputeBestHand computeBestHand = new ComputeBestHand(CardSubset.union(player.getPocket(), board));
				handsValues.add(new Pair<HandsPokerValue, Player>(handsPokerMap.getHand(computeBestHand.getHighestHand()), player));
			}
		}

		Collections.sort(handsValues);

		int bestRank = handsValues.get(0).getKey().getRank();
		for(Pair<HandsPokerValue, Player> pair:handsValues)
		{
			Player p = pair.getValue();
			logPlayerFinalResult((pair.getKey().getRank() == bestRank ? "Winner" : "Loser"), p, pair.getKey().getHandName());
		}
		//Map<Rank,Triple<groupPot,SumBets,List<Player>>>
		Map<Integer, Triple<Integer, Integer, List<Player>>> playerSortByRank = new TreeMap<Integer, Triple<Integer, Integer, List<Player>>>();

		//Groupe all players by ranking and if they is an equality, there would be n players in the same group
		for(int i = 0; i < handsValues.size(); ++i)
		{
			Pair<HandsPokerValue, Player> pair = handsValues.get(i);

			int rank = pair.getKey().getRank();
			if (playerSortByRank.get(rank) == null)
			{
				playerSortByRank.put(rank, new Triple<Integer, Integer, List<Player>>(0, 0, new ArrayList<Player>()));
			}
			Triple<Integer, Integer, List<Player>> triple = playerSortByRank.get(rank);
			triple.getValue2().add(pair.getValue());
			playerSortByRank.put(rank, triple);
		}

		//We transform it to array
		Set<Entry<Integer, Triple<Integer, Integer, List<Player>>>> entrySet = playerSortByRank.entrySet();
		@SuppressWarnings("unchecked")
		Triple<Integer, Integer, List<Player>>[] triples = new Triple[entrySet.size()];
		int i = 0;
		for(Entry<Integer, Triple<Integer, Integer, List<Player>>> entry:entrySet)
		{
			Triple<Integer, Integer, List<Player>> triple = entry.getValue();
			triples[i++] = new Triple<Integer, Integer, List<Player>>(triple.getKey(), triple.getValue1(), triple.getValue2());
		}

		//We put the flag hasWon to true for the player(s) who win(s)
		for(i = 0; i < triples[0].getValue2().size(); ++i)
		{
			triples[0].getValue2().get(i).win();
		}

		divideUpPot(triples);

		try
		{
			Thread.sleep(DELAY_BETWEEN_EACH_TURN);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		initialize();
		updateGUI();
	}

	public void updateGUI()
	{
		panelGameBoard.updateGUI();
	}

	public void setNewState()
	{
		indexLastRaise = -1;
		indexPlayer = getNextIndex(indexDealer);
	}

	public void addToBoard(Card card)
	{
		board.add(card);
	}

	public void logPlayerAction(Player player, Action action, int amount)
	{
		log(player.getProfile().getName() + " " + action.toString() + " " + ((amount != -1) ? amount + "$" : ""));
		soundEngine.playSound(action);
	}

	public void logPlayerAction(Player player, Action action)
	{
		logPlayerAction(player, action, -1);
	}

	public void logBoard(String state, String cards)
	{
		log("---- " + state + " ----");
		log(cards);
	}

	public void logPlayerFinalResult(String rank, Player player, String handName)
	{
		log(rank + " : " + player.getProfile().getName() + " with " + player.getPocket().toString() + " -> " + handName);
	}

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	public int getNbTurn()
	{
		return nbTurn;
	}

	public SoundEngine getSoundEngine()
	{
		return soundEngine;
	}

	public boolean getIsFinished()
	{
		return isFinished;
	}

	public int getSmallBlind()
	{
		return smallBlind;
	}

	public int getBigBlind()
	{
		return bigBlind;
	}

	public Board getBoard()
	{
		return board;
	}

	public int getUnfoldedPlayer()
	{
		int out = 0;
		for(Player p:players)
		{
			out += (p.isFolded()) ? 0 : 1;
		}
		return out;
	}

	public int getAllInPlayer()
	{
		int out = 0;
		for(Player p:players)
		{
			out += (p.getBankroll() == 0) ? 1 : 0;
		}
		return out;
	}

	public Card[] getUnorderedBoard()
	{
		int nbCards = 0;

		if (oldState == StateType.FlopState)
		{
			nbCards = Statistics.NUMBER_CARDS_FLOP;
		}
		else if (oldState == StateType.TurnState)
		{
			nbCards = Statistics.NUMBER_CARDS_TURN;
		}
		else if (oldState == StateType.RiverState)
		{
			nbCards = Statistics.NUMBER_CARDS_RIVER;
		}

		Card[] cards = new Card[nbCards];
		for(int i = 0; i < nbCards; ++i)
		{
			cards[i] = futureBoard[i];
		}
		return cards;
	}

	public Player getCurrentPlayer()
	{
		return players.get(indexPlayer);
	}

	public int getNbPlayers()
	{
		return players.size();
	}

	public StateType getOldState()
	{
		return oldState;
	}

	public Pot getPot()
	{
		return pot;
	}

	public List<Player> getPlayers()
	{
		return players;
	}

	public Player getLastRaisePlayer()
	{
		if (indexLastRaise < 0 || indexLastRaise >= players.size())
		{
			return null;
		}
		else
		{
			return players.get(indexLastRaise);
		}
	}

	/*------------------------------*\
	|*				Set				*|
	\*------------------------------*/

	public void setLogger(JTextArea logger)
	{
		this.logger = logger;
	}

	public void setIndexLastRaise(Player p)
	{
		indexLastRaise = players.indexOf(p);
	}

	public void setState(State s)
	{
		getPot().nextState();
		state = s;
	}

	public void setOldState(StateType oldState)
	{
		this.oldState = oldState;
		String state = "";
		StringBuilder sb = new StringBuilder();

		if (this.oldState == StateType.FlopState)
		{
			state = "Flop";
		}
		else if (this.oldState == StateType.TurnState)
		{
			state = "Turn";
		}
		else if (this.oldState == StateType.RiverState)
		{
			state = "River";
		}
		else
		{
			state = "Preflop";
		}

		Card[] cards = getUnorderedBoard();
		for(Card c:cards)
		{
			sb.append(c.toString());
		}

		logBoard(state, sb.toString());
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	/**
	 * @param triples
	 *            Triple<groupPot,SumBets,List<Player>>
	 */
	private void divideUpPot(Triple<Integer, Integer, List<Player>>[] triples)
	{
		for(int i = 0; i < triples.length; ++i)
		{
			//Find the min bet of the group
			int min = triples[i].getValue2().get(0).getTurnSpending();

			for(int j = 0; j < triples[i].getValue2().size(); ++j)
			{
				Player p = triples[i].getValue2().get(j);
				min = (p.getTurnSpending() < min) ? p.getTurnSpending() : min;
				triples[i].setKey(triples[i].getKey() + p.getTurnSpending());
				triples[i].setValue1(triples[i].getValue1() + p.getTurnSpending());
			}

			//Decrease the min found to all next players and add each bet to the groupPot
			for(int j = i + 1; j < triples.length; ++j)
			{
				for(int k = 0; k < triples[j].getValue2().size(); ++k)
				{
					Player p = triples[j].getValue2().get(k);
					if (p.getTurnSpending() >= min)
					{
						p.removeTurningSpend(min);
						triples[i].setKey(triples[i].getKey() + min);
					}
					else
					{
						triples[i].setKey(triples[i].getKey() + p.getTurnSpending());
						p.removeTurningSpend(min);
					}
				}
			}
		}

		int nbPlayer = 0;
		for(Triple<Integer, Integer, List<Player>> triple:triples)
		{
			nbPlayer += triple.getValue2().size();
		}

		int[] winValues = new int[nbPlayer];

		//We know now which money each player has to receive, so we process
		for(int i = 0; i < triples.length; ++i)
		{
			for(int j = 0; j < triples[i].getValue2().size(); ++j)
			{
				Player p = triples[i].getValue2().get(j);
				int moneyGiven = triples[i].getKey() * p.getTurnSpending();
				if (moneyGiven != 0 && triples[i].getValue1() != 0)
				{
					moneyGiven /= triples[i].getValue1();
					pot.removeAmount(moneyGiven);
					p.giveMoney(moneyGiven);
					winValues[i] += moneyGiven;
				}
			}
		}

		/*There might be a rest if the pot is not divisible. In this case, we take
		the rest and distribute it between all the first group of winners, $ per $*/
		int rest = pot.getTurnTotal();
		pot.removeAmount(rest);

		while(rest != 0)
		{
			for(int i = 0; i < triples[0].getValue2().size(); ++i)
			{
				Player p = triples[0].getValue2().get(i);
				if (rest > 0)
				{
					p.giveMoney(1);
					winValues[i] += 1;
					--rest;
				}
			}
		}

		int indexWinValues = 0;
		for(Triple<Integer, Integer, List<Player>> triple:triples)
		{
			List<Player> players = triple.getValue2();
			for(Player p:players)
			{
				logPlayerAction(p, Action.WinMoney, winValues[indexWinValues++]);
			}
		}

		List<Player> playersToKill = new ArrayList<Player>();
		for(int i = 0; i < players.size(); ++i)
		{
			Player p = players.get(i);
			if (p.getBankroll() <= 0)
			{
				logPlayerAction(p, Action.Leave);
				p.kill();
				playersToKill.add(p);
			}
		}

		for(Player p:playersToKill)
		{
			players.remove(p);
		}
	}

	private int getNextIndex(int val)
	{
		return (val < (players.size() - 1)) ? val + 1 : 0;
	}

	private int getPreviousIndex(int val)
	{
		return (val == 0) ? getNbPlayers() - 1 : val - 1;
	}

	private void initialize()
	{
		for(Player p:players)
		{
			p.newTurn();
		}
		isFinished = players.size() == 1;

		if (!isFinished)
		{
			if (++nbTurn % NB_TURN_BEFORE_CHANGE_BLIND == 0)
			{
				smallBlind *= 2;
				bigBlind *= 2;
			}

			isFinished = false;
			pot.initialize();
			oldState = null;

			setState(new PreFlopState());
			board = new Board();
			deck = new Deck();

			magicIndex = -2 * players.size();

			indexDealer = getNextIndex(indexDealer);
			players.get(indexDealer).setRole(Role.Dealer);

			//If there are 2 players, there is no small blind !
			if (players.size() > 2)
			{
				indexSmallBlind = getNextIndex(indexDealer);
				indexPlayer = indexSmallBlind;
				Player smallBlindPlayer = players.get(indexSmallBlind);
				smallBlindPlayer.setRole(Role.SmallBlind);

				indexBigBlind = getNextIndex(indexSmallBlind);
			}
			else
			{
				indexSmallBlind = -1;
				indexBigBlind = getNextIndex(indexDealer);
				indexPlayer = indexBigBlind;
			}
			indexLastRaise = -1;

			Player bigBlindPlayer = players.get(indexBigBlind);
			bigBlindPlayer.setRole(Role.BigBlind);
		}
	}

	private void runSimulationPlayer()
	{
		int lastPlayer = indexPlayer;
		int i = lastPlayer;
		int nbCardInBoard = Statistics.NUMBER_CARDS_FLOP;
		do
		{
			i = getNextIndex(i);
			Player player = players.get(i);
			if (!player.isFolded())
			{
				Statistics.getFlopTurnRiverValues(player, player.getPocket(), futureBoard, getNbPlayers(), nbCardInBoard);
			}
			if (i == lastPlayer)
			{
				i = getNextIndex(i);
				nbCardInBoard++;
			}
		} while(i != lastPlayer && nbCardInBoard <= Statistics.NUMBER_CARDS_RIVER);
	}

	private void log(String message)
	{
		if (logger != null)
		{
			logger.append(message + "\n");
		}
		else
		{
			System.out.println(message);
		}
	}
}

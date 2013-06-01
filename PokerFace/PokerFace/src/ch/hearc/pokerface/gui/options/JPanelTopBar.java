
package ch.hearc.pokerface.gui.options;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.hearc.pokerface.gameengine.gamecore.SoundEngine;
import ch.hearc.pokerface.gameengine.player.profile.Profile;
import ch.hearc.pokerface.gui.JFrameMain;
import ch.hearc.pokerface.gui.tools.ButtonTools;
import ch.hearc.pokerface.gui.tools.ColorShop;
import ch.hearc.pokerface.gui.tools.ImageShop;

public class JPanelTopBar extends JPanel
{

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private static volatile JPanelTopBar	instance	= null;

	// Inputs / Outputs
	private JLabel							avatar;
	private JLabel							name;
	private JLabel							bankroll;
	private JLabel							coin;
	private JLabel							clockLabel;
	private JLabel							help;
	private JLabel							quitApplication;
	private JLabel							quitGame;
	private JLabel							volumeIcon;
	private JSlider							volumeSlider;

	private SimpleDateFormat				ft;

	private JFrameMain						frameMain;

	/*------------------------------*\
	|*			  Static			*|
	\*------------------------------*/

	private static final String				RULES		= "<html><h3>The Cards</h3>"
																+ "<p>At the beginning, each player receive 2 cards that the other(s) player(s) can't see. In the deck, there are in total 52 cards.<br />"
																+ "During the game, others cards will appear at the middle : first 3, then 1 and finally 1 other. These cards are shared with the other players<br />"
																+ "The purpose is to have the best hand ranking. To help you, there is at the bottom left all the handrankings possible</p>"
																+ "<h3>Game process</h3>"
																+ "<p>In the poker texas holdem, there are 4 main states and few bettings states. In the oreders, the state are :"
																+ "<ul>"
																+ "<li><b>Pre flop</b> : You receive your 2 cards</li>"
																+ "<li><b>Flop</b> : 3 cards appear over the board</li>"
																+ "<li><b>Turn</b> : 1 card appears over the board</li>"
																+ "<li><b>River</b> : 1 last card appears over the board</li>"
																+ "</ul>"
																+ "Between each step there is a betting state. It will be described afterwards.<br />"
																+ "After the last betting state, all the players in play show their cards. If a player has less good cards than another one, he has the possibility to not show his cards.<br />"
																+ "At the end, the best player take all the money. If there is more than one winner (tie), the pot is divided up.</p>"
																+ "<h3>Bet</h3>"
																+ "During a game, each player has a role : "
																+ "<ul>"
																+ "<li>Small Blind (only when there are more than 2 players)</li>"
																+ "<li>Big Blind</li>"
																+ "<li>Dealer</li>"
																+ "<li>Nothing</li>"
																+ "</ul>"
																+ "During the first betting state, after the preflop state, the big blind must bet an amount (defined by the table options) and the big blind twice more.<br />"
																+ "The dealer deals the card then the small blind bets and finally the big blind. Afterwards, the next player could play and make a decision.<br /> After all players have played, the small blind has to play and the Big Blind has to finish the turn if no body has raised.<br />"
																+ "After the preflop, there is no more roles.<br />" + "The diffections action possible are :" + "<ul>" + "<li><b>Fold</b> : You left the current game</li>"
																+ "<li><b>Check</b> : If nobody has already bet, that mean \"I continue but I bet nothing\"</li>" + "<li><b>Bet </b> : Same than check but you define the bet</li>"
																+ "<li><b>Call</b> : To continue the game, you have to pay the bet</li>" + "<li><b>Raise</b> : If you have good hands or want to bluff, you could pay more than necessary</li>" + "</ul></html>";

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	/*
	 * @return The only instance of the class
	 */
	public final static JPanelTopBar getInstance()
	{
		if (JPanelTopBar.instance == null)
		{
			synchronized (JPanelTopBar.class)
			{
				if (JPanelTopBar.instance == null)
				{
					JPanelTopBar.instance = new JPanelTopBar();
				}
			}
		}
		return JPanelTopBar.instance;
	}

	private JPanelTopBar()
	{
		geometry();
		control();
		apparence();

		Timer t = new Timer(1000, updateClockAction);
		t.start();
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public void refreshProfile(Profile profile)
	{
		name.setText(profile.getName());
		avatar.setIcon(profile.getAvatar().getIcon());
		bankroll.setText(profile.getCapital() + " $");
	}

	public void setFrameMain(JFrameMain frameMain)
	{
		this.frameMain = frameMain;
	}

	public void setCapital(String capital)
	{
		bankroll.setText(capital + " $");
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private void geometry()
	{
		name = new JLabel();
		avatar = new JLabel();
		coin = new JLabel("", ImageShop.ICON_COIN, 0);
		bankroll = new JLabel();
		clockLabel = new JLabel();
		clockLabel.setFont(new Font("Arial", Font.BOLD, 18));
		help = new JLabel("", ImageShop.ICON_BUTTON_HELP, 0);
		quitApplication = new JLabel("", ImageShop.ICON_BUTTON_QUIT_APPLICATION, 0);
		quitGame = new JLabel("", ImageShop.ICON_BUTTON_QUIT_GAME, 0);
		volumeIcon = new JLabel("\u266B");
		volumeIcon.setFont(new Font("Arial", Font.BOLD, 18));

		volumeSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 80);
		SoundEngine.getInstance().setVolume(80);
		volumeSlider.setPaintLabels(false);

		ft = new SimpleDateFormat("HH:mm:ss");

		Box profil = Box.createHorizontalBox();
		profil.add(avatar);
		profil.add(Box.createHorizontalStrut(20));
		profil.add(name);
		profil.add(Box.createHorizontalStrut(20));
		profil.add(coin);
		profil.add(Box.createHorizontalStrut(5));
		profil.add(bankroll);
		profil.add(Box.createHorizontalStrut(20));
		profil.add(help);

		Box info = Box.createHorizontalBox();
		info.add(volumeIcon);
		info.add(volumeSlider);
		info.add(Box.createHorizontalStrut(20));
		info.add(clockLabel);
		info.add(Box.createHorizontalStrut(20));
		info.add(quitGame);
		info.add(Box.createHorizontalStrut(20));
		info.add(quitApplication);
		info.add(Box.createHorizontalStrut(10));

		setLayout(new GridLayout(1, 3));

		add(profil);
		add(new JLabel(ImageShop.ICON_LOGO_TOPBAR));
		add(info);
	}

	private void apparence()
	{
		setBackground(ColorShop.PF_BACKGROUND);
		name.setForeground(ColorShop.PF_RED);
		bankroll.setForeground(ColorShop.PF_GOLD_COLOR);
		clockLabel.setForeground(Color.WHITE);
		volumeIcon.setForeground(Color.WHITE);
		try
		{
			name.setFont(ButtonTools.getButtonFont(false).deriveFont(26f));
			bankroll.setFont(ButtonTools.getButtonFont(false).deriveFont(26f));
			name.setFont(ButtonTools.getButtonFont(false).deriveFont(26f));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void control()
	{
		help.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				showHelp();
			}
		});

		quitApplication.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				System.exit(0);
			}
		});

		quitGame.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (frameMain != null)
				{
					frameMain.gameToMainMenu();
				}
			}
		});

		volumeSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				SoundEngine.getInstance().setVolume((volumeSlider.getValue()));
			}
		});
	}

	private void showHelp()
	{
		JOptionPane.showMessageDialog(this, RULES, "Poker Rules, Texas Hold'Em No Limit", JOptionPane.INFORMATION_MESSAGE, ImageShop.ICON_BUTTON_HELP);
	}

	private ActionListener	updateClockAction	= new ActionListener()
												{
													@Override
													public void actionPerformed(ActionEvent e)
													{
														clockLabel.setText(ft.format(new Date()));
													}
												};
}

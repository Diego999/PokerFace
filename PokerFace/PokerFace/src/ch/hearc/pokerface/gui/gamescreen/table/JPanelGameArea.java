
package ch.hearc.pokerface.gui.gamescreen.table;

import javax.swing.Box;
import javax.swing.JPanel;

import ch.hearc.pokerface.gui.gamescreen.player.PlayerComponent;
import ch.hearc.pokerface.gui.gamescreen.table.board.GameInformation;


public class JPanelGameArea extends JPanel
{
	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private GameInformation gameInformation;
	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/
	public JPanelGameArea(GameInformation gameInformation, int nbPlayers)
	{
		Box box = Box.createVerticalBox();
		Box playerBox = Box.createHorizontalBox();
		this.gameInformation = gameInformation;
		for(int i = 0; i < nbPlayers; ++i)
		{
			playerBox.add(new PlayerComponent("Player " + i, 10000));
			playerBox.add(Box.createHorizontalStrut(15));
		}
		box.add(playerBox);
		box.add(Box.createVerticalStrut(50));
		box.add(gameInformation);
		add(box);

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


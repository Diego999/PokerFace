
package ch.hearc.pokerface.gui.gamescreen.table.board;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class GameInformation extends JPanel
{
	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/
	public GameInformation()
	{
		for(int i = 0; i < 5; ++i)
		{
			add(new JLabel("info " + i));
		}
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


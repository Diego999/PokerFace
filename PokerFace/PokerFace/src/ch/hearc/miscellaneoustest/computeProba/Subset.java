
package ch.hearc.miscellaneoustest.computeProba;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Subset implements Iterable<String>
{
	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private List<String> subset;

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public Subset()
	{
		subset = new ArrayList<String>();
		subset.add("Ap");
		subset.add("2p");
		subset.add("3p");
		subset.add("4p");
		subset.add("5p");
		subset.add("6p");
		subset.add("7p");
		subset.add("8p");
		subset.add("9p");
		subset.add("Tp");
		subset.add("Jp");
		subset.add("Qp");
		subset.add("Kp");
		subset.add("At");
		subset.add("2t");
		subset.add("3t");
		subset.add("4t");
		subset.add("5t");
		subset.add("6t");
		subset.add("7t");
		subset.add("8t");
		subset.add("9t");
		subset.add("Tt");
		subset.add("Jt");
		subset.add("Qt");
		subset.add("Kt");
		subset.add("Ac");
		subset.add("2c");
		subset.add("3c");
		subset.add("4c");
		subset.add("5c");
		subset.add("6c");
		subset.add("7c");
		subset.add("8c");
		subset.add("9c");
		subset.add("Tc");
		subset.add("Jc");
		subset.add("Qc");
		subset.add("Kc");
		subset.add("AC");
		subset.add("2C");
		subset.add("3C");
		subset.add("4C");
		subset.add("5C");
		subset.add("6C");
		subset.add("7C");
		subset.add("8C");
		subset.add("9C");
		subset.add("TC");
		subset.add("JC");
		subset.add("QC");
		subset.add("KC");
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public void remove(String key)
	{
		subset.remove(key);
	}

	@Override
	public Iterator<String> iterator()
	{
		return subset.listIterator();
	}
}


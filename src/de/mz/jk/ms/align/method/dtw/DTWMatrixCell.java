package de.mz.jk.ms.align.method.dtw;
/**
 * alignment matrix cell
 * @author J.Kuharev
 */
public class DTWMatrixCell 
{
	/** alignment score */
	public int score = 0;
	
	/** precursor on left */
	public boolean fromLeft = false;
	
	/** precursor on top */
	public boolean fromTop = false;
	
	/** diagonal precursor */
	public boolean fromDiagonal = false;
}

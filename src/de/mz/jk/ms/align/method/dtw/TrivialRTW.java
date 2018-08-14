/** ISOQuant, isoquant.plugins.processing.expression.align.linear, 26.07.2011*/
package de.mz.jk.ms.align.method.dtw;

import de.mz.jk.jsix.lists.XSubList;
import de.mz.jk.ms.align.com.AlignmentMatcher;
import de.mz.jk.ms.align.com.LinkedAlignment;

/**
 * <h3>{@link TrivialRTW}</h3>
 * trivial alignment of two sequences
 * one of the sequences must have size of 0 or 1
 * @author Joerg Kuharev
 * @version 26.07.2011 17:20:42
 */
public class TrivialRTW<TYPE>
{
    private AlignmentMatcher<TYPE> matcher = null;

	/** matcher is needed to validate peak equality while constructing trivial alignments */
	public TrivialRTW(AlignmentMatcher<TYPE> matcher){this.matcher = matcher;}
	
	/**
     * trivial alignment of two sequences.<br>
     * one of the sequences has size of 0 or 1<br>
     * thus the result will contain maximum one match,
     * all other positions are aligned with gaps
     * @param X first sequence 
     * @param Y second sequence
     * @param useLeftPath should Y be shifted maximally to the left in case of multiple matching possibilities.<pre>
     * if true the first occurance in X and last occurance in Y will match, e.g.
     * X: ---x		X: xaax
     * Y: xaax	or	Y: x---
     * otherwise the last occurance in X and first occurance in Y will match, e.g.
     * X: x---		X: xaax
     * Y: xaax	or	Y: ---x</pre>
     * @return the alignment
     */
	public LinkedAlignment<TYPE> getAlignment(XSubList<TYPE> X, XSubList<TYPE> Y, boolean useLeftPath)
	{
		LinkedAlignment<TYPE> res = null;

		int Xn = (X==null) ? 0 : X.size();
		int Yn = (Y==null) ? 0 : Y.size();

		LinkedAlignment<TYPE> aln = null;

		if(Xn==0) 
		{ // all Y aligned with gaps
			for(int i=0; i<Yn; i++)
			{
				aln = new LinkedAlignment<TYPE>( null, Y.get(i) );
				res = (res == null) ? aln : res.append( aln );
			}
		}
		else if(Xn==1)
		{ // all Y aligned with gaps having maximum one XY-match
			TYPE x = X.get(0);
			for(int i=0; i<Yn; i++)
			{
				int yi = useLeftPath ? Yn - i - 1 : i;
				TYPE y = Y.get( yi );
				if( matcher.match(x, y) ) 
				{
					aln = new LinkedAlignment<TYPE>( x, y );
					x = null;
				}
				else
				{
					aln = new LinkedAlignment<TYPE>(null, y);
				}
				res = (res == null) ? aln : useLeftPath ? aln.append(res) : res.append( aln );
			}
			// no XY-match found then add X+gap
			if( x!=null ) res.append( new LinkedAlignment<TYPE>(x, null) );
		}
		else if(Yn==0)
		{ // all X aligned with gaps
			for(int i=0; i<Xn; i++)
			{
				aln = new LinkedAlignment<TYPE>( X.get(i), null );
				res = (res == null) ? aln : res.append( aln );
			}
		}
		else
		{ // all X aligned with gaps having maximum one XY-match
			TYPE y = Y.get(0);
			for(int i=0; i<X.size(); i++)
			{
				int xi = useLeftPath ? i : Xn - i - 1; 
				TYPE x = X.get(xi);
				if( matcher.match(x, y) ) 
				{
					aln = new LinkedAlignment<TYPE>( x, y );
					y = null;
				}
				else
				{
					aln = new LinkedAlignment<TYPE>(x, null);
				}
				res = (res == null) ? aln : useLeftPath ? res.append( aln ) : aln.append(res);
			}
			if( y!=null ) res.append( new LinkedAlignment<TYPE>(null, y) );
		}

		return res;
	}
}

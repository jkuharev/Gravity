/** ISOQuant, isoquant.plugins.processing.expression.align.linear, 29.07.2011*/
package de.mz.jk.ms.align.method.dtw.linear;

import de.mz.jk.jsix.lists.XSubList;
import de.mz.jk.ms.align.com.AlignmentMatcher;

/**
 * <h3>{@link SimpleLevenshteinDistanceCalculator}</h3>
 * @author Joerg Kuharev
 * @version 29.07.2011 12:32:06
 */
public class SimpleLevenshteinDistanceCalculator<TYPE> extends LevenshteinDistanceCalculator<TYPE>
{
	/**
	 * @param matcher
	 */
	public SimpleLevenshteinDistanceCalculator(AlignmentMatcher<TYPE> matcher)
	{
		super(matcher);
	}

	/**
	 * forward score calulation.<br>
	 * the calculation is done by running 
	 * from upper left to bottom right    
	 * <pre>
	 * -- X0 ... Xn
	 * |
	 * Y0
	 * .		diagonal    | vertical
	 * .		------------+-----------
	 * Ym		horizontal  | current
	 * </pre>
	 *  
	 * @param topSeq sequence with the fixed length 
	 * @param leftSeq sequence with the variable length
	 * @return score vector having length of X
	 */
    @Override
    public int[] getForwardDistanceVector(XSubList<TYPE> topSeq, XSubList<TYPE> leftSeq)
	{
		int n = topSeq.size();
		int m = leftSeq.size();
		
		int[] T =  new int[n+1];
		
		// init score vector
		for(int i=0; i<=n; i++){ T[i]= i * matcher.SCORE_GAP; }
		
		int diagonalScore = 0; 
		int horizontalScore = 0;
		int verticalScore = 0;
		int currentScore = 0;
		
		TYPE _Y = null;
		
		// in left sequence from top to bottom
		for(int y=0; y<m; y++)
		{
			_Y = leftSeq.get(y);
			
			diagonalScore = T[0];
			T[0] = diagonalScore + matcher.SCORE_GAP;
			
			// in top sequence from left to right
			for(int x=1; x<=n; x++)
			{
				verticalScore = T[x];
				horizontalScore = T[x-1];			
				currentScore = matcher.getScore( topSeq.get(x-1), _Y, horizontalScore, verticalScore, diagonalScore );
				diagonalScore = verticalScore;
				T[x] = currentScore;
			}
		}
		
		return T;
	}
	
    
	/**
	 * backward score calculation.<br>
	 * the calculation is done by running 
	 * from bottom right to upper left    
	 * <pre>
	 * -- X0 ... Xn
	 * |
	 * Y0
	 * .		current  | horizontal
	 * .		---------+-----------
	 * Ym		vertical | diagonal
	 * </pre>
	 *  
	 * @param topSeq sequence with the fixed length 
	 * @param leftSeq sequence with the variable length
	 * @return score vector having length of X
	 */
    @Override 
	public int[] getBackwardDistanceVector(XSubList<TYPE> topSeq, XSubList<TYPE> leftSeq)
	{
		int n = topSeq.size();
		int m = leftSeq.size();
		
		int[] T = new int[n+1];
		
		// init score vector
		for(int i=0; i<=n; i++){ T[n-i]= i*matcher.SCORE_GAP; }
		
		int diagonalScore = 0;
		int horizontalScore = 0;
		int verticalScore = 0;
		int currentScore = 0;
		
		TYPE _Y = null;
		
		// in left sequence from bottom to top
		for(int y=m-1; y>=0; y--)
		{
			_Y = leftSeq.get(y);
			
			diagonalScore = T[n];
			T[n]= diagonalScore + matcher.SCORE_GAP;
			
			// in top sequence from right to left
			for(int x=n-1; x>=0; x--)
			{
				verticalScore = T[x];
				horizontalScore = T[x+1];
				currentScore = matcher.getScore( topSeq.get(x), _Y, horizontalScore, verticalScore, diagonalScore );
				diagonalScore = verticalScore;
				T[x] = currentScore;
			}
		}
		
		return T;
	}
	
}

/** ISOQuant, isoquant.plugins.processing.expression.align.linear, 29.07.2011*/
package de.mz.jk.ms.align.method.dtw.linear;

import java.util.List;

import de.mz.jk.jsix.lists.XSubList;
import de.mz.jk.ms.align.com.AlignmentMatcher;

/**
 * <h3>{@link BandedLevenshteinDistanceCalculator}</h3>
 * @author Joerg Kuharev
 * @version 29.07.2011 12:32:06
 */
public class BandedLevenshteinDistanceCalculator<TYPE> extends LevenshteinDistanceCalculator<TYPE>
{
	protected List<int[]> colRanges = null;
	
	/**
	 * @param matcher
	 * @param columnRanges
	 */
	public BandedLevenshteinDistanceCalculator(AlignmentMatcher<TYPE> matcher, List<int[]> columnRanges)
	{
		super( matcher );
		setColRanges(columnRanges);
	}

	/**
	 * @param colRanges the colRanges to set
	 */
	protected void setColRanges(List<int[]> columnRanges)
	{
		this.colRanges = columnRanges;
	}
	
	/**
	 * transform absolute column ranges into relative subspace by given X and Y sublists<br>
	 * <pre>
	 * absolute space and known column ranges
	 *   0 1 2 3 4 5 6 7 8 9
	 * 0 * * * * *
	 * 1   * * * *
	 * 2     * * * * *
	 * 3     * * * * * *
	 * 4       * * * * *
	 * 5         * * * * *
	 * 6         * * * * *
	 * 7           * * * *
	 * 8           * * * * *
	 * 9               * * *
	 * 
	 * while looking into subspace x[6...9], y[7..9]
	 *     6 7 8 9 absolute indexes
	 *     0 1 2 3 relative indexes
	 * 7=0 * * *
	 * 8=1 * * * *
	 * 9=2   * * * 
	 * 
	 * absolute column ranges are 
	 * 	1: sliced by absolute subY-coordinates 
	 * 	2: tranformed by absolute subX begin coordinate
	 * 	3: cutted to fit into subX   
	 * </pre>
	 * @param topSeq
	 * @param leftSeq
	 * @return
	 */
	protected int[][] getRelativeRange(XSubList<?> topSeq, XSubList<?> leftSeq)
	{
		// create sliced range
		int[][] range = new int[leftSeq.size()][];
		
		int yShift = leftSeq.getAbsoluteStartIndex();
		int xShift = topSeq.getAbsoluteStartIndex();
		
		int n = topSeq.size() - 1;
		int[] absRange;
		int _r, r_;
		for(int i=0; i<range.length; i++)
		{
			// get column's range
			absRange = colRanges.get(i + yShift);
			
			// tranformed by absolute subX begin coordinate
			_r = absRange[0] - xShift;
			r_ = absRange[1] - xShift;
			
			// cut to fit into subX
			if(_r<0) _r=0;
			if(r_>n) r_=n;

			// save range
			range[i] = new int[]{_r, r_};
		}
		
		// range[0][0] = 0;
		// range[range.length-1][1] = n;
				
		return range;
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
		int diagonalScore = 0; 
		int horizontalScore = 0;
		int verticalScore = 0;
		int currentScore = 0;
		TYPE _Y = null;
		int[][] relRange = getRelativeRange(topSeq, leftSeq);
		int[] curRange = relRange[0];
		int[] oldRange = curRange;
		
		// initialize score vector
		int _r = curRange[0];
		int r_ = curRange[1]+1;		
		for(int i=0; i<=n; i++)
		{ 
			T[i]= (i>=_r && i<=r_) ? i * matcher.SCORE_GAP : matcher.SCORE_EDGE; 
		}

		for(int y=0; y<m; y++)
		{
			_Y = leftSeq.get(y);
			
			oldRange = curRange;
			curRange = relRange[y];
			
			// set cells before range begin to SCORE_BAD
			for(int x=oldRange[0]; x<curRange[0]; x++)
			{
				T[x] = matcher.SCORE_EDGE;
			}
			
			// set cells between old range end(exclusive) and new range end(inclusive) to SCORE_BAD
			for(int x=oldRange[1]+1; x<=curRange[1]; x++)
			{ 
				T[x+1] = matcher.SCORE_EDGE;
			}
			
			diagonalScore = T[curRange[0]];
			T[curRange[0]] = diagonalScore + matcher.SCORE_GAP;

			for(int x=curRange[0]; x<=curRange[1]; x++)
			{
				verticalScore = T[x+1];
				horizontalScore = T[x];			
				currentScore = matcher.getScore( topSeq.get(x), _Y, horizontalScore, verticalScore, diagonalScore );
				diagonalScore = verticalScore;
				T[x+1] = currentScore;
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
		int[] T =  new int[n+1];
		int diagonalScore = 0; 
		int horizontalScore = 0;
		int verticalScore = 0;
		int currentScore = 0;
		TYPE _Y = null;
		int[][] relRange = getRelativeRange(topSeq, leftSeq);
		int[] curRange = relRange[m-1];
		int[] oldRange = curRange;
		
		// initialize score vector
		int _r = curRange[0];
		int r_ = curRange[1]+1;		
		for(int i=0; i<=n; i++)
		{
			T[i]= (i>=_r && i<=r_) ? (n-i) * matcher.SCORE_GAP : matcher.SCORE_EDGE;
		}

		for(int y=m-1; y>=0; y--)
		{
			_Y = leftSeq.get(y);
			
			oldRange = curRange;
			curRange = relRange[y];
			
			for(int x=curRange[0]+1; x<oldRange[0]; x++)
			{
				T[x] = matcher.SCORE_EDGE;
			}
			
			// set cells between old range end(exclusive) and new range end(inclusive) to SCORE_BAD
			for(int x=curRange[1]+1; x<=oldRange[1]; x++)
			{ 
				T[x+1] = matcher.SCORE_EDGE;
			}
			
			diagonalScore = T[curRange[1]+1];
			T[curRange[1]+1] = diagonalScore + matcher.SCORE_GAP;
			
			for(int x=curRange[1]; x>=curRange[0]; x--)
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

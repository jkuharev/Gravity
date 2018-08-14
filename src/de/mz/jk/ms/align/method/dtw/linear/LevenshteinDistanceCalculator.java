/** ISOQuant, isoquant.plugins.processing.expression.align.linear, 26.07.2011*/
package de.mz.jk.ms.align.method.dtw.linear;

import de.mz.jk.jsix.lists.XSubList;
import de.mz.jk.ms.align.com.AlignmentMatcher;

/**
 * <h3>{@link LevenshteinDistanceCalculator}</h3>
 * @author Joerg Kuharev
 * @version 26.07.2011 16:51:23
 */
public abstract class LevenshteinDistanceCalculator <TYPE>
{
	protected AlignmentMatcher<TYPE> matcher = null;

	/**
	 * construct a levenshtein distance calculator
	 * and assign a matcher for checking the similarity of sequence elements
	 * @param matcher
	 */
	public LevenshteinDistanceCalculator(AlignmentMatcher<TYPE> matcher)
	{
		this.matcher = matcher;
	}

	/**
	 * calculate forward levenshtein distance 
	 * from left to right and from top to bottom
	 * @param topSeq
	 * @param leftSeq
	 * @return last calculated distance vector
	 */
	public abstract int[] getForwardDistanceVector(XSubList<TYPE> topSeq, XSubList<TYPE> leftSeq);

	/**
	 * calculate backward levenshtein distance 
	 * from right to left and from bottom to top
	 * @param topSeq
	 * @param leftSeq
	 * @return last calculated distance vector
	 */
	public abstract int[] getBackwardDistanceVector(XSubList<TYPE> topSeq, XSubList<TYPE> leftSeq);
	
	/**
	 * find outer min value occurances in a vector 
	 * @param vector
	 * @return array with the length of two containing first and last index of minimum value
	 */
	public int[] indexesOfMin(int[] vector)
	{
		int firstMin = 0;
		int lastMin = 0;
		for(int i=1; i<vector.length; i++)
		{
			if( vector[i]<vector[firstMin] )
				firstMin = lastMin = i;
			else 
			if( vector[i]==vector[firstMin] )
				lastMin = i;
		}
		return new int[]{firstMin, lastMin};
	}
	
	/**
	 * summarize two vectors,
	 * vectors must be equally size
	 * @param a
	 * @param b
	 * @return sum vector
	 */
	public int[] sum(int[] a, int[] b)
	{
		int n = a.length;
		int[] sum = new int[n];
		for(int i=0; i<n; i++) sum[i] = a[i] + b[i];
		return sum;
	}
}

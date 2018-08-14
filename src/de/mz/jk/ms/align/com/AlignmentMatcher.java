/** ISOQuant, isoquant.plugins.processing.expression.align.linear, 26.07.2011*/
package de.mz.jk.ms.align.com;

/**
 * <h3>{@link AlignmentMatcher}</h3>
 * @author Joerg Kuharev
 * @version 26.07.2011 17:56:38
 */
abstract public class AlignmentMatcher <TYPE>
{
	public int SCORE_MATCH = -1;
	public int SCORE_MISMATCH = 3;
	public int SCORE_GAP = 1;
	
	public int SCORE_INIT = 0;
	public int SCORE_EDGE = SCORE_GAP * 1000000;
		
	/**
	 * create matcher and define parameters
	 * @param scoreMatch
	 * @param scoreMismatch
	 * @param scoreGap
	 * @param maxDeltaMassPPM
	 */
	public AlignmentMatcher(int scoreMatch, int scoreMismatch, int scoreGap)
	{
		this.SCORE_MATCH = scoreMatch;
		this.SCORE_MISMATCH = scoreMismatch;
		this.SCORE_GAP = scoreGap;
	}
	
	abstract public boolean match(TYPE a, TYPE b);
	
	/**
	 * calculate score from horizontal, vertical and diagonal precursor cells using rules:<br>
	 * <pre>
	 * 				| diagonal + (a matches b) ? SCORE_MATCH : SCORE_MISMATCH 
	 * score = min	| horizontal + SCORE_GAP
	 * 				| vertical + SCORE_GAP
	 * </pre>
	 * @param a first peak
	 * @param b second peak
	 * @param horizontalPrecursorScore
	 * @param verticalPrecursorScore
	 * @param diagonalPrecursorScore
	 * @return calculated score
	 */
	public int getScore( TYPE a, TYPE b, int horizontalPrecursorScore, int verticalPrecursorScore, int diagonalPrecursorScore )
	{
		return
			min(
				diagonalPrecursorScore + ( ( match(a, b) ) ? SCORE_MATCH : SCORE_MISMATCH ),
				horizontalPrecursorScore + SCORE_GAP,
				verticalPrecursorScore + SCORE_GAP
			);
	}
	
	/**
	 * get minimum value
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public int min(int a, int b, int c)
	{ 
		return (a<b && a<c) ? a : ((b<c) ? b : c); 
	}
}

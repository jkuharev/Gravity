package de.mz.jk.ms.align.method.dtw;

import java.util.ArrayList;
import java.util.List;

import de.mz.jk.jsix.lists.XSubList;
import de.mz.jk.jsix.math.interpolation.Interpolator;
import de.mz.jk.jsix.math.interpolation.LinearInterpolator;
import de.mz.jk.jsix.math.matrix.Matrix;
import de.mz.jk.ms.align.com.*;

/**
 * abstract Retention Time Warping
 * @author Jörg Kuharev
 */
public abstract class RTW implements Runnable
{
	private static final double defaultMaxDeltaMass = 10.0;
	private static final double defaultMaxDeltaDrift = 1.0;
	public final int DEFAULT_SCORE_INIT = 0;
	public final int DEFAULT_SCORE_MATCH = -1;
	public final int DEFAULT_SCORE_MISMATCH = 3;
	public final int DEFAULT_SCORE_GAP = 1;
	protected boolean useLeftPath = true;
	protected LinkedAlignment<IMSPeak> aln = null;
	protected PeakMatcher matcher = null;
	protected XSubList<IMSPeak> top = null;
	protected XSubList<IMSPeak> left = null;
	protected int nCols = 0;
	protected int nRows = 0;
	protected Matrix<DTWMatrixCell> M = null;
	public boolean DEBUG = false;

	protected RTW(List<IMSPeak> leftPeaks, List<IMSPeak> topPeaks)
	{
		this(new XSubList<IMSPeak>(leftPeaks), new XSubList<IMSPeak>(topPeaks));
	}

	protected RTW(XSubList<IMSPeak> leftPeaks, XSubList<IMSPeak> topPeaks)
	{
		initPeaks(leftPeaks, topPeaks);
		matcher = new PeakMatcher(
				DEFAULT_SCORE_MATCH,
				DEFAULT_SCORE_MISMATCH,
				DEFAULT_SCORE_GAP,
				defaultMaxDeltaMass,
				defaultMaxDeltaDrift
				);
	}

	protected RTW(XSubList<IMSPeak> leftPeaks, XSubList<IMSPeak> topPeaks, PeakMatcher matcher)
	{
		initPeaks(leftPeaks, topPeaks);
		this.matcher = matcher;
	}

	/**
	 * @param leftPeaks
	 * @param topPeaks
	 */
	private void initPeaks(XSubList<IMSPeak> leftPeaks, XSubList<IMSPeak> topPeaks)
	{
		top = topPeaks;
		left = leftPeaks;
		nRows = left.size();
		nCols = top.size();
	}

	/**
	 * set path finding mode
	 * @param pathMode one of constant values:
	 * 		{@link DTWPathDescription.LEFT} or 
	 * 		{@link DTWPathDescription.RIGHT}
	 */
	public void setPathMode(DTWPathDescription pathMode)
	{
		useLeftPath = (pathMode == DTWPathDescription.LEFT);
	}

	/**
	 * set ppm threshold<br>
	 * use <b>setMaxDeltaMassPPM(5);</b> 
	 * for use max allowed difference between two masses of 5/1000000
	 * @param ppm
	 */
	public void setMaxDeltaMassPPM(double ppm)
	{
		matcher.setMaxDeltaMassPPM(ppm);
	}

	/**
	 * pass max delta drift time to peak matcher
	 * @param maxDeltaDrift
	 */
	public void setMaxDeltaDriftTime(double maxDeltaDrift)
	{
		matcher.setMaxDeltaDrift(maxDeltaDrift);
	}

	/**
	 * @param topRowID identifier of IMSPeak-List used on top of dynamic programming matrix
	 * @param leftRowID identifier of IMSPeak-List used on left of dynamic programming matrix
	 * @return PeakAlignment identified by top/left row names
	 */
	public abstract PairwiseAlignment<IMSPeak> getPeakAlignment(Object topRowID, Object leftRowID);

	/**
	 * convert a pairwise peak alignment to linear interpolator
	 * @param refPeaks peaks of the reference run
	 * @param keyPeaks peaks of the run beeing mapped to the reference
	 * @return
	 */
	protected Interpolator convertAlignment2Interpolator(List<IMSPeak> refPeaks, List<IMSPeak> keyPeaks) throws Exception
	{
		// check for zero peak lists and return null
		if (refPeaks == null || keyPeaks == null || refPeaks.size() < 1 || keyPeaks.size() < 1) throw new Exception( "empty alignment result" );
		List<Double> refValues = new ArrayList<Double>();
		List<Double> keyValues = new ArrayList<Double>();
		int n = Math.min(refPeaks.size(), keyPeaks.size());
		// System.out.print( n + " matches" );
		// copy time values into T and L
		for (int i = 0; i < n; i++)
		{
			IMSPeak pt = refPeaks.get(i);
			IMSPeak pl = keyPeaks.get(i);
			if (pt != null && pl != null)
			{
				refValues.add( (double)pt.rt );
				keyValues.add( (double)pl.rt );
			}
		}
		int lastIndex = keyValues.size() - 1;

		double lastRT = Math.max( refValues.get( lastIndex ), keyValues.get( lastIndex ) );
		
		// set interpolator's upper boundary to the next order of magnitude
		double bigNumber = Math.pow( 10, (int)Math.log10( lastRT ) + 2 );
		return new LinearInterpolator( keyValues, refValues, 0, 0, bigNumber, bigNumber );
	}

	/**
	 * get algorithm results as a linear interpolating representation 
	 * for further prediction of missing / not initialized values
	 * @param useLeftPath preferred path = (preferTopPath==true) ? the top path : the left path 
	 * @param topRowIsReference reference sequence = (topRowIsReference==true) ? sequence at the top : sequence at the left
	 * @return interpolator
	 */
	public Interpolator getInterpolator(boolean topRowIsReference) throws Exception
	{
		PairwiseAlignment<IMSPeak> a = getPeakAlignment( "top", "left" );
		return (topRowIsReference)
				? convertAlignment2Interpolator(a.getRow("top"), a.getRow("left"))
				: convertAlignment2Interpolator(a.getRow("left"), a.getRow("top"));
	}
}

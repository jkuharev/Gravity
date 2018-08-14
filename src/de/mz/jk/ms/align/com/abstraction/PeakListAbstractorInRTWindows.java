package de.mz.jk.ms.align.com.abstraction;

import java.util.ArrayList;
import java.util.List;

import de.mz.jk.jsix.libs.XJava;
import de.mz.jk.ms.align.com.IMSPeak;
import de.mz.jk.ms.align.com.IMSPeakComparator;

/**
 * split the list of peaks into equally sized parts along RT and abstract resulting sublists separately.
 * This method ensures a better distribution of resulting abstraction along RT axis.
 * <h3>{@link PeakListAbstractorInRTWindows}</h3>
 * @author jkuharev
 * @version July 25, 2016 2:59:16 PM
 */
public abstract class PeakListAbstractorInRTWindows extends PeakListAbstractor
{
	public static int DefaultNumberOfTimeWindows = 5;
	private int numberOfTimeWindows = DefaultNumberOfTimeWindows;
	private List<PeakListAbstractor> subPeaksAbstractors = null;

	/**
	 * by user defined number of time windows
	 * @param parentPeakList
	 * @param nWindows
	 */
	public PeakListAbstractorInRTWindows(List<IMSPeak> parentPeakList, int nWindows)
	{
		super( parentPeakList );
		numberOfTimeWindows = nWindows;
		workingPeakList.sort( IMSPeakComparator.RT_ASC );
		List<List<IMSPeak>> peakListWindows = XJava.splitList( workingPeakList, nWindows );
		subPeaksAbstractors = new ArrayList<PeakListAbstractor>( nWindows );
		for ( int i = 0; i < numberOfTimeWindows; i++ )
		{
			List<IMSPeak> winPeaks = peakListWindows.get( i );
			PeakListAbstractor winAbs = getSingleWindowAbstractor( winPeaks );
			subPeaksAbstractors.add( winAbs );
		}
	}

	/** 
	 * to implement in a subclass: 
	 * an appropriate abstractor that is used to 
	 * abstract a part of parent peak list 
	 * inside a single time window
	 */
	protected abstract PeakListAbstractor getSingleWindowAbstractor(List<IMSPeak> peaksInThisTimeWindow);

	/**
	 * abstract original peak list by selecting given number of peaks,
	 * the selection is done in equally distributed parts in the predefined time windows.
	 * ATTENTION: resulting peaks may be sorted in the wrong order!!!
	 */
	@Override protected List<IMSPeak> getSubPeaks(int sublistSize)
	{
		List<IMSPeak> res = new ArrayList<IMSPeak>( sublistSize );
		// limit to max
		sublistSize = Math.min( sublistSize, peakListSize );
		// size of each part
		int partSize = (int)Math.ceil( sublistSize / (double)numberOfTimeWindows );
		for ( PeakListAbstractor winAbs : subPeaksAbstractors )
		{
			List<IMSPeak> subPeaks = winAbs.getSubPeaks( partSize );

			res.addAll( subPeaks );
		}
		return res.subList( 0, sublistSize );
	}
}
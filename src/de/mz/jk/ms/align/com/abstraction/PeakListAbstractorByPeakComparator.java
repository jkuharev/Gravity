package de.mz.jk.ms.align.com.abstraction;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.mz.jk.ms.align.com.IMSPeak;

public class PeakListAbstractorByPeakComparator extends PeakListAbstractor
{
	private Comparator<IMSPeak> subPeaksComparator = null;

	public PeakListAbstractorByPeakComparator(List<IMSPeak> parentPeakList, Comparator<IMSPeak> peakComparator)
	{
		super( parentPeakList );
		this.subPeaksComparator = peakComparator;
		Collections.sort( workingPeakList, subPeaksComparator );
	}

	/**
	 * resulting peaks are in the order provided by the user defined peak comparator implementation! 
	 */
	@Override public List<IMSPeak> getSubPeaks(int sublistSize)
	{
		return getFirstWorkingPeaks( sublistSize );
	}
}

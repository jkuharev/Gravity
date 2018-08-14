package de.mz.jk.ms.align.com.abstraction;

import java.util.List;

import de.mz.jk.ms.align.com.IMSPeak;

/**
 * peak list abstractor that splits parent peaks into equally sized sublists along the RT Axis 
 * and abstracts peaks of each sublist by highest intensity separately
 * <h3>{@link PeakListAbstractorInRTWindowsByHighestIntensity}</h3>
 * @author jkuharev
 * @version Jul 25, 2016 2:59:04 PM
 */
public  class PeakListAbstractorInRTWindowsByHighestIntensity extends PeakListAbstractorInRTWindows
{
	/**
	 * abstract peaks by splitting the original peak list into default number of equally sized parts (aka time windows).
	 * In every time window, the abstraction is done by selecting most intense peaks.
	 * @param parentPeakList
	 */
	public PeakListAbstractorInRTWindowsByHighestIntensity(List<IMSPeak> parentPeakList)
	{
		this( parentPeakList, DefaultNumberOfTimeWindows );
	}

	/**
	 * abstract peaks by splitting the original peak list into given number of equally sized parts (aka time windows).
	 * In every time window, the abstraction is done by selecting most intense peaks.
	 * @param parentPeakList
	 * @param numberOfRtWindows
	 */
	public PeakListAbstractorInRTWindowsByHighestIntensity(List<IMSPeak> parentPeakList, int numberOfRtWindows)
	{
		super( parentPeakList, numberOfRtWindows );
	}

	@Override protected PeakListAbstractor getSingleWindowAbstractor(List<IMSPeak> peaksInThisTimeWindow)
	{
		return new PeakListAbstractorByHighestIntensity( peaksInThisTimeWindow );
	}
}
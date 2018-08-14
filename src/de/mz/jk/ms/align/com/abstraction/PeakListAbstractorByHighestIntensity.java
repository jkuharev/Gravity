package de.mz.jk.ms.align.com.abstraction;

import java.util.List;

import de.mz.jk.ms.align.com.IMSPeak;
import de.mz.jk.ms.align.com.IMSPeakComparator;

/**
 * peak list abstractor using most intense peaks 
 * <h3>{@link PeakListAbstractorByHighestIntensity}</h3>
 * @author kuharev
 * @version 25.02.2014 14:05:41
 */
public class PeakListAbstractorByHighestIntensity extends PeakListAbstractorByPeakComparator
{
	public PeakListAbstractorByHighestIntensity(List<IMSPeak> parentPeakList)
	{
		super( parentPeakList, IMSPeakComparator.INTENSITY_DESC );
	}
}
package de.mz.jk.ms.align.com.abstraction;

import java.util.List;

import de.mz.jk.ms.align.com.IMSPeak;
import de.mz.jk.ms.align.com.IMSPeakComparator;

/**
 * peak list abstractor by heaviest peaks
 * <h3>{@link PeakListAbstractorByHighestMass}</h3>
 * @author kuharev
 * @version 25.02.2014 14:07:09
 */
public  class PeakListAbstractorByHighestMass extends PeakListAbstractorByPeakComparator
{
	public PeakListAbstractorByHighestMass(List<IMSPeak> parentPeakList)
	{
		super( parentPeakList, IMSPeakComparator.MASS_DESC );
	}
}
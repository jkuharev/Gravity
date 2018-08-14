package de.mz.jk.ms.align.com.abstraction;

import java.util.List;

import de.mz.jk.ms.align.com.IMSPeak;

public  enum AbstractionMethod
{
	/** abstract representation of a peak list by most intense peaks.
	 * This method may result in the selection of many noise peaks. */
	INTENSITY
	
	/** abstract representation of a peak list by heaviest peaks.
	 * This method may select only lately eluting peaks as they are usually heavier than early eluting analyts. */
	,MASS
	
	/** abstract representation of a peak list by most intense peaks in time windows.
	 * This method reduces the risk of noise selection. */
	,INTENSITY_IN_RT_WINDOWS
	
	/** abstract representation of a peak list by heaviest peaks in time windows. 
	 * This method distributes the risk of chromatography bias by taking heaviest peaks at different time points.
	 * It looks like this is the most reproducible method to abstract peaks across multiple lcms runs of an experiment!!! */
	,MASS_IN_RT_WINDOWS

	// /** abstract representation of a peak list by peaks with the highest ion
	// mobility drift time. */
	// DRIFT,
	// /** abstract representation of a peak list by peaks with the highest ion
	// mobility drift time in separate retention time windows. */
	// DRIFT_IN_TIME_WINDOWS
	;
	public static AbstractionMethod fromString(String method)
	{
		String m = method.toLowerCase();
		if (m.contains( "inten" ))
		{
			return m.contains( "win" ) ? INTENSITY_IN_RT_WINDOWS: INTENSITY; 
		}
		else
		{
			return m.contains( "mass" ) && !m.contains( "win" ) ? MASS : MASS_IN_RT_WINDOWS;
		}
	}
	
	/**
	 * generates an an appropriate abstractor by given method.
	 * For the rt windowed methods, the default numbers of windows are used.
	 * @param peaks the original list of lcmc peaks to be abstracted
	 * @param method the abstraction method
	 * @return
	 */
	public PeakListAbstractor toPeaksAbstractor( List<IMSPeak> peaks )
	{
		switch (this)
		{
			case INTENSITY:
				return new PeakListAbstractorByHighestIntensity( peaks );
			case MASS:
				return new PeakListAbstractorByHighestMass( peaks );
			case INTENSITY_IN_RT_WINDOWS:
				return new PeakListAbstractorInRTWindowsByHighestIntensity( peaks );
			case MASS_IN_RT_WINDOWS:
			default:
				return new PeakListAbstractorInRTWindowsByHighestMass( peaks );
		}
	}
}
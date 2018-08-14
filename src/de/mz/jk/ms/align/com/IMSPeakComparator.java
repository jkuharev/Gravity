/** ISOQuant, isoquant.plugins.processing.expression.clustering.io, 11.11.2011*/
package de.mz.jk.ms.align.com;

import java.util.Comparator;


/**
 * <h3>{@link IMSPeakComparator}</h3>
 * Set of IMSPeak comparators by Rt, Intenisty, Mass, Drift, Type   
 * @author kuharev
 * @version 11.11.2011 15:33:49
 */
public class IMSPeakComparator
{
	public static final Comparator<IMSPeak> INTENSITY_ASC = new byIntensityAsc();
	public static final Comparator<IMSPeak> INTENSITY_DESC = new byIntensityDesc();
	public static final Comparator<IMSPeak> RT_ASC = new byRtAsc();
	public static final Comparator<IMSPeak> RT_DESC = new byRtDesc();
	public static final Comparator<IMSPeak> MASS_ASC = new byMassAsc();
	public static final Comparator<IMSPeak> MASS_DESC = new byMassDesc();
	public static final Comparator<IMSPeak> DRIFT_ASC = new byDriftAsc();
	public static final Comparator<IMSPeak> DRIFT_DESC = new byDriftDesc();
	
	public static class byRtAsc implements Comparator<IMSPeak>
	{
		public int compare(IMSPeak a, IMSPeak b)
		{
			return ( a.rt < b.rt ) ? -1 : ( ( a.rt > b.rt ) ? 1 : 0 );
		}
	}

	public static class byRtDesc implements Comparator<IMSPeak>
	{
		public int compare(IMSPeak a, IMSPeak b)
		{
			return ( a.rt < b.rt ) ? 1 : ( ( a.rt > b.rt ) ? -1 : 0 );
		}
	}

	public static class byIntensityAsc implements Comparator<IMSPeak>
	{
		public int compare(IMSPeak a, IMSPeak b)
		{
			return ( a.intensity < b.intensity ) ? -1 : ( ( a.intensity > b.intensity ) ? 1 : 0 );
		}
	}

	public static class byIntensityDesc implements Comparator<IMSPeak>
	{
		public int compare(IMSPeak a, IMSPeak b)
		{
			return ( a.intensity < b.intensity ) ? 1 : ( ( a.intensity > b.intensity ) ? -1 : 0 );
		}
	}

	public static class byMassAsc implements Comparator<IMSPeak>
	{
		public int compare(IMSPeak a, IMSPeak b)
		{
			return ( a.mass < b.mass ) ? -1 : ( ( a.mass > b.mass ) ? 1 : 0 );
		}
	}

	public static class byMassDesc implements Comparator<IMSPeak>
	{
		public int compare(IMSPeak a, IMSPeak b)
		{
			return ( a.mass < b.mass ) ? 1 : ( ( a.mass > b.mass ) ? -1 : 0 );
		}
	}

	public static class byDriftAsc implements Comparator<IMSPeak>
	{
		public int compare(IMSPeak a, IMSPeak b)
		{
			return ( a.drift < b.drift ) ? -1 : ( ( a.drift > b.drift ) ? 1 : 0 );
		}
	}

	public static class byDriftDesc implements Comparator<IMSPeak>
	{
		public int compare(IMSPeak a, IMSPeak b)
		{
			return ( a.drift < b.drift ) ? 1 : ( ( a.drift > b.drift ) ? -1 : 0 );
		}
	}

	public static class byTypeAsc implements Comparator<IMSPeak>
	{
		public int compare(IMSPeak a, IMSPeak b)
		{
			return ( a.type < b.type ) ? -1 : ( ( a.type > b.type ) ? 1 : 0 );
		}
	}

	public static class byTypeDesc implements Comparator<IMSPeak>
	{
		public int compare(IMSPeak a, IMSPeak b)
		{
			return ( a.type < b.type ) ? -1 : ( ( a.type > b.type ) ? 1 : 0 );
		}
	}
}

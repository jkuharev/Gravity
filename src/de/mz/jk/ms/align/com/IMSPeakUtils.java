/** JSiX, de.mz.jk.ms.align.com, 28.02.2014*/
package de.mz.jk.ms.align.com;

import java.util.ArrayList;
import java.util.List;

import de.mz.jk.jsix.plot.pt.XYPlotter;
import de.mz.jk.jsix.plot.pt.XYPlotter.PointStyle;

/**
 * <h3>{@link IMSPeakUtils}</h3>
 * @author kuharev
 * @version 28.02.2014 08:49:24
 */
public class IMSPeakUtils
{
	// public static float Hplus = 1.0078f;
	public static float Hplus = 1.0072764f;

	/** mass = (mz - h+) * charge */
	public static float getPeakMass(float mz, float charge)
	{
		return ( mz - Hplus ) * charge;
	}

	/** mz = mass/charge + h+ */
	public static float getPeakMZ(float mass, float charge)
	{
		return mass / charge + Hplus;
	}

	public static List<Float> getRetentionTimes(List<IMSPeak> peaks)
	{
		List<Float> res = new ArrayList<Float>( peaks.size() );
		for ( IMSPeak p : peaks ) res.add( p.rt );
		return res;
	}
	
	public static List<Float> getIntensities(List<IMSPeak> peaks)
	{
		List<Float> res = new ArrayList<Float>( peaks.size() );
		for ( IMSPeak p : peaks ) res.add( p.intensity );
		return res;
	}
	
	public static List<Float> getMasses(List<IMSPeak> peaks)
	{
		List<Float> res = new ArrayList<Float>( peaks.size() );
		for ( IMSPeak p : peaks ) res.add( p.mass );
		return res;
	}

	public static void plotRetentionTimes(List<IMSPeak> peaks, String label)
	{
		String callerClassName = new Exception().getStackTrace()[1].getClassName();
		XYPlotter pt = new XYPlotter( 800, 600 );
		pt.setPlotTitle( callerClassName + ( label != null ? ": " + label : "" ) );
		pt.setPointStyle( PointStyle.dots );
		pt.setXAxisLabel( "peak order" );
		pt.setYAxisLabel( "retention time" );
		pt.plotY( IMSPeakUtils.getRetentionTimes( peaks ), "", false );
	}

	public static void plotIntensities(List<IMSPeak> peaks, String label)
	{
		String callerClassName = new Exception().getStackTrace()[1].getClassName();
		XYPlotter pt = new XYPlotter( 800, 600 );
		pt.setPlotTitle( callerClassName + ( label != null ? ": " + label : "" ) );
		pt.setPointStyle( PointStyle.dots );
		pt.setXAxisLabel( "peak order" );
		pt.setYAxisLabel( "Intensity" );
		pt.plotY( IMSPeakUtils.getIntensities( peaks ), "", false );
	}
}

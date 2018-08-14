/** JSiX, de.mz.jk.ms.align.com, Jul 22, 2016*/
package de.mz.jk.ms.align.com.abstraction;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import de.mz.jk.jsix.libs.XJava;
import de.mz.jk.jsix.math.KernelDensityEstimator;
import de.mz.jk.jsix.math.interpolation.Interpolator;
import de.mz.jk.jsix.mysql.MySQL;
import de.mz.jk.jsix.plot.pt.XYPlotter;
import de.mz.jk.jsix.plot.pt.XYPlotter.PointStyle;
import de.mz.jk.ms.align.com.IMSPeak;
import de.mz.jk.ms.align.com.IMSPeakComparator;
import de.mz.jk.ms.align.com.IMSPeakUtils;
import de.mz.jk.ms.align.method.dtw.RTW;
import de.mz.jk.ms.align.method.dtw.linear.LinearRTW;

/**
 * <h3>{@link PeakListAbstractorByTimeWindows}</h3>
 * @author jkuharev
 * @version Jul 22, 2016 2:45:30 PM
 */
public class PeakListAbstractorByTimeWindowsTest
{
	public static void main(String[] args) throws Exception
	{
		MySQL db = new MySQL( "localhost", "Proj__13966189271230_9093339492956815_100_1", "root", "", true );
		List<IMSPeak> refPeaks = selectPeaks( db, 3 );
		List<IMSPeak> runPeaks = selectPeaks( db, 4 );

		int nPeaks = 5000;
		
		List<IMSPeak> allPeaks = refPeaks;
		PeakListAbstractor abs = new PeakListAbstractorInRTWindowsByHighestIntensity( allPeaks, 10 );
		List<IMSPeak> subPeaks = abs.getSortedSubPeaks( 5000, IMSPeakComparator.RT_ASC );
		XYPlotter pt = new XYPlotter( 800, 600 );
		pt.setPlotTitle( "Time Distribution" );
		pt.setXAxisLabel( "order" );
		pt.setYAxisLabel( "time" );
		pt.setPointStyle( PointStyle.dots );
		pt.plotY( IMSPeakUtils.getRetentionTimes( subPeaks ), "", false );

//		XYPlotter p = new XYPlotter( 800, 600 );
//		p.setPointStyle( "dots" );
//
//		// bench( runPeaks, AbstractionMethod.INTENSITY, nPeaks, p );
//		// bench( refPeaks, AbstractionMethod.INTENSITY_IN_RT_WINDOWS, nPeaks, p );
//		// bench( runPeaks, AbstractionMethod.MASS, nPeaks, p );
//		// bench( runPeaks, AbstractionMethod.MASS_IN_RT_WINDOWS, nPeaks, p );
//		align( refPeaks, runPeaks, AbstractionMethod.INTENSITY, nPeaks, p );
//		align( refPeaks, runPeaks, AbstractionMethod.INTENSITY_IN_RT_WINDOWS, nPeaks, p );
//		align( refPeaks, runPeaks, AbstractionMethod.MASS, nPeaks, p );
//		align( refPeaks, runPeaks, AbstractionMethod.MASS_IN_RT_WINDOWS, nPeaks, p );

	}

// private static int numberOfRtWindows = 10;

	public static List<IMSPeak> abstractPeaks(List<IMSPeak> peaks, AbstractionMethod method, int size)
	{
		PeakListAbstractor peakAbs = method.toPeaksAbstractor( peaks );
		return peakAbs.getSortedSubPeaks( size, IMSPeakComparator.RT_ASC );
	}

	private static void bench(List<IMSPeak> peaks, AbstractionMethod abstractionMethod, int abstractionSize, XYPlotter p) throws Exception
	{
		List<IMSPeak> abstractedPeaks = abstractPeaks( peaks, abstractionMethod, abstractionSize );
		List<Float> RTs = IMSPeakUtils.getRetentionTimes( abstractedPeaks );
		p.plotY( RTs, abstractionMethod.toString() + " " + abstractionSize, true );
		// KernelDensityEstimator kde = new KernelDensityEstimator( RTs, 0.3 );
		// List<Double> x = XJava.fillDoubleList( kde.getMinX(), kde.getMaxX(),
		// 0.2 );
		// List<Double> y = kde.getDensities( x );
		// p.plotXY( x, y, abstractionMethod.toString() + " " + abstractionSize,
		// true );
	}

	private static void align(List<IMSPeak> refPeaks, List<IMSPeak> runPeaks, AbstractionMethod abstractionMethod, int abstractionSize, XYPlotter p) throws Exception
	{
		List<IMSPeak> abstractedRefPeaks = abstractPeaks( refPeaks, abstractionMethod, abstractionSize );
		List<IMSPeak> abstractedRunPeaks = abstractPeaks( runPeaks, abstractionMethod, abstractionSize );
		RTW rtw = new LinearRTW( abstractedRunPeaks, abstractedRefPeaks );
		rtw.run();
		Interpolator func = rtw.getInterpolator( true );
		System.out.println( abstractionMethod + ": " + func.getOriginalSize() + "/(" + abstractedRefPeaks.size() + ", " + abstractedRunPeaks.size() + ")" );
		// --------- bench
		List<Double> RTs = func.getOriginalX();// new ArrayList<Double>();
//		for ( IMSPeak peak : abstractedRunPeaks )	RTs.add( (double)peak.rt );
		KernelDensityEstimator kde = new KernelDensityEstimator( RTs, 0.3 );
		List<Double> x = XJava.fillDoubleList( kde.getMinX(), kde.getMaxX(), 0.2 );
		List<Double> y = kde.getDensities( x );
		p.plotXY( x, y, abstractionMethod.toString() + " " + abstractionSize, true );
	}

	private static List<IMSPeak> selectPeaks(MySQL db, int run)
	{
		List<IMSPeak> peaks = new ArrayList<IMSPeak>();
		try
		{
			ResultSet rs = db.executeSQL(
					"SELECT `index`, Mass, RT, Mobility, Intensity  FROM mass_spectrum " +
							" WHERE workflow_index=" + run +
							" AND Mass >= 300 AND Mass <= 3000 " +
							" AND Intensity >= 1000" +
							" ORDER BY RT ASC" );
			while (rs.next())
			{
				peaks.add(
						new IMSPeak(
								rs.getInt( "index" ),
								rs.getFloat( "Mass" ),
								rs.getFloat( "RT" ),
								rs.getFloat( "Mobility" ),
								rs.getFloat( "Intensity" ) 
						)
				);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return peaks;
	}

	/**
	 * @param p
	 * @param i
	 * @param peaksByHiInten
	 */
	private static void addPeaksToPlot(XYPlotter p, int plotID, String label, List<IMSPeak> peaks)
	{
		p.setLegend( plotID, label + "(" + peaks.size() + ")" );
		for ( IMSPeak point : peaks )
		{
			p.addPoint( plotID, point.rt, point.mass, false );
		}
	}

	private static XYPlotter plotPeaks(List<IMSPeak> peaks, String plotTitle)
	{
		XYPlotter plot = new XYPlotter( 800, 600 );
		plot.setPointStyle( "dots" );
		plot.setPlotTitle( plotTitle + ", " + peaks.size() + " points" );
		for ( IMSPeak point : peaks )
		{
			plot.addPoint( 1, point.rt, point.mass, false );
		}
		return plot;
	}
}

/** ISOQuant, isoquant.plugins.processing.expression.align.rtw, 13.07.2011 */
package de.mz.jk.ms.align.method.dtw;

import java.util.ArrayList;
import java.util.List;

import de.mz.jk.jsix.lists.XSubList;
import de.mz.jk.jsix.math.interpolation.Interpolator;
import de.mz.jk.jsix.plot.pt.XYPlotter;
import de.mz.jk.ms.align.com.IMSPeak;
import de.mz.jk.ms.align.com.PairwiseAlignment;
import de.mz.jk.ms.align.com.PeakMatcher;

/**
 * <h3>{@link BandedRTW}</h3>
 * Retention Time Warping using column ranges 
 * for reducing memory usage and time complexity 
 * while running dynamic programming
 * @author Joerg Kuharev
 * @version 13.07.2011 17:29:00
 */
public abstract class BandedRTW extends RTW
{
	/**
	 * @param leftPeaks
	 * @param topPeaks
	 */
	public BandedRTW(List<IMSPeak> leftPeaks, List<IMSPeak> topPeaks)
	{
		super( leftPeaks, topPeaks );
	}
	
	/**
	 * @param leftPeaks
	 * @param topPeaks
	 * @param matcher
	 */
	public BandedRTW(XSubList<IMSPeak> leftPeaks, XSubList<IMSPeak> topPeaks, PeakMatcher matcher)
	{
		super( leftPeaks, topPeaks, matcher );
	}

	/** do not forget to call initCallRanges() before using colRanges */
	protected List<int[]> colRanges = null;
	
	protected List<PairwiseAlignment> predefinedPathAlignments = new ArrayList<PairwiseAlignment>();
	protected List<Interpolator> predefinedPathFunctions = new ArrayList<Interpolator>();
	
	private int lastRangeBegin = 0;
	private int lastRangeEnd = 0;
	
	/**
	 * number of peaks for enlargement of predefined path horizontally<br>
	 * RADIUS = 50 
	 * means usage of minimum 100 peaks around a single predefined path definition 
	 */
	protected int RADIUS = 100;
	
	public void setRadius(int radius){RADIUS = radius;}
	public int getRadius(){return RADIUS;}
	
	/** 
	 * calculates and return column ranges beeing relevant for alignment results<br>
	 * @return list of ranges, each range is an int[2] array with range[0] as begin and range[1] as end index
	 */
	public List<int[]> getColRanges()
	{
		if(colRanges==null) initColRanges();
		return  colRanges;
	}	

	// ----------------- CONVERTING PREDEFINED PATHS TO INDEXES -------------------	
	/**
	 * find top peak index ranges for each peak on the left<br>  
	 */
	protected void initColRanges()
	{
//		if(Defaults.DEBUG) System.out.println( "\tmaking column ranges ..." );
		
		colRanges = new ArrayList<int[]>(nRows);
		
		int oldStart = 0;
		int oldEnd = 0;
		
		for(int row=0; row<nRows; row++)
		{
			int[] range = new int[2];

			if(predefinedPathFunctions.size()>0)
			{
				double minRT = Double.MAX_VALUE;
				double maxRT = Double.MIN_VALUE;
				
				for(Interpolator f : predefinedPathFunctions)
				{
					double rt = f.getY( left.get(row).rt );
					if(rt>maxRT) maxRT=rt;
					if(rt<minRT) minRT=rt;
				}
				
				// find range
				range[0] = getRangeBeginIndex( minRT ) - RADIUS;
				range[1] = getRangeEndIndex( maxRT ) + RADIUS;
				
				// ensure overlapping ranges 
				if(oldEnd - range[0] < RADIUS) range[0] = oldEnd - RADIUS;
				
				// ensure growing index
				if(range[0] < oldStart) range[0] = oldStart;
				if(range[1] < oldEnd) range[1] = oldEnd;
				
				// ensure array bounds
				if(range[0]<0) range[0] = 0;
				if(range[1]>=top.size()) range[1] = top.size() - 1;
			}
			else
			{
				range[0] = 0;
				range[1] = top.size() - 1;
			}
			
			oldStart = range[0];
			oldEnd = range[1];
			
			colRanges.add( range );
		}

		// first range must start at 0
		colRanges.get(0)[0] = 0;
		// last range must end at last column
		colRanges.get(colRanges.size()-1)[1] = nCols-1;
	}
	
	/** 
	 * find on top the index of first peak with time > refRT 
	 * @param refRT
	 * @return
	 */
	private int getRangeEndIndex(double refRT) 
	{
		int n = top.size() - 1;
		/*
		// move to the left
		while( lastRangeEnd > 0 && top.get(lastRangeEnd-1).rt > refRT ) lastRangeEnd--;
		*/
		// move to the right
		while( lastRangeEnd < n && top.get(lastRangeEnd).rt <= refRT ) lastRangeEnd++;
		return lastRangeEnd;
	}

	private int getRangeBeginIndex(double refRT) 
	{
		int n = top.size() - 1;
		// to the right
		while( lastRangeBegin < n && top.get(lastRangeBegin).rt < refRT ) lastRangeBegin++;
		// to the left
		while( lastRangeBegin > 0 && top.get(lastRangeBegin).rt >= refRT ) lastRangeBegin--;
		return lastRangeBegin;
	}

	/**
	 * preset an alignment path by corresponding peaks
	 * @param topPeaks
	 * @param leftPeaks
	 */
	public void addCorridorAlignment(List<IMSPeak> topPeaks, List<IMSPeak> leftPeaks)
	{
		try
		{
			predefinedPathFunctions.add( convertAlignment2Interpolator( topPeaks, leftPeaks ) );
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * preset an alignment path by function<br>
	 * function should use top peaks as reference: ref_rt = f(left.rt)
	 * @param f
	 */
	public void addCorridorFunction(Interpolator f)
	{
		predefinedPathFunctions.add( f );
	}
	
	/** 
	 * preset alignment path by multiple functions <br>
	 * function should use top peaks as reference: ref_rt = f(left.rt)
	 * @param funcs
	 */
	public void addCorridorFunctions(Iterable<Interpolator> funcs)
	{
		for ( Interpolator f : funcs )
			addCorridorFunction( f );
	}

	/**
	 * visualization of defined ranges,
	 * for debugging only
	 */
	public void plotColRanges()
	{
		XYPlotter p = new XYPlotter();

		List<Double> x = new ArrayList<Double>(colRanges.size());
		List<Double> from = new ArrayList<Double>(colRanges.size());
		List<Double> to = new ArrayList<Double>(colRanges.size());
		
		for(int i=0; i<colRanges.size(); i++)
		{
			x.add( (double)left.get( i ).rt );
			from.add( (double)top.get( colRanges.get( i )[0] ).rt );
			to.add( (double)top.get( colRanges.get( i )[1] ).rt );
		}
		
		p.setPlotTitle("time ranges to be aligned");
		p.setXAxisLabel("time");
		p.setYAxisLabel("ref_rt");
		
		p.plotXY(x, from, "range begin", true);
		p.plotXY(x, to, "range end", true);
	}
}

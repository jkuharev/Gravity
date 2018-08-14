package de.mz.jk.ms.align.com.abstraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.mz.jk.ms.align.com.IMSPeak;

/** PeakListTimeWarpingTool, , 25.02.2014*/
/**
 * extracts an abstract representation out of a peak list by multiple criteria.
 * The original "parent" peak list remains unchanged.
 * To keep parent list unchanged,
 * a "working" copy of the parent peak list is created during object initialization.
 * 
 * <h3>{@link PeakListAbstractor}</h3>
 * @author kuharev
 * @version 25.02.2014 11:13:33
 */
public abstract class PeakListAbstractor
{
	protected List<IMSPeak> parentPeakList = null;
	protected List<IMSPeak> workingPeakList = null;
	protected int peakListSize = 0;
		
	/**
	 * peak list abstractor for given peak list
	 * @param parentPeakList
	 */
	public PeakListAbstractor(List<IMSPeak> parentPeakList)
	{
		setPeakList( parentPeakList );
	}

	/** 
	 * implementable peak list abstraction
	 * ATTENTION: the order of resulting peaks may vary, reorder them on your needs!
	 */
	protected abstract List<IMSPeak> getSubPeaks(int sublistSize);

	/** @return used parent peak list */
	public List<IMSPeak> getParentPeakList(){return parentPeakList;}

	/**
	 * set the parent peak list, and copy it to the working peak list
	 * @param parentPeakList
	 */
	protected void setPeakList(List<IMSPeak> parentPeakList)
	{
		this.parentPeakList = parentPeakList;
		this.peakListSize = parentPeakList.size();
		this.workingPeakList = new ArrayList<IMSPeak>( this.parentPeakList );
	}

	/** @return the working copy of parent peak list */
	public List<IMSPeak> getWorkingPeakList()
	{
		return workingPeakList;
	}

	/**
	 * get a valid sublist size by part of parent peak list size (N).
	 * case (part < 0): size = 0
	 * case (0 < part < 1): size = part * N  
	 * case (1 < part < N): size = (int)part
	 * case (part > N): size = N
	 * @param part the target sublist size as part of parent peak list size
	 * @return valid sublist size
	 */
	public int getValidSublistSize(float part)
	{
		return ( part <= 0 ) ? 0 : ( part <= 1 ) ? (int)( part * peakListSize ) : ( part < peakListSize ) ? (int)part : peakListSize;
	}

	/**
	 * get a sublist of first n working peaks
	 * @param n the size of sublist (between 0 and size of parent list)
	 * @return sublist of peaks
	 */
	protected List<IMSPeak> getFirstWorkingPeaks(int n)
	{
		return new ArrayList<IMSPeak>( workingPeakList.subList( 0, getValidSublistSize( n ) ) );
	}

	/**
	 * get a sublist of first n working peaks by n = part * size of parent list
	 * @param part
	 * @return sublist of peaks
	 */
	protected List<IMSPeak> getFirstWorkingPeaks(float part)
	{
		return new ArrayList<IMSPeak>( workingPeakList.subList( 0, getValidSublistSize( part ) ) );
	}

	/**
	 * get an abstraction of parent peak list by partial size
	 * @param part resulting sublist size as portion of parent peak list size (0<part<=1) or as the number of elements (1>part<n)
	 * @param subPeaksComparator applied to order the result 
	 * @return abstract representation of parent peak list as a selection of some peaks 
	 */
	public List<IMSPeak> getSortedSubPeaks(float part, Comparator<IMSPeak> subPeaksComparator)
	{
		List<IMSPeak> subPeaks = getSubPeaks( getValidSublistSize( part ) );
		if (subPeaksComparator != null) Collections.sort( subPeaks, subPeaksComparator );
		return subPeaks;
	}
}

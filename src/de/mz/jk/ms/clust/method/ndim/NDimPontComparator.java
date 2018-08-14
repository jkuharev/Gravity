package de.mz.jk.ms.clust.method.ndim;
import java.util.Comparator;

/** DBSCAN, , 26.03.2012*/
/**
 * comparator for ordering n-dimensional points by given dimension 
 * <h3>{@link DotCom}</h3>
 * @author kuharev
 * @version 26.03.2012 14:49:17
 */
public class NDimPontComparator implements Comparator<NDimPoint>
{
	private int compairedDimension = 0;

	/**
	 * number of dimension used for comparing dots
	 * @param compairedDimension
	 */
	public NDimPontComparator(int compairedDimension)
	{
		this.compairedDimension = compairedDimension; 
	}
	
	@Override public int compare(NDimPoint a, NDimPoint b)
	{
		if(a.x[compairedDimension] < b.x[compairedDimension])	return -1;
		if(a.x[compairedDimension] > b.x[compairedDimension]) return 1;
		return 0;
	}
}

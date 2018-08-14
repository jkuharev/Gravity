package de.mz.jk.ms.clust.method.ndim;

import java.util.*;

/** DBSCAN, , 23.03.2012 */
/**
 * <h3>{@link NDimSpace}</h3>
 * @author kuharev
 * @version 23.03.2012 10:07:34
 */
public class NDimSpace
{
	protected int nDims = 1;
	protected int nDots = 0;
	/** all dots in their original order */
	protected Collection<NDimPoint<?>> dots = null;
	/** dots ordered by every single dimension */
	protected NDimPoint<?>[][] dimOrderedDots = null;
	/** here comes a list if clusters each as a lists of points */
	protected List<Set<NDimPoint<?>>> clusters = null;
	/** these points are assumed to be noise */
	protected Set<NDimPoint<?>> noise = null;
	/** default neighborhood radius to use */
	protected float neighborhoodRadius = 1f;
	protected float squaredNeighborhoodRadius = 1f;

	/**
	 * define a n-dimensional space
	 * @param numOfDims
	 */
	public NDimSpace(int numOfDims)
	{
		this.nDims = numOfDims;
	}

	/**
	 * define space and add points
	 * @param numOfDims
	 * @param dots
	 */
	public NDimSpace(int numOfDims, Collection<NDimPoint<?>> dots)
	{
		this.nDims = numOfDims;
		setDots(dots);
	}

	/** clear clusterung results */
	public void resetClusters()
	{
		clusters = new ArrayList<Set<NDimPoint<?>>>();
		noise = new HashSet<NDimPoint<?>>();
	}

	/**
	 * @param neighborhoodRadius the neighborhoodRadius to set
	 */
	public void setNeighborhoodRadius(float neighborhoodRadius)
	{
		this.neighborhoodRadius = neighborhoodRadius;
		this.squaredNeighborhoodRadius = neighborhoodRadius * neighborhoodRadius;
	}

	/**
	 * insert points into this space
	 * @param dots
	 * @return 
	 */
	public void setDots(Collection<NDimPoint<?>> dots)
	{
		this.dots = dots;
		this.nDots = dots.size();
		resetClusters();
		createDimensionOrder();
	}

	/**
	 * @return the dots
	 */
	public Collection<NDimPoint<?>> getDots()
	{
		return dots;
	}

	/**
	 * @return
	 */
	public Set<NDimPoint<?>> getNewCluster()
	{
		Set<NDimPoint<?>> C = new HashSet<NDimPoint<?>>();
		clusters.add(C);
		return C;
	}

	/**
	 * @return the clusters
	 */
	public List<Set<NDimPoint<?>>> getClusters()
	{
		return clusters;
	}

	/**
	 * @return the noise
	 */
	public Set<NDimPoint<?>> getNoise()
	{
		return noise;
	}

	/**
	 * add a point to a cluster
	 * @param point
	 * @param cluster
	 */
	public void addToCluster(NDimPoint point, Collection<NDimPoint<?>> cluster)
	{
		point.noise = false;
		noise.remove(point);
		cluster.add(point);
	}

	/**
	 * mark a point as noise
	 * @param point
	 */
	public void addToNoise(NDimPoint point)
	{
		point.noise = true;
		noise.add(point);
	}

	/**
	 * make lookup index structures for each dimension
	 */
	private void createDimensionOrder()
	{
		// Array of Arrays of dots
		dimOrderedDots = new NDimPoint[nDims][];
		// for each dimension as i
		for (int i = 0; i < nDims; i++)
		{
			// make i-th array of dots
			NDimPoint[] iDots = dots.toArray(new NDimPoint[0]);
			// sort dots by i-th dimension in ascending order
			Arrays.sort(iDots, new NDimPontComparator(i));
			// tell each point the its position in i-th array
			for (int j = 0; j < nDots; j++)
			{
				NDimPoint dot = iDots[j];
				dot.i[i] = j;
			}
			// collect sorted array of dots
			dimOrderedDots[i] = iDots;
		}
	}

	/**
	 * find neighbor dots inside n-dimansional radius around given center dot
	 * @param dot
	 * @param radius
	 * @param useEuklidianDistance
	 * @return
	 */
	public Set<NDimPoint<?>> getNeighbors(NDimPoint dot)
	{
		// find neighbors in first dimension
		Set<NDimPoint<?>> nearDots = findNeighbors(dot, 0);
		// find neighbors present in all other dimensions too
		for (int i = 1; i < nDims; i++)
		{
			// neighbors from i-th dimension
			Set<NDimPoint<?>> nextDots = findNeighbors(dot, i);
			// remove dots not adjacent in i-th dimension
			Set<NDimPoint<?>> farDots = new HashSet<NDimPoint<?>>(nearDots.size());
			for (NDimPoint d : nearDots)
			{
				if (!nextDots.contains(d)) farDots.add(d);
			}
			nearDots.removeAll(farDots);
		}
		// we use Euklidian distance?
		Set<NDimPoint<?>> farDots = new HashSet<NDimPoint<?>>(nearDots.size());
		for (NDimPoint d : nearDots)
		{
			if (squaredDist(dot, d) > squaredNeighborhoodRadius) farDots.add(d);
		}
		nearDots.removeAll(farDots);
		return nearDots;
	}

	/**
	 * squared euklidian distance as SUM( (a.x[i]-b.x[i])^2 ) with i for each dimension 
	 * @param a
	 * @param b
	 * @return
	 */
	public double squaredDist(NDimPoint a, NDimPoint b)
	{
		double dist = 0;
		for (int i = 0; i < nDims; i++)
			dist += Math.pow(a.x[i] - b.x[i], 2);
		return dist;
	}

	private Set<NDimPoint<?>> findNeighbors(NDimPoint theDot, int theDim)
	{
		NDimPoint[] dimDots = dimOrderedDots[theDim];
		float minValue = theDot.x[theDim] - neighborhoodRadius;
		float maxValue = theDot.x[theDim] + neighborhoodRadius;
		int dotIndex = theDot.i[theDim];
		int firstIndex = dotIndex;
		int lastIndex = dotIndex;
		// find outer left neighbor
		for (int i = firstIndex - 1; i > -1 && dimDots[i].x[theDim] > minValue; i--)
		{
			firstIndex = i;
		}
		// find outer right neighbor
		for (int i = lastIndex + 1; i < nDots && dimDots[i].x[theDim] < maxValue; i++)
		{
			lastIndex = i;
		}
		// list of neighbors
		Set<NDimPoint<?>> neighborDots = new HashSet<NDimPoint<?>>(lastIndex - firstIndex);
		// collect neighbor dots
		for (int i = firstIndex; i <= lastIndex; i++)
		{
			// skip center point
			if (i != dotIndex) neighborDots.add(dimDots[i]);
		}
		return neighborDots;
	}
}

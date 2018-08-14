package de.mz.jk.ms.clust.method.dbscan;

import java.util.Collection;
import java.util.Set;

import de.mz.jk.ms.clust.method.ndim.NDimClusteringAlgorithm;
import de.mz.jk.ms.clust.method.ndim.NDimPoint;
import de.mz.jk.ms.clust.method.ndim.NDimSpace;

/**
 * implementation if density based clustering algorithm DBSCAN as described on<br> 
<pre>
usage:

// create a collection of points and fill them
Collection<NDimPoint<?>> dots = new HashSet<NDimPoint<?>>();
...
// create a n-dimensional geometric space 
NDimSpace space = new NDimSpace(2, dots);
// run clustering algorithm
NDimClusteringAlgorithm dbscan = new DBSCAN(space, 1.2f, 2);
dbscan.doClustering();
// retreive clusters and noise
Collection<Set<NDimPoint<?>>> clusters = dbscan.getSpace().getClusters();
Set<NDimPoint<?>> noise = dbscan.getSpace().getNoise();
</pre>
this implementation is based on following pseudocode: (source: <a href="http://en.wikipedia.org/wiki/DBSCAN">http://en.wikipedia.org/wiki/DBSCAN</a>)
<pre>
	DBSCAN(D, eps, MinPts)
	   C = 0
	   for each unvisited point P in dataset D
	      mark P as visited
	      N = regionQuery(P, eps)
	      if sizeof(N) < MinPts
	         mark P as NOISE
	      else
	         C = next cluster
	         expandCluster(P, N, C, eps, MinPts)
	          
	expandCluster(P, N, C, eps, MinPts)
	   add P to cluster C
	   for each point P' in N 
	      if P' is not visited
	         mark P' as visited
	         N' = regionQuery(P', eps)
	         if sizeof(N') >= MinPts
	            N = N joined with N'
	      if P' is not yet member of any cluster
	         add P' to cluster C
</pre>
 *         
 * <h3>{@link DBSCAN}</h3>
 * @author kuharev
 * @version 26.03.2012 15:30:07
 */
public class DBSCAN extends NDimClusteringAlgorithm
{
	protected int minPts = 2;

	/**
	 * @param dots the dataset as list of dots to be clustered
	 * @param epsilon the radius, in which we expect the minimum number of neighbors to find a cluster 
	 * @param minPts the minimum number of neighbors inside of epsilon
	 */
	public DBSCAN(Collection<NDimPoint<?>> dots, int numOfDims, float epsilon, int minPts)
	{
		setSpace(new NDimSpace(numOfDims));
		space.setDots(dots);
		space.setNeighborhoodRadius(epsilon);
		setMinimumNeighborPoints(minPts);
	}

	/**
	 * 
	 * @param space
	 * @param minPoints
	 */
	public DBSCAN(NDimSpace space, int minPoints)
	{
		setSpace(space);
		setMinimumNeighborPoints(minPoints);
	}

	/**
	 * 
	 * @param space
	 * @param epsilon
	 * @param minPoints
	 */
	public DBSCAN(NDimSpace space, float epsilon, int minPoints)
	{
		setSpace(space);
		space.setNeighborhoodRadius(epsilon);
		setMinimumNeighborPoints(minPoints);
	}

	/**
	 * @param minPts the minPts to set
	 */
	public void setMinimumNeighborPoints(int minPts)
	{
		this.minPts = minPts;
	}

	/**
		DBSCAN(D, eps, MinPts)
		   C = 0
		   for each unvisited point P in dataset D
		      mark P as visited
		      N = regionQuery(P, eps)
		      if sizeof(N) < MinPts
		         mark P as NOISE
		      else
		         C = next cluster
		         expandCluster(P, N, C, eps, MinPts)
	*/
	public void doClustering()
	{
		space.resetClusters();
		for (NDimPoint P : space.getDots())
		{
			if (P.unvisited)
			{
				P.unvisited = false;
				Set<NDimPoint<?>> N = space.getNeighbors(P);
				if (N.size() < minPts)
				{
					space.addToNoise(P);
				}
				else
				{
					expandCluster(P, N, space.getNewCluster());
				}
			}
		}
	}

	/**
		expandCluster(P, N, C, eps, MinPts)ein
		   add P to cluster C
		   for each point P' in N 
		      if P' is not visited
		         mark P' as visited
		         N' = regionQuery(P', eps)
		         if sizeof(N') >= MinPts
		            N = N joined with N'
		      if P' is not yet member of any cluster
		         add P' to cluster C 
	*/
	private void expandCluster(NDimPoint point, Collection<NDimPoint<?>> neighbors, Collection<NDimPoint<?>> cluster)
	{
		space.addToCluster(point, cluster);
		for (NDimPoint neighborPoint : neighbors)
		{
			if (neighborPoint.unvisited)
			{
				neighborPoint.unvisited = false;
				Collection<NDimPoint<?>> neighborsNeighbors = space.getNeighbors(neighborPoint);
				if (neighborsNeighbors.size() >= minPts)
				{
					expandCluster(neighborPoint, neighborsNeighbors, cluster);
				}
				else
				{
					space.addToCluster(neighborPoint, cluster);
				}
			}
			// a point is already visited but marked as noise
			else if (neighborPoint.noise)
			{
				space.addToCluster(neighborPoint, cluster);
			}
		}
	}
}

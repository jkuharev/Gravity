/** ISOQuant, isoquant.plugins.processing.expression.clustering.hnh, 23.11.2011 */
package de.mz.jk.ms.clust.method.hierarchical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mz.jk.ms.clust.com.ClusteringPeak;
import de.mz.jk.ms.clust.com.GeneralPeakClusteringProcedure;

/**
 * <h3>{@link HierarchicalClusteringProcedure}</h3>
 * @author kuharev
 * @version 23.11.2011 12:41:53
 */
public class HierarchicalClusteringProcedure extends GeneralPeakClusteringProcedure<HierarchicalClusteringConfig>
{
// private HierarchicalClusteringConfig cfg = null;
// private List<List<ClusteringPeak>> clusteringResult = null;
	private PeakClusterNeighborFinder finder = null;
	private PeakClusterDistanceMetric dm = null;

	/**
	 * prepare clustering for a list of peaks by using given configuration
	 * @param peaks
	 * @param cfg
	 */
	public HierarchicalClusteringProcedure(List<ClusteringPeak> peaks, HierarchicalClusteringConfig cfg)
	{
		super(cfg);
		this.dm = cfg.getDistanceMetric();
		this.dm.setPeaks(peaks);
		this.finder = cfg.getNeighborFinder();
	}

	@Override protected void runClusteringAlgorithm()
	{
		PeakClusterGroup startGroup = PeakClusterGroup.createSinglePeakPerClusterGroup(dm.getPeaks());
		startGroup.distances = dm.getPeakDistanceMatrix();
		PeakClusterGroup group = startGroup;
		int nClusters = group.clusters.size();
		float minDistance = group.distances.getMinDistance();
		float distanceThreshold = cfg.getClusterDistanceThreshold();
// for(int stepCount=0; nClusters > 1 && minDistance <= distanceTrashold;
// stepCount++ )
		while (nClusters > 1 && minDistance <= distanceThreshold)
		{
			// what nodes should be pooled
			List<Integer> nnIndexes = finder.getNodesForJoin(group);
			// avoid endless loops
			if (nnIndexes.size() < 2) break;
			// reorganize clisters
			group = PeakClusterGroup.joinNodes(group, nnIndexes);
			// build distances for new clusters
			group.distances = dm.buildDistanceMatrix(group);
			nClusters = group.clusters.size();
			minDistance = group.distances.getMinDistance();
			distanceThreshold = cfg.getClusterDistanceThreshold();
		}
		// store clustering results
		clusteringResult = new ArrayList<Collection<ClusteringPeak>>();
		for (PeakCluster c : group.clusters)
		{
			clusteringResult.add(c.peaks);
		}
	}
// /**
// * retrieve results of this clustering run,
// * this function will wait until this clistering thread will have finished
// * @return list of lists of peaks
// */
// public Collection<Collection<ClusteringPeak>> getClusteringResult()
// {
// lock.acquireUninterruptibly();
// lock.release();
// return clusteringResult;
// }
//
// /**
// * @return the cfg
// */
// public HierarchicalClusteringConfig getCfg(){ return cfg; }
}

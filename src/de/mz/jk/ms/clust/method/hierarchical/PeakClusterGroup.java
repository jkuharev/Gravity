/** ISOQuant, isoquant.plugins.processing.expression.clustering.hnh, 23.11.2011 */
package de.mz.jk.ms.clust.method.hierarchical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.mz.jk.ms.clust.com.ClusteringPeak;

/**
 * <h3>{@link PeakClusterGroup}</h3>
 * @author kuharev
 * @version 23.11.2011 10:21:02
 */
public class PeakClusterGroup
{
	public List<PeakCluster> clusters = new ArrayList<PeakCluster>();
	public DistanceMatrix distances = null;

	/**
	 * create new cluster group with given nodes joined
	 * @param oldGroup
	 * @param clusterIndexesToJoin
	 * @return
	 */
	public static PeakClusterGroup joinNodes(PeakClusterGroup oldGroup, List<Integer> clusterIndexesToJoin)
	{
		PeakClusterGroup newGroup = new PeakClusterGroup();
		PeakCluster newCluster = new PeakCluster();
		// run through the old group of clusters
		for (int i = 0; i < oldGroup.clusters.size(); i++)
		{
			// is current cluster to be joined
			if (clusterIndexesToJoin.contains(i))
			{
				newCluster.peaks.addAll(oldGroup.clusters.get(i).peaks);
			}
			else
			{
				newGroup.clusters.add(oldGroup.clusters.get(i));
			}
		}
		// append new group at the end of list
		newGroup.clusters.add(newCluster);
		return newGroup;
	}

	/**
	 * create new cluster group 
	 * having each given peak packaged into a single cluster
	 * @param peaks
	 * @return
	 */
	public static PeakClusterGroup createSinglePeakPerClusterGroup(List<ClusteringPeak> peaks)
	{
		PeakClusterGroup grp = new PeakClusterGroup();
		for (int i = 0; i < peaks.size(); i++)
		{
			ClusteringPeak p = peaks.get(i);
			PeakCluster c = new PeakCluster();
			c.peaks = Collections.singletonList(p);
			c.id = i;
			c.clusterPeak = p;
			c.initialized = true;
			grp.clusters.add(c);
		}
		return grp;
	}
}

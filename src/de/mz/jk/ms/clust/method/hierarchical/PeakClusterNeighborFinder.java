/** ISOQuant, isoquant.plugins.processing.expression.clustering.hnh, 23.11.2011 */
package de.mz.jk.ms.clust.method.hierarchical;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.mz.jk.ms.clust.com.ClusteringPeak;

/**
 * <h3>{@link PeakClusterNeighborFinder}</h3>
 * @author kuharev
 * @version 23.11.2011 15:23:16
 */
public abstract class PeakClusterNeighborFinder
{
	protected HierarchicalClusteringConfig cfg = null;

	public HierarchicalClusteringConfig getConfig()
	{
		return cfg;
	}

	public void setConfig(HierarchicalClusteringConfig cfg)
	{
		this.cfg = cfg;
	}

	public abstract List<Integer> getNodesForJoin(PeakClusterGroup clusterGroup);

	public static enum Description
	{
		NEAREST_NEIGHBOR,
		HIGHEST_INTENSITY_FIRST;
		public static Description fromString(String desc)
		{
			if (desc.toLowerCase().startsWith("h")) return HIGHEST_INTENSITY_FIRST;
			return NEAREST_NEIGHBOR;
		}

		public PeakClusterNeighborFinder newInstance()
		{
			switch (this)
			{
				case HIGHEST_INTENSITY_FIRST:
					return new HIGHEST_INTENSITY_FIRST();
				default:
					return new NEAREST_NEIGHBOR();
			}
		}
	}

	public static class NEAREST_NEIGHBOR extends PeakClusterNeighborFinder
	{
		@Override public List<Integer> getNodesForJoin(PeakClusterGroup clusterGroup)
		{
			float minDst = clusterGroup.distances.getMinDistance();
			int n = clusterGroup.clusters.size();
			for (int row = 1; row < n; row++)
			{
				for (int col = 0; col < row; col++)
				{
					if (clusterGroup.distances.getDistance(row, col) == minDst) { return Arrays.asList(new Integer[] { row, col }); }
				}
			}
			return Collections.emptyList();
		}
	}

	public static class HIGHEST_INTENSITY_FIRST extends PeakClusterNeighborFinder
	{
		@Override public List<Integer> getNodesForJoin(PeakClusterGroup clusterGroup)
		{
			DistanceMatrix dm = clusterGroup.distances;
			List<PeakCluster> cs = clusterGroup.clusters;
			int n = cs.size();
			// array of intensities
			float[] is = new float[n];
			// array of sorted intensities
			float[] iss = new float[n];
			// blacklisted clusters
			boolean[] bl = new boolean[n];
			for (int i = 0; i < n; i++)
			{
				// find the highest intensity for each cluster
				is[i] = getHighestIntensity(cs.get(i).peaks);
				iss[i] = is[i];
				// assume no clusters to be blacklisted
				bl[i] = false; // nothing is blacklisted
			}
			// intensities in ascending order
			Arrays.sort(iss);
			// walk through intensities from highest to lowest
			for (int i = n - 1; i >= 0; i--)
			{
				// the highest intensity is
				float hiInten = iss[i];
				int hiIndex = indexOf(is, hiInten);
				// skip blacklisted
				if (bl[hiIndex]) continue;
				float minDist = Float.MAX_VALUE;
				int nnIndex = 0;
				for (int j = 0; j < n; j++)
				{
					// skip on blacklisted node or on the hi intensity node
// itself
					if (bl[j] || j == hiIndex) continue;
					float dist = dm.getDistance(hiIndex, j);
					if (minDist > dist)
					{
						minDist = dist;
						nnIndex = j;
					}
				}
				if (minDist <= cfg.getClusterDistanceThreshold()) { return Arrays.asList(new Integer[] { hiIndex, nnIndex }); }
				// blacklist this hiIndex cluster
				bl[hiIndex] = true;
			}
			return Collections.emptyList();
		}

		/**
		 * @param array
		 * @param value
		 * @return
		 */
		private int indexOf(float[] array, float value)
		{
			for (int i = 0; i < array.length; i++)
			{
				if (array[i] == value) return i;
			}
			return 0;
		}

		/**
		 * @param peaks 
		 * @return the highest intensity in a cluster
		 */
		private float getHighestIntensity(List<ClusteringPeak> peaks)
		{
			float res = 0f;
			for (ClusteringPeak p : peaks)
			{
				if (res < p.inten) res = p.inten;
			}
			return res;
		}
	}
}

/**
 * ISOQuant, isoquant.plugins.processing.expression.clustering.dbscan,
 * 12.04.2012
 */
package de.mz.jk.ms.clust.method.dbscan;

import java.util.*;

import de.mz.jk.ms.clust.com.ClusteringPeak;
import de.mz.jk.ms.clust.com.GeneralPeakClusteringProcedure;
import de.mz.jk.ms.clust.method.ndim.NDimPoint;
import de.mz.jk.ms.clust.method.ndim.NDimSpace;

/**
 * <h3>{@link DBSCANClusteringProcedure}</h3>
 * @author kuharev
 * @version 12.04.2012 14:41:24
 */
public class DBSCANClusteringProcedure extends GeneralPeakClusteringProcedure<DBSCANClusteringConfiguration>
{
	private Collection<ClusteringPeak> peaks = null;
	private Collection<NDimPoint<?>> dots = null;

	public DBSCANClusteringProcedure(Collection<ClusteringPeak> peaks, DBSCANClusteringConfiguration cfg)
	{
		super(cfg);
		cfg.setClusteringProcedure(this);
		setPeaks(peaks);
	}

	/**
	 *  
	 * @param peaks
	 */
	private void setPeaks(Collection<ClusteringPeak> peaks)
	{
		this.peaks = peaks;
		this.dots = cfg.transfromToNDimPoints(peaks);
	}

	@Override protected void runClusteringAlgorithm()
	{
		NDimSpace space = new NDimSpace(3);
		space.setDots(dots);
		DBSCAN dbscan = new DBSCAN(space, cfg.getClusterDistanceThreshold(), cfg.getMinNeighborCount());
		dbscan.run();
		List<Set<NDimPoint<?>>> clusters = space.getClusters();
		Set<NDimPoint<?>> noise = space.getNoise();
		clusteringResult = new ArrayList<Collection<ClusteringPeak>>(clusters.size() + noise.size());
		for (Set<NDimPoint<?>> points : clusters)
		{
			List<ClusteringPeak> clusterPeaks = new ArrayList<ClusteringPeak>(points.size());
			for (NDimPoint<?> point : points)
			{
				clusterPeaks.add((ClusteringPeak) point.getPayload());
			}
			clusteringResult.add(clusterPeaks);
		}
		for (NDimPoint<?> point : noise)
		{
			clusteringResult.add(Collections.singleton((ClusteringPeak) point.getPayload()));
		}
	}
}

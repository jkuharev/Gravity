/** ISOQuant, isoquant.plugins.processing.expression.clustering.hnh, 07.12.2011 */
package de.mz.jk.ms.clust.method.hierarchical;

import java.util.Collection;
import java.util.List;

import de.mz.jk.ms.clust.com.ClusteringPeak;
import de.mz.jk.ms.clust.com.GeneralPeakClusteringThread;
import de.mz.jk.ms.clust.com.PipeLine;

/**
 * <h3>{@link HierarchicalClusteringProcedureThread}</h3>
 * @author kuharev
 * @version 07.12.2011 10:35:52
 */
public class HierarchicalClusteringProcedureThread extends GeneralPeakClusteringThread<HierarchicalClusteringConfig>
{
	/**
	 * @param in
	 * @param out
	 * @param cfg
	 */
	public HierarchicalClusteringProcedureThread(PipeLine<Collection<ClusteringPeak>> in, PipeLine<Collection<ClusteringPeak>> out, HierarchicalClusteringConfig cfg)
	{
		super(in, out, cfg);
	}

	@Override protected Collection<Collection<ClusteringPeak>> runClustering(Collection<ClusteringPeak> peaks, HierarchicalClusteringConfig cfg)
	{
		HierarchicalClusteringProcedure pcp = new HierarchicalClusteringProcedure((List<ClusteringPeak>) peaks, cfg);
		pcp.run();
		return pcp.getClusteringResult();
	}
}

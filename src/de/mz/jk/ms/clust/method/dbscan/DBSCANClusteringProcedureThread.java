/** ISOQuant, isoquant.plugins.processing.expression.clustering.dbscan, 13.04.2012*/
package de.mz.jk.ms.clust.method.dbscan;

import java.util.Collection;

import de.mz.jk.ms.clust.com.ClusteringPeak;
import de.mz.jk.ms.clust.com.GeneralPeakClusteringThread;
import de.mz.jk.ms.clust.com.PipeLine;

/**
 * <h3>{@link DBSCANClusteringProcedureThread}</h3>
 * @author kuharev
 * @version 13.04.2012 13:50:57
 */
public class DBSCANClusteringProcedureThread extends GeneralPeakClusteringThread<DBSCANClusteringConfiguration>
{
	/**
	 * @param in
	 * @param out
	 * @param cfg
	 */
	public DBSCANClusteringProcedureThread(PipeLine<Collection<ClusteringPeak>> in, PipeLine<Collection<ClusteringPeak>> out, DBSCANClusteringConfiguration cfg)
	{
		super(in, out, cfg);
	}

	@Override protected Collection<Collection<ClusteringPeak>> runClustering(Collection<ClusteringPeak> peaks, DBSCANClusteringConfiguration cfg)
	{
		DBSCANClusteringProcedure cp = new DBSCANClusteringProcedure(peaks, cfg);
		cp.run();
		return cp.getClusteringResult();
	}	
}

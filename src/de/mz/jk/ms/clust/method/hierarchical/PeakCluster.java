/** ISOQuant, isoquant.plugins.processing.expression.clustering.hnh, 22.11.2011 */
package de.mz.jk.ms.clust.method.hierarchical;

import java.util.ArrayList;
import java.util.List;

import de.mz.jk.ms.clust.com.ClusteringPeak;

/**
 * <h3>{@link PeakCluster}</h3>
 * @author kuharev
 * @version 22.11.2011 16:49:52
 */
public class PeakCluster
{
	public List<ClusteringPeak> peaks = new ArrayList<ClusteringPeak>();
	public int id = 0;
	public ClusteringPeak clusterPeak = null;
	public boolean initialized = false;
	public boolean hidden = false;
}

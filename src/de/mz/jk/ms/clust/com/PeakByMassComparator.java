/** ISOQuant, isoquant.plugins.processing.expression.clustering.io, 12.04.2012*/
package de.mz.jk.ms.clust.com;

import java.util.Comparator;

/**
 * peak comparator for ordering by ascending mass 
 * <h3>{@link PeakByMassComparator}</h3>
 * @author kuharev
 * @version 12.04.2012 15:08:35
 */
public class PeakByMassComparator implements Comparator<ClusteringPeak>
{
	@Override public int compare(ClusteringPeak a, ClusteringPeak b)
	{
		if(a.mass < b.mass)	return -1;
		if(a.mass > b.mass) return 1;
		return 0;
	}
}

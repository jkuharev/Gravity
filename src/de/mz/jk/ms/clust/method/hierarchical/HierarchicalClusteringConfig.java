/** ISOQuant, isoquant.plugins.processing.expression.clustering.hnh, 23.11.2011 */
package de.mz.jk.ms.clust.method.hierarchical;

import de.mz.jk.ms.clust.com.GeneralPeakClusteringConfiguration;

/**
 * <h3>{@link HierarchicalClusteringConfig}</h3>
 * @author kuharev
 * @version 23.11.2011 13:10:10
 */
public class HierarchicalClusteringConfig extends GeneralPeakClusteringConfiguration
{
	/** exchangeable distance measurement between clusters */
	private PeakClusterDistanceMetric distanceMetric = null;
	/** exchangable neighbor finder */
	private PeakClusterNeighborFinder neighborFinder = null;

	/** do not forget to manually set parameters */
	public HierarchicalClusteringConfig()
	{}

	/**
	 * create configuration using given parameters
	 * @param _massRes
	 * @param _timeRes
	 * @param _driftRes
	 * @param _distanceMetric
	 * @param _neighborFinder
	 */
	public HierarchicalClusteringConfig(
		float _massRes, float _timeRes, float _driftRes, float _distanceThreshold,
		PeakClusterDistanceMetric _distanceMetric, PeakClusterNeighborFinder _neighborFinder)
	{
		super(_massRes, _timeRes, _driftRes, _distanceThreshold);
		setDistanceMetric(_distanceMetric);
		setNeighborFinder(_neighborFinder);
	}

	/**
	 * assign given distance metric to this configuration
	 * and assign this configuration to that distance metric 
	 * @param distanceMetric
	 */
	public void setDistanceMetric(PeakClusterDistanceMetric distanceMetric)
	{
		this.distanceMetric = distanceMetric;
		distanceMetric.setConfig(this);
	}

	public PeakClusterDistanceMetric getDistanceMetric()
	{
		return distanceMetric;
	}

	/**
	 * assign a neighbor finder to this configuration
	 * and assign this configuration to that finder 
	 * @param neighborFinder
	 */
	public void setNeighborFinder(PeakClusterNeighborFinder neighborFinder)
	{
		neighborFinder.setConfig(this);
		this.neighborFinder = neighborFinder;
	}

	public PeakClusterNeighborFinder getNeighborFinder()
	{
		return neighborFinder;
	}
}

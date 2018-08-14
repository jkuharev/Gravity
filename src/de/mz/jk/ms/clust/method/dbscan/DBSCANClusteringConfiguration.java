/**
 * ISOQuant, isoquant.plugins.processing.expression.clustering.dbscan,
 * 12.04.2012
 */
package de.mz.jk.ms.clust.method.dbscan;

import java.util.ArrayList;
import java.util.Collection;

import de.mz.jk.ms.clust.com.ClusteringPeak;
import de.mz.jk.ms.clust.com.GeneralPeakClusteringConfiguration;
import de.mz.jk.ms.clust.method.ndim.NDimPoint;

/**
 * <h3>{@link DBSCANClusteringConfiguration}</h3>
 * @author kuharev
 * @version 12.04.2012 14:57:01
 */
public class DBSCANClusteringConfiguration extends GeneralPeakClusteringConfiguration
{
	private int minNeighborCount = 2;
	private boolean useAbsTransform = true;

	public int getMinNeighborCount()
	{
		return minNeighborCount;
	}

	public void setMinNeighborCount(int minNeighborCount)
	{
		this.minNeighborCount = minNeighborCount;
	}

	/** please manually define parameters  */
	public DBSCANClusteringConfiguration()
	{}

	public void setAbsolutePeakTransformationEnabled(boolean useAbsolutePeakTransformation)
	{
		this.useAbsTransform = useAbsolutePeakTransformation;
	}

	/**  */
	public DBSCANClusteringConfiguration(float _massRes, float _timeRes, float _driftRes, float _distanceThreshold, int _minNeighborCount)
	{
		super(_massRes, _timeRes, _driftRes, _distanceThreshold);
		setMinNeighborCount(_minNeighborCount);
	}

	/**
	 * transform peaks (rt/drift/mass) into homogeneous space
	 * generated points carrying their origin peak as payload 
	 * @param peaks
	 */
	public Collection<NDimPoint<?>> transfromToNDimPoints(Collection<ClusteringPeak> peaks)
	{
		return useAbsTransform ? transfromToNDimPointsAbsolute(peaks) : transfromToNDimPointsRelative(peaks);
	}

	/**
	 * transform peaks (rt/drift/mass) into homogeneous space
	 * generated points carrying their origin peak as payload 
	 * @param peaks
	 */
	private Collection<NDimPoint<?>> transfromToNDimPointsAbsolute(Collection<ClusteringPeak> peaks)
	{
		double logMassRes = Math.log(1 + massResolution);
		Collection<NDimPoint<?>> dots = new ArrayList<NDimPoint<?>>(peaks.size());
		for (ClusteringPeak peak : peaks)
		{
			float ppm = (float) (Math.log(peak.mass) / logMassRes);
			float time = peak.time / timeResolution;
			float drift = peak.drift / driftResolution;
			dots.add(new NDimPoint<ClusteringPeak>(peak.id, new float[] { time, ppm, drift }, peak));
		}
		return dots;
	}

	/**
	 * transform peaks (rt/drift/mass) into homogeneous space
	 * generated points carrying their origin peak as payload 
	 * @param peaks
	 * 
	 * @WARNING this version cumulates ppm calculation error for every following peak
	 */
	private Collection<NDimPoint<?>> transfromToNDimPointsRelative(Collection<ClusteringPeak> peaks)
	{
		double logMassRes = Math.log(1 + massResolution);
		Collection<NDimPoint<?>> dots = new ArrayList<NDimPoint<?>>(peaks.size());
		ClusteringPeak prevPeak = null;
		float ppm = 0f;
		int i = 0;
		for (ClusteringPeak peak : peaks)
		{
			// first peak's ppm is 0
			ppm += (i++ < 1) ? 0f : (float) ((Math.log(peak.mass) - Math.log(prevPeak.mass)) / logMassRes);
			float time = peak.time / timeResolution;
			float drift = peak.drift / driftResolution;
			dots.add(new NDimPoint<ClusteringPeak>(peak.id, new float[] { time, ppm, drift }, peak));
			prevPeak = peak;
		}
		return dots;
	}
}

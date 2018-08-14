package de.mz.jk.ms.clust.com;

/**
 * general peak clustering configuration
 * <h3>{@link GeneralPeakClusteringConfiguration}</h3>
 * @author kuharev
 * @version 23.11.2011 13:10:10
 */
public class GeneralPeakClusteringConfiguration
{
	/** 1.0 */
	public static final float DEFAULT_MAX_ALLOWED_MIN_CLUSTER_DISTANCE = 1f;
	/** 6/10^6 parts = 6 ppm */
	public static final float DEFAULT_MASS_RESOLUTION = 6f / 1000000f;
	/** 0.2 min */
	public static final float DEFAULT_TIME_RESOLUTION = 0.2f;
	/** 2.0 bins */
	public static final float DEFAULT_DRIFT_RESOLUTION = 2f;// 0.025f;
	/** target distance between clusters */
	private float clusterDistanceThreshold = DEFAULT_MAX_ALLOWED_MIN_CLUSTER_DISTANCE;

	public void setClusterDistanceThreshold(float maxMinDist)
	{
		clusterDistanceThreshold = maxMinDist * maxMinDist;
	}

	public float getClusterDistanceThreshold()
	{
		return clusterDistanceThreshold;
	}
	/** mass unit size */
	protected float massResolution = DEFAULT_MASS_RESOLUTION;

	public void setMassResolution(float massRes)
	{
		massResolution = massRes;
	}

	public float getMassResolution()
	{
		return massResolution;
	}
	/** time unit size */
	protected float timeResolution = DEFAULT_TIME_RESOLUTION;

	public void setTimeResolution(float timeRes)
	{
		timeResolution = timeRes;
	}

	public float getTimeResolution()
	{
		return timeResolution;
	}
	/** drift unit size */
	protected float driftResolution = DEFAULT_DRIFT_RESOLUTION;

	public void setDriftResolution(float driftRes)
	{
		driftResolution = driftRes;
	}

	public float getDriftResolution()
	{
		return driftResolution;
	}
	/** host procedure */
	private GeneralPeakClusteringProcedure clusteringProcedure = null;

	/** do not forget to manually set parameters */
	public GeneralPeakClusteringConfiguration()
	{}

	/**  */
	public GeneralPeakClusteringConfiguration(float _massRes, float _timeRes, float _driftRes, float _distanceThreshold)
	{
		setMassResolution(_massRes);
		setTimeResolution(_timeRes);
		setDriftResolution(_driftRes);
		setClusterDistanceThreshold(_distanceThreshold);
	}

	public void setClusteringProcedure(GeneralPeakClusteringProcedure peakClusteringProcedure)
	{
		this.clusteringProcedure = peakClusteringProcedure;
	}

	public GeneralPeakClusteringProcedure getClusteringProcedure()
	{
		return clusteringProcedure;
	}
}

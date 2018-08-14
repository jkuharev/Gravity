/** ISOQuant, isoquant.plugins.processing.expression.clustering.hnh, 23.11.2011 */
package de.mz.jk.ms.clust.method.hierarchical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.mz.jk.jsix.math.XStat;
import de.mz.jk.ms.clust.com.ClusteringPeak;

/**
 * <h3>{@link PeakClusterDistanceMetric}</h3>
 * @author kuharev
 * @version 23.11.2011 13:34:34
 */
public abstract class PeakClusterDistanceMetric
{
	public static enum Description
	{
		MEDIAN_LINKAGE,
		AVERAGE_LINKAGE,
		SINGLE_LINKAGE,
		COMPLETE_LINKAGE,
		CENTROID,
		MEDOID,
		INTENSITY_WEIGHTED_CENTROID;
		/**
		 * clustering method decoded from string,
		 * unrecognized values are decoded to SINGLE_LINKAGE  
		 * @param value string value describing one of possible values
		 * @return enum value
		 */
		public static Description fromString(String value)
		{
			String v = value.toLowerCase();
			if (v.startsWith("median")) return MEDIAN_LINKAGE;
			if (v.startsWith("average")) return AVERAGE_LINKAGE;
			if (v.startsWith("complete")) return COMPLETE_LINKAGE;
			if (v.startsWith("medoid")) return MEDOID;
			if (v.startsWith("centroid")) return CENTROID;
			if (v.startsWith("inten")) return INTENSITY_WEIGHTED_CENTROID;
			return SINGLE_LINKAGE;
		}

		/**
		 * get distance metric by its description
		 * @param desc
		 * @return
		 */
		public PeakClusterDistanceMetric newInstance()
		{
			switch (this)
			{
				case MEDIAN_LINKAGE:
					return new MEDIAN_LINKAGE();
				case AVERAGE_LINKAGE:
					return new AVERAGE_LINKAGE();
				case SINGLE_LINKAGE:
					return new SINGLE_LINKAGE();
				case COMPLETE_LINKAGE:
					return new COMPLETE_LINKAGE();
				case MEDOID:
					return new MEDOID();
				case INTENSITY_WEIGHTED_CENTROID:
					return new INTENSITY_WEIGHTED_CENTROID();
				case CENTROID:
				default:
					return new CENTROID();
			}
		}
	}

	/**
	 * distance between clusters is determined by
	 * smallest distance between elements of those clusters   
	 * <h3>{@link SINGLE_LINKAGE}</h3>
	 * @author kuharev
	 * @version 25.11.2011 12:58:54
	 */
	public static class SINGLE_LINKAGE extends PeakClusterDistanceMetric
	{
		@Override public float measureDistance(PeakClusterGroup group, int srcNode, int dstNode)
		{
			PeakCluster A = group.clusters.get(srcNode);
			PeakCluster B = group.clusters.get(dstNode);
			float minDist = Float.MAX_VALUE;
			for (ClusteringPeak a : A.peaks)
				for (ClusteringPeak b : B.peaks)
				{
					// float dist = getDistance(a, b);
					float dist = lookupDistance(a, b);
					if (minDist > dist) minDist = dist;
				}
			return minDist;
		}
	};

	/**
	 * distance between clusters is determined by
	 * farest distance between elements of those clusters 
	 * <h3>{@link COMPLETE_LINKAGE}</h3>
	 * @author kuharev
	 * @version 25.11.2011 12:59:11
	 */
	public static class COMPLETE_LINKAGE extends PeakClusterDistanceMetric
	{
		@Override public float measureDistance(PeakClusterGroup group, int srcNode, int dstNode)
		{
			PeakCluster A = group.clusters.get(srcNode);
			PeakCluster B = group.clusters.get(dstNode);
			float maxDist = Float.MIN_VALUE;
			for (ClusteringPeak a : A.peaks)
				for (ClusteringPeak b : B.peaks)
				{
					float dist = lookupDistance(a, b);
					if (maxDist < dist) maxDist = dist;
				}
			return maxDist;
		}
	};

	/**
	 * distance between clusters is determined by
	 * average distance between elements of those clusters 
	 * <h3>{@link AVERAGE_LINKAGE}</h3>
	 * @author kuharev
	 * @version 25.11.2011 12:59:11
	 */
	public static class AVERAGE_LINKAGE extends PeakClusterDistanceMetric
	{
		@Override public float measureDistance(PeakClusterGroup group, int srcNode, int dstNode)
		{
			PeakCluster A = group.clusters.get(srcNode);
			PeakCluster B = group.clusters.get(dstNode);
			float sumDist = 0f;
			for (ClusteringPeak a : A.peaks)
				for (ClusteringPeak b : B.peaks)
				{
					sumDist += lookupDistance(a, b);
				}
			return sumDist / A.peaks.size() / B.peaks.size();
		}
	};

	/**
	 * distance between clusters is determined by
	 * median distance between elements of those clusters 
	 * <h3>{@link COMPLETE_LINKAGE}</h3>
	 * @author kuharev
	 * @version 25.11.2011 12:59:11
	 */
	public static class MEDIAN_LINKAGE extends PeakClusterDistanceMetric
	{
		@Override public float measureDistance(PeakClusterGroup group, int srcNode, int dstNode)
		{
			PeakCluster A = group.clusters.get(srcNode);
			PeakCluster B = group.clusters.get(dstNode);
			int n = A.peaks.size() * B.peaks.size();
			List<Float> dists = new ArrayList<Float>(n);
			for (ClusteringPeak a : A.peaks)
			{
				for (ClusteringPeak b : B.peaks)
				{
					dists.add(lookupDistance(a, b));
				}
			}
			return (float) XStat.median(dists);
		}
	};

	/**
	 * distance between clusters is determined by
	 * the distance between average elements of those clusters 
	 * <h3>{@link CENTROID}</h3>
	 * @author kuharev
	 * @version 25.11.2011 12:59:11
	 */
	public static class CENTROID extends PeakClusterDistanceMetric
	{
		@Override public float measureDistance(PeakClusterGroup group, int srcNode, int dstNode)
		{
			PeakCluster A = group.clusters.get(srcNode);
			PeakCluster B = group.clusters.get(dstNode);
			if (!A.initialized) findAverageCentroid(A);
			if (!B.initialized) findAverageCentroid(B);
			return getDistance(A.clusterPeak, B.clusterPeak);
		}

		private void findAverageCentroid(PeakCluster c)
		{
			int n = c.peaks.size();
			ClusteringPeak p = new ClusteringPeak();
			for (ClusteringPeak a : c.peaks)
			{
				p.mass += a.mass;
				p.time += a.time;
				p.drift += a.drift;
			}
			p.mass = p.mass / n;
			p.time = p.time / n;
			p.drift = p.drift / n;
			c.clusterPeak = p;
			c.initialized = true;
		}
	};

	/**
	 * distance between clusters is determined by
	 * the distance between average elements of those clusters 
	 * <h3>{@link CENTROID}</h3>
	 * @author kuharev
	 * @version 25.11.2011 12:59:11
	 */
	public static class INTENSITY_WEIGHTED_CENTROID extends PeakClusterDistanceMetric
	{
		@Override public float measureDistance(PeakClusterGroup group, int srcNode, int dstNode)
		{
			PeakCluster A = group.clusters.get(srcNode);
			PeakCluster B = group.clusters.get(dstNode);
			if (!A.initialized) findAverageCentroid(A);
			if (!B.initialized) findAverageCentroid(B);
			return getDistance(A.clusterPeak, B.clusterPeak);
		}

		private void findAverageCentroid(PeakCluster c)
		{
			float sMass = 0f;
			float sTime = 0f;
			float sInten = 0f;
			float sDrift = 0f;
			for (ClusteringPeak a : c.peaks)
			{
				sInten += a.inten;
				sMass += a.mass * a.inten;
				sTime += a.time * a.inten;
				sDrift += a.drift * a.inten;
			}
			ClusteringPeak p = new ClusteringPeak();
			p.mass = sMass / sInten;
			p.time = sTime / sInten;
			p.drift = sDrift / sInten;
			c.clusterPeak = p;
			c.initialized = true;
		}
	};

	/**
	 * distance between clusters is determined by
	 * the distance between average elements of those clusters 
	 * <h3>{@link MEDOID}</h3>
	 * @author kuharev
	 * @version 25.11.2011 12:59:11
	 */
	public static class MEDOID extends PeakClusterDistanceMetric
	{
		@Override public float measureDistance(PeakClusterGroup group, int srcNode, int dstNode)
		{
			PeakCluster A = group.clusters.get(srcNode);
			PeakCluster B = group.clusters.get(dstNode);
			if (!A.initialized) findMedianCentroid(A);
			if (!B.initialized) findMedianCentroid(B);
			return getDistance(A.clusterPeak, B.clusterPeak);
		}

		private void findMedianCentroid(PeakCluster c)
		{
			int n = c.peaks.size();
			float[] masses = new float[n];
			float[] times = new float[n];
			float[] drifts = new float[n];
			for (int i = 0; i < n; i++)
			{
				ClusteringPeak p = c.peaks.get(i);
				masses[i] = p.mass;
				times[i] = p.time;
				drifts[i] = p.drift;
			}
			Arrays.sort(masses);
			Arrays.sort(times);
			Arrays.sort(drifts);
			int size = n + 1;
			int lmid = (size / 2) - 1;
			int rmid = (size + 1) / 2 - 1;
			ClusteringPeak p = new ClusteringPeak();
			p.mass = (masses[lmid] + masses[rmid]) / 2.0f;
			p.time = (times[lmid] + times[rmid]) / 2.0f;
			p.time = (drifts[lmid] + drifts[rmid]) / 2.0f;
			c.clusterPeak = p;
			c.initialized = true;
		}
	};
	protected HierarchicalClusteringConfig cfg = null;
	protected List<ClusteringPeak> peaks = null;
	protected DistanceMatrix peakDistanceMatrix = null;

	/**
	 * set configuration data for this clustering session
	 * @param cfg
	 */
	public void setConfig(HierarchicalClusteringConfig cfg)
	{
		this.cfg = cfg;
	}

	/** @return the peakDistanceMatrix */
	public DistanceMatrix getPeakDistanceMatrix()
	{
		return peakDistanceMatrix;
	}

	/**
	 * generate distance matrix for given peaks
	 * @param peaks the list of peaks
	 * @return ready to use distance matrix
	 */
	public DistanceMatrix createPeakDistanceMatrix(List<ClusteringPeak> peaks)
	{
		int n = peaks.size();
		DistanceMatrix M = new DiagonalDistanceMatrix(n);
		for (int row = 1; row < n; row++)
		{
			for (int col = 0; col < row; col++)
			{
				M.setDistance(
						row, col,
						cfg.getDistanceMetric().getDistance(peaks.get(row), peaks.get(col))
						);
			}
		}
		return M;
	}

	/**
	 * create a new distance matrix for given group of peak clusters
	 * @param g the group
	 * @return the calculated distance matrix
	 */
	public DiagonalDistanceMatrix buildDistanceMatrix(PeakClusterGroup g)
	{
		int n = g.clusters.size();
		// create new distance matrix
		DiagonalDistanceMatrix M = new DiagonalDistanceMatrix(n);
		// build distances
		for (int row = 1; row < n; row++)
		{
			for (int col = 0; col < row; col++)
			{
				float dst = cfg.getDistanceMetric().measureDistance(g, row, col);
				M.setDistance(row, col, dst);
			}
		}
		return M;
	}

	/**
	 * look up distance between peaks
	 * @param a
	 * @param b
	 * @return
	 */
	public float lookupDistance(ClusteringPeak a, ClusteringPeak b)
	{
		int ai = peaks.indexOf(a);
		int bi = peaks.indexOf(b);
		float dist = peakDistanceMatrix.getDistance(ai, bi);
		return dist;
	}

	/**
	 * determine distance between two clusters
	 * @param group
	 * @param srcNode
	 * @param dstNode
	 * @return
	 */
	public abstract float measureDistance(PeakClusterGroup group, int srcNode, int dstNode);

	/**
	 * calculate quadratic (Euclidean) distance (metric) between two peaks,
	 * distance = timeDist^2 + massDist^2 + driftDist^2
	 * @param a first peak
	 * @param b second peak
	 * @return distance
	 */
	public float getDistance(ClusteringPeak a, ClusteringPeak b)
	{
		float mz = (a.mass - b.mass) / ((a.mass < b.mass) ? a.mass : b.mass) / cfg.getMassResolution();
		float rt = (a.time - b.time) / cfg.getTimeResolution();
		float dt = (a.drift - b.drift) / cfg.getDriftResolution();
		return mz * mz + rt * rt + dt * dt;
	}

	/** 
	 * set list of peaks to be clustered
	 * @param peaks
	 */
	public void setPeaks(List<ClusteringPeak> peaks)
	{
		this.peaks = Collections.unmodifiableList(peaks);
		peakDistanceMatrix = createPeakDistanceMatrix(peaks);
	}

	/** @return list of peaks beeing clutered */
	public List<ClusteringPeak> getPeaks()
	{
		return peaks;
	}
}

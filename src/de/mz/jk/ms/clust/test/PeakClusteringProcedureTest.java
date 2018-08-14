/** ISOQuant, isoquant.plugins.processing.expression.clustering.hnh, 24.11.2011 */
package de.mz.jk.ms.clust.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mz.jk.jsix.math.XMath;
import de.mz.jk.jsix.plot.pt.XYPlotter;
import de.mz.jk.ms.clust.com.ClusteringPeak;
import de.mz.jk.ms.clust.method.dbscan.DBSCANClusteringConfiguration;
import de.mz.jk.ms.clust.method.dbscan.DBSCANClusteringProcedure;
import de.mz.jk.ms.clust.method.hierarchical.HierarchicalClusteringConfig;
import de.mz.jk.ms.clust.method.hierarchical.HierarchicalClusteringProcedure;
import de.mz.jk.ms.clust.method.hierarchical.PeakClusterDistanceMetric;
import de.mz.jk.ms.clust.method.hierarchical.PeakClusterNeighborFinder;

/**
 * <h3>{@link PeakClusteringProcedureTest}</h3>
 * @author kuharev
 * @version 24.11.2011 10:42:00
 */
public class PeakClusteringProcedureTest
{
	public static void main(String[] args)
	{
		float[][] sampleData = sampleData2;
		int n = sampleData.length;
		List<ClusteringPeak> peaks = new ArrayList<ClusteringPeak>(n);// sampleData.length);
		for (int i = 0; i < n; i++)
		{
			float[] s = sampleData[i];
			ClusteringPeak p = new ClusteringPeak();
			p.id = i;
			p.time = s[0]; // * 0.01f + 10;
			p.mass = (float) s[1]; // * 0.001f + 2000;
			p.inten = (float) XMath.log2(s[2]); // * 500;
			if (p.time > 85 && p.time < 90 && p.mass > 1588.85 && p.mass < 1588.88)
				peaks.add(p);
		}
		float DISTANCE_TRESHOLD = 20f;
// {
// HierarchicalClusteringConfig cfg = new HierarchicalClusteringConfig();
// cfg.setMassResolution( (float) 0.75E-6 );
// cfg.setTimeResolution(0.01f);
// cfg.setDistanceMetric( new PeakClusterDistanceMetric.CENTROID() );
// cfg.setNeighborFinder( new
// PeakClusterNeighborFinder.HIGHEST_INTENSITY_FIRST() );
// cfg.setClusterDistanceThreshold(DISTANCE_TRESHOLD);
//
// HierarchicalClusteringProcedure pcp = new
// HierarchicalClusteringProcedure(peaks, cfg);
// pcp.run();
//
// Collection<Collection<IMSPeak>> clusters = pcp.getClusteringResult();
// plotClusters(clusters, "HIFI CENTROID");
// }
		DISTANCE_TRESHOLD = 1f;
		{
			DBSCANClusteringConfiguration cfg = new DBSCANClusteringConfiguration();
			cfg.setMassResolution((float) 6E-6);
			cfg.setTimeResolution(0.2f);
			cfg.setMinNeighborCount(3);
			cfg.setClusterDistanceThreshold(DISTANCE_TRESHOLD);
			DBSCANClusteringProcedure cp = new DBSCANClusteringProcedure(peaks, cfg);
			cp.run();
			Collection<Collection<ClusteringPeak>> clusters = cp.getClusteringResult();
			plotClusters(clusters, "DBSCAN(eps=" + cfg.getClusterDistanceThreshold() + ", pts=" + cfg.getMinNeighborCount() + ")");
		}
//
		DISTANCE_TRESHOLD = 1f;
		{
			HierarchicalClusteringConfig cfg = new HierarchicalClusteringConfig();
			cfg.setMassResolution(6E-6f);
			cfg.setTimeResolution(0.2f);
			cfg.setDistanceMetric(new PeakClusterDistanceMetric.SINGLE_LINKAGE());
			cfg.setNeighborFinder(new PeakClusterNeighborFinder.NEAREST_NEIGHBOR());
			cfg.setClusterDistanceThreshold(DISTANCE_TRESHOLD);
			HierarchicalClusteringProcedure pcp = new HierarchicalClusteringProcedure(peaks, cfg);
			pcp.run();
			Collection<Collection<ClusteringPeak>> clusters = pcp.getClusteringResult();
			plotClusters(clusters, "NN Single Linkage");
		}
// float DISTANCE_TRESHOLD = 60f;
// {
// HierarchicalClusteringConfig cfg = new HierarchicalClusteringConfig();
// cfg.setDistanceMetric( new
// PeakClusterDistanceMetric.INTENSITY_WEIGHTED_CENTROID() );
// cfg.setNeighborFinder( new
// PeakClusterNeighborFinder.HIGHEST_INTENSITY_FIRST() );
// cfg.setClusterDistanceTreshold(DISTANCE_TRESHOLD);
//
// HierarchicalClusteringProcedure pcp = new
// HierarchicalClusteringProcedure(peaks, cfg);
// pcp.run();
//
// List<List<IMSPeak>> clusters = pcp.getClusteringResult();
// plotClusters(clusters, "HiFi Intensity Weighted Centroid");
// }
// {
// HierarchicalClusteringConfig cfg = new HierarchicalClusteringConfig();
// cfg.setDistanceMetric( new PeakClusterDistanceMetric.CENTROID() );
// cfg.setNeighborFinder(new
// PeakClusterNeighborFinder.HIGHEST_INTENSITY_FIRST());
// cfg.setClusterDistanceTreshold(DISTANCE_TRESHOLD);
//
// HierarchicalClusteringProcedure pcp = new
// HierarchicalClusteringProcedure(peaks, cfg);
// pcp.run();
//
// List<List<IMSPeak>> clusters = pcp.getClusteringResult();
// plotClusters(clusters, "HiFi");
// }
// {
// HierarchicalClustering hc = new HierarchicalClustering(peaks, new
// AverageLinkage());
// hc.run();
// List<List<IMSPeak>> clusters = hc.getClusteredPeaksAsList();
//
// plotClusters(clusters, "old procedure");
// }
	}

	public static void plotClusters(Collection<Collection<ClusteringPeak>> clusters, String title)
	{
		XYPlotter plotter = new XYPlotter(800, 600);
		plotter.setPlotTitle(title);
		plotter.setPointStyle(XYPlotter.PointStyle.bigdots);
		System.out.println("plotting " + clusters.size() + " clusters for " + title + " ...");
		int i = 0;
		for (Collection<ClusteringPeak> l : clusters)
		{
			i++;
			for (ClusteringPeak p : l)
			{
				plotter.addPoint(i, p.time, p.mass, true);
			}
		}
	}
/*
-- getting large clusters
SELECT `cluster_average_index`, COUNT(`index`) as size 
FROM `clustered_emrt` 
GROUP BY `cluster_average_index`
ORDER BY size DESC;

-- getting cluster with specified size
SELECT `cluster_average_index`, COUNT(`index`) as size 
FROM `clustered_emrt` 
GROUP BY `cluster_average_index`
HAVING size=100;

-- getting peaks from one cluster
SELECT 
	CONCAT('new float[] {', ROUND(`ref_rt`,3), 'f, ', `mass`, 'f, ', `inten`, 'f}, ')
FROM `clustered_emrt`
WHERE `cluster_average_index`=92529
LIMIT 1000;
*/
	static float[][] sampleData1 = new float[][] {
			new float[] { 76f, 1588.7f, 1426.0f },
			new float[] { 122f, 1588.9f, 1143.0f }
	};
	static float[][] sampleData2 = new float[][] {
			new float[] { 76f, 1588.7f, 1426.0f },
			new float[] { 122f, 1588.9f, 1143.0f },
			new float[] { 77.700264f, 1588.867f, 1426.0f },
			new float[] { 79.88109f, 1588.8713f, 1143.0f },
			new float[] { 83.03301f, 1588.8755f, 2050.0f },
			new float[] { 85.222885f, 1588.8606f, 1183.0f },
			new float[] { 85.65446f, 1588.8762f, 1028.0f },
			new float[] { 88.053825f, 1588.8777f, 1727.0f },
			new float[] { 89.577194f, 1588.8741f, 1325.0f },
			new float[] { 90.7489f, 1588.8743f, 1682.0f },
			new float[] { 92.15134f, 1588.8726f, 912.0f },
			new float[] { 94.604294f, 1588.861f, 1122.0f },
			new float[] { 95.538155f, 1588.8784f, 967.0f },
			new float[] { 99.33401f, 1588.8752f, 973.0f },
			new float[] { 100.48996f, 1588.873f, 1071.0f },
			new float[] { 102.38611f, 1588.8698f, 980.0f },
			new float[] { 105.04557f, 1588.8658f, 860.0f },
			new float[] { 107.31026f, 1588.8656f, 947.0f },
			new float[] { 79.08711f, 1588.8634f, 1277.0f },
			new float[] { 79.44697f, 1588.8722f, 1510.0f },
			new float[] { 79.98437f, 1588.8765f, 1861.0f },
			new float[] { 81.678604f, 1588.8696f, 1647.0f },
			new float[] { 84.05291f, 1588.8707f, 2199.0f },
			new float[] { 86.46258f, 1588.8206f, 1852.0f },
			new float[] { 89.40327f, 1588.8647f, 849.0f },
			new float[] { 92.586914f, 1588.8748f, 870.0f },
			new float[] { 95.39776f, 1588.8674f, 934.0f },
			new float[] { 95.75101f, 1588.8656f, 1072.0f },
			new float[] { 96.9368f, 1588.8679f, 1434.0f },
			new float[] { 99.13309f, 1588.8181f, 3125.0f },
			new float[] { 99.9787f, 1588.8685f, 944.0f },
			new float[] { 101.093285f, 1588.8676f, 894.0f },
			new float[] { 103.409874f, 1588.8651f, 837.0f },
			new float[] { 104.36873f, 1588.8647f, 935.0f },
			new float[] { 105.98905f, 1588.8676f, 890.0f },
			new float[] { 79.238815f, 1588.7743f, 884.0f },
			new float[] { 80.45508f, 1588.865f, 2331.0f },
			new float[] { 81.29659f, 1588.805f, 848.0f },
			new float[] { 83.54159f, 1588.8678f, 1736.0f },
			new float[] { 86.44809f, 1588.8636f, 1352.0f },
			new float[] { 87.19798f, 1588.8702f, 811.0f },
			new float[] { 93.64555f, 1588.8748f, 1654.0f },
			new float[] { 94.0039f, 1588.856f, 1532.0f },
			new float[] { 94.96423f, 1588.8748f, 1308.0f },
			new float[] { 95.59346f, 1588.8741f, 1124.0f },
			new float[] { 97.23735f, 1588.8665f, 1089.0f },
			new float[] { 99.21498f, 1588.8232f, 3949.0f },
			new float[] { 100.8514f, 1588.8668f, 1123.0f },
			new float[] { 101.50503f, 1588.8698f, 1076.0f },
			new float[] { 102.23299f, 1588.87f, 1026.0f },
			new float[] { 105.61327f, 1588.866f, 1301.0f },
			new float[] { 106.26112f, 1588.8235f, 2793.0f },
			new float[] { 106.68232f, 1588.8674f, 828.0f },
			new float[] { 107.78824f, 1588.8663f, 976.0f },
			new float[] { 110.855034f, 1588.8752f, 1123.0f },
			new float[] { 113.44717f, 1588.8661f, 1109.0f },
			new float[] { 115.63621f, 1588.8668f, 913.0f },
			new float[] { 117.4912f, 1588.8711f, 935.0f },
			new float[] { 118.083275f, 1588.8676f, 1106.0f },
			new float[] { 119.05605f, 1588.8701f, 893.0f },
			new float[] { 120.120094f, 1588.8663f, 946.0f },
			new float[] { 120.96772f, 1588.8654f, 835.0f },
			new float[] { 83.38f, 1588.8691f, 2041.0f },
			new float[] { 84.75f, 1588.8743f, 1934.0f },
			new float[] { 87.08f, 1588.8771f, 1455.0f },
			new float[] { 89.34f, 1588.8724f, 1982.0f },
			new float[] { 90.32f, 1588.8689f, 1030.0f },
			new float[] { 91.21f, 1588.8706f, 1276.0f },
			new float[] { 95.08f, 1588.8613f, 1218.0f },
			new float[] { 95.89f, 1588.8668f, 1337.0f },
			new float[] { 96.63f, 1588.8658f, 1221.0f },
			new float[] { 97.6f, 1588.8721f, 983.0f },
			new float[] { 99.82f, 1588.8729f, 810.0f },
			new float[] { 100.4f, 1588.8856f, 863.0f },
			new float[] { 103.19f, 1588.8687f, 932.0f },
			new float[] { 77.78864f, 1588.8651f, 2109.0f },
			new float[] { 80.71369f, 1588.8696f, 2470.0f },
			new float[] { 87.9256f, 1588.8633f, 2510.0f },
			new float[] { 94.56818f, 1588.873f, 1433.0f },
			new float[] { 98.0583f, 1588.8616f, 984.0f },
			new float[] { 99.5708f, 1588.8689f, 1390.0f },
			new float[] { 101.39518f, 1588.8665f, 978.0f },
			new float[] { 102.86433f, 1588.8628f, 875.0f },
			new float[] { 104.42037f, 1588.8772f, 1229.0f },
			new float[] { 104.97013f, 1588.8627f, 1018.0f },
			new float[] { 105.30172f, 1588.8643f, 1338.0f },
			new float[] { 105.793564f, 1588.8715f, 826.0f },
			new float[] { 106.03269f, 1588.8225f, 2526.0f },
			new float[] { 106.62322f, 1588.8737f, 994.0f },
			new float[] { 107.20686f, 1588.8628f, 1161.0f },
			new float[] { 107.64874f, 1588.8734f, 1049.0f },
			new float[] { 111.32693f, 1588.8727f, 872.0f },
			new float[] { 112.71392f, 1588.8645f, 883.0f },
			new float[] { 117.39289f, 1588.8765f, 838.0f },
			new float[] { 79.80149f, 1588.8762f, 1236.0f },
			new float[] { 80.80151f, 1588.7706f, 1235.0f },
			new float[] { 81.269165f, 1588.8666f, 1416.0f },
			new float[] { 81.81761f, 1588.8785f, 1483.0f },
			new float[] { 82.92537f, 1588.8733f, 1602.0f },
			new float[] { 84.17854f, 1588.9001f, 2488.0f },
			new float[] { 88.6578f, 1588.8763f, 987.0f },
			new float[] { 92.45348f, 1588.8711f, 1117.0f },
			new float[] { 97.01172f, 1588.872f, 1755.0f },
			new float[] { 101.250694f, 1588.8708f, 879.0f },
			new float[] { 105.86646f, 1588.8231f, 1648.0f },
			new float[] { 77.35716f, 1588.8661f, 2649.0f },
			new float[] { 79.563995f, 1588.7657f, 1231.0f },
			new float[] { 85.01352f, 1588.8654f, 2530.0f },
			new float[] { 86.353424f, 1588.8658f, 2706.0f },
			new float[] { 89.27509f, 1588.8665f, 2404.0f },
			new float[] { 89.37206f, 1588.777f, 921.0f },
			new float[] { 89.67773f, 1588.8683f, 2390.0f },
			new float[] { 92.37295f, 1588.8757f, 2196.0f },
			new float[] { 92.474815f, 1588.7024f, 878.0f },
			new float[] { 92.60998f, 1588.8652f, 2235.0f },
			new float[] { 93.05833f, 1588.8756f, 1884.0f },
			new float[] { 96.02989f, 1588.8654f, 2160.0f },
			new float[] { 99.0808f, 1588.8612f, 1864.0f },
			new float[] { 100.06255f, 1588.8662f, 1365.0f },
			new float[] { 104.485054f, 1588.8713f, 1512.0f },
			new float[] { 104.881195f, 1588.8726f, 1637.0f },
			new float[] { 105.57221f, 1588.8726f, 1299.0f },
			new float[] { 107.04427f, 1588.8647f, 1036.0f },
			new float[] { 109.51818f, 1588.8647f, 1331.0f },
			new float[] { 109.89796f, 1588.8624f, 1351.0f },
			new float[] { 110.55191f, 1588.8823f, 1424.0f },
			new float[] { 111.06065f, 1588.8698f, 1137.0f },
			new float[] { 112.28407f, 1588.868f, 1507.0f },
			new float[] { 113.10453f, 1588.8734f, 1282.0f },
			new float[] { 113.761505f, 1588.8755f, 1027.0f },
			new float[] { 114.59521f, 1588.8658f, 1194.0f },
			new float[] { 115.45327f, 1588.8679f, 1128.0f },
			new float[] { 116.32749f, 1588.8698f, 1036.0f },
			new float[] { 116.93989f, 1588.8658f, 1179.0f },
			new float[] { 117.28907f, 1588.8704f, 954.0f },
			new float[] { 118.72604f, 1588.8702f, 1058.0f },
			new float[] { 120.27349f, 1588.8644f, 1024.0f },
			new float[] { 121.37441f, 1588.8701f, 1033.0f },
			new float[] { 77.63943f, 1588.8711f, 1571.0f },
			new float[] { 78.73769f, 1588.8666f, 2328.0f },
			new float[] { 79.190056f, 1588.8718f, 2369.0f },
			new float[] { 81.06444f, 1588.8627f, 1195.0f },
			new float[] { 95.48167f, 1588.8713f, 931.0f },
			new float[] { 96.25286f, 1588.8665f, 1324.0f },
			new float[] { 96.81583f, 1588.8688f, 1329.0f },
			new float[] { 97.4771f, 1588.8662f, 1253.0f },
			new float[] { 98.85524f, 1588.868f, 1009.0f },
			new float[] { 99.474945f, 1588.8658f, 1837.0f },
			new float[] { 100.15311f, 1588.8643f, 1207.0f },
			new float[] { 101.41158f, 1588.8741f, 1041.0f },
			new float[] { 101.84543f, 1588.8928f, 1656.0f },
			new float[] { 102.54703f, 1588.8737f, 1011.0f },
			new float[] { 108.670044f, 1588.8708f, 847.0f },
			new float[] { 109.29885f, 1588.869f, 977.0f },
			new float[] { 110.02179f, 1588.8701f, 854.0f },
			new float[] { 110.659164f, 1588.878f, 917.0f },
			new float[] { 112.90706f, 1588.8726f, 835.0f },
			new float[] { 115.09804f, 1588.8793f, 831.0f },
			new float[] { 115.44456f, 1588.8741f, 891.0f },
			new float[] { 117.989944f, 1588.8689f, 837.0f },
			new float[] { 118.593f, 1588.8693f, 828.0f },
			new float[] { 78.8388f, 1588.8643f, 2544.0f },
			new float[] { 80.55093f, 1588.8724f, 1708.0f },
			new float[] { 81.888596f, 1588.867f, 2192.0f },
			new float[] { 84.70581f, 1588.866f, 1549.0f },
			new float[] { 85.29662f, 1588.8652f, 1515.0f },
			new float[] { 86.429955f, 1588.7155f, 1064.0f },
			new float[] { 87.53043f, 1588.8651f, 1308.0f },
			new float[] { 88.12042f, 1588.8638f, 1997.0f },
			new float[] { 89.93101f, 1588.8673f, 1282.0f },
			new float[] { 90.41405f, 1588.8722f, 1049.0f },
			new float[] { 90.75464f, 1588.8688f, 1355.0f },
			new float[] { 91.29289f, 1588.8665f, 1138.0f },
			new float[] { 91.52674f, 1588.8696f, 1342.0f },
			new float[] { 94.9469f, 1588.8668f, 1184.0f },
			new float[] { 95.26935f, 1588.8632f, 1369.0f },
			new float[] { 98.562904f, 1588.8748f, 1099.0f },
			new float[] { 99.13034f, 1588.8787f, 1465.0f },
			new float[] { 104.005554f, 1588.8684f, 1082.0f },
			new float[] { 104.58119f, 1588.8715f, 977.0f },
			new float[] { 105.37475f, 1588.8716f, 912.0f },
			new float[] { 106.143936f, 1588.8666f, 806.0f },
			new float[] { 106.708954f, 1588.8778f, 1103.0f },
			new float[] { 109.19466f, 1588.8651f, 849.0f },
			new float[] { 110.71362f, 1588.8701f, 813.0f },
			new float[] { 113.27302f, 1588.8768f, 818.0f },
			new float[] { 114.12045f, 1588.8582f, 823.0f },
			new float[] { 114.5211f, 1588.8752f, 931.0f },
			new float[] { 115.2215f, 1588.8702f, 872.0f },
			new float[] { 115.716f, 1588.8789f, 841.0f },
			new float[] { 117.068214f, 1588.872f, 804.0f },
			new float[] { 118.460815f, 1588.8718f, 906.0f },
			new float[] { 119.737434f, 1588.8748f, 824.0f },
			new float[] { 79.27514f, 1588.7872f, 897.0f },
			new float[] { 79.25945f, 1588.7866f, 18320.0f },
			new float[] { 79.27314f, 1588.7821f, 21592.0f },
			new float[] { 79.27713f, 1588.783f, 12574.0f },
			new float[] { 79.27f, 1588.784f, 17636.0f },
			new float[] { 79.2724f, 1588.7837f, 11303.0f },
			new float[] { 79.27359f, 1588.7834f, 20758.0f },
			new float[] { 79.27138f, 1588.7834f, 11179.0f },
			new float[] { 79.24445f, 1588.7837f, 24539.0f },
			new float[] { 79.26907f, 1588.7833f, 1217.0f },
			new float[] { 78.56158f, 1588.8185f, 15762.0f },
			new float[] { 78.56137f, 1588.816f, 10752.0f },
			new float[] { 78.51f, 1588.8134f, 5157.0f },
			new float[] { 78.51421f, 1588.8138f, 2940.0f },
			new float[] { 78.50573f, 1588.8173f, 17517.0f },
			new float[] { 78.510796f, 1588.8163f, 5238.0f },
			new float[] { 79.81544f, 1588.8573f, 4389.0f },
			new float[] { 79.84397f, 1588.8647f, 4979.0f },
			new float[] { 79.804436f, 1588.8624f, 3986.0f },
			new float[] { 79.87f, 1588.8601f, 1982.0f },
			new float[] { 79.84388f, 1588.862f, 1474.0f },
			new float[] { 79.85522f, 1588.8622f, 9473.0f },
			new float[] { 84.95736f, 1588.875f, 3311.0f },
			new float[] { 84.99108f, 1588.8757f, 3410.0f },
			new float[] { 84.98663f, 1588.8737f, 4481.0f },
			new float[] { 84.95135f, 1588.8695f, 4507.0f },
			new float[] { 85.01079f, 1588.8715f, 1762.0f },
			new float[] { 78.05182f, 1588.8698f, 2106.0f },
			new float[] { 78.06f, 1588.8668f, 2067.0f },
			new float[] { 78.06041f, 1588.8654f, 3734.0f },
			new float[] { 99.28f, 1588.8182f, 2658.0f },
			new float[] { 99.239334f, 1588.8171f, 3599.0f },
			new float[] { 99.26738f, 1588.8181f, 2006.0f },
			new float[] { 79.34122f, 1588.8658f, 2494.0f },
			new float[] { 79.33391f, 1588.8672f, 3333.0f },
			new float[] { 98.22547f, 1588.8679f, 1013.0f },
			new float[] { 98.27569f, 1588.8696f, 3250.0f },
			new float[] { 88.642426f, 1588.868f, 1733.0f },
			new float[] { 88.70261f, 1588.8676f, 1484.0f },
			new float[] { 88.82f, 1588.8678f, 1107.0f },
			new float[] { 88.767624f, 1588.8641f, 3237.0f },
			new float[] { 80.39352f, 1588.8712f, 2036.0f },
			new float[] { 80.27708f, 1588.8677f, 3211.0f },
			new float[] { 80.33883f, 1588.8696f, 1898.0f },
			new float[] { 87.76009f, 1588.8671f, 1542.0f },
			new float[] { 87.7539f, 1588.8666f, 3092.0f },
			new float[] { 83.23664f, 1588.8691f, 1596.0f },
			new float[] { 83.20457f, 1588.868f, 2219.0f },
			new float[] { 83.15129f, 1588.8698f, 2890.0f },
			new float[] { 80.848595f, 1588.8729f, 1579.0f },
			new float[] { 80.8429f, 1588.8776f, 2783.0f },
			new float[] { 86.51832f, 1588.8708f, 2751.0f },
			new float[] { 86.47622f, 1588.8729f, 1163.0f },
			new float[] { 81.90407f, 1588.8765f, 2239.0f },
			new float[] { 81.8854f, 1588.8748f, 2186.0f },
			new float[] { 81.82128f, 1588.8715f, 2721.0f },
			new float[] { 78.22357f, 1588.8757f, 2715.0f },
			new float[] { 78.17187f, 1588.8722f, 1668.0f },
			new float[] { 81.51457f, 1588.8685f, 2668.0f },
			new float[] { 81.53204f, 1588.868f, 1890.0f },
			new float[] { 79.85522f, 1588.8397f, 2546.0f },
			new float[] { 79.86038f, 1588.8414f, 1547.0f },
			new float[] { 78.512985f, 1588.8738f, 1602.0f },
			new float[] { 78.504036f, 1588.8722f, 2376.0f },
			new float[] { 80.06993f, 1588.8718f, 2347.0f },
			new float[] { 80.09327f, 1588.8756f, 2222.0f },
			new float[] { 77.13449f, 1588.8716f, 2319.0f },
			new float[] { 77.19645f, 1588.8689f, 1919.0f },
			new float[] { 86.84567f, 1588.8704f, 1654.0f },
			new float[] { 86.90861f, 1588.8674f, 1042.0f },
			new float[] { 86.94457f, 1588.8665f, 1800.0f },
			new float[] { 87.00245f, 1588.8702f, 2310.0f },
			new float[] { 97.96438f, 1588.8676f, 819.0f },
			new float[] { 98.00045f, 1588.8718f, 2257.0f },
			new float[] { 83.78688f, 1588.8718f, 1884.0f },
			new float[] { 83.75577f, 1588.8715f, 1397.0f },
			new float[] { 83.69f, 1588.8729f, 1334.0f },
			new float[] { 83.676315f, 1588.8678f, 2218.0f },
			new float[] { 83.63528f, 1588.8647f, 1864.0f },
			new float[] { 90.94682f, 1588.8717f, 1356.0f },
			new float[] { 90.95989f, 1588.8695f, 2212.0f },
			new float[] { 81.171555f, 1588.8751f, 1750.0f },
			new float[] { 81.143005f, 1588.8713f, 2150.0f },
			new float[] { 77.583466f, 1588.8649f, 2143.0f },
			new float[] { 77.53277f, 1588.8691f, 1785.0f },
			new float[] { 88.26849f, 1588.8726f, 1543.0f },
			new float[] { 88.12f, 1588.8726f, 2071.0f },
			new float[] { 88.19181f, 1588.874f, 948.0f },
			new float[] { 82.50457f, 1588.8718f, 1711.0f },
			new float[] { 82.5806f, 1588.8741f, 1991.0f },
			new float[] { 82.60035f, 1588.8694f, 2023.0f },
			new float[] { 96.47726f, 1588.8652f, 1259.0f },
			new float[] { 96.5289f, 1588.8694f, 2009.0f },
			new float[] { 95.09451f, 1588.8763f, 963.0f },
			new float[] { 95.06408f, 1588.8761f, 2006.0f },
			new float[] { 89.18908f, 1588.87f, 1542.0f },
			new float[] { 89.22881f, 1588.872f, 1985.0f },
			new float[] { 84.48178f, 1588.8737f, 1957.0f },
			new float[] { 84.44438f, 1588.8704f, 1560.0f },
			new float[] { 100.59502f, 1588.8654f, 853.0f },
			new float[] { 100.62938f, 1588.8687f, 1869.0f },
			new float[] { 89.43237f, 1588.8737f, 1834.0f },
			new float[] { 89.437584f, 1588.8773f, 1367.0f },
			new float[] { 85.61987f, 1588.8647f, 1333.0f },
			new float[] { 85.76916f, 1588.8688f, 1002.0f },
			new float[] { 85.72f, 1588.8721f, 1636.0f },
			new float[] { 85.66113f, 1588.8693f, 1252.0f },
			new float[] { 85.6571f, 1588.8708f, 1591.0f },
			new float[] { 85.522285f, 1588.8708f, 1810.0f },
			new float[] { 85.58693f, 1588.8708f, 1234.0f },
			new float[] { 80.82f, 1588.8665f, 1342.0f },
			new float[] { 80.89191f, 1588.8678f, 1776.0f },
			new float[] { 86.02739f, 1588.8685f, 1568.0f },
			new float[] { 86.115295f, 1588.868f, 1756.0f },
			new float[] { 86.08893f, 1588.8695f, 1398.0f },
			new float[] { 96.90524f, 1588.8752f, 1735.0f },
			new float[] { 96.87266f, 1588.8752f, 1278.0f },
			new float[] { 85.88301f, 1588.8707f, 1368.0f },
			new float[] { 85.90004f, 1588.87f, 1689.0f },
			new float[] { 85.85744f, 1588.8674f, 1693.0f },
			new float[] { 91.70637f, 1588.8708f, 1684.0f },
			new float[] { 91.74531f, 1588.8665f, 1015.0f },
			new float[] { 87.52643f, 1588.8745f, 1153.0f },
			new float[] { 87.538185f, 1588.8727f, 1404.0f },
			new float[] { 87.63f, 1588.8708f, 1349.0f },
			new float[] { 87.60826f, 1588.8717f, 1027.0f },
			new float[] { 87.5865f, 1588.8698f, 1660.0f },
			new float[] { 102.68523f, 1588.8751f, 910.0f },
			new float[] { 102.744255f, 1588.8712f, 1624.0f },
			new float[] { 102.68407f, 1588.8696f, 1045.0f },
			new float[] { 108.514496f, 1588.8687f, 906.0f },
			new float[] { 108.58959f, 1588.8666f, 1589.0f },
			new float[] { 92.01f, 1588.868f, 1201.0f },
			new float[] { 91.95978f, 1588.8754f, 1026.0f },
			new float[] { 91.922874f, 1588.8691f, 1144.0f },
			new float[] { 91.94133f, 1588.8712f, 1492.0f },
			new float[] { 92.76f, 1588.8676f, 1282.0f },
			new float[] { 92.766815f, 1588.8759f, 1145.0f },
			new float[] { 92.76487f, 1588.8722f, 1488.0f },
			new float[] { 108.88825f, 1588.8668f, 901.0f },
			new float[] { 108.93328f, 1588.87f, 942.0f },
			new float[] { 108.93322f, 1588.8729f, 1476.0f },
			new float[] { 97.735725f, 1588.8698f, 1298.0f },
			new float[] { 97.79004f, 1588.8698f, 1391.0f },
			new float[] { 97.80549f, 1588.8699f, 1459.0f },
			new float[] { 94.390564f, 1588.8708f, 1137.0f },
			new float[] { 94.36025f, 1588.87f, 1339.0f },
			new float[] { 94.34803f, 1588.865f, 1029.0f },
			new float[] { 94.28979f, 1588.8627f, 1438.0f },
			new float[] { 94.015396f, 1588.8641f, 1146.0f },
			new float[] { 94.03f, 1588.8683f, 1416.0f },
			new float[] { 103.504776f, 1588.87f, 1014.0f },
			new float[] { 103.51601f, 1588.8693f, 1239.0f },
			new float[] { 103.649376f, 1588.8657f, 1310.0f },
			new float[] { 103.58766f, 1588.8676f, 1019.0f },
			new float[] { 91.59268f, 1588.8627f, 1295.0f },
			new float[] { 91.63532f, 1588.8665f, 1229.0f },
			new float[] { 89.01721f, 1588.8702f, 1294.0f },
			new float[] { 89.073524f, 1588.8683f, 1138.0f },
			new float[] { 98.37444f, 1588.8654f, 954.0f },
			new float[] { 98.4079f, 1588.8608f, 1282.0f },
			new float[] { 95.70277f, 1588.8737f, 902.0f },
			new float[] { 95.78449f, 1588.8718f, 1243.0f },
			new float[] { 95.16491f, 1588.868f, 1308.0f },
			new float[] { 95.09672f, 1588.8683f, 1234.0f },
			new float[] { 100.04358f, 1588.873f, 928.0f },
			new float[] { 100.11786f, 1588.873f, 1192.0f },
			new float[] { 89.79401f, 1588.8693f, 1168.0f },
			new float[] { 89.76745f, 1588.8671f, 974.0f },
			new float[] { 99.708015f, 1588.8668f, 1010.0f },
			new float[] { 99.66833f, 1588.8715f, 1158.0f },
			new float[] { 110.265884f, 1588.8665f, 1097.0f },
			new float[] { 110.249886f, 1588.8694f, 937.0f },
			new float[] { 115.92223f, 1588.8704f, 1088.0f },
			new float[] { 115.887566f, 1588.8689f, 838.0f },
			new float[] { 81.35f, 1588.8477f, 887.0f },
			new float[] { 81.33858f, 1588.8486f, 1070.0f },
			new float[] { 112.372475f, 1588.8658f, 1013.0f },
			new float[] { 112.39892f, 1588.8687f, 895.0f },
			new float[] { 111.57462f, 1588.8691f, 978.0f },
			new float[] { 111.55561f, 1588.8684f, 853.0f },
			new float[] { 113.00413f, 1588.8657f, 829.0f },
			new float[] { 112.95933f, 1588.8647f, 971.0f },
			new float[] { 102.09242f, 1588.8674f, 871.0f },
			new float[] { 102.07277f, 1588.8644f, 1064.0f },
			new float[] { 101.99046f, 1588.8654f, 848.0f },
			new float[] { 101.95001f, 1588.8656f, 986.0f },
			new float[] { 116.48494f, 1588.8757f, 824.0f },
			new float[] { 116.4694f, 1588.8765f, 839.0f },
			new float[] { 113.85394f, 1588.8739f, 838.0f },
			new float[] { 113.86685f, 1588.8717f, 808.0f },
			new float[] { 103.102066f, 1588.8705f, 800.0f },
			new float[] { 103.10938f, 1588.8737f, 836.0f },
			new float[] { 111.47997f, 1588.8702f, 823.0f },
			new float[] { 111.53597f, 1588.8724f, 801.0f }
	};
}

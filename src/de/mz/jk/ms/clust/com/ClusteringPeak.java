package de.mz.jk.ms.clust.com;

/**
 * simplified representation of an EMRT
 * @author Jörg Kuharev
 */
public class ClusteringPeak
{
	/** index */
	public int id = 0;
	/** mass */
	public float mass = 0f;
	/** ref_rt */
	public float time = 0f;
	/** cluster_average_index */
	public int cluster = 0;
	/** intensity */
	public float inten = 0f;
	/** ion mobility */
	public float drift = 0f;

	@Override public int hashCode()
	{
		return id;
	}
}

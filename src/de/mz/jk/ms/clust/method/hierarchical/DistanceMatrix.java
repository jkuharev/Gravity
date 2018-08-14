package de.mz.jk.ms.clust.method.hierarchical;

/**
 * graph representation by a distance matrix
 * @author Jörg Kuharev
 */
public class DistanceMatrix
{
	/** number of vertices */
	protected int size = 0;
	/** distance storage */
	protected float[][] D = null;
	/** minimum available distance */
	protected float minDistance = Float.MAX_VALUE;
	/** maximum available distance */
	protected float maxDistance = Float.MIN_VALUE;
	/** initial/unconnected distance */
	public float initDistance = Float.MAX_VALUE;

	public DistanceMatrix()
	{}

	/**
	 * @param numberOfVertices number of nodes in your graph
	 * @param zeroDistance the value of zero distance, e.g. distance from a vertex to itself
	 * @param unconnectedDistance initial distance between unconnected vertices 
	 */
	public DistanceMatrix(int numberOfVertices, float unconnectedDistance)
	{
		initDistance = unconnectedDistance;
		initDistanceMatrix(numberOfVertices);
	}

	/**
	 * @param numberOfVertices number of nodes in your graph
	 */
	public DistanceMatrix(int numberOfVertices)
	{
		initDistanceMatrix(numberOfVertices);
	}

	/**
	 * (re)initialize distance matrix
	 * @param numberOfVertices number of nodes in your graph
	 */
	public void initDistanceMatrix(int numberOfVertices)
	{
		size = numberOfVertices;
		// full dense matrix
		D = new float[size][size];
	}

	/**
	 * set a distance
	 * @param fromVertex
	 * @param toVertex
	 * @param dist
	 */
	public void setDistance(int fromVertex, int toVertex, float dist)
	{
		D[fromVertex][toVertex] = dist;
		if (minDistance > dist) minDistance = dist;
		if (maxDistance < dist) maxDistance = dist;
	}

	/**
	 * @param fromVertex
	 * @param toVertex
	 * @return distance between vertices
	 */
	public float getDistance(int fromVertex, int toVertex)
	{
		return D[fromVertex][toVertex];
	}

	/**
	 * @return number of vertices
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * @return the minDistance
	 */
	public float getMinDistance()
	{
		return minDistance;
	}

	/**
	 * @return the maxDistance
	 */
	public float getMaxDistance()
	{
		return maxDistance;
	}
}

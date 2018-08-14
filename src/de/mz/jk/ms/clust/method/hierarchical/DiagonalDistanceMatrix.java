package de.mz.jk.ms.clust.method.hierarchical;

/**
 * Undirected graph represented by a diagonal distance matrix.<br>
 * Vertices are indexed in java manner from 0.<br>
 * Edges between given vertices are float values.<br>
 * @author Jörg Kuharev
 */
public class DiagonalDistanceMatrix extends DistanceMatrix
{
	/** default zero distance */
	public float zeroDistance = 0.0f;

	/**
	 * @param numberOfVertices number of nodes in your graph
	 * @param zeroDistance the value of zero distance, e.g. distance from a vertex to itself 
	 */
	public DiagonalDistanceMatrix(int numberOfVertices, float zeroDistance)
	{
		this.zeroDistance = zeroDistance;
		initDistanceMatrix(numberOfVertices);
	}

	/**
	 * @param numberOfVertices number of nodes in your graph
	 */
	public DiagonalDistanceMatrix(int numberOfVertices)
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
		int n = size - 1;
		D = new float[n][];
		// let's create diagonal matrix having bottom left values
		for (int row = 0; row < n; row++)
		{
			D[row] = new float[row + 1];
		}
	}

	/**
	 * set a distance
	 * @param fromVertex
	 * @param toVertex
	 * @param dist
	 */
	public void setDistance(int fromVertex, int toVertex, float dist)
	{
		// make sure addressing from higher index to lower one
		if (fromVertex > toVertex)
		{
			D[fromVertex - 1][toVertex] = dist;
		}
		else if (fromVertex < toVertex)
		{
			D[toVertex - 1][fromVertex] = dist;
		}
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
		return (fromVertex == toVertex)
				? zeroDistance // no distance
				: (fromVertex > toVertex) // ensure we are in lower left
// diagonal of matrix
				? D[fromVertex - 1][toVertex]
						: D[toVertex - 1][fromVertex];
	}
}

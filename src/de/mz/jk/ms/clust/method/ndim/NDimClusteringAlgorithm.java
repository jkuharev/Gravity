/** DBSCAN, xclust, 30.03.2012*/
package de.mz.jk.ms.clust.method.ndim;

/**
 * <h3>{@link NDimClusteringAlgorithm}</h3>
 * @author kuharev
 * @version 30.03.2012 10:10:41
 */
public abstract class NDimClusteringAlgorithm implements Runnable
{
	/** underlying space */
	protected NDimSpace space = null;

	/** set the space */
	public void setSpace( NDimSpace space )
	{
		this.space  = space;
	}
	
	/** get the space */
	public NDimSpace getSpace()
	{
		return space;
	}
	
	@Override public void run()
	{
		doClustering();
	}

	/** do clustering procedure */
	public abstract void doClustering();	
}

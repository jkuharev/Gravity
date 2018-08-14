package de.mz.jk.ms.clust.com;

import java.util.Collection;
import java.util.concurrent.Semaphore;

import de.mz.jk.ms.clust.com.ClusteringPeak;

/**
 * <h3>{@link GeneralPeakClusteringProcedure}</h3>
 * @author kuharev
 * @version 23.11.2011 12:41:53
 */
public abstract class GeneralPeakClusteringProcedure<ImplementedConfigurationType extends GeneralPeakClusteringConfiguration> implements Runnable
{
	protected ImplementedConfigurationType cfg = null;
	protected Collection<Collection<ClusteringPeak>> clusteringResult = null;
	protected Semaphore lock = new Semaphore(1);

	/**
	 * prepare clustering for a list of peaks 
	 * by using given configuration
	 * and acquire result resource lock
	 * which will be released after clustering procedure is done
	 * @param peaks
	 * @param cfg
	 */
	public GeneralPeakClusteringProcedure(ImplementedConfigurationType cfg)
	{
		cfg.setClusteringProcedure(this);
		this.cfg = cfg;
		lock.acquireUninterruptibly();
	}

	public void run()
	{
		try
		{
			runClusteringAlgorithm();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		lock.release();
	}

	/** do specific algorithm to cluster peaks */
	protected abstract void runClusteringAlgorithm();

	/**
	 * retrieve results of this clustering run,
	 * this function will wait until this clistering thread will have finished
	 * @return list of lists of peaks
	 */
	public Collection<Collection<ClusteringPeak>> getClusteringResult()
	{
		lock.acquireUninterruptibly();
		lock.release();
		return clusteringResult;
	}

	/** @return the cfg */
	public ImplementedConfigurationType getCfg()
	{
		return cfg;
	}
}

package de.mz.jk.ms.clust.com;

import java.util.Collection;

public abstract class GeneralPeakClusteringThread<ImplementedConfigurationType extends GeneralPeakClusteringConfiguration> extends Thread
{
	public boolean DEBUG = false;
	public static int idCounter = 0;
	private PipeLine<Collection<ClusteringPeak>> in = null;
	private PipeLine<Collection<ClusteringPeak>> out = null;
	private int id = 0;
	private int loopCounter = 0;
	private ImplementedConfigurationType cfg = null;

	/**
	 * after start()
	 * this thread iteratively takes peaks from input pipeline
	 * then runs a clustering procedure on them (each time passing configuration parameters to it) 
	 * and outputs results to the output pipeline.<br>
	 * the loop is done until no peaks from the input pipeline are available
	 * @param in
	 * @param out
	 * @param cfg
	 */
	public GeneralPeakClusteringThread(PipeLine<Collection<ClusteringPeak>> in, PipeLine<Collection<ClusteringPeak>> out, ImplementedConfigurationType cfg)
	{
		this.in = in;
		this.out = out;
		this.cfg = cfg;
		id = ++idCounter;
	}

	@Override public void run()
	{
		if (in == null || out == null) return;
		if (DEBUG) System.out.println(id + ":\tclustering thread started!");
		Collection<ClusteringPeak> inPeaks = null;
		while ((inPeaks = in.take()) != null)
		{
			loopCounter++;
			Collection<Collection<ClusteringPeak>> outPeaks = runClustering(inPeaks, cfg);
			for (Collection<ClusteringPeak> c : outPeaks)
			{
				out.put(c);
			}
		}
		if (DEBUG) System.out.println(id + ":\tclustering thread stopped after " + loopCounter + " clustering approaches.");
	}

	/**
	 * @param peaks
	 * @param cfg
	 */
	protected abstract Collection<Collection<ClusteringPeak>> runClustering(Collection<ClusteringPeak> peaks, ImplementedConfigurationType cfg);
}

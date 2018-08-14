/**
 * ISOQuant, isoquant.plugins.processing.expression.align.linear.parallel,
 * 10.05.2011
 */
package de.mz.jk.ms.align.com;

/**
 * <h3>{@link PeakMatcher}</h3>
 * @author Joerg Kuharev
 * @version 10.05.2011 13:40:11
 */
public class PeakMatcher extends AlignmentMatcher<IMSPeak>
{
	protected double maxDeltaMass = 10.0 / 1000000.0;
	protected double maxDeltaDrift = 1.0;

	/**
	 * create distance calculator and define parameters
	 * @param scoreMatch
	 * @param scoreMismatch
	 * @param scoreGap
	 * @param maxDeltaMassPPM
	 */
	public PeakMatcher(int scoreMatch, int scoreMismatch, int scoreGap, double maxDeltaMassPPM, double maxDeltaDriftTime)
	{
		super(scoreMatch, scoreMismatch, scoreGap);
		setMaxDeltaMassPPM(maxDeltaMassPPM);
		setMaxDeltaDrift(maxDeltaDriftTime);
	}

	/** use default values */
	public PeakMatcher()
	{
		super(-1, 3, 1);
	}

	/**
	 * set ppm threshold<br>
	 * use <b>setMaxDeltaMassPPM(5);</b> 
	 * for use max allowed difference between two masses of 5/1000000
	 * @param ppm
	 */
	public void setMaxDeltaMassPPM(double ppm)
	{
		maxDeltaMass = ppm / 1000000.0;
	}

	/**
	 * @param maxDeltaDrift the maximum accepted drift difference between two peaks
	 */
	public void setMaxDeltaDrift(double maxDeltaDrift)
	{
		this.maxDeltaDrift = maxDeltaDrift;
	}

	/**
	 * compare peaks
	 * @param a first peak
	 * @param b second peak
	 * @return true if mass difference <= allowedMassPPM, otherwise false
	 */
	public boolean match(IMSPeak a, IMSPeak b)
	{
		boolean res =
				a != null && b != null
						&& a.type == b.type
						&& ((a.drift > b.drift) ? matchMobility(a.drift, b.drift) : matchMobility(b.drift, a.drift))
						&& ((a.mass > b.mass) ? matchMass(a.mass, b.mass) : matchMass(b.mass, a.mass))
// (a.mass>b.mass)
// ? ((a.mass - b.mass) <= a.mass * maxDeltaMass)
// : ((b.mass-a.mass) <= b.mass * maxDeltaMass)
		;
		return res;
	}

	/**
	 * @param drift
	 * @param drift2
	 * @return
	 */
	private boolean matchMobility(double biggerDrift, double smallerDrift)
	{
		boolean res = (biggerDrift - smallerDrift) <= maxDeltaDrift;
		return res;
	}

	/**
	 * @param mass
	 * @param mass2
	 * @return
	 */
	private boolean matchMass(double biggerMass, double smallerMass)
	{
		boolean res = (biggerMass - smallerMass) <= biggerMass * maxDeltaMass;
		return res;
	}
}

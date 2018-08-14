package de.mz.jk.ms.align.com;

/**
 * simple representation of a mass spectra peak, or EMRT
 * @author J.Kuharev
 */
public class IMSPeak
{
	/** user defined peak */
	public IMSPeak(long index, float mass, float rt)
	{
		this.peak_id = index;
		this.mass = mass;
		this.rt = rt;
	}
	
	public IMSPeak(long index, float mass, float rt, float drift)
	{
		this( index, mass, rt );
		this.drift = drift;
	}

	public IMSPeak(long index, float mass, float rt, float drift, float intensity)
	{
		this( index, mass, rt, drift );
		this.intensity = intensity;
	}

	public IMSPeak(long index, float mass, float rt, float drift, float intensity, byte type)
	{
		this( index, mass, rt, drift, intensity );
		this.type = type;
	}
	
	/** empty peak with peak_id = 0, mass = 0, time = 0 */
	public IMSPeak(){}

	/** peak identification */
	public long peak_id = 0;
	
	/** run identification */
	public int peak_list_id = 0;
	
	/** detected mz */
	public float mz = 0.0f;

	/** measured mass */
	public float mass = 0.0f;
	
	/** charge state */
	public float charge = 0;

	/** retention time */
	public float rt = 0.0f;
	
	/** corrected retention time */
	public float ref_rt = 0.0f;
	
	/** ion mobility */
	public float drift = 0.0f;
	
	/** type of peak, could be used for filtering */
	public byte type = 0;

	/** intensity **/
	public float intensity = 0.0f;
}

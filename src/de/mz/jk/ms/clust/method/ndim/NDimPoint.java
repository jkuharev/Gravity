package de.mz.jk.ms.clust.method.ndim;
/** DBSCAN, , 23.03.2012*/
/**
 * <h3>{@link NDimPoint}</h3>
 * @author kuharev
 * @version 23.03.2012 10:07:50
 */
public class NDimPoint<PAYLOAD_TYPE>
{
	private PAYLOAD_TYPE payload = null;
	
	private static int idCounter = 0; 
	public int id = 0;
	
	/** n-dimansional coordinates */
	public float x[] = null;
	
	/** index position */
	public int i[] = null;
	
	/** already visited mark */
	public boolean unvisited = true;
		
	/** is noise */
	public boolean noise = false;
	
	/**
	 * point with given dimensions and auto-incremented id 
	 * @param dim
	 * @return
	 */
	public NDimPoint(int dim)
	{
		x = new float[dim];
		i = new int[dim];		
		id = ++idCounter;
	}
	
	/**
	 * point with given dimensions and given id
	 * @param dim
	 * @param id
	 */
	public NDimPoint(int dim, int id)
	{
		x = new float[dim];
		i = new int[dim];		
		this.id = id;
	}
	
	/**
	 * point with given id and coordinates
	 * @param id
	 * @param coords
	 */
	public NDimPoint(int id, float[] coords)
	{
		this.id = id;
		this.x = coords;
		this.i = new int[x.length];
	}
	
	/**
	 * point with given id, coordinates and payload
	 * @param id
	 * @param coords
	 * @param payload
	 */
	public NDimPoint(int id, float[] coords, PAYLOAD_TYPE payload)
	{
		this(id, coords);
		setPayload(payload);		
	}
	
	/** @param payload the payload to set */
	public void setPayload(PAYLOAD_TYPE payload)
	{
		this.payload = payload;
	}
	
	/** @return the payload */
	public PAYLOAD_TYPE getPayload()
	{
		return payload;
	}
	
	/** 
	 * compaire point by their id
	 * make sure id is unique!!! 
	 */
	@Override public boolean equals(Object obj) 
	{
		if(obj instanceof NDimPoint) return ((NDimPoint)obj).id == this.id;
		return false;
	}
	
	/** id as hash code */
	@Override public int hashCode()
	{
		return this.id;
	}
}

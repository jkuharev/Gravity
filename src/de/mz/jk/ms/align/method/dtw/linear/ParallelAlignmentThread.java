/** ISOQuant, isoquant.plugins.processing.expression.align.linear, 05.08.2011*/
package de.mz.jk.ms.align.method.dtw.linear;

import java.util.concurrent.Semaphore;

import de.mz.jk.jsix.lists.XSubList;
import de.mz.jk.ms.align.com.LinkedAlignment;

/**
 * <h3>{@link ParallelAlignmentThread}</h3>
 * @author Joerg Kuharev
 * @version 05.08.2011 12:20:53
 */
public class ParallelAlignmentThread <TYPE> extends Thread
{
	private LinkedAlignment<TYPE> alignment = null;
	private ParallelAlignment<TYPE> hirsch = null;
	private XSubList<TYPE> left = null;
	private XSubList<TYPE> top = null;
	private int depth = 0;
	private Semaphore sem = new Semaphore(0); 		
	
	/**
	 * construct a thread and execute wrapped alignment implementation
	 * @param leftSeq
	 * @param topSeq
	 * @param alnImpl
	 * @param depth
	 */
	public ParallelAlignmentThread( XSubList<TYPE> leftSeq, XSubList<TYPE> topSeq, ParallelAlignment<TYPE> alnImpl, int depth)
	{
		this.left = leftSeq;
		this.top = topSeq;
		this.hirsch = alnImpl;
		this.depth  = depth;
		this.start();
	}

	@Override public void run()
	{
		alignment = hirsch.align(top, left, depth);
		sem.release();
	}
	
	/**
	 * wait until execution of wrapped alignment algorithm ends and get calculated alignment
	 * @return calculated alignment
	 */
	public LinkedAlignment<TYPE> getAlignment()
	{
		sem.acquireUninterruptibly();
		return alignment;
	}
}

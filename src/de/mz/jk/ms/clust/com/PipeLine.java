package de.mz.jk.ms.clust.com;

import java.util.LinkedList;
import java.util.Queue;

/**
 * synchronized queue of TYPE elements based on linked list
 * with given or default capacity and termination sequence (shutdown signal)
 * @author Jörg Kuharev
 * @param <TYPE>
 */
public class PipeLine<TYPE>
{
	public static final int DEFAULT_INITIAL_CAPACITY = 1000;
	private Queue<TYPE> buffer = null;
	private int capacity = DEFAULT_INITIAL_CAPACITY;
	private int fillRate = 0;
	private boolean active = true;

	/**
	 * pipeline with given initial capacity
	 * @param capacity
	 */
	public PipeLine(int capacity)
	{
		this();
		this.capacity = capacity;
	}

	/**
	 * pipeline with default initial capacity
	 */
	public PipeLine()
	{
		buffer = new LinkedList<TYPE>();
	}

	/**
	 * enqueue an element into pipeline and reactivates terminated pipeline
	 * @param content
	 */
	public synchronized void put(TYPE content)
	{
		// wait for free capacity
		while (fillRate >= capacity)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		// mutual exclusion begin
		if (content != null)
		{
			buffer.add(content);
			active = true;
			fillRate++; // use fillRate instead of queue.size()
		}
		// mutual exclusion end
		notifyAll();
	}

	/**
	 * dequeue an element from pipeline
	 * @return head element from queue or null if queue is empty and terminated
	 */
	public synchronized TYPE take()
	{
		// wait for availability of elements
		while (fillRate <= 0 && active)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		// mutual exclusion begin
		TYPE res = null;
		if (fillRate > 0)
		{
			res = buffer.poll();
			fillRate--;
		}
		// mutual exclusion end
		notifyAll();
		return res;
	}

	/**
	 * request shutdown
	 */
	public synchronized void shutdown()
	{
		active = false;
		notifyAll();
	}
}

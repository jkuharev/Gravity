/** ISOQuant, isoquant.plugins.processing.expression.align, 05.05.2011*/
package de.mz.jk.ms.align.com;

import de.mz.jk.jsix.lists.XLinkedList;


/**
 * <h3>{@link LinkedAlignment}</h3>
 * 
 * alignment of two rows (X and Y) containing user defined objects,
 * implemented as a linked list having head and tail alignment objects.<br>
 * 
 * ATTENTION: this implementation does not check for cyclic linkage
 * 
 * @author Joerg Kuharev
 * @version 05.05.2011 10:04:41
 */
public class LinkedAlignment<T> extends XLinkedList<LinkedAlignment<T>, T>
{
	private T x = null;
	private T y = null;
	
	/**
	 * create empty null-null alignment
	 */
	public LinkedAlignment(){}
	
	/**
	 * create alignment
	 * @param x
	 * @param y
	 */
	public LinkedAlignment(T x, T y)
	{ 
		setXY(x, y);
	}

	/** set or replace current X value */
	public void setX(T x){this.x = x;}
	
	/** get current X */
	public T getX(){return x;}
	
	/** set or replace current Y value */
	public void setY(T y){this.y = y;}
	
	/** get current Y */
	public T getY(){return y;}
	
	/**
	 * set or replace current X and Y
	 * @param x the new X value
	 * @param y the new Y value
	 */
	public void setXY(T x, T y)
	{ 
		this.setX(x); 
		this.setY(y);
	}
}

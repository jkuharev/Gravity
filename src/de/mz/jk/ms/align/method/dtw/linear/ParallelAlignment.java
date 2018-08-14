/** ISOQuant, isoquant.plugins.processing.expression.align.linear, 05.08.2011*/
package de.mz.jk.ms.align.method.dtw.linear;

import de.mz.jk.jsix.lists.XSubList;
import de.mz.jk.ms.align.com.LinkedAlignment;

/**
 * <h3>{@link ParallelAlignment}</h3>
 * @author Joerg Kuharev
 * @version 05.08.2011 12:17:19
 */
public interface ParallelAlignment <TYPE>
{
	public LinkedAlignment<TYPE> align( XSubList<TYPE> topSeq, XSubList<TYPE> leftSeq, int depth );
	public void setMaxDepth(int maxDepth);
}

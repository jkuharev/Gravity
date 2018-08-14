/** ISOQuant, isoquant.plugins.processing.expression.align, 13.05.2011*/
package de.mz.jk.ms.align.com;

/**
 * <h3>{@link DTWPathDescription}</h3>
 * description of the dynamic programming path
 * @author Joerg Kuharev
 * @version 13.05.2011 10:38:25
 */
public enum DTWPathDescription
{
	LEFT, RIGHT;

	public static DTWPathDescription fromString(String pathDesc)
	{
		String d = pathDesc.toLowerCase();
		if (d.contains( "right" )) return RIGHT;
		if (d.contains( "left" )) return LEFT;
		if (d.startsWith( "r" )) return RIGHT;
		return LEFT;
	}
}

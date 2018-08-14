package de.mz.jk.ms.align.com;

import java.util.*;

/**
 * An alignment contains multiple rows (with equal sizes).
 * Each row is identified by a unique rowID.
 * Peaks from different rows at identical positions were clustered together.
 * Gaps are marked with 'null' instead of valid TYPE-object
 * so there are positions with a peak in one row and 'null' in an other. 
 * @author J.Kuharev
 */
public class PairwiseAlignment<TYPE>
{
	private Map<Object, List<TYPE>> rows = new HashMap<Object, List<TYPE>>();

	public PairwiseAlignment()
	{}

	public PairwiseAlignment(Object rowID1, List<TYPE> row1, Object rowID2, List<TYPE> row2)
	{
		this();
		setRow(rowID1, row1);
		setRow(rowID2, row2);
	}

	/**
	 * add a new identified alignment row
	 * @param rowID
	 * @param row
	 */
	public void setRow(Object rowID, List<TYPE> row)
	{
		rows.put(rowID, row);
	}

	/**
	 * create a new identified alignment row
	 * @param rowID
	 */
	public void createRow(Object rowID)
	{
		rows.put(rowID, new ArrayList<TYPE>());
	}

	/**
	 * replace existing peak at given index in identified row
	 * @param rowID
	 * @param index
	 * @param peak
	 */
	public void setPeak(Object rowID, int index, TYPE peak)
	{
		rows.get(rowID).set(index, peak);
	}

	/**
	 * row with given identifier
	 * @param rowID
	 * @return
	 */
	public List<TYPE> getRow(Object rowID)
	{
		return rows.get(rowID);
	}

	/**
	 * available row identifier
	 * @return
	 */
	public Collection<Object> getRowIDs()
	{
		return rows.keySet();
	}

	/**
	 * size of longest row
	 * @return
	 */
	public int getSize()
	{
		int size = 0;
		for (Object id : rows.keySet())
		{
			if (rows.get(id).size() > size)
				size = rows.get(id).size();
		}
		return size;
	}

	/**
	 * one column of alignment at index position
	 * @param index
	 * @return Map with RowID named Peaks
	 */
	public Map<Object, TYPE> getAlignmentAt(int index)
	{
		Map<Object, TYPE> res = new HashMap<Object, TYPE>();
		for (Object id : rows.keySet())
		{
			res.put(id, (rows.get(id).size() > index) ? rows.get(id).get(index) : null);
		}
		return res;
	}
}

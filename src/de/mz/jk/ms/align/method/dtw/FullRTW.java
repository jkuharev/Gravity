package de.mz.jk.ms.align.method.dtw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.mz.jk.jsix.math.matrix.ArrayMatrix;
import de.mz.jk.jsix.utilities.Bencher;
import de.mz.jk.ms.align.com.PairwiseAlignment;
import de.mz.jk.ms.align.com.IMSPeak;

/**
 * FullRTW = Full Retention Time Warping<br>
 * RT Alignment (or Dynamic Time Warping) between 2 lists of peaks.<br>
 * usage:<br><pre>
		List<IMSPeak> leftPeakList = ...;
		List<IMSPeak> topPeakList = ...;
		FullRTW rtw = new FullRTW(leftPeakList, topPeakList);
		PeakAlignment aln = rtw.getPeakAlignment("top", "left", true); 
		// aln contains pairwise peak alignment now found by using outer top path
 * </pre>
 * @author J.Kuharev  
 */
public class FullRTW extends RTW
{
	protected boolean scoreMatrixCalculated = false;

	/**
	 * create QRTW from peak lists<br>
	 * 
	 * @param leftPeaks
	 * @param topPeaks
	 */
	public FullRTW(List<IMSPeak> leftPeaks, List<IMSPeak> topPeaks)
	{
		super(leftPeaks, topPeaks);
	}

	public synchronized void run()
	{
		scoreMatrixCalculated = false;
		if (DEBUG)
			System.out.println(
					"--------------------------------------------------------------------------------\n" +
							"\taligning " + (top.size()) + " x " + (left.size()) + " peaks by FullRTW algorithm:");
		Bencher t = new Bencher().start();
		initMatrix();
		makeScores();
		if (DEBUG)
			System.out.println(
					"\tduration: " + t.stop().getSec() + "s\n" +
							"--------------------------------------------------------------------------------"
					);
		scoreMatrixCalculated = true;
	}

	public PairwiseAlignment getPeakAlignment(Object topRowID, Object leftRowID)
	{
		if (!scoreMatrixCalculated) run();
		if (DEBUG) System.out.print("\tfinding alignment by outer " + ((useLeftPath) ? "left" : "top ") + " path ... ");
		/*	topRow for result */
		List<IMSPeak> topResult = new ArrayList<IMSPeak>();
		/* leftRow for result */
		List<IMSPeak> leftResult = new ArrayList<IMSPeak>();
		// start with last known cell
		int x = nCols;
		int y = nRows;
		int matchCount = 0;
		DTWMatrixCell m = null;
		while (x > 0 || y > 0)
		{
			m = M.getCell(x, y);
			if (m.fromLeft && (useLeftPath || !m.fromTop))
			{
				// gap in left sequence
				topResult.add(top.get(x - 1));
				leftResult.add(null);
				x--;
			}
			else if (m.fromTop)
			{
				// gap in top sequence
				topResult.add(null);
				leftResult.add(left.get(y - 1));
				y--;
			}
			else if (m.fromDiagonal)
			{
				// diagonal way
				topResult.add(top.get(x - 1));
				leftResult.add(left.get(y - 1));
				x--;
				y--;
				matchCount++;
			}
			else
			{
				// should never happen
				System.err.println("broken path in dynamic programming matrix at x=" + x + " y=" + y);
				x -= (x > 0) ? 1 : 0;
				y -= (y > 0) ? 1 : 0;
			}
		}
		Collections.reverse(topResult);
		Collections.reverse(leftResult);
		if (DEBUG) System.out.println("[" + matchCount + " matches]");
		return new PairwiseAlignment(topRowID, topResult, leftRowID, leftResult);
	}

	private void makeScores()
	{
		if (DEBUG) System.out.println("\tcalculating scores ...");
		IMSPeak leftPeak, topPeak;
		int diagonalScore = 0, verticalScore = 0, horizontalScore = 0;
		for (int row = 0; row < nRows; row++)
		{
			leftPeak = left.get(row);
			// for each cell in row
			for (int col = 0; col < nCols; col++)
			{
				DTWMatrixCell thisCell = new DTWMatrixCell();
				topPeak = top.get(col);
				diagonalScore = M.getCell(col, row).score + ((matcher.match(topPeak, leftPeak)) ? matcher.SCORE_MATCH : matcher.SCORE_MISMATCH);
				verticalScore = M.getCell(col + 1, row).score + matcher.SCORE_GAP;
				horizontalScore = M.getCell(col, row + 1).score + matcher.SCORE_GAP;
				// thisCell.score = HMath.max(diagonalScore, horizontalScore,
// verticalScore);
				thisCell.score = matcher.min(diagonalScore, horizontalScore, verticalScore);
				// set precursor flags
				thisCell.fromDiagonal = thisCell.score == diagonalScore;
				thisCell.fromLeft = thisCell.score == horizontalScore;
				thisCell.fromTop = thisCell.score == verticalScore;
				M.setCell(thisCell, col + 1, row + 1);
			}
		}
	}

	private void initMatrix()
	{
		M = new ArrayMatrix<DTWMatrixCell>(nCols + 1, nRows + 1);
		if (DEBUG) System.out.println("\tcreating score matrix ...");
		// first cell init
		DTWMatrixCell cell = new DTWMatrixCell();
		cell.score = matcher.SCORE_INIT;
		M.setCell(cell, 0, 0);
		// first row init
		for (int col = 1; col <= nCols; col++)
		{
			DTWMatrixCell c = new DTWMatrixCell();
			c.score = M.getCell(col - 1, 0).score + matcher.SCORE_GAP;
			c.fromLeft = true;
			M.setCell(c, col, 0);
		}
		// first col init
		for (int row = 1; row <= nRows; row++)
		{
			DTWMatrixCell c = new DTWMatrixCell();
			c.score = M.getCell(0, row - 1).score + matcher.SCORE_GAP;
			c.fromTop = true;
			M.setCell(c, 0, row);
		}
	}
}

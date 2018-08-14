package de.mz.jk.ms.align.method.dtw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.mz.jk.jsix.math.matrix.BandMatrix;
import de.mz.jk.jsix.utilities.Bencher;
import de.mz.jk.ms.align.com.IMSPeak;
import de.mz.jk.ms.align.com.PairwiseAlignment;

/**
 * QuickRTW = Quick Retention Time Warping<br>
 * RT Alignment (or Dynamic Time Warping) between 2 lists of peaks.<br>
 * usage:<br><pre>
		List<IMSPeak> leftPeakList = ...;
		List<IMSPeak> topPeakList = ...;
		RTW rtw = new QuickRTW(leftPeakList, topPeakList);
		PeakAlignment predefinedPathAlignments = rtw.getPeakAlignment("top", "left", true); 
		// predefinedPathAlignments contains pairwise peak alignment now found by using outer top path
 * </pre>
 * @author J.Kuharev  
 */
public class QuickRTW extends BandedRTW
{
	protected int EXTREME_VALUE = 1000000;
	private boolean scoreMatrixCalculated = false;

	/**
	 * create QRTW from peak lists<br>
	 * 
	 * @param leftPeaks
	 * @param topPeaks
	 */
	public QuickRTW(List<IMSPeak> leftPeaks, List<IMSPeak> topPeaks)
	{
		super(leftPeaks, topPeaks);
		EXTREME_VALUE = nCols + nRows + 100;
		// extend top peaks by gap due to range end
		// top.add( new IMSPeak(0, 99999, Double.MAX_VALUE/4) );
	}

	/**
	 * make sure you have added predefined paths before run
	 */
	public synchronized void run()
	{
		scoreMatrixCalculated = false;
		if (DEBUG)
			System.out.println(
					"--------------------------------------------------------------------------------\n" +
							"\taligning " + nCols + " x " + nRows + " peaks by FastRTW algorithm:");
		Bencher t = new Bencher().start();
		initColRanges();
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
		// start with left neighbor of last known cell
		int x = colRanges.get(colRanges.size() - 1)[1];
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
			int[] range = colRanges.get(row);
			// for each col in colRange of this row
			for (int col = range[0]; col <= range[1]; col++)
			{
				DTWMatrixCell thisCell = new DTWMatrixCell();// M.getCell(col+1,
// row+1);
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
		M = new BandMatrix<DTWMatrixCell>(nCols + 1, nRows + 1);
		DTWMatrixCell zeroCell = new DTWMatrixCell();
		zeroCell.score = EXTREME_VALUE;
		M.setDefaultNullCell(zeroCell);
		if (DEBUG) System.out.println("\tcreating score matrix ...");
		// init top left cell
		M.setCell(newCell(matcher.SCORE_INIT, false, false, false), 0, 0);
		// first row width
		int w = colRanges.get(0)[1];
		// first row init
		int score = 0;
		for (int col = 1; col <= w; col++)
		{
			score = M.getCell(col - 1, 0).score + matcher.SCORE_GAP;
			M.setCell(newCell(score, true, false, false), col, 0);
		}
		// first col init
		for (int row = 1; row <= nRows; row++)
		{
			if (colRanges.get(row - 1)[0] == 0)
			{
				score = M.getCell(0, row - 1).score + matcher.SCORE_GAP;
				M.setCell(newCell(score, false, false, true), 0, row);
			}
			else break;
		}
	}

	private DTWMatrixCell newCell(int score, boolean fromLeft, boolean fromDiagonal, boolean fromTop)
	{
		DTWMatrixCell cell = new DTWMatrixCell();
		cell.score = score;
		cell.fromLeft = fromLeft;
		cell.fromDiagonal = fromDiagonal;
		cell.fromTop = fromTop;
		return cell;
	}

}

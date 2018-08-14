package de.mz.jk.ms.align.method.dtw.linear;

import java.util.ArrayList;
import java.util.List;

import de.mz.jk.jsix.libs.XJava;
import de.mz.jk.jsix.lists.XSubList;
import de.mz.jk.ms.align.com.LinkedAlignment;

/**
 * <h3>{@link LCSFastHirschberg}</h3>
 * @author Joerg Kuharev
 * @version 05.05.2011 14:25:56
 */
public class LCSFastHirschberg
{
	private static final boolean DEBUG = true;
	
	public final int SCORE_MATCH = -1;
	public final int SCORE_MISMATCH = 3;
	public final int SCORE_GAP = 1;
	
	public int SCORE_BAD = SCORE_GAP * 1000;
	public List<int[]> colRanges = new ArrayList<int[]>();
	
	public static void main(String[] args)
	{
//		testAlignment("bier, bier, bier, bett, bett, bett, tier, brett", "bier+++++++++++++++bett", !true);
		testAlignment("Schlauchboot", "Schnittlauchbrot", true);
//		testAlignment("xla", "alx", !true);
//		testAlignment("ARTISCHOCKEN", "ALTESOCKEN", true);
//		testAlignment("ALTE", "RATTE", true);
//		testAlignment("ax", "xa", true);
//		testAlignment("Schokoriegel", "ein Schokoriegel? Schon schön so ein Riegel", true);
	}
	
	public static void testAlignment(String X, String Y, boolean useLeftPath)
	{
		List<int[]> ranges = makeLinearRanges(X, Y, 10);
		
		System.out.println("aligning words:\nX:\t"+X+"\nY:\t"+Y);
		System.out.println("\nusing path:");
		printRanges(X, Y, ranges);
		System.out.println();
		
		LCSFastHirschberg h = new LCSFastHirschberg();
		h.colRanges = ranges;
		
		printAlignment(
			h.align(
				new XSubList<String>(XJava.getStringList(X)), 
				new XSubList<String>(XJava.getStringList(Y)),
				useLeftPath
			)
		);
	}
	
	/**
	 * @param x
	 * @param y
	 * @param ranges
	 */
	private static void printRanges(String X, String Y, List<int[]> ranges)
	{
		System.out.println(" ");
		for(char _x : X.toCharArray()) System.out.print(" "+_x);
		System.out.println();
		for(int y=0; y<Y.length(); y++)
		{
			System.out.print(Y.charAt(y));
			int[] r = ranges.get(y);
			for(int x=0; x<X.length(); x++)
			{
				System.out.print(" " + ((x<r[0] || x>r[1]) ? " " : "x" ));
			}
			System.out.println();
		}
	}

	/**
	 * @param x
	 * @param y
	 * @param i
	 * @return 
	 */
	private static List<int[]> makeLinearRanges(String top, String left, int radius)
	{
		int n = top.length();
		int m = left.length();
		double nm = n*1.0/m;
		
		List<int[]> res = new ArrayList<int[]>();
		for(int y=0; y<m; y++)
		{
			int x = (int) (y * nm);
			int _r = x - radius;
			int r_ = x + radius;
			if(_r<0) _r=0;
			if(r_>=n) r_=n-1;
			res.add(new int[]{_r, r_});
		}
		
		res.get(m-1)[1] = n-1;
		
		return res;
	}
	
	private static void printAlignment(LinkedAlignment<String> aln)
	{
		LinkedAlignment<String> a = aln.getHeadElement();
		String x = "";
		String y = "";
		while(a!=null)
		{
			x += (a.getX()!=null) ? a.getX() : "-";
			y += (a.getY()!=null) ? a.getY() : "-";
			a = a.getSuccessor();		
		}
		System.out.println("'"+x+"'");
		System.out.println("'"+y+"'");
	}

	public LinkedAlignment<String> align( XSubList<String> X, XSubList<String> Y, boolean useLeftPath )
	{
		int n = (X==null) ? 0 : X.size();
		int m = (Y==null) ? 0 : Y.size();

		// break condition
		if(n<=1 || m<=1)
		{
			// find alignment using needleman wunsch (trivial case)
			return getTrivialAlignment(X, Y, useLeftPath);
		}
		
		// divide precursor problem into two subproblems
		
		// define y-split position 
		int midY = (m+1) / 2;
		
		// split Y into two substrings
		XSubList<String> prefixY = Y.subList(0, midY);
		XSubList<String> suffixY = Y.subList( midY );
 		
 		if(DEBUG) System.out.println("(sub)problem's hirschberg alignment:");
		// get score vectors
 		int[] upperScoreVector = getForwardScoreVector(X, prefixY);
		System.out.println("----------------------------------------------------------------------------------------------------");
		int[] lowerScoreVector = getBackwardScoreVector(X, suffixY);
		
		// sum score vectors
		int[] sumScoreVector = sum(upperScoreVector, lowerScoreVector);
		
		// find minimum score's index
		int splitX = indexesOfMin( sumScoreVector )[useLeftPath ? 0 : 1];
		
		System.out.print("sum:\t");
		for(int i=0; i<sumScoreVector.length; i++)
		{
			if( i==splitX )
				System.out.print( "\t[" + sumScoreVector[i] + "]\t" );
			else
				System.out.print( "\t" + sumScoreVector[i] + "\t" );
		}
		System.out.println();
		
		// split X into two substrings
		XSubList<String> prefixX =  X.subList(0, splitX); // exclusive split position
		XSubList<String> suffixX =  X.subList( splitX );
		
		if(DEBUG) System.out.println("splitting \t'"+str(X)+"' into '" + str(prefixX) + "', '" + str(suffixX) + "'");
		if(DEBUG) System.out.println("splitting \t'"+str(Y)+"' into '" + str(prefixY) + "', '" + str(suffixY) + "'\n");
		
		// jump into recursion for solving subproblems
		LinkedAlignment<String> head = align(prefixX, prefixY, useLeftPath);
		LinkedAlignment<String> tail = align(suffixX, suffixY, useLeftPath);
		
		// concatenate alignments
		LinkedAlignment<String> res = (head==null) ? tail : ( (tail==null) ? head : head.append(tail) );
		
		return res;
	}
	
	public String str(XSubList<String> subList)
	{
		return XJava.joinList(subList.asList(), "");
	}
	
	/**
	 * transform absolute column ranges into relative subspace by given X and Y sublists<br>
	 * <pre>
	 * absolute space and known column ranges 
	 *   0 1 2 3 4 5 6 7 8 9
	 * 0 * * * * *
	 * 1   * * * *
	 * 2     * * * * *
	 * 3     * * * * * *
	 * 4       * * * * *
	 * 5         * * * * *
	 * 6         * * * * * *
	 * 7           * * * *
	 * 8           * * * * *
	 * 9               * * *
	 * 
	 * while looking into subspace x[6...9], y[7..9]
	 *     6 7 8 9 absolute indexes
	 *     0 1 2 3 relative indexes
	 * 7=0 * * *
	 * 8=1 * * * *
	 * 9=2   * * * 
	 * 
	 * absolute column ranges are 
	 * 	1: sliced by absolute subY-coordinates 
	 * 	2: tranformed by absolute subX begin coordinate
	 * 	3: cutted to fit into subX   
	 * </pre>
	 * @param X
	 * @param Y
	 * @return
	 */
	public int[][] getRelativeRange(XSubList<?> X, XSubList<?> Y)
	{
		int[][] range = new int[Y.size()][];
		int yShift = Y.getAbsoluteStartIndex();
		int xShift = X.getAbsoluteStartIndex();
		int n = X.size() - 1;
		int[] absRange;
		int _r, r_;
		for(int i=0; i<range.length; i++)
		{
			absRange = colRanges.get(i + yShift);
			// tranformed by absolute subX begin coordinate
			_r = absRange[0] - xShift;
			r_ = absRange[1] - xShift;
			// cut to fit into subX
			if(_r<0) _r=0;
			if(r_>n) r_=n;
			range[i] = new int[]{_r, r_};
		}
		
		// range[0][0] = 0;
		// range[range.length-1][1] = n;
				
		return range;
	}
	
	/**
	 * forward score calulation
	 * @param X sequence with the fixed length 
	 * @param Y sequence with the variable length
	 * @return score vector having length of X
	 */
	private int[] getForwardScoreVector(XSubList<String> X, XSubList<String> Y)
	{
		int n = X.size();
		int m = Y.size();
		int[] T =  new int[n+1];
		int diagonalScore = 0; 
		int horizontalScore = 0;
		int verticalScore = 0;
		int currentScore = 0;
		String _Y = "";
		int[][] relRange = getRelativeRange(X, Y);
		int[] curRange = relRange[0];
		int[] oldRange = curRange;
		
		// initialize score vector
		int _r = curRange[0];
		int r_ = curRange[1]+1;		
		for(int i=0; i<=n; i++)
		{ 
			T[i]= (i>=_r && i<=r_) ? i * SCORE_GAP : SCORE_BAD; 
		}
		
		if(DEBUG)
		{
			System.out.print("\t-\t");
			for(int i=0; i<n; i++) System.out.print("\t"+X.get(i)+"\t");
			System.out.print("\t-\n-\n\t");
			for(int _v : T) System.out.print("\t"+_v+"\t");
			System.out.println();
		}

		for(int y=0; y<m; y++)
		{
			_Y = Y.get(y)+"";
			System.out.print(_Y+"\n\t");
			
			oldRange = curRange;
			curRange = relRange[y];
			
			// set cells before range begin to SCORE_BAD
			for(int x=oldRange[0]; x<curRange[0]; x++)
			{
				T[x] = SCORE_BAD;
			}
			
			// set cells between old range end(exclusive) and new range end(inclusive) to SCORE_BAD
			for(int x=oldRange[1]+1; x<=curRange[1]; x++)
			{ 
				T[x+1] = SCORE_BAD;
			}
			
			diagonalScore = T[curRange[0]];
			T[curRange[0]] = diagonalScore + SCORE_GAP;
						
//			for(int x=1; x<=n; x++)
			for(int x=curRange[0]; x<=curRange[1]; x++)
			{
				verticalScore = T[x+1];
				horizontalScore = T[x];			
				currentScore = getScore( X.get(x)+"", _Y, horizontalScore, verticalScore, diagonalScore );
				diagonalScore = verticalScore;
				T[x+1] = currentScore;
			}
			
//			if(curRange[0]>0) T[0] = SCORE_BAD;
			
			if(DEBUG)
			{
				for(int _v : T) System.out.print("\t"+_v+"\t");
				System.out.println();
			}
		}
		
		return T;
	}
	
	/**
	 * backward score calculation 
	 * @param X sequence with the fixed length 
	 * @param Y sequence with the variable length
	 * @return score vector having length of X
	 */
	private int[] getBackwardScoreVector(XSubList<String> X, XSubList<String> Y)
	{
		String out = "", line="";
		
		int n = X.size();
		int m = Y.size();
		int[] T =  new int[n+1];
		int diagonalScore = 0; 
		int horizontalScore = 0;
		int verticalScore = 0;
		int currentScore = 0;
		String _Y = "";
		int[][] relRange = getRelativeRange(X, Y);
		int[] curRange = relRange[m-1];
		int[] oldRange = curRange;
		
		// initialize score vector
		int _r = curRange[0];
		int r_ = curRange[1]+1;		
		for(int i=0; i<=n; i++)
		{
			T[i]= (i>=_r && i<=r_) ? (n-i) * SCORE_GAP : SCORE_BAD;
		}
		
		if(DEBUG)
		{
			out += "\t";
			for(int _v : T) out += ("\t"+_v+"\t");
			out += ("\n-\n");
		}
		
		for(int y=m-1; y>=0; y--)
		{
			_Y = Y.get(y);
			
			oldRange = curRange;
			curRange = relRange[y];
			
			// set cells before range begin to SCORE_BAD
			/*
			X =   0 1 2 3 4 5 6
			T = 0 1 2 3 4 5 6 7
			c =     1 2 3
			o =         3 4 5
			for x in 1..<3 do T[x+1]=SCORE_BAD
			*/
			for(int x=curRange[0]+1; x<oldRange[0]; x++)
			{
				T[x] = SCORE_BAD;
			}
			
			// set cells between old range end(exclusive) and new range end(inclusive) to SCORE_BAD
			for(int x=curRange[1]+1; x<=oldRange[1]; x++)
			{ 
				T[x+1] = SCORE_BAD;
			}
			
			diagonalScore = T[curRange[1]+1];
			T[curRange[1]+1] = diagonalScore + SCORE_GAP;
			
			for(int x=curRange[1]; x>=curRange[0]; x--)
			{
				verticalScore = T[x];
				horizontalScore = T[x+1];
				currentScore = getScore( X.get(x)+"", _Y, horizontalScore, verticalScore, diagonalScore );
				diagonalScore = verticalScore;
				T[x] = currentScore;
			}

//			diagonalScore = T[n];
//			T[n]= diagonalScore + SCORE_GAP;
//			
//			for(int x=n-1; x>=0; x--)
//			{
//				verticalScore = T[x];
//				horizontalScore = T[x+1];
//				currentScore = getScore( X.get(x)+"", _Y, horizontalScore, verticalScore, diagonalScore );
//				diagonalScore = verticalScore;
//				T[x] = currentScore;
//			}
			
			if(DEBUG)
			{
				line = "\t";
				for(int _v : T) line += ("\t"+_v+"\t");
				line += ("\n"+_Y+"\n");
				out = line + out;
			}
		}
		
		if(DEBUG) System.out.println(out);
		
		return T;
	}
	
	private int getScore( String A, String B, int horizontalScore, int verticalScore, int diagonalScore )
	{
		return 
			min(
				diagonalScore + ( ( match(A, B) ) ? SCORE_MATCH : SCORE_MISMATCH ),
				horizontalScore + SCORE_GAP,
				verticalScore + SCORE_GAP
			);
	}
	
	public static int min(int a, int b, int c){ return (a<b && a<c) ? a : ((b<c) ? b : c); }

	/**
	 * find the index of minimum value in a vector
	 * @param vector
	 * @param left if true the first occurance of minimum value will be found, otherwise the last one
	 * @return first or last index of minimum in given vector
	 */
	private int indexOfMin(int[] vector, boolean left)
	{
		int min = 0;
		for (int i=1; i<vector.length; i++)
		{
			if( vector[i]<vector[min] || ( !left && vector[i]==vector[min] ) ) 
				min = i;
		}
		return min;
	}
	
	private int[] indexesOfMin(int[] vector)
	{
		int firstMin = 0;
		int lastMin = 0;
		for (int i=1; i<vector.length; i++)
		{
			if( vector[i]<vector[firstMin] )
			{
				firstMin = lastMin = i;
			}
			else 
			if( vector[i]==vector[firstMin] )
			{		
				lastMin = i;
			}
		}
		return new int[]{firstMin, lastMin};
	}

	private boolean match(String a, String b)
	{
		return a!=null && b!=null && a.equalsIgnoreCase(b); 
	}
	
	/**
	 * @param a
	 * @param b
	 * @return
	 */
	private int[] sum(int[] a, int[] b)
	{
		int n = a.length;
		int[] sum = new int[n];
		for(int i=0; i<n; i++)
		{
			sum[i] = a[i] + b[i];
		}
		return sum;
	}
	
	/**
	 * trivial alignment of two sequences.<br>
	 * one of the sequences has size of 0 or 1<br>
	 * thus the result will contain maximum one match,
	 * all other positions are aligned with gaps
	 * @param X first sequence 
	 * @param Y second sequence
	 * @param useLeftPath should Y be shifted maximally to the left in case of multiple matching possibilities.<pre>
	 * if true the first occurance in X and last occurance in Y will match, e.g.
	 * X: ---x		X: xaax
	 * Y: xaax	or	Y: x---
	 * otherwise the last occurance in X and first occurance in Y will match, e.g.
	 * X: x---		X: xaax
	 * Y: xaax	or	Y: ---x</pre>
	 * @return the alignment
	 */
	private LinkedAlignment<String> getTrivialAlignment(XSubList<String> X, XSubList<String> Y, boolean useLeftPath)
	{
		LinkedAlignment<String> res = null;
		
		int Xn = (X==null) ? 0 : X.size();
		int Yn = (Y==null) ? 0 : Y.size();
		
		LinkedAlignment<String> aln = null;
		
		if(Xn==0) 
		{ // all Y aligned with gaps
			for(int i=0; i<Yn; i++)
			{
				aln = new LinkedAlignment<String>( null, Y.get(i)+"" );
				res = (res == null) ? aln : res.append( aln );
			}
		}
		else if(Xn==1)
		{ // all Y aligned with gaps having maximum one XY-match
			String x = X.get(0);
			for(int i=0; i<Yn; i++)
			{
				int yi = useLeftPath ? Yn - i - 1 : i;
				String y = Y.get( yi )+"";
				if( match(x, y) ) 
				{
					aln = new LinkedAlignment<String>( x, y );
					x = null;
				}
				else
				{
					aln = new LinkedAlignment<String>(null, y);
				}
				res = (res == null) ? aln : useLeftPath ? aln.append(res) : res.append( aln );
			}
			// no XY-match found then add X+gap
			if( x!=null ) res.append( new LinkedAlignment<String>(x, null) );
		}
		else if(Yn==0)
		{ // all X aligned with gaps
			for(int i=0; i<Xn; i++)
			{
				aln = new LinkedAlignment<String>( X.get(i)+"", null );
				res = (res == null) ? aln : res.append( aln );
			}
		}
		else
		{ // all X aligned with gaps having maximum one XY-match
			String y = Y.get(0);
			for(int i=0; i<X.size(); i++)
			{
				int xi = useLeftPath ? i : Xn - i - 1; 
				String x = X.get(xi)+"";
				if( match(x, y) ) 
				{
					aln = new LinkedAlignment<String>( x, y );
					y = null;
				}
				else
				{
					aln = new LinkedAlignment<String>(x, null);
				}
				res = (res == null) ? aln : useLeftPath ? res.append( aln ) : aln.append(res);
			}
			if( y!=null ) res.append( new LinkedAlignment<String>(null, y) );
		}

		if(DEBUG)
		{
			System.out.println("trivial alignment:");
			printAlignment(res);
			System.out.println();
		}
		
		return res;
	}
}
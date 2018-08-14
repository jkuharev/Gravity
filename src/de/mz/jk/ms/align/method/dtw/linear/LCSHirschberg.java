package de.mz.jk.ms.align.method.dtw.linear;

import de.mz.jk.jsix.libs.XJava;
import de.mz.jk.jsix.lists.XSubList;
import de.mz.jk.ms.align.com.LinkedAlignment;

/**
 * <h3>{@link LCSHirschberg}</h3>
 * @author Joerg Kuharev
 * @version 05.05.2011 14:25:56
 */
public class LCSHirschberg
{
	private static final boolean DEBUG = true;
	
	public final int SCORE_MATCH = -1;
	public final int SCORE_MISMATCH = 3;
	public final int SCORE_GAP = 1;
	
	public static void main(String[] args)
	{
//		testAlignment("bier, bier, bier, bett, bett, bett, tier, brett", "bier bett", !true);
		testAlignment("Schlauchboot", "Schnittlauchbrot", true);
//		testAlignment("xla", "alx", true);
//		testAlignment("ALTE", "RATTE", true);
//		testAlignment("ax", "xa", !true);
	}
	
	public static void testAlignment(String X, String Y, boolean useLeftPath)
	{
		LCSHirschberg h = new LCSHirschberg();
		printAlignment( 
			h.align(
				new XSubList<String>(XJava.getStringList(X)), 
				new XSubList<String>(XJava.getStringList(Y)), 
				useLeftPath
			)
		);
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
		System.out.println();
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
		// init row
		for(int i=0; i<=n; i++){ T[i]= i * SCORE_GAP; }
		
		if(DEBUG)
		{
			System.out.print("\t-\t");
			for(int i=0; i<n; i++) System.out.print("\t"+X.get(i)+"\t");
			System.out.print("\t-\n-\n\t");
			for(int _v : T) System.out.print("\t"+_v+"\t");
			System.out.println();
		}
		
		int diagonalScore = 0; 
		int horizontalScore = 0;
		int verticalScore = 0;
		int currentScore = 0;
		
		String _Y = "";
		
		for(int y=0; y<m; y++)
		{
			_Y = Y.get(y)+"";
			
			System.out.print(_Y+"\n\t");
			
			diagonalScore = T[0];
			T[0] = diagonalScore + SCORE_GAP;
			
			for(int x=1; x<=n; x++)
			{
				verticalScore = T[x];
				horizontalScore = T[x-1];			
				currentScore = getScore( X.get(x-1)+"", _Y, horizontalScore, verticalScore, diagonalScore );
				diagonalScore = verticalScore;
				T[x] = currentScore;
			}
			
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
		
		int[] T = new int[n+1];
		// init row
		for(int i=0; i<=n; i++){ T[n-i]= i*SCORE_GAP; }
		
		if(DEBUG)
		{
			out += "\t";
			for(int _v : T) out += ("\t"+_v+"\t");
			out += ("\n-\n");
		}
		
		int diagonalScore = 0;
		int horizontalScore = 0;
		int verticalScore = 0;
		int currentScore = 0;
		
		String _Y = "";
		
		for(int y=m-1; y>=0; y--)
		{
			_Y = Y.get(y) + "";
			
			diagonalScore = T[n];
			T[n]= diagonalScore + SCORE_GAP;
			
			for(int x=n-1; x>=0; x--)
			{
				verticalScore = T[x];
				horizontalScore = T[x+1];
				currentScore = getScore( X.get(x)+"", _Y, horizontalScore, verticalScore, diagonalScore );
				diagonalScore = verticalScore;
				T[x] = currentScore;
			}
			
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
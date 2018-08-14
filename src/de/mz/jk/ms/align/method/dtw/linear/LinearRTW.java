/** ISOQuant, isoquant.plugins.processing.expression.align.rtw.hirschberg, 21.04.2011 */
/**
// 01 --
// 02 -- Der Divide & Conquer-Algorithmus von Hirschberg zur
// 03 -- Berechnung des globalen Alignments auf linearem Speicher.
// 04 --
// 05 -- Bei m =  | x | ,n =  | y | ,n < m besitzt der Algorithmus eine Laufzeit von Θ(nm)
// 06 -- und einen Speicherverbrauch von Θ(min{n,m}).
// 07 --
// 08 function HirschbergAlignment(x,y : string) return A is
// 09        function SubAlignment(i1,j1,i2,j2 : integer) return A is
// 10                mitte,cut : integer
// 11                s,c : real
// 12                T^\ell,T^r : array(j1..j2) of real
// 13        begin
// 14                if i1 + 1 = i2 or j1 = j2 then
// 15                        -- Konstruiere Matrix T für die Teil-Strings
// 16                        -- x(i1 + 1..i2) und y(j1 + 1..j2)
// 17                        -- Achtung: Nur linearer Speicherplatz erforderlich!
// 18                        T := ...
// 19                        -- Berechne triviales Alignment auf Matrix T
// 20                        -- in linearer Laufzeit
// 21                        return Alignment(T,x(i1 + 1..i2),y(j1 + 1..j2))
// 22                end if
// 23
// 24                mitte := (i1 + i2) / 2
// 25                -- finde ausgehend von (i1,j1) den minimalen Pfad
// 26                -- mit dem Vorwärtsalgorithmus:
// 27                T^\ell(j_1) := 0
// 28                for j in j1 + 1..j2 loop
// 29                        T^\ell(j) := T^\ell(j-1) + Ins(y_j)
// 30                end loop
// 31                for i in i1 + 1..mitte loop
// 32                        s := T^\ell(j_1)
// 33                        c := T^\ell(j_1) + Del(x_i)
// 34                        T^\ell(j_1) := c
// 35                        for j in j1 + 1..j2 loop
// 36                                c := \min\begin{cases}T^\ell(j)&+Del(x_i)\\s&+Sub(x_i,y_j)\\c&+Ins(y_j)\end{cases}
// 37                                s := T^\ell(j)
// 38                                T^\ell(j) := c
// 39                        end loop
// 40                end loop
// 41                -- finde minimalen score-pfad nach (i2,j2)
// 42                Tr(j2) := 0
// 43                for j in j2 − 1..j1 loop
// 44                        Tr(j) := Tr(j + 1) + Ins(yj + 1)
// 45                end loop
// 46                for i in i2 − 1..mitte loop
// 47                        s := Tr(j2)
// 48                        c := Tr(j2) + Del(xi + 1)
// 49                        Tr(j2) := c;
// 50                        for j in j2 − 1..j1 loop
// 51                                c := \min\begin{cases}T^r(j)&+Del(x_{i+1})\\s&+Sub(x_{i+1},y_{j+1})\\c&+Ins(y_{j+1})\end{cases}
// 52                                s := Tr(j)
// 53                                Tr(j) := c
// 54                        end loop
// 55                end loop
// 56                -- finde den Punkt aus j1..j2 in dem der Minimale Pfad die
// 57                -- mittlere Zeile schneidet:
// 58                -- cut :=_{def} \mbox{argmin}_{j_1\leq j\leq j_2}(T^\ell(j)+T^r(j))
// 59                for j in j1..j2 loop
// 60                        if j=j1 then
// 61                                cut := j1
// 62                        elsif T^\ell(j)+T^r(j)<T^\ell(cut)+T^r(cut) then
// 63                                cut := j
// 64                        end if
// 65                end loop
// 66                -- Alignment entsteht durch Konkatenation von linkem und
// 67                -- rechtem Teil-Alignment:
// 68                return SubAlignment(i1,j1,mitte,cut)
// 69                                \star SubAlignment(mitte,cut,i2,j2)
// 70        end SubAlignment
// 71        m,n : integer
// 72 begin
// 73        m :=  | x | ; n :=  | y | 
// 74        -- Sonderbehandlung: x ist der leere String und lässt keine Zerteilung zu:
// 75        if m=0 then
// 76                return \begin{pmatrix}-\\ y_1\end{pmatrix}\star\begin{pmatrix}-\\ y_2\end{pmatrix}\star\cdots\star\begin{pmatrix}-\\y_n\end{pmatrix}
// 77        else
// 78                return SubAlignment(0,0,m,n)
// 79        end if
// 80 end HirschbergAlignment
 */
package de.mz.jk.ms.align.method.dtw.linear;

import java.util.ArrayList;
import java.util.List;

import de.mz.jk.jsix.lists.XSubList;
import de.mz.jk.ms.align.com.IMSPeak;
import de.mz.jk.ms.align.com.LinkedAlignment;
import de.mz.jk.ms.align.com.PairwiseAlignment;
import de.mz.jk.ms.align.method.dtw.RTW;
import de.mz.jk.ms.align.method.dtw.TrivialRTW;

/**
 * <h3>{@link LinearRTW}</h3>
 * Retention time alignment by using adapted Hirschberg's algorithm<br>
 * see <a href="http://en.wikipedia.org/wiki/Hirschberg%27s_algorithm">Wikipedia: Hirschberg's algorithm</a>
 * 
 * @author kuharev
 * @version 21.04.2011 13:57:39
 */
public class LinearRTW extends RTW
{
	private TrivialRTW<IMSPeak> trivialAligner = null;
	private LevenshteinDistanceCalculator<IMSPeak> levenshtein = null;
	
	/**
	 * construct Retention Time Warping using Hirschberg's Divide and Conquer algorithm
	 * @param leftPeaks
	 * @param topPeaks
	*/
	public LinearRTW(List<IMSPeak> leftPeaks, List<IMSPeak> topPeaks)
	{
		super(leftPeaks, topPeaks);
	}

	@Override public void run()
	{
		trivialAligner = new TrivialRTW<IMSPeak>(matcher);
		levenshtein = new SimpleLevenshteinDistanceCalculator<IMSPeak>(matcher);
		aln = align(top, left);
	}

	/**
	 * ATTENTION: Hirschberg results in only one alignment path
	 * thus preferTopPath is ignored in this implementation
	 */
	@Override public PairwiseAlignment getPeakAlignment(Object topRowID, Object leftRowID)
	{
		if(aln==null) return null;
		
		LinkedAlignment<IMSPeak> e = aln.getHeadElement();
		int n = aln.getSize();
		
		List<IMSPeak> topList = new ArrayList<IMSPeak>(n);
		List<IMSPeak> leftList = new ArrayList<IMSPeak>(n);
		
		while( e!=null )
		{
			topList.add( e.getX() );
			leftList.add( e.getY() );
			e = e.getSuccessor();
		}

		return new PairwiseAlignment(topRowID, topList, leftRowID, leftList);
	}

	/**
	 * align sequences using hirschberg's divide and conquer algorithm
	 * @param topSeq
	 * @param leftSeq
	 * @param useLeftPath
	 * @return
	 */
    public LinkedAlignment<IMSPeak> align( XSubList<IMSPeak> topSeq, XSubList<IMSPeak> leftSeq )
    {
        int n = (topSeq==null) ? 0 : topSeq.size();
        int m = (leftSeq==null) ? 0 : leftSeq.size();

        // on break condition find alignment using needleman wunsch (trivialAligner case)
        if(n<=1 || m<=1) return trivialAligner.getAlignment(topSeq, leftSeq, useLeftPath);
        
        // divide precursor problem into two subproblems
        
        // define y-split position 
        int midY = (m+1) / 2;
        
        // split left sequence into two subsequences
        XSubList<IMSPeak> prefixY = leftSeq.subList(0, midY);
        XSubList<IMSPeak> suffixY = leftSeq.subList( midY );
        
        // get score vectors
        int[] upperScoreVector = levenshtein.getForwardDistanceVector(topSeq, prefixY);
        int[] lowerScoreVector = levenshtein.getBackwardDistanceVector(topSeq, suffixY);
        
        // sum score vectors
        int[] sumScoreVector = levenshtein.sum(upperScoreVector, lowerScoreVector);
        
        // find minimum score's index
        int splitX = levenshtein.indexesOfMin( sumScoreVector )[useLeftPath ? 0 : 1];
        
        // prevent unneeded stack growing
        upperScoreVector = null;
        lowerScoreVector = null;
        sumScoreVector = null;

        // split X into two subPeaks
        XSubList<IMSPeak> prefixX =  topSeq.subList(0, splitX); // exclusive split position
        XSubList<IMSPeak> suffixX =  topSeq.subList( splitX );
        
        // jump into deeper recursion for solving subproblems
        LinkedAlignment<IMSPeak> head = align(prefixX, prefixY);
        LinkedAlignment<IMSPeak> tail = align(suffixX, suffixY);
        
        // concatenate alignments
        LinkedAlignment<IMSPeak> res = (head==null) ? tail : ( (tail==null) ? head : head.append(tail) );
        
        return res;
    }
}

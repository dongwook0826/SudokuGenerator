package sudoku;

import java.util.Random;

public class DiffStatMain {
	public static void main(String[] args){
		int diffCut = 20;
		int[] diffStatCnt = new int[diffCut+1];
		
		int searchCnt = 500;
		int iterRate = 1000;
		
		Random rand = new Random();
		System.out.print("base data generating");
		
		for(int sr = 1; sr<=searchCnt; sr++){
			if(sr%(searchCnt/10)==0) System.out.print(".");
			SudokuGenerator sdkGen = new SudokuGenerator(rand.nextLong());
			int diff = sdkGen.generatePuzzleGridDiff(1, 9, rand.nextLong(), iterRate);
			if(diff/100 >= diffCut){
				diffStatCnt[diffStatCnt.length-1]++;
			}else{
				diffStatCnt[diff/100]++;
			}
		}System.out.println();
		
		for(int d=0; d<diffStatCnt.length; d++){
			System.out.printf("diff %2d : %3d  |", d, diffStatCnt[d]);
			for(int k=0; k<diffStatCnt[d]; k+=5){
				System.out.print("*");
			}System.out.println();
		}
	}
}

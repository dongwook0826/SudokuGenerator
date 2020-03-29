package sudoku;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;

public class EulerMain {
	public static void main(String[] args) throws IOException, FileNotFoundException{
		
		BufferedReader reader = new BufferedReader(new FileReader("./p096_sudoku.txt"));
		
		int[][] sudokuArr;
		// int[][] solution;
		String[] rawSudRow = new String[9];
		String rawLine;
		// int rightTopSum = 0;
		
		while(true){
			rawLine = reader.readLine();
			if(rawLine == null) break;
			sudokuArr = new int[9][9];
			for(int i=0; i<9; i++){
				rawLine = reader.readLine(); // (j+1)'th row in String
				rawSudRow = rawLine.split("");
				for(int j=0; j<9; j++){
					sudokuArr[i][j] = Integer.parseInt(rawSudRow[j]);
				}
			}// sudoku initialized
			
			Sudoku sudoku = new Sudoku(sudokuArr);
			sudoku.printSolution(true);
			
		}
		reader.close();
		// System.out.println("Ans = "+rightTopSum);
	}
}

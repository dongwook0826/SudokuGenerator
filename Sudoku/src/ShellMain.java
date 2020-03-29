package sudoku;

import java.util.Scanner;

public class ShellMain {
	public static void main(String[] args){
		System.out.println("Sudoku Solver in Java -- by TenDong");
		System.out.println("(version 0.2)");
		String rawSudokuLine;
		int[][] sudokuBoard;
		String willContinue;
		
		
		Scanner sc = new Scanner(System.in);
		while(true){
			System.out.println("( Each letters can be numbers(1-9) and blanks(any other letters) )");
			System.out.println("Please input Sudoku puzzle in 81-letter string : ");
			
			while(true){
				rawSudokuLine = sc.nextLine();
				if(rawSudokuLine.length()!=81){
					System.out.println("invalid puzzle length; input again");
					continue;
				}else break;
			}
			sudokuBoard = new int[9][9];
			for(int r=0; r<sudokuBoard.length; r++){
				for(int c=0; c<sudokuBoard[r].length; c++){
					sudokuBoard[r][c] = (int)rawSudokuLine.charAt(r*9+c) - (int)'0';
					if(sudokuBoard[r][c]<1 || sudokuBoard[r][c]>9){
						sudokuBoard[r][c] = 0;
					}
				}
			}// sudoku board initialized
			
			Sudoku sudoku = new Sudoku(sudokuBoard);
			sudoku.printSolution(true);
			
			System.out.println("Have another puzzle? (Y/N)");
			willContinue = sc.nextLine();
			if(willContinue.length() == 0){
				break;
			}else if(willContinue.charAt(0) != 'y' || willContinue.charAt(0) != 'Y'){
				continue;
			}else break;
		}
		sc.close();
		System.out.println("Thanks for playing!");
		System.out.println("(upcoming with sudoku generator...)");
	}
}

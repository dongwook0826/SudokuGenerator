package sudoku;

public class GenTestMain {
	public static void main(String[] args){
		long seed = System.currentTimeMillis();
		System.out.println("seed : "+seed);
		SudokuGenerator sdkgen = new SudokuGenerator(seed);
		int[][] baseGrid = sdkgen.baseGrid();
		for(int i=0; i<baseGrid.length; i++){
			for(int j=0; j<baseGrid[i].length; j++){
				System.out.print(baseGrid[i][j]);
			}System.out.println();
		}System.out.println();
		
		int maximaSearchCnt = 100;
		
		int difficulty = 0;
		int tempDiff;
		Sudoku puzzle = new Sudoku(baseGrid);
		int[][] puzzleGrid = new int[baseGrid.length][baseGrid[0].length];
		
		System.out.print("Generating puzzle");
		
		for(int search = 1; search<=maximaSearchCnt; search++){
			if(search%(maximaSearchCnt/10)==0){
				System.out.print(".");
			}
			tempDiff = sdkgen.generatePuzzleGridDiff(1, 9, System.currentTimeMillis(), 1000);
			if(tempDiff <= difficulty) continue;
			puzzleGrid = sdkgen.puzzleGrid();
			difficulty = tempDiff;
		}System.out.println();
		puzzle = new Sudoku(puzzleGrid);
		puzzle.printSudoku();
		System.out.printf("difficulty estimation : %d\n", difficulty);
		
	}
}

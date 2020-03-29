package sudoku;

public class SudokuGenerator extends Sudoku {
	// not yet implemented...
	
	protected static final int[][] CANONICAL_GRID = {
		{0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,3,0,8,5},
		{0,0,1,0,2,0,0,0,0},
		{0,0,0,5,0,7,0,0,0},
		{0,0,4,0,0,0,1,0,0},
		{0,9,0,0,0,0,0,0,0},
		{5,0,0,0,0,0,0,7,3},
		{0,0,2,0,1,0,0,0,0},
		{0,0,0,0,4,0,0,0,9}
	};
	
	public SudokuGenerator(){
		super(CANONICAL_GRID);
	}
}
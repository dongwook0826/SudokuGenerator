package sudoku;

import java.util.Random;

public class SudokuGenerator {
	
	// private Sudoku puzzle;
	private int[][] baseGrid;
	private int[][] puzzleGrid;
	private int[] puzzleInfo;
	// private boolean validGenerated = false;
	static final int[][] PERMUTE_3 = {
		{0,1,2}, {0,2,1}, {1,0,2}, {1,2,0}, {2,0,1}, {2,1,0}
	};
	static final int[][] UPP_TRI_IND = {
		{0,0},
		{1,0},{0,1},
		{2,0},{1,1},{0,2},
		{3,0},{2,1},{1,2},{0,3},
		{4,0},{3,1},{2,2},{1,3},{0,4},
		{5,0},{4,1},{3,2},{2,3},{1,4},{0,5},
		{6,0},{5,1},{4,2},{3,3},{2,4},{1,5},{0,6},
		{7,0},{6,1},{5,2},{4,3},{3,4},{2,5},{1,6},{0,7},
		{8,0},{7,1},{6,2},{5,3},{4,4},{3,5},{2,6},{1,7},{0,8}
	};
	static final int[][] RIGHT_QUAD_IND = {
		{0,0},
		{1,0},{1,1},
		{2,0},{2,1},{2,2},
		{3,0},{3,1},{3,2},{3,3},
		{4,0},{4,1},{4,2},{4,3},{4,4},
		{5,0},{5,1},{5,2},{5,3},
		{6,0},{6,1},{6,2},
		{7,0},{7,1},
		{8,0}
	};
	
	public SudokuGenerator(long seed){
		Random rand = new Random(seed);
		generateBaseGrid(rand);
	}
	
	public int generatePuzzleGridDiff(int designIndicator, int difficulty,
									  long seed, int iterRate){
		
		Random rand = new Random(seed);
		
		int[][] tempPuzzleGrid;
		Sudoku tempPuzzle;
		int[] tempPuzzleInfo;
		// boolean isSolved = false;
		// System.out.print("Puzzle generating");
		
		int diffTarget;
		if(difficulty<=0){
			diffTarget = 0;
		}else if(difficulty<9){
			diffTarget = difficulty;
		}else diffTarget = Integer.MAX_VALUE;
		
		tempPuzzleGrid = new int[baseGrid.length][];
		puzzleGrid = new int[baseGrid.length][];
		
		for(int i=0; i<baseGrid.length; i++){
			puzzleGrid[i] = baseGrid[i].clone();
			tempPuzzleGrid[i] = baseGrid[i].clone();
		}
		
		// puzzle = new Sudoku(puzzleGrid);
		puzzleInfo = new int[3];
		puzzleInfo[0] = 1;
		
		// tempPuzzle = new Sudoku(tempPuzzleGrid);
		tempPuzzleInfo = new int[3];
		tempPuzzleInfo[0] = 1;
		
		boolean eraseSwitch = true;
		
		for(int iter=0; iter<iterRate; iter++){
			/*
			case 0 : -2, +2
			case 1 : -2, +2
			case 2 : -2, +2
			case 3 : -2, +2
			*/
			switch(designIndicator){
				case 1 : // asymmetry
					if(eraseSwitch){ // 찬칸 중에 2칸 골라 지우기
						for(int i=0; i<2; i++){
							int tgInd = rand.nextInt(81-tempPuzzleInfo[2]-i);
							int ind=-1;
							for(int j=0; j<=tgInd; j++){
								do{
									ind++;
								}while(tempPuzzleGrid[ind/9][ind%9] <= 0);
							}tempPuzzleGrid[ind/9][ind%9] = 0;
						}
					}else{ // 빈칸 중에 2칸 골라 채우기
						for(int i=0; i<2; i++){
							int tgInd = rand.nextInt(tempPuzzleInfo[2]-i);
							int ind=-1;
							for(int j=0; j<=tgInd; j++){
								do{
									ind++;
								}while(tempPuzzleGrid[ind/9][ind%9] >= 1);
							}tempPuzzleGrid[ind/9][ind%9] = baseGrid[ind/9][ind%9];
						}
					}break;
				case 3 : // order-4 rotational symmetry
					if(eraseSwitch){ // 찬칸 중에 1칸과 그 회전대칭 +3칸 지우기
						int tgInd = rand.nextInt((84-tempPuzzleInfo[2])/4);
						int ind=-1;
						for(int j=0; j<=tgInd; j++){
							do{
								ind++;
							}while(tempPuzzleGrid[ind/5][4-ind%5]<=0);
						}int xind = ind/5, yind = 4-ind%5;
						tempPuzzleGrid[xind][yind] = 0;
						tempPuzzleGrid[8-yind][xind] = 0;
						tempPuzzleGrid[8-xind][8-yind] = 0;
						tempPuzzleGrid[yind][8-xind] = 0;
					}else{ // 빈칸 중에 1칸과 그 맞은칸 채우기
						int tgInd = rand.nextInt((tempPuzzleInfo[2]+3)/4);
						int ind=-1;
						for(int j=0; j<=tgInd; j++){
							do{
								ind++;
							}while(tempPuzzleGrid[ind/9][ind%9]>=1);
						}int xind = ind/5, yind = 4-ind%5;
						tempPuzzleGrid[xind][yind] = baseGrid[xind][yind];
						tempPuzzleGrid[8-yind][xind] = baseGrid[8-yind][xind];
						tempPuzzleGrid[8-xind][8-yind] = baseGrid[8-xind][8-yind];
						tempPuzzleGrid[yind][8-xind] = baseGrid[yind][8-xind];
					}break;
				case 4 : // 1-way reflexive symmetry
					int axisEmpty = 0;
					for(int i=0; i<tempPuzzleGrid.length; i++){
						if(tempPuzzleGrid[i][4]<=0) axisEmpty++;
					}
					if(eraseSwitch){ // 찬칸 중에 1칸과 그 거울칸 지우기
						int tgInd = rand.nextInt(45 - (tempPuzzleInfo[2]+axisEmpty)/2);
						int ind=-1;
						for(int j=0; j<=tgInd; j++){
							do{
								ind++;
							}while(tempPuzzleGrid[ind/5][ind%5]<=0);
						}int xind = ind/5, yind = ind%5;
						tempPuzzleGrid[xind][yind] = 0;
						tempPuzzleGrid[xind][8-yind] = 0;
						
					}else{ // 빈칸 중에 1칸과 그 거울칸 채우기
						int tgInd = rand.nextInt((tempPuzzleInfo[2]+axisEmpty)/2);
						int ind=-1;
						for(int j=0; j<=tgInd; j++){
							do{
								ind++;
							}while(tempPuzzleGrid[ind/5][ind%5]>=1);
						}int xind = ind/5, yind = ind%5;
						tempPuzzleGrid[xind][yind] = baseGrid[xind][yind];
						tempPuzzleGrid[xind][8-yind] = baseGrid[xind][8-yind];
					}break;
				case 5 : // 2-way reflexive symmetry
					axisEmpty = 0;
					for(int i=0; i<tempPuzzleGrid.length; i++){
						if(tempPuzzleGrid[i][4]<=0) axisEmpty++;
						if(tempPuzzleGrid[4][i]<=0) axisEmpty++;
					}
					if(eraseSwitch){ // 찬칸 중에 1칸과 그 거울칸 지우기
						int tgInd = rand.nextInt(25 - (tempPuzzleInfo[2]+axisEmpty+1)/4);
						int ind=-1;
						for(int j=0; j<=tgInd; j++){
							do{
								ind++;
							}while(tempPuzzleGrid[ind/5][ind%5]<=0);
						}int xind = ind/5, yind = ind%5;
						tempPuzzleGrid[xind][yind] = 0;
						tempPuzzleGrid[xind][8-yind] = 0;
						tempPuzzleGrid[8-xind][yind] = 0;
						tempPuzzleGrid[8-xind][8-yind] = 0;
					}else{ // 빈칸 중에 1칸과 그 거울칸 채우기
						int tgInd = rand.nextInt((tempPuzzleInfo[2]+axisEmpty+1)/4);
						int ind=-1;
						for(int j=0; j<=tgInd; j++){
							do{
								ind++;
							}while(tempPuzzleGrid[ind/5][ind%5]>=1);
						}int xind = ind/5, yind = ind%5;
						tempPuzzleGrid[xind][yind] = baseGrid[xind][yind];
						tempPuzzleGrid[xind][8-yind] = baseGrid[xind][8-yind];
						tempPuzzleGrid[8-xind][yind] = baseGrid[8-xind][yind];
						tempPuzzleGrid[8-xind][8-yind] = baseGrid[8-xind][8-yind];
					}break;
				case 6 : // 1-way diagonal reflexive symmetry
					axisEmpty = 0;
					for(int i=0; i<tempPuzzleGrid.length; i++){
						if(tempPuzzleGrid[i][8-i]<=0) axisEmpty++;
					}
					if(eraseSwitch){ // 찬칸 중에 1칸과 그 거울칸 지우기
						int tgInd = rand.nextInt(45 - (tempPuzzleInfo[2]+axisEmpty)/2);
						int ind=-1;
						for(int j=0; j<=tgInd; j++){
							do{
								ind++;
							}while(tempPuzzleGrid[UPP_TRI_IND[ind][0]][UPP_TRI_IND[ind][1]]<=0);
						}
						tempPuzzleGrid[UPP_TRI_IND[ind][0]][UPP_TRI_IND[ind][1]] = 0;
						tempPuzzleGrid[8-UPP_TRI_IND[ind][1]][8-UPP_TRI_IND[ind][0]] = 0;
					}else{ // 빈칸 중에 1칸과 그 거울칸 채우기
						int tgInd = rand.nextInt((tempPuzzleInfo[2]+axisEmpty)/2);
						int ind=-1;
						for(int j=0; j<=tgInd; j++){
							do{
								ind++;
							}while(tempPuzzleGrid[UPP_TRI_IND[ind][0]][UPP_TRI_IND[ind][1]]>=1);
						}
						tempPuzzleGrid[UPP_TRI_IND[ind][0]][UPP_TRI_IND[ind][1]]
							= baseGrid[UPP_TRI_IND[ind][0]][UPP_TRI_IND[ind][1]];
						tempPuzzleGrid[8-UPP_TRI_IND[ind][1]][8-UPP_TRI_IND[ind][0]]
							= baseGrid[8-UPP_TRI_IND[ind][1]][8-UPP_TRI_IND[ind][0]];
					}break;
				case 7 : // 2-way diagonal reflexive symmetry
					axisEmpty = 0;
					for(int i=0; i<tempPuzzleGrid.length; i++){
						if(tempPuzzleGrid[i][8-i]<=0) axisEmpty++;
						if(tempPuzzleGrid[i][i]<=0) axisEmpty++;
					}
					if(eraseSwitch){ // 찬칸 중에 1칸과 그 거울칸 지우기
						int tgInd = rand.nextInt(25 - (tempPuzzleInfo[2]+axisEmpty+1)/4);
						int ind=-1;
						for(int j=0; j<=tgInd; j++){
							do{
								ind++;
							}while(tempPuzzleGrid[RIGHT_QUAD_IND[ind][0]][RIGHT_QUAD_IND[ind][1]]<=0);
						}
						tempPuzzleGrid[RIGHT_QUAD_IND[ind][0]][RIGHT_QUAD_IND[ind][1]] = 0;
						tempPuzzleGrid[8-RIGHT_QUAD_IND[ind][1]][8-RIGHT_QUAD_IND[ind][0]] = 0;
						tempPuzzleGrid[RIGHT_QUAD_IND[ind][1]][RIGHT_QUAD_IND[ind][0]] = 0;
						tempPuzzleGrid[8-RIGHT_QUAD_IND[ind][0]][8-RIGHT_QUAD_IND[ind][1]] = 0;
					}else{ // 빈칸 중에 1칸과 그 거울칸 채우기
						int tgInd = rand.nextInt((tempPuzzleInfo[2]+axisEmpty+1)/4);
						int ind=-1;
						for(int j=0; j<=tgInd; j++){
							do{
								ind++;
							}while(tempPuzzleGrid[RIGHT_QUAD_IND[ind][0]][RIGHT_QUAD_IND[ind][1]]>=1);
						}
						tempPuzzleGrid[RIGHT_QUAD_IND[ind][0]][RIGHT_QUAD_IND[ind][1]]
							= baseGrid[RIGHT_QUAD_IND[ind][0]][RIGHT_QUAD_IND[ind][1]];
						tempPuzzleGrid[8-RIGHT_QUAD_IND[ind][1]][8-RIGHT_QUAD_IND[ind][0]]
							= baseGrid[8-RIGHT_QUAD_IND[ind][1]][8-RIGHT_QUAD_IND[ind][0]];
						tempPuzzleGrid[RIGHT_QUAD_IND[ind][1]][RIGHT_QUAD_IND[ind][0]]
							= baseGrid[RIGHT_QUAD_IND[ind][1]][RIGHT_QUAD_IND[ind][0]];
						tempPuzzleGrid[8-RIGHT_QUAD_IND[ind][0]][8-RIGHT_QUAD_IND[ind][1]]
							= baseGrid[8-RIGHT_QUAD_IND[ind][0]][8-RIGHT_QUAD_IND[ind][1]];
					}break;
					
				default : // rotational symmetry
					if(eraseSwitch){ // 찬칸 중에 1칸과 그 맞은칸 지우기
						int tgInd = rand.nextInt((82-tempPuzzleInfo[2])/2);
						int ind=-1;
						for(int j=0; j<=tgInd; j++){
							do{
								ind++;
							}while(tempPuzzleGrid[ind/9][ind%9]<=0);
						}int xind = ind/9, yind = ind%9;
						tempPuzzleGrid[xind][yind] = 0;
						tempPuzzleGrid[8-xind][8-yind] = 0;
					}else{ // 빈칸 중에 1칸과 그 맞은칸 채우기
						int tgInd = rand.nextInt((tempPuzzleInfo[2]+1)/2);
						int ind=-1;
						for(int j=0; j<=tgInd; j++){
							do{
								ind++;
							}while(tempPuzzleGrid[ind/9][ind%9]>=1);
						}int xind = ind/9, yind = ind%9;
						tempPuzzleGrid[xind][yind] = baseGrid[xind][yind];
						tempPuzzleGrid[8-xind][8-yind] = baseGrid[8-xind][8-yind];
					}break;
			}
			
			tempPuzzle = new Sudoku(tempPuzzleGrid);
			tempPuzzleInfo = tempPuzzle.solveSudokuInfo(true);
			
			if(tempPuzzleInfo[0] > 1 || diffTarget < tempPuzzleInfo[1]){ // not unique
				eraseSwitch = false;
				continue;
			}
			eraseSwitch = true;
			
			if(tempPuzzleInfo[1] > puzzleInfo[1]
			  || (tempPuzzleInfo[1] == puzzleInfo[1] && tempPuzzleInfo[2] > puzzleInfo[2])){
				for(int i=0; i<puzzleGrid.length; i++){
					puzzleGrid[i] = tempPuzzleGrid[i].clone();
				}
				// puzzleGrid = tempPuzzleGrid;
				puzzleInfo = tempPuzzleInfo.clone();
			}
		}
		return (100*puzzleInfo[1] + puzzleInfo[2]);
		
	}
	
	protected void generateBaseGrid(Random rand){
		
		baseGrid = new int[9][9];
		
		// box row 0 filling
		boolean[] chosen = new boolean[10];
		for(int i=9; i>=1; i--){
			int tgNum = rand.nextInt(i)+1; // random number 1~i
			int n=0;
			for(int j=1; j<=tgNum; j++){
				do{
					n++;
				}while(chosen[n]);
			}
			baseGrid[Sudoku.BOX_CELL[0][9-i][0]][Sudoku.BOX_CELL[0][9-i][1]] = n;
			chosen[n] = true;
		}
		// box 0 filled
		
		chosen = new boolean[6];
		for(int i=6; i>=4; i--){
			int tgInd = rand.nextInt(i);
			int ind = -1;
			for(int j=0; j<=tgInd; j++){
				do{
					ind++;
				}while(chosen[ind]);
			}
			baseGrid[Sudoku.BOX_CELL[1][6-i][0]][Sudoku.BOX_CELL[1][6-i][1]]
				= baseGrid[Sudoku.BOX_CELL[0][ind+3][0]][Sudoku.BOX_CELL[0][ind+3][1]];
			chosen[ind] = true;
		} // ----- box 1 row 0 filled
		
		chosen = new boolean[10];
		for(int i=0; i<3; i++){
			chosen[baseGrid[Sudoku.BOX_CELL[0][i+3][0]]
						   [Sudoku.BOX_CELL[0][i+3][1]]] = true;
			chosen[baseGrid[Sudoku.BOX_CELL[1][i][0]]
						   [Sudoku.BOX_CELL[1][i][1]]] = true;
		}
		int availCnt = 0;
		for(int n=1; n<chosen.length; n++){
			if(!chosen[n]) availCnt++;
		}
		// availCnt = 3, 4, 5, 6 for 30, 21, 12, 03
		int[] availNum = new int[3];
		switch(availCnt){
			case 3 :
				availNum[0] = baseGrid[0][0];
				availNum[1] = baseGrid[0][1];
				availNum[2] = baseGrid[0][2];
				break;
			case 4 :
				for(int i=0; i<3; i++){
					if(chosen[baseGrid[2][i]]) continue;
					availNum[0] = baseGrid[2][i];
					break;
				}int[] row0Choice = PERMUTE_3[rand.nextInt(6)];
				availNum[1] = baseGrid[0][row0Choice[0]];
				availNum[2] = baseGrid[0][row0Choice[1]];
				break;
			case 5 :
				for(int i=0, j=0; i<3 && j<2; i++){
					if(chosen[baseGrid[2][i]]) continue;
					availNum[j++] = baseGrid[2][i];
				}availNum[2] = baseGrid[0][rand.nextInt(3)];
				break;
			case 6 :
				availNum[0] = baseGrid[2][0];
				availNum[1] = baseGrid[2][1];
				availNum[2] = baseGrid[2][2];
		}
		int[] fillChoice = PERMUTE_3[rand.nextInt(6)];
		for(int i=0; i<fillChoice.length; i++){
			baseGrid[1][i+3] = availNum[fillChoice[i]];
		} // ----- box 1 row 1 filled
		
		chosen = new boolean[10];
		for(int i=0; i<6; i++){
			chosen[baseGrid[Sudoku.BOX_CELL[1][i][0]][Sudoku.BOX_CELL[1][i][1]]] = true;
		}
		availNum = new int[3];
		for(int n=1, j=0; n<=9 && j<availNum.length; n++){
			if(chosen[n]) continue;
			availNum[j++] = n;
		}
		fillChoice = PERMUTE_3[rand.nextInt(6)];
		for(int i=0; i<fillChoice.length; i++){
			baseGrid[2][i+3] = availNum[fillChoice[i]];
		} // ----- box 1 row 2 filled
		// box 1 filled
		
		for(int r=0; r<3; r++){
			chosen = new boolean[10];
			for(int c=0; c<6; c++){
				chosen[baseGrid[r][c]] = true;
			}
			availNum = new int[3];
			for(int n=1, j=0; n<=9 && j<availNum.length; n++){
				if(chosen[n]) continue;
				availNum[j++] = n;
			}
			fillChoice = PERMUTE_3[rand.nextInt(6)];
			for(int i=0; i<fillChoice.length; i++){
				baseGrid[r][i+6] = availNum[fillChoice[i]];
			}
		}
		// box 2 filled
		/*
		// debug
		for(int i=0; i<3; i++){
			for(int j=0; j<baseGrid[i].length; j++){
				System.out.print(baseGrid[i][j]);
			}System.out.println();
		}
		*/
		// col 0 filling
		chosen = new boolean[10];
		for(int r=0; r<3; r++){
			chosen[baseGrid[r][0]] = true;
		}
		/*
		// debug
		for(int i=1; i<chosen.length; i++){
			System.out.print(chosen[i]+" ");
		}
		*/
		for(int i=6; i>=1; i--){
			int tgNum = rand.nextInt(i)+1;
			// System.out.print(tgNum+" ");
			int n=0;
			for(int j=1; j<=tgNum; j++){
				do{
					n++;
				}while(chosen[n]);
			}// System.out.println(n);
			baseGrid[9-i][0] = n;
			chosen[n] = true;
		}
		
		// remaining 48 cells filling
		Sudoku incompl = new Sudoku(baseGrid);
		incompl.naiveSolveSudoku(rand);
		// incompl.solBool has its grid info
		baseGrid = incompl.solutionInIntArray();
	}
	
	public int[][] baseGrid(){
		return this.baseGrid;
	}
	
	public int[][] puzzleGrid(){
		return this.puzzleGrid;
	}
	
	public void printGrid(){
		Sudoku base = new Sudoku(baseGrid);
		base.printSudoku();
	}
}

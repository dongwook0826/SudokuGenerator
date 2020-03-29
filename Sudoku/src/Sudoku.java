package sudoku;

// trivial - easy - plain - handy - moderate 
// - tricky - hard - vexatious - fiendish - divine
// Splendid!
// ...but isn't it divided too much?

public class Sudoku {
	
	protected boolean[][][] sdkBool;
	// boolean [row] [col] [number]; [0] true for initial given clues
	protected boolean[][][] firstSolBool;
	// first found solution storage
	protected boolean[][][] solBool;
	// sdkBool, but only for solving process
	protected boolean isValid;
	private boolean[][][][] branchImage;
	private int[][] branchCell; // {rind, cind, chosen candidate, branch method indicator, area ind}
	// branch indicator = -1,0,1,2 for cell-base, set-base over box,row,col
	private int[] branchFactor;
	
	protected static final boolean[] EMPTY_CELL = {false, true, true, true, true,
													true, true, true, true, true};
	protected static final boolean[] FILLING_CELL = {true, false, false, false, false,
													false, false, false, false, false};
	
	protected static final int[][] CELL_BOX = {
		{0,0,0,1,1,1,2,2,2},
		{0,0,0,1,1,1,2,2,2},
		{0,0,0,1,1,1,2,2,2},
		{3,3,3,4,4,4,5,5,5},
		{3,3,3,4,4,4,5,5,5},
		{3,3,3,4,4,4,5,5,5},
		{6,6,6,7,7,7,8,8,8},
		{6,6,6,7,7,7,8,8,8},
		{6,6,6,7,7,7,8,8,8}
	};
	protected static final int[][][] BOX_CELL = {
		{{0,0},{0,1},{0,2},{1,0},{1,1},{1,2},{2,0},{2,1},{2,2}},
		{{0,3},{0,4},{0,5},{1,3},{1,4},{1,5},{2,3},{2,4},{2,5}},
		{{0,6},{0,7},{0,8},{1,6},{1,7},{1,8},{2,6},{2,7},{2,8}},
		{{3,0},{3,1},{3,2},{4,0},{4,1},{4,2},{5,0},{5,1},{5,2}},
		{{3,3},{3,4},{3,5},{4,3},{4,4},{4,5},{5,3},{5,4},{5,5}},
		{{3,6},{3,7},{3,8},{4,6},{4,7},{4,8},{5,6},{5,7},{5,8}},
		{{6,0},{6,1},{6,2},{7,0},{7,1},{7,2},{8,0},{8,1},{8,2}},
		{{6,3},{6,4},{6,5},{7,3},{7,4},{7,5},{8,3},{8,4},{8,5}},
		{{6,6},{6,7},{6,8},{7,6},{7,7},{7,8},{8,6},{8,7},{8,8}}
	};
	
	public Sudoku(int[][] sudoku){
		sdkBool = new boolean[9][9][10];
		
		if(sudoku.length != 9 || sudoku[0].length != 9){
			System.out.println("Improper sudoku input");
		}else{
			for(int i=0; i<9; i++){
				for(int j=0; j<9; j++){
					if(sudoku[i][j] == 0){
						sdkBool[i][j] = EMPTY_CELL.clone();
					}else{
						sdkBool[i][j][0] = true;
						sdkBool[i][j][sudoku[i][j]] = true;
					}
				}
			}
		}isValid = seemsValid();
	}
	
	protected boolean seemsValid(){
		// box check
		for(int b=0; b<BOX_CELL.length; b++){
			for(int bi=0; bi<BOX_CELL[b].length-1; bi++){
				if(!sdkBool[BOX_CELL[b][bi][0]][BOX_CELL[b][bi][1]][0]) // if not filled
					continue;
				int n=1;
				while(!sdkBool[BOX_CELL[b][bi][0]][BOX_CELL[b][bi][1]][n]) n++; // n filled in the cell
				for(int bj=bi+1; bj<BOX_CELL[b].length; bj++){
					if(sdkBool[BOX_CELL[b][bj][0]][BOX_CELL[b][bj][1]][0]
					&& sdkBool[BOX_CELL[b][bj][0]][BOX_CELL[b][bj][1]][n]){
						System.out.printf("box %d cell %d %d overlap %d\n", b, bi, bj, n);
						return false;
					}
				}
			}
		}
		// row+column check
		for(int r=0; r<sdkBool.length; r++){
			for(int c=0; c<sdkBool[r].length; c++){
				if(!sdkBool[r][c][0])
					continue;
				int n=1;
				while(!sdkBool[r][c][n]) n++;
				// row check
				for(int ci=c+1; ci<sdkBool[r].length; ci++){
					if(sdkBool[r][ci][0] && sdkBool[r][ci][n]){
						System.out.printf("row %d cell %d %d overlap %d\n", r, c, ci, n);
						return false;
					}
				}
				// column check
				for(int ri=r+1; ri<sdkBool.length; ri++){
					if(sdkBool[ri][c][0] && sdkBool[ri][c][n]){
						System.out.printf("col %d cell %d %d overlap %d\n", c, r, ri, n);
						return false;
					}
				}
			}
		}
		return true;
	}
	
	//-------------------------------from here, solving method------------------------------
	
	protected int[] solveSudokuInfo(boolean ifMultSol){
		
		int[] sdkInfo = {0,0}; // {sol cnt, branch diff}; teq depth later implementable...maybe
		// play on solBool when searching solution
		if(!isValid){
			System.out.println("Invalid sudoku");
			return sdkInfo;
		}
		
		// copy sdkBool onto solBool
		solBool = new boolean[sdkBool.length][sdkBool[0].length][];
		int emptyCellCnt = sdkBool.length * sdkBool[0].length;
		for(int i=0; i<solBool.length; i++){
			for(int j=0; j<solBool[i].length; j++){
				solBool[i][j] = sdkBool[i][j].clone();
			}
		}
		
		// pencil-mark
		for(int i=0; i<solBool.length; i++){
			for(int j=0; j<solBool[i].length; j++){
				if(!solBool[i][j][0]){
					continue;
				}emptyCellCnt--;
				for(int n=1; n<solBool[i][j].length; n++){
					if(!solBool[i][j][n]) continue;
					erasePencilMark(i, j, n);
				}
			}
		}
		
		// solve...
		int breakCrit = ifMultSol ? 2 : 1;
		
		branchImage = new boolean[emptyCellCnt][][][];
		branchCell = new int[emptyCellCnt][];
		branchFactor = new int[emptyCellCnt];
		// int firstBranchFactor = 0;
		int branchCnt = 0;
		boolean branchChangeStacked = false;
		
		int minCand;
		int[] minInd;
		
		int minSetCnt;
		int[] minSetInfo;
		
		
		solveRecursion : while(true){
			// naked single search
			
			if(branchChangeStacked){ // change branch to next cell/candidate
				// System.out.println("todo : branch change");
				int n=1;
				switch(branchCell[branchCnt][3]){
					case -1 : // cell candidate base
						while(n<=solBool.length){
							if(solBool[branchCell[branchCnt][0]][branchCell[branchCnt][1]][n]) break;
							n++;
						}if(n>solBool.length){
							if(branchCnt<=0){
								break solveRecursion;
							}
							branchRollBack(--branchCnt);
							// System.out.printf("\tnow depth %d\n", branchCnt);
							continue solveRecursion;
						}
						branchCell[branchCnt][2] = n;
						break;
					case 0 : // box set num base
						n=branchCell[branchCnt][2];
						int box = branchCell[branchCnt][4];
						int bi=0;
						while(bi<BOX_CELL[box].length){
							if(solBool[BOX_CELL[box][bi][0]][BOX_CELL[box][bi][1]][n]) break;
							bi++;
						}if(bi>=solBool.length){
							if(branchCnt<=0){
								break solveRecursion;
							}
							branchRollBack(--branchCnt);
							// System.out.printf("\tnow depth %d\n", branchCnt);
							continue solveRecursion;
						}
						branchCell[branchCnt][0] = BOX_CELL[box][bi][0];
						branchCell[branchCnt][1] = BOX_CELL[box][bi][1];
						break;
					case 1 : // row set num base
						n=branchCell[branchCnt][2];
						int row = branchCell[branchCnt][4];
						int ci=0;
						while(ci<solBool[row].length){
							if(solBool[row][ci][n]) break;
							ci++;
						}if(ci>=solBool[row].length){
							if(branchCnt<=0){
								break solveRecursion;
							}
							branchRollBack(--branchCnt);
							// System.out.printf("\tnow depth %d\n", branchCnt);
							continue solveRecursion;
						}
						branchCell[branchCnt][1] = row;
						branchCell[branchCnt][1] = ci;
						break;
					case 2 : // column set num base
						n=branchCell[branchCnt][2];
						int col = branchCell[branchCnt][4];
						int ri=0;
						while(ri<solBool.length){
							if(solBool[ri][col][n]) break;
							ri++;
						}if(ri>=solBool.length){
							if(branchCnt<=0){
								break solveRecursion;
							}
							branchRollBack(--branchCnt);
							// System.out.printf("\tnow depth %d\n", branchCnt);
							continue solveRecursion;
						}
						branchCell[branchCnt][0] = ri;
						branchCell[branchCnt][1] = col;
				}
				erasePencilMark(branchCell[branchCnt][0], branchCell[branchCnt][1], branchCell[branchCnt][2]);
				branchChangeStacked = false;
			}
			
			minCand = solBool.length+1;
			int cand;
			minInd = new int[2];
			
			for(int i=0; i<solBool.length; i++){
				for(int j=0; j<solBool[i].length; j++){
					if(solBool[i][j][0]) continue;
					cand = 0;
					for(int n=1; n<solBool[i][j].length; n++){
						if(solBool[i][j][n]) cand++;
					}
					if(cand == 0){
						if(branchCnt <= 0){ // unsolvable
							break solveRecursion;
						}else{ // no solution on this branch
							branchRollBack(--branchCnt);
							// System.out.printf("\tnow depth %d\n", branchCnt);
							branchChangeStacked = true;
							continue solveRecursion;
						}
					}else if(cand == 1){ // cell fixed
						int n=1;
						while(!solBool[i][j][n]) n++;
						erasePencilMark(i, j, n);
						// System.out.printf("cell %d %d fixed : %d\n", i, j, n);
						continue solveRecursion;
					}else{ // cand>1; is backtracking pivot cell?
						if(cand >= minCand) continue;
						minCand = cand;
						minInd[0] = i;
						minInd[1] = j;
					}
				}
			}if(minCand > solBool.length){ // already solved; the only way of breaking recursion with solution
				sdkInfo[0]++;
				if(sdkInfo[0] == 1){
					System.out.println("SOLUTION FOUND");
					firstSolBool = new boolean[solBool.length][solBool[0].length][];
					for(int r=0; r<firstSolBool.length; r++){
						for(int c=0; c<firstSolBool[r].length; c++){
							firstSolBool[r][c] = solBool[r][c].clone();
						}
					}
					for(int br=0; branchFactor[br]>0; br++){
						sdkInfo[1] += (branchFactor[br]-1)*(branchFactor[br]-1);
					}
				}
				if(sdkInfo[0] >= breakCrit){
					// sdkInfo[2]=firstBranchFactor;
					break solveRecursion;
				}else{ // sdkInfo[0] == 1, breakCrit == 2
					if(branchCnt<=0){ // solution unique
						// sdkInfo[1]=firstBranchFactor;
						break solveRecursion;
					}
					branchRollBack(--branchCnt);
					// System.out.printf("\tnow depth %d\n", branchCnt);
					branchChangeStacked = true;
					continue solveRecursion;
				}
			}
			
			// hidden single search
			minSetCnt = solBool.length+1;
			int setCnt;
			minSetInfo = new int[3]; // {candidate num, 0-1-2 for box-row-col, area ind}
			boolean[] isFilled;
			
			// ------ box search
			for(int b=0; b<BOX_CELL.length; b++){
				isFilled = new boolean[solBool.length+1];
				for(int i=0; i<BOX_CELL[b].length; i++){ // check occupied numbers
					if(!solBool[BOX_CELL[b][i][0]][BOX_CELL[b][i][1]][0]) continue;
					int n=1;
					while(!solBool[BOX_CELL[b][i][0]][BOX_CELL[b][i][1]][n]) n++;
					isFilled[n] = true;
					// System.out.printf("box %d cell %d filled %d\n", b, i, n);
				}
				// debug
				/*
				System.out.printf("box %d : ", b);
				for(int n=1; n<isFilled.length; n++){
					System.out.printf("%b ", isFilled[n]);
				}System.out.println();
				*/
				for(int n=1; n<=solBool.length; n++){
					if(isFilled[n]) continue;
					setCnt=0;
					for(int i=0; i<BOX_CELL[b].length; i++){
						if(solBool[BOX_CELL[b][i][0]][BOX_CELL[b][i][1]][n])
							setCnt++;
					}
					if(setCnt == 0){
						if(branchCnt <= 0){ // unsolvable
							break solveRecursion;
						}else{ // no solution on this branch
							branchRollBack(--branchCnt);
							// System.out.printf("\tnow depth %d\n", branchCnt);
							branchChangeStacked = true;
							continue solveRecursion;
						}
					}else if(setCnt == 1){ // number fix
						int i=0;
						while(!solBool[BOX_CELL[b][i][0]][BOX_CELL[b][i][1]][n]) i++;
						erasePencilMark(BOX_CELL[b][i][0], BOX_CELL[b][i][1], n);
						// System.out.printf("num %d fixed : box %d cell %d\n", n, b, i);
						continue solveRecursion;
					}else{
						if(setCnt >= minSetCnt) continue;
						minSetCnt = setCnt;
						minSetInfo[0] = n;
						minSetInfo[1] = 0;
						minSetInfo[2] = b;
					}
				}
			}
			// ---------------------- row search
			for(int r=0; r<solBool.length; r++){
				isFilled = new boolean[solBool.length+1];
				for(int c=0; c<solBool[r].length; c++){ // check occupied numbers
					if(!solBool[r][c][0]) continue;
					int n=1;
					while(!solBool[r][c][n]) n++;
					isFilled[n] = true;
				}
				for(int n=1; n<=solBool.length; n++){
					if(isFilled[n]) continue;
					setCnt=0;
					for(int c=0; c<solBool[r].length; c++){
						if(solBool[r][c][n])
							setCnt++;
					}
					if(setCnt == 0){
						if(branchCnt <= 0){ // unsolvable
							break solveRecursion;
						}else{ // no solution on this branch
							branchRollBack(--branchCnt);
							// System.out.printf("\tnow depth %d\n", branchCnt);
							branchChangeStacked = true;
							continue solveRecursion;
						}
					}else if(setCnt == 1){ // number fix
						int c=0;
						while(!solBool[r][c][n]) c++;
						erasePencilMark(r, c, n);
						// System.out.printf("num %d fixed : row %d cell %d\n", n, r, c);
						continue solveRecursion;
					}else{
						if(setCnt >= minSetCnt) continue;
						minSetCnt = setCnt;
						minSetInfo[0] = n;
						minSetInfo[1] = 1;
						minSetInfo[2] = r;
					}
				}
			}
			// --------------------------------------------------- column search
			for(int c=0; c<solBool[0].length; c++){
				isFilled = new boolean[solBool.length+1];
				for(int r=0; r<solBool.length; r++){ // check occupied numbers
					if(!solBool[r][c][0]) continue;
					int n=1;
					while(!solBool[r][c][n]) n++;
					isFilled[n] = true;
				}
				for(int n=1; n<=solBool.length; n++){
					if(isFilled[n]) continue;
					setCnt=0;
					for(int r=0; r<solBool.length; r++){
						if(solBool[r][c][n])
							setCnt++;
					}
					if(setCnt == 0){
						if(branchCnt <= 0){ // unsolvable
							break solveRecursion;
						}else{ // no solution on this branch
							branchRollBack(--branchCnt);
							// System.out.printf("\tnow depth %d\n", branchCnt);
							branchChangeStacked = true;
							continue solveRecursion;
						}
					}else if(setCnt == 1){ // number fix
						int r=0;
						while(!solBool[r][c][n]) r++;
						erasePencilMark(r, c, n);
						// System.out.printf("num %d fixed : column %d cell %d\n", n, c, r);
						continue solveRecursion;
					}else{
						if(setCnt >= minSetCnt) continue;
						minSetCnt = setCnt;
						minSetInfo[0] = n;
						minSetInfo[1] = 2;
						minSetInfo[2] = c;
					}
				}
			}
			
			// backtracking
			
			// System.out.printf("backtrack depth %d, factor ", branchCnt+1);
			// branchFactor modulation needed
			// 새로 branch를 뻗을 때만 branchFactor가 더해지거나 수정됨
			
			branchImage[branchCnt] = new boolean[solBool.length][solBool[0].length][];
			for(int r=0; r<solBool.length; r++){
				for(int c=0; c<solBool[r].length; c++){
					branchImage[branchCnt][r][c] = solBool[r][c].clone();
				}
			}
			
			if(minCand > minSetCnt){ // set-search based branching
				branchCell[branchCnt] = new int[4];
				branchCell[branchCnt][0] = minInd[0];
				branchCell[branchCnt][1] = minInd[1];
				branchCell[branchCnt][3] = -1;
				if(branchFactor[branchCnt] < minSetCnt){
					branchFactor[branchCnt] = minSetCnt;
				}
				// System.out.println(minSetCnt);
				for(int n=1; n<=solBool.length; n++){
					if(solBool[minInd[0]][minInd[1]][n]){
						branchCell[branchCnt][2] = n;
						// System.out.printf("cell %d %d num %d\n", minInd[0], minInd[1], n);
						break;
					}
				}
			}else{ // cell candidate based branching
				branchCell[branchCnt] = new int[5];
				branchCell[branchCnt][2] = minSetInfo[0];
				branchCell[branchCnt][3] = minSetInfo[1];
				branchCell[branchCnt][4] = minSetInfo[2];
				if(branchFactor[branchCnt] < minCand){
					branchFactor[branchCnt] = minCand;
				}
				// System.out.println(minCand);
				switch(minSetInfo[1]){
					case 0 :
						for(int i=0; i<BOX_CELL[minSetInfo[2]].length; i++){
							if(solBool[BOX_CELL[minSetInfo[2]][i][0]][BOX_CELL[minSetInfo[2]][i][1]][minSetInfo[0]]){
								branchCell[branchCnt][0] = BOX_CELL[minSetInfo[2]][i][0];
								branchCell[branchCnt][1] = BOX_CELL[minSetInfo[2]][i][1];
								// System.out.printf("num %d box %d %d\n", minSetInfo[0], minSetInfo[2], i);
								break;
							}
						}break;
					case 1 :
						for(int c=0; c<solBool[minSetInfo[2]].length; c++){
							if(solBool[minSetInfo[2]][c][minSetInfo[0]]){
								branchCell[branchCnt][0] = minSetInfo[2];
								branchCell[branchCnt][1] = c;
								// System.out.printf("num %d row %d %d\n", minSetInfo[0], minSetInfo[2], c);
								break;
							}
						}break;
					case 2 :
						for(int r=0; r<solBool.length; r++){
							if(solBool[r][minSetInfo[2]][minSetInfo[0]]){
								branchCell[branchCnt][0] = r;
								branchCell[branchCnt][1] = minSetInfo[2];
								// System.out.printf("num %d col %d %d\n", minSetInfo[0], minSetInfo[2], r);
								break;
							}
						}break;
				}
			}// System.out.printf("cell %d %d num %d\n", branchCell[branchCnt][0], branchCell[branchCnt][1], branchCell[branchCnt][2]);
			erasePencilMark(branchCell[branchCnt][0], branchCell[branchCnt][1], branchCell[branchCnt][2]);
			branchCnt++;
			// System.out.printf("\tnow depth %d\n", branchCnt);
		}
		/*
		if(sdkInfo[0]==0){
			System.out.println("No solution");
		}*/
		return sdkInfo;
	}
	
	private void erasePencilMark(int i, int j, int n){
		int box = CELL_BOX[i][j];
		solBool[i][j] = FILLING_CELL.clone();
		for(int b=0; b<BOX_CELL[box].length; b++){
			solBool[BOX_CELL[box][b][0]][BOX_CELL[box][b][1]][n] = false;
		}solBool[i][j][n] = true;
		for(int c=0; c<solBool[i].length; ){
			if(CELL_BOX[i][c]==box){
				c+=3;
				continue;
			}solBool[i][c++][n] = false;
		}for(int r=0; r<solBool.length; ){
			if(CELL_BOX[r][j]==box){
				r+=3;
				continue;
			}solBool[r++][j][n] = false;
		}solBool[i][j][0] = true;
		// System.out.printf("pivot cell %d %d num %d pencil mark erased\n", i, j, n);
	}
	
	private void branchRollBack(int branchInd){
		/*
		System.out.printf("rollback to depth %d; cell %d %d num %d fail\n", branchInd,
						  branchCell[branchInd][0], branchCell[branchInd][1], branchCell[branchInd][2]);
		*/
		for(int ri=0; ri<solBool.length; ri++){
			for(int ci=0; ci<solBool[ri].length; ci++){
				solBool[ri][ci] = branchImage[branchInd][ri][ci].clone();
			}
		}solBool[branchCell[branchInd][0]][branchCell[branchInd][1]][branchCell[branchInd][2]] = false;
	}
	
	// -----------------------until here, methods for solveSudokuInfo----------------------------
	
	protected void resetSudoku(){
		for(int i=0; i<sdkBool.length; i++){
			for(int j=0; j<sdkBool[i].length; j++){
				if(!sdkBool[i][j][0]){
					sdkBool[i][j] = EMPTY_CELL.clone();
				}
			}
		}
	}
	
	public void printSudoku(){
		System.out.println("┏━━━┯━━━┯━━━┳━━━┯━━━┯━━━┳━━━┯━━━┯━━━┓");
		for(int i=0; i<sdkBool.length; i++){
			System.out.print("┃");
			for(int j=0; j<sdkBool[i].length; j++){
				if(sdkBool[i][j][0]){
					for(int n=1; n<sdkBool[i][j].length; n++){
						if(sdkBool[i][j][n]){
							System.out.printf(" %d ", n);
							break;
						}
					}
				}else{
					System.out.print("   ");
				}
				if(j%3==2){
					System.out.print("┃");
				}else{
					System.out.print("│");
				}
			}System.out.println();
			if(i%3!=2){
				System.out.println("┠───┼───┼───╂───┼───┼───╂───┼───┼───┨");
			}else if(i==sdkBool.length-1){
				System.out.println("┗━━━┷━━━┷━━━┻━━━┷━━━┷━━━┻━━━┷━━━┷━━━┛");
			}else{
				System.out.println("┣━━━┿━━━┿━━━╋━━━┿━━━┿━━━╋━━━┿━━━┿━━━┫");
			}
		}
	}
	
	public void printSolution(boolean ifMultSol){
		long startTime = System.currentTimeMillis();
		int[] sdkInfo = solveSudokuInfo(ifMultSol);
		long endTime = System.currentTimeMillis();
		
		System.out.println("┏━━━┯━━━┯━━━┳━━━┯━━━┯━━━┳━━━┯━━━┯━━━┓");
		for(int i=0; i<firstSolBool.length; i++){
			System.out.print("┃");
			for(int j=0; j<firstSolBool[i].length; j++){
				for(int n=1; n<firstSolBool[i][j].length; n++){
					if(firstSolBool[i][j][n]){
						System.out.printf(" %d ", n);
						break;
					}
				}
				if(j%3==2){
					System.out.print("┃");
				}else{
					System.out.print("│");
				}
			}System.out.println();
			if(i%3!=2){
				System.out.println("┠───┼───┼───╂───┼───┼───╂───┼───┼───┨");
			}else if(i==firstSolBool.length-1){
				System.out.println("┗━━━┷━━━┷━━━┻━━━┷━━━┷━━━┻━━━┷━━━┷━━━┛");
			}else{
				System.out.println("┣━━━┿━━━┿━━━╋━━━┿━━━┿━━━╋━━━┿━━━┿━━━┫");
			}
		}
		
		switch(sdkInfo[0]){
			case 0 :
				System.out.println("No solution");
				break;
			case 1 :
				if(ifMultSol){
					System.out.println("Unique solution");
				}else{
					System.out.println("found solution");
				}break;
			case 2 :
				System.out.println("Multiple solution");
		}
		System.out.printf("branch difficulty : %d\n", sdkInfo[1]);
		// System.out.printf("required technique level : %d\n", sdkInfo[2]);
		System.out.printf("total %6.5fs taken\n", (endTime-startTime)/1000.0);
	}
}

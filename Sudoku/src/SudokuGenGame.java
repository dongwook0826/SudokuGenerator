package sudoku;

import java.util.Scanner;
import java.io.*;

public class SudokuGenGame {
	public static void main(String[] args) throws IOException {
		
		String choiceStr;
		int choice;
		
		int maximaSearchCnt = 1500;
		int iterRate = 200;
		
		final String[] DIFF_STR = {
			"EASY", "HANDY", "MODERATE", "TRICKY",
			"CHALLENGING", "HARD", "COMPLICATED",
			"EVIL", "FIENDISH", "DIVINE"
		};
		
		final char[] FORBIDDEN_CHAR = {'\\', '/', ':', '*', '?', '"', '<', '>', '|'};
		
		Scanner sc = new Scanner(System.in);
		BufferedWriter bw = null;
		
		String fileName = "";
		
		System.out.println("******************************************");
		System.out.println("*                                        *");
		System.out.println("*          SUDOKU GEN CHALLENGE          *"); // SUDOKU CHALLENGE
		System.out.println("*                                        *");
		System.out.println("*            made by. TenDong            *"); // made by. TenDong
		System.out.println("*                                        *");
		System.out.println("******************************************");
		
		gameLoop : while(true){
			System.out.println("\n ---------- HOME ----------");
			System.out.println("\nWhich task do you want to do? (1, 2 or 0)");
			System.out.println("\t(1) Generate a new charming piece of Sudoku");
			System.out.println("\t(2) Analyze and solve a Sudoku I already have");
			System.out.println("\t(0) Exit this game");
			
			choice = -1;
			while(choice < 0){
				System.out.print(">>>>>>>> ");
				choiceStr = sc.nextLine();
				if(choiceStr.length() == 0){
					System.out.println("No input : try again!");
					continue;
				}
				choice = (int)choiceStr.charAt(0) - '0';
				switch(choice){
					case 0 :
						break gameLoop;
					case 1 : case 2 :
						break;
					default :
						System.out.println("Invalid choice : try again!");
						choice = -1;
				}
			}
			System.out.println();
			switch(choice){
				case 1 :
					System.out.println("\n ---------- MODE : GENERATOR ----------");
					genLoop : while(true){
						System.out.println("\nIn which geometry do you want the puzzle to be? (1 to 7, or 0)");
						System.out.println("\t(1) Asymmetry");
						System.out.println("\t(2) Rotational symmetry (order 2) <-- default design");
						System.out.println("\t(3) Rotational symmetry (order 4)");
						System.out.println("\t(4) Vertical reflexive symmetry");
						System.out.println("\t(5) Vertical & horizontal reflexive symmetry");
						System.out.println("\t(6) Diagonal reflexive symmetry (1-way)");
						System.out.println("\t(7) Diagonal reflexive symmetry (2-way)");
						System.out.println("\t(0) Go back to task choice");
						while(true){
							System.out.print(">>>>>>>> ");
							choiceStr = sc.nextLine();
							if(choiceStr.length() == 0){
								System.out.println("No input : try again!");
								continue;
							}else break;
						}
						
						choice = (int)choiceStr.charAt(0) - '0';
						if(choice == 0) continue gameLoop;
						
						int design = choice;
						
						System.out.println("\nHow hard may it be at most? (0 to 9; any other input to go back)");
						System.out.println("\t(0) easy - (1) handy - (2) moderate - (3) tricky");
						System.out.println("\t- (4) challenging - (5) hard - (6) complicated");
						System.out.println("\t- (7) evil - (8) fiendish - (9) divine");
						while(true){
							System.out.print(">>>>>>>> ");
							choiceStr = sc.nextLine();
							if(choiceStr.length() == 0){
								System.out.println("No input : try again!");
								continue;
							}break;
						}
						choice = (int)choiceStr.charAt(0) - '0';
						int diffBound = choice;
						if(choice<0 || choice>9){
							continue genLoop;
						}
						
						SudokuGenerator sdkGen = new SudokuGenerator(System.currentTimeMillis());
						int[][] baseGrid = sdkGen.baseGrid();
						
						Sudoku puzzle = new Sudoku(baseGrid);
						int[][] puzzleGrid = new int[baseGrid.length][baseGrid[0].length];
						
						int diffIndic = 0;
						int tempDiff;
						System.out.print("\nGenerating one nice and lovely piece of Sudoku");
						for(int search = 1; search<=maximaSearchCnt; search++){
							if(search%(maximaSearchCnt/10)==0){
								System.out.print(".");
							}
							tempDiff = sdkGen.generatePuzzleGridDiff(design, diffBound, System.currentTimeMillis(), iterRate);
							if(tempDiff <= diffIndic) continue;
							puzzleGrid = sdkGen.puzzleGrid();
							diffIndic = tempDiff;
						}System.out.println();
						
						puzzle = new Sudoku(puzzleGrid);
						puzzle.printSudoku();
						System.out.printf("LEVEL : %s (rate %d)\n", DIFF_STR[diffIndic < 900 ? diffIndic/100 : 9], diffIndic);
						
						System.out.println("\nWant to save it as txt file? (Y/N)");
						
						saveProcess : while(true){
							System.out.print(">>>>>>>> ");
							choiceStr = sc.nextLine();
							if(choiceStr.length() == 0 || (choiceStr.charAt(0) != 'y' && choiceStr.charAt(0) != 'Y') ){
								break;
							}
							
							System.out.println("\nPlease choose the output task you want to proceed");
							System.out.println("\t(1) Save the puzzle in a new txt file");
							if(fileName.length() != 0){
								System.out.printf("\t(2) Save it in the txt file just generated : %s\n", fileName+".txt");
							}System.out.println("\t(0) Quit the saving process and go back");
							while(true){
								System.out.print(">>>>>>>> ");
								choiceStr = sc.nextLine();
								if(choiceStr.length() == 0){
									System.out.println("No input : try again!");
									continue;
								}break;
							}
							
							choice = (int)choiceStr.charAt(0) - '0';
							if(choice <= 0 || choice>2){
								break saveProcess;
							}else if(choice == 2 && fileName.length() == 0){
								break saveProcess;
							}
							
							if(choice == 1){
								System.out.println("\nInput the name of text file to save your Sudoku (without extension)");
								
								while(true){
									System.out.print(">>>>>>>> ");
									fileName = sc.nextLine();
									if(fileName.length() == 0){
										System.out.println("No input : try again!");
										continue;
									}
									
									boolean fileNameValid = true;
									validCheck : for(int i=0; i<fileName.length(); i++){
										for(int f=0; f<FORBIDDEN_CHAR.length; f++){
											if(fileName.charAt(i) == FORBIDDEN_CHAR[f]){
												fileNameValid = false;
												break validCheck;
											}
										}
									}
									if(!fileNameValid){
										System.out.println("Invalid character included : try again!");
										continue;
									}
									break;
								}
							}
							
							bw = new BufferedWriter(new FileWriter(fileName+".txt", true));
							
							for(int i=0; i<puzzleGrid.length; i++){
								for(int j=0; j<puzzleGrid[i].length; j++){
									bw.write((char)(puzzleGrid[i][j]+'0'));
								}
							}bw.write(" ("+diffIndic+")");
							bw.newLine();
							bw.flush();
							
							System.out.printf("\nSaving success : %s\n", fileName+".txt");
							break;
						}
						
						System.out.println("\nExpose solution on the shell? (Y/N)");
						System.out.print(">>>>>>>> ");
						choiceStr = sc.nextLine();
						if(choiceStr.length() == 0 || (choiceStr.charAt(0) != 'y' && choiceStr.charAt(0) != 'Y') ){
							continue;
						}System.out.println("Solution : ");
						sdkGen.printGrid();
						switch(diffIndic/100){
							case 0 :
								System.out.println("Piece of cake, isn't it? ~_~");
								break;
							case 1 : case 2 : case 3 :
								System.out.println("Just moderate enough! XD");
								break;
							case 4 : case 5 : case 6 : case 7 : case 8 :
								System.out.println("Ugh, that was a hard one @_@");
								break;
							default :
								System.out.println("I feel like my brain is scorching...!!");
						}
					}
				case 2 :
					System.out.println("\n ---------- MODE : ANALYZER ---------- ");
					System.out.println("\nHave a puzzle to analyze? (Y/N)");
					analLoop : while(true){
						System.out.print(">>>>>>>> ");
						choiceStr = sc.nextLine();
						if(choiceStr.length() == 0 || (choiceStr.charAt(0) != 'y' && choiceStr.charAt(0) != 'Y') ){
							continue gameLoop;
						}
						
						String rawSudoku;
						
						System.out.println("\n[ Each letters can be numbers(1-9) and blanks(any other letters) ]");
						System.out.println("Input the Sudoku puzzle to be parsed, in one 81-letter string!");
						
						while(true){
							System.out.print(">>>>>>>> ");
							rawSudoku = sc.nextLine();
							if(rawSudoku.length() != 81){
								System.out.println("Invalid length : try again!");
								continue;
							}else break;
						}
						
						int[][] sdkGrid = new int[9][9];
						for(int r=0; r<sdkGrid.length; r++){
							for(int c=0; c<sdkGrid[r].length; c++){
								sdkGrid[r][c] = (int)rawSudoku.charAt(r*9+c) - '0';
								if(sdkGrid[r][c] <= 0 || sdkGrid[r][c] > 9){
									sdkGrid[r][c] = 0;
								}
							}
						}
						
						Sudoku sudoku = new Sudoku(sdkGrid);
						
						System.out.println("Parsing complete : ");
						sudoku.printSudoku();
						
						System.out.println("\nExpose solution on the shell? (Y/N)");
						while(true){
							System.out.print(">>>>>>>> ");
							choiceStr = sc.nextLine();
							if(choiceStr.length() == 0 || (choiceStr.charAt(0) != 'y' && choiceStr.charAt(0) != 'Y') ){
								continue analLoop;
							}else break;
						}
						
						System.out.println("Solution : ");
						
						sudoku.printSolution(true);
						
						System.out.println("\nHave another one to solve? (Y/N)");
					}
			}
		}
		
		sc.close();
		if(bw != null){
			bw.close();
		}
		System.out.println("Seems you need to go back to work, what a pity...");
		System.out.println("Anyway, thanks for playing!");
		System.out.println("(and what would be next...?)");
	}
}

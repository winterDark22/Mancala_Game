import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import static java.lang.System.clearProperty;
import static java.lang.System.exit;

public class Main {

    public static final int inf = (int) 1e9;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println("How do you want to play the game?\n 1.AI vs AI 2.Human vs AI 3. Autoplay");
            System.out.println("Exit(-1)");
            int x = scan.nextInt();

            //AI vs AI
            if (x == 1) {
                int h1, h2;
                System.out.print("Enter heuristic for player 1: ");
                h1 = scan.nextInt();
                System.out.print("Enter heuristic for player 2: ");
                h2 = scan.nextInt();

                int cntPlayer1 = 0;
                int cntPlayer2 = 0;

                for (int i = 0; i < 100; i++) {
                    State state = new State();
                    while (true) {
                        if (state.gameOver()) {

                            int[] remainingStones = state.checkSlots();
                            int[] finalScore = state.getFinalScore();
                            if(remainingStones[0] == 0)
                                finalScore[1] += remainingStones[1];
                            if(remainingStones[1] == 0)
                                finalScore[0] += remainingStones[0];

                            if (finalScore[0] > finalScore[1]) {
                                cntPlayer1++;
                            } else if (finalScore[0] < finalScore[1]) {
                                cntPlayer2++;
                            }
                            break;
                        }
                        if (state.curPlayer == 0) {
                            state.setHeuristic(h1);
                            state.miniMaxWithPruning(-inf, inf, 5);
                        } else {
                            state.setHeuristic(h2);
                            state.miniMaxWithPruning(-inf, inf, 5);
                        }
                        //state.miniMaxWithPruning(-inf, inf, 10);
                        int childNo = state.nextState();
                        state = state.childs[childNo];
                        if (state == null) {
                            System.out.println("Error");
                            exit(0);
                        }
//                        System.out.println(childNo + 1);
                    }
                }
                int draw = 100 - cntPlayer1 - cntPlayer2;
                System.out.println("RESULT: Player 1 : " + cntPlayer1 + "\n\t\tPlayer 2: " + cntPlayer2 + "\n\t\tDraw : " + draw);
            }

            //Human vs AI
            else if (x == 2) {
                State state = new State();
                while (true) {
                    state.printBoard();
                    if (state.gameOver()) {
                        System.out.println("Game Over");

                        int[] remainingStones = state.checkSlots();
                        int[] finalScore = state.getFinalScore();
                        if(remainingStones[0] == 0)
                            finalScore[1] += remainingStones[1];
                        if(remainingStones[1] == 0)
                            finalScore[0] += remainingStones[0];
//                        int[] finalScore = state.getFinalScore();
                        if (finalScore[0] > finalScore[1]) {
                            System.out.println("Winner: Player 1");
                        } else {
                            System.out.println("Winner: Player 2");
                        }
                        System.out.println("Your Score: " + finalScore[0]);
                        System.out.println("Computer's Score: " + finalScore[1]);
                        break;
                    }

                    if (state.curPlayer == 0) {
                        int slotNo;
                        while (true) {
                            System.out.println("Enter your move: ");
                            slotNo = scan.nextInt();
                            if (state.board[0][slotNo - 1] > 0)
                                break;
                            System.out.println("Not a valid move");
                        }

                        state = state.move(slotNo - 1);
                    } else {
                        System.out.println("Computer's turn: ");
                        state.miniMaxWithPruning(-inf, inf, 10);
                        int childNo = state.nextState();
                        state = state.childs[childNo];
                        if (state == null) {
                            System.out.println("Error");
                            exit(0);
                        }
                        System.out.println(childNo + 1);
                    }
                }
            }

            //Autoplay
            else if(x == 3) {
                System.out.print("Set heuristic: "); //for AI
                int h = scan.nextInt();
                System.out.print("Set depth: ");
                int d = scan.nextInt();

                int cntPlayer1 = 0;
                int cntPlayer2 = 0;

                for(int i=0; i<100; i++) {
                    State state = new State();
                    while (true) {
//                        state.printBoard();
                        if (state.gameOver()) {
//                            System.out.println("Game Over");
                            //int[] finalScore = state.getFinalScore();

                            int[] remainingStones = state.checkSlots();
                            int[] finalScore = state.getFinalScore();
                            if(remainingStones[0] == 0)
                                finalScore[1] += remainingStones[1];
                            if(remainingStones[1] == 0)
                                finalScore[0] += remainingStones[0];
                            if (finalScore[0] > finalScore[1]) {
                                cntPlayer1++;
                            }
                            else if (finalScore[0] < finalScore[1]){
                                cntPlayer2++;
                            }
                            break;
                        }

                        if (state.curPlayer == 0) {    //user chooses the moves randomly
                            int slotNo;
                            ArrayList<Integer> list = new ArrayList<>();
                            for(int j=1; j<=6; j++) list.add(j);
                            while (true) {
                                Collections.shuffle(list);
                                slotNo = list.get(0);
                                if(state.board[0][slotNo - 1] > 0)
                                break;
                            }
                            state = state.move(slotNo - 1);
                        } else {
//                            System.out.println("Computer's turn: ");
                            state.setHeuristic(h);
                            state.miniMaxWithPruning(-inf, inf, d);
                            int childNo = state.nextState();
                            state = state.childs[childNo];
                            if (state == null) {
                                System.out.println("Error");
                                exit(0);
                            }
//                            System.out.println(childNo + 1);
                        }
                    }
                }
                int draw = 100 - cntPlayer1 - cntPlayer2;
                System.out.println("RESULT: Player 1 : " + cntPlayer1 + "\n\t\tPlayer 2: " + cntPlayer2 + "\n\t\tDraw : " + draw);

            }
            else
                break;
        }

    }

}

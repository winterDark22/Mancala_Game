import java.lang.reflect.Array;
import java.security.PublicKey;
import java.util.*;

public class State {

    int board[][];

    int curPlayer;
    int nextPlayer;
    int currentValue;
    boolean freeMove;       //counts the number of free move gained till now

    int stoneCaptured;

    int heuristic;
    State[] childs;

    int W[] = {0, 12, 25, 45, 64, 75};

    public State() {
        initBoard();
        curPlayer = 0;
        freeMove = false;
        heuristic = 2;            //default setting of heuristic
        stoneCaptured = 0;
        childs = new State[6];
    }

    public State(int[][] board, int curPlayer, boolean freeMove, int stoneCaptured) {
        this.board = board;
        this.curPlayer = curPlayer;
        this.freeMove = freeMove;
        this.stoneCaptured = stoneCaptured;
        childs = new State[6];
    }

    public void initBoard() {
        board = new int[2][7];
        for(int i=0; i<2; i++) {
            for (int j = 0; j < 7; j++) {
                board[i][j] = 4;
            }
        }
        board[0][6] = 0;         // Storage of player-0 , user
        board[1][6] = 0;         // Storage of player-1 , computer
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    public void setInitValue() {
        if(curPlayer == 0) {
            currentValue = -Main.inf;
        }
        else {
            currentValue = Main.inf;
        }
    }

    public int[] checkSlots() {
        int[] stones = new int[2];
        for(int i=0; i<2; i++) {
            for(int j=0; j<6; j++) {
                stones[i] += board[i][j];
            }
        }
        return stones;
    }

    boolean gameOver() {
        int[] stones = checkSlots();
        if(stones[curPlayer] == 0 || stones[1-curPlayer] == 0)
            return true;
        return false;
    }


    int miniMaxWithPruning(int alpha, int beta, int depth) {
        if(gameOver()) {
            int[] stones = checkSlots();
            if(stones[curPlayer] == 0) {
                board[1 - curPlayer][6] += stones[1 - curPlayer];
                for(int i=0; i<6; i++) board[1-curPlayer][i] = 0;
            }
            else if(stones[1-curPlayer] == 0) {
                board[curPlayer][6] += stones[curPlayer];
                for(int i=0; i<6; i++) board[curPlayer][i] = 0;
            }
            return heuristicValue();
        }
        if(depth == 0) {
            return heuristicValue();
        }
        setInitValue();
        ArrayList<Integer> indexes = new ArrayList<>();
        for(int i=0; i<6; i++) indexes.add(i);
        Collections.shuffle(indexes);
        for(int i: indexes) {
//            System.out.println(i);
            if(board[curPlayer][i] == 0)    //slot is empty, no move
                continue;


            State child = move(i);
            child.heuristic = this.heuristic;
            childs[i] = child;
            int childValue = child.miniMaxWithPruning(alpha, beta, depth-1);
            if(curPlayer == 0) {
                if(childValue > currentValue)
                    currentValue = childValue;
                if(childValue > alpha)
                    alpha = childValue;
            }
            else {
                if(childValue < currentValue)
                    currentValue = childValue;
                if(childValue < beta)
                    beta = childValue;
            }
            if(alpha >= beta)          //pruning
                break;
        }
        return currentValue;
    }

    public State move(int slotNo) {
        // this function will return a board with new move and also indicate about freeMoveAvailable

        int[][] newboard = new int[2][7];
        for(int i=0; i<2; i++) {
            for(int j=0; j<7; j++)
                newboard[i][j] = board[i][j];
        }
        int whichPlayer = curPlayer;
        int stone = newboard[curPlayer][slotNo];
        newboard[curPlayer][slotNo] -= stone;
        slotNo++;
        nextPlayer = 1 - curPlayer;         //assume no freemove
        int childStoneCaptured = 0;

        while(stone > 0) {
            if(slotNo == 7) {
                whichPlayer = 1 - whichPlayer;
                slotNo = 0;
            }
            if(slotNo == 6 && whichPlayer != curPlayer) {
                slotNo++;
                continue;
            }
            if(whichPlayer == curPlayer && stone == 1 && (slotNo >= 0 && slotNo < 6) && (newboard[whichPlayer][slotNo] == 0 && newboard[1-whichPlayer][5-slotNo] > 0)) {
                childStoneCaptured = 1 + newboard[1-whichPlayer][5-slotNo];
                newboard[whichPlayer][6] += childStoneCaptured;
                newboard[1-whichPlayer][5-slotNo] = 0;
                break;
            }

            newboard[whichPlayer][slotNo++]++;
            stone--;

            if(stone == 0 && whichPlayer == curPlayer && slotNo == 7) {  //freemove
                freeMove = true;
                nextPlayer = curPlayer;    //nextplayer will be same as currPlayer
            }
        }
        return new State(newboard, nextPlayer, freeMove, childStoneCaptured);
    }

    public int nextState() {
        for(int i=0; i<6; i++) {
            if(childs[i] == null) continue;
            if(childs[i].getCurrentValue() == currentValue)
                return i;
        }
        return -1;
    }

    public void printBoard() {
        System.out.print("\t\t\t");
        for(int i=5; i>=0; i--){
            System.out.print(board[1][i] + "   ");
        }
        System.out.print("\n  " + board[1][6] + "\t\t\t\t\t\t\t\t\t\t" + board[0][6] + "\n\t\t\t");
        for(int i=0; i<6; i++){
            System.out.print(board[0][i] + "   ");
        }
        System.out.println("\n");
    }

    //ALL the heuristic here
    public int heuristicValue(){
        int hVal = -1;
        if(heuristic == 1) hVal = h1();
        else if(heuristic == 2) hVal = h2();
        else if(heuristic == 3) hVal = h3();
        else if(heuristic == 4) hVal = h4();
       currentValue = hVal;
       return hVal;
    }


    int h1() {
        return board[0][6] - board[1][6];
    }

    int h2() {
        int stoneMine = 0;
        int stoneOpponent = 0;
        for(int i=0; i<6; i++) {
            stoneMine += board[0][i];
            stoneOpponent += board[1][i];
        }
        int x = W[1] * h1() + W[2] * (stoneMine - stoneOpponent);
        return x;
    }

    int h3() {
        int x = h2();
        if(freeMove)
            return x + W[3];
        else
            return x;
    }
    int h4() {
        return h3() + W[4] * stoneCaptured;
    }

    public int[] getFinalScore() {
        int[] score = new int[2];
        score[0] = board[0][6];
        score[1] = board[1][6];
        return score;
    }

    public int[][] getBoard() {
        return board;
    }
    public int getCurPlayer() {
        return curPlayer;
    }
    public int getCurrentValue() {
        return this.currentValue;
    }
}

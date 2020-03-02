package sk.tuke.gamestudio.game.backgammon.lukac.core;

import java.util.ArrayList;
import java.util.Random;


public class Field extends Tile {

    private final int WHITE_TAKEN_AWAY = 26, BLACK_TAKEN_AWAY = 27, WHITE_FINISH = 25, BLACK_FINISH = 0;

    private Colour[] colours;
    private int[] count;
    private Colour currentPlayer;
    private ArrayList<Integer> dices = new ArrayList<>();
    private int score;
    //private ArrayList<Integer[]> validMoves = new ArrayList<>();

    public Field(){
        createEasy();
        score = 200;
    }

    private void createEasy(){
        throwDices();

        currentPlayer = Colour.BLACK;

        colours = new Colour[28];
        count = new int[28];

        //create
        for (int i=0; i < 28; i++){
            switch (i){
                case 1:
                    colours[i] = Colour.BLACK;
                    count[i] = 5;
                    break;
                case 2:
                    colours[i] = Colour.BLACK;
                    count[i] = 5;
                    break;
                case 3:
                    colours[i] = Colour.BLACK;
                    count[i] = 5;
                    break;
                case 22:
                    colours[i] = Colour.WHITE;
                    count[i] = 5;
                    break;
                case 23:
                    colours[i] = Colour.WHITE;
                    count[i] = 5;
                    break;
                case 24:
                    colours[i] = Colour.WHITE;
                    count[i] = 5;
                    break;
                default:
                    colours[i] = Colour.NONE;
                    count[i] = 0;

            }
        }

    }


    public void create(){
        //0-23 fields on the board
        //24,25 for taken away (white,black)
        //26,27 finished stones

        //28

        throwDices();

        currentPlayer = Colour.BLACK;

        colours = new Colour[28];
        count = new int[28];

        //create
        for (int i=0; i < 28; i++){
            switch (i){
                case 1:
                    colours[i] = Colour.WHITE;
                    count[i] = 2;
                    break;
                case 6:
                    colours[i] = Colour.BLACK;
                    count[i] = 5;
                    break;
                case 8:
                    colours[i] = Colour.BLACK;
                    count[i] = 3;
                    break;
                case 12:
                    colours[i] = Colour.WHITE;
                    count[i] = 5;
                    break;
                case 13:
                    colours[i] = Colour.BLACK;
                    count[i] = 5;
                    break;
                case 17:
                    colours[i] = Colour.WHITE;
                    count[i] = 3;
                    break;
                case 19:
                    colours[i] = Colour.WHITE;
                    count[i] = 5;
                    break;
                case 24:
                    colours[i] = Colour.BLACK;
                    count[i] = 2;
                    break;
                case WHITE_TAKEN_AWAY:
                    colours[i] = Colour.WHITE;
                    count[i] = 0;
                    break;
                case BLACK_TAKEN_AWAY:
                    colours[i] = Colour.BLACK;
                    count[i] = 0;
                    break;
                case BLACK_FINISH:
                    colours[i] = Colour.BLACK;
                    count[i] = 0;
                    break;
                case WHITE_FINISH:
                    colours[i] = Colour.WHITE;
                    count[i] = 0;
                    break;
                default:
                    colours[i] = Colour.NONE;
                    count[i] = 0;

            }
        }


    }


    public Colour[] getColour(){
        return colours;
    }

    public int[] getCount(){
        return count;
    }

    public int getScore() { return score; }

    public void throwDices(){

        dices.clear();

        Random random = new Random();

        int dice1 = random.nextInt(6) + 1;
        int dice2 = random.nextInt(6) + 1;

        if (dice1 == dice2) {
            dices.add(dice1);
            dices.add(dice1);
            dices.add(dice1);
            dices.add(dice1);
        }

        if (dice1 != dice2) {
            dices.add(dice1);
            dices.add(dice2);
        }

    }

    public ArrayList<Integer> getDices(){
        return dices;
    }


    public void moveStone(int start,int end){
        if (isMovePossible(start,end)){

            //full tile
            if(count[end] > 0 && colours[start] == colours[end]){
                if(count[start] == 0) colours[start] = Colour.NONE;
                count[start]--;
                count[end]++;
            }

            //empty tile
            if(count[end] == 0){
                colours[end] = colours[start];
                count[start]--;
                count[end]++;
//                if(count[start] == 0) colours[start] = Colour.NONE;
            }

            //capture
            if(count[end] == 1 && colours[start] != colours[end]){
                colours[end] = colours[start];
                if(colours[end] == Colour.WHITE) count[BLACK_TAKEN_AWAY]++;
                else if (colours[end] == Colour.BLACK) count[WHITE_TAKEN_AWAY]++;
                count[start]--;
//                if(count[start] == 0) colours[start] = Colour.NONE;
            }

            if(end == 0 || end == 25) score+=10;

            if(start == 26) start = 0;
            if(start == 27) start = 25;

            for(int i=0;i<dices.size();i++){
                if(dices.get(i) == Math.abs(start-end)){
                    dices.remove(i);
                    break;
                }
            }

            if(end == 25 || end == 0){
                for(int i=0; i<dices.size();i++){
                    if(dices.get(i) >= Math.abs(start-end)){
                        dices.remove(i);
                        break;
                    }
                }
            }

            if(count[start] == 0) colours[start] = Colour.NONE;


            score-=1;

        } else System.out.println("Pohyb je neplatny.");
    }

    public void setPlayer(){
        if (currentPlayer == Colour.BLACK) currentPlayer = Colour.WHITE;
        else currentPlayer = Colour.BLACK;
    }

    public Colour getPlayer(){
        /*if (currentPlayer == Colour.BLACK) return GameState.BLACK_PLAYING;
        else return GameState.WHITE_PLAYING;*/
        return currentPlayer;
    }

    public GameState isSolved(){
        if (count[0] == 15){
            return GameState.BLACK_WIN;
        } else if (count[25] == 15) return GameState.WHITE_WIN;
        if (currentPlayer == Colour.BLACK) return GameState.BLACK_PLAYING;
        else return GameState.WHITE_PLAYING;
    }

    private boolean allAtHome(Colour player){
        int counter = 0;

        //19-25 for white
         if (player == Colour.WHITE){
            for (int i = 19; i < 26; i++){
                if(colours[i] == Colour.WHITE) counter += count[i];
            }
            return (counter == 15);
        }

        //0-6 for black
        if (player == Colour.BLACK){
            for (int i = 0; i < 7; i++){
                if(colours[i] == Colour.BLACK) counter += count[i];
            }
            return (counter == 15);
        }

        return false;
    }

    /*private GameState getGameState(){
        if(currentPlayer == Colour.WHITE) return GameState.WHITE_PLAYING;
        if(count[0] == 15) return GameState.WHITE_WIN;
        if(count[25] == 15) return GameState.BLACK_WIN;
        return GameState.BLACK_PLAYING;
    }*/

    public boolean isMovePossible(int start, int end){

        Colour player = colours[start];
        Colour possition = colours[end];

        if(player == Colour.WHITE && start > end && start != 26) return false;
        if(player == Colour.BLACK && end > start) return false;
        if(start > 27 || start == 0) return false;
        if(getTileState(this,start) == TileState.EMPTY) return false;
        if(end > 25) return false;

//      if((count[WHITE_TAKEN_AWAY] != 0 || count[BLACK_TAKEN_AWAY] != 0) && start < 26 ) return false;
        if(count[WHITE_TAKEN_AWAY] != 0 && start < 26 && player == Colour.WHITE) return false;
        if(count[BLACK_TAKEN_AWAY] != 0 && start < 26 && player == Colour.BLACK) return false;

        if((end == 25 || end == 0) && !allAtHome(currentPlayer)) return false;
        if(player != possition && getTileState(this,end) == TileState.MORE_STONES) return false;
        if(currentPlayer != player) return false;

        if(start == 26) start=0;
        if(start == 27) start=25;

        //if(end != 0 && end != 25) {
            for (Integer dice : dices) {
                if (dice == Math.abs(end - start)) return true;
            }
//        }else{
//            for(Integer dice : dices){
//                if (dice >= Math.abs(end-start)) return true;
//            }
//        }


        return false;
    }

    /*private TileState getTileState(int x){
        if(count[x] == 0) return TileState.EMPTY;
        if(count[x] == 1) return TileState.ONE_STONE;
        return TileState.MORE_STONES;
    }*/

    public boolean validMoves(){
        //prechadzat polom cisel 1-27 a kontrolovat ci tam je nejaky mozny pohyb
        //2 specialne pripady pre WTA BTA kontrolovat ich ako prve ak tam je nieco musi ist z tohto miesta ak to nejde hned vracat false

        if(getTileState(this,26) != TileState.EMPTY/*count[26] != 0*/ && currentPlayer == Colour.WHITE) {
            for (Integer dice : dices) {
                if (isMovePossible(26,dice)) return true;
            }
            return false;
        }

        if(getTileState(this,27) != TileState.EMPTY /*count[27] != 0*/ && currentPlayer == Colour.BLACK){
            for(Integer dice: dices){
                if (isMovePossible(27,25-dice)) return true;
            }
            return false;
        }

        //end black
//        if(allAtHome(Colour.BLACK) && currentPlayer == Colour.BLACK) {
//            for(int i = 1; i<7;i++) {
//                for (Integer dice : dices) {
//                    if(i-dice < 0){
//                        System.out.println("ok");
//                        if(isMovePossible(i,0)) return true;
//                    }
//                }
//            }
//        }

        //end white
//        if(allAtHome(Colour.WHITE)) {
//            for (int i = 19; i < 25; i++) {
//                for(Integer dice: dices){
//                    if(i+dice > 25){
//                        System.out.println("hehe");
//                        int x=25;
//                        if(currentPlayer == Colour.WHITE && isMovePossible(i,x)) return true;
//                        System.out.println("no");
//                    }
//                }
//            }
//        }

        for(int i=1; i<25; i++){
            for(Integer dice: dices){
                if(i+dice < 28 && currentPlayer == Colour.WHITE && isMovePossible(i,i+dice)) return true;
                if(i-dice >= 0 && currentPlayer == Colour.BLACK && isMovePossible(i,i-dice)) return true;
            }
        }

        return false;
    }


    public void easyMove(){

        for(int i=1; i<25; i++){
            for(Integer dice: dices){
                if(i+dice < 28 && currentPlayer == Colour.WHITE && isMovePossible(i,i+dice)) {
                    moveStone(i,i+dice);
                    return;
                }
//                if(i-dice >= 0 && currentPlayer == Colour.BLACK && isMovePossible(i,i-dice)) {
//                    moveStone(i,i-dice);
//                    return;
//                }
            }
        }

        if(getTileState(this,26) != TileState.EMPTY /*count[26] != 0*/ && currentPlayer == Colour.WHITE) {
            for (Integer dice : dices) {
                if (isMovePossible(26,dice)){
                    moveStone(26,dice);
                    return;
                }
            }
        }

//        if(getTileState(this,27) != TileState.EMPTY /*count[27] != 0*/ && currentPlayer == Colour.BLACK){
//            for(Integer dice: dices){
//                if (isMovePossible(27,25-dice)){
//                    moveStone(27,25-dice);
//                    return;
//                }
//            }
//        }




    }

    //public void mediumMove(){}

    //public void hardMove(){}

}

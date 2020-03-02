package sk.tuke.gamestudio.game.backgammon.lukac.consoleui;


import org.springframework.beans.factory.annotation.Autowired;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.backgammon.lukac.core.Colour;
import sk.tuke.gamestudio.game.backgammon.lukac.core.Field;
import sk.tuke.gamestudio.game.backgammon.lukac.core.GameState;
import sk.tuke.gamestudio.service.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleUI {

    private static final String GAME_NAME = "backgammon";

    private Field field;
    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private boolean multiplayer = false;

    private final Pattern INPUT_PATTERN_move = Pattern.compile("([1-9]|1[0-9]|2[0-7])([\\s]+)([0-9]|1[0-9]|2[0-7])");
    private final Pattern INPUT_PATTERN_players = Pattern.compile("([12])");
    private final Pattern INPUT_PATTERN_difficulty = Pattern.compile(("[123]"));

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;

    private String name;
    private String string = "";

    private String readLine(){
        try {
            return br.readLine();
        } catch (IOException e){
            System.err.println("Bad input, try again");
            return "";
        }
    }

    public void startGame() {

        while (true) {
            play();

            handleName();

            while(!string.equals("N")){
                menu();
//                if(string.equals("N")) break;
            }
        }

    }


    private void play(){

        field = new Field();

        System.out.println("BACKGAMMON:");
        singleOrMultiplayer();
        //singleplayer
        if(!multiplayer){
            do {
                field.throwDices();
                field.setPlayer();
                move();
                field.setPlayer();
                field.throwDices();
                while (field.getDices().size() > 0) {
                    show();
                    if (!field.validMoves() && field.getDices().size() > 0) {
                        System.out.println("No possible moves for: " + field.getPlayer());
                        break;
                    }
                    if(field.isSolved() == GameState.WHITE_WIN || field.isSolved() == GameState.BLACK_WIN) break;
                    field.easyMove();
//                    move();
                    if (!field.validMoves() && field.getDices().size() > 0) {
                        System.out.println("No possible moves for: " + field.getPlayer());
                        break;
                    }
                }
            } while (field.isSolved() == GameState.BLACK_PLAYING || field.isSolved() == GameState.WHITE_PLAYING);

            show();

            if (field.isSolved() == GameState.BLACK_WIN) System.out.println("Black won");
            else if (field.isSolved() == GameState.WHITE_WIN) System.out.println("White won");
        }

        //multiplayer
        if(multiplayer) {
            do {
                move();
                field.setPlayer();
                field.throwDices();
            } while (field.isSolved() == GameState.BLACK_PLAYING || field.isSolved() == GameState.WHITE_PLAYING);

            show();

            if (field.isSolved() == GameState.BLACK_WIN) System.out.println("Black won");
            else if (field.isSolved() == GameState.WHITE_WIN) System.out.println("White won");

        }

    }

    private void move(){
        while (field.getDices().size() > 0) {
            show();
            if (!field.validMoves() && field.getDices().size() > 0) {
                System.out.println("No possible moves for: " + field.getPlayer());
                break;
            }
            handleInput();
            if (!field.validMoves() && field.getDices().size() > 0) {
                System.out.println("No possible moves for: " + field.getPlayer());
                break;
            }
        }
    }


    private void show(){

        StringBuilder string = new StringBuilder();
        ArrayList<Integer> dices = field.getDices();

        string.append("-12--11--10---9---8---7-----26----6---5---4---3---2---1-----0----\n");

        for(int i=7; i>0; i--) {
            stoneTop(i,field,string);
            string.append("|\n");
        }

        string.append("=================================================================\n");

        for(int i=7; i>0; i--) {
            stoneBottom(i,field,string);
            string.append("|\n");
        }

        string.append("-13--14--15--16--17--18-----27---19--20--21--22--23--24----25----\n");

        System.out.println(string);

        System.out.print(field.getPlayer()+"'s turn");
        if(field.getPlayer() == Colour.BLACK) System.out.println(" #");
        else System.out.println(" $");

        System.out.print("Dices: ");
        for (Integer dice : dices) {
            System.out.print(dice + " ");
        }
        System.out.println();

    }

    private void stoneBottom(int i, Field field, StringBuilder string){

        //left
        for (int j=13;j<19;j++){
            string.append("| ");
            drawStone(j,i,field,string);
            string.append(" ");
        }

        //middle
        string.append("||  ");
        drawStone(27,i,field,string);
        string.append("  |");

        //right
        for (int j=19;j<25;j++){
            string.append("| ");
            drawStone(j,i,field,string);
            string.append(" ");
        }

        //home
        string.append("||  ");
        drawStone(25,i,field,string);
        string.append("  |");
    }

    private void stoneTop(int i, Field field, StringBuilder string){

        //left
        for(int j=12;j>6;j--){
            string.append("| ");
            drawStone(j,i,field,string);
            string.append(" ");
        }

        //middle
        string.append("||  ");
        drawStone(26,i,field,string);
        string.append("  |");

        //right
        for (int j=6;j>0;j--){
            string.append("| ");
            drawStone(j,i,field,string);
            string.append(" ");
        }

        //home
        string.append("||  ");
        drawStone(0, i, field, string);
        string.append("  |");

    }

    private void drawStone(int j, int i, Field field, StringBuilder string){
        int[] count = field.getCount();
        Colour[] colour = field.getColour();

        if(count[j] >= i){
            if(count[j] > 7 && i==7){
                string.append(count[j]-6);
            }else if(colour[j] == Colour.BLACK){
                string.append("#");
            }else if (colour[j] == Colour.WHITE){
                string.append("o");
            }
        }else string.append(" ");
    }


    private void handleInput(){

        System.out.println("Type input (start end):");

        String line = readLine();

        if(line.equals("X")){
            System.exit(0);
        }

        Matcher m = INPUT_PATTERN_move.matcher(line);

        if (m.matches()) {
            field.moveStone(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(3)));
        } else {
            System.out.println("Bad input, try again");
        }

    }

    /*public void chooseDiffuculty(){
        System.out.println("Choose difficulty Easy(E), Medium(M), Hard(H): ");
    }*/

    private void handleName(){
        System.out.println("Type your name: ");

        String line = readLine();
        name = line;

        Score score = new Score(GAME_NAME, line, 99, new java.util.Date());
        scoreService.addScore(score);

    }

    private void handleComment(){
        System.out.println("Type your comment: ");

        String line = readLine();

        Comment comment = new Comment(name,GAME_NAME,line,new java.util.Date());
        try {
            commentService.addComment(comment);
        } catch (CommentException e) {
            e.printStackTrace();
        }

        //menu();
    }

    private void handleRating(){
        System.out.println("Type your rating: ");

        String line = readLine();
        int rate = Integer.parseInt(line);

        Rating rating = new Rating(GAME_NAME,name,rate,new java.util.Date());

        try {
            ratingService.setRating(rating);
        } catch (RatingException e) {
            e.printStackTrace();
        }
    }

    private void menu() {

        //System.out.println("Your score: "+score);
        System.out.println();
        System.out.println("MENU: ");
        System.out.println("Top 10 <T>");
        System.out.println("Comments <C>");
        System.out.println("Average rating <A>");
        System.out.println();
        System.out.println("Add comment <K>");
        System.out.println("Add rating <G>");
        System.out.println();
        System.out.println("New Game <N>");
        System.out.println("Exit <X>");



        this.string = readLine();

        if (string.equalsIgnoreCase("A")){
            printAverageRating();
        }
        if (string.equalsIgnoreCase("T")){
            printScores();
        }
        if (string.equalsIgnoreCase("C")){
            printComments();
        }
        if(string.equalsIgnoreCase("X")){
            System.exit(0);
        }
        if(string.equalsIgnoreCase("K")){
            handleComment();
        }
        if(string.equalsIgnoreCase("G")){
            handleRating();
        }

    }


    private void singleOrMultiplayer(){

        System.out.println("Singleplayer <1> ");
        System.out.println("Multiplayer <2> ");

        String line = readLine();

        if(line.equalsIgnoreCase("X")){
            System.exit(0);
        }

        Matcher m = INPUT_PATTERN_players.matcher(line);

        if (m.matches()) {
            if (Integer.parseInt(m.group(1)) == 1) multiplayer = false;
            else multiplayer = true;
        }
//        if(m.group(1).equals("A"));

    }

    private void printScores(){
        List<Score> scores = scoreService.getBestScores(GAME_NAME);

        System.out.println("Top 10:");
        for (Score s: scores){
            System.out.println(s);
        }

        //menu();
    }

    private void printComments(){
        List<Comment> comments = null;
        try {
            comments = commentService.getComments(GAME_NAME);
        } catch (CommentException e) {
            e.printStackTrace();
        }
        System.out.println("Comments:");
        for (Comment c: comments){
            System.out.println(c);
        }

        //menu();
    }


    private void printAverageRating(){
        int averateRating = 0;

        try {
            averateRating = ratingService.getAverageRating(GAME_NAME);
        } catch (RatingException e) {
            e.printStackTrace();
        }
        System.out.println("Average Rating: ");
        System.out.println(averateRating);
    }

}

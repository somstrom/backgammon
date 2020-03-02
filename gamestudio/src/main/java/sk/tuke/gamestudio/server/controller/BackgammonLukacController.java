package sk.tuke.gamestudio.server.controller;

import ch.qos.logback.core.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.backgammon.lukac.core.Colour;
import sk.tuke.gamestudio.game.backgammon.lukac.core.Field;
import sk.tuke.gamestudio.game.backgammon.lukac.core.GameState;
import sk.tuke.gamestudio.service.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


//http://localhost:8080/backgammon-lukac
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
@RequestMapping("/backgammon-lukac")
public class BackgammonLukacController {
    private static Map<Integer, Field> fields = new HashMap<>();

    @Autowired
    private ServletContext servletContext;

    private Field field;

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;

    private int start = -1;
    private int count;
    private int columnIndex;
    private String loggedUser;
    private int displayId;
    private int mode = 1;
    private boolean stop=true;

    @RequestMapping
    public String backgammon(String column, Model model){

        if (field == null){
            createField();
        }
        if (mode == 1){
            singleP(column);
        }
        updateModel(model);
        return "backgammon-lukac";
    }


    private void singleP(String column){
        try{
            if(column != null && (field.isSolved() == GameState.BLACK_PLAYING /*|| field.isSolved() == GameState.WHITE_PLAYING*/)){
                if(start != -1 && start == Integer.parseInt(column)){}
                else {
                    if(field.validMoves()) {
//                        if (start == Integer.parseInt(column)) start = -1;

                        if(start != -1 && !field.isMovePossible(start,Integer.parseInt(column))){
                            start = Integer.parseInt(column);
                        }

                        if (start != -1) {
                            field.moveStone(start, Integer.parseInt(column));
//
                            start = -1;
                        } else start = Integer.parseInt(column);

                        if (field.getDices().size() == 0 || !field.validMoves()) {
                            field.setPlayer();
                            field.throwDices();
                            System.out.println("HERE"+ field.getDices().size()+" "+field.validMoves());
                        }
                    }else{
                        field.setPlayer();
                        field.throwDices();
                        System.out.println("no valid moves");
                    }
                }
            }else{
                if(field.isSolved() == GameState.BLACK_WIN) {
                    //vykreslit black win
                    hallOfFame();
                }
                if(field.isSolved() == GameState.WHITE_WIN) {
                    //vykreslit black win
                    hallOfFame();
                }
            }
            if(field.isSolved() == GameState.WHITE_PLAYING){
                while(field.getDices().size() > 0 && field.validMoves()){
                    field.easyMove();
                    renderAsHtml();
                    if(field.isSolved() != GameState.WHITE_PLAYING) break;
                }
                field.setPlayer();
                field.throwDices();
            }

        } catch(NumberFormatException e/*| IOException | ScriptException | NoSuchMethodException e*/) {
            e.printStackTrace();
        }
    }

    private void hallOfFame(){
        System.out.println(stop);
        if(stop && loggedUser != null) {
            Score score = new Score("backgammon", loggedUser, field.getScore(), new java.util.Date());
            scoreService.addScore(score);
            stop = false;
        }
    }


    private void fieldUp(StringBuilder sb){
        //field 0-12
        sb.append("<table class='field'>\n");
        for(int row = 0; row < 5; row++){
            sb.append("<tr>\n");
            for(int column = 12;column > 0; column--){
                sb.append("<td>\n");
                count = field.getCount()[column];
                if(count>5) count = 5;
                if(start != -1 && field.isMovePossible(start,column) && field.getColour()[column] == Colour.NONE && row == 0){
                    sb.append("<a href='" +
                            String.format("%s/backgammon-lukac?column=%s",servletContext.getContextPath(),column)
                            + "'>" + String.format("<img src='%s/images.backgammon/clear.png' class='end'></a >",servletContext.getContextPath()));
                }
                if(count > row) {
                    if (field.getColour()[column] != Colour.NONE) {
                        if((field.getColour()[column] == field.getPlayer() && row == count-1) || (start != -1 && field.isMovePossible(start,column) && count-1 == row)){
                            sb.append("<a href='" +
                                    String.format("%s/backgammon-lukac?column=%s",servletContext.getContextPath(),column)
                                    + "'>\n");
                        }
                        sb.append(String.format("<img src='%s/images.backgammon/",servletContext.getContextPath()) + getImage(field.getColour(), column));
                        if(field.getCount()[column]>5 && row == 4) sb.append(field.getCount()[column]-4+".png'"); else sb.append(".png'");
                        if(start != -1 && field.isMovePossible(start,column) && count-1 == row) {
                            sb.append("class='end'");
                        }
                        sb.append("></a>");
                    }
                }
                if(column == 7){
                    sb.append(String.format("<td><img src='%s/images.backgammon/clear.png'></td>\n",servletContext.getContextPath()));
                }
            }
        }
        sb.append("</table>\n");
    }

    private void blackHome(StringBuilder sb){
        sb.append(String.format("<a href='%s/backgammon-lukac?column=0'>",servletContext.getContextPath()));
        sb.append("<div");
        if(start != -1 && field.isMovePossible(start,0)){
            sb.append(" class='home'>\n");
        }else{
            sb.append(" class='nah'>\n");
        }
        if(field.getCount()[0] == 0) {
            sb.append(String.format("<img src='%s/images.backgammon/home-black0.png'>\n",servletContext.getContextPath()));
        }
        if(field.getCount()[0] > 0){
           sb.append(String.format("<img src='%s/images.backgammon/black-home"+field.getCount()[0]+".png'>\n",servletContext.getContextPath()));
        }
//        if(start != -1 && field.isMovePossible(start,0)){}

        sb.append("</div>");
        sb.append("</a>");
    }

    private void blackDices(StringBuilder sb){
        if(field.getPlayer() == Colour.BLACK) {
            sb.append("<table id='dices'>\n<tr id='dices-row'>\n");
            showDices(sb);
            sb.append("</tr>\n");
            sb.append("</table>\n");
        }
    }

    private void takenAway(StringBuilder sb){
        sb.append("<div class='relative-position'>");
        sb.append("<table id='taken-away'>\n<tr>\n");
        columnIndex=26;

        sb.append("<td>\n");
        if((field.getCount()[columnIndex]> 0) && field.getPlayer() == Colour.WHITE){
            sb.append("<a href='" +
                    String.format("%s/backgammon-lukac?column=%s",servletContext.getContextPath(),columnIndex)
                    + "'>\n");
        }
        if(field.getCount()[columnIndex]>0) {
            sb.append(String.format("<img src='%s/images.backgammon/white",servletContext.getContextPath()));

            if (field.getCount()[columnIndex] > 1) sb.append(field.getCount()[columnIndex] + ".png'>");
            else sb.append(".png'>");
        }else sb.append(String.format("<img src='%s/images.backgammon/clear.png'",servletContext.getContextPath()));
        sb.append("</a>");
        sb.append("</td>\n");

        sb.append("</tr>\n");

        sb.append("<tr>\n");

        columnIndex=27;

        sb.append("<td>\n");
        if((field.getCount()[columnIndex]> 0) && field.getPlayer() == Colour.BLACK){
            sb.append("<a href='" +
                    String.format("%s/backgammon-lukac?column=%s",servletContext.getContextPath(),columnIndex)
                    + "'>\n");
        }
        if(field.getCount()[columnIndex]>0) {
            sb.append(String.format("<img src='%s/images.backgammon/black",servletContext.getContextPath()));

            if (field.getCount()[columnIndex] > 1) sb.append(field.getCount()[columnIndex] + ".png'>");
            else sb.append(".png'>");
        }else sb.append(String.format("<img src='%s/images.backgammon/clear.png'",servletContext.getContextPath()));
        sb.append("</a>");
        sb.append("</td>\n");

        sb.append("</tr>\n</table>\n");
        sb.append("</div>");
    }

    private void whiteDices(StringBuilder sb){
        if(field.getPlayer() == Colour.WHITE) {
            sb.append("<table id='dices2'>\n<tr id='dices-row'>\n");
            showDices(sb);
            sb.append("</tr>\n");
            sb.append("</table>\n");
        }
    }

    private void fieldDown(StringBuilder sb){
        //13-24
        sb.append("<div class='flex'>");
        sb.append("<table class='field'>\n");
        for(int row = 5; row > 0; row--){
            sb.append("<tr>\n");
            for(int column = 13;column < 25; column++){
                sb.append("<td>\n");
                count = field.getCount()[column];
                if(count>5) count = 5;
                if(start != -1 && field.isMovePossible(start,column) && field.getColour()[column] == Colour.NONE && row == 1){
                    sb.append("<a href='" +
                            String.format("%s/backgammon-lukac?column=%s",servletContext.getContextPath(),column)
                            + String.format("'><img src='%s/images.backgammon/clear.png' class='end'></a >",servletContext.getContextPath()));
                }
                if(count >= row) {
                    if (field.getColour()[column] != Colour.NONE) {
                        if((field.getColour()[column] == field.getPlayer() && count==row) || (start != -1 && field.isMovePossible(start,column) && count == row)) {
                            sb.append("<a href='" +
                                    String.format("%s/backgammon-lukac?column=%s",servletContext.getContextPath(),column)
                                    + "'>\n");
                        }
                        sb.append(String.format("<img src='%s/images.backgammon/",servletContext.getContextPath()) + getImage(field.getColour(), column));
                        if(field.getCount()[column]>5 && row == 5) sb.append(field.getCount()[column]-4+".png'"); else sb.append(".png'");
                        if(start != -1 && field.isMovePossible(start,column) && count == row) {
                            sb.append("class='end'");
                        }
                        sb.append("></a>");
                    }
                }
                if(column == 18){
                    sb.append(String.format("<td><img src='%s/images.backgammon/clear.png'></td>\n",servletContext.getContextPath()));
                }
            }
        }

        sb.append("</table>\n");
    }

    private void whiteHome(StringBuilder sb){
        //white home
        sb.append(String.format("<a href='%s/backgammon-lukac?column=25'>",servletContext.getContextPath()));
        sb.append("<div class='home2'>\n");
//        if(field.getCount()[25] == 1) {
//            sb.append(String.format("<img src='%s/images.backgammon/white.png'>\n",servletContext.getContextPath()));
//        }
//        if(field.getCount()[25] > 1){
        sb.append(String.format("<img src='%s/images.backgammon/white-home"+field.getCount()[25]+".png'>\n",servletContext.getContextPath()));
//        }
        sb.append("</div>");
        sb.append("</a>");
    }

    private void table(StringBuilder sb){

        if(field.isSolved() == GameState.BLACK_WIN){
            hallOfFame();
            sb.append("<div class='table'>\n");
            sb.append(String.format("<img src='%s/images.backgammon/black-won.png'>\n",servletContext.getContextPath()));
            sb.append("<p class='score'>Your score: "+ field.getScore()+"</p>\n");
            sb.append(String.format(String.format("<a href='%s/backgammon-lukac/hof'>HOF</a>",servletContext.getContextPath())));
            sb.append("</div>\n");
        }
        else if(field.isSolved() == GameState.WHITE_WIN){
            hallOfFame();
            sb.append("<div class='table'>\n");
            sb.append(String.format("<img src='%s/images.backgammon/white-won.png'>\n",servletContext.getContextPath()));
            sb.append("<p class='score'>Your score: "+ field.getScore()+"</p>\n");
            sb.append(String.format(String.format("<a href='%s/backgammon-lukac/hof'>HOF</a>",servletContext.getContextPath())));
            sb.append("</div>\n");
        }
        else if(!field.validMoves()){
            sb.append("<div class='table'>\n");
            sb.append(String.format("<img src='%s/images.backgammon/table.png'\n>",servletContext.getContextPath()));
            sb.append(String.format(String.format("<a href='%s/backgammon-lukac?column=0' class='ok'>OK</a>",servletContext.getContextPath())));
            sb.append("</div>\n");
        }
    }

    public String renderAsHtml(){

        StringBuilder sb = new StringBuilder();

//        sb.append("<div class='container'>\n");
            sb.append("<div class='background'>\n");

                sb.append("<div class='flex'>\n");
                    fieldUp(sb);
                    blackHome(sb);
                sb.append("</div>\n");

                sb.append("<div class='flex'>\n");
                    blackDices(sb);
                    takenAway(sb);
                    whiteDices(sb);
                sb.append("</div>\n");

                sb.append("<div class='flex2'>\n");
                    fieldDown(sb);
                    whiteHome(sb);
                sb.append("</div>\n");

            sb.append("</div>\n");

                table(sb);



        return sb.toString();
    }


//    public String renderAsHtml() {
//        // The most basic (and outdated) method of creating game field representation
//        // is by composing a string containing all HTML code for field layout as a table.
//        // But be warned: dark is that path... Figure out better approach.
//
////        color = field.getColour();
//
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("<div class='container'>\n");
//
//        sb.append("<div class='background'>\n");
//
//        sb.append("<div class='flex'>");
//        //0 - 12//////////////////////////////////////////////////////////////////////////////////////////
//        sb.append("<table class='field'>\n");
//        for(int row = 0; row < 5; row++){
//            sb.append("<tr>\n");
//            for(int column = 12;column > 0; column--){
//                sb.append("<td>\n");
//                count = field.getCount()[column];
//                if(count>5) count = 5;
//                if(start != -1 && field.isMovePossible(start,column) && field.getColour()[column] == Colour.NONE && row == 0){
//                    sb.append("<a href='" +
//                            String.format("%s/backgammon-lukac?column=%s",servletContext.getContextPath(),column)
//                            + "'>" + String.format("<img src='%s/images.backgammon/clear.png' class='end'></a >",servletContext.getContextPath()));
//                }
//                if(count > row) {
//                    if (field.getColour()[column] != Colour.NONE) {
//                        if((field.getColour()[column] == field.getPlayer() && row == count-1) || (start != -1 && field.isMovePossible(start,column) && count-1 == row)){
//                            sb.append("<a href='" +
//                                    String.format("%s/backgammon-lukac?column=%s",servletContext.getContextPath(),column)
//                                    + "'>\n");
//                        }
//                        sb.append(String.format("<img src='%s/images.backgammon/",servletContext.getContextPath()) + getImage(field.getColour(), column));
//                        if(field.getCount()[column]>5 && row == 4) sb.append(field.getCount()[column]-4+".png'"); else sb.append(".png'");
//                        if(start != -1 && field.isMovePossible(start,column) && count-1 == row) {
//                            sb.append("class='end'");
//                        }
//                        sb.append("></a>");
//                    }
//                }
//                if(column == 7){
//                    sb.append(String.format("<td><img src='%s/images.backgammon/clear.png'></td>\n",servletContext.getContextPath()));
//                }
//            }
//        }
//
//        sb.append("</table>\n");
//
//        //black home//////////////////////////////////////////////////////////////////////////////////////////
//
//        sb.append(String.format("<a href='%s/backgammon-lukac?column=0'>",servletContext.getContextPath()));
//        sb.append("<div class='home'>\n");
//        if(field.getCount()[0] > 0) {
//            sb.append(String.format("<img src='%s/images.backgammon/black.png' class='end'>\n",servletContext.getContextPath()));
//        }
//        sb.append("</div>");
//        sb.append("</a>");
//
//        /*sb.append("<a href='/backgammon-lukac?column=0'>");
//        sb.append("<table class='home'>\n");
//        for(int index=15;index>0;index--){
//            sb.append("<tr>\n");
//            sb.append("<td>\n");
//            if(field.getCount()[0] >= index) {
//                sb.append("<img src='/images.backgammon/home-black.png' class='end'>\n");
//            }
////            }else{
////                sb.append("<img src='/images.backgammon/home-clear.png' class='end''>\n");
////            }
//            sb.append("</td>\n");
//            sb.append("</tr>\n");
//        }
//        sb.append("</a>");
//        sb.append("</table>\n");*/
//
//
//
//        sb.append("</div>");
//
//        sb.append("<div class='flex'>");
//        //dices black//////////////////////////////////////////////////////////////////////////////////////////
//        if(field.getPlayer() == Colour.BLACK) {
//            sb.append("<table id='dices'>\n<tr id='dices-row'>\n");
////            sb.append("<td><img src='/images.backgammon/clear.png'></td>");
//            showDices(sb);
//            sb.append("</tr>\n");
//            sb.append("</table>\n");
//        }
//
//
//        //26(white)-27(black)//////////////////////////////////////////////////////////////////////////////////////////
////        color = field.getColour();
//
//        sb.append("<div class='relative-position'>");
//        sb.append("<table id='taken-away'>\n<tr>\n");
//        columnIndex=26;
//
//        sb.append("<td>\n");
//        if((field.getCount()[columnIndex]> 0) && field.getPlayer() == Colour.WHITE){
//            sb.append("<a href='" +
//                    String.format("%s/backgammon-lukac?column=%s",servletContext.getContextPath(),columnIndex)
//                    + "'>\n");
//        }
//        if(field.getCount()[columnIndex]>0) {
//            sb.append(String.format("<img src='%s/images.backgammon/white",servletContext.getContextPath()));
//
//            if (field.getCount()[columnIndex] > 1) sb.append(field.getCount()[columnIndex] + ".png'>");
//            else sb.append(".png'>");
//        }else sb.append(String.format("<img src='%s/images.backgammon/clear.png'",servletContext.getContextPath()));
//        sb.append("</a>");
//        sb.append("</td>\n");
//
//        sb.append("</tr>\n");
//
//        sb.append("<tr>\n");
//
//        columnIndex=27;
//
//        sb.append("<td>\n");
//        if((field.getCount()[columnIndex]> 0) && field.getPlayer() == Colour.BLACK){
//            sb.append("<a href='" +
//                    String.format("%s/backgammon-lukac?column=%s",servletContext.getContextPath(),columnIndex)
//                    + "'>\n");
//        }
//        if(field.getCount()[columnIndex]>0) {
//            sb.append(String.format("<img src='%s/images.backgammon/black",servletContext.getContextPath()));
//
//            if (field.getCount()[columnIndex] > 1) sb.append(field.getCount()[columnIndex] + ".png'>");
//            else sb.append(".png'>");
//        }else sb.append(String.format("<img src='%s/images.backgammon/clear.png'",servletContext.getContextPath()));
//        sb.append("</a>");
//        sb.append("</td>\n");
//
//        sb.append("</tr>\n");
//
//
//        sb.append("</tr>\n</table>\n");
//        sb.append("</div>");
//        //dices white//////////////////////////////////////////////////////////////////////////////////////////
//
//        if(field.getPlayer() == Colour.WHITE) {
//            sb.append("<table id='dices2'>\n<tr id='dices-row'>\n");
////            sb.append("<td><img src='/images.backgammon/clear.png'></td>");
//            showDices(sb);
//            sb.append("</tr>\n");
//            sb.append("</table>\n");
//        }
//
//        sb.append("</div>");
//
//        //13-24//////////////////////////////////////////////////////////////////////////////////////////
//
////        color = field.getColour();
//
//        sb.append("<div class='flex'>");
//        sb.append("<table class='field'>\n");
//        for(int row = 5; row > 0; row--){
//            sb.append("<tr>\n");
//            for(int column = 13;column < 25; column++){
//                sb.append("<td>\n");
//                count = field.getCount()[column];
//                if(count>5) count = 5;
//                if(start != -1 && field.isMovePossible(start,column) && field.getColour()[column] == Colour.NONE && row == 1){
//                    sb.append("<a href='" +
//                            String.format("%s/backgammon-lukac?column=%s",servletContext.getContextPath(),column)
//                            + String.format("'><img src='%s/images.backgammon/clear.png' class='end'></a >",servletContext.getContextPath()));
//                }
//                if(count >= row) {
//                    if (field.getColour()[column] != Colour.NONE) {
//                        if((field.getColour()[column] == field.getPlayer() && count==row) || (start != -1 && field.isMovePossible(start,column) && count == row)) {
//                            sb.append("<a href='" +
//                                    String.format("%s/backgammon-lukac?column=%s",servletContext.getContextPath(),column)
//                                    + "'>\n");
//                        }
//                        sb.append(String.format("<img src='%s/images.backgammon/",servletContext.getContextPath()) + getImage(field.getColour(), column));
//                        if(field.getCount()[column]>5 && row == 5) sb.append(field.getCount()[column]-4+".png'"); else sb.append(".png'");
//                        if(start != -1 && field.isMovePossible(start,column) && count == row) {
//                            sb.append("class='end'");
//                        }
//                        sb.append("></a>");
//                    }
//                }
//                if(column == 18){
//                    sb.append(String.format("<td><img src='%s/images.backgammon/clear.png'></td>\n",servletContext.getContextPath()));
//                }
//            }
//        }
//
//        sb.append("</table>\n");
//
//        //white home
//        sb.append(String.format("<a href='%s/backgammon-lukac?column=25'>",servletContext.getContextPath()));
//        sb.append("<div class='home'>\n");
//        if(field.getCount()[25] > 0) {
//            sb.append(String.format("<img src='%s/images.backgammon/white.png' class='end'>\n",servletContext.getContextPath()));
//        }
//        sb.append("</div>");
//        sb.append("</a>");
//
//        sb.append("</div>\n");
//        sb.append("</div>\n");
//
////        sb.append("KOCKY:" +field.getDices());
//
////        for(int i=1;i<25;i++){
////            System.out.println(i+" "+field.getColour()[i]);
////        }
//
//        return sb.toString();
//    }

    private void createField(){
        field = new Field();
    }

    // TODO: add another methods as needed

    private String getImage(Colour[] color,int i){
        if (color[i] == Colour.BLACK) return "black";
        if (color[i] == Colour.WHITE) return "white";
        return "clear";
    }

    private void showDices(StringBuilder sb){
        for (int index = 0; index < field.getDices().size(); index++) {
            sb.append(String.format("<td><img src='%s/images.backgammon/dice",servletContext.getContextPath()) + field.getDices().get(index) + ".png'>");
        }

    }

    @RequestMapping("/new")
    public String newGame(Model model){
        stop = true;
        createField();
        updateModel(model);
        return "backgammon-lukac";
    }

    @RequestMapping("/new-view")
    public String newGameView(Model model){
        stop = true;
        createField();
        updateModel(model);
        return "backgammon-lukac-view";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/login-view")
    public String loginView(){
        return "login-view";
    }

//    @RequestMapping("/login")
//    public String log(String login, Model model){
//        loggedUser = login;
//        updateModel(model);
//        return "login";
//    }

    @RequestMapping("/play")
    public String start(String login,Model model){
        stop = true;
        createField();
        loggedUser = login;
        updateModel(model);
        return "backgammon-lukac";
    }

    @RequestMapping("/play-view")
    public String startView(String login,Model model){
        stop=true;
        createField();
        loggedUser = login;
        updateModel(model);
        return "backgammon-lukac-view";
    }

    @RequestMapping("/hof")
    public String hof(Model model){
        updateModel(model);
        return "hof";
    }

    @RequestMapping("/comments")
    public String comments(Model model){
        updateModel(model);
        return "comments-rating";
    }

    @RequestMapping("/addComment")
    public String comment(String comment,Model model){
        Comment comments = new Comment(loggedUser,"backgammon",comment,new java.util.Date());
        try {
            commentService.addComment(comments);
        } catch (CommentException e) {
            e.printStackTrace();
        }
        updateModel(model);
        return "comments-rating";
    }

    @RequestMapping("/setRating")
    public String setRating(String rate,Model model){
        Rating ratings = new Rating("backgammon",loggedUser,Integer.parseInt(rate),new java.util.Date());
        System.out.println(rate);
        try {
            ratingService.setRating(ratings);
        } catch (RatingException e) {
            e.printStackTrace();
        }
        updateModel(model);
        return "comments-rating";
    }

    @RequestMapping("/logout")
    public String logout(Model model) {
        loggedUser = null;
        updateModel(model);
        return "login";
    }

    @RequestMapping("/logout-view")
    public String logoutView(Model model){
        loggedUser = null;
        updateModel(model);
        return "login-view";
    }

    @RequestMapping("/display/{id}")
    public String display(@PathVariable int id, Model model) {
        displayId = id;
        Field field = fields.get(id);
        if (field != null)
            model.addAttribute("htmlField", renderAsHtml());
        updateModel(model);
        return "backgammon-lukac_wiev";
    }

    @RequestMapping("/display/pair/{id}")
    public String pair(@PathVariable int id, Model model) {
        displayId = id;
        if (field == null)
            createField();
        fields.put(id, field);
        updateModel(model);
        return "backgammon-lukac";
    }



    private void updateModel(Model model) {
//        model.addAttribute("displayId", displayId);
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("scores", scoreService.getBestScores("backgammon"));
        try {
            model.addAttribute("comments",commentService.getComments("backgammon"));
            model.addAttribute("rating",ratingService.getAverageRating("backgammon"));
//            model.addAttribute("rate",ratingService.getRating("backgammon",loggedUser));
        } catch (CommentException | RatingException e) {
            e.printStackTrace();
        }
    }




}

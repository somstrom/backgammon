package sk.tuke.gamestudio.game.backgammon.lukac.webui;

import sk.tuke.gamestudio.game.backgammon.lukac.core.Colour;
import sk.tuke.gamestudio.game.backgammon.lukac.core.Field;
import sk.tuke.gamestudio.service.ScoreService;

public class WebUI {

//    private Field field;
//    private Colour[] color;
//    private ScoreService scoreService;
//    private int start = -1;
//    private int count;
//    private int columnIndex;
//
//    public WebUI(ScoreService scoreService) {
//        this.scoreService = scoreService;
//        createField();
//        color = field.getColour();
//    }
//
//    public void processCommand(String command, String row, String column) {
//	//TODO: implement what should happen when a command comes (open, mark, newgame, ...)
//        try{
//
//            if(column != null) {
//                if (start == Integer.parseInt(column)) start = -1;
//
//                if (start != -1) {
//                    field.moveStone(start, Integer.parseInt(column));
//                    start = -1;
//                } else start = Integer.parseInt(column);
//
//                if (field.getDices().size() == 0 || !field.validMoves()) {
//                    field.setPlayer();
//                    field.throwDices();
//                }
//            }
//
//        } catch(NumberFormatException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    // TODO: create game field representation for HTML
//    public String renderAsHtml() {
//	// The most basic (and outdated) method of creating game field representation
//	// is by composing a string containing all HTML code for field layout as a table.
//	// But be warned: dark is that path... Figure out better approach.
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
//                            String.format("/backgammon-lukac?column=%s",column)
//                            + "'><img src='/images.backgammon/clear.png' class='end'></a >");
//                }
//                if(count > row) {
//                    if (field.getColour()[column] != Colour.NONE) {
//                        if((field.getColour()[column] == field.getPlayer() && row == count-1) || (start != -1 && field.isMovePossible(start,column) && count-1 == row)){
//                            sb.append("<a href='" +
//                                    String.format("/backgammon-lukac?column=%s",column)
//                                    + "'>\n");
//                        }
//                        sb.append("<img src='/images.backgammon/" + getImage(field.getColour(), column));
//                        if(field.getCount()[column]>5 && row == 4) sb.append(field.getCount()[column]-4+".png'"); else sb.append(".png'");
//                        if(start != -1 && field.isMovePossible(start,column) && count-1 == row) {
//                            sb.append("class='end'");
//                        }
//                        sb.append("></a>");
//                    }
//                }
//                if(column == 7){
//                    sb.append("<td><img src='/images.backgammon/clear.png'></td>\n");
//                }
//            }
//        }
//
//        sb.append("</table>\n");
//
//        //black home//////////////////////////////////////////////////////////////////////////////////////////
//        sb.append("<a href='/backgammon-lukac?column=0'>");
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
//        sb.append("</table>\n");
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
//                    String.format("/backgammon-lukac?column=%s",columnIndex)
//                    + "'>\n");
//        }
//        if(field.getCount()[columnIndex]>0) {
//            sb.append("<img src='/images.backgammon/white");
//
//            if (field.getCount()[columnIndex] > 1) sb.append(field.getCount()[columnIndex] + ".png'>");
//            else sb.append(".png'>");
//        }else sb.append("<img src='/images.backgammon/clear.png'");
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
//                    String.format("/backgammon-lukac?column=%s",columnIndex)
//                    + "'>\n");
//        }
//        if(field.getCount()[columnIndex]>0) {
//            sb.append("<img src='/images.backgammon/black");
//
//            if (field.getCount()[columnIndex] > 1) sb.append(field.getCount()[columnIndex] + ".png'>");
//            else sb.append(".png'>");
//        }else sb.append("<img src='/images.backgammon/clear.png'");
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
//        sb.append("<table class='field'>\n");
//        for(int row = 5; row > 0; row--){
//            sb.append("<tr>\n");
//            for(int column = 13;column < 25; column++){
//                sb.append("<td>\n");
//                count = field.getCount()[column];
//                if(count>5) count = 5;
//                if(start != -1 && field.isMovePossible(start,column) && field.getColour()[column] == Colour.NONE && row == 1){
//                    sb.append("<a href='" +
//                            String.format("/backgammon-lukac?column=%s",column)
//                            + "'><img src='/images.backgammon/clear.png' class='end'></a >");
//                }
//                if(count >= row) {
//                    if (field.getColour()[column] != Colour.NONE) {
//                        if((field.getColour()[column] == field.getPlayer() && count==row) || (start != -1 && field.isMovePossible(start,column) && count == row)) {
//                            sb.append("<a href='" +
//                                    String.format("/backgammon-lukac?column=%s",column)
//                            + "'>\n");
//                        }
//                        sb.append("<img src='/images.backgammon/" + getImage(field.getColour(), column));
//                        if(field.getCount()[column]>5 && row == 5) sb.append(field.getCount()[column]-4+".png'"); else sb.append(".png'");
//                        if(start != -1 && field.isMovePossible(start,column) && count == row) {
//                            sb.append("class='end'");
//                        }
//                        sb.append("></a>");
//                    }
//                }
//                if(column == 18){
//                    sb.append("<td><img src='/images.backgammon/clear.png'></td>\n");
//                }
//            }
//        }
//
//        sb.append("</table>\n");
//        sb.append("</div>\n");
//        sb.append("</div>\n");
//
////        sb.append("KOCKY:" +field.getDices());
//
//        for(int i=1;i<25;i++){
//            System.out.println(i+" "+field.getColour()[i]);
//        }
//
//    	return sb.toString();
//    }
//
//    private void createField(){
//        field = new Field();
//    }
//
//    // TODO: add another methods as needed
//
//    private String getImage(Colour[] color,int i){
//        if (color[i] == Colour.BLACK) return "black";
//        if (color[i] == Colour.WHITE) return "white";
//        return "clear";
//    }
//
//    private void showDices(StringBuilder sb){
//        for (int index = 0; index < field.getDices().size(); index++) {
//            sb.append("<td><img src='/images.backgammon/dice" + field.getDices().get(index) + ".png'>");
//        }
//
//    }


}

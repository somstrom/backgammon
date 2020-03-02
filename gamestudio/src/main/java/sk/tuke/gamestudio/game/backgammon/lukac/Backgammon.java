package sk.tuke.gamestudio.game.backgammon.lukac;

import sk.tuke.gamestudio.game.backgammon.lukac.consoleui.ConsoleUI;
import sk.tuke.gamestudio.game.backgammon.lukac.core.Field;
import sk.tuke.gamestudio.game.backgammon.lukac.webui.WebUI;

public class Backgammon {

    private static Field field;

    public static void main(String[] args) {
	// write your code here

        ConsoleUI ui = new ConsoleUI();
        ui.startGame();

    }

}

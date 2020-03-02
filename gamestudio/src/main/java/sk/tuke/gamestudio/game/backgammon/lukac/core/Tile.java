package sk.tuke.gamestudio.game.backgammon.lukac.core;

public class Tile {

    /*@Override
    public String toString() {
        return this.state == TileState.BLACK_STONE ? "#" : "o";
    }*/

    public TileState getTileState(Field field,int x){
        if(field.getCount()[x] == 0) return TileState.EMPTY;
        if(field.getCount()[x] == 1) return TileState.ONE_STONE;
        return TileState.MORE_STONES;
    }

}

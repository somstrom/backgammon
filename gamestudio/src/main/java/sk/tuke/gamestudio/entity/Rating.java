package sk.tuke.gamestudio.entity;

import javax.persistence.*;
import java.util.Date;


@Entity
@NamedQueries({
        @NamedQuery(name = "Rating.getRating",
                query = "SELECT r.rating FROM Rating r WHERE r.game=:game AND r.player=:player"),
        @NamedQuery(name = "Rating.getAverageRating",
                query = "SELECT AVG(r.rating) FROM Rating r WHERE r.game=:game"),
        @NamedQuery(name = "Rating.setRating",
                query = "SELECT r FROM Rating r WHERE r.player=:player")
})
public class Rating {

    @Id
    @GeneratedValue
    private int ident;
    private String player;
    private String game;
    private int rating;
    private Date ratedon;

    public Rating(){}

    public Rating(String game, String player, int rating, Date ratedon) {
        this.player = player;
        this.game = game;
        this.rating = rating;
        this.ratedon = ratedon;
    }

    public int getIdent() { return ident; }
    public void setIdent(int ident) { this.ident = ident; }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Date getRatedon() {
        return ratedon;
    }

    public void setRatedon(Date ratedon) {
        this.ratedon = ratedon;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Rating{");
        sb.append("player='").append(player).append('\'');
        sb.append(", game='").append(game).append('\'');
        sb.append(", rating=").append(rating);
        sb.append(", ratedon=").append(ratedon);
        sb.append('}');
        return sb.toString();
    }
}

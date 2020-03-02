package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingServiceJDBC implements RatingService {

    private static final String URL = "jdbc:postgresql://localhost/gamestudio";
    private static final String USER = "postgres";
    private static final String PASSWORD = "somstrom";

    private List<Rating> ratings = new ArrayList<>();

    /*public static final String INSERT_SCORE =
            "INSERT INTO score (game, player, points, playedon) VALUES (?, ?, ?, ?)";

    public static final String SELECT_SCORE =
            "SELECT game, player, points, playedon FROM score WHERE game = ? ORDER BY points DESC LIMIT 10;";*/

    public static final String INSERT_RATING =
            "INSERT INTO rating (game, player, rating, ratedon) VALUES (?, ?, ?, ?) ON CONFLICT (player) DO UPDATE SET rating = ?";

    public static final String SELECT_RATING =
            "SELECT game, player, rating, ratedon FROM rating WHERE game = ? ORDER BY rating DESC LIMIT 10;";


    @Override
    public void setRating(Rating rating) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try(PreparedStatement ps = connection.prepareStatement(INSERT_RATING)){
                ps.setString(1,rating.getGame());
                ps.setString(2,rating.getPlayer());
                ps.setInt(3,rating.getRating());
                ps.setTimestamp(4,new Timestamp(rating.getRatedon().getTime()));
                ps.setInt(5,rating.getRating());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RatingException("Error saving rating", e);
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        float sum = 0;
        int size = 0;

        try (Connection connection = DriverManager.getConnection(URL,USER,PASSWORD)){
            try (PreparedStatement ps = connection.prepareStatement(SELECT_RATING)){
                ps.setString(1,game);
                try (ResultSet rs = ps.executeQuery()){
                    while (rs.next()){
                        sum+=rs.getInt(3);
                        size++;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RatingException("Error loading rating", e);
        }

        if(size == 0) return 0;

        return (int) (sum/size);
    }

    @Override
    public int getRating(String game, String player) throws RatingException {

        int rating1 = 0;

        try (Connection connection = DriverManager.getConnection(URL,USER,PASSWORD)){
            try (PreparedStatement ps = connection.prepareStatement(SELECT_RATING)){
                ps.setString(1,game);
                try (ResultSet rs = ps.executeQuery()){
                    while (rs.next()){
                        Rating rating = new Rating(
                                rs.getString(1),
                                rs.getString(2),
                                rs.getInt(3),
                                rs.getTimestamp(4)
                        );
                        rating1 = rs.getInt(3);
                        //ratings.add(rating);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RatingException("Error loading rating", e);
        }

        return rating1;
    }

    public static void main(String[] args) throws Exception {
        Rating rating  = new Rating("fraaj","backgammon",0, new java.util.Date());
        RatingService ratingService = new RatingServiceJDBC();
        ratingService.setRating(rating);
        System.out.println(ratingService.getRating("backgammon", "marek"));
    }
}

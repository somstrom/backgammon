package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentServiceJDBC implements CommentService {
    public static final String URL = "jdbc:postgresql://localhost/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "somstrom";

    public static final String INSERT_COMMENT =
            "INSERT INTO comment (game, player, comment, commentedon) VALUES (?, ?, ?, ?)";

    private static final String SELECT_COMMENT =
            "SELECT game, player, comment, commentedon FROM comment WHERE game = ? ORDER BY commentedon DESC LIMIT 10;";



    @Override
    public void addComment(Comment comment) throws CommentException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
            try (PreparedStatement ps = connection.prepareStatement(INSERT_COMMENT)){
                ps.setString(1,comment.getGame());
                ps.setString(2,comment.getPlayer());
                ps.setString(3,comment.getComment());
                ps.setTimestamp(4, new Timestamp(comment.getCommentedOn().getTime()));

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new CommentException("Error saving score", e);
        }

    }

    @Override
    public List<Comment> getComments(String game) throws CommentException {

        List<Comment> comments = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
            try (PreparedStatement ps = connection.prepareStatement(SELECT_COMMENT)){
                ps.setString(1,game);
                try (ResultSet rs = ps.executeQuery()){
                    while (rs.next()){
                        Comment comment = new Comment(
                                rs.getString(1),
                                rs.getString(2),
                                rs.getString(3),
                                rs.getDate(4)
                        );
                        comments.add(comment);
                    }
                }
            }
        } catch (SQLException e) {
            throw new CommentException("Error loading comment", e);
        }
        return comments;
    }

    public static void main(String[] args) throws Exception {
        Comment comment = new Comment("peto" ,"backgammon", "Frajerina",new java.util.Date());
        CommentService commentService = new CommentServiceJDBC();
        commentService.addComment(comment);
        System.out.println(commentService.getComments("backgammon"));

    }
}

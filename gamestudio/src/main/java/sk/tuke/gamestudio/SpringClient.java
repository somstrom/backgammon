package sk.tuke.gamestudio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.tuke.gamestudio.game.backgammon.lukac.consoleui.ConsoleUI;
import sk.tuke.gamestudio.service.*;

@Configuration
@SpringBootApplication
public class SpringClient {

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder(SpringClient.class).web(WebApplicationType.NONE).run(args);
    }

    @Bean
    public CommandLineRunner runner(ConsoleUI ui) { return args -> ui.startGame(); }

    @Bean
    public sk.tuke.gamestudio.game.backgammon.lukac.consoleui.ConsoleUI backgammonLukacConsoleUI() { return new sk.tuke.gamestudio.game.backgammon.lukac.consoleui.ConsoleUI(); }

    @Bean
    public ScoreService scoreService() {
        return new ScoreServiceRestClient();
//        return new ScoreServiceJPA();
    }

    @Bean
    public CommentService commentService() {
        return new CommentServiceRestClient();
//        return new CommentServiceJPA();
    }

    @Bean
    public RatingService ratingService() {
        return new RatingServiceRestClient();
//        return new RatingServiceJPA();
    }


}
package sk.tuke.gamestudio.service;

import org.springframework.transaction.annotation.Transactional;
import sk.tuke.gamestudio.entity.Rating;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Transactional
public class RatingServiceJPA implements RatingService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        List<Rating> rate = entityManager.createNamedQuery("Rating.setRating").setParameter("player",rating.getPlayer()).getResultList();
        if(!rate.isEmpty()) {
            rate.get(0).setRating(rating.getRating());
            //entityManager.merge(rate.get(0));
        }else entityManager.persist(rating);
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        List<Double> rate = entityManager.createNamedQuery("Rating.getAverageRating").setParameter("game",game).getResultList();
        if(rate.get(0) != null){
            double rating = rate.get(0);
            return (int)Math.round(rating);
        }
        return 0;

//        Float v = Float.valueOf(entityManager.createNamedQuery("Rating.getAverageRating").setParameter("game",game).getSingleResult().toString());
//        return v.intValue();
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        Float v = Float.valueOf(entityManager.createNamedQuery("Rating.getRating").setParameter("game",game).setParameter("player",player).getSingleResult().toString());
        return v.intValue();

//        Rating rate = (Rating) entityManager.createNamedQuery("Rating.getRating").setParameter("game",game).setParameter("player",player).getSingleResult();
//        return rate.getRating();
    }
}


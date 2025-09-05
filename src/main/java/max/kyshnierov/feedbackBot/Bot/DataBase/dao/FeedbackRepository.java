package max.kyshnierov.feedbackBot.Bot.DataBase.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import max.kyshnierov.feedbackBot.Bot.DataBase.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByIsListFalse();
    List<Feedback> findTop100ByOrderByIdDesc();
}

package max.kyshnierov.feedbackBot.Bot.DataBase.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import max.kyshnierov.feedbackBot.Bot.DataBase.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long>{
    Users findByChatId(Long chatId);
}

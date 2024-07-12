package com.example.testTg_bot.moodle.Repository;

import com.example.testTg_bot.moodle.Entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
}

package com.example.testTg_bot.moodle.Repository;

import com.example.testTg_bot.moodle.Entity.AdsEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdsRepository extends CrudRepository<AdsEntity, Long> {
}

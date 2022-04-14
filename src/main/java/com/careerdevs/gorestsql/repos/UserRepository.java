package com.careerdevs.gorestsql.repos;

import com.careerdevs.gorestsql.models.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends CrudRepository<User, Integer> {


    //TODO: Fix code, runs without error but does not affect auto increment
    @Modifying
    @Transactional
    @Query (value = "ALTER TABLE user AUTO_INCREMENT = 1", nativeQuery = true)
    public void resetId();


}

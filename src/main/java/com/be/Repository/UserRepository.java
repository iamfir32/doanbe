package com.be.Repository;

import com.be.Const.UserRole;
import com.be.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    List<User> findAllByRole(UserRole role);

    @Query("SELECT u FROM User u WHERE u.role = :userRole AND (u.teamRescue IS NULL AND u.teamVictim IS NULL )")
    List<User> findAllFreeRescuerByRole(UserRole userRole);

    Optional<User> findByUsername(String username);

    @Query("select u from User u where u.teamVictim.id in :ids or u.teamRescue.id in :ids")
    List<User> findAllByTeamIn(List<Long> ids);
}

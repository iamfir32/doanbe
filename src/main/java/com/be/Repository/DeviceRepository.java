package com.be.Repository;

import com.be.Const.UserRole;
import com.be.Entity.Device;
import com.be.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device,Long> {
    List<Device> findAllByRole(UserRole role);

    @Query("SELECT u FROM Device u WHERE u.role = :userRole AND (u.teamRescue IS NULL AND u.teamVictim IS NULL )")
    List<Device> findAllFreeRescuerByRole(UserRole userRole);

    Optional<Device> findByUsername(String username);

    @Query("select u from Device u where u.teamVictim.id in :ids or u.teamRescue.id in :ids")
    List<Device> findAllByTeamIn(List<Long> ids);
}

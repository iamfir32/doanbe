package com.be.Controller;

import com.be.Entity.Device;
import com.be.Entity.Team;
import com.be.Entity.User;
import com.be.Model.Team.CreateTeam;
import com.be.Model.UpdateUser;
import com.be.Repository.DeviceRepository;
import com.be.Repository.TeamRepository;
import com.be.Repository.UserRepository;
import com.be.exception.ApiException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/team")
public class TeamController {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @GetMapping
    @CrossOrigin
    public ResponseEntity<List<Team>> GetTeams(){
        return ResponseEntity.ok(teamRepository.findAll());
    }

    @PostMapping
    @CrossOrigin
    public ResponseEntity<Team> CreateTeam(@RequestBody CreateTeam request) {
        teamRepository.findByName(request.getName()).ifPresent(team -> {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Tên nhóm đã tồn tại");
        });

        List<Long> allIds = Stream.concat(request.getMembers().stream(), request.getVictims().stream())
                .distinct()
                .toList();

        List<Device> users = deviceRepository.findAllById(allIds);

        Map<Boolean, List<Device>> groupedUsers = users.stream()
                .collect(Collectors.partitioningBy(user -> request.getMembers().contains(user.getId())));

        List<Device> teamRescuers = groupedUsers.get(true);
        List<Device> teamVictims = groupedUsers.get(false);

        Team team = Team.builder()
                .name(request.getName())
                .color(request.getColor())
                .rescuer(teamRescuers)
                .victims(teamVictims)
                .build();

        teamRescuers.forEach(user -> user.setTeamRescue(team));
        teamVictims.forEach(user -> user.setTeamVictim(team));

        Team savedTeam = teamRepository.save(team);

        return ResponseEntity.ok(savedTeam);
    }

    @PutMapping
    @CrossOrigin
    public ResponseEntity<Team> EditTeam(@RequestBody CreateTeam request) {
        teamRepository.findByName(request.getName()).ifPresent(team -> {
            if (!Objects.equals(team.getId(), request.getId())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Tên nhóm đã tồn tại");
            }
        });

        Optional<Team> foundedTeam = teamRepository.findById(request.getId());
        if (foundedTeam.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Không tìm thấy đội");
        }
        Team existingTeam = foundedTeam.get();

        List<Long> newUserIds = Stream.concat(request.getMembers().stream(), request.getVictims().stream())
                .distinct()
                .toList();

        List<Long> currentUserIds = Stream.concat(
                existingTeam.getRescuer().stream().map(Device::getId),
                existingTeam.getVictims().stream().map(Device::getId)
        ).distinct().toList();

        List<Long> userIdsToRemove = currentUserIds.stream()
                .filter(id -> !newUserIds.contains(id))
                .toList();

        List<Long> userIdsToAdd = newUserIds.stream()
                .filter(id -> !currentUserIds.contains(id))
                .toList();

        List<Device> usersToRemove = deviceRepository.findAllById(userIdsToRemove);
        usersToRemove.forEach(user -> {
            if (user.getTeamRescue() != null && user.getTeamRescue().getId().equals(existingTeam.getId())) {
                user.setTeamRescue(null);
            }
            if (user.getTeamVictim() != null && user.getTeamVictim().getId().equals(existingTeam.getId())) {
                user.setTeamVictim(null);
            }
        });
        deviceRepository.saveAll(usersToRemove);

        List<Device> usersToAdd = deviceRepository.findAllById(userIdsToAdd);
        Map<Boolean, List<Device>> groupedUsersToAdd = usersToAdd.stream()
                .collect(Collectors.partitioningBy(user -> request.getMembers().contains(user.getId())));

        List<Device> newTeamRescuers = groupedUsersToAdd.get(true);
        List<Device> newTeamVictims = groupedUsersToAdd.get(false);

        List<Device> updatedRescuers = existingTeam.getRescuer().stream()
                .filter(user -> !userIdsToRemove.contains(user.getId()))
                .collect(Collectors.toList());
        updatedRescuers.addAll(newTeamRescuers);

        List<Device> updatedVictims = existingTeam.getVictims().stream()
                .filter(user -> !userIdsToRemove.contains(user.getId()))
                .collect(Collectors.toList());
        updatedVictims.addAll(newTeamVictims);

        existingTeam.setRescuer(updatedRescuers);
        existingTeam.setVictims(updatedVictims);

        newTeamRescuers.forEach(user -> user.setTeamRescue(existingTeam));
        newTeamVictims.forEach(user -> user.setTeamVictim(existingTeam));

        deviceRepository.saveAll(newTeamRescuers);
        deviceRepository.saveAll(newTeamVictims);

        existingTeam.setName(request.getName());
        existingTeam.setColor(request.getColor());
        Team savedTeam = teamRepository.save(existingTeam);

        return ResponseEntity.ok(savedTeam);
    }


    @PostMapping("/delete")
    @CrossOrigin
    public ResponseEntity<Integer> DeleteTeam(@RequestBody List<Long> ids){
        List<Device> users = deviceRepository.findAllByTeamIn(ids);
        users.forEach(x->{
            x.setTeamVictim(null);
            x.setTeamRescue(null);
        });
        deviceRepository.saveAll(users);
        teamRepository.deleteAllById(ids);
        return ResponseEntity.ok(1);
    }
}

package com.be.Controller;

import com.be.Const.UserRole;
import com.be.DTO.UserDTO;
import com.be.Entity.Device;
import com.be.Entity.Team;
import com.be.Entity.User;
import com.be.Model.UpdateUser;
import com.be.Repository.DeviceRepository;
import com.be.Repository.UserRepository;
import com.be.exception.ApiException;
import org.hibernate.sql.Update;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private DeviceRepository deviceRepository;

    @GetMapping("/getAllRescuer")
    @CrossOrigin
    public ResponseEntity<List<Device>> GetAllRescuer(){
        return ResponseEntity.ok(deviceRepository.findAllFreeRescuerByRole(UserRole.RESCUER));
    }

    @GetMapping("/getAllVictim")
    @CrossOrigin
    public ResponseEntity<List<Device>> GetAllVictim(){
        return ResponseEntity.ok(deviceRepository.findAllFreeRescuerByRole(UserRole.VICTIM));
    }

    @PostMapping("/addRescuer")
    @CrossOrigin
    public String AddRescuer(@RequestBody UserDTO request){
        Optional<Device> user =deviceRepository.findByUsername(request.getUsername());
        if(user.isEmpty()){
            throw new ApiException(HttpStatus.BAD_REQUEST,"Not found user!");
        }
        BeanUtils.copyProperties(request,user.get());
        user.get().setLast_update(new Date());
        deviceRepository.save(user.get());
        return "success";
    }
    @PutMapping
    @CrossOrigin
    public String UpdateUser(@RequestBody UpdateUser request){
        Optional<Device> user =deviceRepository.findById(request.getId());
        if(user.isEmpty()){
            throw new ApiException(HttpStatus.BAD_REQUEST,"Not found user!");
        }
        BeanUtils.copyProperties(request,user.get());
        user.get().setLast_update(new Date());
        deviceRepository.save(user.get());
        return "success";
    }
}

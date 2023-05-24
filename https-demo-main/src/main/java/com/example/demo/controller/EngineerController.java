package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.repo.SkillRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.EngineerService;
import com.example.demo.utils.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/engineer")
public class EngineerController {

    @Autowired
    EngineerService engineerService;

    @Autowired
    private PasswordValidator passwordValidator;

    //ALSO USE FOR CREATE
    @PostMapping(value="/update-engineer-skill")
    @PreAuthorize("hasRole('SOFTWARE_ENGINEER')")
    public ResponseEntity UpdateEngineerSkill(@RequestBody EngineerSkillDTO engineerSkillDTO)
    {
        if(engineerSkillDTO.getRating() >5 || engineerSkillDTO.getRating() < 1)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Skill createdSkill = engineerService.UpdateEngineerSkill(engineerSkillDTO);

        if(createdSkill == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(createdSkill, HttpStatus.OK);
    }

    //ALSO USE FOR CREATE
    @PreAuthorize("hasRole('SOFTWARE_ENGINEER')")
    @PostMapping(value="/update-engineer-cv")
    public ResponseEntity UpdateEngineerCV(@RequestParam("file") MultipartFile file, @RequestParam("username") String username) throws IOException {
        boolean createdCV = engineerService.UpdateEngineerCV(file, username);

        if(createdCV == false)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(createdCV, HttpStatus.OK);
    }

    @PostMapping(value="/get-engineer-skills")
    public ResponseEntity GetSkillsForEngineer(@RequestBody PasswordlessLoginDTO enginnerEmailDTO)
    {
        List<Skill> skillsList = engineerService.GetSkillsForEnginner(enginnerEmailDTO);
        return new ResponseEntity<>(skillsList, HttpStatus.OK);
    }

    @PostMapping(value="/get-project-tasks")
    @PreAuthorize("hasRole('SOFTWARE_ENGINEER')")
    public ResponseEntity GetProjectTasksForEngineer(@RequestBody PasswordlessLoginDTO enginnerEmailDTO)
    {
        List<ProjectTask> projectTaskList = engineerService.GetProjectTasksForEnginner(enginnerEmailDTO);
        return new ResponseEntity<>(projectTaskList, HttpStatus.OK);
    }
    @PostMapping(value="/get-project-and-project-tasks")
    @PreAuthorize("hasRole('SOFTWARE_ENGINEER')")
    public ResponseEntity GetProjectWithProjectTasksForEngineer(@RequestBody PasswordlessLoginDTO enginnerEmailDTO)
    {
        List<EngineerProjectWithProjectTaskDTO> projectTaskList = engineerService.GetProjectWithProjectTasksForEnginner(enginnerEmailDTO);
        return new ResponseEntity<>(projectTaskList, HttpStatus.OK);
    }

    @PostMapping(value="/update-project-task")
    public ResponseEntity UpdateProjectTaskForEngineer(@RequestBody UpdateProjectTaskRequestDTO requestDTO){

        boolean changed = engineerService.UpdateProjectTaskForEngineer(requestDTO);
        if (!changed) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value="/account-details")
    public ResponseEntity getAccountDetails(@RequestBody PasswordlessLoginDTO enginnerEmailDTO) {

        EngineerAccountDetailsDTO accountDetails = engineerService.GetAccountDetails(enginnerEmailDTO.getUsername());
        if (accountDetails == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(accountDetails, HttpStatus.OK);
    }

    @PostMapping(value="/account-details-update")
    public ResponseEntity updateAccountDetails(@RequestBody EngineerAccountDetailsDTO engineerAccountDetailsDTO) {

        System.out.println("APDEJTUJEM DETALJE");
        if(engineerAccountDetailsDTO.getPassword() != null)
        {
            if (!passwordValidator.isValid(engineerAccountDetailsDTO.getPassword()))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        boolean changed = engineerService.UpdateAccountDetails(engineerAccountDetailsDTO);
        if (!changed) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

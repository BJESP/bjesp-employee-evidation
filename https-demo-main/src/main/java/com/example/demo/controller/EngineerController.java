package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.repo.SkillRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.EngineerService;
import com.example.demo.utils.GeneralValidation;
import com.example.demo.utils.PasswordValidator;

import com.example.demo.service.UserService;
import com.example.demo.utils.UserValidation;
import org.hibernate.annotations.Check;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    UserService userService;

    @Autowired
    private PasswordValidator passwordValidator;
    @Autowired
    UserValidation userValidation;

    //ALSO USE FOR CREATE
    @PostMapping(value="/update-engineer-skill")
    @PreAuthorize("hasRole('SOFTWARE_ENGINEER')")
    public ResponseEntity UpdateEngineerSkill(@RequestBody EngineerSkillDTO engineerSkillDTO)
    {
        if(CheckPermissionForRole("UPDATE_ENGINEER_SKILL")) {
            try {
                userValidation.validRating(engineerSkillDTO.getRating());

                Skill createdSkill = engineerService.UpdateEngineerSkill(engineerSkillDTO);

                if (createdSkill == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                return new ResponseEntity<>(createdSkill, HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping(value="/create-engineer-skill")
    @PreAuthorize("hasRole('SOFTWARE_ENGINEER')")
    public ResponseEntity CreateEngineerSkill(@RequestBody EngineerSkillDTO engineerSkillDTO)
    {
        if(CheckPermissionForRole("CREATE_ENGINEER_SKILL")) {
            try {
                userValidation.validRating(engineerSkillDTO.getRating());

                Skill createdSkill = engineerService.UpdateEngineerSkill(engineerSkillDTO);

                if (createdSkill == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                return new ResponseEntity<>(createdSkill, HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
         else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //ALSO USE FOR CREATE
    @PreAuthorize("hasRole('SOFTWARE_ENGINEER')")
    @PostMapping(value="/update-engineer-cv")
    public ResponseEntity UpdateEngineerCV(@RequestParam("file") MultipartFile file, @RequestParam("username") String username) throws IOException {
        if(CheckPermissionForRole("UPDATE_ENGINEER_CV")) {
            try
            {
                userValidation.validUserEmail(username);
                boolean createdCV = engineerService.UpdateEngineerCV(file, username);
                if (createdCV == false) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                return new ResponseEntity<>(createdCV, HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping(value="/create-engineer-cv")
    @PreAuthorize("hasRole('SOFTWARE_ENGINEER')")
    public ResponseEntity CreateEngineerCV(@RequestParam("file") MultipartFile file, @RequestParam("username") String username) throws IOException {
        if(CheckPermissionForRole("CREATE_ENGINEER_CV")) {
            try
            {
                userValidation.validUserEmail(username);
                boolean createdCV = engineerService.UpdateEngineerCV(file, username);

                if (createdCV == false) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                return new ResponseEntity<>(createdCV, HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value="/get-engineer-skills")
    @PreAuthorize("hasRole('SOFTWARE_ENGINEER')")
    public ResponseEntity GetSkillsForEngineer(@RequestBody PasswordlessLoginDTO enginnerEmailDTO)
    {
        if(CheckPermissionForRole("READ_ENGINEER_SKILL")) {
            try
            {
                userValidation.validPasswordlessLoginDTO(enginnerEmailDTO);
                List<Skill> skillsList = engineerService.GetSkillsForEnginner(enginnerEmailDTO);
                return new ResponseEntity<>(skillsList, HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value="/get-project-tasks")
    @PreAuthorize("hasRole('SOFTWARE_ENGINEER')")
    public ResponseEntity GetProjectTasksForEngineer(@RequestBody PasswordlessLoginDTO enginnerEmailDTO)
    {
        if(CheckPermissionForRole("READ_ENGINEER_TASK"))
        {
            try
            {
                userValidation.validPasswordlessLoginDTO(enginnerEmailDTO);
                List<ProjectTask> projectTaskList = engineerService.GetProjectTasksForEnginner(enginnerEmailDTO);
                return new ResponseEntity<>(projectTaskList, HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping(value="/get-project-and-project-tasks")
    @PreAuthorize("hasRole('SOFTWARE_ENGINEER')")
    public ResponseEntity GetProjectWithProjectTasksForEngineer(@RequestBody PasswordlessLoginDTO enginnerEmailDTO)
    {
        if(CheckPermissionForRole("READ_PROJECT_TASK"))
        {
            try
            {
                userValidation.validPasswordlessLoginDTO(enginnerEmailDTO);
                List<EngineerProjectWithProjectTaskDTO> projectTaskList = engineerService.GetProjectWithProjectTasksForEnginner(enginnerEmailDTO);
                return new ResponseEntity<>(projectTaskList, HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value="/update-project-task")
    public ResponseEntity UpdateProjectTaskForEngineer(@RequestBody UpdateProjectTaskRequestDTO requestDTO){
        if(CheckPermissionForRole("UPDATE_ENGINEER_TASK")) {
            try {
                userValidation.validUpdateProjectTaskRequestDTO(requestDTO);
                boolean changed = engineerService.UpdateProjectTaskForEngineer(requestDTO);
                if (!changed) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                return new ResponseEntity<>(HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

            }
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public boolean CheckPermissionForRole(String privilege){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasPermission = userService.CheckPermissionForRole(authentication,privilege);
        return hasPermission;

    }

    @PreAuthorize("hasRole('SOFTWARE_ENGINEER')")
    @PostMapping(value="/account-details")
    public ResponseEntity getAccountDetails(@RequestBody PasswordlessLoginDTO enginnerEmailDTO) {
        if(CheckPermissionForRole("READ_ENGINEER_ACCOUNT_DETAILS"))
        {
            try
            {
                userValidation.validPasswordlessLoginDTO(enginnerEmailDTO);
                EngineerAccountDetailsDTO accountDetails = engineerService.GetAccountDetails(enginnerEmailDTO.getUsername());
                if (accountDetails == null) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                return new ResponseEntity<>(accountDetails, HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasRole('SOFTWARE_ENGINEER')")
    @PostMapping(value="/account-details-update")
    public ResponseEntity updateAccountDetails(@RequestBody EngineerAccountDetailsDTO engineerAccountDetailsDTO) {
        if(CheckPermissionForRole("UPDATE_ENGINEER_ACCOUNT_DETAILS"))
        {
            try {
                userValidation.validUpdateEngineerAccountDetailsDTO(engineerAccountDetailsDTO);
                System.out.println("APDEJTUJEM DETALJE");
                if (engineerAccountDetailsDTO.getPassword() != null) {
                    if (!passwordValidator.isValid(engineerAccountDetailsDTO.getPassword()))
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                boolean changed = engineerService.UpdateAccountDetails(engineerAccountDetailsDTO);
                if (!changed) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                return new ResponseEntity<>(HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

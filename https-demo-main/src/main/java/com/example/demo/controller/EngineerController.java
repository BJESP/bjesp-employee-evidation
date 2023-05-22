package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.CVDocument;
import com.example.demo.model.EngineerProfile;
import com.example.demo.model.ProjectTask;
import com.example.demo.model.Skill;
import com.example.demo.repo.SkillRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.EngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    //ALSO USE FOR CREATE
    @PostMapping(value="/update-engineer-skill")
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
    @PostMapping(value="/update-engineer-cv")
    public ResponseEntity UpdateEngineerCV(@ModelAttribute EngineerCVDocumentDTO CVDocument) throws IOException {
        boolean createdCV = engineerService.UpdateEngineerCV(CVDocument);

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
    public ResponseEntity GetProjectTasksForEngineer(@RequestBody PasswordlessLoginDTO enginnerEmailDTO)
    {
        List<ProjectTask> projectTaskList = engineerService.GetProjectTasksForEnginner(enginnerEmailDTO);
        return new ResponseEntity<>(projectTaskList, HttpStatus.OK);
    }
    @PostMapping(value="/get-project-and-project-tasks")
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
}

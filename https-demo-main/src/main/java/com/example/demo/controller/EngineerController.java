package com.example.demo.controller;

import com.example.demo.dto.EngineerCVDocumentDTO;
import com.example.demo.dto.EngineerSkillDTO;
import com.example.demo.dto.PasswordlessLoginDTO;
import com.example.demo.dto.ProjectManagerUpdateDTO;
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

    @PostMapping(value="/get-project-tasks")
    public ResponseEntity GetProjectTasksForEngineer(@RequestBody PasswordlessLoginDTO enginnerEmailDTO)
    {
        List<ProjectTask> projectTaskList = engineerService.GetProjectTasksForEnginner(enginnerEmailDTO);
        return new ResponseEntity<>(projectTaskList, HttpStatus.OK);
    }

}

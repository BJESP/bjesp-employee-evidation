package com.example.demo.controller;

import com.example.demo.dto.EngineerCVDocumentDTO;
import com.example.demo.dto.EngineerSkillDTO;
import com.example.demo.dto.ProjectManagerUpdateDTO;
import com.example.demo.model.CVDocument;
import com.example.demo.model.EngineerProfile;
import com.example.demo.model.Skill;
import com.example.demo.repo.SkillRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.EngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity UpdateEngineerCV(@RequestParam("documentData") MultipartFile file, @ModelAttribute EngineerCVDocumentDTO CVDocument)
    {
        CVDocument.setDocumentData(file);
        boolean createdCV = engineerService.UpdateEngineerCV(CVDocument);

        if(createdCV == false)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(createdCV, HttpStatus.OK);
    }



}

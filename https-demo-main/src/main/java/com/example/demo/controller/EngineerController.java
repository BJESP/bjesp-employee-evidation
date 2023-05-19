package com.example.demo.controller;

import com.example.demo.dto.EngineerSkillDTO;
import com.example.demo.dto.ProjectManagerUpdateDTO;
import com.example.demo.model.EngineerProfile;
import com.example.demo.model.Skill;
import com.example.demo.repo.SkillRepo;
import com.example.demo.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/engineer")
public class EngineerController {

    @Autowired
    SkillRepo skillRepo;

    @Autowired
    UserRepo userRepo;

    @PostMapping(value="/update-engineer-skill")
    public boolean UpdateEngineerSkill(@RequestBody EngineerSkillDTO engineerSkillDTO){

        System.out.println("JSON EMAIL: " + engineerSkillDTO.getEngineerProfileEmail());
        System.out.println("JSON: " + engineerSkillDTO.getName());
        System.out.println("JSON: " + engineerSkillDTO.getRating());
        if(!userRepo.existsByEmail(engineerSkillDTO.getEngineerProfileEmail()))
        {
            System.out.println("NEMA TOG USERA");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND).hasBody();
        }

        Skill skill = skillRepo.findByEngineerProfileEmailAndName(engineerSkillDTO.getEngineerProfileEmail(), engineerSkillDTO.getName());

        if (skill == null)
        {
            skill = new Skill();
        }

        skill.setRating(engineerSkillDTO.getRating());
        skill.setName(engineerSkillDTO.getName());
        skill.setEngineerProfile((EngineerProfile) userRepo.findByEmail(engineerSkillDTO.getEngineerProfileEmail()));

        skillRepo.save(skill);

        return new ResponseEntity<>(HttpStatus.OK).hasBody();
    }

}

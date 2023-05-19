package com.example.demo.service;

import com.example.demo.dto.EngineerCVDocumentDTO;
import com.example.demo.dto.EngineerSkillDTO;
import com.example.demo.model.CVDocument;
import com.example.demo.model.EngineerProfile;
import com.example.demo.model.Skill;
import com.example.demo.model.User;
import com.example.demo.repo.CVDocumentRepo;
import com.example.demo.repo.SkillRepo;
import com.example.demo.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class EngineerService
{
    @Autowired
    SkillRepo skillRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    CVDocumentRepo cvDocumentRepo;
    public Skill UpdateEngineerSkill(EngineerSkillDTO engineerSkillDTO)
    {
        if(!userRepo.existsByEmail(engineerSkillDTO.getEngineerProfileEmail()))
        {
            System.out.println("NEMA TOG USERA");
            return null;
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

        return skill;
    }

    public boolean UpdateEngineerCV(EngineerCVDocumentDTO cvDocument)
    {
        if(!userRepo.existsByEmail(cvDocument.getEngineerProfileEmail()))
        {
            System.out.println("NEMA TOG USERA");
            return false;
        }

        CVDocument newCvDocument = cvDocumentRepo.findByEngineerProfile((EngineerProfile) userRepo.findByEmail(cvDocument.getEngineerProfileEmail()));


        if (newCvDocument == null)
        {
            newCvDocument = new CVDocument();
        }

        newCvDocument.setDocumentData(cvDocument.getDocumentData());
        newCvDocument.setDocumentName(cvDocument.getDocumentName());
        newCvDocument.setEngineerProfile((EngineerProfile) userRepo.findByEmail(cvDocument.getEngineerProfileEmail()));

        cvDocumentRepo.save(newCvDocument);

        return true;
    }
}

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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

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

    public boolean UpdateEngineerCV(EngineerCVDocumentDTO cvDocument) throws IOException {

        if(!userRepo.existsByEmail(cvDocument.getEngineerProfileEmail()))
        {
            System.out.println("NEMA TOG USERA");
            return false;
        }

        CVDocument newCvDocument = cvDocumentRepo.findByEngineerProfile((EngineerProfile) userRepo.findByEmail(cvDocument.getEngineerProfileEmail()));


        if (newCvDocument == null)
        {
            newCvDocument = new CVDocument();
            newCvDocument.setInternalName(UUID.randomUUID().toString());
        }

        saveFile(newCvDocument.getInternalName(), cvDocument.getDocumentData());
        newCvDocument.setDocumentName(cvDocument.getDocumentName());
        newCvDocument.setEngineerProfile((EngineerProfile) userRepo.findByEmail(cvDocument.getEngineerProfileEmail()));

        cvDocumentRepo.save(newCvDocument);

        return true;
    }

    public static boolean saveFile(String internalName, MultipartFile multipartFile)
            throws IOException {
        Path uploadPath = Paths.get("Files-Upload");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }


        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(internalName + ".pdf");
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save file:", ioe);
        }

        return true;
    }
}

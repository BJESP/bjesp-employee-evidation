package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EngineerService
{
    @Autowired
    ProjectRepo projectRepo;
    @Autowired
    SkillRepo skillRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    CVDocumentRepo cvDocumentRepo;

    @Autowired
    ProjectTaskRepo projectTaskRepo;
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

    public boolean UpdateEngineerCV(byte[] file, String username) throws IOException {

        System.out.println("EMAIL: " + username);
        if(!userRepo.existsByEmail(username))
        {
            System.out.println("NEMA TOG USERA");
            return false;
        }

        CVDocument newCvDocument = cvDocumentRepo.findByEngineerProfile(Optional.ofNullable((EngineerProfile) userRepo.findByEmail(username)));


        if (newCvDocument == null)
        {
            newCvDocument = new CVDocument();
            newCvDocument.setInternalName(UUID.randomUUID().toString());
        }

        saveFile(newCvDocument.getInternalName(), file);
        //newCvDocument.setDocumentName(file.getOriginalFilename());
        newCvDocument.setEngineerProfile((EngineerProfile) userRepo.findByEmail(username));

        cvDocumentRepo.save(newCvDocument);

        return true;
    }

    public static boolean saveFile(String internalName, byte[] multipartFile)
            throws IOException {
        Path uploadPath = Paths.get("Files-Upload");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try {
            Path filePath = uploadPath.resolve(internalName + ".pdf");
            Files.write(filePath, multipartFile);
        } catch (IOException ioe) {
            throw new IOException("Could not save file:", ioe);
        }

        return true;
    }

    public List<ProjectTask> GetProjectTasksForEnginner(PasswordlessLoginDTO engineerEmailDTO) {

        User user = userRepo.findByEmail(engineerEmailDTO.getUsername());
        if(user == null)
        {
            System.out.println("NEMA TOG USERA");
            return null;
        }
        return projectTaskRepo.getAllProjectTasksByEngineerProfileId(user.getId());
    }

    public List<Skill> GetSkillsForEnginner(PasswordlessLoginDTO engineerEmailDTO) {

        User user = userRepo.findByEmail(engineerEmailDTO.getUsername());
        if(user == null)
        {
            System.out.println("NEMA TOG USERA");
            return null;
        }

        return skillRepo.getAllSkillsByEngineerProfileId(user.getId());
    }
    public List<EngineerProjectWithProjectTaskDTO> GetProjectWithProjectTasksForEnginner(PasswordlessLoginDTO engineerEmailDTO) {

        User user = userRepo.findByEmail(engineerEmailDTO.getUsername());
        if(user == null)
        {
            System.out.println("NEMA TOG USERA");
            return null;
        }

        System.out.println("NASAO USERA SA ID: " +  user.getId());
        List<ProjectTask> projectTaskList = projectTaskRepo.getAllProjectTasksByEngineerProfileId(user.getId());
        List<EngineerProjectWithProjectTaskDTO> engineerProjectWithProjectTaskDTOS = new ArrayList<>();
        for (ProjectTask projectTask : projectTaskList)
        {
            EngineerProjectWithProjectTaskDTO newProjectProjectTask = new EngineerProjectWithProjectTaskDTO();

            newProjectProjectTask.setProjectName(projectTask.getProject().getName());
            newProjectProjectTask.setTaskName(projectTask.getTaskName());
            newProjectProjectTask.setDescription(projectTask.getDescription());
            newProjectProjectTask.setEndDate(projectTask.getEndDate());
            newProjectProjectTask.setStartDate(projectTask.getStartDate());
            newProjectProjectTask.setId(projectTask.getId());
            engineerProjectWithProjectTaskDTOS.add(newProjectProjectTask);
        }

        return engineerProjectWithProjectTaskDTOS;
    }

    public boolean UpdateProjectTaskForEngineer(UpdateProjectTaskRequestDTO requestDTO) {

        System.out.println(requestDTO.getProjectName());
        System.out.println("USERNAME: " + requestDTO.getUsername());

        User user = userRepo.findByEmail(requestDTO.getUsername());
        if(user == null)
        {
            System.out.println("NEMA TOG USERA");
            return false;
        }

        List<ProjectTask> projectTasks = projectTaskRepo.getAllProjectTasksByEngineerProfileId(user.getId());

        for (ProjectTask projectTask:projectTasks)
        {
            if(projectTask.getProject().getName().equals(requestDTO.getProjectName()))
            {
                projectTask.setDescription(requestDTO.getDescription());
                projectTaskRepo.save(projectTask);
                return true;
            }
        }

        System.out.println("NISAM NASAO TASK SA TIM PROJEKTOM I TIM USEROM");
        return false;
    }

    public boolean UpdateAccountDetails(EngineerAccountDetailsDTO engineerAccountDetailsDTO)
    {
        System.out.println("APDEJTUJEM DETALJE I U SERVISU");
        User user = userRepo.findByEmail(engineerAccountDetailsDTO.getEmail());
        if(user == null)
        {
            System.out.println("NEMA TOG USERA");
            return false;
        }

        user.setAddress(engineerAccountDetailsDTO.getAddress());
        if(engineerAccountDetailsDTO.getPassword() != null)
        {
            if(!engineerAccountDetailsDTO.getPassword().equals("") )
            {
                user.setPassword(engineerAccountDetailsDTO.getPassword());
            }
        }
        user.setFirstName(engineerAccountDetailsDTO.getFirstName());
        user.setLastName(engineerAccountDetailsDTO.getLastName());
        user.setPhoneNumber(engineerAccountDetailsDTO.getPhoneNumber());

        userRepo.save(user);

        return true;
    }

    public EngineerAccountDetailsDTO GetAccountDetails(String username) {
        User user = userRepo.findByEmail(username);

        if(user == null)
        {
            return null;
        }

        EngineerAccountDetailsDTO engineerAccountDetailsDTO = new EngineerAccountDetailsDTO();
        engineerAccountDetailsDTO.setAddress(user.getAddress());
        engineerAccountDetailsDTO.setFirstName(user.getFirstName());
        engineerAccountDetailsDTO.setLastName(user.getLastName());
        engineerAccountDetailsDTO.setPhoneNumber(user.getPhoneNumber());

        return engineerAccountDetailsDTO;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public byte[] loadEngineerCv(String username) {
    try {
        CVDocument cvDocument = null;
        if(isNumeric(username)) {
            if (!userRepo.existsById(Long.valueOf(username))) {
                System.out.println("NEMA TOG USERA");
                return null;
            }

            cvDocument = cvDocumentRepo.findByEngineerProfile(userRepo.findById(Long.valueOf(username)));
            if (cvDocument == null) {
                return null; // CV not found for the engineer
            }
        }
        else
        {
            if (!userRepo.existsByEmail((username))) {
                System.out.println("NEMA TOG USERA");
                return null;
            }

            cvDocument = cvDocumentRepo.findByEngineerProfile(Optional.ofNullable(userRepo.findByEmail(username)));
            if (cvDocument == null) {
                return null; // CV not found for the engineer
            }
        }

        byte[] encryptedCv = readFile(cvDocument.getInternalName());
        if (encryptedCv == null) {
            return null; // Failed to read the CV file
        }

        return encryptedCv;
    } catch (Exception e) {
        // Handle any exceptions that may occur during the loading process
        e.printStackTrace();
        // Return null or throw an appropriate exception
        return null;
    }
}

    private byte[] readFile(String internalName) throws IOException {
        Path filePath = Paths.get("Files-Upload", internalName + ".pdf");
        return Files.readAllBytes(filePath);
    }


}

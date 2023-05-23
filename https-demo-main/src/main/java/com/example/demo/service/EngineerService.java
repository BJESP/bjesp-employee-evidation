package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.repo.*;
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
import java.util.ArrayList;
import java.util.List;
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
}

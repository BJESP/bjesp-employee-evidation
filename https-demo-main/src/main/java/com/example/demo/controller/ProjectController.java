package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.EngineerProfile;
import com.example.demo.model.ProjectManagerProfile;
import com.example.demo.model.User;
import com.example.demo.model.ValidationResult;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserService;
import com.example.demo.utils.UserValidation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/project",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserValidation userValidation;
    private Logger logger =  LogManager.getLogger(ProjectController.class);

    @GetMapping()
    @PreAuthorize("hasPermission(1, 'Project', 'READ')")
    public ResponseEntity getAll(HttpServletRequest request) {
        try {
            logger.info("Reading all projects");
            return new ResponseEntity<List<ProjectDTO>>(projectService.getAll(), HttpStatus.OK);
        }
        catch(NullPointerException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping()
    @PreAuthorize("hasPermission(1, 'Project', 'CREATE')")
    public ResponseEntity createProject(@RequestBody ProjectDTO data) {
        try {
            ValidationResult validationResult = userValidation.validProjectDTO(data);
            if (validationResult.isValid()) {
                projectService.createProject(data);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(validationResult.getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);

        }
    }
    @GetMapping(value="/engineers/{projectId}")
    @PreAuthorize("hasPermission(1, 'Task', 'READ')")
    public ResponseEntity getAllEngineersNotOnProject(@PathVariable String projectId){
        try {
            logger.info("Successfully get all enigneers not working on project ");
            List<EngineerDTO> engineerDTOS = userService.getAllEngineersNotOnProject(projectId);
            return new ResponseEntity<>(engineerDTOS, HttpStatus.OK);
        }
        catch(NullPointerException e){
            logger.error("NullPointerException occurred: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping(value="/engineersWorking/{projectId}")
    @PreAuthorize("hasPermission(1, 'Task', 'READ')")
    public ResponseEntity getAllEngineersOnProject(@PathVariable String projectId,HttpServletRequest request){
        try {
            logger.info("Processing all engineers for projectId: {}", projectId);
            List<EngineerDTO> engineerDTOS = userService.getAllEngineersOnProject(projectId);
            return new ResponseEntity<>(engineerDTOS, HttpStatus.OK);
        }
        catch(NullPointerException e){
            logger.error("NullPointerException occurred: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping(value="/addTask")
    @PreAuthorize("hasPermission(1, 'Project_task', 'CREATE')")
    public ResponseEntity AddEngineerToTask(@RequestBody AddEngineerToProjectDTO addEngineerToProjectDTO,HttpServletRequest request){
        try {
            ValidationResult validationResult = userValidation.validAddEngineerToProjectDTO(addEngineerToProjectDTO);
            if (validationResult.isValid()) {
                logger.info("Added project task to engineer");
                projectService.AddEngineerToProject(addEngineerToProjectDTO);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                logger.error(validationResult.getErrorMessage());
                return new ResponseEntity<>(validationResult.getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
        }catch (IllegalArgumentException e) {
            logger.error("AddEngineerToTask: IllegalArgumentException occurred - {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);

        }
    }

    public boolean CheckPermissionForRole(String privilege){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasPermission = userService.CheckPermissionForRole(authentication,privilege);
        return hasPermission;

    }
    @GetMapping(value="/managersWorking/{projectId}")
    @PreAuthorize("hasPermission(1, 'Project_managers', 'READ')")
    public ResponseEntity getAllManagersOnProject(@PathVariable String projectId,HttpServletRequest request){
        try {
            logger.info("Reading managers working on project");
            List<EmployeeDTO> managersDTOS = projectService.getAllManagersOnProject(projectId);
            return new ResponseEntity<>(managersDTOS, HttpStatus.OK);
        }
        catch(NullPointerException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping(value="/managersNotWorking/{projectId}")
    @PreAuthorize("hasPermission(1, 'Project_managers', 'READ')")
    public ResponseEntity getAllManagersNotOnProject(@PathVariable String projectId, HttpServletRequest request){
        try {
            logger.info("Reading managers who are not working on project");
            List<EmployeeDTO> managersDTOS = projectService.getAllManagersNotOnProject(projectId);
            return new ResponseEntity<>(managersDTOS, HttpStatus.OK);
        }
        catch(NullPointerException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping(value="/addManagerAdmin/{email}/{projectId}")
    @PreAuthorize("hasPermission(1, 'Project_managers', 'UPDATE')")
    public ResponseEntity<HttpStatus> AddManagerToProject(@PathVariable String email,@PathVariable String projectId,HttpServletRequest request){
        try {
            logger.info("Added manager  to project");
            projectService.addManagerToProject(projectId, email);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(NullPointerException e){
            logger.error("Unsuccessfully added manager to project");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

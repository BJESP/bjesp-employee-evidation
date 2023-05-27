package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.EngineerProfile;
import com.example.demo.model.User;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserService;
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

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProjectDTO>> getAll(HttpServletRequest request) {
        if(CheckPermissionForRole("READ_PROJECTS")) {
            return new ResponseEntity<List<ProjectDTO>>(projectService.getAll(), HttpStatus.OK);
        } else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> createProject(@RequestBody ProjectDTO data) {
        if(CheckPermissionForRole("CREATE_PROJECT")) {
            projectService.createProject(data);
            return new ResponseEntity<>(HttpStatus.OK);
        } else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping(value="/engineers/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EngineerDTO>> getAllEngineersNotOnProject(@PathVariable String projectId){
        if(CheckPermissionForRole("READ_PROJECT_TASK")) {
            List<EngineerDTO> engineerDTOS = userService.getAllEngineersNotOnProject(projectId);
            return new ResponseEntity<>(engineerDTOS,HttpStatus.OK);
        } else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping(value="/engineersWorking/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EngineerDTO>> getAllEngineersOnProject(@PathVariable String projectId){
        if(CheckPermissionForRole("READ_PROJECT_TASK")) {
            List<EngineerDTO> engineerDTOS = userService.getAllEngineersOnProject(projectId);
            return new ResponseEntity<>(engineerDTOS, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping(value="/addTask")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity AddEngineerToTask(@RequestBody AddEngineerToProjectDTO addEngineerToProjectDTO){
        if(CheckPermissionForRole("CREATE_PROJECT_TASK")) {
            projectService.AddEngineerToProject(addEngineerToProjectDTO);
            return new ResponseEntity<>(HttpStatus.OK);
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
    @GetMapping(value="/managersWorking/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeDTO>> getAllManagersOnProject(@PathVariable String projectId){
        if(CheckPermissionForRole("READ_PROJECT_MANAGERS")) {
            List<EmployeeDTO> managersDTOS = projectService.getAllManagersOnProject(projectId);
            return new ResponseEntity<>(managersDTOS,HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping(value="/managersNotWorking/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeDTO>> getAllManagersNotOnProject(@PathVariable String projectId){
        if(CheckPermissionForRole("READ_PROJECT_MANAGERS")) {
            List<EmployeeDTO> managersDTOS = projectService.getAllManagersNotOnProject(projectId);
            return new ResponseEntity<>(managersDTOS,HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping(value="/addManagerAdmin/{email}/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> AddManagerToProject(@PathVariable String email,@PathVariable String projectId){
        if(CheckPermissionForRole("UPDATE_PROJECT_MANAGERS")) {
            projectService.addManagerToProject(projectId, email);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

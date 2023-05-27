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
    @PreAuthorize("hasPermission(1, 'Project', 'READ')")
    public ResponseEntity<List<ProjectDTO>> getAll(HttpServletRequest request) {
            return new ResponseEntity<List<ProjectDTO>>(projectService.getAll(), HttpStatus.OK);
    }
    @PostMapping()
    @PreAuthorize("hasPermission(1, 'Project', 'CREATE')")
    public ResponseEntity<HttpStatus> createProject(@RequestBody ProjectDTO data) {
            projectService.createProject(data);
            return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping(value="/engineers/{projectId}")
    @PreAuthorize("hasPermission(1, 'Task', 'READ')")
    public ResponseEntity<List<EngineerDTO>> getAllEngineersNotOnProject(@PathVariable String projectId){
            List<EngineerDTO> engineerDTOS = userService.getAllEngineersNotOnProject(projectId);
            return new ResponseEntity<>(engineerDTOS,HttpStatus.OK);
    }
    @GetMapping(value="/engineersWorking/{projectId}")
    @PreAuthorize("hasPermission(1, 'Task', 'READ')")
    public ResponseEntity<List<EngineerDTO>> getAllEngineersOnProject(@PathVariable String projectId){
            List<EngineerDTO> engineerDTOS = userService.getAllEngineersOnProject(projectId);
            return new ResponseEntity<>(engineerDTOS, HttpStatus.OK);
    }
    @PostMapping(value="/addTask")
    @PreAuthorize("hasPermission(1, 'Project_task', 'CREATE')")
    public ResponseEntity AddEngineerToTask(@RequestBody AddEngineerToProjectDTO addEngineerToProjectDTO){
            projectService.AddEngineerToProject(addEngineerToProjectDTO);
            return new ResponseEntity<>(HttpStatus.OK);
    }

    public boolean CheckPermissionForRole(String privilege){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasPermission = userService.CheckPermissionForRole(authentication,privilege);
        return hasPermission;

    }
    @GetMapping(value="/managersWorking/{projectId}")
    @PreAuthorize("hasPermission(1, 'Project_managers', 'READ')")
    public ResponseEntity<List<EmployeeDTO>> getAllManagersOnProject(@PathVariable String projectId){
            List<EmployeeDTO> managersDTOS = projectService.getAllManagersOnProject(projectId);
            return new ResponseEntity<>(managersDTOS,HttpStatus.OK);
    }
    @GetMapping(value="/managersNotWorking/{projectId}")
    @PreAuthorize("hasPermission(1, 'Project_managers', 'READ')")
    public ResponseEntity<List<EmployeeDTO>> getAllManagersNotOnProject(@PathVariable String projectId){
            List<EmployeeDTO> managersDTOS = projectService.getAllManagersNotOnProject(projectId);
            return new ResponseEntity<>(managersDTOS,HttpStatus.OK);
    }
    @PostMapping(value="/addManagerAdmin/{email}/{projectId}")
    @PreAuthorize("hasPermission(1, 'Project_managers', 'UPDATE')")
    public ResponseEntity<HttpStatus> AddManagerToProject(@PathVariable String email,@PathVariable String projectId){
            projectService.addManagerToProject(projectId, email);
            return new ResponseEntity<>(HttpStatus.OK);

    }
}

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
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProjectDTO>> getAll(HttpServletRequest request) {
        return new ResponseEntity<List<ProjectDTO>>(projectService.getAll(), HttpStatus.OK);
    }
    @PostMapping()
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> createProject(@RequestBody ProjectDTO data) {
        projectService.createProject(data);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping(value="/engineers/{projectId}")
    public ResponseEntity<List<EngineerDTO>> getAllEngineersNotOnProject(@PathVariable String projectId){
        List<EngineerDTO> engineerDTOS = userService.getAllEngineersNotOnProject(projectId);
        return new ResponseEntity<>(engineerDTOS,HttpStatus.OK);
    }
    @GetMapping(value="/engineersWorking/{projectId}")
    public ResponseEntity<List<EngineerDTO>> getAllEngineersOnProject(@PathVariable String projectId){
        List<EngineerDTO> engineerDTOS = userService.getAllEngineersOnProject(projectId);
        return new ResponseEntity<>(engineerDTOS,HttpStatus.OK);
    }
    @PostMapping(value="/addTask")
    public ResponseEntity AddEngineerToTask(@RequestBody AddEngineerToProjectDTO addEngineerToProjectDTO){
        projectService.AddEngineerToProject(addEngineerToProjectDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

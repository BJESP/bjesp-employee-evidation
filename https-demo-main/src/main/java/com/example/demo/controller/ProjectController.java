package com.example.demo.controller;

import com.example.demo.dto.EmployeeDTO;
import com.example.demo.dto.ProjectDTO;
import com.example.demo.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/project",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @GetMapping()
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProjectDTO>> getAll(HttpServletRequest request) {
        return new ResponseEntity<List<ProjectDTO>>(projectService.getAll(), HttpStatus.OK);
    }
}

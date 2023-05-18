package com.example.demo.service;

import com.example.demo.dto.ProjectDTO;
import com.example.demo.model.Project;
import com.example.demo.repo.ProjectRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepo projectRepository;

    public List<ProjectDTO> getAll(){
        ArrayList<Project> all = (ArrayList<Project>) projectRepository.findAll();
        List<ProjectDTO> dtos = new ArrayList<>();
        for(Project p:all){
            ProjectDTO dto = new ProjectDTO(p.getName(), p.getDuration(), p.getDescription());
            dtos.add(dto);
        }
        return dtos;
    }
}

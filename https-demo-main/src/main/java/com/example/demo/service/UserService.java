package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.repo.BlockedUserRepo;
import com.example.demo.repo.EngineerRepo;
import com.example.demo.repo.RoleRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.utils.HMAC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    private final UserRepo userRepository;
    private final RoleRepo roleRepository;
    private final  EmailService emailService;
    private final  RegistrationTokenService registrationTokenService;
    private final HMAC HMACService;
    private final BlockedUserRepo blockedUserRepository;
    private final ProjectService projectService;
    private final EngineerRepo engineerRepo;

    public UserService(UserRepo userRepository, RoleRepo roleRepository, EmailService emailService, RegistrationTokenService registrationTokenService, HMAC hmacService, BlockedUserRepo blockedUserRepository, ProjectService projectService, EngineerRepo engineerRepo) {
        this.userRepository = userRepository;

        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.registrationTokenService = registrationTokenService;
        HMACService = hmacService;
        this.blockedUserRepository = blockedUserRepository;
        this.projectService = projectService;
        this.engineerRepo = engineerRepo;
    }

    public User findByUserEmail(String userEmail) {
        List<User> users = findAll();
        for (User user : users) {
            if (user.getEmail().equals(userEmail)) {
                return user;
            }
        }
        return null;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
    public void registerUser(RegistrationDTO userRegDTO) {
        User user = new User(userRegDTO);
        Collection<Role> roles = new ArrayList<Role>();
        if(userRegDTO.getRole().equals("ROLE_HR_MANAGER")){
            roles.add(roleRepository.findByName("ROLE_HR_MANAGER"));
            user.setRoles(roles);
            userRepository.save(user);
        } else if (userRegDTO.getRole().equals("ROLE_SOFTWARE_ENGINEER")) {
            roles.add(roleRepository.findByName("ROLE_SOFTWARE_ENGINEER"));
            EngineerProfile eng = new EngineerProfile(userRegDTO);
            eng.setRoles(roles);
            userRepository.save(eng);
        } else if (userRegDTO.getRole().equals("ROLE_PROJECT_MANAGER")) {
            roles.add(roleRepository.findByName("ROLE_PROJECT_MANAGER"));
            ProjectManagerProfile projectManagerProfile = new ProjectManagerProfile(userRegDTO);
            projectManagerProfile.setRoles(roles);
            userRepository.save(projectManagerProfile);
        }
    }
    public void registerAdmin(RegistrationDTO userRegDTO) {
        User user = new User(userRegDTO);
        Collection<Role> roles = new ArrayList<Role>();
        roles.add(roleRepository.findByName("ROLE_ADMIN"));
        user.setRoles(roles);
        user.setActive(true);
        userRepository.save(user);
    }
    public void editAdmin(RegistrationDTO userRegDTO) {
        User user = userRepository.findByEmail(userRegDTO.getEmail());
        user.setFirstName(userRegDTO.getFirstName());
        user.setLastName(userRegDTO.getLastName());
        user.setPhoneNumber(userRegDTO.getPhoneNumber());
        if(userRegDTO.getPassword()!=null){
            user.setPassword(userRegDTO.getPassword());
        }
        user.setTitle(userRegDTO.getTitle());
        user.getAddress().setCountry(userRegDTO.getCountry());
        user.getAddress().setCity(userRegDTO.getCity());
        user.getAddress().setStreet(userRegDTO.getStreet());
        user.getAddress().setStreetNumber(userRegDTO.getStreetNumber());
        userRepository.save(user);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void approveRegistrationRequest(String email) {
        User user = userRepository.findByEmail(email);
        emailService.sendRegistrationEmail(user);
    }

    public void denyRegistrationRequest(String email, String reason) {
        User user = userRepository.findByEmail(email);
        emailService.sendRegistrationDeniedEmail(email, reason);
        BlockedUser bu = new BlockedUser();
        bu.setEmail(user.getEmail());
        bu.setBlockedUntil(LocalDateTime.now().plusMonths(1));
        blockedUserRepository.save(bu);
        userRepository.delete(user);
    }
    public boolean verifyUser(String token, String hmac) throws Exception{
        RegistrationToken secureToken = registrationTokenService.findByToken(token);
        User user = userRepository.getOne(secureToken.getUser().getId());
        if (Objects.isNull(user) || Objects.isNull(secureToken)) {
            return false;
        }
        if(secureToken.isExpired()) {
            registrationTokenService.removeToken(secureToken);
            userRepository.delete(user);
        }
        if (Objects.isNull(secureToken) || !token.equals(secureToken.getToken()) || secureToken.isExpired()) {
            throw new Exception("Invalid token");
        }

        // Verify the HMAC
        boolean hmacValid = HMACService.verifyHmac(user.getEmail(), hmac.replace(" ", "+"), "my_secret_key");
        if (!hmacValid) {
            return false;
        }
        user.setActive(true);
        if(user.getRoles().stream().iterator().next().getName().equals("ROLE_SOFTWARE_ENGINEER"))
            user.setDateOfEmployment(LocalDate.now());
        userRepository.save(user);
        //brise se da bi moglo samo jednom da se iskoristi
        registrationTokenService.removeToken(secureToken);
        return true;
    }
    public boolean isBlocked(String email){
        BlockedUser bu = blockedUserRepository.findByEmail(email);
        if(bu == null)
            return false;
        return bu.isBlocked();
    }

    public List<EmployeeDTO> getAll(){
        ArrayList<User> all = (ArrayList<User>) userRepository.findAllByIsActive(true);
        List<EmployeeDTO> dtos = new ArrayList<>();
        for(User u:all){
            EmployeeDTO dto = new EmployeeDTO(u.getEmail(),u.getFirstName(), u.getLastName(), u.getPhoneNumber(), u.getAddress(), u.getTitle(), u.getRoles().iterator().next().getName());
            dtos.add(dto);
        }
        return dtos;
    }
    public List<RegistrationRequestDTO> getRegistrationRequests(){
        ArrayList<User> all = (ArrayList<User>) userRepository.findAllByIsActive(false);
        ArrayList<RegistrationToken> approved = (ArrayList<RegistrationToken>) registrationTokenService.registrationTokenRepository.findAll();
        List<RegistrationRequestDTO> dtos = new ArrayList<>();
        Iterator<User> iterator1 = all.iterator();
        while (iterator1.hasNext()) {
            User element1 = iterator1.next();
            Long id1 = element1.getId();
            Iterator<RegistrationToken> iterator2 = approved.iterator();
            while (iterator2.hasNext()) {
                RegistrationToken element2 = iterator2.next();
                if(element2.getUser()!=null) {
                    Long id2 = element2.getUser().getId();
                    if (id1 == id2) {
                        iterator1.remove();
                    }
                }
            }
        }
        for(User u:all){
            RegistrationRequestDTO registrationRequestDTO = new RegistrationRequestDTO(u.getEmail(),u.getFirstName(), u.getLastName(), u.getPhoneNumber(), u.getTitle(), u.getRoles().stream().iterator().next().getName());
            dtos.add(registrationRequestDTO);
        }
        return dtos;
    }
    public List<EngineerDTO> getAllEngineersNotOnProject(String projectId){
        ArrayList<EngineerProfile> eng = (ArrayList<EngineerProfile>) engineerRepo.findAllByIsActive(true);
        ArrayList<ProjectTask> onProject = projectService.getAllProjectTasksIdByProject(projectId);
        List<EngineerDTO> dtos = new ArrayList<>();
        Iterator<EngineerProfile> iterator1 = eng.iterator();
        while (iterator1.hasNext()) {
            EngineerProfile element1 = iterator1.next();
            Long id1 = element1.getId();
            Iterator<ProjectTask> iterator2 = onProject.iterator();
            while (iterator2.hasNext()) {
                ProjectTask element2 = iterator2.next();
                if(element2.getEngineerProfile()!=null) {
                    Long id2 = element2.getEngineerProfile().getId();
                    if (id1 == id2) {
                        iterator1.remove();
                    }
                }
            }
        }
        for(User engineer: eng){
            EngineerDTO dto = new EngineerDTO(engineer.getId(),engineer.getFirstName(),engineer.getLastName());
            dtos.add(dto);

        }

        return dtos;
    }
    public List<EngineerDTO> getAllEngineersOnProject(String projectId) {
        //ArrayList<User> all = (ArrayList<User>) userRepository.findAllByIsActive(true);
        List<EngineerProfile> eng = engineerRepo.findAllByIsActive(true);
        ArrayList<ProjectTask> onProject = projectService.getAllProjectTasksIdByProject(projectId);
        List<EngineerDTO> dtos = new ArrayList<>();
        for (User u : eng) {
            for (ProjectTask t : onProject)
                if (u.getRoles().stream().iterator().next().getName().equals("ROLE_SOFTWARE_ENGINEER") && t.getEngineerProfile().getEmail().equals(u.getEmail()))
                {
                    EngineerDTO dto = new EngineerDTO(u.getId(),u.getFirstName(),u.getLastName(),t.getTaskName(), t.getDescription(), t.getStartDate(), t.getEndDate(), t.getId());
                    dtos.add(dto);
                }
        }
        return dtos;
    }
    public boolean CheckPermissionForRole(Authentication authentication,String privilege) {

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals(privilege)) {
                return true; // User has the required privilege
            }

        }
        return false;
    }
    public boolean isInitial(User user){
        return (user.isInitialAdmin() && !user.isChangedPassword());
    }
    public void changeInitialPassword(User user, String pass){
        user.setPassword(pass);
        user.setChangedPassword(true);
        userRepository.save(user);
    }
    public List<EngineerSearchDTO> searchEngineers(EngineerSearchDTO data) {
        List<EngineerProfile> eng = engineerRepo.findAllByIsActive(true);
        List<EngineerSearchDTO> dtos = new ArrayList<>();
        for(EngineerProfile e : eng) {
            boolean match = true;
            if (isNotBlank(data.getName()) && !e.getFirstName().toLowerCase().contains(data.getName().toLowerCase())) {
                match = false;
            }
            if (isNotBlank(data.getSurname()) && !e.getLastName().toLowerCase().contains(data.getSurname().toLowerCase())) {
                match = false;
            }
            if (isNotBlank(data.getEmail()) && !e.getEmail().contains(data.getEmail())) {
                match = false;
            }
            if(data.getEmployedFrom()!=null && e.getDateOfEmployment().isBefore(data.getEmployedFrom().plusDays(1))) {
                match = false;
            }
            if(data.getEmployedTo()!=null && e.getDateOfEmployment().isAfter(data.getEmployedTo().plusDays(1))){
                match = false;
            }
            if (match) {
                dtos.add(new EngineerSearchDTO(e.getFirstName(), e.getLastName(), e.getEmail(), e.getDateOfEmployment(), null));
            }
        }
        return dtos;
    }
    public List<EngineerSearchDTO> getEngineersForSearch(){
        List<EngineerProfile> eng = engineerRepo.findAllByIsActive(true);
        List<EngineerSearchDTO> dtos = new ArrayList<>();
        for(EngineerProfile e:eng){
            EngineerSearchDTO dto = new EngineerSearchDTO(e.getFirstName(), e.getLastName(), e.getEmail(), e.getDateOfEmployment(), null);
            dtos.add(dto);
        }
        return dtos;
    }
    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}

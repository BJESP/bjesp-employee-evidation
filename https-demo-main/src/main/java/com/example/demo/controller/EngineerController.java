package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.security.KeyStoreConfig;
import com.example.demo.service.EngineerService;
import com.example.demo.utils.PasswordValidator;

import com.example.demo.service.UserService;
import com.example.demo.utils.UserValidation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;
import java.security.spec.X509EncodedKeySpec;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/engineer")
public class EngineerController {

    private static final String RSA_PRIVATE_KEY_FILE = "private.key";
    private static final String RSA_PUBLIC_KEY_FILE = "public.key";

    private final KeyStoreConfig keyStoreConfig;

    @Autowired
    EngineerService engineerService;
    @Autowired
    UserService userService;

    @Autowired
    private PasswordValidator passwordValidator;
    @Autowired
    UserValidation userValidation;

    private Logger logger =  LogManager.getLogger(EngineerController.class);

    public EngineerController(KeyStoreConfig keyStoreConfig) {
        this.keyStoreConfig = keyStoreConfig;
    }

    //ALSO USE FOR CREATE
    @PostMapping(value="/update-engineer-skill")
    @PreAuthorize("hasPermission(#engineerSkillDTO.rating, 'Engineer_Skill', 'UPDATE')")
    public ResponseEntity UpdateEngineerSkill(@RequestBody EngineerSkillDTO engineerSkillDTO, HttpServletRequest request)
    {

            try {
                logger.info("Updating engineer skill");
                userValidation.validRating(engineerSkillDTO.getRating());

                Skill createdSkill = engineerService.UpdateEngineerSkill(engineerSkillDTO);

                if (createdSkill == null) {
                    logger.warn("Skill wasn't updated successfully ");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                return new ResponseEntity<>(createdSkill, HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                logger.warn(e.getMessage()+"during updating engineer skill");
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }



    }
    @PostMapping(value="/create-engineer-skill")
    @PreAuthorize("hasPermission(#engineerSkillDTO.rating, 'Engineer_Skill', 'CREATE')")
    public ResponseEntity CreateEngineerSkill(@RequestBody EngineerSkillDTO engineerSkillDTO,HttpServletRequest request)
    {

            try {
                logger.info("Creating skil for  engineer ");
                userValidation.validRating(engineerSkillDTO.getRating());

                Skill createdSkill = engineerService.UpdateEngineerSkill(engineerSkillDTO);

                if (createdSkill == null) {
                    logger.error("Skill wasn't created successfully");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                return new ResponseEntity<>(createdSkill, HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                logger.error(e.getMessage()+"during creating engineer skill");
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }

    }

   // @PreAuthorize("hasPermission(#username,'CVDocument', 'READ')")
    @PostMapping(value="/load-engineer-cv")
    public ResponseEntity<byte[]> loadEngineerCv(@RequestBody PasswordlessLoginDTO enginnerEmailDTO) throws IOException {
        try {
           // userValidation.validUserEmail(enginnerEmailDTO.getUsername());

            byte[] encryptedCv = engineerService.loadEngineerCv(enginnerEmailDTO.getUsername());
            if (encryptedCv == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            byte[] decryptedCv = decryptDocument(encryptedCv);
            if (decryptedCv == null) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);

            headers.setContentDispositionFormData("attachment", "engineer_cv.pdf");
            System.out.println("CV: " + decryptedCv);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(decryptedCv);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    //ALSO USE FOR CREATE
    @PreAuthorize("hasPermission(#username,'CVDocument', 'UPDATE')")
    @PostMapping(value="/update-engineer-cv")
    public ResponseEntity UpdateEngineerCV(@RequestParam("file") MultipartFile file, @RequestParam("username") String username,HttpServletRequest request) throws IOException
        {
            try {
                logger.info("Updating engineer CV");
                userValidation.validUserEmail(username);

                byte[] encryptedFile = encryptDocument(file.getBytes());

                boolean createdCV = engineerService.UpdateEngineerCV(encryptedFile, username);
                if (createdCV == false) {
                    logger.error("CV wasn't updated successfully");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                return new ResponseEntity<>(createdCV, HttpStatus.OK);
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage()+"during updating engineer CV");
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

    private boolean keyFilesExist() {
        return Files.exists(Paths.get(RSA_PRIVATE_KEY_FILE)) && Files.exists(Paths.get(RSA_PUBLIC_KEY_FILE));
    }

    public byte[] encryptDocument(byte[] document) {
        try {
            System.out.println("DOCKUMENT: " + document);
            // Generisanje AES ključa
            SecretKey aesKey = generateAESKey();

            // Šifrovanje dokumenta koristeći AES ključ
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encryptedDocument = aesCipher.doFinal(document);

          /*  // Provera da li postoje RSA ključevi
            if (!keyFilesExist())
            {
                // Generisanje RSA ključeva
                KeyPair rsaKeyPair = generateRSAKeyPair();

                // Čuvanje RSA ključeva
                savePrivateKey(rsaKeyPair.getPrivate(), RSA_PRIVATE_KEY_FILE);
                savePublicKey(rsaKeyPair.getPublic(), RSA_PUBLIC_KEY_FILE);
            }*/

            System.out.println("1");
            System.out.println("2");


            // Enkripcija AES ključa koristeći RSA javni ključ
            Cipher rsaCipher = Cipher.getInstance("RSA");
            System.out.println("3");
            System.out.println("3.1 " +  loadKeyFromKeyStore(keyStoreConfig).getPublic());
            rsaCipher.init(Cipher.ENCRYPT_MODE, loadKeyFromKeyStore(keyStoreConfig).getPublic());
            System.out.println("4");

            byte[] encryptedAESKey = rsaCipher.doFinal(aesKey.getEncoded());
            System.out.println("5");

            // Kombinacija šifrovanog dokumenta i šifrovanog AES ključa
            byte[] combinedData = new byte[encryptedDocument.length + encryptedAESKey.length];
            System.arraycopy(encryptedDocument, 0, combinedData, 0, encryptedDocument.length);
            System.arraycopy(encryptedAESKey, 0, combinedData, encryptedDocument.length, encryptedAESKey.length);

            return combinedData;
        } catch (Exception e) {
            System.out.println("BAGCINA: " + e);
            // Handle exception appropriately
        }

        return null;
    }

    public static KeyPair loadKeyFromKeyStore(KeyStoreConfig keyStoreConfig) throws Exception {
        try {
            System.out.println("3.2");
            KeyStore keyStore = KeyStore.getInstance("JKS");
            System.out.println("3.3");
            System.out.println("FN: " + keyStoreConfig.getPikulaFileName());
            System.out.println("PS: " +keyStoreConfig.getPikulaKsPassword().toCharArray());
            keyStore.load(new FileInputStream(keyStoreConfig.getPikulaFileName()), keyStoreConfig.getPikulaKsPassword().toCharArray());

            System.out.println("3.4");
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyStoreConfig.getPikulaAlias(), new KeyStore.PasswordProtection(keyStoreConfig.getPikulaKsPassword().toCharArray()));
            System.out.println("3.5");
            PrivateKey privateKey = privateKeyEntry.getPrivateKey();
            System.out.println("3.6");
            PublicKey publicKey = privateKeyEntry.getCertificate().getPublicKey();
            System.out.println("3.7");
            return new KeyPair(publicKey, privateKey);

        } catch (Exception e) {
        System.out.println("BAGCINA2: " + e);
        // Handle exception appropriately
        }
        return null;
    }

    private SecretKey generateAESKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            // Handle exception appropriately
        }

        return null;
    }

    private KeyPair generateRSAKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            // Handle exception appropriately
        }

        return null;
    }

    private byte[] decryptDocument(byte[] combinedData) {
        try {
            // Load RSA private key

            PrivateKey privateKey = (PrivateKey) loadKeyFromKeyStore(keyStoreConfig).getPrivate();

            // Razdvajanje šifrovanog dokumenta i šifrovanog AES ključa
            int encryptedDocumentLength = combinedData.length - 256; // 256 bytes for RSA encrypted AES key
            byte[] encryptedDocument = new byte[encryptedDocumentLength];
            byte[] encryptedAESKey = new byte[256];
            System.arraycopy(combinedData, 0, encryptedDocument, 0, encryptedDocumentLength);
            System.arraycopy(combinedData, encryptedDocumentLength, encryptedAESKey, 0, 256);

            // Dekriptovanje AES ključa koristeći RSA privatni ključ
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedAESKey = rsaCipher.doFinal(encryptedAESKey);

            // Dekriptovanje dokumenta koristeći AES ključ
            SecretKey aesKey = new SecretKeySpec(decryptedAESKey, "AES");
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey);

            return aesCipher.doFinal(encryptedDocument);
        } catch (Exception e) {
            // Handle exception appropriately
        }

        return null;
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

    private void savePrivateKey(java.security.PrivateKey privateKey, String fileName) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            fileOutputStream.write(privateKey.getEncoded());
        } catch (Exception e) {
            // Handle exception appropriately
        }
    }

    private void savePublicKey(java.security.PublicKey publicKey, String fileName) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            fileOutputStream.write(publicKey.getEncoded());
        } catch (Exception e) {
            // Handle exception appropriately
        }
    }

    private PrivateKey loadPrivateKey(String fileName) {
        try {
            Path path = Paths.get(fileName);
            byte[] keyBytes = Files.readAllBytes(path);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            // Handle exception appropriately
        }

        return null;
    }

    private PublicKey loadPublicKey(String fileName) {
        try {
            Path path = Paths.get(fileName);
            byte[] keyBytes = Files.readAllBytes(path);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            // Handle exception appropriately
        }

        return null;
    }


    @PostMapping(value="/create-engineer-cv")
    @PreAuthorize("hasPermission(#username,'CVDocument', 'CREATE')")

    public ResponseEntity CreateEngineerCV(@RequestParam("file") MultipartFile file, @RequestParam("username") String username) throws IOException
    {

        try {
            userValidation.validUserEmail(username);

            byte[] encryptedFile = encryptDocument(file.getBytes());

            boolean createdCV = engineerService.UpdateEngineerCV(encryptedFile, username);
            if (createdCV == false) {
                logger.error("CV wasn't created successfully");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }


            logger.info("Created engineer CV");
                return new ResponseEntity<>(createdCV, HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                logger.error(e.getMessage()+"during creating engineer CV");
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

            }



    }

    @PostMapping(value="/get-engineer-skills")
    @PreAuthorize("hasPermission(#enginnerEmailDTO,'Skill', 'READ')")
    public ResponseEntity GetSkillsForEngineer(@RequestBody PasswordlessLoginDTO enginnerEmailDTO,HttpServletRequest request)
    {

            try
            {
                logger.info("Getting skills for engineer");
                userValidation.validPasswordlessLoginDTO(enginnerEmailDTO);
                List<Skill> skillsList = engineerService.GetSkillsForEnginner(enginnerEmailDTO);
                return new ResponseEntity<>(skillsList, HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                logger.error("");
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }


    }



    @PostMapping(value="/get-project-tasks")
    @PreAuthorize("hasPermission(#enginnerEmailDTO,'ProjectTask', 'READ')")
    public ResponseEntity GetProjectTasksForEngineer(@RequestBody PasswordlessLoginDTO enginnerEmailDTO,HttpServletRequest request) {
        try {
            logger.info("Getting project task for engineer");
            userValidation.validPasswordlessLoginDTO(enginnerEmailDTO);
            List<ProjectTask> projectTaskList = engineerService.GetProjectTasksForEnginner(enginnerEmailDTO);
            return new ResponseEntity<>(projectTaskList, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



    @PostMapping(value="/get-project-and-project-tasks")
    @PreAuthorize("hasPermission(#enginnerEmailDTO,'ProjectTask', 'READ')")
    public ResponseEntity GetProjectWithProjectTasksForEngineer(@RequestBody PasswordlessLoginDTO enginnerEmailDTO)
    {

            try
            {
                userValidation.validPasswordlessLoginDTO(enginnerEmailDTO);
                List<EngineerProjectWithProjectTaskDTO> projectTaskList = engineerService.GetProjectWithProjectTasksForEnginner(enginnerEmailDTO);
                return new ResponseEntity<>(projectTaskList, HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }

    }

    @PostMapping(value="/update-project-task")
    @PreAuthorize("hasPermission(#requestDTO,'ProjectTask', 'UPDATE')")
    public ResponseEntity UpdateProjectTaskForEngineer(@RequestBody UpdateProjectTaskRequestDTO requestDTO,HttpServletRequest request){

            try {
                logger.info("Updating engineer project task ");
                userValidation.validUpdateProjectTaskRequestDTO(requestDTO);
                boolean changed = engineerService.UpdateProjectTaskForEngineer(requestDTO);
                if (!changed) {
                    logger.error("Can't update project task for engineer");
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                return new ResponseEntity<>(HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                logger.error(e.getMessage()+"during project task for engineer");
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

            }

    }

    public boolean CheckPermissionForRole(String privilege){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasPermission = userService.CheckPermissionForRole(authentication,privilege);
        return hasPermission;

    }


    @PostMapping(value="/account-details")
    @PreAuthorize("hasPermission(#enginnerEmailDTO,'EngineerProfile', 'READ')")
    public ResponseEntity getAccountDetails(@RequestBody PasswordlessLoginDTO enginnerEmailDTO, HttpServletRequest request) {

            try
            {
                logger.info("Engineer account information");
                userValidation.validPasswordlessLoginDTO(enginnerEmailDTO);
                EngineerAccountDetailsDTO accountDetails = engineerService.GetAccountDetails(enginnerEmailDTO.getUsername());
                if (accountDetails == null) {
                    logger.error("Engineer account information doesn't exist");
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                return new ResponseEntity<>(accountDetails, HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                logger.error(e.getMessage()+"during getting account details");
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }


    }

    @PreAuthorize("hasPermission(#engineerAccountDetailsDTO,'EngineerProfile', 'UPDATE')")
    @PostMapping(value="/account-details-update")
    public ResponseEntity updateAccountDetails(@RequestBody EngineerAccountDetailsDTO engineerAccountDetailsDTO, HttpServletRequest request) {

            try {

                userValidation.validUpdateEngineerAccountDetailsDTO(engineerAccountDetailsDTO);
                logger.info("Updating engineer account ");
                System.out.println("APDEJTUJEM DETALJE");
                if (engineerAccountDetailsDTO.getPassword() != null) {
                    if (!passwordValidator.isValid(engineerAccountDetailsDTO.getPassword())) {

                        logger.error("Invalid pasword, doesn't meet all requirements");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                }

                boolean changed = engineerService.UpdateAccountDetails(engineerAccountDetailsDTO);
                if (!changed) {
                    logger.error("Cant update engineer account");
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                return new ResponseEntity<>(HttpStatus.OK);
            }
            catch (IllegalArgumentException e)
            {
                logger.error(e.getMessage()+"during updating account details");
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }



}

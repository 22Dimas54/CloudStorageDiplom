package ru.netology.diplom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplom.entity.StorageFile;
import ru.netology.diplom.entity.User;
import ru.netology.diplom.repository.RoleRepository;
import ru.netology.diplom.repository.StorageFileRepository;
import ru.netology.diplom.repository.UserRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final Path root = Paths.get("storage");
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    StorageFileRepository storageFileRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    private final String REGISTRATION_ROLE = "ROLE_USER";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUserName(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }
        return user.get();
    }

    public boolean saveUser(String userName, String password) {
        Optional<User> userFromDB = userRepository.findByUserName(userName);

        if (!userFromDB.isPresent()) {
            return false;
        }

        User user = new User();
        user.setRoles(Collections.singleton(roleRepository.findByName(REGISTRATION_ROLE)));
        user.setUserName(userName);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
        return true;
    }

    public ResponseEntity uploadFile(MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
                storageFileRepository.save(new StorageFile(file.getOriginalFilename(), file.getSize(), new Date()));
                return new ResponseEntity(HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    public List<StorageFile> showAllFiles(Integer limit) {
        return storageFileRepository.findAll(PageRequest.of(0, limit)).getContent();
    }

    public ResponseEntity deleteFile(String filename) {
        var foundStorageFile = storageFileRepository.findByName(filename);
        if (foundStorageFile != null) {
            try {
                Path fileToDeletePath = Paths.get(root + File.separator + foundStorageFile.getName());
                Files.delete(fileToDeletePath);
                storageFileRepository.deleteById(foundStorageFile.getId());
                return new ResponseEntity(HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    public StorageFile findByName(String filename) {

        return storageFileRepository.findByName(filename);
    }

    public InputStreamResource download(String name) throws IOException {
        File file = new File(root + File.separator + name);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new IOException();
        }
    }

    public ResponseEntity<StorageFile> putFile(String name, String putNameFile) {
        var foundStorageFile = storageFileRepository.findByName(name);
        if (foundStorageFile == null) {
            return new ResponseEntity<StorageFile>(HttpStatus.NOT_FOUND);
        }
        File oldFile = new File(root + File.separator + foundStorageFile.getName());
        File newFile = new File(root + File.separator + putNameFile);
        if (oldFile.renameTo(newFile)) {
            foundStorageFile.setName(putNameFile);
            foundStorageFile.setChangeDate(new Date());
            storageFileRepository.save(foundStorageFile);
        }
        return new ResponseEntity<StorageFile>(HttpStatus.OK);
    }
}


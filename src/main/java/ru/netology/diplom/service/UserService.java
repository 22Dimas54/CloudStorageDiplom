package ru.netology.diplom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
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
        User user = userRepository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public boolean saveUser(String userName, String password) {
        User userFromDB = userRepository.findByUserName(userName);

        if (userFromDB != null) {
            return false;
        }

        User user = new User();
        user.setRoles(Collections.singleton(roleRepository.findByName(REGISTRATION_ROLE)));
        user.setUserName(userName);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
        return true;
    }

    public String uploadFile(MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
                storageFileRepository.save(new StorageFile(file.getOriginalFilename(), file.getSize(), new Date()));
                return "Вы удачно загрузили " + file.getOriginalFilename() + " !";
            } catch (Exception e) {
                return "Вам не удалось загрузить " + file.getOriginalFilename() + " => " + e.getMessage();
            }
        } else {
            return "Вам не удалось загрузить " + file.getOriginalFilename() + " потому что файл пустой.";
        }
    }

    public List<StorageFile> showAllFiles() {
        return storageFileRepository.findBy();
    }

    public ResponseEntity<Void> deleteFile(Long id) {
        if (storageFileRepository.existsById(id)) {
            try {
                Path fileToDeletePath = Paths.get(root + File.separator + storageFileRepository.findById(id).get().getName());
                Files.delete(fileToDeletePath);
                storageFileRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public Optional<StorageFile> findById(Long id) {
        return storageFileRepository.findById(id);
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

    public Object putFile(Long id, String name) {
        Optional<StorageFile> foundStorageFile = storageFileRepository.findById(id);
        if (foundStorageFile.isEmpty()){
            return new ResponseEntity<StorageFile>(HttpStatus.NOT_FOUND);
        }
        StorageFile storageFile = foundStorageFile.get();
        File oldFile = new File(root + File.separator + storageFile.getName());
        File newFile = new File(root + File.separator + name);
        if (oldFile.renameTo(newFile)) {
            storageFile.setName(name);
            storageFile.setChangeDate(new Date());
            storageFileRepository.save(storageFile);
        }
        return storageFile;
    }

    public String show() {
        return "test";
    }
}

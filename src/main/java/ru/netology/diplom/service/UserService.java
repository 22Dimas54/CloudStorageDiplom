package ru.netology.diplom.service;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final Path root = Paths.get("uploads");
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

    public Object showAllFiles() {
        return storageFileRepository.findBy();
    }

    public String deleteFile(Long id) {
        if (storageFileRepository.existsById(id)) {
            try {
                storageFileRepository.deleteById(id);
                return String.format("The file with the ID %s has been successfully deleted", id);
            } catch (Exception e) {
                return String.format("File with id %s could not be deleted", id);
            }

        } else {
            return String.format("File with ID %s not found", id);
        }
    }
}

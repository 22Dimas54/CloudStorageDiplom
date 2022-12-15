package ru.netology.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.diplom.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);
}

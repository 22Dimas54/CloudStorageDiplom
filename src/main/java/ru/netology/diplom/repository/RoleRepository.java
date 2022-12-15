package ru.netology.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.diplom.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}

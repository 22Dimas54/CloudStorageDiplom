package ru.netology.diplom.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.diplom.entity.StorageFile;
import ru.netology.diplom.entity.User;

import java.util.List;

public interface StorageFileRepository extends JpaRepository<StorageFile, Long> {
    List<StorageFile> findBy();

    StorageFile findByName(String fileName);

    List<StorageFile> findAllByUser(PageRequest of, User user);
}

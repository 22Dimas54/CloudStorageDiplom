package ru.netology.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.diplom.entity.StorageFile;

import java.util.List;


public interface StorageFileRepository extends JpaRepository<StorageFile, Long> {
    List<StorageFile> findBy();

    StorageFile findByName(String fileName);
}

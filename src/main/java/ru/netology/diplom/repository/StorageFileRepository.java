package ru.netology.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.diplom.entity.StorageFile;

public interface StorageFileRepository extends JpaRepository<StorageFile, Long> {
}

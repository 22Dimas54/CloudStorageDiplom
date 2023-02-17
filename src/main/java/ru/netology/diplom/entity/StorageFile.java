package ru.netology.diplom.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "storage_files")
public class StorageFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private Long size;

    @Column
    private Date changeDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public StorageFile(String name, Long size, Date changeDate, User user) {
        this.name = name;
        this.size = size;
        this.changeDate = changeDate;
        this.user = user;
    }

    public StorageFile(String name, long size, Date changeDate) {
        this.name = name;
        this.size = size;
        this.changeDate = changeDate;
    }
}

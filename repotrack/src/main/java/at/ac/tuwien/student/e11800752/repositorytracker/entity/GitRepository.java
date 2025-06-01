package at.ac.tuwien.student.e11800752.repositorytracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "REPOSITORIES")
public class GitRepository {

    @Column(nullable = false)
    private String name;

    @Id
    @Column(nullable = false, unique = true)
    private String url;

    @OneToMany(mappedBy = "gitRepository")
    private List<GitCommit> commits;
}

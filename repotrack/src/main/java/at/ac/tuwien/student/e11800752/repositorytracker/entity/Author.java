package at.ac.tuwien.student.e11800752.repositorytracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "AUTHORS")
public class Author {

    private String firstName;

    private String lastName;

    @Id
    @Column(unique = true, nullable = false)
    private String email;

    @OneToMany(mappedBy = "author")
    private List<GitCommit> gitCommits;
}

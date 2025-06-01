package at.ac.tuwien.student.e11800752.repositorytracker.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "COMMITS")
public class GitCommit {

    @Id
    private String commitHash;

    private String commitMessage;

    private Instant commitTimestamp;

    private Boolean isMergeCommit;

    @ManyToOne
    @JoinColumn(name = "repository_id")
    private GitRepository gitRepository;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;
}

package at.ac.tuwien.student.e11800752.repositorytracker.persistence;

import at.ac.tuwien.student.e11800752.repositorytracker.entity.GitCommit;
import org.springframework.data.repository.CrudRepository;

public interface GitCommitDao extends CrudRepository<GitCommit, String> {
}

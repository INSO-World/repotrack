package at.ac.tuwien.student.e11800752.repositorytracker.persistence;

import at.ac.tuwien.student.e11800752.repositorytracker.entity.GitRepository;
import org.springframework.data.repository.CrudRepository;

public interface GitRepositoryDao extends CrudRepository<GitRepository, String> {
}

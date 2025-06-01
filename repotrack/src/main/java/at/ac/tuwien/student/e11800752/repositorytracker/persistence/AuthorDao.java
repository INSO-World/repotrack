package at.ac.tuwien.student.e11800752.repositorytracker.persistence;

import at.ac.tuwien.student.e11800752.repositorytracker.entity.Author;
import org.springframework.data.repository.CrudRepository;

public interface AuthorDao extends CrudRepository<Author, Long> {
}

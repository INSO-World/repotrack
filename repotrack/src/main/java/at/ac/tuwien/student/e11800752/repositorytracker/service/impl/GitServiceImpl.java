package at.ac.tuwien.student.e11800752.repositorytracker.service.impl;

import at.ac.tuwien.student.e11800752.repositorytracker.exception.ServiceException;
import at.ac.tuwien.student.e11800752.repositorytracker.service.GitService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class GitServiceImpl implements GitService {

    @Override
    public void cloneRepository(String remoteRepositoryUrl, File pathToLocalRepository) throws ServiceException {
        try (Git git = Git.cloneRepository()
                .setURI(remoteRepositoryUrl)
                .setDirectory(pathToLocalRepository)
                .setCloneAllBranches(true)
                .setCloneSubmodules(true)
                .call()) {
            // No further action needed... closing resources
        } catch (GitAPIException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void pullRepository(File repository) {
        try (Git git = Git.open(repository)) {
            git.pull();
        } catch (IOException e) {
            throw new RuntimeException("An error has occurred while trying to open a local repository: " + e.getMessage(), e);
        }
    }
}

package at.ac.tuwien.student.e11800752.repositorytracker.service;

import at.ac.tuwien.student.e11800752.repositorytracker.exception.ServiceException;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;

public interface GitService {


    /**
     * Clones a git repository via URL and stores it in the specified location
     *
     * @param remoteRepositoryUrl the URL of the remote
     * @param pathToLocalRepository the target file location, where the local copy of the repository should be stored
     * @throws ServiceException in case an error happens while cloning the repository
     */
    public void cloneRepository(String remoteRepositoryUrl, File pathToLocalRepository);

    /**
     * Pulls all new changes on the remote repository into the local repository
     *
     * @param repository the local copy of the repository
     */
    public PullResult pullRepository(File repository);
}

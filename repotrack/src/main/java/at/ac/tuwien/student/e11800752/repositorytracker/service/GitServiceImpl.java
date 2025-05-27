package at.ac.tuwien.student.e11800752.repositorytracker.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class GitServiceImpl implements GitService {

    @Override
    public void cloneRepository(String remoteRepositoryUrl, File pathToLocalRepository) throws GitAPIException {
        Git git = Git.cloneRepository()
                .setURI(remoteRepositoryUrl)
                .setDirectory(pathToLocalRepository)
                .setCloneAllBranches(true)
                .call();
    }
}

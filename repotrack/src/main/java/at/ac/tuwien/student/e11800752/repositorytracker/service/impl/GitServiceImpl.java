package at.ac.tuwien.student.e11800752.repositorytracker.service.impl;

import at.ac.tuwien.student.e11800752.repositorytracker.exception.ServiceException;
import at.ac.tuwien.student.e11800752.repositorytracker.service.AuthenticationService;
import at.ac.tuwien.student.e11800752.repositorytracker.service.GitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitServiceImpl implements GitService {

    private final AuthenticationServiceImpl authService;

    @Override
    public void cloneRepository(String remoteRepositoryUrl, File pathToLocalRepository) {
        log.info("Cloning repository " + remoteRepositoryUrl);
        try (Git git = Git.cloneRepository()
                .setCredentialsProvider(authService.getGitCredentials())
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
    public PullResult pullRepository(File repository) {
        log.info("Pulling repository " + repository.getName());
        try (Git git = Git.open(repository)) {
            return git.pull().setCredentialsProvider(authService.getGitCredentials()).call();
        } catch (IOException e) {
            throw new ServiceException("An error has occurred while trying to open a local repository: " + e.getMessage(), e);
        } catch (GitAPIException e) {
            throw new ServiceException("An error has occurred while trying to pull from remote: " + e.getMessage(), e);
        }
    }
}

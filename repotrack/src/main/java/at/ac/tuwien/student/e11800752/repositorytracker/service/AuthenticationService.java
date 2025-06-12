package at.ac.tuwien.student.e11800752.repositorytracker.service;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public interface AuthenticationService {

    /**
     * Provides the git credentials configured in the environment
     * @return the credentials
     */
    UsernamePasswordCredentialsProvider getGitCredentials();
}

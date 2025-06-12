package at.ac.tuwien.student.e11800752.repositorytracker.service.impl;

import at.ac.tuwien.student.e11800752.repositorytracker.service.AuthenticationService;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${git.user.password}")
    private String gitUserPassword;

    @Value("${git.user.name}")
    private String gitUserUsername;

    @Override
    public UsernamePasswordCredentialsProvider getGitCredentials() {
        return new UsernamePasswordCredentialsProvider(gitUserUsername, gitUserPassword);
    }

}

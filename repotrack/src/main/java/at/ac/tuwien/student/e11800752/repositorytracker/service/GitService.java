package at.ac.tuwien.student.e11800752.repositorytracker.service;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

public interface GitService {


    public void cloneRepository(String remoteRepositoryUrl, File pathToLocalRepository) throws GitAPIException;
}

package at.ac.tuwien.student.e11800752.repositorytracker.service.impl;

import at.ac.tuwien.student.e11800752.repositorytracker.entity.Author;
import at.ac.tuwien.student.e11800752.repositorytracker.entity.GitCommit;
import at.ac.tuwien.student.e11800752.repositorytracker.entity.GitRepository;
import at.ac.tuwien.student.e11800752.repositorytracker.exception.ServiceException;
import at.ac.tuwien.student.e11800752.repositorytracker.persistence.AuthorDao;
import at.ac.tuwien.student.e11800752.repositorytracker.persistence.GitCommitDao;
import at.ac.tuwien.student.e11800752.repositorytracker.persistence.GitRepositoryDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitRepositoryProcessor {

    private final GitRepositoryDao gitRepositoryDao;
    private final GitCommitDao gitCommitDao;
    private final AuthorDao authorDao;

    private Git git;
    private GitRepository gitRepository;

    public void process(File rawRepository) {
        try {
            git = Git.open(rawRepository);
        } catch (IOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
        Repository jgitRepository = git.getRepository();
        processRepository(jgitRepository);
    }

    public void processRepository(Repository jgitRepository) {
        log.info("Processing repository");
        String remoteUrl = jgitRepository.getConfig().getString(ConfigConstants.CONFIG_REMOTE_SECTION, "origin", ConfigConstants.CONFIG_KEY_URL);
        String repoName = extractRepoName(remoteUrl);
        if (!gitRepositoryDao.existsById(remoteUrl)) {
            gitRepository = GitRepository.builder().url(remoteUrl).name(repoName).build();
            gitRepositoryDao.save(gitRepository);
        } else {
            gitRepository = gitRepositoryDao.findById(remoteUrl).orElse(null);
        }
        processAllBranches(jgitRepository);
    }

    public void processAllBranches(Repository jgitRepository) {
        log.info("Processing all branches");
        try (RevWalk revWalk = new RevWalk(jgitRepository)) {
            // Add all branch tips as start points
            List<Ref> branchRefs = git.branchList()
                    .setListMode(ListBranchCommand.ListMode.ALL) // Include both local and remote branches
                    .call();

            // Exclude Merge-Commits
            revWalk.setRevFilter(RevFilter.NO_MERGES);

            for (Ref branchRef : branchRefs) {
                processSingularBranch(revWalk, branchRef);
            }
        } catch (GitAPIException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private void processSingularBranch(RevWalk revWalk, Ref branchRef) {
        log.info("Processing branch {}", branchRef.getName());
        try {
            revWalk.markStart(revWalk.parseCommit(branchRef.getObjectId()));
            for (RevCommit commit : revWalk) {
                processCommit(commit);
                revWalk.dispose();
            }
        } catch (IOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private void processCommit(RevCommit revCommit) {
        log.info("Processing commit {}", revCommit.getName());
        // Check if commit was already processed
        if (gitCommitDao.existsById(revCommit.getName())) {
            return; // Skip saving, already present
        }

        // Extract commit metadata
        PersonIdent authorIdent = revCommit.getAuthorIdent();
        Author author = processAndCreateAuthor(authorIdent);
        String message = revCommit.getFullMessage();
        String commitHash = revCommit.getName();
        boolean isMerge = revCommit.getParentCount() > 1;
        GitCommit commit = GitCommit.builder()
                .author(author)
                .isMergeCommit(isMerge)
                .commitMessage(message)
                .commitHash(commitHash)
                .gitRepository(gitRepository)
                .commitTimestamp(revCommit.getCommitterIdent().getWhenAsInstant())
                .build();
        gitCommitDao.save(commit);
    }

    private Author processAndCreateAuthor(PersonIdent authorIdent) {
        Author author = Author.builder().email(authorIdent.getEmailAddress()).firstName(authorIdent.getName()).build();
        authorDao.save(author);
        return author;
    }

    public String extractRepoName(String url) {
        String pattern = ".*/([^/]+?)(?:\\.git)?$";
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(url);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid repository URL: " + url);
    }
}

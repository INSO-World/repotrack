package at.ac.tuwien.student.e11800752.repositorytracker.scheduled;

import at.ac.tuwien.student.e11800752.repositorytracker.exception.ServiceException;
import at.ac.tuwien.student.e11800752.repositorytracker.service.impl.GitRepositoryProcessor;
import at.ac.tuwien.student.e11800752.repositorytracker.service.GitService;
import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class GitRepositoryObserver implements SchedulingConfigurer {

    // Take configured cron expression, or default to every 24 hours
    @Value("${repo.fetch.cron}")
    private String cronExpression;

    // Take ";" separated List of repository URLs
    @Value("#{'${repositories}'.split(';')}")
    private String[] repositoryUrls;

    private final GitService gitService;
    private final GitRepositoryProcessor gitRepositoryProccessor;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        log.info("Configuring update schedule {}", cronExpression);
        taskRegistrar.addTriggerTask(
                () -> syncRepositories(List.of(repositoryUrls)),
                triggerContext -> new CronTrigger(cronExpression).nextExecution(triggerContext)
        );
    }

    public void syncRepositories(List<String> remoteRepositoryURIs) {
        for (String remoteRepositoryURI : remoteRepositoryURIs) {
            String pathToLocalRepository = Hashing.sha256().hashString(remoteRepositoryURI, StandardCharsets.UTF_8).toString();
            handleRepository(remoteRepositoryURI, pathToLocalRepository);
        }
    }

    public void handleRepository(String remoteRepositoryURI, String pathToLocalRepository) {
        File localRepository = new File(pathToLocalRepository);
        PullResult result = null;
        if (localRepository.exists()) {
            result = gitService.pullRepository(localRepository);
        } else {
            gitService.cloneRepository(remoteRepositoryURI, localRepository);
        }
        processRepository(result, localRepository);
    }

    public void processRepository(PullResult pullResult, File localRepository) {
        if (pullResult == null) {
            log.info("Successfully cloned {}, start processing repository", localRepository.getName());
            gitRepositoryProccessor.process(localRepository);
        } else if (pullResult.getMergeResult() != null && pullResult.getMergeResult().getMergeStatus() == MergeResult.MergeStatus.ALREADY_UP_TO_DATE) {
            log.info("Everything is up to date on {}", localRepository.getName());
        } else {
            log.info("New changes found for {}, start processing changes", localRepository.getName());
            gitRepositoryProccessor.process(localRepository);
        }
    }


}

package at.ac.tuwien.student.e11800752.repositorytracker.scheduled;

import at.ac.tuwien.student.e11800752.repositorytracker.exception.ServiceException;
import at.ac.tuwien.student.e11800752.repositorytracker.service.GitRepositoryProcessor;
import at.ac.tuwien.student.e11800752.repositorytracker.service.GitService;
import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        if (localRepository.exists()) {
            pullRepository(localRepository);
        } else {
            cloneRepository(remoteRepositoryURI, localRepository);
        }
        //gitRepositoryProccessor.process(repository);
    }

    public void cloneRepository(String remoteRepositoryURI, File directoryToSaveTo) {
        try {
            gitService.cloneRepository(remoteRepositoryURI, directoryToSaveTo);
        } catch (ServiceException e) {
            log.error("Error fetching repository from URL {}: {}", remoteRepositoryURI, e.getMessage());
        }
    }

    public void pullRepository(File repository) {
        gitService.pullRepository(repository);
    }


}

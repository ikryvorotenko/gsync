package io.gsync.service

import io.gsync.domain.Repo
import io.gsync.repository.RepoRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DefaultRepoScanner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRepoScanner.class);

    SyncService syncService
    RepoConfig repoConfig
    BashService bash
    RepoRepository repoRepository

    @Autowired
    DefaultRepoScanner(
        SyncService syncService, RepoConfig repoConfig, BashService bash, RepoRepository repoRepository
    ) {
        this.bash = bash
        this.repoConfig = repoConfig
        this.syncService = syncService
        this.repoRepository = repoRepository
    }

    @Override
    void run(String... args) throws Exception {
        def file = new File(repoConfig.path)

        logger.info("Scanning $repoConfig.path...")

        def dirs = file.listFiles({
            it.isDirectory()
        } as FileFilter)

        if (!dirs) {
            logger.info("No repositories found")
        }

        dirs.each {
            try {
                logger.info("Found $it.name")
                def svnUrl = bash.call("git svn info --url", it).trim()
                def gitUrl = bash.call("git config --get remote.origin.url", it).trim()

                repoRepository.save(
                    new Repo(name: it.name, svnUrl: svnUrl, gitUrl: gitUrl)
                )
            } catch (Exception e) {
                logger.warn("Could not inti repo by path `$it.name`, caused: ${e.message}")
            }
        }
    }

}

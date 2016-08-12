package io.gsync.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class PullService {

    def SyncService syncService
    def FilesystemRepoService repoService

    @Autowired
    PullService(SyncService syncService, FilesystemRepoService repoService) {
        this.syncService = syncService
        this.repoService = repoService
    }

    @Scheduled(fixedDelay = 300000L)
    def pull() {
        repoService.allRepos().each {
            syncService.pull(it)
        }
    }

}

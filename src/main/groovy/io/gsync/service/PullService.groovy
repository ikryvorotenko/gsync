package io.gsync.service

import io.gsync.repository.RepoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class PullService {

    def SyncService syncService
    def RepoRepository repoService

    @Autowired
    PullService(SyncService syncService, RepoRepository repoService) {
        this.syncService = syncService
        this.repoService = repoService
    }

    @Scheduled(fixedDelay = 300000L)
    def pull() {
        repoService.findAll().each {
            syncService.pull(it)
        }
    }

}

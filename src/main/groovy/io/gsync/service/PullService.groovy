package io.gsync.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class PullService {

    @Value("\${gsync.repos}")
    private String reposPath;

    def SyncService syncService

    @Autowired
    PullService(SyncService syncService) {
        this.syncService = syncService
    }

    @Scheduled(fixedDelay = 300000L)
    def pull() {
        new File(reposPath).eachDir {
            syncService.pull(it)
        }
    }

}

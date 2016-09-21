package io.gsync.web

import io.gsync.domain.Repo
import io.gsync.repository.RepoRepository
import io.gsync.repository.UserRepository
import io.gsync.service.SyncService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sync/{name}")
class SimpleSyncController {

    RepoRepository repoRepository;
    UserRepository userRepository;
    SyncService syncService;

    @Autowired
    SimpleSyncController(RepoRepository repoRepository, SyncService syncService, UserRepository userRepository) {
        this.repoRepository = repoRepository
        this.syncService = syncService
        this.userRepository = userRepository
    }

    @RequestMapping("/push")
    def ResponseEntity<?> push(@PathVariable String name) {
        def repo = repoRepository.findByName(name);
        return ResponseEntity.ok(syncService.push(repo));
    }

    @RequestMapping("/pull")
    def ResponseEntity<?> pull(@PathVariable String name) {
        def repo = repoRepository.findByName(name);

        return ResponseEntity.ok(syncService.pull(repo))
    }

    @RequestMapping("/init")
    def ResponseEntity<?> init(@PathVariable String name) {
        Repo repo = repoRepository.findByName(name)
        return ResponseEntity.ok(
            syncService.init(repo)
        );
    }

    @RequestMapping("/status")
    def ResponseEntity<?> status(@PathVariable String name) {
        Repo repo = repoRepository.findByName(name)

        return ResponseEntity.ok(
            syncService.status(repo)
        )
    }
}

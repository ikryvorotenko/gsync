package io.gsync.web

import io.gsync.domain.Repo
import io.gsync.repository.RepoRepository
import io.gsync.repository.UserRepository
import io.gsync.service.SyncService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sync")
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

    @RequestMapping(
        path = "/push/{repoId}"
    )
    def ResponseEntity<?> push(@PathVariable Long repoId, @RequestParam String message, @RequestParam String username) {
        if (!message) {
            return ResponseEntity.badRequest().body("The message is required")
        }

        def user = userRepository.findByGitUsername(username)

        def repo = repoRepository.findOne(repoId);
        return ResponseEntity.ok(syncService.push(repo, message, user));
    }

    @RequestMapping(
        path = "/pull/{repoId}"
    )
    def ResponseEntity<?> pull(@PathVariable Long repoId) {
        def repo = repoRepository.findOne(repoId);

        return ResponseEntity.ok(syncService.pull(repo))
    }

    @RequestMapping("/init/{repoId}")
    def ResponseEntity<?> init(@PathVariable Long repoId) {
        Repo repo = repoRepository.findOne(repoId)
        return ResponseEntity.ok(
            syncService.init(repo)
        );
    }
}

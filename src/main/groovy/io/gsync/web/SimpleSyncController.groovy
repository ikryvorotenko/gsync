package io.gsync.web

import io.gsync.service.FilesystemRepoService
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

    FilesystemRepoService repoService;
    SyncService syncService;

    @Autowired
    SimpleSyncController(FilesystemRepoService repoService, SyncService syncService) {
        this.repoService = repoService
        this.syncService = syncService
    }

    @RequestMapping(
        path = "/push/{name}"
    )
    def ResponseEntity<?> push(@PathVariable String name, @RequestParam String message) {
        if (!name) {
            return ResponseEntity.badRequest().body("The repository name is required")
        }

        if (!message) {
            return ResponseEntity.badRequest().body("The message is required")
        }

        def repo = repoService.findRepo(name);
        return ResponseEntity.ok(syncService.push(repo, message));
    }

    @RequestMapping(
        path = "/pull/{name}"
    )
    def ResponseEntity<?> pull(@PathVariable String name) {
        if (!name) {
            return ResponseEntity.badRequest().body("The repository name is required")
        }

        def repo = repoService.findRepo(name);

        return ResponseEntity.ok(syncService.pull(repo))
    }
}

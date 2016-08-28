package io.gsync.web

import io.gsync.repository.RepoRepository
import io.gsync.repository.UserRepository
import io.gsync.service.SyncService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/gitLab")
class GitLabSyncController {

    RepoRepository repoRepository;
    UserRepository userRepository;

    SyncService syncService;

    @Autowired
    GitLabSyncController(RepoRepository RepoRepository, SyncService syncService, UserRepository userRepository) {
        this.repoRepository = RepoRepository
        this.syncService = syncService
        this.userRepository = userRepository
    }

    @RequestMapping(
        method = RequestMethod.POST,
        path = "/push"
    )
    def ResponseEntity<?> push(@RequestBody Map<String, Object> param) {
        if (param["object_kind"] != "merge_request") {
            return ResponseEntity.badRequest().body("Not a merge request");
        }

        def attr = param["object_attributes"]
        if (attr["target_branch"] != "master") {
            return ResponseEntity.badRequest().body("Not a master branch");
        }

        if (attr["action"] != "merge") {
            return ResponseEntity.badRequest().body("Not a merge action");
        }

        String name = param['project']?.name
        if (!name) {
            return ResponseEntity.badRequest().body("The attribute `project.name` is required");
        }

        String title = attr["title"];
        if (!title) {
            return ResponseEntity.badRequest().body("The attribute `title` is required");
        }

        def user = userRepository.findByGitUsername(param["user"].username);

        return ResponseEntity.ok(syncService.push(repoRepository.findByName(name), title, user));
    }

}

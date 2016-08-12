package io.gsync.web

import io.gsync.service.SyncService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sync")
class GitLabSyncController {

    @Value("\${gsync.repos}")
    String reposPath;
    SyncService syncService;

    @Autowired
    GitLabSyncController(SyncService syncService) {
        this.syncService = syncService
    }

    @RequestMapping
    def ResponseEntity<?> sync(@RequestBody Map<String, Object> param) {
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

        File repo = new File("$reposPath/${param['project'].name}")
        String title = attr["title"];
        return ResponseEntity.ok(syncService.push(repo, title));
    }

}

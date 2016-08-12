package io.gsync.service

import io.gsync.domain.Repo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SyncService {

    def BashService service;

    @Autowired
    SyncService(BashService service) {
        this.service = service
    }

    def String pull(Repo repo) {
        service.call([
            "git checkout master",
            "git pull",
            "git checkout svnsync",
            "git svn fetch",
            "git svn rebase",
            "git checkout master",
            "git merge svnsync --no-edit",
            "git push"
        ], repo.location)
    }

    def String push(Repo repo, String commitMessage) {
        return service.call([
            'git checkout master',
            'git pull',
            'git checkout svnsync',
            "git merge --no-ff master -m '${commitMessage}'",
            'git svn dcommit',
            'git checkout master',
            'git merge svnsync',
            'git push'
        ], repo.location)
    }

}

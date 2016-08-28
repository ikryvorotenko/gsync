package io.gsync.web

import io.gsync.service.SshService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/config")
class GitConfigController {

    SshService sshService;

    @Autowired
    GitConfigController(SshService sshService) {
        this.sshService = sshService
    }

    @RequestMapping("/ssh")
    def getKey() {
        return sshService.publicKey;
    }

    @RequestMapping("/ssh/create")
    def createKey() {
        return sshService.createKey();
    }


}

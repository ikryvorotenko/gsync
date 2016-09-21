package io.gsync.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SshService {

    BashService bash

    String home = System.getProperty("user.home")

    @Autowired
    SshService(BashService bash) {
        this.bash = bash
    }

    def String getPublicKey() {
        def home = home
        def rsaPath = ".ssh/id_rsa.pub"

        if (!new File("$home/$rsaPath").exists())
            return null

        return bash(["cat $rsaPath"],
            new File(home))
    }

    def String createKey() {
        def home = home
        def rsaPath = ".ssh"

        if (new File("$home/$rsaPath/id_rsa.key").exists())
            throw new IllegalStateException("The ssh key already exists")

        new File("$home/$rsaPath").mkdirs()

        return bash(["ssh-keygen -t rsa -N \"\" -f id_rsa"],
            new File("$home/$rsaPath"))
    }
}

package io.gsync.service

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "gsync.repo")
class RepoConfig {
    String path;

    RepoConfig(String path = null) {
        this.path = path
    }
}

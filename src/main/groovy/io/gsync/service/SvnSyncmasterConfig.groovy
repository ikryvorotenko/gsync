package io.gsync.service

import io.gsync.domain.Credentials
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "gsync.syncmaster.svn")
class SvnSyncmasterConfig extends Credentials {
}

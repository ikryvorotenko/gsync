package io.gsync.web

import com.google.common.io.Resources
import io.gsync.domain.Repo
import io.gsync.service.FilesystemRepoService
import io.gsync.service.SyncService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

class GitLabSyncControllerSpec extends Specification {

    private static final Logger logger = LoggerFactory.getLogger(GitLabSyncControllerSpec.class);

    def SyncService syncService = Mock(SyncService)
    def FilesystemRepoService repoService = Mock(FilesystemRepoService)
    def GitLabSyncController controller = new GitLabSyncController(repoService, syncService)

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build()

    def "it should process request for push"() {
        given:
        def repo = new Repo(new File(""))
        repoService.findRepo(_) >> repo
        def request = Resources.getResource("merge-request.json").text

        when:
        def response = mockMvc.perform(
            post("/gitLab/push")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        then:
        response.status == 200

        1 * syncService.push(repo, "US#1 - Implement some functionality")
    }
}

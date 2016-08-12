package io.gsync.web

import groovy.json.JsonSlurper
import io.gsync.service.SyncService
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

class SyncControllerSpec extends Specification {

    def GitLabSyncController controller
    def SyncService syncService = Mock(SyncService)

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build()

    def setup() {
        controller.syncService = syncService
        controller.reposPath = "reposPath"
    }

    def "it should process request for push"() {
        given: "the merge request merged"
        Map<String, Object> requestBody = new JsonSlurper().parse(new File("merge-request.json"))
        when: "the sync url is hit"
        def response = mockMvc.perform(get("/sync")).andReturn().response
        then:
        1 * syncService.push(new File("reposPath/awesome-project"), "US#1 - Implement some functionality")
        response.status == 200

    }
}

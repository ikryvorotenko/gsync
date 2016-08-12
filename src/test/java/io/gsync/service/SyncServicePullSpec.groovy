package io.gsync.service

import org.junit.Rule
import spock.lang.Specification

class SyncServicePullSpec extends Specification {

    @Rule
    TestRepos repos

    def BashService service = new BashService()
    def SyncService syncService

    void setup() {
        syncService = new SyncService(service)
    }

    def "it should pull the commits from svn to git"() {
        given:
        repos.init()

        expect:
        repos.gitCommits() == 1

        when:
        service.call([
            "echo 'hello world' >> new-file.txt",
            "svn add new-file.txt",
            "svn commit -m 'test commit'"
        ], repos.svnClient)
        syncService.pull(repos.syncRepo)

        then:
        repos.gitCommits() == 2
    }

}

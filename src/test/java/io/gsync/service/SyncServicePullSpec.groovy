package io.gsync.service

import io.gsync.domain.Repo
import org.junit.Rule
import spock.lang.Specification

class SyncServicePullSpec extends Specification {

    @Rule
    TestRepos repos

    BashService service
    SyncService syncService
    Repo repo

    void setup() {
        service = new BashService()
        syncService = new SyncService(
            service,
            new SvnSyncmasterConfig(),
            new RepoConfig(repos.newFolder().path)
        )
        repo = new Repo(1L, "name", "file://${repos.svnRepo.absolutePath}", "file://${repos.gitRepo.absolutePath}")
    }

    def "it should pull the commits from svn to git"() {
        given:
        repos.init()
        syncService.init(repo);

        expect:
        repos.gitCommits() == 1

        when:
        service.call([
            "echo 'hello world' >> new-file.txt",
            "svn add new-file.txt",
            "svn commit -m 'test commit'"
        ], repos.svnClient)
        syncService.pull(this.repo)

        then:
        repos.gitCommits() == 2
    }

}

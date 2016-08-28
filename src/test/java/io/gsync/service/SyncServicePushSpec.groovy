package io.gsync.service

import io.gsync.domain.Repo
import org.junit.Rule
import spock.lang.Specification

class SyncServicePushSpec extends Specification {

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

    def "it should push code to assigned svn"() {
        given:
        repos.init()
        syncService.init(repo)

        expect:
        repos.svnRevision() == 1

        when:
        def commitMessage = "test message"
        repos.'commit file to git repo'(commitMessage);
        syncService.push(repo, commitMessage)

        then:
        repos.svnRevision() == 2
    }

    def "it should not push changes to svn from non-master branch"() {
        given:
        repos.init()
        syncService.init(repo)

        expect:
        repos.svnRevision() == 1

        when:
        def commitMessage = "test message"
        def featureBranch = "feature/branch-1"
        service.call([
            "git fetch",
            "git checkout master",
            "git checkout -b $featureBranch",
            "echo 'hello world' >> feature-file.txt",
            "git add feature-file.txt",
            "git commit . -m '${commitMessage}'",
            "git push -u origin $featureBranch"
        ], repos.gitClient)

        syncService.push(repo, commitMessage)

        then:
        repos.svnRevision() == 1
    }

    def "it should push changes once they are merged from feature branch to master"() {
        given:
        repos.init()
        syncService.init(repo)

        expect:
        repos.svnRevision() == 1

        when:
        def commitMessage = "test message"
        def featureBranch = "feature/branch-1"
        service.call([
            "git fetch",
            "git checkout master",
            "git checkout -b $featureBranch",
            "echo 'hello world' >> feature-file.txt",
            "git add feature-file.txt",
            "git commit . -m '${commitMessage}'",
            "git push -u origin $featureBranch"
        ], repos.gitClient)

        service.call([
            "git fetch",
            "git checkout master",
            "git merge origin/feature/branch-1",
            "git push"
        ], repos.gitClient);

        syncService.push(repo, commitMessage)

        then:
        repos.svnRevision() == 2
    }


}

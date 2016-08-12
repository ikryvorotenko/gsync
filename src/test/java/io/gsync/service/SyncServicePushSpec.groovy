package io.gsync.service

import org.junit.Rule
import spock.lang.Specification

class SyncServicePushSpec extends Specification {

    @Rule
    TestRepos repos

    def BashService service = new BashService()
    def SyncService syncService

    void setup() {
        syncService = new SyncService(service)
    }

    def "it should push code to assigned svn"() {
        given:
        repos.init()

        expect:
        repos.svnRevision() == 1

        when:
        def commitMessage = "test message"
        repos.'commit file to git repo'(commitMessage);
        syncService.push(repos.syncRepo, commitMessage)

        then:
        repos.svnRevision() == 2
    }

    def "it should not push changes to svn from non-master branch"() {
        given:
        repos.init()

        expect:
        repos.svnRevision() == 1

        when:
        def commitMessage = "test message"
        def featureBranch = "feature/branch-1"
        service.call([
            "git checkout master",
            "git checkout -b $featureBranch",
            "echo 'hello world' >> feature-file.txt",
            "git add feature-file.txt",
            "git commit . -m '${commitMessage}'",
            "git push -u origin $featureBranch"
        ], repos.gitClient)

        syncService.push(repos.syncRepo, commitMessage)

        then:
        repos.svnRevision() == 1
    }

    def "it should push changes once they are merged from feature branch to master"() {
        given:
        repos.init()

        expect:
        repos.svnRevision() == 1

        when:
        def commitMessage = "test message"
        def featureBranch = "feature/branch-1"
        service.call([
            "git checkout master",
            "git checkout -b $featureBranch",
            "echo 'hello world' >> feature-file.txt",
            "git add feature-file.txt",
            "git commit . -m '${commitMessage}'",
            "git push -u origin $featureBranch"
        ], repos.gitClient)

        service.call([
            "git checkout master",
            "git merge origin/feature/branch-1",
            "git push"
        ], repos.gitClient);

        syncService.push(repos.syncRepo, commitMessage)

        then:
        repos.svnRevision() == 2
    }


}

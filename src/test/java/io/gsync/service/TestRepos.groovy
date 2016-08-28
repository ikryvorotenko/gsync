package io.gsync.service

import org.junit.rules.TemporaryFolder

public class TestRepos extends TemporaryFolder {

    def File svnRepo
    def File gitRepo
    def File svnClient
    def File gitClient

    def BashService bash = new BashService();

    @Override
    protected void before() throws Throwable {
        super.before()
        svnRepo = newFolder()
        gitRepo = newFolder()
        svnClient = newFolder()
        gitClient = newFolder()
    }

    public void init() {
        'init global git repo'()
        'init global svn repo'()

        'init svn client repo'()
        'commit to svn repo'()

        'init git client repo'()
    }

    public 'commit file to git repo'(commitMessage) {
        bash([
            "git fetch",
            ("git checkout master"),
            ("echo 'hello world' >> test.txt"),
            "git add test.txt",
            ("git commit . -m " + "'${commitMessage}'"),
            "git push"
        ], gitClient)
    }

    public Integer svnRevision() {
        bash([
            "svn update"
        ], svnClient);
        return bash("svnversion", svnClient).toInteger()
    }

    private 'init svn client repo'() {
        bash([
            "svn co file://${svnRepo.absolutePath} .",
        ], svnClient)
    }

    private 'commit to svn repo'() {
        bash([
            "echo 'hello world' >> test-svn.txt",
            "svn add test-svn.txt",
            "svn commit -m 'test commit'"
        ], svnClient)
    }

    private 'init git client repo'() {
        bash([
            "git clone file://${gitRepo.absolutePath} ."
        ], gitClient)
    }

    private 'init global svn repo'() {
        bash("svnadmin create .", svnRepo)
    }

    private 'init global git repo'() {
        bash("git init --bare", gitRepo)
    }

    int gitCommits() {
        bash([
            "git fetch",
            "git checkout master",
            "git pull"
        ], gitClient)

        def process = "git rev-list --count master".execute([], gitClient)
        process.waitFor()
        return process.text.toInteger()
    }
}

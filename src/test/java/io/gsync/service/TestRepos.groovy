package io.gsync.service

import org.junit.rules.TemporaryFolder;

public class TestRepos extends TemporaryFolder {

    def File svnRepo
    def File gitRepo
    def File syncRepo
    def File svnClient
    def File gitClient

    def BashService service = new BashService();

    @Override
    protected void before() throws Throwable {
        super.before()
        svnRepo = newFolder()
        gitRepo = newFolder()
        syncRepo = newFolder()
        svnClient = newFolder()
        gitClient = newFolder()
    }

    public void init() {
        'init global git repo'()
        'init global svn repo'()

        'init svn client repo'()
        'commit to svn repo'()

        'init sync git repo'();

        'init git client repo'()
    }

    public 'commit file to git repo'(commitMessage) {
        service.call([
            ("git checkout master"),
            ("echo 'hello world' >> test.txt"),
            "git add test.txt",
            ("git commit . -m " + "'${commitMessage}'"),
            "git push"
        ], gitClient)
    }

    public Integer svnRevision() {
        service.call([
            "svn update"
        ], svnClient);
        return service.call("svnversion", svnClient).toInteger()
    }

    private  'init svn client repo'() {
        service.call([
            "svn co file://${svnRepo.absolutePath} .",
        ], svnClient)
    }

    private  'commit to svn repo'() {
        service.call([
            "echo 'hello world' >> test-svn.txt",
            "svn add test-svn.txt",
            "svn commit -m 'test commit'"
        ], svnClient)
    }

    private  'init git client repo'() {
        service.call([
            "git clone file://${gitRepo.absolutePath} ."
        ], gitClient)
    }


    private  'init sync git repo'() {
        service.call([
            "git init",
            "git svn init file://${svnRepo.absolutePath}",
            "git checkout -b svnsync",
            "git svn fetch",
            "git checkout -b master",

            "git remote add origin file://${gitRepo.absolutePath}",
            "git push -u origin master"
        ], syncRepo)
    }

    private 'init global svn repo'() {
        service.call("svnadmin create .", svnRepo)
    }

    private 'init global git repo'() {
        service.call("git init --bare", gitRepo)
    }

    int gitCommits() {
        service.call([
            "git checkout master",
            "git pull"
        ], gitClient)

        def process = "git rev-list --count master".execute([], gitClient)
        process.waitFor()
        return process.text.toInteger()
    }
}

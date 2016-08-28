package io.gsync.service

import io.gsync.domain.Credentials
import io.gsync.domain.Repo
import io.gsync.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SyncService {

    def BashService bash;

    def SvnSyncmasterConfig svnSyncmaster;

    def RepoConfig repoConfig;

    @Autowired
    SyncService(
        BashService bash, SvnSyncmasterConfig svnSyncmaster, RepoConfig repoConfig
    ) {
        this.bash = bash
        this.svnSyncmaster = svnSyncmaster
        this.repoConfig = repoConfig
    }

    String init(Repo repo) {
        def file = location(repo)
        file.mkdirs();

        authSvn(repo, svnSyncmaster)

        return bash([
            "git init",
            "git svn init $repo.svnUrl",
            "git checkout -b svnsync",
            "git svn fetch",
            "git checkout -b master",

            "git remote add origin $repo.gitUrl",
            "git push -u origin master"
        ], file)

    }

    private File location(Repo repo) {
        if (!repo.id) {
            throw new IllegalArgumentException("Id is required for initialization");
        }

        new File("$repoConfig.path/$repo.id")
    }

    private String authSvn(Repo repo, Credentials credintials) {
        bash([
            "svn info " +
                "--non-interactive " +
                "--username $credintials.username " +
                "--password $credintials.password $repo.svnUrl"
        ], location(repo))
    }

    String pull(Repo repo) {
        bash.call([
            "git checkout master",
            "git pull",
            "git checkout svnsync",
            "git svn fetch",
            "git svn rebase",
            "git checkout master",
            "git merge svnsync --no-edit",
            "git push"
        ], location(repo))
    }

    String push(Repo repo, String commitMessage, User user = null) {
        if (!user) {
            authSvn(repo, svnSyncmaster)
        } else {
            authSvn(repo, user.svnCredentials)
        }

        return bash.call([
            'git checkout master',
            'git pull',
            'git checkout svnsync',
            "git merge --no-ff master -m '${commitMessage}'",
            'git svn dcommit',
            'git checkout master',
            'git merge svnsync',
            'git push'
        ], location(repo))
    }

}

package io.gsync.service

import io.gsync.domain.Credentials
import io.gsync.domain.Repo
import io.gsync.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SyncService {

    private static final Logger logger = LoggerFactory.getLogger(SyncService.class);

    BashService bash;

    SvnSyncmasterConfig svnSyncmaster;

    RepoConfig repoConfig;

    UserRepository userRepository

    @Autowired
    SyncService(
        BashService bash, SvnSyncmasterConfig svnSyncmaster, RepoConfig repoConfig, UserRepository userRepository
    ) {
        this.bash = bash
        this.svnSyncmaster = svnSyncmaster
        this.repoConfig = repoConfig
        this.userRepository = userRepository
    }

    String init(Repo repo) {
        def file = location(repo)
        file.mkdirs();

        authenticateSvn(repo, svnSyncmaster)

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

    RepoStatus status(Repo repo) {
        def file = location(repo)
        authenticateSvn(repo, svnSyncmaster)

        bash([
            "git svn fetch",
            "git fetch"
        ], file)

        def gitHash = bash("git rev-parse origin/master", file).trim()
        def svnRevision = bash("git svn log --limit=1 --oneline", file).tokenize("|").first().trim()

        def svnRevisionHash = bash("git svn find-rev $svnRevision", file).trim()
        def gitHashRevision = bash("git svn find-rev $gitHash", file).trim()

        def gitExtra = gitExtra(file)
        def svnExtra = svnExtra(file)

        return new RepoStatus(gitHash, svnRevision, svnRevisionHash, gitHashRevision, gitExtra, svnExtra)
    }

    private List<String> svnExtra(File file) {
        bash("git log git-svn --not origin/master --no-merges --oneline --format=%H | " +
            "while read line; do git svn find-rev \$line; done", file).tokenize("\n")
    }

    private List<String> gitExtra(File file) {
        bash("git log origin/master --not git-svn --no-merges --oneline --format=%H", file).tokenize("\n")
    }

    private File location(Repo repo) {
        if (!repo.name) {
            throw new IllegalArgumentException("Name is required for initialization");
        }

        new File("$repoConfig.path/$repo.name")
    }

    private String authenticateSvn(Repo repo, Credentials credintials) {
        bash([
            "svn info " +
                "--non-interactive " +
                "--username $credintials.username " +
                "--password $credintials.password $repo.svnUrl"
        ], location(repo), false)
    }

    String pull(Repo repo) {
        def location = location(repo)
        authenticateSvn(repo, svnSyncmaster)

        bash([
            "git svn fetch",
            "git fetch"
        ], location)

        if (!svnExtra(location)) {
            return "Nothing to pull from svn";
        }

        bash.call([
            "git checkout master",
            "git pull",
            "git checkout svnsync",
            "git svn fetch",
            "git svn rebase",
            "git checkout master",
            "git merge svnsync --no-edit",
            "git push"
        ], location)
    }

    String push(Repo repo) {
        def location = location(repo)

        bash([
            "git svn fetch",
            "git fetch"
        ], location)

        if (!gitExtra(location)) {
            return "Nothing to push to svn";
        }

        def svnExtra = svnExtra(location)
        if (svnExtra) {
            return "There are ${svnExtra.size()} not syncronized commits in SVN. Pull them first."
        }

        bash.call([
            "git checkout master",
            "git pull"], location
        );

        authenticateSvn(repo, user(repo))

        def message = bash.call(["git log -1 --format=%B HEAD"], location)
        return bash.call([
            'git checkout svnsync',
            "git merge --no-ff master -m '${message}'",
            'git svn dcommit',
            'git checkout master',
            'git merge svnsync',
            'git push'
        ], location)
    }

    private Credentials user(Repo repo) {
        def email = bash.call(["git log -1 --format=format:%ae HEAD"], location(repo)).trim()
        def user = userRepository.findByGitEmail(email)

        if (!user || !user.svnCredentials) {
            logger.warn("No user mapping provided for user `$email`, using sync-master credentials...")
            return svnSyncmaster
        }

        return user.svnCredentials
    }

}

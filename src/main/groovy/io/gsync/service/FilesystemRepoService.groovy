package io.gsync.service

import io.gsync.domain.Repo
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class FilesystemRepoService implements RepoService {

    @Value("\${gsync.repos}")
    String reposPath;

    @Override
    Repo findRepo(String name) {
        def repo = new Repo();

        def file = new File("$reposPath/$name")
        if (!file.exists()) {
            throw new GsyncException("The repo $name doesn't exist under $reposPath")
        }

        repo.location = file
        return repo
    }

    @Override
    List<Repo> allRepos() {
        def repos = []
        new File("$reposPath").eachDir {
            repos << new Repo(it)
        }
        return repos
    }
}

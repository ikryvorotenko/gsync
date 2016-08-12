package io.gsync.service

import io.gsync.domain.Repo

interface RepoService {
    Repo findRepo(String name)

    List<Repo> allRepos()
}
package io.gsync.repository

import io.gsync.domain.Repo
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RestResource

@RestResource
interface RepoRepository extends PagingAndSortingRepository<Repo, Long> {
    Repo findByName(String name)
}

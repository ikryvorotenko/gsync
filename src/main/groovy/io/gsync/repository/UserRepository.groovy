package io.gsync.repository

import io.gsync.domain.User
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RestResource

@RestResource
interface UserRepository extends PagingAndSortingRepository<User, Long> {
    User findByGitUsername(String username);
    User findByGitEmail(String username);
}

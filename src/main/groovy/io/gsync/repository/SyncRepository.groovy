package io.gsync.repository

import io.gsync.domain.Sync
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RestResource

@RestResource
interface SyncRepository extends PagingAndSortingRepository<Sync, Long> {

}
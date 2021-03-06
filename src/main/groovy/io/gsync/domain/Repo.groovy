package io.gsync.domain

import org.hibernate.validator.constraints.NotBlank

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Repo {
    @Id
    @GeneratedValue
    Long id
    @NotBlank
    String name
    @NotBlank
    String svnUrl
    @NotBlank
    String gitUrl

    Repo(Long id = null, String name = null, String svnUrl = null, String gitUrl = null) {
        this.id = id
        this.name = name
        this.svnUrl = svnUrl
        this.gitUrl = gitUrl
    }
}

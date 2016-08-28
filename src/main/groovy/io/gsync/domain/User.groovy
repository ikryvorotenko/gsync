package io.gsync.domain

import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class User {
    @Id
    @GeneratedValue
    Long id;

    String gitUsername
    String gitEmail

    Credentials svnCredentials
}

@Embeddable
class Credentials {
    String username
    String password
}
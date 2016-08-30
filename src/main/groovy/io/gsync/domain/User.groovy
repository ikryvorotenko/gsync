package io.gsync.domain

import com.fasterxml.jackson.annotation.JsonProperty
import io.gsync.service.CryptoConverter

import javax.persistence.*

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

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Convert(converter = CryptoConverter.class)
    String password

}
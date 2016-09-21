package io.gsync.domain

import com.fasterxml.jackson.annotation.JsonProperty
import io.gsync.service.CryptoConverter
import org.hibernate.validator.constraints.NotBlank

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class User {
    @Id
    @GeneratedValue
    Long id;

    @NotBlank
    String gitUsername

    @NotBlank
    String gitEmail

    @NotNull
    Credentials svnCredentials
}

@Embeddable
class Credentials {
    @NotBlank
    String username

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Convert(converter = CryptoConverter.class)
    @NotBlank
    String password

}
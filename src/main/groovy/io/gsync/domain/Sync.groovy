package io.gsync.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class Sync {

    @Id
    @GeneratedValue
    Long id

    @ManyToOne
    Repo repo

    LocalDateTime synctime;

    String text;
}

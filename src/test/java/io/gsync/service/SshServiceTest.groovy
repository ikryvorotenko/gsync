package io.gsync.service

import spock.lang.Specification

class SshServiceTest extends Specification {

    SshService service = new SshService(new BashService());

    def "it should return public key"() {
        when:
        def key = service.publicKey
        then:
        key != null
    }

}

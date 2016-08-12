package io.gsync.service

class GsyncException extends RuntimeException {
    GsyncException() {
        super()
    }

    GsyncException(String message) {
        super(message)
    }

    GsyncException(String message, Throwable cause) {
        super(message, cause)
    }

    GsyncException(Throwable cause) {
        super(cause)
    }
}

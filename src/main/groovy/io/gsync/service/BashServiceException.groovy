package io.gsync.service

class BashServiceException extends RuntimeException {
    BashServiceException(String message) {
        super(message)
    }

    BashServiceException(String message, Throwable cause) {
        super(message, cause)
    }

    BashServiceException(Throwable cause) {
        super(cause)
    }
}

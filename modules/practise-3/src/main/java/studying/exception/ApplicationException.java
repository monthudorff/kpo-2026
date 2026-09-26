package studying.exception;

import lombok.Getter;

/** Runtime exception with an application-level error code and root cause. */
@Getter
public class ApplicationException extends RuntimeException {
    private final ApplicationErrorCode code;

    /**
     * Creates an application error.
     *
     * @param code error code
     * @param message description
     * @param cause original exception, or {@code null} when there is no root cause
     */
    public ApplicationException(ApplicationErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * Creates an application error.
     *
     * @param code error code
     * @param message description
     */
    public ApplicationException(ApplicationErrorCode code, String message) {
        super(message);
        this.code = code;
    }
}

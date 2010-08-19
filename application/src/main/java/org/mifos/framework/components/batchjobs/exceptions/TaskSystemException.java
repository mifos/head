package org.mifos.framework.components.batchjobs.exceptions;

public class TaskSystemException extends Exception {

    public TaskSystemException() {
        super();
    }

    public TaskSystemException(String message) {
        super(message);
    }

    public TaskSystemException(Throwable cause) {
        super(cause);
    }

    public TaskSystemException(String message, Throwable cause) {
        super(message, cause);
    }

}

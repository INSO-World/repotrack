package at.ac.tuwien.student.e11800752.repositorytracker.exception;

public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
    public ServiceException(String message, Throwable cause) {}
}

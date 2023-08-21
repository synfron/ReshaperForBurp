package synfron.reshaper.burp.core.exceptions;

public class WrappedException extends RuntimeException {
    public WrappedException(Exception e) {
        super(e);
    }
}

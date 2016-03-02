package example.ws.cli;


public class CalcClientException extends Exception {

    public CalcClientException() {
    }

    public CalcClientException(String message) {
        super(message);
    }

    public CalcClientException(Throwable cause) {
        super(cause);
    }

    public CalcClientException(String message, Throwable cause) {
        super(message, cause);
    }

}

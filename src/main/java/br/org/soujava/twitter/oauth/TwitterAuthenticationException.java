package br.org.soujava.twitter.oauth;

public class TwitterAuthenticationException extends Exception {


    public TwitterAuthenticationException(final String message) {
        super(message);
    }

    public TwitterAuthenticationException(final String message, final Throwable t) {
        super(message, t);
    }

}

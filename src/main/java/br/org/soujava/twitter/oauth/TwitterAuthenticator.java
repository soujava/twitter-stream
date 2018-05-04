package br.org.soujava.twitter.oauth;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.ConcurrentMap;

import com.google.api.client.auth.oauth.*;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import br.org.soujava.twitter.utils.Constants;
import br.org.soujava.twitter.utils.PropsUtils;
import org.mapdb.DB;
import org.mapdb.DBMaker;


public class TwitterAuthenticator {

    private final PrintStream out;

    private final String consumerKey;
    private final String consumerSecret;

    private HttpRequestFactory factory;

    private static final HttpTransport TRANSPORT = new NetHttpTransport();
    private static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
    private static final String ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
    private static final String REQUEST_TOKEN_URL = "https://api.twitter.com/oauth/request_token";

    public TwitterAuthenticator(final PrintStream out, final String consumerKey, final String consumerSecret) {
        this.out = out;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public synchronized HttpRequestFactory getAuthorizedHttpRequestFactory() throws TwitterAuthenticationException {
        if (factory != null) {
            return factory;
        }

        factory = createRequestFactory();
        return factory;
    }

    private HttpRequestFactory createRequestFactory() throws TwitterAuthenticationException {
        DB db = DBMaker.fileDB("config.db").make();

        OAuthHmacSigner signer = new OAuthHmacSigner();
        signer.clientSharedSecret = consumerSecret;

        OAuthCredentialsResponse requestTokenResponse = getTemporaryToken(signer);
        signer.tokenSharedSecret = requestTokenResponse.tokenSecret;

        OAuthAuthorizeTemporaryTokenUrl authorizeUrl = new OAuthAuthorizeTemporaryTokenUrl(AUTHORIZE_URL);
        authorizeUrl.temporaryToken = requestTokenResponse.token;

        ConcurrentMap configBox = db.hashMap("CONFIG_BOX").createOrOpen();

        Object ACCESS_TOKEN_RESPONSE_OBJ = configBox.get("ACCESS_TOKEN_RESPONSE");

        Object TOKEN_SHARED_SECRET_OBJ = configBox.get("TOKEN_SHARED_SECRET");
        Object CLIENT_SHARED_SECRET_OBJ = configBox.get("CLIENT_SHARED_SECRET");


        OAuthCredentialsResponse accessTokenResponse;

        // lets try to recycle tokens :D
        String ACCESS_TOKEN_RESPONSE = null;
        if (ACCESS_TOKEN_RESPONSE_OBJ instanceof String) {
            ACCESS_TOKEN_RESPONSE = (String) ACCESS_TOKEN_RESPONSE_OBJ;
        }

        String TOKEN_SHARED_SECRET = null;
        if (TOKEN_SHARED_SECRET_OBJ instanceof String) {
            TOKEN_SHARED_SECRET = (String) TOKEN_SHARED_SECRET_OBJ;
        }

        String CLIENT_SHARED_SECRET = null;
        if (CLIENT_SHARED_SECRET_OBJ instanceof String) {
            CLIENT_SHARED_SECRET = (String) CLIENT_SHARED_SECRET_OBJ;
        }

        try {
            //it will trow exception if token is invalid or not present on the db
            if (CLIENT_SHARED_SECRET == null || TOKEN_SHARED_SECRET == null) {
                throw new TwitterAuthenticationException("PIN not saved on database yet!");
            } else {
                out.println("Will try to recycle old token ...");


                OAuthParameters parameters = new OAuthParameters();
                parameters.consumerKey = consumerKey;
                parameters.token = ACCESS_TOKEN_RESPONSE;

                signer = new OAuthHmacSigner();
                signer.clientSharedSecret = CLIENT_SHARED_SECRET;
                signer.tokenSharedSecret = TOKEN_SHARED_SECRET;
                parameters.signer = signer;

                HttpRequestFactory httpRequestFactory = TRANSPORT.createRequestFactory(parameters);

                //test that it works
                String twitterUrl = new PropsUtils().getPropertyValueString(Constants.TWITTER_URL, Constants.TWITTER_URL_DEFAULT);

                GenericUrl url = new GenericUrl(twitterUrl + "test");
                HttpRequest request = httpRequestFactory.buildGetRequest(url);
                HttpResponse httpResponse = request.execute();
                if (httpResponse.getStatusCode() != HttpStatusCodes.STATUS_CODE_OK) {
                    throw new TwitterAuthenticationException("PIN no longer valid!");
                }

                return httpRequestFactory;
            }

        } catch (Exception e) {
            out.println(e.getMessage());
            //got error, lets try getting a new TOKEN then :|
            String PIN = retrievePin(authorizeUrl);
            accessTokenResponse = retrieveAccessTokens(PIN, signer, authorizeUrl.temporaryToken);
            signer.tokenSharedSecret = accessTokenResponse.tokenSecret;

            //all good, lets save our token (PIN) and other required fields

            configBox.put("ACCESS_TOKEN_RESPONSE", accessTokenResponse.token);
            configBox.put("CLIENT_SHARED_SECRET", signer.clientSharedSecret);
            configBox.put("TOKEN_SHARED_SECRET", signer.tokenSharedSecret);

        }finally {
            db.close();
        }


        OAuthParameters parameters = new OAuthParameters();
        parameters.consumerKey = consumerKey;
        parameters.token = accessTokenResponse.token;
        parameters.signer = signer;

        return TRANSPORT.createRequestFactory(parameters);
    }

    private OAuthCredentialsResponse getTemporaryToken(final OAuthHmacSigner signer) throws TwitterAuthenticationException {
        OAuthGetTemporaryToken requestToken = new OAuthGetTemporaryToken(REQUEST_TOKEN_URL);
        requestToken.consumerKey = consumerKey;
        requestToken.transport = TRANSPORT;
        requestToken.signer = signer;

        OAuthCredentialsResponse requestTokenResponse;
        try {
            requestTokenResponse = requestToken.execute();
        } catch (IOException e) {
            throw new TwitterAuthenticationException("Unable to get temporary token: " + e.getMessage(), e);
        }

        out.println("Aquired temporary token...\n");

        return requestTokenResponse;
    }

    private String retrievePin(final OAuthAuthorizeTemporaryTokenUrl authorizeUrl) throws TwitterAuthenticationException {
        String providedPin;
        Scanner scanner = new Scanner(System.in);
        try {
            out.println("Go to the following link in your browser:\n" + authorizeUrl.build());
            out.println("\nPlease enter the retrieved PIN:");
            providedPin = scanner.nextLine();
        } finally {
            scanner.close();
        }

        if (providedPin == null) {
            throw new TwitterAuthenticationException("Unable to read entered PIN");
        }

        return providedPin;
    }

    private OAuthCredentialsResponse retrieveAccessTokens(final String providedPin, final OAuthHmacSigner signer, final String token) throws TwitterAuthenticationException {
        OAuthGetAccessToken accessToken = new OAuthGetAccessToken(ACCESS_TOKEN_URL);
        accessToken.verifier = providedPin;
        accessToken.consumerKey = consumerSecret;
        accessToken.signer = signer;
        accessToken.transport = TRANSPORT;
        accessToken.temporaryToken = token;

        OAuthCredentialsResponse accessTokenResponse;
        try {
            accessTokenResponse = accessToken.execute();
        } catch (IOException e) {
            throw new TwitterAuthenticationException("Unable to authorize access: " + e.getMessage(), e);
        }
        out.println("\nAuthorization was successful");

        return accessTokenResponse;
    }
}

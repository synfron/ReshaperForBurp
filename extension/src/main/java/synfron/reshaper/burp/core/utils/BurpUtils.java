package synfron.reshaper.burp.core.utils;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;

public abstract class BurpUtils {
    public static BurpUtils current = new BurpUtilsPassiveImpl();

    public abstract ByteArray toByteArray(String text);

    public abstract ByteArray toByteArray(byte[] bytes);

    public abstract HttpRequest toHttpRequest(String text);

    public abstract HttpResponse toHttpResponse(String text);

    public static class BurpUtilsPassiveImpl extends BurpUtils {

        @Override
        public ByteArray toByteArray(String text) {
            return ByteArray.byteArray(text);
        }

        @Override
        public ByteArray toByteArray(byte[] bytes) {
            return ByteArray.byteArray(bytes);
        }

        @Override
        public HttpRequest toHttpRequest(String text) {
            return HttpRequest.httpRequest(text);
        }

        @Override
        public HttpResponse toHttpResponse(String text) {
            return HttpResponse.httpResponse(text);
        }
    }
}

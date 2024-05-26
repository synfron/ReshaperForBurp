package synfron.reshaper.burp.runner;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.burpsuite.BurpSuite;
import burp.api.montoya.collaborator.Collaborator;
import burp.api.montoya.comparer.Comparer;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.Marker;
import burp.api.montoya.core.Range;
import burp.api.montoya.core.Registration;
import burp.api.montoya.decoder.Decoder;
import burp.api.montoya.extension.Extension;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import burp.api.montoya.http.Http;
import burp.api.montoya.http.HttpMode;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.handler.HttpHandler;
import burp.api.montoya.http.message.*;
import burp.api.montoya.http.message.params.HttpParameter;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.requests.HttpTransformation;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.http.message.responses.analysis.*;
import burp.api.montoya.http.sessions.CookieJar;
import burp.api.montoya.http.sessions.SessionHandlingAction;
import burp.api.montoya.intruder.HttpRequestTemplate;
import burp.api.montoya.intruder.Intruder;
import burp.api.montoya.intruder.PayloadGeneratorProvider;
import burp.api.montoya.intruder.PayloadProcessor;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.persistence.PersistedObject;
import burp.api.montoya.persistence.Persistence;
import burp.api.montoya.persistence.Preferences;
import burp.api.montoya.proxy.*;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyResponseHandler;
import burp.api.montoya.proxy.websocket.ProxyWebSocketCreationHandler;
import burp.api.montoya.repeater.Repeater;
import burp.api.montoya.scanner.Scanner;
import burp.api.montoya.scope.Scope;
import burp.api.montoya.scope.ScopeChangeHandler;
import burp.api.montoya.sitemap.SiteMap;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.Theme;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.editor.*;
import burp.api.montoya.ui.editor.extension.HttpRequestEditorProvider;
import burp.api.montoya.ui.editor.extension.HttpResponseEditorProvider;
import burp.api.montoya.ui.menu.MenuBar;
import burp.api.montoya.ui.swing.SwingUtils;
import burp.api.montoya.utilities.*;
import burp.api.montoya.websocket.WebSocketCreatedHandler;
import burp.api.montoya.websocket.WebSockets;
import burp.api.montoya.websocket.extension.ExtensionWebSocketCreation;
import synfron.reshaper.burp.core.utils.BurpUtils;
import synfron.reshaper.burp.ui.components.IFormComponent;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

class Api implements MontoyaApi {

    @Override
    public BurpSuite burpSuite() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collaborator collaborator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comparer comparer() {
        return new ComparerImpl();
    }

    @Override
    public Decoder decoder() {
        return new DecoderImpl();
    }

    @Override
    public Extension extension() {
        return new ExtensionImpl();
    }

    @Override
    public Http http() {
        return new HttpImpl();
    }

    @Override
    public Intruder intruder() {
        return new IntruderImpl();
    }

    @Override
    public Logging logging() {
        return new LoggingImpl();
    }

    @Override
    public Persistence persistence() {
        return new PersistenceImpl();
    }

    @Override
    public Proxy proxy() {
        return new ProxyImpl();
    }

    @Override
    public Repeater repeater() {
        return new RepeatImpl();
    }

    @Override
    public Scanner scanner() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Scope scope() {
        return new ScopeImpl();
    }

    @Override
    public SiteMap siteMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserInterface userInterface() {
        return new UserInterfaceImpl();
    }

    @Override
    public Utilities utilities() {
        return new UtilitiesImpl();
    }

    @Override
    public WebSockets websockets() {
        return new WebSocketsImpl();
    }

    private static class UtilitiesImpl implements Utilities {

        @Override
        public Base64Utils base64Utils() {
            return null;
        }

        @Override
        public ByteUtils byteUtils() {
            return null;
        }

        @Override
        public CompressionUtils compressionUtils() {
            return null;
        }

        @Override
        public CryptoUtils cryptoUtils() {
            return null;
        }

        @Override
        public HtmlUtils htmlUtils() {
            return null;
        }

        @Override
        public NumberUtils numberUtils() {
            return null;
        }

        @Override
        public RandomUtils randomUtils() {
            return null;
        }

        @Override
        public StringUtils stringUtils() {
            return null;
        }

        @Override
        public URLUtils urlUtils() {
            return null;
        }
    }

    private static class WebSocketsImpl implements WebSockets {

        @Override
        public Registration registerWebSocketCreatedHandler(WebSocketCreatedHandler handler) {
            return null;
        }

        @Override
        public ExtensionWebSocketCreation createWebSocket(HttpService service, String path) {
            return null;
        }

        @Override
        public ExtensionWebSocketCreation createWebSocket(burp.api.montoya.http.message.requests.HttpRequest upgradeRequest) {
            return null;
        }
    }

    private static class ComparerImpl implements Comparer {

        @Override
        public void sendToComparer(ByteArray... data) {

        }
    }

    private static class DecoderImpl implements Decoder {

        @Override
        public void sendToDecoder(ByteArray data) {

        }
    }

    private static class ExtensionImpl implements Extension {

        @Override
        public void setName(String extensionName) {

        }

        @Override
        public String filename() {
            return null;
        }

        @Override
        public boolean isBapp() {
            return false;
        }

        @Override
        public void unload() {

        }

        @Override
        public Registration registerUnloadingHandler(ExtensionUnloadingHandler handler) {
            return null;
        }
    }

    private static class HttpImpl implements Http {
        @Override
        public Registration registerHttpHandler(HttpHandler handler) {
            return null;
        }

        @Override
        public Registration registerSessionHandlingAction(SessionHandlingAction sessionHandlingAction) {
            return null;
        }

        @Override
        public HttpRequestResponse sendRequest(HttpRequest request) {
            return null;
        }

        @Override
        public HttpRequestResponse sendRequest(HttpRequest request, HttpMode httpMode) {
            return null;
        }

        @Override
        public HttpRequestResponse sendRequest(HttpRequest request, HttpMode httpMode, String connectionId) {
            return null;
        }

        @Override
        public ResponseKeywordsAnalyzer createResponseKeywordsAnalyzer(List<String> keywords) {
            return null;
        }

        @Override
        public ResponseVariationsAnalyzer createResponseVariationsAnalyzer() {
            return null;
        }

        @Override
        public CookieJar cookieJar() {
            return null;
        }
    }

    private static class CookieJarImpl implements CookieJar {

        @Override
        public void setCookie(String name, String value, String path, String domain, ZonedDateTime expiration) {

        }

        @Override
        public List<Cookie> cookies() {
            return List.of();
        }
    }

    private static class IntruderImpl implements Intruder {

        @Override
        public Registration registerPayloadProcessor(PayloadProcessor payloadProcessor) {
            return null;
        }

        @Override
        public Registration registerPayloadGeneratorProvider(PayloadGeneratorProvider payloadGeneratorProvider) {
            return null;
        }

        @Override
        public void sendToIntruder(HttpService service, HttpRequestTemplate requestTemplate) {

        }

        @Override
        public void sendToIntruder(HttpRequest request) {

        }
    }

    private static class LoggingImpl implements Logging {

        @Override
        public PrintStream output() {
            return null;
        }

        @Override
        public PrintStream error() {
            return null;
        }

        @Override
        public void logToOutput(String message) {

        }

        @Override
        public void logToError(String message) {

        }

        @Override
        public void raiseDebugEvent(String message) {

        }

        @Override
        public void raiseInfoEvent(String message) {

        }

        @Override
        public void raiseErrorEvent(String message) {

        }

        @Override
        public void raiseCriticalEvent(String message) {

        }
    }

    private static class PersistenceImpl implements Persistence {

        @Override
        public PersistedObject extensionData() {
            return null;
        }

        @Override
        public Preferences preferences() {
            return new PreferencesImpl();
        }
    }

    private static class PreferencesImpl implements Preferences {

        @Override
        public String getString(String key) {
            return null;
        }

        @Override
        public void setString(String key, String value) {

        }

        @Override
        public void deleteString(String key) {

        }

        @Override
        public Set<String> stringKeys() {
            return null;
        }

        @Override
        public Boolean getBoolean(String key) {
            return null;
        }

        @Override
        public void setBoolean(String key, boolean value) {

        }

        @Override
        public void deleteBoolean(String key) {

        }

        @Override
        public Set<String> booleanKeys() {
            return null;
        }

        @Override
        public Byte getByte(String key) {
            return null;
        }

        @Override
        public void setByte(String key, byte value) {

        }

        @Override
        public void deleteByte(String key) {

        }

        @Override
        public Set<String> byteKeys() {
            return null;
        }

        @Override
        public Short getShort(String key) {
            return null;
        }

        @Override
        public void setShort(String key, short value) {

        }

        @Override
        public void deleteShort(String key) {

        }

        @Override
        public Set<String> shortKeys() {
            return null;
        }

        @Override
        public Integer getInteger(String key) {
            return null;
        }

        @Override
        public void setInteger(String key, int value) {

        }

        @Override
        public void deleteInteger(String key) {

        }

        @Override
        public Set<String> integerKeys() {
            return null;
        }

        @Override
        public Long getLong(String key) {
            return null;
        }

        @Override
        public void setLong(String key, long value) {

        }

        @Override
        public void deleteLong(String key) {

        }

        @Override
        public Set<String> longKeys() {
            return null;
        }
    }

    private static class ProxyImpl implements Proxy {

        @Override
        public void enableIntercept() {

        }

        @Override
        public void disableIntercept() {

        }

        @Override
        public List<ProxyHttpRequestResponse> history() {
            return null;
        }

        @Override
        public List<ProxyHttpRequestResponse> history(ProxyHistoryFilter filter) {
            return null;
        }

        @Override
        public List<ProxyWebSocketMessage> webSocketHistory() {
            return null;
        }

        @Override
        public List<ProxyWebSocketMessage> webSocketHistory(ProxyWebSocketHistoryFilter filter) {
            return null;
        }

        @Override
        public Registration registerRequestHandler(ProxyRequestHandler handler) {
            return null;
        }

        @Override
        public Registration registerResponseHandler(ProxyResponseHandler handler) {
            return null;
        }

        @Override
        public Registration registerWebSocketCreationHandler(ProxyWebSocketCreationHandler handler) {
            return null;
        }
    }

    private static class RepeatImpl implements Repeater {

        @Override
        public void sendToRepeater(HttpRequest request) {

        }

        @Override
        public void sendToRepeater(HttpRequest request, String name) {

        }
    }

    private static class ScopeImpl implements Scope {

        @Override
        public boolean isInScope(String url) {
            return false;
        }

        @Override
        public void includeInScope(String url) {

        }

        @Override
        public void excludeFromScope(String url) {

        }

        @Override
        public Registration registerScopeChangeHandler(ScopeChangeHandler handler) {
            return null;
        }
    }

    private static class UserInterfaceImpl implements UserInterface {

        @Override
        public MenuBar menuBar() {
            return null;
        }

        @Override
        public Registration registerSuiteTab(String title, Component component) {
            return null;
        }

        @Override
        public Registration registerContextMenuItemsProvider(ContextMenuItemsProvider provider) {
            return null;
        }

        @Override
        public Registration registerHttpRequestEditorProvider(HttpRequestEditorProvider provider) {
            return null;
        }

        @Override
        public Registration registerHttpResponseEditorProvider(HttpResponseEditorProvider provider) {
            return null;
        }

        @Override
        public RawEditor createRawEditor(EditorOptions... options) {
            return new MessageEditorImpl();
        }

        @Override
        public WebSocketMessageEditor createWebSocketMessageEditor(EditorOptions... options) {
            return new MessageEditorImpl();
        }

        @Override
        public HttpRequestEditor createHttpRequestEditor(EditorOptions... options) {
            return new MessageEditorImpl();
        }

        @Override
        public HttpResponseEditor createHttpResponseEditor(EditorOptions... options) {
            return new MessageEditorImpl();
        }

        @Override
        public void applyThemeToComponent(Component component) {

        }

        @Override
        public Theme currentTheme() {
            return null;
        }

        @Override
        public SwingUtils swingUtils() {
            return null;
        }
    }

    private static class MessageEditorImpl extends EditorImpl implements WebSocketMessageEditor, HttpRequestEditor, HttpResponseEditor, RawEditor {

        @Override
        public void setEditable(boolean editable) {

        }

        @Override
        public ByteArray getContents() {
            return BurpUtils.current.toByteArray(editorBox.getText());
        }

        @Override
        public void setContents(ByteArray contents) {
            editorBox.setText(contents.toString());
        }

        @Override
        public HttpRequest getRequest() {
            return HttpRequest.httpRequest(BurpUtils.current.toByteArray(editorBox.getText()));
        }

        @Override
        public void setRequest(HttpRequest request) {
            editorBox.setText(request.toString());
        }

        @Override
        public HttpResponse getResponse() {
            return burp.api.montoya.http.message.responses.HttpResponse.httpResponse(BurpUtils.current.toByteArray(editorBox.getText()));
        }

        @Override
        public void setResponse(HttpResponse response) {
            editorBox.setText(response.toString());
        }
    }

    private static class EditorImpl implements Editor, IFormComponent {

        private final JPanel component;
        protected final JTextPane editorBox;

        public EditorImpl() {
            component = new JPanel(new BorderLayout());
            component.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

            editorBox = createTextPane();

            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setViewportView(editorBox);

            component.add(scrollPane, BorderLayout.CENTER);
        }

        @Override
        public void setSearchExpression(String expression) {

        }

        @Override
        public boolean isModified() {
            return false;
        }

        @Override
        public int caretPosition() {
            return 0;
        }

        @Override
        public Optional<Selection> selection() {
            return Optional.empty();
        }

        @Override
        public Component uiComponent() {
            return component;
        }
    }

    private static class HttpResponseImpl implements HttpResponse {

        private final ByteArrayImpl byteArray;

        public HttpResponseImpl(String text) {
            this.byteArray = new ByteArrayImpl(text);
        }

        @Override
        public String toString() {
            return byteArray.toString();
        }

        @Override
        public short statusCode() {
            return 0;
        }

        @Override
        public String reasonPhrase() {
            return null;
        }

        @Override
        public String httpVersion() {
            return null;
        }

        @Override
        public List<HttpHeader> headers() {
            return null;
        }

        @Override
        public ByteArray body() {
            return null;
        }

        @Override
        public String bodyToString() {
            return null;
        }

        @Override
        public int bodyOffset() {
            return 0;
        }

        @Override
        public List<Marker> markers() {
            return null;
        }

        @Override
        public List<Cookie> cookies() {
            return null;
        }

        @Override
        public MimeType statedMimeType() {
            return null;
        }

        @Override
        public MimeType inferredMimeType() {
            return null;
        }

        @Override
        public List<KeywordCount> keywordCounts(String... keywords) {
            return null;
        }

        @Override
        public List<Attribute> attributes(AttributeType... types) {
            return null;
        }

        @Override
        public ByteArray toByteArray() {
            return null;
        }

        @Override
        public HttpResponse copyToTempFile() {
            return null;
        }

        @Override
        public HttpResponse withStatusCode(short statusCode) {
            return null;
        }

        @Override
        public HttpResponse withReasonPhrase(String reasonPhrase) {
            return null;
        }

        @Override
        public HttpResponse withHttpVersion(String httpVersion) {
            return null;
        }

        @Override
        public HttpResponse withBody(String body) {
            return null;
        }

        @Override
        public HttpResponse withBody(ByteArray body) {
            return null;
        }

        @Override
        public HttpResponse withAddedHeader(HttpHeader header) {
            return null;
        }

        @Override
        public HttpResponse withAddedHeader(String name, String value) {
            return null;
        }

        @Override
        public HttpResponse withUpdatedHeader(HttpHeader header) {
            return null;
        }

        @Override
        public HttpResponse withUpdatedHeader(String name, String value) {
            return null;
        }

        @Override
        public HttpResponse withRemovedHeader(HttpHeader header) {
            return null;
        }

        @Override
        public HttpResponse withRemovedHeader(String name) {
            return null;
        }

        @Override
        public HttpResponse withMarkers(List<Marker> markers) {
            return null;
        }

        @Override
        public HttpResponse withMarkers(Marker... markers) {
            return null;
        }
    }

    public static class ByteArrayImpl implements ByteArray {

        private final byte[] bytes;

        public ByteArrayImpl(String text) {
            bytes = text.getBytes();
        }

        public ByteArrayImpl(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public byte getByte(int index) {
            return 0;
        }

        @Override
        public void setByte(int index, byte value) {

        }

        @Override
        public void setByte(int index, int value) {

        }

        @Override
        public void setBytes(int index, byte... data) {

        }

        @Override
        public void setBytes(int index, int... data) {

        }

        @Override
        public void setBytes(int index, ByteArray byteArray) {

        }

        @Override
        public int length() {
            return 0;
        }

        @Override
        public byte[] getBytes() {
            return bytes;
        }

        @Override
        public String toString() {
            return new String(bytes);
        }

        @Override
        public ByteArray subArray(int startIndexInclusive, int endIndexExclusive) {
            return null;
        }

        @Override
        public ByteArray subArray(Range range) {
            return null;
        }

        @Override
        public ByteArray copy() {
            return null;
        }

        @Override
        public ByteArray copyToTempFile() {
            return null;
        }

        @Override
        public int indexOf(ByteArray searchTerm) {
            return 0;
        }

        @Override
        public int indexOf(String searchTerm) {
            return 0;
        }

        @Override
        public int indexOf(ByteArray searchTerm, boolean caseSensitive) {
            return 0;
        }

        @Override
        public int indexOf(String searchTerm, boolean caseSensitive) {
            return 0;
        }

        @Override
        public int indexOf(ByteArray searchTerm, boolean caseSensitive, int startIndexInclusive, int endIndexExclusive) {
            return 0;
        }

        @Override
        public int indexOf(String searchTerm, boolean caseSensitive, int startIndexInclusive, int endIndexExclusive) {
            return 0;
        }

        @Override
        public int countMatches(ByteArray searchTerm) {
            return 0;
        }

        @Override
        public int countMatches(String searchTerm) {
            return 0;
        }

        @Override
        public int countMatches(ByteArray searchTerm, boolean caseSensitive) {
            return 0;
        }

        @Override
        public int countMatches(String searchTerm, boolean caseSensitive) {
            return 0;
        }

        @Override
        public int countMatches(ByteArray searchTerm, boolean caseSensitive, int startIndexInclusive, int endIndexExclusive) {
            return 0;
        }

        @Override
        public int countMatches(String searchTerm, boolean caseSensitive, int startIndexInclusive, int endIndexExclusive) {
            return 0;
        }

        @Override
        public ByteArray withAppended(byte... data) {
            return null;
        }

        @Override
        public ByteArray withAppended(int... data) {
            return null;
        }

        @Override
        public ByteArray withAppended(String text) {
            return null;
        }

        @Override
        public ByteArray withAppended(ByteArray byteArray) {
            return null;
        }

        @Override
        public Iterator<Byte> iterator() {
            return null;
        }
    }

    private static class HttpRequestImpl implements HttpRequest {

        private final ByteArrayImpl byteArray;

        public HttpRequestImpl(String text) {
            this.byteArray = new ByteArrayImpl(text);
        }

        @Override
        public String toString() {
            return byteArray.toString();
        }

        @Override
        public HttpService httpService() {
            return null;
        }

        @Override
        public String url() {
            return null;
        }

        @Override
        public String method() {
            return null;
        }

        @Override
        public String path() {
            return null;
        }

        @Override
        public String httpVersion() {
            return null;
        }

        @Override
        public List<HttpHeader> headers() {
            return null;
        }

        @Override
        public ContentType contentType() {
            return null;
        }

        @Override
        public List<ParsedHttpParameter> parameters() {
            return null;
        }

        @Override
        public ByteArray body() {
            return null;
        }

        @Override
        public String bodyToString() {
            return null;
        }

        @Override
        public int bodyOffset() {
            return 0;
        }

        @Override
        public List<Marker> markers() {
            return null;
        }

        @Override
        public ByteArray toByteArray() {
            return null;
        }

        @Override
        public HttpRequest copyToTempFile() {
            return null;
        }

        @Override
        public HttpRequest withService(HttpService service) {
            return null;
        }

        @Override
        public HttpRequest withPath(String path) {
            return null;
        }

        @Override
        public HttpRequest withMethod(String method) {
            return null;
        }

        @Override
        public HttpRequest withAddedParameters(List<HttpParameter> parameters) {
            return null;
        }

        @Override
        public HttpRequest withAddedParameters(HttpParameter... parameters) {
            return null;
        }

        @Override
        public HttpRequest withRemovedParameters(List<HttpParameter> parameters) {
            return null;
        }

        @Override
        public HttpRequest withRemovedParameters(HttpParameter... parameters) {
            return null;
        }

        @Override
        public HttpRequest withUpdatedParameters(List<HttpParameter> parameters) {
            return null;
        }

        @Override
        public HttpRequest withUpdatedParameters(HttpParameter... parameters) {
            return null;
        }

        @Override
        public HttpRequest withTransformationApplied(HttpTransformation transformation) {
            return null;
        }

        @Override
        public HttpRequest withBody(String body) {
            return null;
        }

        @Override
        public HttpRequest withBody(ByteArray body) {
            return null;
        }

        @Override
        public HttpRequest withAddedHeader(String name, String value) {
            return null;
        }

        @Override
        public HttpRequest withAddedHeader(HttpHeader header) {
            return null;
        }

        @Override
        public HttpRequest withUpdatedHeader(String name, String value) {
            return null;
        }

        @Override
        public HttpRequest withUpdatedHeader(HttpHeader header) {
            return null;
        }

        @Override
        public HttpRequest withRemovedHeader(String name) {
            return null;
        }

        @Override
        public HttpRequest withRemovedHeader(HttpHeader header) {
            return null;
        }

        @Override
        public HttpRequest withMarkers(List<Marker> markers) {
            return null;
        }

        @Override
        public HttpRequest withMarkers(Marker... markers) {
            return null;
        }

        @Override
        public HttpRequest withDefaultHeaders() {
            return null;
        }
    }

    public static class BurpUtilsImpl extends BurpUtils {

        @Override
        public ByteArray toByteArray(String text) {
            return new ByteArrayImpl(text);
        }

        @Override
        public ByteArray toByteArray(byte[] bytes) {
            return new ByteArrayImpl(bytes);
        }

        @Override
        public HttpRequest toHttpRequest(String text) {
            return new HttpRequestImpl(text);
        }

        @Override
        public HttpResponse toHttpResponse(String text) {
            return new HttpResponseImpl(text);
        }
    }
}

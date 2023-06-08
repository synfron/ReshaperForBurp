# Message Values

Message values are values that are extracted from components of the HTTP message, WebSocket message, or connection details associated with an event that is being processed by Rules.

Note: HTTP message values that are accessible by WebSocket Rule operations refer to components of the originating ws:// or wss:// request that triggered the establishment of the WebSocket connection.

* auto-gen TOC:
  {:toc}

## Source Address 

Key: SourceAddress

Rule Availability: HTTP

Example: `127.0.0.1`

## Destination Address 

Host name without port.

Key: DestinationAddress

Rule Availability: HTTP, WebSocket

Example: `www.example.com`

## Destination Port 

Key: DestinationPort

Rule Availability: HTTP, WebSocket

Example: `80`

## Protocol 

`http` or `https`

Key: HttpProtocol

Rule Availability: HTTP, WebSocket

## URL 

Key: URL

Rule Availability: HTTP, WebSocket

Example: `http://www.example.com/index.html?query=test`

## WebSocket Message

Key: WebSocketMessage

Rule Availability: WebSocket

## Request Message 

Key: HttpRequestMessage

Rule Availability: HTTP, WebSocket

Example:
```
GET / HTTP/1.1
Host: www.example.com
User-Agent: Mozilla/5.0 Firefox/78.0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
Accept-Language: en-US,en;q=0.5
Accept-Encoding: gzip, deflate
Connection: close
Upgrade-Insecure-Requests: 1
Pragma: no-cache
Cache-Control: no-cache
```

## Request Status Line 

Key: HttpRequestStatusLine

Rule Availability: HTTP, WebSocket

Example: `GET /path/to/page/index.html?claim=reset&type=plain HTTP/1.1`

## Request Method 

Key: HttpRequestMethod

Rule Availability: HTTP, WebSocket

Example: `GET`

## Request URI 

Key: HttpRequestUri

Rule Availability: HTTP, WebSocket

Example: `/path/to/page/index.html?claim=reset&type=plain`

## Request URI Path 

Key: HttpRequestUriPath

Rule Availability: HTTP, WebSocket

Example: `/path/to/page/index.html` from `/path/to/page/index.html?claim=reset&type=plain`

## Request URI Query Parameters 

Key: HttpRequestUriQueryParameters

Rule Availability: HTTP, WebSocket

Example: `claim=reset&type=plain` from `/path/to/page/index.html?claim=reset&type=plain`

## Request URI Query Parameter 

Key: HttpRequestUriQueryParameter

Rule Availability: HTTP, WebSocket

Example: Returns `plain` using identifier `type` given the request URI `/path/to/page/index.html?claim=reset&type=plain`

## Request Headers 

Key: HttpRequestHeaders

Rule Availability: HTTP, WebSocket

Example:
```
Host: www.example.com
User-Agent: Mozilla/5.0 Firefox/78.0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
Accept-Language: en-US,en;q=0.5
Accept-Encoding: gzip, deflate
Cookie: Preferences=local; AID=2Zy8
Connection: close
Upgrade-Insecure-Requests: 1
Pragma: no-cache
Cache-Control: no-cache
```

## Request Header 

Key: HttpRequestHeader

Rule Availability: HTTP, WebSocket

Example: Based request header `Accept-Encoding: gzip, deflate`, this returns `gzip, deflate` using identifier `Accept-Encoding`

Example: `gzip, deflate` at identifier `Accept-Encoding`

## Request Cookie 

Key: HttpRequestCookie

Rule Availability: HTTP, WebSocket

Example: For cookie header `Cookie: AID=2Zy8`, this returns `2Zy8` using identifier `AID`

## Request Body 

Key: HttpRequestBody

Rule Availability: HTTP, WebSocket

## Response Message 

Key: HttpResponseMessage

Rule Availability: HTTP

Example:
```
HTTP/1.1 404 Not Found
Accept-Ranges: bytes
Age: 354581
Cache-Control: max-age=604800
Content-Type: text/html; charset=UTF-8
Date: Mon, 07 Dec 2020 07:30:13 GMT
Expires: Mon, 14 Dec 2020 07:30:13 GMT
Last-Modified: Thu, 03 Dec 2020 05:00:32 GMT
Server: ECS (ord/5739)
Vary: Accept-Encoding
X-Cache: 404-HIT
Content-Length: 1256
Connection: close
```

## Response Status Line 

Key: HttpResponseStatusLine

Rule Availability: HTTP

Example: `HTTP/1.1 404 Not Found`

## Response Status Code 

Key: HttpResponseStatusCode

Rule Availability: HTTP

Example: `404`

## Response Status Message 

Key: HttpResponseStatusMessage

Rule Availability: HTTP

Example: `Not Found`

## Response Headers 

Key: HttpResponseHeaders

Rule Availability: HTTP

Example:
```
HTTP/1.1 404 Not Found
Accept-Ranges: bytes
Age: 354581
Cache-Control: max-age=604800
Content-Type: text/html; charset=UTF-8
Date: Mon, 07 Dec 2020 07:30:13 GMT
Set-Cookie: AID=2Zy8
Expires: Mon, 14 Dec 2020 07:30:13 GMT
Last-Modified: Thu, 03 Dec 2020 05:00:32 GMT
Server: ECS (ord/5739)
Vary: Accept-Encoding
X-Cache: 404-HIT
Content-Length: 1256
Connection: close
```

## Response Header 

Key: HttpResponseHeader

Rule Availability: HTTP

Example: Based response header `Cache-Control: max-age=604800`, this returns `max-age=604800` using identifier `Cache-Control`

## Response Cookie 

Key: HttpResponseCookie

Rule Availability: HTTP

Example: For cookie header `Set-Cookie: AID=2Zy8`, this returns `2Zy8` using identifier `AID`

## Response Body 

Key: HttpResponseBody

Rule Availability: HTTP
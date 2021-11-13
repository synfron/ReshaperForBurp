# Message Values

* auto-gen TOC:
  {:toc}

## Source Address 

Key: SourceAddress

Example: `127.0.0.1`

## Destination Address 

Host name without port.

Key: DestinationAddress

Example: `www.example.com`

## Destination Port 

Key: DestinationPort

Example: `80`

## Protocol 

`http` or `https`

Key: HttpProtocol

## URL 

Key: URL

Example: `http://www.example.com/index.html?query=test`

## Request Message 

Key: HttpRequestMessage

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

Example: `GET /path/to/page/index.html?claim=reset&type=plain HTTP/1.1`

## Request Method 

Key: HttpRequestMethod

Example: `GET`

## Request URI 

Key: HttpRequestUri

Example: `/path/to/page/index.html?claim=reset&type=plain`

## Request URI Path 

Key: HttpRequestUriPath

Example: `/path/to/page/index.html` from `/path/to/page/index.html?claim=reset&type=plain`

## Request URI Query Parameters 

Key: HttpRequestUriQueryParameters

Example: `claim=reset&type=plain` from `/path/to/page/index.html?claim=reset&type=plain`

## Request URI Query Parameter 

Key: HttpRequestUriQueryParameter

Example: Returns `plain` using identifier `type` given the request URI `/path/to/page/index.html?claim=reset&type=plain`

## Request Headers 

Key: HttpRequestHeaders

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

Example: Based request header `Accept-Encoding: gzip, deflate`, this returns `gzip, deflate` using identifier `Accept-Encoding`

Example: `gzip, deflate` at identifier `Accept-Encoding`

## Request Cookie 

Key: HttpRequestCookie

Example: For cookie header `Cookie: AID=2Zy8`, this returns `2Zy8` using identifier `AID`

## Request Body 

Key: HttpRequestBody

## Response Message 

Key: HttpResponseMessage

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

Example: `HTTP/1.1 404 Not Found`

## Response Status Code 

Key: HttpResponseStatusCode

Example: `404`

## Response Status Message 

Key: HttpResponseStatusMessage

Example: `Not Found`

## Response Headers 

Key: HttpResponseHeaders - Example:
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

Example: Based response header `Cache-Control: max-age=604800`, this returns `max-age=604800` using identifier `Cache-Control`

## Response Cookie 

Key: HttpResponseCookie 

Example: For cookie header `Set-Cookie: AID=2Zy8`, this returns `2Zy8` using identifier `AID`

## Response Body 

Key: HttpResponseBody
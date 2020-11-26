# Rules

Rules allow you to set actions to perform (called Thens) if HTTP messages/connections (event) received by Burp Suite meet certain criteria (called Whens). Rules are processed in order. If the Rule is set to auto-run, the Rule will be run automatically when an HTTP event is received, otherwise it must be specifically triggered. Rules must be enabled to run at all.

## Message Values

Source Address - Example: `127.0.0.1`

Destination Address - Example: `www.example.com`

Destination Port - Example: `80`

Protocol - http or https

Request Message - Example: 
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

Request Status Line - Example: `GET /path/to/page/index.html?claim=reset&type=plain HTTP/1.1`

Request Method - Example: `GET`

Request URI - Example: `/path/to/page/index.html?claim=reset&type=plain`

Request URI Path - Example: `/path/to/page/index.html` from `/path/to/page/index.html?claim=reset&type=plain`

Request URI Query Parameters - Example: `claim=reset&type=plain` from `/path/to/page/index.html?claim=reset&type=plain`

Request URI Query Parameter - Example: `plain` at identifier `type` from `/path/to/page/index.html?claim=reset&type=plain`

Request Headers -  Example: 
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

Request Header - Example: `gzip, deflate` at identifier `Accept-Encoding`

Request Cookie - Example: `2Zy8` at identifier `AID`

Request Body

Response Message - Example:
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

Response Status Line - Example: `HTTP/1.1 404 Not Found`

Response Status Code - Example: `404`

Response Status Message - Example: `Not Found`

Response Headers - Example:
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

Response Header - Example: `max-age=604800` at identifier `Cache-Control`

Response Cookie - Example: `2Zy8` at identifier `AID`

Response Body

## Whens

Check if an HTTP message meets certain criteria. Multiple Whens are checked in order and treated as AND conditions logically by default. If the relevant value does not match the constraints of the When (opposite if `Negate Result` is selected), unless the following When has specified to `Use OR Condition`, no further Whens are process for the current Rule and all Thens are skipped.

### Event Direction

If the HTTP message is a Request or Response

#### Fields

Event Direction - Request or Response

### Has Entity

If the HTTP event contains a certain entity

#### Fields

Message Value - The HTTP event entity to check

Identifier - The property of the HTTP entity to check. Only available for certain Message Values (e.g. request header). Supports variable tags.

### Matches Text

If a value (text, variable, or HTTP event entity) matches a value

#### Fields

Use Message Value - Match on a Message Value (HTTP event entity). Otherwise, use the specified text.

Message Value - The HTTP event entity to check. Only available if `Use Message Value` is selected.

Identifier - The property of the HTTP entity to check. Only available for certain Message Values (e.g. request header). Supports variable tags.

Source Text - The text to use as the value to check. Only available if `Use Message Value` is not selected. Supports variable tags.

Message Value Type - Declare that the value is Text, JSON (node), or HTML (element). If JSON or HTML, use JSON path or a CSS selector to get the inner value.

Message Value Path - Specify a JSON path for JSON or a CSS selector for HTML to get a value from within the original value and then use this value instead. Only available if `Message Value Type` is JSON or HTML. Supports variable tags.

Match Text - The text to match the value against. Supports variable tags.

Match Type - Match the text using Equals, Contains, Begins With, Ends With, or Regex.

### Proxy Name

If received by a certain Burp proxy listener

Proxy Name - The Burp proxy listener interface (e.g. 127.0.0.1:8080)

## Thens

### Break

Stop rules or then action processing

#### Fields

Break Type - If Skip Next Thens, skip running any further Thens of the Rule. If Skip Next Rules, skip running any further Thens and Rules for this event.

### Delay

Delay further processing/sending of the HTTP event

#### Fields

Delay (milliseconds) - The amount of time to delay further processing. Supports variable tags.

### Log

Log message to the Burp extension console

#### Fields

Text - The text to log. Supports variable tags.

### Highlight

Highlight the request/response line in the HTTP history

#### Fields

Color - The color used to highlight the request/response line.

### Run Rules

Run a specific rule or all auto-run rules.

#### Fields

Run Single - Run a specific Rule is selected. Otherwise, run all auto-run Rules.

Run Name - The name of the Rule to run. Only available if `Run Single` is selected.

### Run Script

Execute a JavaScript script

The engine supports up to partial ES6/ES2015.

#### Extended Library

Scripts have access to certain Reshaper specific functions.

Reshaper.

&nbsp;&nbsp;&nbsp;&nbsp;variables.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;getGlobalVariable(name)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;getEventVariable(name)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;setGlobalVariable(name, value)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;setEventVariable(name, value)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;deleteGlobalVariable(name)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;deleteEventVariable(name)

#### Fields

Script - The text of the JavaScript script to run.

### Set Event Direction

Change whether to send a request or to send a response at the end of processing

If the event direction is switched from request to response, no request is sent. Instead, whatever is set in the HTTP response message is sent. Switching from response to request is not functional.

#### Fields

Set Event Direction - Request or Response. 

### Set Value

Set the value of an HTTP event using another value (text, variable, or HTTP event entity)

#### Fields

Use Message Value - Use Message Value (HTTP event entity) as the source value. Otherwise, use the specified text.

Source Message Value - The HTTP event entity to get the source value from. Only available if `Use Message Value` is selected.

Source Identifier - The property of the HTTP entity to get the source value from. Only available for certain Message Values (e.g. request header). Supports variable tags.

Text - The text to use as the source value. Only available if `Use Message Value` is not selected. Supports variable tags.

Source Message Value Type - Declare that the value is Text, JSON (node), or HTML (element). If JSON or HTML, use JSON path or a CSS selector to get the inner value.

Source Message Value Path - Specify a JSON path for JSON or a CSS selector for HTML to get a value from within the source value and then use this value instead. Only available if `Source Message Value Type` is JSON or HTML. Supports variable tags.

Use Regex Replace - Use regex on the source value.

Regex Pattern - The Regex pattern to run on the source value. If there is a successful match, a Regex replace is performed on the value using `Regex Replacement Text`. Only available if `Use Regex Replace` is selected. Supports variable tags.

Regex Pattern - The replacement value to use in the Regex replace. Only available if `Use Regex Replace` is selected. Supports variable tags.

Destination Message Value - The HTTP event entity to set the value of.

Destination Message Value Type - Declare that the value to set is Text, JSON (node), or HTML (element). If JSON or HTML, use JSON path or a CSS selector to get the inner value.

Destination Message Value Path - Specify a JSON path for JSON or a CSS selector for HTML to set the value of within the HTTP event entity. Only available if `Source Message Value Type` is JSON or HTML. Supports variable tags.


### Delete Value

Remove an HTTP message entity

#### Fields

Message Value - The HTTP event entity to delete.

Identifier - The property of the HTTP entity to delete. Only available for certain Message Values (e.g. request header). Supports variable tags.

### Set Variable

Set a variable using another value (text, variable, or HTTP event entity)

#### Fields

Use Message Value - Use Message Value (HTTP event entity) as the source value. Otherwise, use the specified text.

Source Message Value - The HTTP event entity to get the source value from. Only available if `Use Message Value` is selected.

Source Identifier - The property of the HTTP entity to get the source value from. Only available for certain Message Values (e.g. request header). Supports variable tags.

Text - The text to use as the source value. Only available if `Use Message Value` is not selected. Supports variable tags.

Source Message Value Type - Declare that the value is Text, JSON (node), or HTML (element). If JSON or HTML, use JSON path or a CSS selector to get the inner value.

Source Message Value Path - Specify a JSON path for JSON or a CSS selector for HTML to get a value from within the source value and then use this value instead. Only available if `Source Message Value Type` is JSON or HTML. Supports variable tags.

Use Regex Replace - Use regex on the source value.

Regex Pattern - The Regex pattern to run on the source value. If there is a successful match, a Regex replace is performed on the value using `Regex Replacement Text`. Only available if `Use Regex Replace` is selected. Supports variable tags.

Regex Pattern - The replacement value to use in the Regex replace. Only available if `Use Regex Replace` is selected. Supports variable tags.

Destination Variable Source - Global or Event scope.

Destination Variable Name - The name of the variable to set. Supports variable tags.

Destination Message Value Type - Declare that the value to set is Text, JSON (node), or HTML (element). If JSON or HTML, use JSON path or a CSS selector to set the inner value.

Destination Message Value Path - Specify a JSON path for JSON or a CSS selector for HTML to set the value of within the variable value. Only available if `Source Message Value Type` is JSON or HTML. Supports variable tags.

### Delete Variable

Delete a variable

#### Fields

Variable Source - Global or Event scope.

Variable Name - The name of the variable to delete. Supports variable tags.

### Drop

Have Burp drop the connection

#### Fields

Drop Message - If selected, Burp will be told to drop the connection.
# Rules

Rules allow you to set actions to perform (called Thens) if an HTTP or WebSocket message (event) received by Burp Suite meets certain criteria (called Whens). Rules are processed in order. If the Rule is set to auto-run, the Rule will be run automatically when an HTTP or WebSocket event is received, otherwise, it must be specifically triggered. Rules must be enabled to run at all.

HTTP events are processed by Rules under the HTTP Rules tab. WebSocket events are processed by Rules under the WebSocket Rules tab.

Note: HTTP message values that are accessible by WebSocket Rule operations refer to components of the originating ws:// or wss:// request that triggered the establishment of the WebSocket connection.

<!-- TOC -->

- [Rules](#rules)
	- [Whens](#whens)
		- [Content Type](#content-type)
		- [Event Direction](#event-direction)
		- [From Tool](#from-tool)
		- [Has Entity](#has-entity)
		- [In Scope](#in-scope)
		- [Matches Text](#matches-text)
		- [Message Type](#message-type)
		- [MIME Type](#mime-type)
		- [Proxy Name](#proxy-name)
		- [Repeat](#repeat)
	- [Thens](#thens)
		- [Break](#break)
		- [Build HTTP Message](#build-http-message)
		- [Comment](#comment)
		- [Delay](#delay)
		- [Delete Value](#delete-value)
		- [Delete Variable](#delete-variable)
		- [Drop](#drop)
		- [Evaluate](#evaluate)
		- [Highlight](#highlight)
		- [Intercept](#intercept)
		- [Log](#log)
		- [Parse HTTP Message](#parse-http-message)
		- [Prompt](#prompt)
		- [Repeat](#repeat)
		- [Run Process](#run-process)
		- [Run Rules](#run-rules)
		- [Run Script](#run-script)
		- [Save File](#save-file)
		- [Send Message](#send-message)
		- [Send Request](#send-request)
		- [Send To](#send-to)
		- [Set Encoding](#set-encoding)
		- [Set Event Direction](#set-event-direction)
		- [Set Value](#set-value)
		- [Set Variable](#set-variable)
		- [Common Fields](#common-fields)
	- [Debugging](#debugging)

<!-- /TOC -->

## Whens

Check if an event message meets certain criteria. Multiple Whens are checked in order and treated as AND conditions logically by default. If the relevant value does not match the constraints of the When (opposite if `Negate Result` is selected), unless the following When has specified to `Use OR Condition`, no further Whens are processed for the current Rule and all Thens are skipped.

### Content Type

If the HTTP request body is reported to match specified content types

Availability: HTTP, WebSocket

#### Fields

Request Content Type - None, JSON, XML, URL Encoded, Multi-Part, AMF, and/or Unknown

### Event Direction

If the HTTP message is a Request or Response, or if the WebSocket message is directed toward the client or server

Availability: HTTP, WebSocket

#### Fields

Event Direction - Request or Response for HTTP, Client or Server for WebSockets

### From Tool

If the HTTP/WebSocket message is from a specific Burp tool

Availability: HTTP, WebSocket

#### Fields

Tool - Proxy, Repeater, Intruder, Target, Scanner, Extender, or Session

### Has Entity

If the HTTP/WebSocket event contains a certain message value entity

Availability: HTTP, WebSocket

#### Fields

Message Value - The message value entity to check

Identifier - The key of the property within the message value entity to check. Only available for certain [Message Values](MessageValues.html) (e.g. request header). Supports variable tags.

### In Scope

If the URL is in the suite-wide scope

Availability: HTTP, WebSocket

#### Fields

URL - The URL to check or leave blank to use the current request's URL. Supports variable tags.

### Matches Text

If a value (text, variable, or HTTP/WebSocket message value entity) matches a value

Availability: HTTP, WebSocket

#### Fields

Use Message Value - Match on a [Message Value](MessageValues.html) (HTTP/WebSocket event entity). Otherwise, use the specified text.

Source Message Value - The HTTP/WebSocket event entity to check. Only available if `Use Message Value` is selected.

Source Identifier - The property of the HTTP/WebSocket entity to check. Only available for certain [Message Values](MessageValues.html) (e.g. request header). Supports variable tags.

Source Identifier Placement - Placement of the value to get if there are multiple (i.e. First, Last). Only available for certain [Message Values](MessageValues.html) (e.g. request header).

Source Text - The text to use as the value to check. Only available if `Use Message Value` is not selected. Supports variable tags.

Source Value Type - Declare that the value is Text, JSON (node), HTML (element), or Params (value).

Source Value Path - Specify a JSON path for JSON, a CSS selector for HTML, or a param name for Params to get a value from within the original value and then use this value instead. Only available if `Source Value Type` is JSON, HTML, or Params. Supports variable tags.

Match Type - Match the text using Equals, Contains, Begins With, Ends With, or Regex.

Match Text - The text to match the value against. Supports variable tags.

Ignore Case - If selected, use case-insensitive comparison.

### Message Type

If the WebSocket message type is text or binary

Availability: WebSocket

#### Fields

Message Type - Text or Binary

### MIME Type

If the HTTP response body is reported to match specified MIME types.

Availability: HTTP

#### Fields

Response MIME Type - HTML, Script, CSS, JSON, SVG, Other XML, Other Text, Image, Out Binary, and/or Unknown.

### Proxy Name

If received by a certain Burp proxy listener

Availability: HTTP

#### Fields

Proxy Name - The Burp proxy listener interface (e.g. 127.0.0.1:8080)

### Repeat

Repeat a group of When constraints for each item in a list

Availability: HTTP, WebSocket

#### Fields

Number of Following Whens Included - The number of When items immediately following this one that are a part of the repeat group. They will not run independently of the repeat group.

Success Criteria - `Any Match`: Repeat for each item in the list until the When constraints in the group successfully match during any iteration. If so, report success. Otherwise, report failure; `All Match`: Repeat for each item in the list ensuring that the When constraints in the group successfully match during all iterations. If so, report success. Otherwise, report failure;

List Variable Source - List variants of the Global, Event, or Session scope.

List Variable Name - The name of the variable to repeat for each item of it. Supports variable tags.

Item Event Variable Name - The name of the single item Event variable to store the current item of the list for each repeat iteration. Supports variable tags.

## Thens

### Break

Stop Rules or then action processing

Availability: HTTP, WebSocket

#### Fields

Break Type - If Skip Next Thens, skip running any further Thens of the Rule. If Skip Next Rules, skip running any further Thens and Rules for this event.

### Build HTTP Message

Build an HTTP request or response message and store the full text in a variable. The actual request or response message of the event is not changed.

Availability: HTTP, WebSocket

#### Fields

Starter HTTP Message - Text to use as the starting template for the HTTP message. Supports variable tags.

Message Value Setters - Set parts of the HTTP message.

Source Text - The text to set in the message. Supports variable tags.

Destination Message Value - The HTTP message entity to set the value of.

Destination Identifier - The property of the HTTP message to set the value of. Only available for certain [Message Values](MessageValues.html) (e.g. request header). Supports variable tags.

Destination Identifier Placement - Placement of the value to set if there are multiple (i.e. First, Last, All, Only - Keep One, New - Add additional). Only available for certain [Message Values](MessageValues.html) (e.g. request header).

Destination Variable Source - Single item or list variants of the Global, Event, or Session scope. See [Set List Variable](#set-list-variable) for fields that are available if a list variant is chosen.

Destination Variable Name - The name of the variable to hold the built HTTP message. Supports variable tags.

### Comment

Add a comment to the line item in the HTTP/WebSocket history

Availability: HTTP, WebSocket

#### Fields

Text - The text of the comment. Supports variable tags.

### Delay

Delay further processing/sending of the HTTP/WebSocket event

Availability: HTTP, WebSocket

#### Fields

Delay (milliseconds) - The amount of time to delay further processing. Supports variable tags.

### Delete Value

Remove an HTTP message entity

Availability: HTTP

#### Fields

Message Value - The HTTP event entity to delete.

Identifier - The property of the HTTP entity to delete. Only available for certain [Message Values](MessageValues.html) (e.g. request header). Supports variable tags.

Identifier Placement - Placement of the value to delete if there are multiple. (i.e. First, Last, All)

### Delete Variable

Delete a variable

Availability: HTTP, WebSocket

#### Fields

Variable Source - Single item or list variants of the Global, Event, or Session scope.

Variable Name - The name of the variable to delete. Supports variable tags.

Item Placement - `First`: Delete the first item in the list; `Last`: Delete the last item in the list; `Index`: Delete zero-based Nth item of the list; `All`: Delete the entire list variable. Only available if `Variable Source` is a list variant.

Index - The zero-based index of the item to delete from the list. The index must already exist in the list. Only available if `Variable Source` is a list variant and `Item Placement` is `Index`. Supports variable tags.

### Drop

Have Burp drop the connection

Availability: HTTP, WebSocket

#### Fields

Drop Message - If selected, Burp will be told to drop the connection.

### Evaluate

Perform operations on values

Availability: HTTP, WebSocket

#### Fields

X - First value. Supports variable tags.

Operation - `Add`, `Subtract`, `Multiply`, `Divide By`, `Increment`, `Decrement`, `Mod`, `Abs`, `Round`, `Equals`, `Greater Than`, `Greater Than Or Equals`, `Less Than`, or `Less Than Or Equals`

Y - Second value. Only available for certain operations. Supports variable tags.

### Highlight

Highlight the line item in the HTTP/WebSocket history

Availability: HTTP, WebSocket

#### Fields

Color - The color used to highlight the line item.

### Intercept

Intercept the message in the Proxy interceptor

Only relevant for Proxy tool-captured events.

Availability: HTTP, WebSocket

#### Fields

Action - User Defined, Intercept, or Disable.

### Log

Log message to the Burp extension console

Availability: HTTP, WebSocket

#### Fields

Text - The text to log. Supports variable tags.

### Parse HTTP Message

Extract values from an HTTP request or response message and store the values in a variable.

Availability: HTTP, WebSocket

#### Fields

HTTP Message - Text to use as the HTTP message. Supports variable tags.

Message Value Getters - Get parts of the HTTP message.

Source Text - The text to set in the message. Supports variable tags.

Source Message Value - The HTTP message entity from which to extract a value.

Source Identifier - The property of the HTTP entity to extract a value from. Only available for certain [Message Values](MessageValues.html) (e.g. request header). Supports variable tags.

Source Identifier Placement - Placement of the value to get if there are multiple (i.e. First, Last). Only available for certain [Message Values](MessageValues.html) (e.g. request header).

Destination Variable Source - Single item or list variants of the Global, Event, or Session scope. See [Set List Variable](#set-list-variable) for fields that are available if a list variant is chosen.

Destination Variable Name - The name of the variable to hold the built HTTP message value. Supports variable tags.

### Prompt

Get text via a prompt dialog

Availability: HTTP, WebSocket

#### Fields

Description - Description text to display in the prompt above the text entry field. Supports variable tags.

Starter Text - Initial text in the text entry field. Supports variable tags.

Fail After (milliseconds) - Flag the request as failed after waiting the specified amount of time for the response. Only available if `Wait for Completion` is selected. Supports variable tags.

Break After Failure - Do not run any other Thens or Rules for this event if the request was flagged as failed. Only available if `Wait for Completion` is selected.

Capture Variable Source - Single item or list variants of the Global, Event, or Session scope. See [Set List Variable](#set-list-variable) for fields that are available if a list variant is chosen.

Capture Variable Name - The name of the variable to store the response message. Supports variable tags.

### Repeat

Repeat a group of Then actions by count, boolean value, or for each item in a list

Availability: HTTP, WebSocket

#### Fields

Number of Following Thens Included - The number of Then items immediately following this one that are a part of the repeat group. They will not run independently of the repeat group.

Repeat Condition - `Count`: Repeat a specified number of times; `Has Next Item`: Repeat for each item in a list variable; `While True`: Repeat while a value is true, y, 1, yes, or on.

Count - Number of times to repeat. Only available if `Repeat Condition` is `Count`. Supports variable tags.

List Variable Source - List variants of the Global, Event, or Session scope. Only available if `Repeat Condition` is `Has Next Item`.

List Variable Name - The name of the variable to repeat for each item of it. Only available if `Repeat Condition` is `Has Next Item`. Supports variable tags.

Item Event Variable Name - The name of the single item Event variable to store the current item of the list for each repeat iteration. Only available if `Repeat Condition` is `Has Next Item`. Supports variable tags.

Boolean Value - Repeat while this value is `true`, `y`, `1`, `yes`, or `on`. Boolean Value should contain a variable tag whose value would change between the repeat iterations in order to avoid unexpected repeating. Only available if `Repeat Condition` is `While True`. Supports variable tags.

Max Count - The max number of times to repeat in situations where `Boolean Value` never evaluates to a false equivalent value. Only available if `Repeat Condition` is `While True`.

### Run Process

Execute a command in a separate process

Availability: HTTP, WebSocket

#### Fields

Command - Command to execute in a separate process. Supports variable tags. Example: `cmd.exe /c dir`

Stdin - Value to send to standard input. Supports variable tags.

Wait for Completion - Wait for the process to exit before continuing.

Fail After (milliseconds) - Flag the process as failed after waiting the specified amount of time for the process to exit. Only available if `Wait for Completion` is selected. Supports variable tags.

Fail on Non-Zero Exit Code - Flag the process as failed if the process returned a non-zero exit code. Only available if `Wait for Completion` is selected.

Kill After Failure - Kill the process after a wait timeout. Only available if `Wait for Completion` is selected.

Break After Failure - Do not run any other Thens or Rules for this event if the process was flagged as failed. Only available if `Wait for Completion` is selected.

Capture Output - Capture standard out of the process. Only available if `Wait for Completion` is selected.

Capture After Failure - Capture standard out even if the process is flagged as failed. Only available if `Wait for Completion` and `Capture Output` is selected.

Capture Variable Source - Single item or list variants of the Global, Event, or Session scope. See [Set List Variable](#set-list-variable) for fields that are available if a list variant is chosen.

Capture Variable Name - The name of the variable to store the captured output. Supports variable tags.

### Run Rules

Run a specific Rule or all auto-run Rules

Availability: HTTP, WebSocket

#### Fields

Run Single - Run a specific Rule is selected. Otherwise, run all auto-run Rules.

Run Name - The name of the Rule to run. Only available if `Run Single` is selected.

### Run Script

Execute a JavaScript script

Availability: HTTP, WebSocket

The engine supports up to partial ES6/ES2015. Scripts have access to certain Reshaper-specific functions. See [Scripting Library](ScriptingLibrary.html)

#### Fields

Script - The text of the JavaScript script to run.

Max Execution (secs) - Terminate long-running scripts after this time.

### Save File

Save text to a file

Availability: HTTP, WebSocket

#### Fields

File Path - File path of the file including the file name. Supports variable tags.

Text - The text to save. Supports variable tags.

Encoding - The charset/encoding of the file (e.g. UTF-8). Supports variable tags.

File Exists Action - Action to do if the file already exists: None (Don't write), Overwrite, Append

### Send Message

Send a separate WebSocket message

Availability: WebSocket

#### Fields

Event Direction - Send to Client or Server. Sending to the client is only allowed for WebSockets captured by the Proxy tool.

Message - The message to send. Supports variable tags.

### Send Request

Send a separate HTTP request

Availability: HTTP, WebSocket

#### Fields

Request - The HTTP request message to send. Uses the value from the current event if left blank. Supports variable tags.

URL - The URL of the request. If this is set, it overrides the Host request header, the request message URI, protocol, address, and port. Supports variable tags.

Protocol - `http` or `https`. If this is set, it overrides the values from the URL (if set) or the current event. Supports variable tags.

Address - Hostname without port. If this is set, it overrides the values from the URL (if set) or the current event. Example: `www.example.com`. Supports variable tags.

Port - Example: `80`. If this is set, it overrides the values from the URL (if set) or the current event. Supports variable tags.

Wait for Completion - Wait for a response before continuing.

Fail After (milliseconds) - Flag the request as failed after waiting the specified amount of time for the response. Only available if `Wait for Completion` is selected. Supports variable tags.

Fail on Error Status Code - Flag the request as failed if the response returned a 4xx or 5xx HTTP status code. Only available if `Wait for Completion` is selected.

Break After Failure - Do not run any other Thens or Rules for this event if the request was flagged as failed. Only available if `Wait for Completion` is selected.

Capture Output - Capture the HTTP response message. Only available if `Wait for Completion` is selected.

Capture After Failure - Capture the HTTP response message even if the request is flagged as failed. Only available if `Wait for Completion` and `Capture Output` is selected.

Capture Variable Source - Single item or list variants of the Global, Event, or Session scope. See [Set List Variable](#set-list-variable) for fields that are available if a list variant is chosen.

Capture Variable Name - The name of the variable to store the response message. Supports variable tags.

### Send To

Send data to other Burp tools or the system's default browser

Availability: HTTP, WebSocket

#### Fields

Send To - Comparer, Intruder, Repeater, or Browser

Override Defaults - Select to be able to override values to send to the given Burp tool

Host - Leave empty to use the default value. Only available for Intruder and Repeater and if `Override Defaults` is selected. Supports variable tags.

Port - Leave empty to use the default value. Only available for Intruder and Repeater and if `Override Defaults` is selected. Supports variable tags.

Protocol - HTTP or HTTPS. Leave empty to use the default value. Only available for Intruder and Repeater and if `Override Defaults` is selected. Supports variable tags.

Request - Full HTTP request text. Leave empty to use the default value. Only available for Intruder and Repeater and if `Override Defaults` is selected. Supports variable tags.

Value - Value to compare. Leave empty to use the default value. Only available for Comparer and if `Override Defaults` is selected. Supports variable tags.

URL - Leave empty to use the default value. Only available for Browser, and `Override Defaults` is selected. Supports variable tags.

### Set Encoding

Set the encoding used to read and write bytes of the HTTP request or response body, or WebSocket binary message

Availability: HTTP, WebSocket

#### Fields

Encoding - The charset/encoding of the file (e.g. UTF-8). Supports variable tags.

### Set Event Direction

Change whether to send a request or to send a response at the end of processing

Availability: HTTP

If the event direction is switched from request to response, no request is sent. Instead, whatever is set in the HTTP response message is sent. Switching from response to request is not functional.

#### Fields

Set Event Direction - Request or Response.

### Set Value

Set the value of an HTTP/WebSocket event using another value (text, variable, or HTTP/WebSocket event entity)

Availability: HTTP, WebSocket

#### Fields

Use Message Value - Use [Message Value](MessageValues.html) (HTTP/WebSocket event entity) as the source value. Otherwise, use the specified text.

Source Message Value - The HTTP/WebSocket event entity from which to get the source value. Only available if `Use Message Value` is selected.

Source Identifier - The property of the HTTP/WebSocket entity to get the source value from. Only available for certain [Message Values](MessageValues.html) (e.g. request header). Supports variable tags.

Source Identifier Placement - Placement of the value to get if there are multiple (i.e. First, Last). Only available for certain [Message Values](MessageValues.html) (e.g. request header).

Source Text - The text to use as the source value. Only available if `Use Message Value` is not selected. Supports variable tags.

Source Value Type - Declare that the value is Text, JSON (node), HTML (element), or Params (value).

Source Value Path - Specify a JSON path for JSON, a CSS selector for HTML, or a param name for Params to get a value from within the original value and then use this value instead. Only available if `Source Value Type` is JSON, HTML, or Params. Supports variable tags.

Use Regex Replace - Use regex on the source value.

Regex Pattern - The Regex pattern to run on the source value. If there is a successful match, a Regex replacement is performed on the value using `Regex Replacement Text`. Only available if `Use Regex Replace` is selected. Supports variable tags.

Regex Pattern - The replacement value to use in the Regex replacement. Only available if `Use Regex Replace` is selected. Supports variable tags.

Destination Message Value - The HTTP/WebSocket event entity to set the value of.

Destination Identifier - The property of the HTTP/WebSocket entity to set the value of. Only available for certain [Message Values](MessageValues.html) (e.g. request header). Supports variable tags.

Destination Identifier Placement - Placement of the value to set if there are multiple (i.e. First, Last, All, Only - Keep One, New - Add additional). Only available for certain [Message Values](MessageValues.html) (e.g. request header).

Destination Value Type - Declare that the value to set is Text, JSON (node), HTML (element), or Params (value).

Destination Value Path - Specify a JSON path for JSON, a CSS selector for HTML, or a param name for Params to get a value from within the original value and then use this value instead. Only available if `Destination Value Type` is JSON, HTML, or Params. Supports variable tags.

### Set Variable

Set a variable using another value (text, variable, or HTTP/WebSocket event entity)

Availability: HTTP, WebSocket

#### Fields

Use Message Value - Use [Message Value](MessageValues.html) (HTTP/WebSocket event entity) as the source value. Otherwise, use the specified text.

Source Message Value - The HTTP/WebSocket event entity from which to get the source value. Only available if `Use Message Value` is selected.

Source Identifier - The property of the HTTP/WebSocket entity to get the source value from. Only available for certain [Message Values](MessageValues.html) (e.g. request header). Supports variable tags.

Source Identifier Placement - Placement of the value to get if there are multiple (i.e. First, Last). Only available for certain [Message Values](MessageValues.html) (e.g. request header).

Source Text - The text to use as the source value. Only available if `Use Message Value` is not selected. Supports variable tags.

Source Value Type - Declare that the value is Text, JSON (node), HTML (element), or Params (value).

Source Value Path - Specify a JSON path for JSON, a CSS selector for HTML, or a param name for Params to get a value from within the original value and then use this value instead. Only available if `Source Value Type` is JSON, HTML, or Params. Supports variable tags.

Use Regex Replace - Use regex on the source value.

Regex Pattern - The Regex pattern to run on the source value. If there is a successful match, a Regex replacement is performed on the value using `Regex Replacement Text`. Only available if `Use Regex Replace` is selected. Supports variable tags.

Regex Pattern - The replacement value to use in the Regex replacement. Only available if `Use Regex Replace` is selected. Supports variable tags.

Destination Variable Source - Single item or list variants of the Global, Event, or Session scope. See [Set List Variable](#set-list-variable) for fields that are available if a list variant is chosen.

Destination Variable Name - The name of the variable to set. Supports variable tags.

Destination Value Type - Declare that the value to set is Text, JSON (node), HTML (element), or Params (value).

Destination Value Path - Specify a JSON path for JSON, a CSS selector for HTML, or a param name for Params to get a value from within the original value and then use this value instead. Only available if `Destination Value Type` is JSON, HTML, or Params. Supports variable tags.

### Common Fields

#### Set List Variable

The following fields are only available if the variable source is a list variant.

Item Placement - `First`: Set/overwrite the first item in the list; `Last`: Set/overwrite the last item in the list; `Index`: Set/overwrite zero-based Nth item of the list; `Add First`: Insert as the first item in the list; `Add Last`: Insert as the last item in the list; `All`: Reset the list with a new delimited value;

Index - The zero-based index to place the value in the list. The index must already exist in the list or be +1 beyond the last item in the list. Only available if `Item Placement` is `Index`. Supports variable tags.

Delimiter - The delimiter used to split the value to create individual items in the list. Note, use special variable tags to specify characters like new lines. Only available if `Item Placement` is `All`. Supports variable tags.


## Debugging

Rules can be debugged by enabling event diagnostics (_Settings > General > Enable Event Diagnostics_) to debug all Rules or by right-clicking the specific Rules you want to debug in the Rules list and selecting `Toggle Debug Logging` in the context menu. This will log details about the actions the Rule(s) have taken for each event (request, response, or WebSocket message) processed, including the result of When constraint checks, and the values that were used in Whens and Thens.

Example Diagnostic Output:

```
Request: http://example.com/
	Rule: Test
		    When Event Direction('Request' equals 'Request') - PASS
		AND When Matches Text('example.com' contains 'example') - PASS
		    Then Set Value(destinationMessageValue='Request Header' destinationIdentifier='special' input='Mine')
		    Then Highlight('orange')
	End Rule
End Request

Response: http://example.com/
	Rule: Test
		    When Event Direction('Response' equals 'Request') - FAIL
	End Rule
End Response
```

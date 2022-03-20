# Rules

Rules allow you to set actions to perform (called Thens) if HTTP messages/connections (event) received by Burp Suite meet certain criteria (called Whens). Rules are processed in order. If the Rule is set to auto-run, the Rule will be run automatically when an HTTP event is received, otherwise it must be specifically triggered. Rules must be enabled to run at all.

* auto-gen TOC:
{:toc}

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

Identifier - The property of the HTTP entity to check. Only available for certain [Message Values](MessageValues.html) (e.g. request header). Supports variable tags.

### Matches Text

If a value (text, variable, or HTTP event entity) matches a value

#### Fields

Use Message Value - Match on a [Message Value](MessageValues.html) (HTTP event entity). Otherwise, use the specified text.

Source Message Value - The HTTP event entity to check. Only available if `Use Message Value` is selected.

Source Identifier - The property of the HTTP entity to check. Only available for certain [Message Values](MessageValues.html) (e.g. request header). Supports variable tags.

Source Identifier Placement - Placement of the value to get if there are multiple (i.e. First, Last). Only available for certain [Message Values](MessageValues.html) (e.g. request header).

Source Text - The text to use as the value to check. Only available if `Use Message Value` is not selected. Supports variable tags.

Source Value Type - Declare that the value is Text, JSON (node), HTML (element), or Params (value).

Source Value Path - Specify a JSON path for JSON, a CSS selector for HTML, or a param name for Params to get a value from within the original value and then use this value instead. Only available if `Source Value Type` is JSON, HTML, or Params. Supports variable tags.

Match Type - Match the text using Equals, Contains, Begins With, Ends With, or Regex.

Match Text - The text to match the value against. Supports variable tags.

### Content Type

If the HTTP request body is reported to match specified content types.

#### Fields

Request Content Type - None, JSON, XML, URL Encoded, Multi-Part, AMF, and/or Unknown

### MIME Type

If the HTTP response body is reported to match specified MIME types.

#### Fields

Response MIME Type - HTML, Script, CSS, JSON, SVG, Other XML, Other Text, Image, Out Binary, and/or Unknown.

### Proxy Name

If received by a certain Burp proxy listener

#### Fields

Proxy Name - The Burp proxy listener interface (e.g. 127.0.0.1:8080)

### From Tool

If the HTTP message is from a specific Burp tool

#### Fields

Tool - Proxy, Repeater, Intruder, Target, Spider, Scanner, Extender, or Session

### In Scope

If the URL is in the suite-wide scope

#### Fields

URL - The URL to check or leave blank to use the current request's URL. Supports variable tags. 

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

### Comment

Add a comment to the request/response line in the HTTP history

#### Fields

Text - The text of the comment. Supports variable tags.

### Prompt

Get text via a prompt dialog.

#### Fields

Description - Description text to display in the prompt above the text entry field. Supports variable tags.

Starter Text - Initial text in the text entry field. Supports variable tags.

Fail After (milliseconds) - Flag the request as failed after waiting the specified amount of time for the response. Only available if `Wait for Completion` is selected. Supports variable tags.

Break After Failure - Do not run any other Thens or Rules for this event if the request was flagged as failed. Only available if `Wait for Completion` is selected.

Capture Variable Source - Global or Event scope.

Capture Variable Name - The name of variable to store the response message. Supports variable tags.

### Run Rules

Run a specific rule or all auto-run rules.

#### Fields

Run Single - Run a specific Rule is selected. Otherwise, run all auto-run Rules.

Run Name - The name of the Rule to run. Only available if `Run Single` is selected.

### Run Script

Execute a JavaScript script

The engine supports up to partial ES6/ES2015. Scripts have access to certain Reshaper specific functions. See [Scripting Library](ScriptingLibrary.html)

#### Fields

Script - The text of the JavaScript script to run.

Max Execution (secs) - Terminate long-running scripts after this time.

### Evaluate

Perform operations on values

#### Fields

X - First value.  Supports variable tags.

Operation - `Add`, `Subtract`, `Multiply`, `Divide By`, `Increment`, `Decrement`, `Mod`, `Abs`, `Round`, `Equals`, `Greater Than`, `Greater Than Or Equals`, `Less Than`, or `Less Than Or Equals`

Y - Second value. Only available for certain operations. Supports variable tags.

### Set Event Direction

Change whether to send a request or to send a response at the end of processing

If the event direction is switched from request to response, no request is sent. Instead, whatever is set in the HTTP response message is sent. Switching from response to request is not functional.

#### Fields

Set Event Direction - Request or Response.

### Set Encoding

Set the encoding used to read and write bytes of the HTTP request or response body.

#### Fields

Encoding - The charset/encoding of the file (e.g. uft-8). Supports variable tags.

### Set Value

Set the value of an HTTP event using another value (text, variable, or HTTP event entity)

#### Fields

Use Message Value - Use [Message Value](MessageValues.html) (HTTP event entity) as the source value. Otherwise, use the specified text.

Source Message Value - The HTTP event entity to get the source value from. Only available if `Use Message Value` is selected.

Source Identifier - The property of the HTTP entity to get the source value from. Only available for certain [Message Values](MessageValues.html) (e.g. request header). Supports variable tags.

Source Identifier Placement - Placement of the value to get if there are multiple (i.e. First, Last). Only available for certain [Message Values](MessageValues.html) (e.g. request header).

Source Text - The text to use as the source value. Only available if `Use Message Value` is not selected. Supports variable tags.

Source Value Type - Declare that the value is Text, JSON (node), HTML (element), or Params (value).

Source Value Path - Specify a JSON path for JSON, a CSS selector for HTML, or a param name for Params to get a value from within the original value and then use this value instead. Only available if `Source Value Type` is JSON, HTML, or Params. Supports variable tags.

Use Regex Replace - Use regex on the source value.

Regex Pattern - The Regex pattern to run on the source value. If there is a successful match, a Regex replace is performed on the value using `Regex Replacement Text`. Only available if `Use Regex Replace` is selected. Supports variable tags.

Regex Pattern - The replacement value to use in the Regex replace. Only available if `Use Regex Replace` is selected. Supports variable tags.

Destination Message Value - The HTTP event entity to set the value of.

Destination Identifier - The property of the HTTP entity to set the value of. Only available for certain [Message Values](MessageValues.html) (e.g. request header). Supports variable tags.

Destination Identifier Placement - Placement of the value to set if there are multiple (i.e. First, Last, All, Only - Keep One, New - Add additional). Only available for certain [Message Values](MessageValues.html) (e.g. request header).

Destination Value Type - Declare that the value to set is Text, JSON (node), HTML (element), or Params (value).

Destination Value Path - Specify a JSON path for JSON, a CSS selector for HTML, or a param name for Params to get a value from within the original value and then use this value instead. Only available if `Destination Value Type` is JSON, HTML, or Params. Supports variable tags.


### Delete Value

Remove an HTTP message entity

#### Fields

Message Value - The HTTP event entity to delete.

Identifier - The property of the HTTP entity to delete. Only available for certain [Message Values](MessageValues.html) (e.g. request header). Supports variable tags.

Identifier Placement - Placement of the value to delete if there are multiple. (i.e. First, Last, All)

### Set Variable

Set a variable using another value (text, variable, or HTTP event entity)

#### Fields

Use Message Value - Use [Message Value](MessageValues.html) (HTTP event entity) as the source value. Otherwise, use the specified text.

Source Message Value - The HTTP event entity to get the source value from. Only available if `Use Message Value` is selected.

Source Identifier - The property of the HTTP entity to get the source value from. Only available for certain [Message Values](MessageValues.html) (e.g. request header). Supports variable tags.

Source Identifier Placement - Placement of the value to get if there are multiple (i.e. First, Last). Only available for certain [Message Values](MessageValues.html) (e.g. request header).

Source Text - The text to use as the source value. Only available if `Use Message Value` is not selected. Supports variable tags.

Source Value Type - Declare that the value is Text, JSON (node), HTML (element), or Params (value).

Source Value Path - Specify a JSON path for JSON, a CSS selector for HTML, or a param name for Params to get a value from within the original value and then use this value instead. Only available if `Source Value Type` is JSON, HTML, or Params. Supports variable tags.

Use Regex Replace - Use regex on the source value.

Regex Pattern - The Regex pattern to run on the source value. If there is a successful match, a Regex replace is performed on the value using `Regex Replacement Text`. Only available if `Use Regex Replace` is selected. Supports variable tags.

Regex Pattern - The replacement value to use in the Regex replace. Only available if `Use Regex Replace` is selected. Supports variable tags.

Destination Variable Source - Global or Event scope.

Destination Variable Name - The name of the variable to set. Supports variable tags.

Destination Value Type - Declare that the value to set is Text, JSON (node), HTML (element), or Params (value).

Destination Value Path - Specify a JSON path for JSON, a CSS selector for HTML, or a param name for Params to get a value from within the original value and then use this value instead. Only available if `Destination Value Type` is JSON, HTML, or Params. Supports variable tags.

### Delete Variable

Delete a variable

#### Fields

Variable Source - Global or Event scope.

Variable Name - The name of the variable to delete. Supports variable tags.

### Save File

Save text to a file.

#### Fields

File Path - File path of the file, include file name. Supports variable tags.

Text - The text to save. Supports variable tags.

Encoding - The charset/encoding of the file (e.g. uft-8). Supports variable tags.

File Exists Action - Action to do if the file already exist: None (Don't write), Overwrite, Append

### Send To

Send data to other Burp tools or the system default browser

#### Fields

Send To - Comparer, Intruder, Repeater Spider, or Browser

Override Defaults - Select to be able to override values to send to the given Burp tool

Host - Leave empty to use default value. Only available for Intruder and Repeater and if `Override Defaults` is selected. Supports variable tags.

Port - Leave empty to use default value. Only available for Intruder and Repeater and if `Override Defaults` is selected. Supports variable tags.

Protocol - HTTP or HTTPS. Leave empty to use default value. Only available for Intruder and Repeater and if `Override Defaults` is selected. Supports variable tags.

Request - Full HTTP request text. Leave empty to use default value. Only available for Intruder and Repeater and if `Override Defaults` is selected. Supports variable tags.

Value - Value to compare. Leave empty to use default value. Only available for Comparer and if `Override Defaults` is selected. Supports variable tags.

URL - Leave empty to use default value. Only available for if Spider or Browser, and `Override Defaults` is selected. Supports variable tags.

### Run Process

Execute a command in a separate process

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

Capture Variable Source - Global or Event scope.

Capture Variable Name - The name of variable to store the captured output. Supports variable tags.

### Build HTTP Message

Build an HTTP request or response message and store the full text in a variable. The actual request or response message of the event is not changed.

#### Fields

Starter HTTP Message - Text to use as the starting template for the HTTP message. Supports variable tags.

Message Value Setters - Set parts of the HTTP message.

Source Text - The text to set in the message. Supports variable tags.

Destination Message Value - The HTTP message entity to set the value of.

Destination Identifier - The property of the HTTP message to set the value of. Only available for certain [Message Values](MessageValues.html) (e.g. request header). Supports variable tags.

Destination Identifier Placement - Placement of the value to set if there are multiple (i.e. First, Last, All, Only - Keep One, New - Add additional). Only available for certain [Message Values](MessageValues.html) (e.g. request header).

Destination Variable Source - Global or Event scope.

Destination Variable Name - The name of the variable to hold the built HTTP message. Supports variable tags.

### Parse HTTP Message

Extract values from an HTTP request or response message and store the values in variable.

#### Fields

HTTP Message - Text to use as the HTTP message. Supports variable tags.

Message Value Getters - Get parts of the HTTP message.

Source Text - The text to set in the message. Supports variable tags.

Source Message Value - The HTTP message entity to extract a value from.

Source Identifier - The property of the HTTP entity to extract a value from. Only available for certain [Message Values](MessageValues.html) (e.g. request header). Supports variable tags.

Source Identifier Placement - Placement of the value to get if there are multiple (i.e. First, Last). Only available for certain [Message Values](MessageValues.html) (e.g. request header).

Destination Variable Source - Global or Event scope.

Destination Variable Name - The name of the variable to hold the built HTTP message value. Supports variable tags.

### Send Request

Send a separate HTTP request.

#### Fields

Request - The HTTP request message to send. Uses the value from the current event if left blank. Supports variable tags.

URL - The URL of the request. If this is set, it overrides the Host request header, the request message URI, protocol, address, and port. Supports variable tags.

Protocol - `http` or `https`. If this is set, it overrides the values from the URL (if set) or the current event. Supports variable tags.

Address - Hostname without port. If this is set, it overrides the values from the URL (if set) or the current event. Example: `www.example.com`. Supports variable tags.

Port - Example: `80`. If this is set, it overrides the values from the URL (if set) or the current event. Supports variable tags.

Wait for Completion - Wait for a response before continuing.

Fail After (milliseconds) - Flag the request as failed after waiting the specified amount of time for the response. Only available if `Wait for Completion` is selected. Supports variable tags.

Fail on Error Status Code - Flag the request as failed if the response returned a with a 4xx or 5xx HTTP status code. Only available if `Wait for Completion` is selected.

Break After Failure - Do not run any other Thens or Rules for this event if the request was flagged as failed. Only available if `Wait for Completion` is selected.

Capture Output - Capture the HTTP response message. Only available if `Wait for Completion` is selected.

Capture After Failure - Capture the HTTP response message even if the request is flagged as failed. Only available if `Wait for Completion` and `Capture Output` is selected.

Capture Variable Source - Global or Event scope.

Capture Variable Name - The name of variable to store the response message. Supports variable tags.

### Drop

Have Burp drop the connection

#### Fields

Drop Message - If selected, Burp will be told to drop the connection.

## Debugging

Rules can be debugged by enabling event diagnostics (*Settings > General > Enable Event Diagnostics*). This will log details about all rules that were run for each event (request or response) including the result of When constraint checks, and the values that were used in Whens and Thens.

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

# Reshaper for Burp

Extension for Burp Suite to trigger actions and reshape HTTP request/response and WebSocket traffic using configurable Rules

![Screenshot](https://user-images.githubusercontent.com/48854453/206939994-3cf7beb7-61bb-4f12-8b7b-10239e4d0281.png)

[Example Usage](https://synfron.github.io/ReshaperForBurp/Examples.html)

## Rules

Rules allow you to set actions to perform (called Thens) if messages/connections (event) received by Burp Suite meet certain criteria (called Whens). Rules are processed in order.

[More](https://synfron.github.io/ReshaperForBurp/Rules.html)

### Whens

Content Type - If the HTTP request body is reported to match specified content types

Event Direction - If the HTTP message is a Request or Response, or if the WebSocket message is directed toward the client or server

From Tool - If the HTTP/WebSocket message is from a specific Burp tool

Has Entity - If the HTTP/WebSocket event contains a certain message value entity

In Scope - If the URL is in the suite-wide scope

Matches Text - If a value (text, variable, or HTTP/WebSocket message value entity) matches a value

Message Type - If the WebSocket message is text or binary

MIME Type - If the HTTP response body is reported to match specified MIME types

Proxy Name - If received by a certain Burp proxy listener

Repeat - Repeat a group of When constraints for each item in a list

[More](https://synfron.github.io/ReshaperForBurp/Rules.html#whens)

### Thens

Break - Stop Rules or then action processing

Build HTTP Message - Build an HTTP request or response message and store the full text in a variable

Comment - Add a comment to the line item in the HTTP/WebSocket history

Delay - Delay further processing/sending of the HTTP/WebSocket event

Delete Value - Remove an HTTP message entity

Delete Variable - Delete a variable

Drop - Have Burp drop the connection

Evaluate - Perform operations on values

Highlight - Highlight the line item in the HTTP/WebSocket history

Intercept - Intercept the message in the Proxy interceptor

Log - Log message to the Burp extension console

Parse HTTP Message - Extract values from an HTTP request or response message and store the values in variable

Prompt - Get text via a prompt dialog

Repeat - Repeat a group of Then actions by count, boolean value, or for each item in a list

Run Process - Execute a command in a separate process

Run Rules - Run a specific Rule or all auto-run Rules

Run Script - Execute a JavaScript script

Save File - Save text to a file

Set Encoding - Set the encoding used to read and write bytes of the HTTP request or response body, or WebSocket message 

Set Event Direction - Change whether to send a request or to send a response at the end of processing

Set Value - Set the value of an HTTP/WebSocket event using another value (text, variable, or HTTP/WebSocket event entity)

Set Variable - Set a variable using another value (text, variable, or HTTP/WebSocket event entity)

Send Message - Send a separate WebSocket message

Send Request - Send a separate HTTP request

Send To - Send data to other Burp tools or the system's default browser

[More](https://synfron.github.io/ReshaperForBurp/Rules.html#thens)

## Variables

Share values across different Rules while processing the same event or all events.

[More](https://synfron.github.io/ReshaperForBurp/Variables.html)

## Development

### Build JAR with IntelliJ

1. Open IntelliJ.
2. Create a new project (Gradle) from existing source using Java 17.
3. Once the project is created/open, wait for IntelliJ to process Gradle dependencies.
4. Run the `jar` Gradle build task from the Gradle tool window/sidebar. The JAR will be placed in the `build\libs` directory.

### Build JAR with CLI

1. Install Java 17.
2. Install Gradle v7.4.
3. Run the `gradle --refresh-dependencies build` command.
4. Run the `gradle build jar` command. The JAR will be placed in the `build\libs` directory.

### Debugging

#### IntelliJ

1. Apply this [git patch](https://gist.github.com/ddwightx/6965732339bdf4cd022d550f40a9e99f) to the project to allow Reshaper to be debugged as a legacy extension in Burp Suite.
2. In Reshaper, using the Settings tab, export all Rules and global variables to a JSON file to prevent data loss.
3. In Extender, unload the Reshaper extension from Burp Suite if you already have the extension installed from the BApp Store or from a JAR.
4. Close Burp Suite.
5. Open the Reshaper project in IntelliJ.
6. Navigate to `java/synfron/reshaper/burp/ui/Window.java`.
7. Right-click the file in the Project view and click `Run Window.main()` or `Run Window.main()`.
8. Burp Suite will open with Reshaper loaded as an legacy extension.

#### CLI

1. Apply this [git patch](https://gist.github.com/ddwightx/6965732339bdf4cd022d550f40a9e99f) to the project to allow Reshaper to be debugged as a legacy extension in Burp Suite.
2. In Reshaper, using the Settings tab, export all Rules and global variables to a JSON file to prevent data loss.
3. In Extender, unload the Reshaper extension from Burp Suite if you already have the extension installed from the BApp Store or from a JAR.
4. Close Burp Suite.
5. In a CLI, execute `java -cp path/to/the/reshaper-for-burp/JAR/file.jar synfron.reshaper.burp.ui.Window`.
6. Burp Suite will open with Reshaper loaded as a legacy extension.

## Contributions

Contributions are encouraged. Issues and Pull Requests welcome. Also help us spread the word.

Primary Developer: Daquanne Dwight

## Support

For help with how to use Reshaper for a particular need, to report a bug, or to make a suggestion, create an issue in GitHub or email support[at]synfron.com.

## License

MIT License. See [LICENSE](https://github.com/synfron/ReshaperForBurp/blob/master/LICENSE)

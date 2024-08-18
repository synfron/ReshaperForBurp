# Reshaper for Burp

Extension for Burp Suite to trigger actions and reshape HTTP request/response and WebSocket traffic using configurable Rules

![Screenshot](https://github.com/user-attachments/assets/3e07dbe2-a71a-4a8a-8f75-206ce7bb9254)

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

[More](https://synfron.github.io/ReshaperForBurp/Whens.html)

### Thens

Break - Stop Rules or then action processing

Build HTTP Message - Build an HTTP request or response message and store the full text in a variable

Comment - Add a comment to the line item in the HTTP/WebSocket history

Delay - Delay further processing/sending of the HTTP/WebSocket event

Delete Value - Remove an HTTP message entity

Delete Variable - Delete a variable

Drop - Have Burp drop the connection

Evaluate - Perform operations on values

Extract - Extract values into lists

Generate - Generate a value

Highlight - Highlight the line item in the HTTP/WebSocket history

Intercept - Intercept the message in the Proxy interceptor

Log - Log message to the Burp extension console

Parse HTTP Message - Extract values from an HTTP request or response message and store the values in variable

Prompt - Get text via a prompt dialog

Read File - Read a file

Repeat - Repeat a group of Then actions by count, boolean value, or for each item in a list

Run Process - Execute a command in a separate process

Run Rules - Run a specific Rule or all auto-run Rules

Run Script - Execute a JavaScript script

Save File - Save text to a file

Send Message - Send a separate WebSocket message

Send Request - Send a separate HTTP request

Send To - Send data to other Burp tools or the system's default browser

Set Encoding - Set the encoding used to read and write bytes of the HTTP request or response body, or WebSocket message

Set Event Direction - Change whether to send a request or to send a response at the end of processing

Set Value - Set the value of an HTTP/WebSocket event using another value (text, variable, or HTTP/WebSocket event entity)

Set Variable - Set a variable using another value (text, variable, or HTTP/WebSocket event entity)

Transform - Transform/convert a value

[More](https://synfron.github.io/ReshaperForBurp/Thens.html)

## Variables

Share values across different Rules while processing the same event or all events.

[More](https://synfron.github.io/ReshaperForBurp/Variables.html)

## Additional Features

[Additional Features](https://synfron.github.io/ReshaperForBurp/Features.html)

## Development

### Build JAR with IntelliJ

1. Open IntelliJ.
2. Create a new project (Gradle) from existing source using Java 21.
3. Once the project is created/open, wait for IntelliJ to process Gradle dependencies.
4. Run the `jar` Gradle build task under the `extension` module from the Gradle tool window/sidebar. The JAR will be placed in the `extension/build/libs` directory.

### Build JAR with CLI

1. Install Java 21.
2. Install Gradle v8.6.
3. Open a terminal into the `extension` directory of the project.
4. Run the `gradle --refresh-dependencies build jar` command.
5. The JAR will be placed in the `extension/build/libs` directory.

### Debugging

#### IntelliJ

1. Set the environment variable `BURP_JAR_PATH` to the `burpsuite_community.jar` file location. (e.g. `C:\Users\<user>\AppData\Local\Programs\BurpSuiteCommunity\burpsuite_community.jar` on Windows)
2. In Reshaper, using the Settings tab, export all Rules and global variables to a JSON file to prevent data loss.
3. In Extender, unload the Reshaper extension from Burp Suite if you already have the extension installed from the BApp Store or from a JAR.
4. Close Burp Suite.
5. Open the Reshaper project in IntelliJ.
6. Navigate to `debug/src/main/java/synfron/reshaper/burp/debug/Burp.java`.
7. Right-click the file in the Project view and click `Run Burp.main()` or `Debug Burp.main()`.
8. Burp Suite will open with Reshaper loaded as an legacy extension.

## Contributions

Contributions are encouraged. Issues and Pull Requests welcome. Also help us spread the word.

Primary Developer: Daquanne Dwight

## Documentation Links

[Website](https://synfron.github.io/ReshaperForBurp/)

[Wiki](https://github.com/synfron/ReshaperForBurp.docs/wiki)

## Support

For help with how to use Reshaper for a particular need, to report a bug, or to make a suggestion, create an issue in GitHub or email support[at]synfron.com.

## License

MIT License. See [LICENSE](https://github.com/synfron/ReshaperForBurp/blob/master/LICENSE)

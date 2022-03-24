# Reshaper for Burp

Extension for Burp Suite to trigger actions and reshape HTTP request and response traffic using configurable rules

![Screenshot](https://user-images.githubusercontent.com/48854453/116795270-c797c100-aaa1-11eb-8353-580f7cf6e6d1.png)

[Example Usage](https://synfron.github.io/ReshaperForBurp/Examples.html)

## Rules

Rules allow you to set actions to perform (called Thens) if HTTP messages/connections (event) received by Burp Suite meet certain criteria (called Whens). Rules are processed in order.

[More](https://synfron.github.io/ReshaperForBurp/Rules.html)

### Whens

Event Direction - If the HTTP message is a Request or Response

Has Entity - If the HTTP event contains a certain entity

Matches Text - If a value (text, variable, or HTTP event entity) matches a value

Content Type - If the HTTP request body is reported to match specified content types

MIME Type - If the HTTP response body is reported to match specified MIME types

Proxy Name - If received by a certain Burp proxy listener

From Tool - If the HTTP message is from a specific Burp tool

In Scope - If the URL is in the suite-wide scope

[More](https://synfron.github.io/ReshaperForBurp/Rules.html#whens)

### Thens

Break - Stop rules or then action processing

Delay - Delay further processing/sending of the HTTP event

Log - Log message to the Burp extension console

Highlight - Highlight the request/response line in the HTTP history

Comment - Add a comment to the request/response line in the HTTP history

Prompt - Get text via a prompt dialog

Run Rules - Run a specific rule or all auto-run rules

Run Script - Execute a JavaScript script

Evaluate - Perform operations on values

Set Event Direction - Change whether to send a request or to send a response at the end of processing

Set Encoding - Set the encoding used to read and write bytes of the HTTP request or response body

Set Value - Set the value of an HTTP event using another value (text, variable, or HTTP event entity)

Delete Value - Remove an HTTP message entity

Set Variable - Set a variable using another value (text, variable, or HTTP event entity)

Delete Variable - Delete a variable

Save File - Save text to a file

Send To - Send data to other Burp tools or the system default browser

Run Process - Execute a command in a separate process

Build HTTP Message - Build an HTTP request or response message and store the full text in a variable

Parse HTTP Message - Extract values from an HTTP request or response message and store the values in variable

Send Request - Send a separate HTTP request

Drop - Have Burp drop the connection

[More](https://synfron.github.io/ReshaperForBurp/Rules.html#thens)

## Variables

Share values across different rules while processing the same event or all events.

[More](https://synfron.github.io/ReshaperForBurp/Variables.html)

## Development

### Build JAR with IntelliJ

1. Open IntelliJ.
2. Create a new project (Gradle) from existing source using Java 15.
3. Once the project is created/open, wait for IntelliJ to process Gradle dependencies.
4. Run the `jar` Gradle build task from the Gradle tool window/sidebar. The JAR will be placed in the `build\libs` directory.

### Build JAR with CLI

1. Install Java 15.
2. Install Gradle v6.8.
3. Run the `gradle --refresh-dependencies build` command.
4. Run the `gradle build jar` command. The JAR will be placed in the `build\libs` directory.

### Debugging

#### IntelliJ

1. Apply this [git patch](https://gist.github.com/ddwightx/6965732339bdf4cd022d550f40a9e99f) to the project to allow Reshaper to be debugged as a legacy extension in Burp Suite.
2. In Reshaper, using the Settings tab, export all rules and global variables to a JSON file to prevent data loss.
3. In Extender, unload the Reshaper extension from Burp Suite if you already have the extension installed from the BApp Store or from a JAR.
4. Close Burp Suite.
5. Open the Reshaper project in IntelliJ.
6. Navigate to `java/synfron/reshaper/burp/ui/Window.java`.
7. Right click the file in the Project view and click `Run Window.main()` or `Run Window.main()`.
8. Burp Suite will open with Reshaper loaded as an legacy extension.

#### CLI

1. Apply this [git patch](https://gist.github.com/ddwightx/6965732339bdf4cd022d550f40a9e99f) to the project to allow Reshaper to be debugged as a legacy extension in Burp Suite.
2. In Reshaper, using the Settings tab, export all rules and global variables to a JSON file to prevent data loss.
3. In Extender, unload the Reshaper extension from Burp Suite if you already have the extension installed from the BApp Store or from a JAR.
4. Close Burp Suite.
5. In a CLI, execute `java -cp path/to/the/reshaper-for-burp/JAR/file.jar synfron.reshaper.burp.ui.Window`.
6. Burp Suite will open with Reshaper loaded as an legacy extension.

## Contributions

Contributions are encouraged. Issues and Pull Requests welcome. Also help us spread the word.

Primary Developer: Daquanne Dwight

## Support

For help with how to use Reshaper for a particular need, to report a bug, or to make a suggestion, create an issue in GitHub or email support[at]synfron.com.

## License

MIT License. See [LICENSE](https://github.com/synfron/ReshaperForBurp/blob/master/LICENSE)

# Reshaper for Burp
Extension for Burp Suite to trigger actions and manipulate HTTP request and response traffic using configurable rules

![Screenshot](https://user-images.githubusercontent.com/48854453/101978373-6b332280-3c22-11eb-806b-ef9b6c72a060.png)

## Rules

Rules allow you to set actions to perform (called Thens) if HTTP messages/connections (event) received by Burp Suite meet certain criteria (called Whens). Rules are processed in order.

[More](https://synfron.github.io/ReshaperForBurp/Rules.html)

### Whens

Event Direction - If the HTTP message is a Request or Response

Has Entity - If the HTTP event contains a certain entity

Matches Text - If a value (text, variable, or HTTP event entity) matches a value

Proxy Name - If received by a certain Burp proxy listener

[More](https://synfron.github.io/ReshaperForBurp/Rules.html#Whens)

### Thens

Break - Stop rules or then action processing

Delay - Delay further processing/sending of the HTTP event

Log - Log message to the Burp extension console

Highlight - Highlight the request/response line in the HTTP history

Run Rules - Run a specific rule or all auto-run rules.

Run Script - Execute a JavaScript script

Set Event Direction - Change whether to send a request or to send a response at the end of processing

Set Value - Set the value of an HTTP event using another value (text, variable, or HTTP event entity)

Delete Value - Remove an HTTP message entity

Set Variable - Set a variable using another value (text, variable, or HTTP event entity)

Delete Variable - Delete a variable

Drop - Have Burp drop the connection

[More](https://synfron.github.io/ReshaperForBurp/Rules.html#Thens)

## Variables

Share values across different rules while processing the same event or all events.

[More](https://synfron.github.io/ReshaperForBurp/Variables.html)

## Contributions
Contributions are encouraged. Issues and Pull Requests welcome. Also help us spread the word.

Primary Developer: Daquanne Dwight

## License
MIT License. See [LICENSE](https://github.com/synfron/ReshaperForBurp/blob/master/LICENSE)

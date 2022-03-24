# Variables

## Custom Variables

Custom variables allow the sharing of values between Rules and are scoped at the Global or Event level. Global and Event variables can be set by Thens or in the UI (Global variables only). Custom variables are only accessible within Reshaper and are readable by Whens and Thens.

Event variables are shared among rules running on a single HTTP event (either request or response).

Global variables are shared among multiple HTTP events for as long at the extension is loaded or until the variables are deleted. Global variables can be set to be Persistent in the Global Variables tab of Reshaper. Persistent variables will be saved and reloaded between Reshaper sessions.

## Accessor Variables

Accessor variables provide access to utility functionality and data that is not strictly created by Reshaper.

Message variables provide access to event message values (e.g. Request URI).

File variables provide access to the contents of a text file.

Special character variables provide access to special characters which typically require escape character sequences to use. (e.g. new lines, tabs, unicode characters)

## Variable Tags

Variables can be read by Whens and Thens when a variable tag is specified in supporting text fields. Variable tags can be typed in manually or inserted via the right-click menu for those text fields.
{% raw %}

**Event Variable Tag (event, e):** `{{event:MyVariableName}}`

**Global Variable Tag (global, g):** `{{global:MyVariableName}}`

For example, if Global variable named `firstName` has the value `John` and variable named `lastName` has the value `Smith`. A field with the value `{{global:firstName}}'s full name is {{global:firstName}} {{global:lastName}}` will be read as `John's full name is John Smith`.

**Message Variable Tag (message, m):** `{{message:messageValueKey}}` or `{{message:messageValueKey:identifier}}` (e.g. `{{message:httprequesturi}}`, `{{message:httprequestheader:Host}}`). See [Message Values](MessageValues.html#)

**File Variable Tag (file, f):** `{{file:encoding:filePath}}`. Example: `{{file:utf-8:~/Documents/file.txt}}`

**Special Character Tag (special, s):** `{{s:specialCharacterSequences}}`. Examples: `{{s:n}}` (new line), `{{s:rn}}` (carriage return + new line), `{{s:u00A9}}` (Copyright symbol)

**Cookie Jar Tag (cookiejar, cj):** `{{cookiejar:domain:name}}` or `{{cookiejar:domain:name:path}}`. Example: `{{cookiejar:example.com:tracker:/}}`


{% endraw %}

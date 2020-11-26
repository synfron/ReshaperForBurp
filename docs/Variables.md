# Variables

Variables allow the sharing of values between Rules. Variables have scope at the Global or Event level. Variables can be set by Thens or in the UI (Global variables only), and can be read by Whens and Thens.

Event variables are shared among rules running on a single HTTP event (request or response).

Global variables are shared among multiple HTTP events for as long at the extension is loaded or until the variables are deleted. Global variables cam be set to be Persistent in the Variables tab of Reshaper. Persistent variables will be saved and reloaded between Reshaper sessions.

## Variable Tags

Variables can be read by Whens and Thens when a variable tag is specified in supporting text fields.
{% raw %}
Event Variable Tag: `{{event:MyVariableName}}`

Global Variable Tag: `{{global:MyVariableName}}`

For example, if Global variable named `firstName` has the value `John` and variable named `lastName` has the value `Smith`. A field with the value `{{global:firstName}}'s full name is {{global:firstName}} {{global:lastName}}` will be read as `John's full name is John Smith`.
{% endraw %}

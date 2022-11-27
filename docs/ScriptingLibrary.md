# Scripting Library

Extra Javascript library APIs usable in `ThenRunScript` scripts.

* auto-gen TOC:
{:toc}

## Reshaper.

### event.

#### getMessageValue(messageValueKey, messageValueIdentifier)

Get a [Message Value](MessageValues.html) from the current event.

Parameters:

messageValueKey - Key of the [Message Value](MessageValues.html).

messageValueIdentifier - The identifier for the [Message Value](MessageValues.html) if applicable. 

#### setMessageValue(messageValueKey, messageValueIdentifier, value)

Set a [Message Value](MessageValues.html) of the current event.

Parameters:

messageValueKey - Key of the [Message Value](MessageValues.html).

messageValueIdentifier - The identifier for the [Message Value](MessageValues.html) if applicable.

value - The new value.

#### getMessageValueKeys()

Get all [Message Value](MessageValues.html) keys.

#### setRuleResponse(ruleResponse)

Set whether further processing of Thens or Rules should continue after this script finishes executing. This provides the same functionality as Then Break.

Continue - Continue processing as normal.</br>
BreakThens - Skip running any further Thens of the current Rule.</br>
BreakRules - Skip running any further Thens and Rules for this event.

Parameters:

ruleResponse - "Continue" | "BreakThens" | "BreakRules"

#### runThen(thenType, thenData)

Run a Then action.

Parameters:

thenType - BuildHttpMessage, Break, DeleteValue, DeleteVariable, Drop, Highlight, Log, ParseHttpMessage, SendRequest, SendTo, SetEventDirection, SetValue, or SetVariable

thenData - Object containing the properties for the Then action. See below.

BuildHttpMessage
```
{
    dataDirection: "Request" | "Response"
    starterHttpMessage: string
    messageValueSetters: [{
        destinationMessageValue: string
        destinationIdentifier: string
        sourceText: string
    }]
    destinationVariableSource: "Global" | "Event"
    destinationVariableName: string
}
```
Delete Value
```
{
    messageValue: MessageValueKey
    identifier: string
}
```
Delete Variable
```
{
    targetSource: "Global" | "Event"
    variableName: string
}
```
Drop
```
{
    dropMessage: boolean
}
```
Highlight
```
{
    color: "None" | "Red" | "Orange" | "Yellow" | "Green" | "Cyan" | "Blue" | "Pink" | "Magenta" | "Gray"
}
```
Comment
```
{
    text: string
}
```
Log
```
{
    text: string
}
```
ParseHttpMessage
```
{
    dataDirection: "Request" | "Response"
    httpMessage: string
    messageValueGetters: [{
        sourceMessageValue: MessageValueKey
        sourceIdentifier: string
        destinationVariableSource: "Global" | "Event"
        destinationVariableName: string
    }]
}
```
SendRequest
```
{
    protocol: "http" | "https"
    address: string
    port: number
    request: string
    waitForCompletion: boolean
    failAfter: number
    failOnErrorStatusCode: boolean
    breakAfterFailure: boolean
    captureOutput: boolean
    captureAfterFailure: boolean
    captureVariableSource: "Global" | "Event"
    captureVariableName: string
}
```
SendTo
```
{
    sendTo: "Comparer" | "Intruder" | "Repeater" | "Browser"
    overrideDefaults: boolean
    host: string
    port: number
    protocol: "http" | "https"
    request: string
    value: string
    url: string
}
```
SetEventDirection
```
{
    dataDirection: "Request" | "Response"
}
```
SetValue
```
{
    text: string
    useMessageValue: boolean
    sourceMessageValue: MessageValueKey
    sourceIdentifier: string
    sourceMessageValueType: "Text" | "JSON" | "XML"
    sourceMessageValuePath: string
    useReplace: boolean
    regexPattern: string
    replacementText: string
    destinationMessageValueType: "Text" | "JSON" | "XML"
    destinationMessageValuePath: string
    destinationMessageValue: MessageValueKey
    destinationIdentifier: string
}
```
SetVariable
```
{
    text: string
    useMessageValue: boolean
    sourceMessageValue: MessageValueKey
    sourceIdentifier: string
    sourceMessageValueType: "Text" | "JSON" | "XML"
    sourceMessageValuePath: string
    useReplace: boolean
    regexPattern: string
    replacementText: string
    destinationMessageValueType: "Text" | "JSON" | "XML"
    destinationMessageValuePath: string
    targetSource: "Global" | "Event"
    variableName: string
}
```

### variables.

#### deleteEventVariable(name)

Delete an event variable.

Parameters:

name - Variable name.

#### deleteGlobalVariable(name)

Delete an global variable.

Parameters:

name - Variable name.

#### getEventVariable(name)

Get the value of an event variable.

Parameters:

name - Variable name.

#### getGlobalVariable(name)

Get the value of a global variable.

Parameters:

name - Variable name.

#### setEventVariable(name, value)

Set the value of an event variable.

Parameters:

name - Variable name.

value - The new value.

#### setGlobalVariable(name, value)

Set the value of a global variable.

Parameters:

name - Variable name.

value - The new value.

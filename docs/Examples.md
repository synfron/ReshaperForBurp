# Example Usage

- [Use a value from one HTTP message in a following HTTP message](#tip1)
- [Redirect a request to a different server](#tip2)
- [Change a value in a returned response](#tip3)
- [Auto-respond to requests without first sending a request to an external server (response mocking)](#tip4)
- [Drop a request so it is not sent to an external server (Works on all supported tools: Proxy, Repeater, Intruder, Scanner, Spider, Target, Extender (other extensions))](#tip5)
- [Share or backup/restore Global Variables and Rules](#tip6)

<a href="#" id="tip1"></a>
**Use a value from one HTTP message in a following HTTP message:**

In the example below, we are taking the `Authorization` header from `www.example.org` requests, storing it in a variable, and setting it on `www.example.com` requests.

1. Open the `Rules` tab.
2. Create a Rule.
3. Set the `Rule Name` to `Get www.example.org Authorization`.
4. Add When -> `Event Direction` (if not already added).
5. Change `Event Direction` to `Request`.
6. Add When -> `Matches Text`.
7. Change `Message Value` to `Destination Address`.
8. Set `Match Text` to `www.example.org`.
9. Add Then -> `Set Variable`.
10. Change `Source Message Value` to `Request Header`.
11. Set `Source Identifier` to `Authorization`.
12. Set `Destination Variable Name` to `exampleAuth`.
13. At the bottom right of the window, check `Enabled` and click `Save`.
14. Create another Rule.
15. Set the `Rule Name` to `Set www.example.com Authorization`.
16. Add When -> `Event Direction` (if not already added).
17. Change `Event Direction` to `Request`.
18. Add When -> `Matches Text`.
19. Change `Message Value` to `Destination Address`.
20. Set `Match Text` to `www.example.com`.
21. Add Then -> `Set Value`.
22. {% raw %}Set `Text` to `{{global:exampleAuth}}`.{% endraw %}
23. Change `Destination Message Value` to `Request Header`.
24. Set `Destination Identifier` to `Authorization`.
25. At the bottom right of the window, check `Enabled` and click `Save`.

<a href="#" id="tip2"></a>
**Redirect a request to a different server:**

In the example below, we are redirecting from `www.example.org` to `www.example.com` by setting the URL and using a message variable to make sure we keep the page path.

1. Open the `Rules` tab.
2. Create or open a Rule.
3. Set a `Rule Name` and add other Whens and Thens as needed.
4. Add When -> `Event Direction` (if not already added).
5. Change `Event Direction` to `Request`.
6. Add Then -> `Set Value`.
7. Set `Text` to `https://www.example.com{{message:httprequesturi}}`.
8. Change `Destination Message Value` to `URL`.
9. At the bottom right of the window, check `Enabled` and click `Save`.


<a href="#" id="tip3"></a>
**Change a value in a returned response:**

In the example below, we tell the browser to allow any origin by overriding the `Access-Control-Allow-Origin` response header.

1. Open the `Rules` tab.
2. Create or open a Rule.
3. Set a `Rule Name` and add other Whens and Thens as needed.
4. Add When -> `Event Direction` (if not already added).
5. Change `Event Direction` to `Response`.
6. Add Then -> `Set Value`.
7. Set `Text` to `*`.
8. Change `Destination Message Value` to `Request Header`.
9. Set `Destination Identifier` to `Access-Control-Allow-Origin`.
10. At the bottom right of the window, check `Enabled` and click `Save`.


<a href="#" id="tip4"></a>
**Auto-respond to requests without first sending a request to an external server (response mocking):**

1. Open the `Global Variables` tab.
2. Add a new Variable.
3. Set a `Variable Name`. We will use `mockResponse` for this example.
4. Paste the full response text (including headers and body) in `Variable Text`.
4. Open the `Rules` tab.
5. Create or open a Rule.
6. Set a `Rule Name` and add other Whens and Thens as needed.
7. Add When -> `Event Direction` (if not already added).
8. Change `Event Direction` to `Request`.
9. Add Then -> `Set Event Direction`.
10. Change `Event Direction` to `Response`.
11. Add Then -> `Set Value`.
12. {% raw %}Set `Text` to `{{global:mockResponse}}`.{% endraw %}
13. Change `Destination Message Value` to `Response Message`.
14. At the bottom right of the window, check `Enabled` and click `Save`.

<a href="#" id="tip5"></a>
**Drop a request so that it is not sent to an external server:**

The example of drop requests below works on all supported tools: Proxy, Repeater, Intruder, Scanner, Spider, Target, Extender (other extensions).

1. Open the `Settings` tab.
2. Ensure the tool is selected under `Capture Traffic From:`
3. Open the `Rules` tab.
4. Create or open a Rule.
5. Set a `Rule Name` and add other Whens and Thens as needed.
6. Add When -> `Event Direction` (if not already added).
7. Change `Event Direction` to `Request`.
8. (Optional) If requests are being received from multiple tools:
    1. Add When -> `From Tool`.
    2. Set `Tool` to the tool the request will come from.
8. Add Then -> `Drop`.
9. Check `Drop Message`.
10. At the bottom right of the window, check `Enabled` and click `Save`.

<a href="#" id="tip6"></a>
**Share or backup/restore Global Variables and Rules:**

Export the relevant items:
1. Open the `Global Variables` tab.
2. Ensure relevant all variables are checked as `Persistent` and saved.
3. Open the `Settings` tab.
4. In the `Export` section, click `Refresh Lists`.
5. Check the checkboxes next to the names of relevant Rules and Global Variables that you want to export. Uncheck any others.
6. Click `Export Data`.
7. Choose a save location and name for the export data file.

Import the exported data file:
1. Open the `Settings` tab.
2. (Optional) In the `Import` section, Check `Override Duplicates` to overwrite any Rules or Global Variables that have the same name as those to be imported.
3. Click `Import Data`.
4. Navigate to the location of the export data file, select it, and click `Open`.


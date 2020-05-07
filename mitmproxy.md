# mitmproxy for understanding systems of services

mitmproxy is a program that has a UI that allows a developer to look through the requests made to an API.
The UI has request and response details.

I found it very useful while trying to understand how the mobile app, keyshare server and irmago server interacted with each other.

An example usage: 

`mitmproxy -p 18088 --mode reverse:http://localhost:8088/`

This opens a server listening on port `18088` and forwarding requests to `http://localhost:8088/` and shows the UI for 
browsing request/responses.

I found the UI a bit strange to move around in. Pressing `?` will bring up the keybindings. `q` goes back to the view 
that spawned the current view. Mouse selection of text is disabled. Pressing `v` while viewing a request/response opens 
a view that allows mouse selection (and copying) of text.

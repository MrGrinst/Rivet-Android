## Rivet for Android

This is the Android code for Rivet as it appeared the day we closed it down. To keep things simple, I'm not committing the full Git history.

A couple things I'd like to point out:

* This code is not well-documented, so it may be hard to follow
* If you want to run the app you'll have to do the following:
	* Disable Fabric (recommended) or put a valid Fabric API secret into the app/fabric.properties file
	* Hard-code responses for registration and location-related server calls if you want to get past the initial screen
	* Run the developmentDebug build variant
# MyGlucoseAndroid
An Android-based glucose tracker application which syncs with a remote server.

- Meant to be used in conjunction with the [.NET Core web server application](https://github.com/SugarCubes/MyGlucoseDotNetCore).

## Setup
- After installing the application, you will be presented with the Login view.
- Before you can log in, you will need to set up the local server (the .NET Core backend application linked to above)
  - Press the back button
  - Select the menu button in the upper-right hand corner of the application
  - Select "Settings..."
  - Under Developer Settings, change the hostname to the IP address of the system running the .NET Core application
    - In Windows, open the Command Prompt window by pressing [Win] + R, then typing "cmd" in the input box
    - At the command prompt, type "ipconfig"
    - Find the internet adapter you are currently using (Ethernet or Wireless LAN adapter)
    - Your IP address will bt listed as "IPv4 Address"
  - Change the port under Developer Settings to the port used by your .NET Core application
    - In the .NET Core project folder, navigate to **.vs > config > applicationhost.config**
    - Open the applicationhost.config file in Notepad, or other text editor
    - Find the lines:

```
<site...>
	<bindings>
		<binding protocol="http" bindingInformation="*:**12345**:localhost" />
		<binding protocol="https" bindingInformation="*:54321:localhost" />
	</bindings>
</site>
```

    - The port number is the first set of numbers in "**XXXXX**:localhost". Enter this into MyGlucose
    - Now that the Android app is set up, you need to follow the directions [on this page](https://github.com/SugarCubes/MyGlucoseDotNetCore)
    - Create a user account in the .NET Core application. This creates the login information to be entered in the Android app
    - Once these directions have been followed and the .NET Core application is running in Administrator mode, you can log in and use the Android app
    - In the final product, you will be able to log all entries in Android. These will be transmitted to the database on the webserver, where a physician would theoretically be able to log in and view your patient information and make more educated decisions related to your health care.
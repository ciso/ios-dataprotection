iOS Dataprotection Viewer
=========================

Introduction
------------

This application has been developed by Christof Stromberger and Peter Teufl 
at the Institute for Applied Information Processing and Communications 
at the Graz University of Technology.

Supported Devices
----------------
This application should work on OS X and Windows machines where iTunes is 
installed and at least one backup has been made of an iOS device.

We have tested it with backups from **iPhone 4S**, **iPad 2** 
and **iPad 3**. All devices had at least iOS 5.1.

## Download Executable jar File
**Click [here](https://github.com/downloads/ciso/ios-dataprotection/dataprotection.jar) to download an executable jar file hosted on GitHub.**

## Usage
You can clone this project using git and compile it yourself or simply download 
the executable from [this link](https://github.com/downloads/ciso/ios-dataprotection/dataprotection.jar).

After successfully downloading the binary you have to use your command window or terminal to 
start this application.

Switch into the folder where you have stored the binary. (For example, I have stored it into my downloads folder)
> cd ~/Downloads

Then execute the jar file.
> java -jar ./dataprotection.jar

Then you should see something like that:
> Backup path: /Users/christofstromberger/Library/Application Support/MobileSync/Backup/
> [1] IAIK iPhone 4S (28.08.2012 09:00)
> [2] IAIK iPad Dev #1 (23.07.2012 12:57)
> [3] IAIK iPhone (28.08.2012 09:16)
> [4] iPad von Christof (20.11.2011 23:13)
> [5] Christof's iPhone (29.08.2012 13:50)
> Choose a backup: 

Now you have to choose your backup which you want to analyze.
> Choose a backup: 5

Then the application asks you if you want to store the results on your desktop. (Mac OS X location)
> Shall we store it on your desktop? If not, provide a path.
> '> 

When you just hit enter, the application will store it on your desktop.
> Okay, we will store it to /Users/christofstromberger/Desktop/analysis.csv
> Extracting and decrypting your backup
> Creating output file in csv format
> 3582/3582 Files extracted
> Finished

Finally, you should have an **analysis.csv** file on your desktop.

## Data Protection Classes

### Complete Protection
The so-called class key is derived from the user's passcode and the unique device identifier. 
Immediately after the user locks the device, the class key is discarded. 
This means that all files in this class become inaccessible until the 
user unlocks the device again. For example, Apple's Mail App that 
protects all messages and the corresponding attachments using 
Complete Protection uses this class.

### Protect Unless Open
This protection class is used when a file needs to be written while 
the device is locked by a passcode. This is commonly used when something 
is processed in the background. For example, when a mail attachment is downloaded 
in the background. The protection class uses asymmetric elliptic curve cryptography 
to achieve this. Additionally, with the usual per-file key, Data Protection generates 
a unique public-private key pair. Then a so- called shared secret is created using the 
file's key and the public key of the particular protection class. The user’s passcode 
and device identifier protect the private key of the class. In fact, the per-file 
key and a unique hash of the aforementioned shared secret is stored in the file’s 
metadata along with the public key. Both, the corresponding private key and the 
per-file key are wiped from the memory when the file is closed. When the user 
wants to open the file again, the shared secret is re- created using the same 
private key. The hash of it is then used to unwrap the per-file key, which 
in turn is used to decrypt the file.

### Protect Until First User Authentication
Basically, this class has the same behavior as 
the **Complete Protection class**, except that the class key is not wiped 
from memory when the device is locked. The benefit of this class is that 
the file remains encrypted even when the device is rebooted.

### No Protection
When this class is used, 
the file is only protected using the device's unique identifier. 
Additionally, it is stored in the effaceable storage section of the memory. 
This is the default class for all files that were not specified with any 
of the aforementioned protection classes. Due to the fact that the entire 
file system of iOS is encrypted, all files that have No Protection class 
are also encrypted. One problem is that the decryption key is stored on 
the device itself. Otherwise, when the user performs a wipe this file 
becomes inaccessible very fast because of the fact that the decryption 
key is stored in the effaceable storage section.

License
------------
...
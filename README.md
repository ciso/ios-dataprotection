iOS Dataprotection Viewer
=========================

Introduction
------------

This application has been developed by Christof Stromberger and Peter Teufl 
at the [Institute for Applied Information Processing and Communications](http://www.iaik.at) 
at the Graz University of Technology.

###iOS Encryption
iOS uses two encryption systems that protect your data (for detailed information lookup the references below):
* **Device encryption**: This system encrypts the whole iOS-filesystem (via keys that are derived from the AES key stored on a chip) and is not linked to your passcode. Thus, by jailbreaking the device an attacker gains access to the filesystem.
* **Data Protection**: This system was introduced in iOS4 and protects files via keys that are protected themselves via so called class keys, which are derived from the **passcode** and the **AES key** which is stored on the chip. The application developer must manually specify the protection class for each file the application uses. An attacker who steals an iPhone can still apply an jailbreak but he won't be able to access files that are encrypted via the Data Protection method. In order to do so, he must know the passcode or mount a brute-force attack on the passcode. Since, both the passcode and the AES key on the chip are used to derive the keys, the attacker's brute-force attack is tied to the iOS-device, which slows down the brute-force attack (roughly 20 minutes on an iPhone4 for the 10000 possible combinations of a 4-digit numerical PIN code, roughly 8 years for a 6-character alphanumeric passcode, more details can be found within this [security analysis](http://www.trailofbits.com/resources/ios4_security_evaluation_slides.pdf)).

Thus, DataProtection - when a good passcode is chosen - offers a pretty secure way to protect the data on your iOS-device. Unfortunately, the user (or administrator) does not see whether an application uses the appropriate classes for its files. For security relevant applications this might lead to the scenarios, which is explained in the following section.

### Why is it important to know the protection class of a particular file?

By chosing an adequate passcode, DataProtection offers good protection. Unfortunately, this protection depends on the application developer, who decides which files need to be protected. Let's consider you have received some important files by email. The pre-installed mail application of Apple secures your received mails and attachment via adequate data protection settings. However, when the attachments are opened in another application (e.g. a PDF reader), which might store the files in its own directory, the protection completely depends on the data protection choices the app developer has made. If no protection class was chosen, then the files are only encrypted with the device encryption system, which can be attacked by jailbreaking the device. Thus, the lose the high protection class of the mail application. The user/administrator is not able to recognize this.

This is why it is important to know which protection classes are used by 
your installed applications. In order to allow the user or the system administrator to determine which protection classes are used for each application, the presented tool was created.

Relevant references:
* Apple developer documentation: [iOS Security](http://images.apple.com/ipad/business/docs/iOS_Security_May12.pdf)
* Jean-Baptiste Bedru, Jean Sigwald: [iPhone data protection in depth](http://esec-lab.sogeti.com/dotclear/public/publications/11-hitbamsterdam-iphonedataprotection.pdf)
* Elcomsoft: [Elcomsoft iOS Forensic Toolkot](http://www.elcomsoft.co.uk/eift.html)
* Andrey Belenko, Dmitry Sklyarov: [Evolution of iOS Data Protection and iPhone Forensics:
from iPhone OS to iOS 5](https://media.blackhat.com/bh-ad-11/Belenko/bh-ad-11-Belenko-iOS_Data_Protection.pdf)
* Trail of Bits - Dino A. Dai Zovi: 
  * Paper: [Apple iOS 4 Security Evaluation](http://www.trailofbits.com/resources/ios4_security_evaluation_paper.pdf)
  * Slides: [Apple iOS 4 Security Evaluation](http://www.trailofbits.com/resources/ios4_security_evaluation_slides.pdf)

DataProtection tool
----------------
The idea of the dataprotection tool is simple: iIOS backups which are carried out by iTunes store meta-information that allows to extract the protection classes for the files within the backup. Thus, the tool analyzes an iTunes backup, extracts the protection classes and stores the extracted information in a simple csv file. This allows the user, (or in most cases the administrator) to analyze whether an application uses adequate protection classes.
The tool is based on prior work (see the end of this document), which was modified to achieve the desired goals.


###Usage and Supported Devices
This application should work on OS X and Windows machines where iTunes is 
installed and at least one backup has been made of an iOS device.

We have tested it with backups from **iPhone 4S**, **iPad 2** 
and **iPad 3**. All devices had at least iOS 5.1.

## Download Executable jar File
**Click [here](https://github.com/downloads/ciso/ios-dataprotection/dataprotection.jar) to download an executable jar file hosted on GitHub.**

## Building
You can build this project by simply invoking `ant` in the root directory of the project. The default is to build a `.jar` file.

```
% ant
Buildfile: ios-dataprotection/build.xml

makedir:
    [mkdir] Created dir: ios-dataprotection/build/classes

compile:
    [javac] Compiling 3 source files to ios-dataprotection/build/classes

jar:
     [echo] The .jar file can be found in 'build' afterwards
      [jar] Building jar: ios-dataprotection/build/ios-dataprotection.jar

BUILD SUCCESSFUL
Total time: 1 second
```

### Proguard
If you want a smaller `.jar` file there is also the option to shrink the `.jar` file. You can do this by invoking Proguard like this:

```
% proguard @ios-dataprotection.pro
ProGuard, version 4.8
Reading program jar [ios-dataprotection/build/ios-dataprotection.jar]
Reading library jar [/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/classes.jar]
Preparing output jar [ios-dataprotection/build/ios-dataprotection_out.jar]
  Copying resources from program jar [ios-dataprotection/build/ios-dataprotection.jar]
proguard @ios-dataprotection.pro  13.98s user 0.27s system 153% cpu 9.311 total
```

At the time of writing the `.jar` is **63KB** before proguard, **29KB** afterwards.

If you get this error: `Error: Can't read [/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home/lib/rt.jar] (No such file or directory)` on OS X you can fix this as shown [here](http://bruehlicke.blogspot.co.at/2009/11/missing-rtjar-mac-os-x-using-proguard.html):

> On your Mac open a terminal and change directory to
>
> `% cd /System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home/lib`
>
> now just softlink to classes.jar via
>
> `% sudo ln -s ../../Classes/classes.jar rt.jar`

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
> Backup path: /Users/christof/Library/Application Support/MobileSync/Backup/  
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
> Okay, we will store it to /Users/christof/Desktop/analysis.csv  
> Extracting and decrypting your backup  
> Creating output file in csv format  
> 3582/3582 Files extracted  
> Finished  

Finally, you should have an **analysis.csv** file on your desktop.

This file contains the following information:
* Application name (identifier)
* Names of the files an application uses
* Protection classes of the analyzed files: Here you should lookout for the class **NSProtectionNone** which indicates that the file is only protected via device encryption.


### Screenshot ###
<img src="http://cstromberger.at/screenshot_analysis.csv.png" alt="Sample Screenshot" />

## Data Protection Classes
This section gives an overview about the deployed protection classes. For detailed information look up the Apple developer documentation.

### Complete Protection
The so-called class key is derived from the user's passcode and the AES key on the chip. This key is used to protect the keys, which are used to encrypt the files. Immediately after the user locks the device, the class key is discarded. 
This means that all files in this class become inaccessible until the 
user unlocks the device again. For example, Apple's Mail App that 
protects all messages and the corresponding attachments using this protection class.

### Protect Unless Open
This class allows to keep the keys in the device memory when the device is locked until the files are closed by the application. Also, due to the deployment of assymetric cryptography, this class allows an application to create protected files, even when the device is locked, and thus the symmetric class keys are not available.

### Protect Until First User Authentication
Basically, this class has the same behavior as 
the **Complete Protection class**, except that the class key is not wiped 
from memory when the device is locked. It remains within the device memory until the device is rebooted. Although this is less secure then the complete protection class, it still offers good protection in case an attacker applies a jailbreak that requires a reboot. After this reboot the files cannot be accessed anymore without knowing the passcode.

### No Protection
While files using this class are still encrypted via the device encryption, this encryption does not depend on the passcode of the user. Thus, when an attacker applies a jailbreak to gain access to the operating system, the operating system decrypts the data and allows access to the files with this class.

Issues
-------------
There is one issue with this tool: Since only the backups are analyzed, only those applications and files can be analyzed that are marked by the app developer for backup (which is the default setting). Other files, e.g. temporary files, that are not marked for backup cannot be extracted. Thus, it might be possible that an application has additional files (e.g. temporary files) with non-secure protection classes, which are not shown by the tool.

## License
This tool is licensed under the MIT License. Some of the code was taken from the projects mentioned below. These parts are marked within the source code files.


Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## Contact
- Peter Teufl, peter.teufl@iaik.tugraz.at
- Christof Stromberger, stromberger@student.tugraz.at

## Special Thanks to
- [Property List Editor](http://sourceforge.net/projects/plist/files/lib/) on Sourceforge
- [iPhoneStalker](http://code.google.com/p/iphonestalker/) on Google code

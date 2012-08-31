/**
 * 
 */
package at.tugraz.iaik;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import net.sf.plist.NSDictionary;
import net.sf.plist.NSObject;
import net.sf.plist.io.PropertyListException;
import net.sf.plist.io.PropertyListParser;

import at.tugraz.iaik.io.MBDBReader;
import at.tugraz.iaik.io.MBDBReader.MBDBData;

/**
 * @author christofstromberger
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("(c) Stromberger 2012, IAIK Graz University of Technology");
		String username = System.getProperty("user.name");
		String pathToBackupDirectory = "";
		String backupFileName = "";
		String pathToBackup = "";
		String pathToManifestFile = "Manifest.mbdb";
		String pathToOutputFile = "/Users/" + username + "/Desktop/analysis.csv";
		boolean isOSSupported = true;
		boolean isEncrypted = true;
				
		//detecting operating system
		if (isMac()) {
			pathToBackupDirectory = "/Users/" + username + "/Library/Application Support/MobileSync/Backup/";
		} else if (isWindows()) {
			pathToBackupDirectory = "C:\\Users\\" + username + "\\AppData\\Roaming\\Apple Computer\\MobileSync\\Backup\\";
		} else if (isUnix()) {
			pathToBackupDirectory = ""; //TODO !!!
		} else {
			System.out.println("Your OS is not supported!");
			isOSSupported = false;
		}
		
		//System.out.println("Backup path: " + pathToBackupDirectory);
		
		File backupDirectory = new File(pathToBackupDirectory);
		if (backupDirectory.isDirectory()) {
			File[] files = backupDirectory.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return !name.toLowerCase().startsWith(".");
			    }
			    });
			
			int counter = 1;
			for (File backupFile : files) {
				String backupFilePath = backupFile.getPath();
				
				
			 isEncrypted = isEncryptedBackup(backupFilePath);
				String deviceName = getDeviceName(backupFilePath);
				String lastBackup = getLastUpdatedDate(backupFilePath);
				
				System.out.println("[" + counter++ + "] " + deviceName + " (" + lastBackup + ")");
				//System.out.println("[" + counter++ + "] " + deviceName + ", Backup date: " + lastUpdated + " (Encrypted: " + isEncrypted + ") " + backupFile.getName());
			}
			
			Scanner in = new Scanner(System.in);
			System.out.print("Choose a backup: ");
		    String input = in.nextLine();
		    int chosenIndex = Integer.parseInt(input)-1;
		    
		    if (chosenIndex > files.length) {
				System.out.print("Input not valid! Please start the application again!");
				return;
		    }
		    
		    pathToBackup = files[chosenIndex].getAbsolutePath();
		}
		
		/*Scanner inputScanner = new Scanner(System.in);
		System.out.println("Shall we store it on your desktop? If not, provide a path.");
		System.out.print("> ");
	    String input = inputScanner.nextLine();
	    System.out.println("debug: " + input);
	    if (input.isEmpty() || input == "\n" || input.toLowerCase() == "yes" || input.toLowerCase() == "y") {
	    	//continue
	    }
	    else {
	    	pathToOutputFile = input;
	    }*/
	    System.out.println("Okay, we will store it to " + pathToOutputFile);
		
		//check if the os is supported
		if (!isOSSupported) {
			System.out.println("Error: Your OS is not supported. We support only Mac, Windows and Linux.");
			return;
		}
		
		//check if the backup is encrypted
		if (isEncrypted) {
			//System.out.println("Error: Your backup is encrypted. Please uncheck 'Encrypt local backup' in iTunes and try again.");
			//return;
			
			System.out.println("Your backup is encrypted. We are going to decrypt it temporary.");
		}

		File manifestFile = new File(pathToBackup + "/" + pathToManifestFile);

		// allocate mbdb reader
		MBDBReader reader = new MBDBReader();
		
		System.out.println("Extracting and decrypting your backup");
		boolean retVal = reader.processMbdb(manifestFile);
		if (!retVal) {
			System.out.println("Error: Could not extract or decrypt your backup.");
			return;
		}
		
		try {
			// Create file
			FileWriter fstream = new FileWriter(pathToOutputFile);
			BufferedWriter out = new BufferedWriter(fstream);

			// adding headlines to csv
			out.write("APP/DOMAIN, PROTECTION CLASS,VERBOSE PROTECTION CLASS,FILE,FILE_SIZE\n");

			System.out.println("Creating output file in csv format");
			int counter = 0;
			System.out.print("" + counter + "/" + reader.mbdbList.size() + " Files extracted");
			for (MBDBData data : reader.mbdbList) {
				if (data.fileLength > 0) {
					String appDomain = "AppDomain-";
					String domain = data.domain.toString();

					// checking if extracted file belongs to an application
					if (domain.startsWith(appDomain)) {
						domain = domain.substring(appDomain.length());
					} else {
						domain = "_APPLE-" + domain;
					}

					String verboseProtectionClass = protectionClassLookup(data.flag);
					
					out.write(domain + "," + data.flag + "," + verboseProtectionClass + "," + data.filename
							+ "," + data.fileLength + "\n");
				}
				
				System.out.print("\r" + ++counter + "/" + reader.mbdbList.size() + " Files extracted");
			}

			out.close();
		} catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
		}

		System.out.println("\nFinished");

	} //end of main
	
	
	public static String protectionClassLookup(int classId) {
		String ret = "UNKNOWN";
		
		switch (classId) {
		case 1:
			ret = "NSFileProtectionComplete";
			break;
		case 2: 
			ret = "NSFileProtectionWriteOnly";
			break;
		case 3:
			ret = "NSFileProtectionCompleteUntilUserAuthentication";
			break;
		case 4:
			ret = "NSProtectionNone";
			break;
		}
		
		return ret;
	}
	
	
	public static boolean isEncryptedBackup(String pathToBackup) {
		boolean ret = true;
		String manifestFileName = "Manifest.plist";
		
		File manifestFile = new File(pathToBackup + "/" + manifestFileName);
		try {
		    NSObject rootNode = PropertyListParser.parse(manifestFile);
		    
		    if (rootNode instanceof NSDictionary) {
		        Map<String, NSObject> nodeValues = ((NSDictionary)rootNode).toMap();
		        NSObject isEncryptedNode = nodeValues.get("IsEncrypted");
		        if (!isEncryptedNode.isTrue())
		        	ret = false;
		    }		
			
		} catch (PropertyListException e) {
			System.err.println("Exception: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Exception: " + e.getMessage());
		}
		
		
		return ret;
	}
	
	public static String getDeviceName(String pathToBackup) {
		String ret = "";
		String manifestFileName = "Manifest.plist";
		
		File manifestFile = new File(pathToBackup + "/" + manifestFileName);
		try {
		    NSObject rootNode = PropertyListParser.parse(manifestFile);
		    
		    if (rootNode instanceof NSDictionary) {
		        Map<String, NSObject> nodeValues = ((NSDictionary)rootNode).toMap();
		        NSObject deviceNameNode = ((NSDictionary) nodeValues.get("Lockdown")).get("DeviceName");
		        ret = deviceNameNode.toString();
		    }		
			
		} catch (PropertyListException e) {
			System.err.println("Exception: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Exception: " + e.getMessage());
		}
		return ret;
	}
	
	public static String getLastUpdatedDate(String pathToBackup) {
		String ret = "";
		String statusFileName = "Status.plist";
		
		File statusFile = new File(pathToBackup + "/" + statusFileName);
		try {
		    NSObject rootNode = PropertyListParser.parse(statusFile);
		    
		    if (rootNode instanceof NSDictionary) {
		        Map<String, NSObject> nodeValues = ((NSDictionary)rootNode).toMap();
		        NSObject lastBackupDate = nodeValues.get("Date");
		        Date backupDate = lastBackupDate.toDate();
		        SimpleDateFormat s = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		        ret = s.format(backupDate);
		    }		
			
		} catch (PropertyListException e) {
			System.err.println("Exception: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Exception: " + e.getMessage());
		}
		return ret;
	}
	
	
	public static boolean isMac() {
		 
		String os = System.getProperty("os.name").toLowerCase();
		// Mac
		return (os.indexOf("mac") >= 0);
	}
	
	public static boolean isWindows() {
		 
		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return (os.indexOf("win") >= 0); 
	}
 
	public static boolean isUnix() {
 
		String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
 
	}
}

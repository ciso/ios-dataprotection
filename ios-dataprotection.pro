## Proguard configuration for dataprotection.jar
##
## On OSX, you might have to apply this workaround, when rt.jar is "missing"
## http://bruehlicke.blogspot.co.at/2009/11/missing-rtjar-mac-os-x-using-proguard.html

-injars             build/ios-dataprotection.jar
-outjars            build/ios-dataprotection_out.jar
-libraryjars        <java.home>/lib/rt.jar
-optimizationpasses 5

## We don't want to obfuscacte, since stack traces could be useful for bugreports
-dontobfuscate

## If you want a smaller .jar file, comment out the '-dontobfuscate' line above
## and comment in these lines:
# -overloadaggressively
# -repackageclasses ''
# -allowaccessmodification

-keep public class at.tugraz.iaik.Main {
    public static void main(java.lang.String[]);
}

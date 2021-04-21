-injars       ArkIntelBot.jar
-outjars      ArkIntelBot_out.jar
-libraryjars  C:\Users\UltimateUser\.p2\pool\plugins\org.junit.jupiter.api_5.0.0.v20170910-2246.jar;C:\Program Files\Java\jre1.8.0_181\lib;C:\Users\UltimateUser\Downloads\java_ee_sdk-8\glassfish5\glassfish\lib\javaee.jar;C:\Users\UltimateUser\Downloads\java_ee_sdk-8\glassfish5\mq\lib\javax.json.jar;G:\Users\UltimateUser\Downloads\JDA-3.8.0_433-withDependencies.jar;C:\Users\UltimateUser\.gradle\caches\modules-2\files-2.1\org.apache.commons\commons-lang3\3.1\905075e6c80f206bbe6cf1e809d2caa69f420c76\commons-lang3-3.1.jar;C:\Users\UltimateUser\.gradle\caches\modules-2\files-2.1\org.apache.commons\commons-compress\1.5\d2bd2c0bd328f1dabdf33e10b6d223ebcbe93343\commons-compress-1.5.jar;C:\Users\UltimateUser\.gradle\caches\modules-2\files-2.1\org.apache.httpcomponents\httpclient\4.2.5\666e26e76f2e87d84e4f16acb546481ae1b8e9a6\httpclient-4.2.5.jar;C:\Users\UltimateUser\.gradle\caches\modules-2\files-2.1\org.apache.httpcomponents\httpcore\4.2.4\3b7f38df6de5dd8b500e602ae8c2dd5ee446f883\httpcore-4.2.4.jar;G:\Users\UltimateUser\Documents\KillZoneMrk2\mysql-connector-java-5.1.23-bin.jar;C:\Users\UltimateUser\.p2\pool\plugins\org.eclipse.jdt.ui_3.13.100.v20180330-0726.jar;G:\Users\UltimateUser\Downloads\gdx-jnigen-1-9-8\gdx-jnigen-1.9.8.jar
-printmapping ArkIntelBot.map

-keep public class arkintelbot.Main {
    public static void main(java.lang.String[]);
}

-optimizationpasses 3
-overloadaggressively
-repackageclasses ''
-allowaccessmodification
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}
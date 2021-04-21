package arkintelbot;

import java.io.File;
import java.net.URL;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamException;

public class TestMain2 {

	public static void main(String[] args) 
	{
		byte[] requestPeek = {(byte) 84,
			      (byte) 83,
			      (byte) 111,
			      (byte) 117,
			      (byte) 114,
			      (byte) 99,
			      (byte) 101,
			      (byte) 32,
			      (byte) 69,
			      (byte) 110,
			      (byte) 103,
			      (byte) 105,
			      (byte) 110,
			      (byte) 101,
			      (byte) 32,
			      (byte) 81,
			      (byte) 117,
			      (byte) 101,
			      (byte) 114,
			      (byte) 121,
			      (byte) 0};
		
		System.out.println(new String(requestPeek));
		/*
		File file = new File("C:\\Users\\UltimateUser\\AppData\\Local\\Temp\\steamworks4j\\1.8.1-SNAPSHOT\\steam_api64.dll");
		System.out.println(file.exists());
		URL url = TestMain2.class.getResource("/steam_api64.dll");
		System.out.println(url == null);
		System.out.println(TestMain2.class.getName());
		*/
		
		try 
		{
			SteamAPI.printDebugInfo(System.out);
			System.out.println(SteamAPI.isSteamRunning());
		    SteamAPI.loadLibraries();
		    
		    if (!SteamAPI.init()) 
		    {
		        // Steamworks initialization error, e.g. Steam client not running
		    	System.out.println("Failed to initialise.");
		    	
		    }
		} 
		catch (SteamException e) 
		{
		    // Error extracting or loading native libraries
			e.printStackTrace();
		}
		
	}

}

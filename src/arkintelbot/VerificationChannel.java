package arkintelbot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class VerificationChannel 
{
	private TextChannel verificationChannel;
	private static final Logger logger = LoggerFactory.getLogger(VerificationChannel.class);
	
	public VerificationChannel(TextChannel verificationChannel)
	{
		this.verificationChannel = verificationChannel;
	}
	
	public boolean IsGuildValid()
	{
		if (verificationChannel == null)
		{
			logger.error("Licensing Channel ID is not valid for current bot.");
			return false;
		}
		
		String guildId = verificationChannel.getGuild().getId();
		try 
		{
			HttpURLConnection conn = (HttpURLConnection)new URL("https://szokoz.eu/cgi-bin/AIBL.py?license="+guildId)
					.openConnection();
			InputStream is = conn.getInputStream();
			byte[] b = new byte[32];
			is.read(b);
			String res = new String(b).trim();
			logger.debug(res);
			if (res.equals("True"))
			{
				return true;
			}
			
			return false;
		}
		catch (IOException e) 
		{
			logger.error(e.getLocalizedMessage());
		}
		
		return false;
	}
	
	public boolean IsLicenseValid(String license)
	{
		if (verificationChannel == null)
		{
			logger.error("Licensing Channel ID is not valid for current bot.");
			return false;
		}
		
		List<Message> history = verificationChannel.getHistory().retrievePast(100).complete();
		Pattern licensePatt = Pattern.compile("<[A-z0-9]+>");
		Pattern licensePartPatt = Pattern.compile("[A-z0-9]+");
		for (int i = 0; i < history.size(); i++)
		{
			Message message = history.get(i);
			String content = message.getContentDisplay();
			Matcher matcher = licensePatt.matcher(content);
			while (matcher.find())
			{
				String licenseLine = matcher.group();
				Matcher matcher2 = licensePartPatt.matcher(licenseLine);
				if (matcher2.find() && matcher2.group().equals(license))
				{
					return true;
				}
			}
		}
		
		return false;
	}
}

package arkintelbot.api;

import java.util.ArrayList;

import arkintelbot.AllegianceIntelChannel;
import arkintelbot.BattleMetrics;
import arkintelbot.VerificationChannel;
import killzonebot.util.Bot;
import net.dv8tion.jda.core.entities.TextChannel;
import steambot.SteamBotJNI;

public class ArkIntelBotAPI 
{
	private String license;
	private String botToken;
	private String slaveLicensingChannelId;
	private String battlemetricsChannelId;
	private String tribeMemberInputChannelId;
	private String tribeAllyInputChannelId;
	private String tribeEnemyInputChannelId;
	private String enemyAlertsChannelId;
	private Bot bot;
	private ArrayList<LiveIntelServer> liveIntelServers = new ArrayList<LiveIntelServer>();
	private VerificationChannel slaveLicensingChannel;
	private AllegianceIntelChannel tribeMemberAllegianceChannel;
	private AllegianceIntelChannel tribeAllyAllegianceChannel;
	private AllegianceIntelChannel tribeEnemyAllegianceChannel;
	private TextChannel enemyAlertsChannel;
	private BattleMetrics battleMetrics;
	private SteamBotJNI steamBot;
	
	public ArkIntelBotAPI()
	{
		
	}

	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public String getLicense() 
	{
		return license;
	}

	public void setLicense(String license) 
	{
		this.license = license;
	}

	public String getBotToken() 
	{
		return botToken;
	}

	public void setBotToken(String botToken) 
	{
		this.botToken = botToken;
	}

	public String getSlaveLicensingChannelId() 
	{
		return slaveLicensingChannelId;
	}

	public void setSlaveLicensingChannelId(String slaveLicensingChannelId) 
	{
		this.slaveLicensingChannelId = slaveLicensingChannelId;
	}

	public String getBattlemetricsChannelId() 
	{
		return battlemetricsChannelId;
	}

	public void setBattlemetricsChannelId(String battlemetricsChannelId) 
	{
		this.battlemetricsChannelId = battlemetricsChannelId;
	}

	public String getTribeMemberInputChannelId() 
	{
		return tribeMemberInputChannelId;
	}

	public void setTribeMemberInputChannelId(String tribeMemberInputChannelId) 
	{
		this.tribeMemberInputChannelId = tribeMemberInputChannelId;
	}

	public String getTribeAllyInputChannelId() 
	{
		return tribeAllyInputChannelId;
	}

	public void setTribeAllyInputChannelId(String tribeAllyInputChannelId) 
	{
		this.tribeAllyInputChannelId = tribeAllyInputChannelId;
	}

	public String getTribeEnemyInputChannelId() 
	{
		return tribeEnemyInputChannelId;
	}

	public void setTribeEnemyInputChannelId(String tribeEnemyInputChannelId) 
	{
		this.tribeEnemyInputChannelId = tribeEnemyInputChannelId;
	}

	public String getEnemyAlertsChannelId() 
	{
		return enemyAlertsChannelId;
	}

	public void setEnemyAlertsChannelId(String enemyAlertsChannelId) 
	{
		this.enemyAlertsChannelId = enemyAlertsChannelId;
	}
	
	public void addLiveIntelServer()
	{
		
	}
	
	public void getLiveIntelServers()
	{
		
	}
}

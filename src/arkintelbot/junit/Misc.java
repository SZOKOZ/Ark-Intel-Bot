package arkintelbot.junit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import arkintelbot.BattleMetrics;

class Misc {

	private static BattleMetrics bm = null;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception 
	{
		bm = new BattleMetrics();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception 
	{
		
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() 
	{
		bm.GetTop3(88);
		System.out.println(bm.GetValueFromJsonCache("servers:servers:1444306:ip"));
	}

}

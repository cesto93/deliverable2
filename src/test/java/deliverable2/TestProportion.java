package deliverable2;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import controller.Proportion;
import model.BugTicket;
import model.ReleaseInfo;

public class TestProportion {
	private ReleaseInfo[] releases;
	private BugTicket[] bugs;
	
	@Before
	public void setUp() {
		releases = new ReleaseInfo[6];
		releases[0] = new ReleaseInfo("12317843", "4.0.0", LocalDate.parse("2011-12-07").atStartOfDay());
		releases[1] = new ReleaseInfo("12319145", "4.1.0", LocalDate.parse("2012-06-13").atStartOfDay());
		releases[2] = new ReleaseInfo("12321757", "4.1.1", LocalDate.parse("2013-01-16").atStartOfDay());
		releases[3] = new ReleaseInfo("52317843", "4.0.0", LocalDate.parse("2011-12-07").atStartOfDay());
		releases[4] = new ReleaseInfo("52319145", "4.0.0", LocalDate.parse("2011-12-07").atStartOfDay());
		releases[5] = new ReleaseInfo("52321757", "4.0.0", LocalDate.parse("2011-12-07").atStartOfDay());
		
		bugs = new BugTicket[4];
		bugs[0] = new BugTicket("", Arrays.asList(new String[]{"12317843"}), Arrays.asList(new String[]{"52317843"}), null);
		bugs[1] = new BugTicket("", Arrays.asList(new String[]{"12319145"}), Arrays.asList(new String[]{"52319145"}), null);
		bugs[2] = new BugTicket("", Arrays.asList(new String[]{"12321757"}), Arrays.asList(new String[]{"52321757"}), null);
		bugs[3] = new BugTicket("", Arrays.asList(new String[]{"11111111"}), Arrays.asList(new String[]{"51111111"}), null);
	}
	
	@Test
	public void TestGetFV() {
		assertEquals(null, Proportion.getFV(bugs[3], releases));
		assertEquals(3, (int) Proportion.getFV(bugs[0], releases));
		assertEquals(4, (int) Proportion.getFV(bugs[1], releases));
		assertEquals(5, (int) Proportion.getFV(bugs[2], releases));
	}
	
	@Test
	public void TestGetOV() {
		assertEquals(0, (int) Proportion.getOV(LocalDate.parse("2011-12-06"), releases));
		assertEquals(1, (int) Proportion.getOV(LocalDate.parse("2011-12-08"), releases));
		assertEquals(2, (int) Proportion.getOV(LocalDate.parse("2012-06-15"), releases));
		assertEquals(null, Proportion.getOV(LocalDate.parse("2013-01-18"), releases));
	}
	
	@Test
	public void TestGetIV() {
		assertEquals(null, Proportion.getIV(bugs[3], releases));
		assertEquals(0, (int) Proportion.getIV(bugs[0], releases));
		assertEquals(1, (int) Proportion.getIV(bugs[1], releases));
		assertEquals(2, (int) Proportion.getIV(bugs[2], releases));
	}
}

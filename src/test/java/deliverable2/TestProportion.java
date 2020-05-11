package deliverable2;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

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
		releases = new ReleaseInfo[3];
		releases[0] = new ReleaseInfo("12317843", "4.0.0", LocalDate.parse("2011-12-07").atStartOfDay());
		releases[1] = new ReleaseInfo("12319145", "4.1.0", LocalDate.parse("2012-06-13").atStartOfDay());
		releases[2] = new ReleaseInfo("12321757", "4.1.1", LocalDate.parse("2013-01-16").atStartOfDay());
		
		bugs = new BugTicket[4];
		bugs[0] = new BugTicket("", new String[]{"12317843"}, new String[]{});
		bugs[1] = new BugTicket("", new String[]{"12319145"}, new String[]{});
		bugs[2] = new BugTicket("", new String[]{"12321757"}, new String[]{});
		bugs[3] = new BugTicket("", new String[]{"11111111"}, new String[]{});
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

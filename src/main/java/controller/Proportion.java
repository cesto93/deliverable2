package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.BugTicket;
import model.ReleaseInfo;
import utils.Mean;

public class Proportion {
	private static final Logger LOGGER = Logger.getLogger(Proportion.class.getName());
	
	private Proportion() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static Integer getOV(LocalDate ticketDate, ReleaseInfo[] releases) {
		for (int i = 0; i < releases.length; i++) {
			if (releases[i].getDate().toLocalDate().isAfter(ticketDate)) 
				return i;
		}
		return null;
	}
	
	public static Integer getFV(BugTicket ticket, ReleaseInfo[] releases) {
		Integer min = null;
		for (String fv : ticket.getFixedVersions()) {
			for (int i = 0; i < releases.length; i++) {
				if (fv.equals(releases[i].getVersionID())) {
					if (min == null) {
						min = i;
					}
					else {
						min = Math.min(min, i);
					}
				}
			}
		}
		return min;
	}
	
	public static Integer getIV(BugTicket ticket, ReleaseInfo[] releases) {
		Integer min = null;
		for (String iv : ticket.getAffectedVersions()) {
			for (int i = 0; i < releases.length; i++) {
				if (iv.equals(releases[i].getVersionID())) {
					if (min == null) {
						min = i;
					}
					else {
						min = Math.min(min, i);
					}
				}
			}
		}
		return min;
	}
	
	private static void addAV(BugTicket bug, int iv, int fv, ReleaseInfo[] releases) {	
		if (iv >= fv)
			return;
		ArrayList<String> affectedVersions = new ArrayList<>();
		for (int i = iv; i < fv; i++) {
			affectedVersions.add(releases[i].getVersionID());
		}
		if (LOGGER.isLoggable(Level.INFO))
			LOGGER.info(bug.getKey() + ": " + affectedVersions.toString());
		
		bug.setAffectedVersions(affectedVersions.toArray(new String[0]));
	}
	
	private static Double calculateP(int fv, int ov, Integer iv) {
		if (iv == null || fv <= iv) {
			if (LOGGER.isLoggable(Level.WARNING))
				LOGGER.warning("iv not valid \t IV: " + iv);
			return null;
		}
		return ((double) (fv - iv)) / (fv - ov);
	}
	
	public static void addMissingAV(BugTicket[] tickets, ReleaseInfo[] releases) {
		Mean mean = new Mean();
		for (BugTicket ticket : tickets) {
			Integer ov = getOV(ticket.getResolutionDate(), releases);
			Integer fv = getFV(ticket, releases);
			if ((ov == null) || (fv == null) || (fv <= ov)) {
				if (LOGGER.isLoggable(Level.WARNING))
					LOGGER.warning("fv or ov not valid FV:" + fv + " OV:" + ov + "\n");
				continue;
			}
			if (ticket.getAffectedVersions().length != 0) {
				Integer iv = getIV(ticket, releases);
				Double p = calculateP(fv, ov, iv);
				mean.addValue(p);
			} else {
				if (!mean.isEmpty()) {
					double p = mean.getMean();
					int iv = (int) ((fv - ov) * p);
					addAV(ticket, iv, fv, releases);
				} else {
					addAV(ticket, ov, fv, releases);
				}
			}
		}
	}
}

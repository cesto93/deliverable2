package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.BugTicket;
import model.ReleaseInfo;

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
	
	private static Double calculateP(BugTicket ticket, int fv, int ov, ReleaseInfo[] releases) {
		Integer iv = getIV(ticket, releases);
		if (iv == null || fv <= iv) {
			if (LOGGER.isLoggable(Level.WARNING))
				LOGGER.warning("iv not valid \t IV: " + iv);
			return null;
		}
		return ((double) (fv - iv)) / (fv - ov);
	}
	
	private static void getAVfromP(BugTicket ticket, double pSum, int n, int fv, int ov, ReleaseInfo[] releases) {
		if (n != 0) {
			double p = pSum / n;
			int iv = (int) ((fv - ov) * p);
			addAV(ticket, iv, fv, releases);
			return;
		}
			addAV(ticket, ov, fv, releases);
	}
	
	public static void addMissingAV(BugTicket[] tickets, ReleaseInfo[] releases) {
		double pSum = 0;
		int n = 0;
		for (BugTicket ticket : tickets) {
			Integer ov = getOV(ticket.getResolutionDate(), releases);
			Integer fv = getFV(ticket, releases);
			if ((ov == null) || (fv == null) || (fv <= ov)) {
				if (LOGGER.isLoggable(Level.WARNING))
					LOGGER.warning("fv or ov not valid FV:" + fv + " OV:" + ov + "\n");
				continue;
			}
			if (ticket.getAffectedVersions().length != 0) {
				Double p = calculateP(ticket, fv, ov, releases);
				if (p != null) {
					pSum += p;
					n++;
				}
			} else {
				if (n != 0) {
					double p = pSum / n;
					int iv = (int) ((fv - ov) * p);
					addAV(ticket, iv, fv, releases);
				} else {
					addAV(ticket, ov, fv, releases);
				}
			}
		}
	}
}

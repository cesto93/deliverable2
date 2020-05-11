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
	
	private static Double calculateProportion(double pSum, int n, BugTicket ticket,  ReleaseInfo[] releases) {
		Integer ov = getOV(ticket.getResolutionDate(), releases);
		Integer fv = getFV(ticket, releases);
		if ((ov == null) || (fv == null) || (fv <= ov)) {
			if (LOGGER.isLoggable(Level.WARNING))
				LOGGER.warning("fv or ov not valid FV:" + fv + " OV:" + ov + "\n");
			return null;
		}
		if (ticket.getAffectedVersions().length != 0) {
			Integer iv = getIV(ticket, releases);
			if (iv == null || fv <= iv) {
				LOGGER.warning("iv not valid \t IV: " + iv);
				return null;
			}
			return ((double) (fv - iv)) / (fv - ov);
		} else {
			if (n != 0) {
				double p = pSum / n;
				int iv = (int) ((fv - ov) * p);
				addAV(ticket, iv, fv, releases);
				return null;
			}
			addAV(ticket, ov, fv, releases);
		}
		return null;
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
				Integer iv = getIV(ticket, releases);
				if (iv == null || fv <= iv) {
					LOGGER.warning("iv not valid \t IV: " + iv);
					continue;
				}
				double p = ((double) (fv - iv)) / (fv - ov);
				pSum += p;
				n++;
				continue;
			} 
			if (n != 0) {
				double p = pSum / n;
				int iv = (int) ((fv - ov) * p);
				addAV(ticket, iv, fv, releases);
				continue;
			}
				addAV(ticket, ov, fv, releases);
		}
	}
}

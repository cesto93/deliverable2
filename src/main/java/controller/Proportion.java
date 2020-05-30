package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
			if (!releases[i].getDate().toLocalDate().isBefore(ticketDate)) // if releaseDate equals or after ticketDate
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
		if (iv < 0) {
			iv = 0;
		}
		
		if (iv >= fv)
			return;
		ArrayList<String> affectedVersions = new ArrayList<>();
		for (int i = iv; i < fv; i++) {
			affectedVersions.add(releases[i].getVersionID());
		}
		if (LOGGER.isLoggable(Level.INFO))
			LOGGER.info(bug.getKey() + ": " + affectedVersions.toString());
		
		bug.setAffectedVersions(affectedVersions);
	}
	
	private static Double calculateP(int fv, int ov, Integer iv) {
		if (iv == null || fv < iv) {
			if (LOGGER.isLoggable(Level.WARNING))
				LOGGER.warning(String.format("Not valid IV: %d FV %d", iv, fv));
			return null;
		}
		return ((double) (fv - iv)) / (fv - ov);
	}
	
	public static void addMissingAV(List<BugTicket> tickets, ReleaseInfo[] releases) {
		Mean mean = new Mean();
		for (BugTicket ticket : tickets) {
			Integer ov = getOV(ticket.getCreationDate(), releases);
			Integer fv = getFV(ticket, releases);
			if ((ov == null) || (fv == null) || (fv <= ov)) {
				if (LOGGER.isLoggable(Level.WARNING) && fv!= null && !fv.equals(ov))
					LOGGER.warning(String.format("Not valid FV: %d date: %s OV: %d", fv, ticket.getCreationDate(), ov));
				continue;
			}
			if (ticket.getAffectedVersions().size() != 0) {
				Integer iv = getIV(ticket, releases);
				Double p = calculateP(fv, ov, iv);
				mean.addValue(p);
			} else {
				if (!mean.isEmpty()) {
					double p = mean.getMean();
					int iv = (int) (fv -  ((fv - ov) * p));
					addAV(ticket, iv, fv, releases);
				} else {
					addAV(ticket, ov, fv, releases);
				}
			}
		}
	}
}

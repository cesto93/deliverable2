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
	
	public static int getOV(LocalDate ticketDate, ReleaseInfo[] releases) {
		for (int i = 0; i < releases.length; i++) {
			if (releases[i].getDate().toLocalDate().isAfter(ticketDate)) 
				return i;
		}
		return - 1;
	}
	
	public static Integer getFV(BugTicket ticket, ReleaseInfo[] releases) {
		Integer min = null;
		for (String fv : ticket.getFixedVersions()) {
			for (int i = 0; i < releases.length; i++) {
				if (fv.equals(releases[i].getVersionID())) 
					if (min == null) 
						min = i;
					else
						min = Math.min(min, i);
			}
		}
		return min;
	}
	
	public static Integer getIV(BugTicket ticket, ReleaseInfo[] releases) {
		Integer min = null;
		for (String iv : ticket.getAffectedVersions()) {
			for (int i = 0; i < releases.length; i++) {
				if (iv.equals(releases[i].getVersionID())) 
					if (min == null) 
						min = i;
					else
						min = Math.min(min, i);
			}
		}
		return min;
	}
	
	private static void addAV(BugTicket bug, int iv, int fv, ReleaseInfo[] releases) {	
		ArrayList<String> affectedVersions = new ArrayList<>();
		for (int i = iv; i < fv; i++) {
			affectedVersions.add(releases[i].getVersionID());
		}
		if (LOGGER.isLoggable(Level.INFO))
			LOGGER.info(bug.getKey() + ": " + affectedVersions.toString());
		
		bug.setAffectedVersions(affectedVersions.toArray(new String[0]));
	}
	
	public static void addMissingAV(BugTicket[] tickets, ReleaseInfo[] releases) {
		double pSum = 0;
		int n = 0;
		for (BugTicket ticket : tickets) {
			int ov = getOV(ticket.getResolutionDate(), releases);
			Integer fv = getFV(ticket, releases);
			if ((ov == -1) || (fv == null) || (fv <= ov)) {
				if (fv != null && fv != 0  && fv != ov)
					LOGGER.warning("fv or ov not valid FV:" + String.valueOf(fv) + " OV:" + String.valueOf(ov) + "\n");
			} else {
				if (ticket.getAffectedVersions().length != 0) {
					Integer iv = getIV(ticket, releases);
					if ((iv != null) && (fv > iv)) {
						double p = (fv - iv) / (fv - ov);
						pSum += p;
						n++;
					} else {
						if (iv == null)
							LOGGER.warning("AV :" + ticket.getAffectedVersions()[0]);
					}
				} else {
					double p = pSum / n;
					if (p != 0) {
						int iv = (int) ((fv - ov) * p);
						if (iv < fv)
							addAV(ticket, iv, fv, releases);
					}
					else {
						addAV(ticket, ov, fv, releases);
					}
				}
			}
		}
	}
}

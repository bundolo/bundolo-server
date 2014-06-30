package org.bundolo.services;

import java.util.List;

import org.bundolo.model.Contest;

public interface ContestService {

    public Contest findContest(Long contestId);

    public Contest findContest(String title);

    // public void deleteContest(Long contestId) throws Exception;

    public Boolean saveOrUpdateContest(Contest contest);

    public List<Contest> findContests(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter);

    public void clearSession();
}

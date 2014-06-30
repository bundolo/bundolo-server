package org.bundolo.services;

import java.util.List;

import org.bundolo.model.Contest;

public interface ContestService {

    public Contest findContest(Long contestId);

    public Contest findContest(String title);

    public Long saveContest(Contest contest) throws Exception;

    public void updateContest(Contest contest) throws Exception;

    public void deleteContest(Long contestId) throws Exception;

    public List<Contest> findContests(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter);
}

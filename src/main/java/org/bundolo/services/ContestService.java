package org.bundolo.services;

import java.util.List;

import org.bundolo.model.Contest;

public interface ContestService {

    public Contest findContest(Long contestId);

    public Long saveContest(Contest contest) throws Exception;

    public void updateContest(Contest contest) throws Exception;

    public void deleteContest(Long contestId) throws Exception;

    public List<Contest> findItemListContests(String query, Integer start, Integer end) throws Exception;

    public Integer findItemListContestsCount(String query) throws Exception;
}

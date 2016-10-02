package org.bundolo.services;

import java.util.List;

import org.bundolo.model.Contest;
import org.springframework.http.ResponseEntity;

public interface ContestService {

	public Contest findContest(Long contestId);

	public Contest findContest(String slug);

	public ResponseEntity<String> saveOrUpdateContest(Contest contest);

	public List<Contest> findContests(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
			String[] filter);

	public Contest findNext(Long contestId, String orderBy, String fixBy, boolean ascending);

	public void clearSession();

	public Long deleteContest(String slug);
}

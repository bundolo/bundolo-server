package org.bundolo.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bundolo.Constants;
import org.bundolo.Utils;
import org.bundolo.model.Comment;
import org.bundolo.model.Content;
import org.bundolo.services.CommentService;
import org.bundolo.services.ConnectionService;
import org.bundolo.services.ContentService;
import org.bundolo.services.ContestService;
import org.bundolo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class CommentController {

    private static final Logger logger = Logger.getLogger(CommentController.class.getName());

    @Autowired
    private CommentService commentService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ContestService contestService;

    @Autowired
    private ConnectionService connectionService;

    @RequestMapping(value = Constants.REST_PATH_PARENT_COMMENTS + "/{parentId}", method = RequestMethod.GET)
    public @ResponseBody
    List<Comment> comments(@PathVariable Long parentId) {
	// TODO check param validity
	return commentService.findCommentsByParentId(parentId);

    }

    // TODO this should eventually become put method, to avoid saving the same comment twice, but it's going to be a
    // problem finding unique url format for them
    @RequestMapping(value = Constants.REST_PATH_COMMENT, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    Long save(@RequestBody final Comment comment) {
	logger.log(Level.WARNING, "saving comment: " + comment);
	// TODO check param validity
	// TODO this could be nicer, move this logic to service. the problem with that is calling content update from
	// comment save method. transactions collide.
	Date creationDate = new Date();
	comment.setCreationDate(creationDate);
	comment.setLastActivity(creationDate);
	Content rootCommentAncestor = contentService.findContent(comment.getParentContent().getContentId());
	// make list of ancestors, so that all comments ancestors are updated
	List<Content> commentAncestors = new ArrayList<Content>();
	while (rootCommentAncestor != null && rootCommentAncestor.getKind().name().contains("comment")) {
	    commentAncestors.add(rootCommentAncestor);
	    rootCommentAncestor = rootCommentAncestor.getParentContent();
	}
	commentAncestors.add(rootCommentAncestor);
	comment.setKind(Utils.getCommentContentKind(rootCommentAncestor.getKind()));
	Long result = commentService.saveComment(comment);
	if (result != null) {
	    // logger.log(Level.WARNING, "saving comment; result not null: " + result);
	    for (Content commentAncestor : commentAncestors) {
		contentService.updateLastActivity(commentAncestor.getContentId(), creationDate);
	    }
	    commentService.clearSession();
	    switch (rootCommentAncestor.getKind()) {
	    case connection_description:
		connectionService.clearSession();
		break;
	    case contest_description:
		contestService.clearSession();
		break;
	    case user_description:
		userService.clearSession();
		break;
	    default:
		contentService.clearSession();
	    }

	}
	// logger.log(Level.WARNING, "saving comment; result: " + result);
	return result;
    }

}
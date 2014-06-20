package org.bundolo.controller;

import java.util.List;
import java.util.logging.Logger;

import org.bundolo.Constants;
import org.bundolo.model.Comment;
import org.bundolo.services.CommentService;
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

    @RequestMapping(value = Constants.REST_PATH_COMMENTS + "/{parentId}", method = RequestMethod.GET)
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
	logger.fine("saving comment: " + comment);
	// TODO check param validity
	return commentService.saveComment(comment);
    }

}
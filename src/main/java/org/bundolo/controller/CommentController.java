package org.bundolo.controller;

import java.util.List;

import org.bundolo.Constants;
import org.bundolo.model.Comment;
import org.bundolo.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = Constants.REST_PATH_COMMENTS + "/{parentId}", method = RequestMethod.GET)
    public @ResponseBody
    List<Comment> comments(@PathVariable Long parentId) {
	// TODO check param validity
	return commentService.findCommentsByParentId(parentId);
    }

}
package cn.edu.tj.wenda.service;

import cn.edu.tj.wenda.dao.CommentDao;
import cn.edu.tj.wenda.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by kang on 2017/10/17.
 */
@Service
public class CommentService {
    @Autowired
    CommentDao  commentDao;

    @Autowired
    SensitiveService sensitiveService;

    public int addComment(Comment comment){
        comment.setContent(sensitiveService.filter(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDao.addComment(comment) > 0 ? comment.getId() : 0;
    }

    public List<Comment> getCommentByEntity(int entityId,int entityType){
        return commentDao.selectCommentByEntity(entityId,entityType);
    }

    public int getCommentCount(int entityId,int entityType){
        return commentDao.getCommentCount(entityId,entityType);
    }

    public boolean deleteComment(int commentId){
        return commentDao.updateStatus(commentId,1) > 0;
    }

    public Comment getCommentById(int id){
        return commentDao.selectCommentById(id);
    }

    public int getUserCommentCount(int userId){
        return commentDao.getUserCommentCount(userId);
    }

    public List<Integer> getUserComment(int userId){
        return commentDao.getUserComment(userId);
    }
}

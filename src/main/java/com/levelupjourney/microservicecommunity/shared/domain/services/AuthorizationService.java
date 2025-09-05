package com.levelupjourney.microservicecommunity.shared.domain.services;

import com.levelupjourney.microservicecommunity.shared.application.acl.iam.IamProfileGateway;
import com.levelupjourney.microservicecommunity.shared.application.acl.iam.ProfileSnapshot;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.UserId;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Domain service for handling authorization logic.
 * Encapsulates business rules for user permissions.
 */
@Service
public class AuthorizationService {
    
    private static final String ADMIN_ROLE = "ADMIN";
    
    private final IamProfileGateway iamProfileGateway;
    
    public AuthorizationService(IamProfileGateway iamProfileGateway) {
        this.iamProfileGateway = iamProfileGateway;
    }
    
    /**
     * Checks if a user is an admin.
     * 
     * @param userId the user ID to check
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin(UserId userId) {
        if (userId == null) {
            return false;
        }
        
        return iamProfileGateway.fetchProfile(userId)
            .map(ProfileSnapshot::roles)
            .map(roles -> roles.contains(ADMIN_ROLE))
            .orElse(false);
    }
    
    /**
     * Checks if a user can edit a post.
     * Business rule: Only the post owner can edit their post.
     * 
     * @param postOwnerId the ID of the post owner
     * @param editorId the ID of the user trying to edit
     * @return true if the user can edit, false otherwise
     */
    public boolean canEditPost(UserId postOwnerId, UserId editorId) {
        if (postOwnerId == null || editorId == null) {
            return false;
        }
        
        return postOwnerId.equals(editorId);
    }
    
    /**
     * Checks if a user can delete a post.
     * Business rule: Only the post owner and admin can delete the post.
     * 
     * @param postOwnerId the ID of the post owner
     * @param deleterId the ID of the user trying to delete
     * @return true if the user can delete, false otherwise
     */
    public boolean canDeletePost(UserId postOwnerId, UserId deleterId) {
        if (postOwnerId == null || deleterId == null) {
            return false;
        }
        
        return postOwnerId.equals(deleterId) || isAdmin(deleterId);
    }
    
    /**
     * Checks if a user can edit a comment.
     * Business rule: Only the commenter can edit their own comment.
     * 
     * @param commentAuthorId the ID of the comment author
     * @param editorId the ID of the user trying to edit
     * @return true if the user can edit, false otherwise
     */
    public boolean canEditComment(UserId commentAuthorId, UserId editorId) {
        if (commentAuthorId == null || editorId == null) {
            return false;
        }
        
        return commentAuthorId.equals(editorId);
    }
    
    /**
     * Checks if a user can delete a comment.
     * Business rule: Only the post owner, admin, and the commenter can delete the comment.
     * 
     * @param postOwnerId the ID of the post owner
     * @param commentAuthorId the ID of the comment author
     * @param deleterId the ID of the user trying to delete
     * @return true if the user can delete, false otherwise
     */
    public boolean canDeleteComment(UserId postOwnerId, UserId commentAuthorId, UserId deleterId) {
        if (deleterId == null) {
            return false;
        }
        
        // Comment author can delete their own comment
        if (commentAuthorId != null && commentAuthorId.equals(deleterId)) {
            return true;
        }
        
        // Post owner can delete comments on their post
        if (postOwnerId != null && postOwnerId.equals(deleterId)) {
            return true;
        }
        
        // Admin can delete any comment
        return isAdmin(deleterId);
    }
    
    /**
     * Checks if a user can like a post (including their own).
     * Business rule: Any user can like a post, including their own post.
     * 
     * @param userId the ID of the user trying to like
     * @return true if the user can like, false otherwise
     */
    public boolean canLikePost(UserId userId) {
        return userId != null && iamProfileGateway.userExists(userId);
    }
    
    /**
     * Gets user roles for additional authorization checks.
     * 
     * @param userId the user ID
     * @return list of user roles, empty if user not found
     */
    public List<String> getUserRoles(UserId userId) {
        if (userId == null) {
            return List.of();
        }
        
        return iamProfileGateway.fetchProfile(userId)
            .map(ProfileSnapshot::roles)
            .orElse(List.of());
    }
}

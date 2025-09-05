# Interaction Testing Guide

This guide shows how to test the **Comments** and **Likes** functionality in the Community microservice.

## 🎯 Available Endpoints

### Comments API

#### 1. Add Comment to Post
```bash
POST /api/v1/comments/posts/{postId}
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/v1/comments/posts/{POST_ID} \
  -H "Content-Type: application/json" \
  -H "X-User-Id: e057bb73-19fe-4eb7-99ab-7d962cad3ed5" \
  -d '{
    "text": "Great post! Thanks for sharing."
  }'
```

#### 2. Get Comments for Post
```bash
GET /api/v1/comments/posts/{postId}
```

**Example:**
```bash
curl http://localhost:8080/api/v1/comments/posts/{POST_ID}
```

#### 3. Edit Comment
```bash
PUT /api/v1/comments/{commentId}
```

**Example:**
```bash
curl -X PUT http://localhost:8080/api/v1/comments/{COMMENT_ID} \
  -H "Content-Type: application/json" \
  -H "X-User-Id: e057bb73-19fe-4eb7-99ab-7d962cad3ed5" \
  -d '{
    "text": "Updated comment text"
  }'
```

#### 4. Delete Comment
```bash
DELETE /api/v1/comments/{commentId}
```

**Example:**
```bash
curl -X DELETE http://localhost:8080/api/v1/comments/{COMMENT_ID} \
  -H "X-User-Id: e057bb73-19fe-4eb7-99ab-7d962cad3ed5"
```

### Likes API

#### 1. Like a Post
```bash
POST /api/v1/posts/{postId}/like
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/v1/posts/{POST_ID}/like \
  -H "X-User-Id: e057bb73-19fe-4eb7-99ab-7d962cad3ed5"
```

#### 2. Unlike a Post
```bash
DELETE /api/v1/posts/{postId}/like
```

**Example:**
```bash
curl -X DELETE http://localhost:8080/api/v1/posts/{POST_ID}/like \
  -H "X-User-Id: e057bb73-19fe-4eb7-99ab-7d962cad3ed5"
```

## 🧪 Complete Testing Workflow

### Step 1: Create a Post
```bash
# Create a post first
POST_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/posts \
  -H "Content-Type: application/json" \
  -H "X-User-Id: e057bb73-19fe-4eb7-99ab-7d962cad3ed5" \
  -d '{
    "text": "This is a test post for interactions!",
    "images": []
  }')

# Extract post ID (assuming jq is available)
POST_ID=$(echo $POST_RESPONSE | jq -r '.id')
echo "Created post: $POST_ID"
```

### Step 2: Add Comments
```bash
# Comment as Mateo
COMMENT1=$(curl -s -X POST http://localhost:8080/api/v1/comments/posts/$POST_ID \
  -H "Content-Type: application/json" \
  -H "X-User-Id: e057bb73-19fe-4eb7-99ab-7d962cad3ed5" \
  -d '{
    "text": "First comment!"
  }')

# Comment as another user
COMMENT2=$(curl -s -X POST http://localhost:8080/api/v1/comments/posts/$POST_ID \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440001" \
  -d '{
    "text": "Second comment from John!"
  }')
```

### Step 3: Like the Post
```bash
# Like as Mateo
curl -X POST http://localhost:8080/api/v1/posts/$POST_ID/like \
  -H "X-User-Id: e057bb73-19fe-4eb7-99ab-7d962cad3ed5"

# Like as John
curl -X POST http://localhost:8080/api/v1/posts/$POST_ID/like \
  -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440001"
```

### Step 4: View Comments
```bash
# Get all comments for the post
curl http://localhost:8080/api/v1/comments/posts/$POST_ID
```

### Step 5: Edit a Comment
```bash
# Extract comment ID from previous response and edit
COMMENT_ID="..." # Get this from previous response
curl -X PUT http://localhost:8080/api/v1/comments/$COMMENT_ID \
  -H "Content-Type: application/json" \
  -H "X-User-Id: e057bb73-19fe-4eb7-99ab-7d962cad3ed5" \
  -d '{
    "text": "Edited comment text!"
  }'
```

## 🔍 Available Mock Users

You can test with these users:

### Real User (from IAM)
- **UUID**: `e057bb73-19fe-4eb7-99ab-7d962cad3ed5`
- **Name**: Mateo Alemán
- **Role**: STUDENT

### Mock Users
- **UUID**: `550e8400-e29b-41d4-a716-446655440001` | **Alias**: `user-1`
  - **Name**: John Doe
  - **Role**: STUDENT

- **UUID**: `550e8400-e29b-41d4-a716-446655440002` | **Alias**: `user-2`
  - **Name**: Jane Smith  
  - **Role**: TEACHER

- **UUID**: `550e8400-e29b-41d4-a716-446655440003` | **Alias**: `user-3`
  - **Name**: Bob Wilson
  - **Role**: STUDENT

## 📊 Testing Scenarios

### Scenario 1: Basic Comment Flow
1. Create a post
2. Add multiple comments from different users
3. View all comments
4. Edit a comment (as original author)
5. Try to edit someone else's comment (should fail)
6. Delete a comment

### Scenario 2: Like/Unlike Flow
1. Create a post
2. Like the post from multiple users
3. Try to like the same post again (should return "already liked")
4. Unlike the post
5. Try to unlike again (should return "not previously liked")

### Scenario 3: Error Handling
1. Try to comment with invalid user ID
2. Try to like with invalid post ID
3. Try to edit/delete non-existent comment
4. Try to edit/delete someone else's comment

## 🎯 Expected Responses

### Comment Created Successfully
```json
{
  "commentId": "comment-uuid-here",
  "postId": "post-uuid-here", 
  "message": "Comment added successfully"
}
```

### Post Liked Successfully
```json
{
  "postId": "post-uuid-here",
  "userId": "user-uuid-here",
  "liked": true,
  "message": "Post liked successfully"
}
```

### Comments List
```json
[
  {
    "id": "comment-uuid",
    "postId": "post-uuid",
    "authorId": "user-uuid",
    "text": "Comment text",
    "createdAt": "2025-09-05T19:00:00",
    "editedAt": null,
    "isEdited": false
  }
]
```

---

**🎉 Ready to test!** Use Swagger UI at `http://localhost:8080/swagger-ui.html` for interactive testing.

# Community Microservice - Bounded Contexts

This document describes the three main bounded contexts in the Community microservice, following Domain-Driven Design (DDD) principles with Spring Data MongoDB.

## 📋 Table of Contents

- [Architecture Overview](#architecture-overview)
- [Bounded Context 1: Posting](#bounded-context-1-posting)
- [Bounded Context 2: Interaction](#bounded-context-2-interaction)
- [Bounded Context 3: IAM Integration (ACL)](#bounded-context-3-iam-integration-acl)
- [MongoDB Collections](#mongodb-collections)
- [API Endpoints](#api-endpoints)
- [Domain Events](#domain-events)
- [Technology Stack](#technology-stack)

## 🏗️ Architecture Overview

The Community microservice implements a social media platform with the following bounded contexts:

```
┌─────────────────────────────────────────────────────────────┐
│                    Community Microservice                  │
├─────────────────┬─────────────────┬─────────────────────────┤
│   Posting BC    │ Interaction BC  │    IAM Integration      │
│                 │                 │        (ACL)            │
│ - Posts         │ - Comments      │ - Profile Cache         │
│ - Content       │ - Likes         │ - User Validation       │
│ - Authorship    │ - Reactions     │ - External Gateway      │
└─────────────────┴─────────────────┴─────────────────────────┘
```

## 📝 Bounded Context 1: Posting

### Purpose
Manages the complete lifecycle of social media posts including creation, editing, and deletion.

### Domain Model

#### Aggregate Root: `Post`
- **Identity**: `PostId` (UUID string)
- **Author**: `UserId` (from external IAM)
- **Content**: `PostBody` (text + images)
- **Timestamps**: Creation and edit times
- **State**: Active/deleted flag

#### Value Objects
- **`PostBody`**: Encapsulates post content with business rules
  - Text: Optional, max 500 characters
  - Images: Optional, max 5 images
  - Invariant: At least one of text or images required
- **`ImageRef`**: Image reference with URL and alt text
- **`PostId`**: Strongly typed post identifier

#### Domain Events
- `PostCreated`: Fired when a new post is created
- `PostEdited`: Fired when post content is modified
- `PostDeleted`: Fired when post is soft-deleted

#### Commands
- `CreatePostCommand`: Create a new post
- `EditPostCommand`: Modify existing post content
- `DeletePostCommand`: Soft delete a post

#### Queries
- `GetPostQuery`: Retrieve a specific post
- `GetPostFeedQuery`: Get paginated post feed (optionally filtered by author)

### Business Rules
1. Only the author can edit or delete their posts
2. Deleted posts cannot be edited
3. Post content must have either text (≤500 chars) or images (≤5)
4. All operations require valid user authentication via ACL

### Directory Structure
```
posting/
├── domain/
│   ├── model/
│   │   ├── aggregates/Post.java
│   │   ├── valueobjects/{PostBody, ImageRef}.java
│   │   ├── commands/{Create, Edit, Delete}PostCommand.java
│   │   ├── queries/Get{Post, PostFeed}Query.java
│   │   └── events/Post{Created, Edited, Deleted}.java
│   └── services/{PostCommandService, PostQueryService}.java
├── application/internal/
│   ├── commandservices/PostCommandServiceImpl.java
│   └── queryservices/PostQueryServiceImpl.java
├── infrastructure/persistence/mongodb/repositories/PostRepository.java
└── interfaces/rest/
    ├── resources/{CreatePostResource, PostResource, ImageRefResource}.java
    └── transform/PostController.java
```

## 💬 Bounded Context 2: Interaction

### Purpose
Handles social interactions on posts including comments and likes.

### Domain Model

#### Aggregate Root: `Comment`
- **Identity**: `CommentId` (UUID string)
- **Post Reference**: `PostId`
- **Author**: `UserId`
- **Content**: `CommentContent` (text ≤300 chars)
- **Timestamps**: Creation and edit times
- **State**: Active/deleted flag

#### Entity: `Like`
- **Composite Key**: `(PostId, UserId)`
- **Timestamp**: Creation time
- **Invariant**: One like per user per post

#### Value Objects
- **`CommentContent`**: Text content with length validation
- **`CommentId`**: Strongly typed comment identifier

#### Domain Events
- `CommentAdded`: New comment on a post
- `CommentEdited`: Comment content modified
- `CommentDeleted`: Comment soft-deleted
- `PostLiked`: User liked a post
- `PostUnliked`: User removed their like

#### Commands
- `AddCommentCommand`: Add comment to a post
- `EditCommentCommand`: Modify comment content
- `DeleteCommentCommand`: Soft delete a comment
- `LikePostCommand`: Like a post
- `UnlikePostCommand`: Remove like from a post

### Business Rules
1. Only comment author can edit/delete their comments
2. Users can only like a post once
3. Comments limited to 300 characters
4. All operations require valid user authentication

### Directory Structure
```
interaction/
├── domain/model/
│   ├── aggregates/{Comment, Like}.java
│   ├── valueobjects/CommentContent.java
│   ├── commands/{Add, Edit, Delete}Comment, {Like, Unlike}PostCommand.java
│   ├── queries/Get{Comments, Likes}Query.java
│   └── events/Comment{Added, Edited, Deleted}, Post{Liked, Unliked}.java
├── application/internal/
│   ├── commandservices/{Comment, Like}CommandServiceImpl.java
│   └── queryservices/{Comment, Like}QueryServiceImpl.java
├── infrastructure/persistence/mongodb/repositories/
└── interfaces/rest/
```

## 🔐 Bounded Context 3: IAM Integration (ACL)

### Purpose
Provides Anti-Corruption Layer for integrating with external Identity and Access Management service while maintaining domain purity.

### ACL Pattern Implementation

#### Components
- **`IamProfileGateway`**: Interface for external IAM communication
- **`ProfileCacheService`**: Domain service for profile management
- **`ProfileSnapshot`**: Domain representation of user profiles
- **`AuthorSnapshot`**: Simplified profile for read models

#### Responsibilities
1. **User Validation**: Verify user existence before domain operations
2. **Profile Caching**: Local cache to reduce external service calls
3. **Data Translation**: Convert external data to domain-friendly format
4. **Isolation**: Protect domain from external service changes

### Cache Strategy
- **Cache First**: Check local cache before external calls
- **Staleness Detection**: Configurable cache expiration
- **Lazy Loading**: Fetch profiles on-demand
- **Eviction**: Manual cache invalidation support

### Directory Structure
```
shared/application/acl/iam/
├── IamProfileGateway.java (interface)
├── ProfileCacheService.java (interface)
├── ProfileSnapshot.java
└── AuthorSnapshot.java

shared/infrastructure/persistence/mongodb/
├── entities/ProfileCacheDocument.java
└── repositories/ProfileCacheRepository.java
```

## 🗄️ MongoDB Collections

### Write Side (Command Model)
- **`posts`**: Post aggregates with embedded content
- **`comments`**: Comment aggregates
- **`likes`**: Like entities with composite keys
- **`profile_cache`**: Cached user profiles from IAM
- **`outbox`**: Domain events for reliable publishing

### Read Side (Query Model) - *To be implemented*
- **`post_feed`**: Denormalized posts with author info and counters
- **`post_detail`**: Enhanced post view with full details
- **`comments_view`**: Comments with embedded author info
- **`post_counters`**: Like and comment counts per post

## 🌐 API Endpoints

### Posts API
```http
POST   /api/v1/posts                    # Create post
GET    /api/v1/posts/{id}              # Get specific post
GET    /api/v1/posts?authorId={id}     # Get post feed
PATCH  /api/v1/posts/{id}              # Edit post
DELETE /api/v1/posts/{id}              # Delete post
```

### Comments API - *To be implemented*
```http
POST   /api/v1/posts/{id}/comments     # Add comment
GET    /api/v1/posts/{id}/comments     # Get post comments
PATCH  /api/v1/comments/{id}           # Edit comment
DELETE /api/v1/comments/{id}           # Delete comment
```

### Likes API - *To be implemented*
```http
POST   /api/v1/posts/{id}/likes        # Like post
DELETE /api/v1/posts/{id}/likes        # Unlike post
```

## ⚡ Domain Events

### Event Flow
```
Domain Operation → Aggregate → Domain Event → Outbox → Event Bus → Read Model Projections
```

### Event Types
- **Posting Events**: `PostCreated`, `PostEdited`, `PostDeleted`
- **Interaction Events**: `CommentAdded`, `CommentEdited`, `CommentDeleted`, `PostLiked`, `PostUnliked`

### Event Handling
- Events stored in outbox for reliable delivery
- Read models updated via event projections
- Cross-context communication through events

## 🔧 Technology Stack

- **Framework**: Spring Boot 3.5.5
- **Database**: MongoDB with Spring Data MongoDB
- **Architecture**: DDD + CQRS + Event Sourcing
- **API**: REST with OpenAPI documentation
- **Validation**: Bean Validation (JSR-303)
- **Documentation**: Swagger/OpenAPI 3

## 🚀 Implementation Status

### ✅ Completed
- [x] Posting bounded context (complete)
- [x] ACL infrastructure for IAM integration
- [x] MongoDB configuration and repositories
- [x] REST API for posts
- [x] Domain events foundation
- [x] CQRS command/query separation

### 🔄 In Progress / Planned
- [ ] Interaction bounded context implementation
- [ ] Read model projections and projectors
- [ ] Outbox pattern implementation
- [ ] ACL service implementations (HTTP client)
- [ ] Profile cache service implementation
- [ ] Event handlers and projectors
- [ ] Integration tests
- [ ] API documentation enhancement

## 📚 Key Design Principles

1. **Domain Purity**: Core domain isolated from infrastructure concerns
2. **Bounded Context Isolation**: Clear boundaries between contexts
3. **Anti-Corruption Layer**: External dependencies abstracted through ACL
4. **Event-Driven Architecture**: Loose coupling through domain events
5. **CQRS**: Command and query responsibilities separated
6. **Single Document Aggregates**: Optimized for MongoDB performance
7. **Strong Typing**: Value objects for all domain identifiers
8. **Business Invariants**: Enforced at aggregate boundaries

---

*This microservice follows DDD best practices adapted for MongoDB, ensuring maintainability, scalability, and domain clarity.*

# Community Microservice - Technical Architecture

## 🏛️ Layered Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        REST API Layer                           │
│  PostController │ CommentController │ LikeController             │
├─────────────────────────────────────────────────────────────────┤
│                     Application Layer                           │
│  CommandServices │ QueryServices │ EventHandlers                │
├─────────────────────────────────────────────────────────────────┤
│                       Domain Layer                              │
│  Aggregates │ Value Objects │ Domain Services │ Events          │
├─────────────────────────────────────────────────────────────────┤
│                   Infrastructure Layer                          │
│  Repositories │ ACL Gateways │ Event Store │ MongoDB            │
└─────────────────────────────────────────────────────────────────┘
```

## 🔄 CQRS + Event Sourcing Pattern

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Command   │    │   Domain    │    │   Events    │
│    Side     │───▶│ Aggregates  │───▶│   Outbox    │
│             │    │             │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
                                            │
                                            ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│    Query    │◀───│    Read     │◀───│  Event Bus  │
│    Side     │    │   Models    │    │ Projectors  │
│             │    │             │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
```

## 🔗 Bounded Context Relationships

```
┌──────────────────────────────────────────────────────────────┐
│                    External IAM Service                     │
└────────────────────────┬─────────────────────────────────────┘
                         │ HTTP/REST
                         ▼
┌──────────────────────────────────────────────────────────────┐
│                        ACL Layer                            │
│  IamProfileGateway │ ProfileCacheService │ ProfileCache     │
└────────────────────────┬─────────────────────────────────────┘
                         │ Domain Objects (UserId, ProfileSnapshot)
                         ▼
┌─────────────────────┬────────────────────┬───────────────────┐
│   Posting Context   │ Interaction Context│  Shared Kernel    │
│                     │                    │                   │
│ Post ────────────┐  │ Comment            │ UserId            │
│ PostBody         │  │ Like               │ PostId            │
│ ImageRef         │  │ CommentContent     │ CommentId         │
│                  │  │                    │ DomainEvent       │
│                  │  │                    │ AggregateRoot     │
└──────────────────┼──┴────────────────────┴───────────────────┘
                   │
                   ▼ Domain Events
┌──────────────────────────────────────────────────────────────┐
│                    Read Models                               │
│ PostFeedView │ PostDetailView │ CommentsView │ PostCounters  │
└──────────────────────────────────────────────────────────────┘
```

## 📊 Data Flow Architecture

### Write Operations Flow
```
REST Request → Controller → Command → Domain Service → Aggregate → Repository → MongoDB
                                           │
                                           ▼
                                    Domain Event → Outbox
```

### Read Operations Flow
```
REST Request → Controller → Query → Query Service → Repository → MongoDB (Read Models)
```

### Event Processing Flow
```
Domain Event → Outbox → Event Publisher → Event Bus → Event Handlers → Read Model Updates
```

## 🗃️ Database Schema Design

### Posts Collection
```json
{
  "_id": "post-uuid",
  "postId": "post-uuid",
  "authorId": "user-uuid",
  "content": {
    "text": "Hello world!",
    "images": [
      {
        "url": "https://cdn.example.com/image.jpg",
        "altText": "Description"
      }
    ]
  },
  "createdAt": "2025-09-05T12:00:00Z",
  "editedAt": null,
  "deleted": false
}
```

### Comments Collection
```json
{
  "_id": "comment-uuid",
  "commentId": "comment-uuid",
  "postId": "post-uuid",
  "authorId": "user-uuid",
  "content": "Nice post!",
  "createdAt": "2025-09-05T12:30:00Z",
  "editedAt": null,
  "deleted": false
}
```

### Likes Collection
```json
{
  "_id": {
    "postId": "post-uuid",
    "userId": "user-uuid"
  },
  "createdAt": "2025-09-05T12:45:00Z"
}
```

### Profile Cache Collection
```json
{
  "_id": "user-uuid",
  "username": "john_doe",
  "name": "John Doe",
  "avatarUrl": "https://cdn.example.com/avatar.jpg",
  "roles": ["STUDENT"],
  "lastSyncedAt": "2025-09-05T11:55:00Z"
}
```

### Read Models (Denormalized)

#### Post Feed View
```json
{
  "_id": "post-uuid",
  "content": {
    "text": "Hello world!",
    "images": [...]
  },
  "createdAt": "2025-09-05T12:00:00Z",
  "editedAt": null,
  "author": {
    "id": "user-uuid",
    "username": "john_doe",
    "name": "John Doe",
    "avatarUrl": "https://cdn.example.com/avatar.jpg",
    "roles": ["STUDENT"]
  },
  "likeCount": 42,
  "commentCount": 17
}
```

## 🛡️ Security & Validation

### Authentication Flow
```
Client Request → JWT Token → User Context → ACL Validation → Domain Operation
```

### Authorization Rules
- Users can only edit/delete their own posts/comments
- User existence validated through ACL before any operation
- All operations require valid user context

### Input Validation
- Bean Validation (JSR-303) at REST layer
- Domain invariants enforced in aggregates
- Business rules validated in domain services

## 🔍 Monitoring & Observability

### Metrics to Track
- API response times
- Domain event processing latency
- Cache hit/miss ratios
- MongoDB query performance
- Error rates by operation type

### Logging Strategy
- Structured logging with correlation IDs
- Domain event audit trail
- ACL interaction logging
- Performance monitoring

## 🚀 Deployment Considerations

### Environment Configuration
- MongoDB connection settings
- External IAM service endpoints
- Cache configuration (TTL, size limits)
- Event bus configuration

### Scaling Strategies
- Read model separation for query scaling
- Event sourcing for write scaling
- MongoDB sharding considerations
- Cache layer scaling

### Resilience Patterns
- Circuit breaker for external IAM calls
- Retry mechanisms with exponential backoff
- Graceful degradation when IAM unavailable
- Event replay capabilities

---

*This technical architecture ensures scalability, maintainability, and resilience while following DDD and CQRS best practices.*

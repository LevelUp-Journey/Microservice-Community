package com.levelup.journey.platform.social.interfaces.rest.resources;

/**
 * Reaction Toggle Response Resource
 * Response for toggle reaction operations
 */
public record ReactionToggleResponse(
        String action,  // "CREATED" or "REMOVED"
        ReactionResource reaction  // The reaction (null if REMOVED)
) {
    public static ReactionToggleResponse created(ReactionResource reaction) {
        return new ReactionToggleResponse("CREATED", reaction);
    }

    public static ReactionToggleResponse removed() {
        return new ReactionToggleResponse("REMOVED", null);
    }
}

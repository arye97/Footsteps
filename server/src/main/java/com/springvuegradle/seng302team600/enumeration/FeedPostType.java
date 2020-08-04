package com.springvuegradle.seng302team600.enumeration;

/**
 * An enum to distinguish between the different types of posts on a users subscription feed
 * Possible feed post types include:
 * FOLLOW - Someone followed your event/You followed an event
 * UNFOLLOW - Somone unfollowed your event/You unfollowed an event
 * MODIFY - The creator modified an event you are following
 * DELETE - The creator deleted an event you are following
 */
enum FeedPostType {
    FOLLOW,
    UNFOLLOW,
    MODIFY,
    DELETE
}
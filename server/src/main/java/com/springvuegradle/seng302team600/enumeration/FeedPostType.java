package com.springvuegradle.seng302team600.enumeration;

/**
 * An enum to distinguish between the different types of posts on a users subscription feed
 * Possible feed post types include:
 * FOLLOW - Someone followed your event/You followed an event
 * UNFOLLOW - Someone unfollowed your event/You unfollowed an event
 * MODIFY - The creator modified an event you are following
 * DELETE - The creator deleted an event you are following
 * ADD_RESULT - You/Someone adds a result to an event you are following
 */
public enum FeedPostType {

    FOLLOW(Values.FOLLOW),
    UNFOLLOW(Values.UNFOLLOW),
    MODIFY(Values.MODIFY),
    DELETE(Values.DELETE),
    ADD_RESULT(Values.ADD_RESULT);

    FeedPostType(String value) {
        //pass
    }

    public static class Values {
        public static final String FOLLOW = "FOLLOW";
        public static final String UNFOLLOW = "UNFOLLOW";
        public static final String MODIFY = "MODIFY";
        public static final String DELETE = "DELETE";
        public static final String ADD_RESULT = "ADD_RESULT";
    }
}
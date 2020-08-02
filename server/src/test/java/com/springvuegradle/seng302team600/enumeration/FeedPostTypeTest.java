package com.springvuegradle.seng302team600.enumeration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FeedPostTypeTest {

    @Test
    void canHaveFollowPosts() {
        assertNotNull(FeedPostType.valueOf("FOLLOW"));
    }

    @Test
    void canHaveUnfollowPosts() {
        assertNotNull(FeedPostType.valueOf("UNFOLLOW"));
    }

    @Test
    void canHaveModifyPosts() {
        assertNotNull(FeedPostType.valueOf("MODIFY"));
    }

    @Test
    void canHaveDeletePosts() {
        assertNotNull(FeedPostType.valueOf("DELETE"));
    }
}
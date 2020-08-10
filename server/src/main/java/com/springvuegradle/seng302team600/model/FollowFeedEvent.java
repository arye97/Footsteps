package com.springvuegradle.seng302team600.model;

import org.hibernate.annotations.DiscriminatorFormula;

import javax.persistence.*;

@Entity
@DiscriminatorFormula("case when follow is True then FeedPostType.Values.FOLLOW else FeedPostType.Values.UNFOLLOW end")
public class FollowFeedEvent extends FeedEvent {

    // Whether or not the event is a follow or unfollow event
    // Not a column as we have access to the feed_event discriminatory column
    Boolean follow;

    @Column(name = "activity_id")
    Long activityId;

    @Column(name = "user_id")
    Long userId;

    /**
     * Default constructor
     * Mandatory for repository events
     */
    public FollowFeedEvent() {
    }

    /**
     * Constructor
     * @param followType    True for a follow event, False for an unfollow event
     */
    public FollowFeedEvent(Boolean followType) {
        follow = followType;
    }

    public Long getActivityId() {
        return activityId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setActivityId(Long id) {
        activityId = id;
    }

    public void setUserId(Long id) {
        userId = id;
    }
}

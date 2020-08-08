package com.springvuegradle.seng302team600.model;

import com.springvuegradle.seng302team600.enumeration.FeedPostType;

import javax.persistence.*;
import java.util.Date;

/**
 * The superclass for feed events
 * All feed events will 'extend' this class
 * The discriminator column is filled in by the subclasses and will use the enumeration FeedPostType
 */
@Entity
@Table(name = "feed_event")
public class FeedEvent {

    // The ID of the event, will be auto-set by Spring
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_event_id", nullable = false)
    Long feedEventId;

    // The timestamp of the feed event generation - ie. when the feed post was triggered
    @Column(name = "time_stamp", nullable = false, columnDefinition = "DATE")
    Date timeStamp;

    // The activity related to the feed event
    @Column(name = "activity_id", nullable = false)
    Long activityId;

    // The user who caused the feed event
    @Column(name = "author_id", nullable = false)
    Long authorId;

    // The user to view this feed event
    @Column(name = "viewer_id", nullable = false)
    Long viewerId;

    // The type of feed post - set by the enum
    @Column(name = "feed_event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    FeedPostType feedEventType;

    /**
     * Default constructor for feed events
     * Mandatory for repository actions
     */
    public FeedEvent() {
    }

    public Long getFeedEventId() {
        return feedEventId;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public FeedPostType getFeedEventType() {
        return feedEventType;
    }

    public Long getActivityId() {
        return activityId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public Long getViewerId() {
        return viewerId;
    }

    public void setFeedEventType(FeedPostType type) {
        feedEventType = type;
    }

    public void setActivityId(Long id) {
        activityId = id;
    }

    public void setAuthorId(Long id) {
        authorId = id;
    }

    public void setViewerId(Long id) {
        viewerId = id;
    }

    public void setTimeStamp(Date time) {
        timeStamp = time;
    }

    /**
     * A method to set the time stamp to the time right now
     */
    public void setTimeStampNow() {
        timeStamp = new Date();
    }
}

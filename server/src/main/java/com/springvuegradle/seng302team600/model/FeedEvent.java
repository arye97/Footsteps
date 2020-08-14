package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_event_id", nullable = false)
    private Long feedEventId;

    // The timestamp of the feed event generation - ie. when the feed post was triggered
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("timeStamp")
    @Column(name = "time_stamp", nullable = false, columnDefinition = "DATETIME")
    private Date timeStamp;

    // The id of the activity related to the feed event
    @JsonProperty("activityId")
    @Column(name = "activity_id", nullable = false)
    private Long activityId;

    // The name of activity related to the feed event (This is a work around for deleted events)
    @JsonProperty("activityName")
    @Column(name = "activity_name", nullable = false)
    private String activityName;

    // The user who caused the feed event
    @JsonProperty("userId")
    @Column(name = "author_id", nullable = false)
    private Long authorId;


    // The user to view this feed event
    @JsonIgnore
    @Column(name = "viewer_id", nullable = false)
    private Long viewerId;

    // The type of feed post - set by the enum
    @JsonProperty("feedEventType")
    @Column(name = "feed_event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedPostType feedEventType;

    /**
     * Default constructor for feed events
     * Mandatory for repository actions
     */
    public FeedEvent() {
    }

    /**
     * Create a FeedEvent with timeStamp now
     * @param activityId The id of the activity related to the feed event
     * @param activityName The name of the activity related to the feed event
     * @param authorId The user who caused the feed event
     * @param viewerId The user to view this feed event
     * @param feedEventType The type of feed post - set by the enum
     */
    public FeedEvent(Long activityId, String activityName, Long authorId, Long viewerId, FeedPostType feedEventType) {
        setTimeStampNow();
        this.activityId = activityId;
        this.activityName = activityName;
        this.authorId = authorId;
        this.viewerId = viewerId;
        this.feedEventType = feedEventType;
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

    public String getActivityName() {
        return activityName;
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

    public void setActivityName(String name) {
        activityName = name;
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

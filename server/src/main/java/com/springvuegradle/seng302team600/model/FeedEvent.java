package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.enumeration.FeedPostType;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    // The users to view this feed event
    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})  // ALL except REMOVE
    @JoinTable(
            name = "feed_event_viewers",
            joinColumns = @JoinColumn(name = "feed_event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> viewers;

    // The type of feed post - set by the enum
    @JsonProperty("feedEventType")
    @Column(name = "feed_event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedPostType feedEventType;

    // The type of feed post - set by the enum
    @Column(name = "outcome_title")
    private String outcomeTitle;

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
     * @param feedEventType The type of feed post - set by the enum
     */
    public FeedEvent(Long activityId, String activityName, Long authorId, FeedPostType feedEventType) {
        setTimeStampNow();
        this.activityId = activityId;
        this.activityName = activityName;
        this.authorId = authorId;
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

    public Set<User> getViewers() {
        return viewers;
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

    public void setViewers(Set<User> viewers) {
        this.viewers = viewers;
    }

    public void addViewer(User viewer) {
        if (viewers == null) viewers = new HashSet<>();
        viewers.add(viewer);
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

    public String getOutcomeTitle() {
        return outcomeTitle;
    }

    public void setOutcomeTitle(String outcomeTitle) {
        this.outcomeTitle = outcomeTitle;
    }
}

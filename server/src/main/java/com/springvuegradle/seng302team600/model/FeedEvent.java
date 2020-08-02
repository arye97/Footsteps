package com.springvuegradle.seng302team600.model;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * The superclass for feed events
 * All feed events will 'extend' this class
 */
@MappedSuperclass
public class FeedEvent {

    // The ID of the event, will be auto-set by Spring
    @Id
    Long eventId;

    // The type of feed post, from the enumeration
    // FeedPostType postType;

    // The timestamp of the feed event generation - ie. when the feed post was triggered
    Date timeStamp;
}

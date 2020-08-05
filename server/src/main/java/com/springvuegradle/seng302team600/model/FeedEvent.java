package com.springvuegradle.seng302team600.model;

import javax.persistence.*;
import java.util.Date;

/**
 * The superclass for feed events
 * All feed events will 'extend' this class
 * The discriminator column is filled in by the subclasses and will use the enumeration FeedPostType
 */
@Entity
@Table(name = "feed_event")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="postType")
public class FeedEvent {

    // The ID of the event, will be auto-set by Spring
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_event_id", nullable = false)
    Long eventId;

    // The timestamp of the feed event generation - ie. when the feed post was triggered
    @Column(name = "time_stamp", nullable = false, columnDefinition = "DATE")
    Date timeStamp;
}

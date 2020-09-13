package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.enumeration.FeedPostType;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.FeedEvent;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.FeedEventRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * This service handles the creation of FeedEvent objects,
 * and saving them on the database via FeedEventRepository.
 */
@Service("feedEventService")
public class FeedEventService {

    private final FeedEventRepository feedEventRepository;
    private final UserRepository userRepository;

    public FeedEventService(FeedEventRepository feedEventRepository, UserRepository userRepository) {
        this.feedEventRepository = feedEventRepository;
        this.userRepository = userRepository;
    }

    /**
     * Helper function for addResultToActivityEvent, modifyActivityEvent and deleteActivityEvent.
     */
    private void activityFeedEvent(Activity activity, FeedEvent feedEvent) {
        // The creator gets notified
        User creator = userRepository.findByUserId(activity.getCreatorUserId());
        feedEvent.addViewer(creator);

        // The participants get notified
        for (User user : activity.getParticipants()) {
            feedEvent.addViewer(user);
        }
        feedEventRepository.save(feedEvent);
    }

    /**
     * Creates FeedEvent instance linking to each participant and the creator for the given activity.
     * These FeedEvents have the feed event type MODIFY.
     * This method assumes activity was modified successfully and should be called after this is known.
     *
     * @param activity the activity being modified
     * @param authorId the user's id who is modifying the activity
     */
    public void modifyActivityEvent(Activity activity, Long authorId) {
        FeedEvent modifyEvent = new FeedEvent();
        modifyEvent.setTimeStampNow();
        modifyEvent.setFeedEventType(FeedPostType.MODIFY);
        modifyEvent.setActivityId(activity.getActivityId());
        modifyEvent.setActivityName(activity.getName());
        modifyEvent.setAuthorId(authorId);

        // Call helper function to save feed event for each user requiring notification
        activityFeedEvent(activity, modifyEvent);
    }

    /**
     * Creates FeedEvent instance linking to each participant and the creator for the given activity.
     * These FeedEvents have the feed event type DELETE.
     * This method assumes activity will be deleted successfully and should be called just before deleting.
     *
     * @param activity the activity being deleted
     * @param authorId the user's id who is deleting the activity
     */
    public void deleteActivityEvent(Activity activity, Long authorId) {
        FeedEvent deleteEvent = new FeedEvent();
        deleteEvent.setTimeStampNow();
        deleteEvent.setFeedEventType(FeedPostType.DELETE);
        deleteEvent.setActivityId(activity.getActivityId());
        deleteEvent.setActivityName(activity.getName());
        deleteEvent.setAuthorId(authorId);

        // Call helper function to save feed event for each user requiring notification
        activityFeedEvent(activity, deleteEvent);
    }

    /**
     * Creates FeedEvent instance linking to each participant and the creator for the given activity.
     * These FeedEvents have the feed event type ADD_RESULT.
     * All FeedEvents are saved to the FeedEventRepository.
     * This method assumes activity result will be added successfully and should be called just before result is added.
     *
     * @param activity     the activity being deleted
     * @param authorId     the user's id who is deleting the activity
     * @param outcomeTitle title of outcome that the result correlates to
     */
    public void addResultToActivityEvent(Activity activity, Long authorId, String outcomeTitle) {
        FeedEvent addResultEvent = new FeedEvent();
        addResultEvent.setTimeStampNow();
        addResultEvent.setFeedEventType(FeedPostType.ADD_RESULT);
        addResultEvent.setActivityId(activity.getActivityId());
        addResultEvent.setActivityName(activity.getName());
        addResultEvent.setAuthorId(authorId);
        addResultEvent.setOutcomeTitle(outcomeTitle);

        // Call helper function to save feed event for each user requiring notification
        activityFeedEvent(activity, addResultEvent);
    }
}

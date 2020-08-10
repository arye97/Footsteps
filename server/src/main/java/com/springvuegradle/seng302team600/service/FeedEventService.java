package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.enumeration.FeedPostType;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.FeedEvent;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.FeedEventRepository;
import org.springframework.stereotype.Service;

/**
 * This service handles the creation of FeedEvent objects,
 * and saving them on the database via FeedEventRepository.
 */
@Service("feedEventService")
public class FeedEventService {

    private final FeedEventRepository feedEventRepository;

    public FeedEventService(FeedEventRepository feedEventRepository) {
        this.feedEventRepository = feedEventRepository;
    }

    /**
     * Performs a partial shallow copy of the given feed event.
     * Only attributes copied are timeStamp, feedEventType, activityId and AuthorId
     * @param masterFeedEvent the feed event to be copied
     * @return the new FeedEvent instance
     */
    private FeedEvent shallowCopy(FeedEvent masterFeedEvent) {
        FeedEvent feedEvent = new FeedEvent();
        feedEvent.setTimeStamp(masterFeedEvent.getTimeStamp());
        feedEvent.setFeedEventType(masterFeedEvent.getFeedEventType());
        feedEvent.setActivityId(masterFeedEvent.getActivityId());
        feedEvent.setAuthorId(masterFeedEvent.getAuthorId());
        return feedEvent;
    }

    /**
     * Helper function for modifyActivityEvent and deleteActivityEvent.
     */
    private void activityFeedEvent(Activity activity, FeedEvent masterFeedEvent) {
        FeedEvent feedEvent = shallowCopy(masterFeedEvent);

        // The following lines set viewerId in FeedEvent for each user requiring notification.
        // The creator gets notified
        feedEvent.setViewerId(activity.getCreatorUserId());
        feedEventRepository.save(feedEvent);

        // feedEvent is reset back to MasterModifyEvent to avoid updating last save during the next save.
        // In other words, the feedEventId is set back to null, avoiding an update.
        feedEvent = shallowCopy(masterFeedEvent);

        // The participants get notified
        for (User user : activity.getParticipants()) {
            feedEvent.setViewerId(user.getUserId());
            feedEventRepository.save(feedEvent);
            feedEvent = shallowCopy(masterFeedEvent);
        }
    }

    /**
     * Creates FeedEvent instances for each participant and the creator for the given activity.
     * These FeedEvents have the feed event type MODIFY.
     * All FeedEvents are saved to the FeedEventRepository.
     * This method assumes activity was modified successfully and should be called after this is known.
     * @param activity the activity being modified
     * @param authorId the user's id who is modifying the activity
     */
    public void modifyActivityEvent(Activity activity, Long authorId) {
        FeedEvent modifyEvent = new FeedEvent();
        modifyEvent.setTimeStampNow();
        modifyEvent.setFeedEventType(FeedPostType.MODIFY);
        modifyEvent.setActivityId(activity.getActivityId());
        modifyEvent.setAuthorId(authorId);

        // Call helper function to save feed event for each user requiring notification
        activityFeedEvent(activity, modifyEvent);
    }

    /**
     * Creates FeedEvent instances for each participant and the creator for the given activity.
     * These FeedEvents have the feed event type DELETE.
     * All FeedEvents are saved to the FeedEventRepository.
     * This method assumes activity will be deleted successfully and should be called just before deleting.
     * @param activity the activity being deleted
     * @param authorId the user's id who is deleting the activity
     */
    public void deleteActivityEvent(Activity activity, Long authorId) {
        FeedEvent deleteEvent = new FeedEvent();
        deleteEvent.setTimeStampNow();
        deleteEvent.setFeedEventType(FeedPostType.DELETE);
        deleteEvent.setActivityId(activity.getActivityId());
        deleteEvent.setAuthorId(authorId);

        // Call helper function to save feed event for each user requiring notification
        activityFeedEvent(activity, deleteEvent);
    }
}

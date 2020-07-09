package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.model.ActivityType;
import com.springvuegradle.seng302team600.repository.ActivityTypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

@Service("activityTypePersistService")
public class ActivityTypeService {

    private ActivityTypeRepository activityTypeRepository;

    public ActivityTypeService(ActivityTypeRepository activityTypeRepository) {
        this.activityTypeRepository = activityTypeRepository;
    }

    /**
     * Takes a Set<ActivityType> of entities not in the database, finds their equivalents in
     * the database by matching name, and returns a new Set<ActivityType> of entities from the database.
     * This way duplicate ActivityTypes are not created in the database.
     * @param detachedActivityTypes entities not saved in the database
     * @return entities saved in the database
     * @throws ResponseStatusException thrown if an entity in detachedActivityTypes does not match an entity in the database
     */
    public Set<ActivityType> getMatchingEntitiesFromRepository(final Set<ActivityType> detachedActivityTypes) throws ResponseStatusException {
        if (detachedActivityTypes == null) {return null;}

        Set<ActivityType> persistedActivityTypes = new HashSet<>(detachedActivityTypes.size());
        for (ActivityType activityType: detachedActivityTypes) {
            String name = activityType.getName();

            ActivityType repositoryEntity = activityTypeRepository.findActivityTypeByName(name);
            if (repositoryEntity == null) {
                throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, String.format(
                        "'%s' is not in the database, and therefore is not a valid ActivityType", name));
            }
            persistedActivityTypes.add(repositoryEntity);
        }
        return persistedActivityTypes;
    }


}

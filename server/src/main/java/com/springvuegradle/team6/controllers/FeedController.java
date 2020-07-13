package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.*;
import com.springvuegradle.team6.models.location.NamedLocationRepository;
import com.springvuegradle.team6.responses.FeedResponse;
import com.springvuegradle.team6.security.UserSecurityService;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

@CrossOrigin(
    origins = "http://localhost:9500",
    allowCredentials = "true",
    allowedHeaders = "://",
    methods = {
      RequestMethod.GET,
      RequestMethod.POST,
      RequestMethod.DELETE,
      RequestMethod.PUT,
      RequestMethod.PATCH
    })
@RestController
@RequestMapping("/feed")
public class FeedController {
  private final ProfileRepository profileRepository;
  private final ActivityRepository activityRepository;
  private final ActivityHistoryRepository activityHistoryRepository;
  private final SubscriptionHistoryRepository subscriptionHistoryRepository;

  FeedController(
      ProfileRepository profileRepository,
      ActivityRepository activityRepository,
      ActivityHistoryRepository activityHistoryRepository,
      SubscriptionHistoryRepository subscriptionHistoryRepository) {
    this.profileRepository = profileRepository;
    this.activityHistoryRepository = activityHistoryRepository;
    this.activityRepository = activityRepository;
    this.subscriptionHistoryRepository = subscriptionHistoryRepository;
  }

  /**
   * Get all the feed that is to be displayed onto the user home feed.
   * This includes all information all updates to activities that the user has subscribed.
   * @param profileId The profile that the feed belongs to
   * @param session The current logged in session
   * @return The resulting feed information
   */
  @GetMapping("/homefeed/{profileId}")
  private ResponseEntity getHomeFeed(@PathVariable Integer profileId, HttpSession session) {

    // Check if authorised
    ResponseEntity<String> authorisedResponse =
        UserSecurityService.checkAuthorised(profileId, session, profileRepository);
    if (authorisedResponse != null) {
      return authorisedResponse;
    }

    Set<SubscriptionHistory> subscriptionHistorySet =
        subscriptionHistoryRepository.findByProfile_id(profileId);
    List<FeedResponse> feeds = new ArrayList<>();
    if (subscriptionHistorySet != null) {
      for (SubscriptionHistory subscriptionHistory : subscriptionHistorySet) {
        List<ActivityHistory> activityHistories =
            activityHistoryRepository
                .getActivityHistoryBetweenSubscribeStartEndDateTimeAndActivityId(
                    subscriptionHistory.getActivity().getId(),
                    subscriptionHistory.getStartDateTime(),
                    subscriptionHistory.getEndDateTime());

        if (activityHistories != null) {
          for (ActivityHistory activityHistory : activityHistories) {
            FeedResponse feed =
                new FeedResponse(
                    activityHistory.getActivity().getId(),
                    activityHistory.getMessage(),
                    activityHistory.getTimeDate().toString());
            feeds.add(feed);
          }
        }

        FeedResponse feed =
            new FeedResponse(
                subscriptionHistory.getActivity().getId(),
                "Subscribed to the activity: "
                    + subscriptionHistory.getActivity().getActivityName()
                    + ".",
                subscriptionHistory.getStartDateTime().toString());
        feeds.add(feed);
        if (subscriptionHistory.getEndDateTime() != null) {
          FeedResponse feed1 =
              new FeedResponse(
                  subscriptionHistory.getActivity().getId(),
                  "Unsubscribed to the activity: "
                      + subscriptionHistory.getActivity().getActivityName()
                      + ".",
                  subscriptionHistory.getEndDateTime().toString());
          feeds.add(feed1);
        }
      }
    }
    // Sorts the feeds in descending order of dateTime
    feeds.sort((o1, o2) -> o2.dateTime.compareTo(o1.dateTime));

    Map<String, List<FeedResponse>> result = new HashMap<>();
    result.put("feeds", feeds);
    JSONObject obj = new JSONObject(result);
    return new ResponseEntity(obj, HttpStatus.OK);
  }
}

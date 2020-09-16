package com.springvuegradle.team6.models.entities;

import com.springvuegradle.team6.requests.CreateActivityRequest;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.SortableField;
import org.hibernate.search.annotations.Spatial;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
import org.hibernate.search.bridge.builtin.IntegerBridge;
import org.hibernate.search.bridge.builtin.impl.BuiltinIterableBridge;

@Indexed
@Entity
@AnalyzerDef(
    name = "activityAnalyzer",
    tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
    filters = {
        @TokenFilterDef(factory = LowerCaseFilterFactory.class),
        @TokenFilterDef(
            factory = EdgeNGramFilterFactory.class,
            params = {
                @Parameter(name = "minGramSize", value = "3"),
                @Parameter(name = "maxGramSize", value = "30")
            })
    })
public class Activity implements Serializable {

  // Constants
  public static final int NAME_MAX_LENGTH = 128;
  public static final int DESCRIPTION_MAX_LENGTH = 2048;
  private static final String LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

  /**
   * This constructor is used for testing purposes only
   */
  public Activity() {
    Set<ActivityType> myEmptySet = Collections.emptySet();
    this.profile = null;
    this.activityName = null;
    this.description = null;
    this.activityTypes = null;
    this.activityTypes = myEmptySet;
    this.continuous = true;
    this.visibilityType = VisibilityType.Public;
    this.creationDate = LocalDateTime.now();
  }

  public Activity(CreateActivityRequest request, Profile profile) {
    this.profile = profile;
    this.activityName = request.activityName;
    this.description = request.description;
    this.activityTypes = request.activityTypes;
    this.tags = request.hashTags;
    this.continuous = request.continuous;

    if (!this.continuous) {
      if (request.startTime != null) {
        this.startTime =
            LocalDateTime.parse(
                request.startTime, DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT));
      }
      if (request.endTime != null) {
        this.endTime =
            LocalDateTime.parse(
                request.endTime, DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT));
      }
    }
    if (request.location != null) {
      Location newLocation = new Location(request.location.latitude, request.location.longitude);
      this.setLocation(newLocation);
    }
    if (request.visibility != null) {
      setVisibilityTypeByString(request.visibility);
    } else {
      this.visibilityType = VisibilityType.Public;
    }
  }

  @Size(max = NAME_MAX_LENGTH)
  @Column(length = NAME_MAX_LENGTH, name = "activity_name")
  @Field(
      index = Index.YES,
      analyze = Analyze.YES,
      store = Store.NO,
      name = "activity_name",
      analyzer = @Analyzer(definition = "activityAnalyzer"))
  private String activityName;

  @Id
  @GeneratedValue
  @Column(name = "id")
  @SortableField
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false)
  @Field(analyze = Analyze.YES, store = Store.NO)
  @FieldBridge(impl = IntegerBridge.class)
  private Profile profile;

  @Size(max = DESCRIPTION_MAX_LENGTH)
  @Column(length = DESCRIPTION_MAX_LENGTH)
  private String description;

  @IndexedEmbedded
  @Field(analyze = Analyze.YES, store = Store.NO)
  @ElementCollection(targetClass = ActivityType.class)
  @Enumerated(EnumType.ORDINAL)
  private Set<ActivityType> activityTypes;

  @IndexedEmbedded
  @Field(analyze = Analyze.YES, store = Store.NO)
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "activity_tags",
      joinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
  @FieldBridge(impl = BuiltinIterableBridge.class)
  private Set<Tag> tags;

  @Field(analyze = Analyze.YES, store = Store.NO, name = "continuous")
  private boolean continuous;

  @Field(analyze = Analyze.YES, store = Store.NO)
  @SortableField
  @Column(columnDefinition = "datetime")
  private LocalDateTime startTime;

  @Field(analyze = Analyze.YES, store = Store.NO)
  @SortableField
  @Column(columnDefinition = "datetime")
  private LocalDateTime endTime;

  @Spatial
  @IndexedEmbedded(depth = 1)
  @SortableField
  @ManyToOne
  private Location location;

  @Column(columnDefinition = "datetime default NOW()")
  private LocalDateTime creationDate;

  /** Map activity id to user id to create profile_subscriptions table in database */
  @ManyToMany(mappedBy = "subscriptions", fetch = FetchType.LAZY)
  private Collection<Profile> subscribers;

  @Column(columnDefinition = "boolean default false")
  private boolean archived;

  @OneToMany(mappedBy = "activity", orphanRemoval = true)
  @Field(analyze = Analyze.YES, store = Store.NO)
  @IndexedEmbedded
  @FieldBridge(impl = BuiltinIterableBridge.class)
  private List<ActivityRole> activityRole;

  @Enumerated(EnumType.ORDINAL)
  @Field(analyze = Analyze.YES, store = Store.NO, name = "visibility")
  private VisibilityType visibilityType;

  @OneToMany(mappedBy = "activity")
  private List<ActivityQualificationMetric> activityQualificationMetrics;

  public String getActivityName() {
    return activityName;
  }

  public void setActivityName(String activityName) {
    this.activityName = activityName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Set<ActivityType> getActivityTypes() {
    return activityTypes;
  }

  public void setActivityTypes(Set<ActivityType> activityTypes) {
    this.activityTypes = activityTypes;
  }

  public boolean isContinuous() {
    return continuous;
  }

  public void setContinuous(boolean continuous) {
    this.continuous = continuous;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public void setStartTimeByString(String startTime) {
    this.startTime =
        LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT));
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public void setEndTimeByString(String endTime) {
    this.endTime =
        LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT));
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Profile getProfile() {
    return profile;
  }

  public void setProfile(Profile profile) {
    this.profile = profile;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  public Set<Tag> getTags() {
    return tags;
  }

  public void setTags(Set<Tag> tags) {
    this.tags = tags;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public VisibilityType getVisibilityType() {
    return this.visibilityType;
  }

  public void setVisibilityTypeByString(String type) {
    String toCamelCase = type.substring(0, 1).toUpperCase() + type.substring(1);
    this.visibilityType = VisibilityType.valueOf(toCamelCase);
  }

  public void setVisibilityType(VisibilityType visibilityType) {
    this.visibilityType = visibilityType;
  }

  public List<ActivityQualificationMetric> getMetrics() {
    return this.activityQualificationMetrics;
  }

  public void setMetrics(List<ActivityQualificationMetric> metrics) {
    this.activityQualificationMetrics = metrics;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Activity activity = (Activity) o;

    return this.id.equals(activity.getId());
  }

  @Override
  public int hashCode() {
    return this.id;
  }

  public Collection<Profile> getSubscribers() {
    return subscribers;
  }

  public void setSubscribers(Collection<Profile> subscribers) {
    this.subscribers = subscribers;
  }

  public void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
  }
}

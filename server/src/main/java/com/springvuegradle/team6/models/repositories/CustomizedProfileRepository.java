package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.Profile;
import java.util.List;

public interface CustomizedProfileRepository {
  List<Profile> searchFullname(String terms, String activityType, String method, int limit, int offset);

  Integer searchFullnameCount(String terms, String activityType, String method);

  List<Profile> searchNickname(String terms, String activityType, String method, int limit, int offset);

  Integer searchNicknameCount(String terms, String activityType, String method);
}

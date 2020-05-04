package com.springvuegradle.team6.models;

import java.util.List;

public interface CustomizedProfileRepository {
  List<Profile> searchFullname(String terms, String activityType, int limit, int offset);

  Integer searchFullnameCount(String terms, String activityType);

  List<Profile> searchNickname(String terms, int limit, int offset);

  Integer searchNicknameCount(String terms);
}

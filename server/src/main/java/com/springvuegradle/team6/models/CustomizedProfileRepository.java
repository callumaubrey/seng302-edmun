package com.springvuegradle.team6.models;

import java.util.List;

public interface CustomizedProfileRepository {
  List<Profile> searchFullname(String terms, int limit, int offset);

  Integer searchFullnameCount(String terms);

  List<Profile> searchNickname(String terms, int limit, int offset);

  Integer searchNicknameCount(String terms);
}

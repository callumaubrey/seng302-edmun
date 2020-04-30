package com.springvuegradle.team6.models;

import java.util.List;

public interface CustomizedProfileRepository {
    List<Profile> searchFullname(String terms, int limit, int offset);
}

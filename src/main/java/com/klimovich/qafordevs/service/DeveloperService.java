package com.klimovich.qafordevs.service;

import com.klimovich.qafordevs.entity.DeveloperEntity;

import java.util.List;

public interface DeveloperService {
    DeveloperEntity saveDeveloper(DeveloperEntity developer);

    DeveloperEntity updateDeveloper(DeveloperEntity developer);

    DeveloperEntity getDeveloperById(Integer id);

    DeveloperEntity getDeveloperByEmail(String email);

    List<DeveloperEntity> getAllDevelopers();

    List<DeveloperEntity> getAllActiveBySpeciality(String speciality);

    void softDeleteById(Integer id);

    void hardDeleteById(Integer id);

}

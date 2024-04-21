package com.klimovich.qafordevs.service;

import com.klimovich.qafordevs.entity.DeveloperEntity;
import com.klimovich.qafordevs.exception.DeveloperNotFoundException;
import com.klimovich.qafordevs.exception.DeveloperWithDuplicateEmailException;
import com.klimovich.qafordevs.repository.DeveloperRepository;
import com.klimovich.qafordevs.util.DataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeveloperServiceImplTest {
    @Mock
    private DeveloperRepository developerRepository;
    @InjectMocks
    private DeveloperServiceImpl serviceUnderTest;

    @Test
    @DisplayName("Test save developer functionality")
    public void givenDeveloperToSave_whenSaveDeveloper_thenRepositoryIsCalled() {
        //given
        DeveloperEntity developerToSave = DataUtils.getJohnDoeTransient();
        BDDMockito.given(developerRepository.findByEmail(anyString())).willReturn(null);
        BDDMockito.given(developerRepository.save(any(DeveloperEntity.class))).willReturn(DataUtils.getJohnDoePersisted());
        //when
        DeveloperEntity savedDeveloper = serviceUnderTest.saveDeveloper(developerToSave);
        //then
        assertThat(savedDeveloper).isNotNull();
    }

    @Test
    @DisplayName("Test save developer with duplicate email functionality")
    public void givenDeveloperToSaveWithDuplicateEmail_whenSaveDeveloper_thenExceptionIsThrown() {
        //given
        DeveloperEntity developerToSave = DataUtils.getJohnDoeTransient();
        BDDMockito.given(developerRepository.findByEmail(anyString())).willReturn(DataUtils.getJohnDoePersisted());
        //when
        assertThrows(DeveloperWithDuplicateEmailException.class, () -> serviceUnderTest.saveDeveloper(developerToSave));
        //then
        verify(developerRepository, never()).save(any(DeveloperEntity.class));
    }

    @Test
    @DisplayName("Test update developer functionality")
    public void givenDeveloperToUpdate_whenUpdateDeveloper_thenRepositoryIsCalled() {
        //given
        DeveloperEntity developerToUpdate = DataUtils.getJohnDoePersisted();
        BDDMockito.given(developerRepository.existsById(anyInt())).willReturn(true);
        BDDMockito.given(developerRepository.save(any(DeveloperEntity.class))).willReturn(developerToUpdate);
        //when
        DeveloperEntity updatedDeveloper = serviceUnderTest.updateDeveloper(developerToUpdate);
        //then
        assertThat(updatedDeveloper).isNotNull();
        verify(developerRepository, times(1)).save(any(DeveloperEntity.class));
    }


    @Test
    @DisplayName("Test update developer with incorrect id functionality")
    public void givenDeveloperToUpdateWithIncorrectId_whenUpdateDeveloper_thenExceptionIsThrown() {
        //given
        DeveloperEntity developerToUpdate = DataUtils.getJohnDoePersisted();
        BDDMockito.given(developerRepository.existsById(anyInt())).willReturn(false);
        //when
        assertThrows(DeveloperNotFoundException.class, () -> serviceUnderTest.updateDeveloper(developerToUpdate));
        //then
        verify(developerRepository, never()).save(any(DeveloperEntity.class));
    }

    @Test
    @DisplayName("Test get developer by id functionality")
    public void givenId_whenGetDeveloperById_thenDeveloperIsReturned() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt())).willReturn(Optional.of(DataUtils.getJohnDoePersisted()));
        //when
        DeveloperEntity obtainedDeveloper = serviceUnderTest.getDeveloperById(1);
        //then
        assertThat(obtainedDeveloper).isNotNull();
    }

    @Test
    @DisplayName("Test get developer by incorrect id functionality")
    public void givenIncorrectId_whenGetById_thenExceptionIsThrown() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt())).willThrow(DeveloperNotFoundException.class);
        //when
        assertThrows(DeveloperNotFoundException.class, () -> serviceUnderTest.getDeveloperById(1));
        //then
    }

    @Test
    @DisplayName("Test get developer by email functionality")
    public void givenEmail_whenGetDeveloperByEmail_thenDeveloperIsReturned() {
        //given
        String email = "john.doe@mail.com";
        BDDMockito.given(developerRepository.findByEmail(anyString())).willReturn((DataUtils.getJohnDoePersisted()));
        //when
        DeveloperEntity obtainedDeveloper = serviceUnderTest.getDeveloperByEmail(email);
        //then
        assertThat(obtainedDeveloper).isNotNull();
    }

    @Test
    @DisplayName("Test get developer by incorrect email functionality")
    public void givenIncorrectEmail_whenGetByEmail_thenExceptionIsThrown() {
        //given
        BDDMockito.given(developerRepository.findByEmail(anyString())).willThrow(DeveloperNotFoundException.class);
        //when
        assertThrows(DeveloperNotFoundException.class, () -> serviceUnderTest.getDeveloperByEmail(""));
        //then
    }

    @Test
    @DisplayName("Test get all developer functionality")
    public void givenThreeDevelopers_whenGetAll_thenOnlyActiveDevelopersAreReturned() {
        //given
        DeveloperEntity developer1 = DataUtils.getJohnDoePersisted();
        DeveloperEntity developer2 = DataUtils.getMikeSmithPersisted();
        DeveloperEntity developer3 = DataUtils.getFrankJonesPersisted();
        List<DeveloperEntity> developers = List.of(developer1, developer2, developer3);
        BDDMockito.given(developerRepository.findAll()).willReturn(developers);
        //when
        List<DeveloperEntity> allDevelopers = serviceUnderTest.getAllDevelopers();
        //then
        assertThat(CollectionUtils.isEmpty(allDevelopers)).isFalse();
        assertThat(allDevelopers.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test get all active developer by speciality functionality")
    public void givenThreeDevelopersAndTwoActive_whenGetAllActiveBySpeciality_thenDevelopersAreReturned() {
        //given
        DeveloperEntity developer1 = DataUtils.getJohnDoePersisted();
        DeveloperEntity developer2 = DataUtils.getMikeSmithPersisted();
        List<DeveloperEntity> developers = List.of(developer1, developer2);
        BDDMockito.given(developerRepository.findAllActiveBySpecialty(anyString())).willReturn(developers);
        //when
        List<DeveloperEntity> allDevelopers = serviceUnderTest.getAllActiveBySpeciality("Java");
        //then
        assertThat(CollectionUtils.isEmpty(allDevelopers)).isFalse();
        assertThat(allDevelopers.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test soft delete developer by id functionality")
    public void givenId_whenSoftDeleteById_thenRepositorySaveMethodIsCalled() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt())).willReturn(Optional.of(DataUtils.getJohnDoePersisted()));
        //when
        serviceUnderTest.softDeleteById(1);
        //then
        verify(developerRepository, times(1)).save(any(DeveloperEntity.class));
        verify(developerRepository, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("Test soft delete developer by incorrect id functionality")
    public void givenIncorrectId_whenSoftDeleteById_thenExceptionIsThrown() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt())).willReturn(Optional.empty());
        //when
        assertThrows(DeveloperNotFoundException.class,() -> serviceUnderTest.softDeleteById(1));
        //then
        verify(developerRepository, never()).save(any(DeveloperEntity.class));
    }

    @Test
    @DisplayName("Test hard delete developer by id functionality")
    public void givenId_whenHardDeleteById_thenRepositoryDeleteMethodIsCalled() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt())).willReturn(Optional.of(DataUtils.getJohnDoePersisted()));
        //when
        serviceUnderTest.hardDeleteById(1);
        //then
        verify(developerRepository, times(1)).deleteById(anyInt());
    }
    @Test
    @DisplayName("Test hard delete developer by incorrect id functionality")
    public void givenIncorrectId_whenHardDeleteById_thenExceptionIsThrown() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt())).willReturn(Optional.empty());
        //when
        assertThrows(DeveloperNotFoundException.class,() -> serviceUnderTest.hardDeleteById(1));
        //then
        verify(developerRepository, never()).deleteById(anyInt());
    }

}






















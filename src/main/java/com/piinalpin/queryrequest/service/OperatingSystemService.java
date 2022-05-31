package com.piinalpin.queryrequest.service;

import com.piinalpin.queryrequest.domain.common.query.SearchRequest;
import com.piinalpin.queryrequest.domain.common.query.SearchSpecification;
import com.piinalpin.queryrequest.domain.entity.User;
import com.piinalpin.queryrequest.domain.entity.OperatingSystem;
import com.piinalpin.queryrequest.repository.UserRepository;
import com.piinalpin.queryrequest.repository.OperatingSystemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
public class OperatingSystemService {

    @Autowired
    private OperatingSystemRepository operatingSystemRepository;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        OperatingSystem os1 = OperatingSystem.builder()
                .kernel("Linux Kernel")
                .name("ubuntu")
                .version("16.1")
                .releaseDate(LocalDateTime.now())
                .usages(1)
                .build();
        OperatingSystem os2 = OperatingSystem.builder()
                .kernel("Windows Kernel")
                .name("Window")
                .version("11")
                .releaseDate(LocalDateTime.now())
                .usages(1)
                .build();
        List<OperatingSystem> listToInit = Arrays.asList(os1, os2);
        User user = User.builder()
                .name("test_customer")
                .operatingSystems(new HashSet<>(Arrays.asList(os1, os2)))
                .build();
//        operatingSystemRepository.saveAll(listToInit);
        userRepository.save(user);
        System.out.println(operatingSystemRepository.findById(1L).get().getUser());
    }

    public Page<OperatingSystem> searchOperatingSystem(SearchRequest request) {
        SearchSpecification<OperatingSystem> specification = new SearchSpecification<>(request);
        Pageable pageable = SearchSpecification.getPageable(request.getPage(), request.getSize());
        return operatingSystemRepository.findAll(specification, pageable);
    }

}

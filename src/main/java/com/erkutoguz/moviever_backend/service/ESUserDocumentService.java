package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.model.UserDocument;
import com.erkutoguz.moviever_backend.repository.UserDocumentRepository;
import com.erkutoguz.moviever_backend.util.UserDocumentMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ESUserDocumentService {

    private final UserDocumentRepository userDocumentRepository;

    public ESUserDocumentService(UserDocumentRepository userDocumentRepository) {
        this.userDocumentRepository = userDocumentRepository;
    }

    public void insertUserDocument(UserDocument userDocument) {
        userDocumentRepository.save(userDocument);
    }

    public void insertMultipleUserDocuments(List<UserDocument> userDocumentList) {
        userDocumentRepository.saveAll(userDocumentList);
    }

    //TODO cache at buralara da
    public Map<String, Object> searchUsersAutoSuggest(String partialInput, int page, int size) throws IOException {
        Pageable pageable = PageRequest.of(page, size);
        Map<String, Object> map = new HashMap<>();
        Page<UserDocument> userDocuments = userDocumentRepository
                .searchByUsernameOrEmailOrFirstNameOrLastName(partialInput, pageable);

        map.put("totalItems", userDocuments.getTotalElements());
        map.put("totalPages", userDocuments.getTotalPages());
        map.put("users", UserDocumentMapper.mapUserDocument(userDocuments.getContent()));
        return map;
    }

}

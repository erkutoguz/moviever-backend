package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.response.AdminIpAddressesResponse;
import com.erkutoguz.moviever_backend.dto.response.IpAddressResponse;
import com.erkutoguz.moviever_backend.model.IpAddress;
import com.erkutoguz.moviever_backend.repository.IpAddressRepository;
import com.erkutoguz.moviever_backend.util.IpAddressMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class IpAddressService {
    @Value("${spring.ip-info.token}")
    private String ipInfoToken;

    private final RestClient restClient;
    private final IpAddressRepository ipAddressRepository;
    public IpAddressService(RestClient restClient,
                            IpAddressRepository ipAddressRepository) {
        this.restClient = restClient;
        this.ipAddressRepository = ipAddressRepository;
    }

    public IpAddressResponse extractIpAddressInformation(String ipAddress) {
        if(ipAddress.startsWith("0:0:0")) {
            return new IpAddressResponse("0.0.0.0","Antalya","Mediterrian", "Turkey", "36.884804,30.704044", "Turkey Internet Provider", "07010", "UTC");
        }

        String url = UriComponentsBuilder.fromHttpUrl("https://ipinfo.io")
                .pathSegment(ipAddress)
                .queryParam("token", ipInfoToken)
                .build()
                .toUriString();

        return restClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(IpAddressResponse.class);
    }


    public List<AdminIpAddressesResponse> retrieveIpAddresses() {
        List<IpAddress> ipAddresses = ipAddressRepository.findAll();
        return IpAddressMapper.map(ipAddresses);
    }
}

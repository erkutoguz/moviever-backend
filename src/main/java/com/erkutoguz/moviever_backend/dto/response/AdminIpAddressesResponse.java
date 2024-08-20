package com.erkutoguz.moviever_backend.dto.response;

import java.util.List;

public record AdminIpAddressesResponse(String ip, int userCount, String city, String region,
                                       String country, List<String> loc,
                                       String org, String postal, String timezone) {
}

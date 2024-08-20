package com.erkutoguz.moviever_backend.dto.response;

public record IpAddressResponse(String ip, String city, String region,
                                String country, String loc,
                                String org, String postal, String timezone) {
}

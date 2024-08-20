package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.dto.response.AdminIpAddressesResponse;
import com.erkutoguz.moviever_backend.dto.response.IpAddressResponse;
import com.erkutoguz.moviever_backend.model.IpAddress;

import java.util.Arrays;
import java.util.List;

public interface IpAddressMapper {

    static IpAddress map(IpAddressResponse response) {
        IpAddress ipAddress =  new IpAddress();
        ipAddress.setCity(response.city());
        ipAddress.setIp(response.ip());
        ipAddress.setCountry(response.country());
        ipAddress.setLoc(Arrays.stream(response.loc().split(",")).toList());
        ipAddress.setOrg(response.org());
        ipAddress.setPostal(response.postal());
        ipAddress.setRegion(response.region());
        ipAddress.setTimezone(response.timezone());
        return ipAddress;
    }

    static AdminIpAddressesResponse map(IpAddress ipAddress) {
        return new AdminIpAddressesResponse(ipAddress.getIp(),ipAddress.getUsers().size(),
                ipAddress.getCity(), ipAddress.getRegion(), ipAddress.getCountry(),
                ipAddress.getLoc(), ipAddress.getOrg(), ipAddress.getPostal(),ipAddress.getTimezone());

    }

    static List<AdminIpAddressesResponse> map(List<IpAddress> list) {
        return list.stream().map(IpAddressMapper::map).toList();
    }
}

package com.francis.bookshop.service;

import com.francis.bookshop.dto.UserDto;

public interface MFAService {

    String provisioningUri(UserDto user);
}

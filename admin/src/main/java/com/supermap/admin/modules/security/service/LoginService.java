package com.supermap.admin.modules.security.service;

import com.supermap.admin.modules.security.vo.RouteVO;
import com.supermap.admin.modules.sys.dto.UserLoginDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author gzw
 */
public interface LoginService {

    String login(UserLoginDTO user, HttpServletRequest request);

    void logout(String token);

    List<RouteVO> getLoginUserRoute(Long userId);

}

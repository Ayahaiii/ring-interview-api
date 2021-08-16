package com.monetware.ringinterview.business.controller.user;

import com.monetware.ringinterview.business.pojo.dto.user.UserPermissionDTO;
import com.monetware.ringinterview.business.pojo.dto.user.UserSearchDTO;
import com.monetware.ringinterview.business.pojo.vo.user.UserListVO;
import com.monetware.ringinterview.business.pojo.vo.user.UserPermissionVO;
import com.monetware.ringinterview.business.pojo.vo.user.UserSearchVO;
import com.monetware.ringinterview.business.service.user.UserService;
import com.monetware.ringinterview.system.base.PageList;
import com.monetware.ringinterview.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Simo
 * @date 2020-02-17
 */
@Api("用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("permission")
    @ApiOperation(value = "获取用户权限")
    public ResultData<UserPermissionDTO> getUserPermission(){
        return new ResultData<>("获取成功", userService.getUserPermission());
    }

    @PostMapping("find")
    @ApiOperation(value = "查询用户")
    public ResultData<UserSearchDTO> searchUser(@RequestBody UserSearchVO userSearchVO){
        return new ResultData<>("查询成功", userService.searchUser(userSearchVO));
    }

    @PostMapping("permission/update/expose")
    @ApiOperation(value = "修改用户权限")
    public ResultData<Integer> updateUserPermission(@RequestBody UserPermissionVO userPermission){
        return new ResultData<>("修改成功", userService.updateUserPermission(userPermission));
    }

    @PostMapping("find/expose")
    @ApiOperation(value = "查询用户")
    public ResultData<PageList> getUserList(@RequestBody UserListVO listVO){
        return new ResultData<>("查询成功", userService.getUserList(listVO));
    }

}

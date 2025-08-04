package com.yuhao.smarteasybuild.controller;


import com.yuhao.smarteasybuild.common.BaseResponse;
import com.yuhao.smarteasybuild.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckController {
    @GetMapping("/")
    public BaseResponse<String> healthCheck() {
        return ResultUtils.success("ok");
    }
}

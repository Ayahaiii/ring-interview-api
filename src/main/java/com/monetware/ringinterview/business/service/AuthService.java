package com.monetware.ringinterview.business.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monetware.ringinterview.system.base.ErrorCode;
import com.monetware.ringinterview.system.base.ResultData;
import com.monetware.ringinterview.system.exception.ServiceException;
import com.monetware.threadlocal.ThreadLocalManager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author Simo
 * @date 2019-07-24
 */
@Slf4j
@Service
public class AuthService {

    /**
     * 请求外部链接
     * 获取所有项目
     * @return
     */
    public Object getResponseResult(Map<String, Object> params, String url){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        //  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setBearerAuth(ThreadLocalManager.getToken());
        //  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        ObjectMapper mapper = new ObjectMapper();
        String value = null;
        try {
            value = mapper.writeValueAsString(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(value, headers);
        ResponseEntity<ResultData> response = restTemplate.postForEntity(url, requestEntity, ResultData.class);
        ResultData<Object> resultData = response.getBody();
        if (resultData.getCode() != 0) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, resultData.getMessage());
        }
        return resultData.getData();
    }

}

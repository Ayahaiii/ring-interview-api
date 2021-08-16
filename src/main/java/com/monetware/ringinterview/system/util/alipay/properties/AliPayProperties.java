package com.monetware.ringinterview.system.util.alipay.properties;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 *
 * @Author: zq
 * @Date: 2019/2/15 17:33
 * @Description: 支付宝支付相关属性
 *
 */
@Component
@Data
public class AliPayProperties {

    /**
     * APPID(支付宝后台自动生成)
     */
    private String appId = "2018112862390082";

    /**
     * 支付地址/支付宝网关（固定）
     */
    private String tradeUrl = "https://openapi.alipay.com/gateway.do";

    /**
     * 私钥（开发者自己生成）
     */
    private String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCCGDT/jJqgNa12cyze5PJ36Hh7c+rhNcuYb45Nx3d+kgIjF7aMg1k4CjWDmennufqBvma9HsVRYprI5++wNAfWx5WmLW6+Ec959gnbQNtMdC1zzlTfdn9Td0rtpzJXwRro57aav79bqkzIL1Vo2kcHT0rI9v1NbZwWIjnBUViK2EgX0ZCLBXhdti9aW/YxzD3kU0SYjjNTsao3HtDWxp4AjmK627gwVDGbHGmbDMD0BPi9lELDBiKlyTVvWI/27S3mSImMuImSwjpcnZ1KVEtHYT7tXt/uZ/t/fAj7mcQaJiWH4cM8DVT6lQvQAbYg1Zs9Nx7ORZZdh7V/wr5vp9wNAgMBAAECggEASRd6p12C/qJJ7DP3sN+nDxGQp6PQAmpsEiATLsXz8CvugXLwQHAHr4Gk4WSXU3ddNIoaRQVFzVz+JAST2UbSZKzL+oqqHqX5EMfYb9Q3ofPgGaGCc+6qyOVMNsvqb/1xtguBv94dkE3VR9+4N+nzYDr/hHuog1kjChh+pIukmJToH2GYLzMXaujcZnB4DHKphFQDvEFjH70PxV5gNDoQouMbeXEt8Ooyq0UbdcgH60jFbmICYXXbLZOKELf1KY6h8w8wow1M3m+t3Aaq4O9u5cpyfNlLa+S67wWAFi+JscB6oI6RnwjmXdZSkcJ6tCP2S3ZcQff5pLwgw7/4nPmNIQKBgQDy88wA8ZSQ53rnSwT7cQl2UwXfHB/IIj81qLxT7wRNT4uN8uqvcEttgx/7b8cftjMGCaiJ6UEzk+4Q+CyFeLCf1jmd+nKGaLVM0IrhCScMB8w4MkhDi7w6rDbXSMcGaYaHMrZ58eYDk2611NH8VMYlDDzzpUaUC9SK+mokyCawxQKBgQCJFMwvPQhgCjxQtPGNYD9c1QCq58q0TGMPDYgx0EO1y7tEGfvtGWMzZNWNtOMvCyi0OeEprGb3NUuPj5URLYon49+zWCoa+L06jkJzj7tC114uv2aB/DsduFcMK7sRgD8WKqqwy4tATvT/ahRPAi5wcg/+nqqfJJmpILPn1EciqQKBgQCqLKybiJvIt+t1sS2HNhErqVG2iouUcrCV1GA95nC7+ljFD/7k7iJXTf6L7yqqAsRQmVlNaG38Q8UAa+U7hGMrQjHPf0rS7Or5KCXvJKsOGTQptMa7UViCPHiZaWfHDQlnD9WTKyYzIQTuJmkDdxnnf1/+RalfL74rruXEZ9H6BQKBgAkhTnwy93TWGbBSbOg6qd23/iRj7ddWYx6kixauRWQ9XoqppGLlVvscUzDuBFekyaA/XqTU68K5VQAJ0ybivpnrjoG2dI95U7krsndZ8mIAUDCS5PPxZcqT9ZBE9V+0B/+k9dtyoyK0X5MHcLTWc1tf7XrK/Jg+6tEd8X2WYOqJAoGARSI6aGq1XJuTxbVxNtCsCfQgxUYS9OyPXauKP3IC27OhV8e4BKSP/IhRNdPVJcFk802kS6sPas5teQH/3Yo007RY/rxrvcnXllzbqZ8Z6vlxQ8OMgE+vckEp5+X89b3oyALxRL8xzvCj/s6+VP8iBwVmbOs588WUaWecibelKAU=";

    /**
     * 公钥（支付宝生成）
     */
    private String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqiQioQS/Reryql6m4la47VZvFqKDjBqqZvrxMIeNz/1mRp+Xw/0klztIphILdQkHZ4pz4g2r3NGXyJ0jzUheddflzeiMKLQMeQWUxgIDZBWY0dDm2hT7Syxn4gWzTdn/L2khB31pl4rkFkB0OyiF8/L521RgQT5dyV3V1agi+Gyk4AzFd8d0N4IXcyexdMBmv8o7dw7R5tXI9CojhCPafvhrrB/QbWCcp2XffEZXoAnRve9v+EDCKEK/eRWW5u+0L7HXualYJVYbRcbpsC8Or1OrkBstF4AY+JU+jBkAzPtvhs5Li9jSzzkpWAWq2sEg5FWGHw8mU7keSgFwLF9SYQIDAQAB";

    /**
     * 编码集，支持GBK/UTF-8
     */
    private String charset = "utf-8";

    /**
     * 	商户生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2
     */
    private String signType = "RSA2";

    /**
     * 	参数返回格式，只支持json
     */
    private String format = "json";

}

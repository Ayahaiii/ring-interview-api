package com.monetware.ringinterview.system.util.lfasr;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.ProgressStatus;
import com.monetware.ringinterview.system.base.ErrorCode;
import com.monetware.ringinterview.system.exception.ServiceException;

import java.util.HashMap;

/**
 * @author Simo
 * @date 2020-02-28
 */
public class LfasrUtil {

    private static LfasrUtil lfasrUtil;

    private static LfasrClientImp lc = null;

    /**
     * 16K标准版\-已录制音频，支持格式wav,flac,opus,mp3,m4a
     */
    private static final LfasrType type = LfasrType.LFASR_STANDARD_RECORDED_AUDIO;

    /**
     * 等待时长（秒）
     */
    private static int sleepSecond = 20;

    private LfasrUtil() {
        try {
            lc = LfasrClientImp.initLfasrClient();
        } catch (LfasrException e) {
            // 初始化异常，解析异常描述信息
            Message initMsg = JSON.parseObject(e.getMessage(), Message.class);
            System.out.println("ecode=" + initMsg.getErr_no());
            System.out.println("failed=" + initMsg.getFailed());
        }
    }

    /**
     * 饿汉线程安全
     * @return 返回实例对象
     */
    public static synchronized LfasrUtil getLfasrUtil() {
        if (lfasrUtil == null){
            lfasrUtil = new LfasrUtil();
        }
        return lfasrUtil;
    }

    /**
     * 转换录音数据
     * @param filePath
     * @return
     */
    public JSONArray convertDataToJSON(String filePath,Integer number) {
        // 获取上传任务ID
        String task_id = "";
        HashMap<String, String> params = new HashMap<>();
        // 转写结果是否包含分词信息
        params.put("has_participle", "false");
        // 转写结果中是否包含发音人分离信息
        params.put("has_seperate", "true");
        // 发音人个数，可选值：0-10，0表示盲分
        params.put("speaker_number", number.toString());
        try {
            // 上传音频文件
            Message uploadMsg = lc.lfasrUpload(filePath, type, params);

            // 判断返回值
            int ok = uploadMsg.getOk();
            if (ok == 0) {
                // 创建任务成功
                task_id = uploadMsg.getData();
                System.out.println("task_id=" + task_id);
            } else {
                // 创建任务失败-服务端异常
                throw new ServiceException(ErrorCode.CUSTOM_MSG, uploadMsg.getFailed());
            }
        } catch (LfasrException e) {
            // 上传异常，解析异常描述信息
            Message uploadMsg = JSON.parseObject(e.getMessage(), Message.class);
            throw new ServiceException(ErrorCode.CUSTOM_MSG, uploadMsg.getFailed());
        }

        // 循环等待音频处理结果
        while (true) {
            try {
                // 等待20s在获取任务进度
                Thread.sleep(sleepSecond * 1000);
                System.out.println("waiting ...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                // 获取处理进度
                Message progressMsg = lc.lfasrGetProgress(task_id);

                // 如果返回状态不等于0，则任务失败
                if (progressMsg.getOk() != 0) {
                    throw new ServiceException(ErrorCode.CUSTOM_MSG, progressMsg.getFailed());
                } else {
                    ProgressStatus progressStatus = JSON.parseObject(progressMsg.getData(), ProgressStatus.class);
                    if (progressStatus.getStatus() == 9) {
                        // 处理完成
                        break;
                    } else {
                        // 未处理完成
                        continue;
                    }
                }
            } catch (LfasrException e) {
                // 获取进度异常处理，根据返回信息排查问题后，再次进行获取
                Message progressMsg = JSON.parseObject(e.getMessage(), Message.class);
                throw new ServiceException(ErrorCode.CUSTOM_MSG, progressMsg.getFailed());
            }
        }

        JSONArray result = null;
        // 获取任务结果
        try {
            Message resultMsg = lc.lfasrGetResult(task_id);
            // 如果返回状态等于0，则获取任务结果成功
            if (resultMsg.getOk() == 0) {
                // 打印转写结果
                result = JSON.parseArray(resultMsg.getData());
            } else {
                // 获取任务结果失败
                throw new ServiceException(ErrorCode.CUSTOM_MSG, resultMsg.getFailed());
            }
        } catch (LfasrException e) {
            // 获取结果异常处理，解析异常描述信息
            Message resultMsg = JSON.parseObject(e.getMessage(), Message.class);
            throw new ServiceException(ErrorCode.CUSTOM_MSG, resultMsg.getFailed());
        }
        return result;
    }

}

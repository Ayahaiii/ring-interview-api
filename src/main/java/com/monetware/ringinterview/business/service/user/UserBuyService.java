package com.monetware.ringinterview.business.service.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.monetware.ringinterview.business.dao.BuyRecordDao;
import com.monetware.ringinterview.business.dao.PayOrderDao;
import com.monetware.ringinterview.business.dao.PermissionDao;
import com.monetware.ringinterview.business.dao.UserDao;
import com.monetware.ringinterview.business.pojo.constant.PayConstant;
import com.monetware.ringinterview.business.pojo.constant.UserConstants;
import com.monetware.ringinterview.business.pojo.dto.user.UserOrderDTO;
import com.monetware.ringinterview.business.pojo.po.BaseUser;
import com.monetware.ringinterview.business.pojo.po.payOrder.BaseBuyRecord;
import com.monetware.ringinterview.business.pojo.po.payOrder.BasePayOrder;
import com.monetware.ringinterview.business.pojo.vo.user.UserBuyVO;
import com.monetware.ringinterview.business.service.AuthService;
import com.monetware.ringinterview.system.base.ErrorCode;
import com.monetware.ringinterview.system.exception.ServiceException;
import com.monetware.ringinterview.system.util.alipay.CPayUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Simo
 * @date 2019-04-17
 */
@Slf4j
@Service
public class UserBuyService {

    @Autowired
    private PayOrderDao payOrderDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BuyRecordDao buyRecordDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private PermissionDao permissionDao;

    @Value("${outurl.authCreateOrderUrl}")
    private String authCreateOrderUrl;

    @Value("${outurl.authGetBalanceUrl}")
    private String authGetBalanceUrl;

    /**
     * ??????????????????
     * @return
     */
    public int insertOrder(UserBuyVO userBuyVO) {
        BasePayOrder rdPayOrder = new BasePayOrder();
        Date now = new Date();
        //??????????????????
        BaseUser user = userDao.selectByPrimaryKey(ThreadLocalManager.getUserId());
        //????????????????????????????????????????????????
        if(userBuyVO.getType() < user.getRole()){
            throw new ServiceException(ErrorCode.CUSTOM_MSG,"???????????????????????????????????????????????????");
        }
        //??????????????????
        double price = permissionDao.selectByPrimaryKey(userBuyVO.getType()).getPrice();
        Map<String, Object> extraData = new HashMap<>();
        if (userBuyVO.getType().equals(UserConstants.ROLE_VIP)) {
            extraData.put("msg", "????????????????? ???????????????");
        } else if (userBuyVO.getType().equals(UserConstants.ROLE_CONPANY)) {
            extraData.put("msg", "????????????????? ???????????????");
        }
        extraData.put("vipType", userBuyVO.getType());
        rdPayOrder.setOutTradeNo(CPayUtil.getTradeNo());
        rdPayOrder.setAmount(new BigDecimal(price));
        rdPayOrder.setType(userBuyVO.getType());
        rdPayOrder.setPayWay(PayConstant.PAY_WAY_BALANCE);
        rdPayOrder.setPayType(PayConstant.PAY_TYPE_EXPEND);
        rdPayOrder.setUserId(ThreadLocalManager.getUserId());
        rdPayOrder.setStatus(PayConstant.STATUS_NO_PAY);
        rdPayOrder.setUpdateTime(now);
        rdPayOrder.setCreateTime(now);
        rdPayOrder.setExtraData(JSONObject.toJSONString(extraData));
        payOrderDao.insertSelective(rdPayOrder);
        return rdPayOrder.getId();
    }

    /**
     * ????????????
     * @param id
     * @return
     */
    public List<UserOrderDTO> getOrder(Integer id) {
        List<UserOrderDTO> result = new ArrayList<>();
        UserOrderDTO orderDTO = new UserOrderDTO();
        orderDTO.setBalance(this.getUserBalance());
        BaseUser user = userDao.selectByPrimaryKey(ThreadLocalManager.getUserId());
        if (StringUtils.isNotBlank(user.getName())) {
            orderDTO.setAccount(user.getName());
        } else if (StringUtils.isNotBlank(user.getTelephone())) {
            orderDTO.setAccount(user.getTelephone());
        } else if (StringUtils.isNotBlank(user.getEmail())) {
            orderDTO.setAccount(user.getEmail());
        }
        BasePayOrder payOrder = payOrderDao.selectByPrimaryKey(id);
        orderDTO.setOrderNo(payOrder.getOutTradeNo());
        orderDTO.setOrderAmount(payOrder.getAmount());
        orderDTO.setName(((Map<String, Object>) JSON.parse(payOrder.getExtraData())).get("msg").toString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        orderDTO.setCreateTime(sdf.format(payOrder.getCreateTime()));
        result.add(orderDTO);
        return result;
    }

    /**
     * ????????????
     */
    public void insertBuyOrder(Integer id) {
        Date now = new Date();
        BasePayOrder payOrder = payOrderDao.selectByPrimaryKey(id);
        if (payOrder.getStatus().equals(PayConstant.STATUS_IS_PAY)) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "???????????????");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("orderAmount", payOrder.getAmount());
        params.put("poundage", payOrder.getAmount());
        params.put("type", PayConstant.TYPE_FT);
        Map<String, Object> extraData = (Map<String, Object>) JSON.parse(payOrder.getExtraData());
        params.put("msg", extraData.get("msg"));
        params.put("extraData", JSONObject.toJSONString(payOrder));
        // TODO
        boolean flag = (boolean) authService.getResponseResult(params, authCreateOrderUrl);
        //????????????????????????
        //????????????????????????
        if (flag) {
            payOrder.setStatus(PayConstant.STATUS_IS_PAY);
            payOrder.setUpdateTime(now);
            payOrderDao.updateByPrimaryKey(payOrder);
            BaseBuyRecord buyRecord = this.insertBuyRecord(payOrder);
            // ??????????????????
            Calendar cal = Calendar.getInstance();
            //?????????????????????????????????
            BaseUser baseUser =  userDao.selectByPrimaryKey(ThreadLocalManager.getUserId());
            if(baseUser.getExpireTime() == null){
                cal.setTime(now);
            }else{
                cal.setTime(baseUser.getExpireTime());
            }
            cal.add(Calendar.YEAR, 1);
            BaseUser user = new BaseUser();
            user.setId(ThreadLocalManager.getUserId());
            user.setRole(payOrder.getType());
            user.setExpireTime(cal.getTime());
            userDao.updateByPrimaryKeySelective(user);
        } else {
            payOrder.setStatus(PayConstant.STATUS_WRONG_PAY);
            payOrder.setUpdateTime(now);
            payOrderDao.updateByPrimaryKey(payOrder);
            throw new ServiceException(ErrorCode.AMOUNT_NOT_ARRIVE);
        }
    }

    /**
     * ????????????
     * @param payOrder
     */
    private BaseBuyRecord insertBuyRecord(BasePayOrder payOrder) {
        Date now = new Date();
        //??????????????????
        BaseBuyRecord rdBuyRecord = new BaseBuyRecord();
        rdBuyRecord.setType(payOrder.getType());
        rdBuyRecord.setPayType(payOrder.getPayType());
        rdBuyRecord.setAmount(payOrder.getAmount());
        rdBuyRecord.setUserId(payOrder.getUserId());
        rdBuyRecord.setIsDelete(0);
        rdBuyRecord.setPayOrderId(payOrder.getId());
        rdBuyRecord.setCreateTime(payOrder.getCreateTime());
        rdBuyRecord.setMessage(((Map<String, Object>) JSON.parse(payOrder.getExtraData())).get("msg").toString());
        buyRecordDao.insert(rdBuyRecord);
        return rdBuyRecord;
    }

    /**
     * ??????????????????
     * @return
     */
    public BigDecimal getUserBalance() {
        Map<String, Object> params = new HashMap<>();
        //TODO
        Map<String, Object> res = (Map<String, Object>) authService.getResponseResult(params, authGetBalanceUrl);
        return new BigDecimal(Double.parseDouble(res.get("balance").toString()));
    }

}

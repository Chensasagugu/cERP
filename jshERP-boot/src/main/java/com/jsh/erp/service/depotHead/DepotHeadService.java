package com.jsh.erp.service.depotHead;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsh.erp.common.constant.SupplierNameEnum;
import com.jsh.erp.common.constant.SupplierType;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.DepotHeadMapper;
import com.jsh.erp.datasource.mappers.DepotHeadMapperEx;
import com.jsh.erp.datasource.mappers.DepotItemMapperEx;
import com.jsh.erp.datasource.vo.*;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.factory.DepotConvert;
import com.jsh.erp.service.account.AccountService;
import com.jsh.erp.service.accountItem.AccountItemService;
import com.jsh.erp.service.depot.DepotService;
import com.jsh.erp.service.depotItem.DepotItemService;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.service.material.MaterialService;
import com.jsh.erp.service.materialExtend.MaterialExtendService;
import com.jsh.erp.service.orgaUserRel.OrgaUserRelService;
import com.jsh.erp.service.person.PersonService;
import com.jsh.erp.service.redis.RedisService;
import com.jsh.erp.service.serialNumber.SerialNumberService;
import com.jsh.erp.service.supplier.SupplierService;
import com.jsh.erp.service.systemConfig.SystemConfigService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.service.userBusiness.UserBusinessService;
import com.jsh.erp.utils.StringUtil;
import com.jsh.erp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;

import static com.jsh.erp.utils.Tools.getCenternTime;
import static com.jsh.erp.utils.Tools.getNow3;

@Service
public class DepotHeadService extends ServiceImpl<DepotHeadMapper,DepotHead> {
    private Logger logger = LoggerFactory.getLogger(DepotHeadService.class);

    @Resource
    private DepotHeadMapper depotHeadMapper;
    @Resource
    private DepotHeadMapperEx depotHeadMapperEx;
    @Resource
    private UserService userService;
    @Resource
    private DepotService depotService;
    @Resource
    DepotItemService depotItemService;
    @Resource
    private SupplierService supplierService;
    @Resource
    private UserBusinessService userBusinessService;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private SerialNumberService serialNumberService;
    @Resource
    private OrgaUserRelService orgaUserRelService;
    @Resource
    private PersonService personService;
    @Resource
    private AccountService accountService;
    @Resource
    private AccountItemService accountItemService;
    @Resource
    DepotItemMapperEx depotItemMapperEx;
    @Resource
    private LogService logService;
    @Resource
    private RedisService redisService;
    @Resource
    private DepotConvert depotConvert;
    @Resource
    private MaterialService materialService;
    @Resource
    private MaterialExtendService materialExtendService;

    public DepotHead getDepotHead(long id) throws Exception {
        DepotHead result = null;
        try {
            result = depotHeadMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHead> getDepotHead() throws Exception {
        DepotHeadExample example = new DepotHeadExample();
        example.createCriteria().andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<DepotHead> list = null;
        try {
            list = depotHeadMapper.selectByExample(example);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<DepotHeadVo4List> select(String type, String subType, String roleType, String status, String purchaseStatus, String number, String linkNumber,
                                         String beginTime, String endTime, String materialParam, Long organId, Long creator, Long depotId, Long accountId, String remark, int offset, int rows) throws Exception {
        List<DepotHeadVo4List> resList = new ArrayList<>();
        List<DepotHeadVo4List> list = new ArrayList<>();
        try {
            String[] depotArray = getDepotArray(subType);
            String[] creatorArray = getCreatorArray(roleType);
            String[] statusArray = StringUtil.isNotEmpty(status) ? status.split(",") : null;
            String[] purchaseStatusArray = StringUtil.isNotEmpty(purchaseStatus) ? purchaseStatus.split(",") : null;
            String[] organArray = getOrganArray(subType, purchaseStatus);
            Map<Long, String> personMap = personService.getPersonMap();
            Map<Long, String> accountMap = accountService.getAccountMap();
            beginTime = Tools.parseDayToTime(beginTime, BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime, BusinessConstants.DAY_LAST_TIME);
            list = depotHeadMapperEx.selectByConditionDepotHead(type, subType, creatorArray, statusArray, purchaseStatusArray, number, linkNumber, beginTime, endTime,
                    materialParam, organId, organArray, creator, depotId, depotArray, accountId, remark, offset, rows);
            if (null != list) {
                for (DepotHeadVo4List dh : list) {
                    if (accountMap != null && StringUtil.isNotEmpty(dh.getAccountIdList()) && StringUtil.isNotEmpty(dh.getAccountMoneyList())) {
                        String accountStr = accountService.getAccountStrByIdAndMoney(accountMap, dh.getAccountIdList(), dh.getAccountMoneyList());
                        dh.setAccountName(accountStr);
                    }
                    if (dh.getAccountIdList() != null) {
                        String accountidlistStr = dh.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountIdList(accountidlistStr);
                    }
                    if (dh.getAccountMoneyList() != null) {
                        String accountmoneylistStr = dh.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountMoneyList(accountmoneylistStr);
                    }
                    if (dh.getChangeAmount() != null) {
                        dh.setChangeAmount(dh.getChangeAmount().abs());
                    }
                    if (dh.getTotalPrice() != null) {
                        dh.setTotalPrice(dh.getTotalPrice().abs());
                    }
                    if (dh.getDeposit() == null) {
                        dh.setDeposit(BigDecimal.ZERO);
                    }
                    dh.setFinishDeposit(depotHeadMapperEx.getFinishDepositByNumber(dh.getNumber()));
                    if (StringUtil.isNotEmpty(dh.getSalesMan())) {
                        dh.setSalesManStr(personService.getPersonByMapAndIds(personMap, dh.getSalesMan()));
                    }
                    if (dh.getOperTime() != null) {
                        dh.setOperTimeStr(getCenternTime(dh.getOperTime()));
                    }
                    dh.setMaterialsList(findMaterialsListByHeaderId(dh.getId()));
                    resList.add(dh);
                }
            }
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return resList;
    }

    public Long countDepotHead(String type, String subType, String roleType, String status, String purchaseStatus, String number, String linkNumber,
                               String beginTime, String endTime, String materialParam, Long organId, Long creator, Long depotId, Long accountId, String remark) throws Exception {
        Long result = null;
        try {
            String[] depotArray = getDepotArray(subType);
            String[] creatorArray = getCreatorArray(roleType);
            String[] statusArray = StringUtil.isNotEmpty(status) ? status.split(",") : null;
            String[] purchaseStatusArray = StringUtil.isNotEmpty(purchaseStatus) ? purchaseStatus.split(",") : null;
            String[] organArray = getOrganArray(subType, purchaseStatus);
            beginTime = Tools.parseDayToTime(beginTime, BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime, BusinessConstants.DAY_LAST_TIME);
            result = depotHeadMapperEx.countsByDepotHead(type, subType, creatorArray, statusArray, purchaseStatusArray, number, linkNumber, beginTime, endTime,
                    materialParam, organId, organArray, creator, depotId, depotArray, accountId, remark);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * ????????????????????????????????????
     *
     * @param subType
     * @return
     * @throws Exception
     */
    public String[] getDepotArray(String subType) throws Exception {
        String[] depotArray = null;
        if (!BusinessConstants.SUB_TYPE_PURCHASE_ORDER.equals(subType) && !BusinessConstants.SUB_TYPE_SALES_ORDER.equals(subType)) {
            String depotIds = depotService.findDepotStrByCurrentUser();
            depotArray = StringUtil.isNotEmpty(depotIds) ? depotIds.split(",") : null;
        }
        return depotArray;
    }

    /**
     * ???????????????????????????????????????
     *
     * @param roleType
     * @return
     * @throws Exception
     */
    public String[] getCreatorArray(String roleType) throws Exception {
        String creator = getCreatorByRoleType(roleType);
        String[] creatorArray = null;
        if (StringUtil.isNotEmpty(creator)) {
            creatorArray = creator.split(",");
        }
        return creatorArray;
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public String[] getOrganArray(String subType, String purchaseStatus) throws Exception {
        String[] organArray = null;
        String type = "UserCustomer";
        Long userId = userService.getCurrentUser().getId();
        //??????????????????
        String ubValue = userBusinessService.getUBValueByTypeAndKeyId(type, userId.toString());
        List<Supplier> supplierList = supplierService.findBySelectCus();
        if (BusinessConstants.SUB_TYPE_SALES_ORDER.equals(subType) || BusinessConstants.SUB_TYPE_SALES.equals(subType)
                || BusinessConstants.SUB_TYPE_SALES_RETURN.equals(subType)) {
            //?????????????????????????????????????????????????????????
            if (StringUtil.isEmpty(purchaseStatus)) {
                if (null != supplierList) {
                    boolean customerFlag = systemConfigService.getCustomerFlag();
                    List<String> organList = new ArrayList<>();
                    for (Supplier supplier : supplierList) {
                        boolean flag = ubValue.contains("[" + supplier.getId().toString() + "]");
                        if (!customerFlag || flag) {
                            organList.add(supplier.getId().toString());
                        }
                    }
                    organArray = StringUtil.listToStringArray(organList);
                }
            }
        }
        return organArray;
    }

    /**
     * ?????????????????????????????????
     *
     * @param roleType
     * @return
     * @throws Exception
     */
    public String getCreatorByRoleType(String roleType) throws Exception {
        String creator = "";
        User user = userService.getCurrentUser();
        if (BusinessConstants.ROLE_TYPE_PRIVATE.equals(roleType)) {
            creator = user.getId().toString();
        } else if (BusinessConstants.ROLE_TYPE_THIS_ORG.equals(roleType)) {
            creator = orgaUserRelService.getUserIdListByUserId(user.getId());
        }
        return creator;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertDepotHead(JSONObject obj, HttpServletRequest request) throws Exception {
        DepotHead depotHead = JSONObject.parseObject(obj.toJSONString(), DepotHead.class);
        depotHead.setCreateTime(new Timestamp(System.currentTimeMillis()));
        depotHead.setStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
        int result = 0;
        try {
            result = depotHeadMapper.insert(depotHead);
            logService.insertLog("??????", BusinessConstants.LOG_OPERATION_TYPE_ADD, request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateDepotHead(JSONObject obj, HttpServletRequest request) throws Exception {
        DepotHead depotHead = JSONObject.parseObject(obj.toJSONString(), DepotHead.class);
        DepotHead dh = null;
        try {
            dh = depotHeadMapper.selectByPrimaryKey(depotHead.getId());
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        depotHead.setStatus(dh.getStatus());
        depotHead.setCreateTime(dh.getCreateTime());
        int result = 0;
        try {
            result = depotHeadMapper.updateByPrimaryKey(depotHead);
            logService.insertLog("??????",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(depotHead.getId()).toString(), request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteDepotHead(Long id, HttpServletRequest request) throws Exception {
        return batchDeleteBillByIds(id.toString());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteDepotHead(String ids, HttpServletRequest request) throws Exception {
        return batchDeleteBillByIds(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteBillByIds(String ids) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append(BusinessConstants.LOG_OPERATION_TYPE_DELETE);
        List<DepotHead> dhList = getDepotHeadListByIds(ids);
        for (DepotHead depotHead : dhList) {
            sb.append("[").append(depotHead.getNumber()).append("]");
            //???????????????????????????????????????
            if ("0".equals(depotHead.getStatus())) {
                User userInfo = userService.getCurrentUser();
                //?????????????????????????????????
                if (BusinessConstants.DEPOTHEAD_TYPE_OUT.equals(depotHead.getType())
                        && !BusinessConstants.SUB_TYPE_TRANSFER.equals(depotHead.getSubType())) {
                    //????????????????????????
                    List<DepotItem> depotItemList = null;
                    try {
                        depotItemList = depotItemMapperEx.findDepotItemListBydepotheadId(depotHead.getId(), BusinessConstants.ENABLE_SERIAL_NUMBER_ENABLED);
                    } catch (Exception e) {
                        JshException.readFail(logger, e);
                    }

                    /**???????????????*/
                    if (depotItemList != null && depotItemList.size() > 0) {
                        for (DepotItem depotItem : depotItemList) {
                            //BasicNumber=OperNumber*ratio
                            serialNumberService.cancelSerialNumber(depotItem.getMaterialId(), depotHead.getNumber(), (depotItem.getBasicNumber() == null ? 0 : depotItem.getBasicNumber()).intValue(), userInfo);
                        }
                    }
                }
                //?????????????????????????????????????????????????????????
                if (BusinessConstants.DEPOTHEAD_TYPE_OUT.equals(depotHead.getType())
                        && BusinessConstants.SUB_TYPE_RETAIL.equals(depotHead.getSubType())) {
                    if (BusinessConstants.PAY_TYPE_PREPAID.equals(depotHead.getPayType())) {
                        if (depotHead.getOrganId() != null) {
                            supplierService.updateAdvanceIn(depotHead.getOrganId(), depotHead.getTotalPrice().abs());
                        }
                    }
                }
                List<DepotItem> list = depotItemService.getListByHeaderId(depotHead.getId());
                //????????????????????????
                depotItemMapperEx.batchDeleteDepotItemByDepotHeadIds(new Long[]{depotHead.getId()});
                //????????????????????????
                batchDeleteDepotHeadByIds(depotHead.getId().toString());
                //????????????????????????????????????-????????????????????????????????????????????????
                if (StringUtil.isNotEmpty(depotHead.getLinkNumber())) {
                    if ((BusinessConstants.DEPOTHEAD_TYPE_IN.equals(depotHead.getType()) &&
                            BusinessConstants.SUB_TYPE_PURCHASE.equals(depotHead.getSubType()))
                            || (BusinessConstants.DEPOTHEAD_TYPE_OUT.equals(depotHead.getType()) &&
                            BusinessConstants.SUB_TYPE_SALES.equals(depotHead.getSubType()))
                            || (BusinessConstants.DEPOTHEAD_TYPE_OTHER.equals(depotHead.getType()) &&
                            BusinessConstants.SUB_TYPE_REPLAY.equals(depotHead.getSubType()))) {
                        String status = BusinessConstants.BILLS_STATUS_AUDIT;
                        //????????????????????????????????????????????????
                        List<DepotHead> exceptCurrentList = getListByLinkNumberExceptCurrent(depotHead.getLinkNumber(), depotHead.getNumber(), depotHead.getType());
                        if (exceptCurrentList != null && exceptCurrentList.size() > 0) {
                            status = BusinessConstants.BILLS_STATUS_SKIPING;
                        }
                        DepotHead dh = new DepotHead();
                        dh.setStatus(status);
                        DepotHeadExample example = new DepotHeadExample();
                        example.createCriteria().andNumberEqualTo(depotHead.getLinkNumber());
                        depotHeadMapper.updateByExampleSelective(dh, example);
                    }
                }
                //???????????????????????????????????????????????????-??????????????????????????????????????????
                if (StringUtil.isNotEmpty(depotHead.getLinkNumber())) {
                    if (BusinessConstants.DEPOTHEAD_TYPE_OTHER.equals(depotHead.getType()) &&
                            BusinessConstants.SUB_TYPE_PURCHASE_ORDER.equals(depotHead.getSubType())) {
                        DepotHead dh = new DepotHead();
                        //???????????????????????????????????????????????????????????????
                        List<DepotItemVo4MaterialAndSum> batchList = depotItemMapperEx.getBatchBillDetailMaterialSum(depotHead.getLinkNumber(), depotHead.getType());
                        if (batchList.size() > 0) {
                            dh.setPurchaseStatus(BusinessConstants.PURCHASE_STATUS_SKIPING);
                        } else {
                            dh.setPurchaseStatus(BusinessConstants.PURCHASE_STATUS_UN_AUDIT);
                        }
                        DepotHeadExample example = new DepotHeadExample();
                        example.createCriteria().andNumberEqualTo(depotHead.getLinkNumber());
                        depotHeadMapper.updateByExampleSelective(dh, example);
                    }
                }
                //??????????????????
                for (DepotItem depotItem : list) {
                    depotItemService.updateCurrentStock(depotItem);
                }
            } else {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_DELETE_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_DELETE_FAILED_MSG));
            }
        }
        logService.insertLog("??????", sb.toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        return 1;
    }

    /**
     * ????????????????????????
     *
     * @param ids
     * @return
     * @throws Exception
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteDepotHeadByIds(String ids) throws Exception {
        User userInfo = userService.getCurrentUser();
        String[] idArray = ids.split(",");
        int result = 0;
        try {
            result = depotHeadMapperEx.batchDeleteDepotHeadByIds(new Date(), userInfo == null ? null : userInfo.getId(), idArray);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    public List<DepotHead> getDepotHeadListByIds(String ids) throws Exception {
        List<Long> idList = StringUtil.strToLongList(ids);
        List<DepotHead> list = new ArrayList<>();
        try {
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andIdIn(idList);
            list = depotHeadMapper.selectByExample(example);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int checkIsNameExist(Long id, String name) throws Exception {
        DepotHeadExample example = new DepotHeadExample();
        example.createCriteria().andIdNotEqualTo(id).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<DepotHead> list = null;
        try {
            list = depotHeadMapper.selectByExample(example);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list == null ? 0 : list.size();
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchSetStatus(String status, String depotHeadIDs) throws Exception {
        int result = 0;
        List<Long> dhIds = new ArrayList<>();
        List<Long> ids = StringUtil.strToLongList(depotHeadIDs);
        for (Long id : ids) {
            DepotHead depotHead = getDepotHead(id);
            if ("0".equals(status)) {
                if ("1".equals(depotHead.getStatus())) {
                    dhIds.add(id);
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_AUDIT_TO_UN_AUDIT_FAILED_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_AUDIT_TO_UN_AUDIT_FAILED_MSG));
                }
            } else if ("1".equals(status)) {
                if ("0".equals(depotHead.getStatus())) {
                    dhIds.add(id);
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_TO_AUDIT_FAILED_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_TO_AUDIT_FAILED_MSG));
                }
            }
        }
        if (dhIds.size() > 0) {
            DepotHead depotHead = new DepotHead();
            depotHead.setStatus(status);
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andIdIn(dhIds);
            result = depotHeadMapper.updateByExampleSelective(depotHead, example);
        }
        return result;
    }

    public String findMaterialsListByHeaderId(Long id) throws Exception {
        String result = null;
        try {
            result = depotHeadMapperEx.findMaterialsListByHeaderId(id);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHeadVo4InDetail> findInDetail(String beginTime, String endTime, String type, String[] creatorArray,
                                                   String[] organArray, String materialParam, List<Long> depotList, Integer oId, String number,
                                                   String remark, Integer offset, Integer rows) throws Exception {
        List<DepotHeadVo4InDetail> list = null;
        try {
            list = depotHeadMapperEx.findInDetail(beginTime, endTime, type, creatorArray, organArray, materialParam, depotList, oId, number, remark, offset, rows);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int findInDetailCount(String beginTime, String endTime, String type, String[] creatorArray,
                                 String[] organArray, String materialParam, List<Long> depotList, Integer oId, String number,
                                 String remark) throws Exception {
        int result = 0;
        try {
            result = depotHeadMapperEx.findInDetailCount(beginTime, endTime, type, creatorArray, organArray, materialParam, depotList, oId, number, remark);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHeadVo4InOutMCount> findInOutMaterialCount(String beginTime, String endTime, String type, String materialParam,
                                                                List<Long> depotList, Integer oId, String roleType, Integer offset, Integer rows) throws Exception {
        List<DepotHeadVo4InOutMCount> list = null;
        try {
            String[] creatorArray = getCreatorArray(roleType);
            String subType = "??????".equals(type) ? "??????" : "";
            String[] organArray = getOrganArray(subType, "");
            list = depotHeadMapperEx.findInOutMaterialCount(beginTime, endTime, type, materialParam, depotList, oId,
                    creatorArray, organArray, offset, rows);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int findInOutMaterialCountTotal(String beginTime, String endTime, String type, String materialParam,
                                           List<Long> depotList, Integer oId, String roleType) throws Exception {
        int result = 0;
        try {
            String[] creatorArray = getCreatorArray(roleType);
            String subType = "??????".equals(type) ? "??????" : "";
            String[] organArray = getOrganArray(subType, "");
            result = depotHeadMapperEx.findInOutMaterialCountTotal(beginTime, endTime, type, materialParam, depotList, oId,
                    creatorArray, organArray);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHeadVo4InDetail> findAllocationDetail(String beginTime, String endTime, String subType, String number,
                                                           String[] creatorArray, String materialParam, List<Long> depotList, List<Long> depotFList,
                                                           String remark, Integer offset, Integer rows) throws Exception {
        List<DepotHeadVo4InDetail> list = null;
        try {
            list = depotHeadMapperEx.findAllocationDetail(beginTime, endTime, subType, number, creatorArray,
                    materialParam, depotList, depotFList, remark, offset, rows);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int findAllocationDetailCount(String beginTime, String endTime, String subType, String number,
                                         String[] creatorArray, String materialParam, List<Long> depotList, List<Long> depotFList,
                                         String remark) throws Exception {
        int result = 0;
        try {
            result = depotHeadMapperEx.findAllocationDetailCount(beginTime, endTime, subType, number, creatorArray,
                    materialParam, depotList, depotFList, remark);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHeadVo4StatementAccount> getStatementAccount(String beginTime, String endTime, Integer organId, String[] organArray,
                                                                  String supplierType, String type, String subType, Integer offset, Integer rows) {
        List<DepotHeadVo4StatementAccount> list = null;
        try {
            list = depotHeadMapperEx.getStatementAccount(beginTime, endTime, organId, organArray, supplierType, type, subType, offset, rows);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int getStatementAccountCount(String beginTime, String endTime, Integer organId,
                                        String[] organArray, String supplierType, String type, String subType) {
        int result = 0;
        try {
            result = depotHeadMapperEx.getStatementAccountCount(beginTime, endTime, organId, organArray, supplierType, type, subType);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHeadVo4StatementAccount> getStatementAccountTotalPay(String beginTime, String endTime, Integer organId,
                                                                          String[] organArray, String supplierType,
                                                                          String type, String subType) {
        List<DepotHeadVo4StatementAccount> list = null;
        try {
            list = depotHeadMapperEx.getStatementAccountTotalPay(beginTime, endTime, organId, organArray, supplierType, type, subType);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public BigDecimal findAllMoney(Integer supplierId, String type, String subType, String mode, String endTime) throws Exception {
        String modeName = "";
        BigDecimal allOtherMoney = BigDecimal.ZERO;
        BigDecimal allDepositMoney = BigDecimal.ZERO;
        if (mode.equals("??????")) {
            modeName = "change_amount";
        } else if (mode.equals("??????")) {
            modeName = "discount_last_money";
            allOtherMoney = depotHeadMapperEx.findAllOtherMoney(supplierId, type, subType, endTime);
            allDepositMoney = depotHeadMapperEx.findDepositMoney(supplierId, type, subType, endTime);
        }
        BigDecimal result = BigDecimal.ZERO;
        try {
            result = depotHeadMapperEx.findAllMoney(supplierId, type, subType, modeName, endTime);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        if (allOtherMoney != null) {
            result = result.add(allOtherMoney);
        }
        if (allDepositMoney != null) {
            result = result.subtract(allDepositMoney);
        }
        return result;
    }

    /**
     * ???????????????
     *
     * @param getS
     * @param type
     * @param subType
     * @param mode    ??????????????????
     * @return
     */
    public BigDecimal allMoney(String getS, String type, String subType, String mode, String endTime) {
        BigDecimal allMoney = BigDecimal.ZERO;
        try {
            Integer supplierId = Integer.valueOf(getS);
            BigDecimal sum = findAllMoney(supplierId, type, subType, mode, endTime);
            if (sum != null) {
                allMoney = sum;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //??????????????????????????????????????????
        if ((allMoney.compareTo(BigDecimal.ZERO)) == -1) {
            allMoney = allMoney.abs();
        }
        return allMoney;
    }

    /**
     * ???????????????????????????????????????????????????????????????
     *
     * @param supplierId
     * @param endTime
     * @param supType
     * @return
     */
    public BigDecimal findTotalPay(Integer supplierId, String endTime, String supType) {
        BigDecimal sum = BigDecimal.ZERO;
        String getS = supplierId.toString();
        if (("??????").equals(supType)) { //??????
            sum = allMoney(getS, "??????", "??????", "??????", endTime).subtract(allMoney(getS, "??????", "??????", "??????", endTime));
        } else if (("?????????").equals(supType)) { //?????????
            sum = allMoney(getS, "??????", "??????", "??????", endTime).subtract(allMoney(getS, "??????", "??????", "??????", endTime));
        }
        return sum;
    }

    public List<DepotHeadVo4List> getDetailByNumber(String number) throws Exception {
        List<DepotHeadVo4List> resList = new ArrayList<DepotHeadVo4List>();
        List<DepotHeadVo4List> list = null;
        try {
            Map<Long, String> personMap = personService.getPersonMap();
            Map<Long, String> accountMap = accountService.getAccountMap();
            list = depotHeadMapperEx.getDetailByNumber(number);
            if (null != list) {
                for (DepotHeadVo4List dh : list) {
                    if (accountMap != null && StringUtil.isNotEmpty(dh.getAccountIdList()) && StringUtil.isNotEmpty(dh.getAccountMoneyList())) {
                        String accountStr = accountService.getAccountStrByIdAndMoney(accountMap, dh.getAccountIdList(), dh.getAccountMoneyList());
                        dh.setAccountName(accountStr);
                    }
                    if (dh.getAccountIdList() != null) {
                        String accountidlistStr = dh.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountIdList(accountidlistStr);
                    }
                    if (dh.getAccountMoneyList() != null) {
                        String accountmoneylistStr = dh.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountMoneyList(accountmoneylistStr);
                    }
                    if (dh.getChangeAmount() != null) {
                        dh.setChangeAmount(dh.getChangeAmount().abs());
                    }
                    if (dh.getTotalPrice() != null) {
                        dh.setTotalPrice(dh.getTotalPrice().abs());
                    }
                    if (StringUtil.isNotEmpty(dh.getSalesMan())) {
                        dh.setSalesManStr(personService.getPersonByMapAndIds(personMap, dh.getSalesMan()));
                    }
                    dh.setOperTimeStr(getCenternTime(dh.getOperTime()));
                    dh.setMaterialsList(findMaterialsListByHeaderId(dh.getId()));
                    dh.setCreatorName(userService.getUser(dh.getCreator()).getUsername());
                    resList.add(dh);
                }
            }
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return resList;
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param linkNumber
     * @param number
     * @return
     * @throws Exception
     */
    public List<DepotHead> getListByLinkNumberExceptCurrent(String linkNumber, String number, String type) throws Exception {
        DepotHeadExample example = new DepotHeadExample();
        example.createCriteria().andLinkNumberEqualTo(linkNumber).andNumberNotEqualTo(number).andTypeEqualTo(type)
                .andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        return depotHeadMapper.selectByExample(example);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param beanJson
     * @param rows
     * @param request
     * @throws Exception
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void addDepotHeadAndDetail(String beanJson, String rows,
                                      HttpServletRequest request) throws Exception {
        /**????????????????????????*/
        DepotHead depotHead = JSONObject.parseObject(beanJson, DepotHead.class);
        String subType = depotHead.getSubType();
        //??????????????????
        if ("??????".equals(subType) || "????????????".equals(subType) || "??????".equals(subType) || "????????????".equals(subType)) {
            if (StringUtil.isEmpty(depotHead.getAccountIdList()) && depotHead.getAccountId() == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_ACCOUNT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_ACCOUNT_FAILED_MSG));
            }
        }
        //????????????
        if ("????????????".equals(subType) || "????????????".equals(subType)) {
            if (depotHead.getChangeAmount().abs().compareTo(depotHead.getDiscountLastMoney().add(depotHead.getOtherMoney())) != 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_BACK_BILL_DEBT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_BACK_BILL_DEBT_FAILED_MSG));
            }
        }
        //?????????????????????????????????????????????????????????
        User userInfo = userService.getCurrentUser();
        depotHead.setCreator(userInfo == null ? null : userInfo.getId());
        depotHead.setCreateTime(new Timestamp(System.currentTimeMillis()));
        if (StringUtil.isEmpty(depotHead.getStatus())) {
            depotHead.setStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
        }
        depotHead.setPurchaseStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
        depotHead.setPayType(depotHead.getPayType() == null ? "??????" : depotHead.getPayType());
        if (StringUtil.isNotEmpty(depotHead.getAccountIdList())) {
            depotHead.setAccountIdList(depotHead.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", ""));
        }
        if (StringUtil.isNotEmpty(depotHead.getAccountMoneyList())) {
            //??????????????????????????????
            String accountMoneyList = depotHead.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
            BigDecimal sum = StringUtil.getArrSum(accountMoneyList.split(","));
            BigDecimal manyAccountSum = sum.abs();
            if (manyAccountSum.compareTo(depotHead.getChangeAmount().abs()) != 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_MSG));
            }
            depotHead.setAccountMoneyList(accountMoneyList);
        }
        //??????????????????????????????????????????????????????
        if (depotHead.getDeposit() != null && StringUtil.isNotEmpty(depotHead.getLinkNumber())) {
            BigDecimal finishDeposit = depotHeadMapperEx.getFinishDepositByNumberExceptCurrent(depotHead.getLinkNumber(), depotHead.getNumber());
            //????????????????????????
            BigDecimal changeAmount = getDepotHead(depotHead.getLinkNumber()).getChangeAmount();
            if (changeAmount != null) {
                BigDecimal preDeposit = changeAmount.abs();
                if (depotHead.getDeposit().add(finishDeposit).compareTo(preDeposit) > 0) {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_DEPOSIT_OVER_PRE_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_DEPOSIT_OVER_PRE_MSG));
                }
            }
        }
        try {
            depotHeadMapper.insertSelective(depotHead);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        /**????????????????????????????????????*/
        if (BusinessConstants.PAY_TYPE_PREPAID.equals(depotHead.getPayType())) {
            if (depotHead.getOrganId() != null) {
                BigDecimal currentAdvanceIn = supplierService.getSupplier(depotHead.getOrganId()).getAdvanceIn();
                if (currentAdvanceIn.compareTo(depotHead.getTotalPrice()) >= 0) {
                    supplierService.updateAdvanceIn(depotHead.getOrganId(), BigDecimal.ZERO.subtract(depotHead.getTotalPrice()));
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_MEMBER_PAY_LACK_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_MEMBER_PAY_LACK_MSG));
                }
            }
        }
        //??????????????????????????????id
        DepotHeadExample dhExample = new DepotHeadExample();
        dhExample.createCriteria().andNumberEqualTo(depotHead.getNumber()).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<DepotHead> list = depotHeadMapper.selectByExample(dhExample);
        if (list != null) {
            Long headId = list.get(0).getId();
            /**???????????????????????????????????????*/
            depotItemService.saveDetials(rows, headId, "add", request);
        }
        logService.insertLog("??????",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(depotHead.getNumber()).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    /**
     * ???????????????????????????????????????
     *
     * @param beanJson
     * @param rows
     * @param request
     * @throws Exception
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void updateDepotHeadAndDetail(String beanJson, String rows, HttpServletRequest request) throws Exception {
        /**????????????????????????*/
        DepotHead depotHead = JSONObject.parseObject(beanJson, DepotHead.class);
        //???????????????????????????
        BigDecimal preTotalPrice = getDepotHead(depotHead.getId()).getTotalPrice().abs();
        String subType = depotHead.getSubType();
        //??????????????????
        if ("??????".equals(subType) || "????????????".equals(subType) || "??????".equals(subType) || "????????????".equals(subType)) {
            if (StringUtil.isEmpty(depotHead.getAccountIdList()) && depotHead.getAccountId() == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_ACCOUNT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_ACCOUNT_FAILED_MSG));
            }
        }
        //????????????
        if ("????????????".equals(subType) || "????????????".equals(subType)) {
            if (depotHead.getChangeAmount().abs().compareTo(depotHead.getDiscountLastMoney().add(depotHead.getOtherMoney())) != 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_BACK_BILL_DEBT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_BACK_BILL_DEBT_FAILED_MSG));
            }
        }
        if (StringUtil.isNotEmpty(depotHead.getAccountIdList())) {
            depotHead.setAccountIdList(depotHead.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", ""));
        }
        if (StringUtil.isNotEmpty(depotHead.getAccountMoneyList())) {
            //??????????????????????????????
            String accountMoneyList = depotHead.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
            BigDecimal sum = StringUtil.getArrSum(accountMoneyList.split(","));
            BigDecimal manyAccountSum = sum.abs();
            if (manyAccountSum.compareTo(depotHead.getChangeAmount().abs()) != 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_MSG));
            }
            depotHead.setAccountMoneyList(accountMoneyList);
        }
        //??????????????????????????????????????????????????????
        if (depotHead.getDeposit() != null && StringUtil.isNotEmpty(depotHead.getLinkNumber())) {
            BigDecimal finishDeposit = depotHeadMapperEx.getFinishDepositByNumberExceptCurrent(depotHead.getLinkNumber(), depotHead.getNumber());
            //????????????????????????
            BigDecimal changeAmount = getDepotHead(depotHead.getLinkNumber()).getChangeAmount();
            if (changeAmount != null) {
                BigDecimal preDeposit = changeAmount.abs();
                if (depotHead.getDeposit().add(finishDeposit).compareTo(preDeposit) > 0) {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_DEPOSIT_OVER_PRE_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_DEPOSIT_OVER_PRE_MSG));
                }
            }
        }
        try {
            depotHeadMapper.updateByPrimaryKeySelective(depotHead);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        /**????????????????????????????????????*/
        if (BusinessConstants.PAY_TYPE_PREPAID.equals(depotHead.getPayType())) {
            if (depotHead.getOrganId() != null) {
                BigDecimal currentAdvanceIn = supplierService.getSupplier(depotHead.getOrganId()).getAdvanceIn();
                if (currentAdvanceIn.compareTo(depotHead.getTotalPrice()) >= 0) {
                    supplierService.updateAdvanceIn(depotHead.getOrganId(), BigDecimal.ZERO.subtract(depotHead.getTotalPrice().subtract(preTotalPrice)));
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_MEMBER_PAY_LACK_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_MEMBER_PAY_LACK_MSG));
                }
            }
        }
        /**???????????????????????????????????????*/
        depotItemService.saveDetials(rows, depotHead.getId(), "update", request);
        logService.insertLog("??????",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(depotHead.getNumber()).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    public Map<String, Object> getBuyAndSaleStatistics(String today, String monthFirstDay, String yesterdayBegin, String yesterdayEnd,
                                                       String yearBegin, String yearEnd, String roleType) throws Exception {
        String[] creatorArray = getCreatorArray(roleType);
        Map<String, Object> map = new HashMap<>();
        //??????
        BigDecimal todayBuy = getBuyAndSaleBasicStatistics("??????", "??????",
                1, today, getNow3(), creatorArray); //??????????????????
        BigDecimal todayBuyBack = getBuyAndSaleBasicStatistics("??????", "????????????",
                1, today, getNow3(), creatorArray); //??????????????????
        BigDecimal todaySale = getBuyAndSaleBasicStatistics("??????", "??????",
                1, today, getNow3(), creatorArray); //??????????????????
        BigDecimal todaySaleBack = getBuyAndSaleBasicStatistics("??????", "????????????",
                1, today, getNow3(), creatorArray); //??????????????????
        BigDecimal todayRetailSale = getBuyAndSaleRetailStatistics("??????", "??????",
                today, getNow3(), creatorArray); //??????????????????
        BigDecimal todayRetailSaleBack = getBuyAndSaleRetailStatistics("??????", "????????????",
                today, getNow3(), creatorArray); //??????????????????
        //??????
        BigDecimal monthBuy = getBuyAndSaleBasicStatistics("??????", "??????",
                1, monthFirstDay, getNow3(), creatorArray); //??????????????????
        BigDecimal monthBuyBack = getBuyAndSaleBasicStatistics("??????", "????????????",
                1, monthFirstDay, getNow3(), creatorArray); //??????????????????
        BigDecimal monthSale = getBuyAndSaleBasicStatistics("??????", "??????",
                1, monthFirstDay, getNow3(), creatorArray); //??????????????????
        BigDecimal monthSaleBack = getBuyAndSaleBasicStatistics("??????", "????????????",
                1, monthFirstDay, getNow3(), creatorArray); //??????????????????
        BigDecimal monthRetailSale = getBuyAndSaleRetailStatistics("??????", "??????",
                monthFirstDay, getNow3(), creatorArray); //??????????????????
        BigDecimal monthRetailSaleBack = getBuyAndSaleRetailStatistics("??????", "????????????",
                monthFirstDay, getNow3(), creatorArray); //??????????????????
        //??????
        BigDecimal yesterdayBuy = getBuyAndSaleBasicStatistics("??????", "??????",
                1, yesterdayBegin, yesterdayEnd, creatorArray); //??????????????????
        BigDecimal yesterdayBuyBack = getBuyAndSaleBasicStatistics("??????", "????????????",
                1, yesterdayBegin, yesterdayEnd, creatorArray); //??????????????????
        BigDecimal yesterdaySale = getBuyAndSaleBasicStatistics("??????", "??????",
                1, yesterdayBegin, yesterdayEnd, creatorArray); //??????????????????
        BigDecimal yesterdaySaleBack = getBuyAndSaleBasicStatistics("??????", "????????????",
                1, yesterdayBegin, yesterdayEnd, creatorArray); //??????????????????
        BigDecimal yesterdayRetailSale = getBuyAndSaleRetailStatistics("??????", "??????",
                yesterdayBegin, yesterdayEnd, creatorArray); //??????????????????
        BigDecimal yesterdayRetailSaleBack = getBuyAndSaleRetailStatistics("??????", "????????????",
                yesterdayBegin, yesterdayEnd, creatorArray); //??????????????????
        //??????
        BigDecimal yearBuy = getBuyAndSaleBasicStatistics("??????", "??????",
                1, yearBegin, yearEnd, creatorArray); //??????????????????
        BigDecimal yearBuyBack = getBuyAndSaleBasicStatistics("??????", "????????????",
                1, yearBegin, yearEnd, creatorArray); //??????????????????
        BigDecimal yearSale = getBuyAndSaleBasicStatistics("??????", "??????",
                1, yearBegin, yearEnd, creatorArray); //??????????????????
        BigDecimal yearSaleBack = getBuyAndSaleBasicStatistics("??????", "????????????",
                1, yearBegin, yearEnd, creatorArray); //??????????????????
        BigDecimal yearRetailSale = getBuyAndSaleRetailStatistics("??????", "??????",
                yearBegin, yearEnd, creatorArray); //??????????????????
        BigDecimal yearRetailSaleBack = getBuyAndSaleRetailStatistics("??????", "????????????",
                yearBegin, yearEnd, creatorArray); //??????????????????
        map.put("todayBuy", todayBuy.subtract(todayBuyBack));
        map.put("todaySale", todaySale.subtract(todaySaleBack));
        map.put("todayRetailSale", todayRetailSale.subtract(todayRetailSaleBack));
        map.put("monthBuy", monthBuy.subtract(monthBuyBack));
        map.put("monthSale", monthSale.subtract(monthSaleBack));
        map.put("monthRetailSale", monthRetailSale.subtract(monthRetailSaleBack));
        map.put("yesterdayBuy", yesterdayBuy.subtract(yesterdayBuyBack));
        map.put("yesterdaySale", yesterdaySale.subtract(yesterdaySaleBack));
        map.put("yesterdayRetailSale", yesterdayRetailSale.subtract(yesterdayRetailSaleBack));
        map.put("yearBuy", yearBuy.subtract(yearBuyBack));
        map.put("yearSale", yearSale.subtract(yearSaleBack));
        map.put("yearRetailSale", yearRetailSale.subtract(yearRetailSaleBack));
        return map;
    }

    public BigDecimal getBuyAndSaleBasicStatistics(String type, String subType, Integer hasSupplier,
                                                   String beginTime, String endTime, String[] creatorArray) {
        return depotHeadMapperEx.getBuyAndSaleBasicStatistics(type, subType, hasSupplier, beginTime, endTime, creatorArray);
    }

    public BigDecimal getBuyAndSaleRetailStatistics(String type, String subType,
                                                    String beginTime, String endTime, String[] creatorArray) {
        return depotHeadMapperEx.getBuyAndSaleRetailStatistics(type, subType, beginTime, endTime, creatorArray).abs();
    }

    public DepotHead getDepotHead(String number) throws Exception {
        DepotHead depotHead = new DepotHead();
        try {
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andNumberEqualTo(number).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
            List<DepotHead> list = depotHeadMapper.selectByExample(example);
            if (null != list && list.size() > 0) {
                depotHead = list.get(0);
            }
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return depotHead;
    }

    public List<DepotHeadVo4List> debtList(Long organId, String materialParam, String number, String beginTime, String endTime,
                                           String type, String subType, String roleType, String status) {
        List<DepotHeadVo4List> resList = new ArrayList<>();
        try {
            String depotIds = depotService.findDepotStrByCurrentUser();
            String[] depotArray = depotIds.split(",");
            String[] creatorArray = getCreatorArray(roleType);
            beginTime = Tools.parseDayToTime(beginTime, BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime, BusinessConstants.DAY_LAST_TIME);
            List<DepotHeadVo4List> list = depotHeadMapperEx.debtList(organId, type, subType, creatorArray, status, number, beginTime, endTime, materialParam, depotArray);
            if (null != list) {
                for (DepotHeadVo4List dh : list) {
                    if (dh.getChangeAmount() != null) {
                        dh.setChangeAmount(dh.getChangeAmount().abs());
                    }
                    if (dh.getTotalPrice() != null) {
                        dh.setTotalPrice(dh.getTotalPrice().abs());
                    }
                    if (dh.getDeposit() == null) {
                        dh.setDeposit(BigDecimal.ZERO);
                    }
                    if (dh.getOperTime() != null) {
                        dh.setOperTimeStr(getCenternTime(dh.getOperTime()));
                    }
                    dh.setFinishDebt(accountItemService.getEachAmountByBillId(dh.getId()));
                    dh.setMaterialsList(findMaterialsListByHeaderId(dh.getId()));
                    resList.add(dh);
                }
            }
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return resList;
    }


    /**
     * ????????????????????????,??????4??????????????????
     * 1. jsh_depot_head ?????????
     * 2. jsh_depot_item    ????????????
     * 3. jsh_material  ????????????????????????????????????????????????????????????
     * 4. jsh_material_extend
     */
    @Transactional
    public void saveOrder(List<DepotInfoVo> depotInfoVos) {
        if(CollectionUtils.isEmpty(depotInfoVos)){
            throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_EMPTY_ORDER_CODE,
                    ExceptionConstants.DEPOT_HEAD_EMPTY_ORDER_MSG);
        }
        List<DepotItem> depotItems = new ArrayList<>();
        for(DepotInfoVo info:depotInfoVos){
            //???????????????
            DepotHead depotHead = depotConvert.convertHeadToPo(info);
            LambdaQueryWrapper<Supplier> supplierWrapper = Wrappers.lambdaQuery();
            if(Objects.nonNull(info.getOrgan())){
                supplierWrapper.eq(Supplier::getSupplier,info.getOrgan());
            }
            Supplier one = supplierService.getOne(supplierWrapper);
            if(Objects.nonNull(one)){
                depotHead.setOrganId(one.getId());
            }else{
                //??????????????????????????????????????????
                Supplier supplier = createNewSupplier(info);
                supplierService.save(supplier);
                depotHead.setOrganId(supplier.getId());
            }
            this.save(depotHead);
            List<DepotItemVo> depotItemVos = info.getItems();
            for(DepotItemVo itemVo:depotItemVos){
                //??????????????????id????????????
                LambdaQueryWrapper<Material> materialWrapper = Wrappers.lambdaQuery();
                if(Objects.nonNull(itemVo.getMaterialName())){
                    materialWrapper.eq(Material::getName,itemVo.getMaterialName());
                }
                if(Objects.nonNull(itemVo.getMaterialModel())){
                    materialWrapper.eq(Material::getModel,itemVo.getMaterialModel());
                }
                Material itemMaterial = materialService.getOne(materialWrapper);
                MaterialExtend itemMaterialExtend;
                //??????????????????????????????????????????
                if(Objects.isNull(itemMaterial)){
                    Material material = createNewMaterial(itemVo);
                    materialService.save(material);
                    itemMaterial = material;
                    //????????????sku
                    itemMaterialExtend = createNewMaterialExtend(itemMaterial,itemVo);
                    materialExtendService.save(itemMaterialExtend);
                }else{
                    LambdaQueryWrapper<MaterialExtend> wrapper = Wrappers.lambdaQuery();
                    if(Objects.nonNull(itemMaterial.getId())){
                        wrapper.eq(MaterialExtend::getMaterialId,itemMaterial.getId());
                    }
                    if(Objects.nonNull(itemVo.getStandards())){
                        wrapper.eq(MaterialExtend::getSku,itemVo.getStandards());
                    }
                    itemMaterialExtend = materialExtendService.getOne(wrapper);
                    if(Objects.isNull(itemMaterialExtend)){
                        itemMaterialExtend = createNewMaterialExtend(itemMaterial,itemVo);
                        materialExtendService.save(itemMaterialExtend);
                    }
                }
                itemVo.setMaterialId(itemMaterial.getId());
                itemVo.setMaterialExtendId(itemMaterialExtend.getId());
                //?????????????????????
                DepotItem depotItem = depotConvert.convertItemToPo(itemVo);
                depotItem.setHeaderId(depotHead.getId());
                depotItems.add(depotItem);
            }
        }
        depotItemService.saveBatch(depotItems);
    }

    /**
     * ??????????????????????????????
     * @param itemVo
     * @return
     */
    private Material createNewMaterial(DepotItemVo itemVo){
        Material material = new Material();
        material.setName(itemVo.getMaterialName());
        material.setModel(itemVo.getMaterialModel());
        material.setUnit(BusinessConstants.MATERIAL_DATA_UNIT);
        material.setMfrs(SupplierNameEnum.ZHENGXUAN.getValue());
        material.setTenantId(itemVo.getTenantId());
        return material;
    }

    /**
     * ?????????????????????????????????sku
     * @param material
     * @param itemVo
     * @return
     */
    private MaterialExtend createNewMaterialExtend(Material material,DepotItemVo itemVo){
        MaterialExtend itemMaterialExtend = new MaterialExtend();
        itemMaterialExtend.setBarCode(materialService.getMaxBarCode()+1);
        itemMaterialExtend.setCommodityUnit(BusinessConstants.MATERIAL_DATA_UNIT);
        itemMaterialExtend.setMaterialId(material.getId());
        itemMaterialExtend.setSku(itemVo.getStandards());
        itemMaterialExtend.setTenantId(itemVo.getTenantId());
        return itemMaterialExtend;
    }

    private Supplier createNewSupplier(DepotInfoVo infoVo){
        Supplier supplier = new Supplier();
        supplier.setSupplier(infoVo.getOrgan());
        supplier.setType(SupplierType.CUSTOMER.getValue());
        supplier.setEnabled(true);
        supplier.setAdvanceIn(BigDecimal.ZERO);
        supplier.setTenantId(infoVo.getTenantId());
        return supplier;
    }
}

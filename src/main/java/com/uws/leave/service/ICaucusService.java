package com.uws.leave.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.leave.CaucusHandle;
import com.uws.domain.leave.LeaveInfo;
import com.uws.domain.leave.VLeaveInfo;

public interface ICaucusService extends IBaseService {

	/**
	 * 党团关系列表查询
	 * @param po
	 * @param pageNo
	 * @param pageSize
	 * @return Page
	 */
	Page queryCaucusListPage(CaucusHandle po, int pageNo, int pageSize, HttpServletRequest request);

	/**
	 * 保存党团关系办理信息
	 * @param caucus
	 * @param stuIds
	 */
	void saveCaucusHandleInfo(CaucusHandle caucus, String stuIds);

	/**
	 * 批量取消党团关系办理信息
	 * @param studentIds
	 */
	void delCaucusHandle(String studentIds);

	/**
	 * 单个取消
	 * @param id
	 */
	void delCancusHandleById(String id);

	/**
	 * 通过学生id获得学生离校信息
	 * @param userId
	 * @return
	 */
	LeaveInfo getLeaveInfoByStuId(String userId);
	
	/***
	 * 单个办理
	 * @param id
	 */
	public void operateCaucus(String studentId, String command);
	
	/**
	 * 
	 * @Description: 办理年份信息 封装返回调用 离校模块调用
	 * @return
	 * @throws
	 */
	public List<String> listCaucusYearList();
	
	/***
	 * 个人党团关系
	 * @param studentId
	 * @return
	 */
	public CaucusHandle getCaucusHandleByStuId(String studentId);
	
	
	/**
	 * 
	 * @Title: queryCaucusById
	 * @Description: 主键查询
	 * @param id
	 * @return
	 * @throws
	 */
	public CaucusHandle queryCaucusById(String id);
	
	/**
	 * 
	 * @Title: importStudyWarningData
	 * @Description: 党团关系导入
	 * @param filePath
	 * @param excelId
	 * @param initData
	 * @param clazz
	 * @throws Exception
	 * @throws
	 */
	public void importCaucusData(String filePath, String excelId, Map initData,Class<CaucusHandle> clazz) throws Exception;
    
	/**
	 * 
	 * @Title: ICaucusService.java 
	 * @Package com.uws.leave.service 
	 * @Description: 获取离校对象
	 * @author LiuChen 
	 * @date 2016-1-20 上午10:18:28
	 */
	public VLeaveInfo getVLeaveInfoByStudentId(String userId);

}

package com.uws.leave.dao;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.leave.CaucusHandle;
import com.uws.domain.leave.LeaveInfo;
import com.uws.domain.leave.VLeaveInfo;

public interface ICaucusDao extends IBaseDao {
	
	/**
	 * 党团关系列表查询
	 * @param po
	 * @param pageNo
	 * @param pageSize
	 * @return Page
	 */
	Page queryCaucusListPage(CaucusHandle po, int pageNo, int pageSize, HttpServletRequest request);

	/**
	 * 通过学生的Id获得党团关系信息
	 * @param id
	 * @return
	 */
	CaucusHandle getCaucusHandleByStuId(String id);

	/**
	 * 通过学生id获得该学生离校办理情况
	 * @param id
	 * @return
	 */
	LeaveInfo getLeaveInfoByStuId(String id);
	
	/**
	 * 
	 * @Description: 办理年份信息 封装返回调用 离校模块调用
	 * @return
	 * @throws
	 */
	public List<String> listCaucusYearList();
	
	/***
	 * 通过id查询党团办理
	 * @param id
	 * @return
	 */
	public CaucusHandle getCaucusHandleById(String id);

	public VLeaveInfo getVLeaveInfoByStudentId(String userId);

}

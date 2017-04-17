package com.uws.leave.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.leave.LeaveDorm;
import com.uws.domain.leave.LeaveInfo;
import com.uws.domain.leave.LeaveSchool;
import com.uws.domain.leave.LifeInfo;
import com.uws.domain.leave.VLeaveInfo;
import com.uws.domain.orientation.StudentInfoModel;

/***
 * @author Jiangbl
 * @date 2015-11-12
 */
public interface ILeaveService extends IBaseService {
	/***
	 * 查询离校办理
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryLeavePage(int pageNum, int pageSize, LeaveInfo leave, HttpServletRequest request);
	
	/****
	 * 办理离校
	 * @param studentId
	 */
	public void operateLeave(String studentId);
	
	/****
	 * 取消办理离校
	 * @param studentId
	 */
	public void cancelLeave(String studentId);
	
	/**
	 * 
	 * @Title: ILeaveService.java 
	 * @Package com.uws.leave.service 
	 * @Description: 根据学生id查询水电费情况
	 * @author LiuChen 
	 * @date 2016-1-19 下午2:22:47
	 */
	public LifeInfo queryLifeInfoByStudenntId(String studentId);
	
	/***
	 * 办理(单个)
	 * @param studentId
	 * @param type
	 * @return
	 */
	public void singleOperateLeave(String studentId, String type);
	
	/***
	 * 取消办理(单个)
	 * @param studentId
	 * @param type
	 * @return
	 */
	public void singleCancelLeave(String studentId, String type);
	
	/**
	 * 
	 * @Description: 办理年份信息 封装返回调用 离校模块调用
	 * @return
	 * @throws
	 */
	public List<String> listLeaveYearList();

	/**
	 * 导入数据
	 * @param paramList
	 */
	public void importLifeInfoData(List<LifeInfo> paramList,HttpServletRequest request);
	
	public Page queryLifeInfoPage(int pageNo, int pageSize,LifeInfo leave, HttpServletRequest request);

	public void deleteLifeInfoById(String[] ids);

	public Page queryLeaveInfoPage(int pageNo, int pageSize,VLeaveInfo leaveInfo, HttpServletRequest request);
	/***
	 * 查询离校(宿管)
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryLeaveDormPage(int pageNum, int pageSize, LeaveDorm leave, HttpServletRequest request);


	/***
	 * 宿管办理
	 * @param studentId
	 * @param command
	 */
	public void operateDorm(String studentId, String command);
	
	/***
	 * 二级学院办理
	 * @param studentId
	 * @param command
	 */
	public void operateCollege(String studentId, String command);
	
	/**
	 * 
	 * @Title: ICaucusService.java 
	 * @Package com.uws.leave.service 
	 * @Description: 获取离校对象
	 * @author LiuChen 
	 * @date 2016-1-20 上午10:18:28
	 */
	public VLeaveInfo getVLeaveInfoByStudentId(String userId);
	
	/***
	 * 查询离校办理 2016-5-17
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryLeaveSchoolPage(int pageNum, int pageSize, LeaveSchool leave, HttpServletRequest request);
	
	/****
	 * 办理离校 2016-5-17
	 * @param studentId
	 */
	public void operateLeaveSchool(String studentId, String status);
	
	/**
	 * @Description: 办理年份信息 封装返回调用 离校模块调用
	 * @return  2016-5-17
	 * @throws
	 */
	public List<String> listLeaveSchoolYearList();
	
	/***
	 * 查询所有未发起的离校信息  2016-5-17
	 * @param leave
	 * @return
	 */
	public List<LeaveSchool> queryLeaveSchoolList(LeaveSchool leave, HttpServletRequest request);
	
	/***
	 * 查询党员办理 2016-5-17
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryCaucusPage(int pageNum, int pageSize, LeaveSchool leave, HttpServletRequest request);
	
	/****
	 * 离校党关系办理 2016-5-17
	 * @param studentId
	 * 状态 ：0 未发起 ;1  发起   ;2 办结
	 */
	public void operateCaucus(String studentId, String status);
	
	/***
	 * 查询户口办理 2016-5-17
	 * @param pageNum
	 * @param pageSize
	 * @param leave  户口办理的状态：0 无需办理；1 需要办理；2 已办理
	 * @return 
	 */
	public Page querySecurityPage(int pageNum, int pageSize, LeaveSchool leave);
	
	/****
	 * 离校户口转移办理 2016-5-17
	 * @param studentId
	 * 户口办理的状态：0 无需办理；1 需要办理；2 已办理
	 */
	public void operateSecurity(String studentId, String status);
	
	/**
	 * 离校户口办理  选择学生  2016-5-17
	 * @Title: queryStudentPage
	 * @Description: 按条件分页查询
	 * @param pageNo
	 * @param pageSize
	 * @param student
	 * @return
	 * @throws
	 */
	public Page queryStudentPage(Integer pageNo,Integer pageSize,StudentInfoModel student);
	
	/****
	 * 二级学院办结离校手续 2016-5-17
	 * @param studentId
	 * 状态 ：0 未发起 ;1  发起   ;2 办结
	 */
	public void operateCollegeLeave(String studentId, String status);
	
	/***
	 * 查询离校办理(全部) 2016-5-17
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryAllLeaveSchoolPage(int pageNum, int pageSize, LeaveSchool leave, HttpServletRequest request);
	
	/***
	 * 查询学生离校情况 2016-5-17
	 * @param studentId
	 * @return
	 */
	public LeaveSchool updateAndgetLeaveSchoolByStudentId(String studentId);
	
	/**
	 * 
	 * @Title: queryQuesInfo
	 * @Description: TODO(通过证件号查询出学生信息)
	 * @param certificatCode
	 *            证件号码
	 * @return studentInfo
	 * @author wangcl
	 */
	public StudentInfoModel getStudentByCertificatCode(String certificateCode);
}

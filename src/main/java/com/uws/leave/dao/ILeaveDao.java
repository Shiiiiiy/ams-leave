package com.uws.leave.dao;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.leave.CollegeHandle;
import com.uws.domain.leave.LeaveDorm;
import com.uws.domain.leave.LeaveInfo;
import com.uws.domain.leave.LeaveSchool;
import com.uws.domain.leave.LifeInfo;
import com.uws.domain.leave.VLeaveInfo;
import com.uws.domain.orientation.StudentInfoModel;

/***
 * @author Jiangbl
 *
 */
public interface ILeaveDao extends IBaseDao{
	/***
	 * 查询离校办理
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryLeavePage(int pageNum, int pageSize,LeaveInfo leave, HttpServletRequest request);
	
	/***
	 * 查询学生离校情况
	 * @param studentId
	 * @return
	 */
	public LeaveInfo queryLeaveByStudentId(String studentId);
	
	/***
	 * 新增图书馆离校办理
	 * @param leave
	 */
	public void addLeave(LeaveInfo leave);
	
	/***
	 * 更新图书光离校办理
	 * @param leave
	 */
	public void updateLeave(LeaveInfo leave);
	
	/**
	 * 
	 * @Description: 办理年份信息 封装返回调用 离校模块调用
	 * @return
	 * @throws
	 */
	public List<String> listLeaveYearList();

	/***
	 * 查询离校(宿管)
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryLeaveDormPage(int pageNum, int pageSize, LeaveDorm leave, HttpServletRequest request);
	
	/***
	 * 查询学生离校宿管办理情况
	 * @param studentId
	 * @return
	 */
	public LeaveDorm getLeaveDormByStuId(String studentId);
	
	public void saveLifeInfo(LifeInfo lifeInfo);

	public void updateLifeInfo(LifeInfo lifeInfo);

	public Page queryLifeInfoPage(int pageNo, int pageSize, LifeInfo leave,
            HttpServletRequest request);

	public LifeInfo queryLifeInfoByStudenntId(String studentId);

	public Page queryLeaveInfoPage(int pageNo, int pageSize, VLeaveInfo leaveInfo, HttpServletRequest request);

	/**
	 * @Description: 获取离校对象
	 * @author  
	 * @date 2016-1-20 上午10:18:28
	 */
	public VLeaveInfo getVLeaveInfoByStudentId(String userId);
	
	/***
	 * 查询学生离校办理情况
	 * @param studentId
	 * @return
	 */
	public CollegeHandle getLeaveByStuId(String studentId);
	
	/***
	 * 查询离校办理 2016-5-17
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryLeaveSchoolPage(int pageNum, int pageSize, LeaveSchool leave, HttpServletRequest request);

	/***
	 * 查询学生离校情况 2016-5-17
	 * @param studentId
	 * @return
	 */
	public LeaveSchool queryLeaveSchoolByStudentId(String studentId);
	
	/***
	 * 更新离校办理状态 2016-5-17
	 * @param leave
	 */
	public void updateLeaveSchool(LeaveSchool leave);
	
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
	
	/***
	 * 查询户口办理 2016-5-17
	 * @param pageNum
	 * @param pageSize
	 * @param leave  户口办理的状态：0 无需办理；1 需要办理；2 已办理
	 * @return 
	 */
	public Page querySecurityPage(int pageNum, int pageSize, LeaveSchool leave);
	
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
	
	/***
	 * 查询离校办理(全部) 2016-5-17
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryAllLeaveSchoolPage(int pageNum, int pageSize, LeaveSchool leave, HttpServletRequest request);
	
	/**
	 * 
	 * @Title: queryQuesInfo
	 * @Description: TODO(通过证件号和当前学年查询出学生信息)
	 * @param certificatCode
	 *            证件号码
	 * @return studentInfo
	 * @author wangcl
	 */
	public StudentInfoModel getStudentByCertificatCode(String certificateCode);
}

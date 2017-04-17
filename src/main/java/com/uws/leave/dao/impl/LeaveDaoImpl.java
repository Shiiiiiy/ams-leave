package com.uws.leave.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uws.common.dao.ICommonRoleDao;
import com.uws.common.service.IStuJobTeamSetCommonService;
import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.leave.CollegeHandle;
import com.uws.domain.leave.LeaveDorm;
import com.uws.domain.leave.LeaveInfo;
import com.uws.domain.leave.LeaveSchool;
import com.uws.domain.leave.LifeInfo;
import com.uws.domain.leave.VLeaveInfo;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.leave.dao.ILeaveDao;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.util.CheckUtils;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;

/****
 * ��У����daoImpl
 * @author Jiangbl
 * @date 2015-11-12
 */

@Repository("com.uws.leave.dao.impl.LeaveDaoImpl")
public class LeaveDaoImpl extends BaseDaoImpl implements ILeaveDao{
	//字典工具类
	private DicUtil dicUtil=DicFactory.getDicUtil();
	
	@Autowired
	private IStuJobTeamSetCommonService jobTeamService;
	
	@Autowired
	private ICommonRoleDao commonRoleDao;
	
	//sessionUtil工具类
	private SessionUtil sessionUtil = SessionFactory.getSession(com.uws.sys.util.Constants.MENUKEY_SYSCONFIG);
	
	/***
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryLeavePage(int pageNum, int pageSize, LeaveInfo leave, HttpServletRequest request){
		List<Object> values = new ArrayList<Object>();
		Dic graduatedDic=this.dicUtil.getDicInfo("Y&N", "Y");// 是否毕业
		String orgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		
		StringBuffer hql = new StringBuffer("select p, t from LeaveInfo t right outer join t.student p where p.edusStatus in ?");
		values.add(ProjectConstants.STUDENT_NORMAL_STAUTS_STRING);
		
		if (DataUtil.isNotNull(leave) && DataUtil.isNotNull(leave.getStudent())) {
			if (DataUtil.isNotNull(leave.getStudent().getCollege().getId())) {//学院
				hql.append(" and p.college.id = ? ");
				values.add(leave.getStudent().getCollege().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getMajor().getId())) {// 专业
				hql.append(" and p.major.id = ? ");
				values.add(leave.getStudent().getMajor().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getClassId().getId())) {// 班级
				hql.append(" and p.classId.id = ? ");
				values.add(leave.getStudent().getClassId().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getName())) {//学生姓名   模糊查询
				hql.append(" and p.name like ? ");
				values.add("%" + HqlEscapeUtil.escape(leave.getStudent().getName()) + "%");
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getStuNumber())) {//学号   模糊查询
				hql.append(" and p.stuNumber like ? ");
				values.add("%" + HqlEscapeUtil.escape(leave.getStudent().getStuNumber()) + "%");
			}
		}
		if(DataUtil.isNotNull(leave.getYear())) {
			hql.append(" and t.year = ? ");
			values.add(leave.getYear());
		}
		//二级学院只查看自己学院的记录
		if(null!=orgId && CheckUtils.isCurrentOrgEqCollege(orgId)){
			hql.append(" and p.college.id = ? ");
			values.add(orgId);
		}
		//毕业班
		hql.append(" and p.classId.id in(select q.id from BaseClassModel q where q.isGraduatedDic.id='"+graduatedDic.getId()+"') ");
		
		hql.append(" order by p.college.id, p.major.id, p.classId.id desc ");
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNum, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNum, pageSize,values.toArray());
		}
	}
	
	/***
	 * 查询学生离校情况
	 * @param studentId
	 * @return
	 */
	public LeaveInfo queryLeaveByStudentId(String studentId){
		List<LeaveInfo> list=this.query("from LeaveInfo t where t.student.id = ? ", new Object[]{studentId});
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	/***
	 * 新增图书馆离校办理
	 * @param leave
	 */
	public void addLeave(LeaveInfo leave){
		this.save(leave);
	}
	
	/***
	 * 更新图书光离校办理
	 * @param leave
	 */
	public void updateLeave(LeaveInfo leave){
		this.update(leave);
	}
	
	/**
	 * 
	 * @Description: 毕业年份信息 封装返回调用 离校模块调用
	 * @return
	 * @throws
	 */
	@SuppressWarnings("unchecked")
    public List<String> listLeaveYearList(){
		return this.query("select distinct year from LeaveInfo order by year desc ");
	}
	
	/**
	 * 
	 * @Description:保存水电费信息
	 * @author LiuChen  
	 * @date 2016-1-19 上午11:13:50
	 */
	@Override
	public void saveLifeInfo(LifeInfo lifeInfo)
	{
	    this.save(lifeInfo);
	}
	
	@Override
	public void updateLifeInfo(LifeInfo lifeInfo)
	{
	    this.update(lifeInfo);
	}
	
	@Override
	public Page queryLifeInfoPage(int pageNo, int pageSize, LifeInfo leave,
	        HttpServletRequest request)
	{
		 List<Object> values = new ArrayList<Object>();
	     StringBuffer hql = new StringBuffer("from LifeInfo l where 1=1");
	    if(null != leave)
	    {   
			StudentInfoModel student = leave.getStudent();
			if( null!= student)
			{
				// 学院
				if (null != student.getCollege() && !StringUtils.isEmpty(student.getCollege().getId())) 
				{
					hql.append(" and l.student.college = ? ");
					values.add(student.getCollege());
				}
				// 专业
				if (null != student.getMajor() && !StringUtils.isEmpty(student.getMajor().getId())) 
				{
					hql.append(" and l.student.major = ? ");
					values.add(student.getMajor());
				}
				// 班级
				if (null != student.getClassId() && !StringUtils.isEmpty(student.getClassId().getId())) 
				{
					hql.append(" and l.student.classId = ? ");
					values.add(student.getClassId());
				}
				// 学号
				if (!StringUtils.isEmpty(student.getStuNumber())) {
					hql.append(" and l.student.stuNumber like ? ");
					if (HqlEscapeUtil.IsNeedEscape(student.getStuNumber())) {
						values.add("%" + HqlEscapeUtil.escape(student.getStuNumber()) + "%");
						hql.append(HqlEscapeUtil.HQL_ESCAPE);
					} else
						values.add("%" + student.getStuNumber() + "%");

				}
				// 姓名
				if (!StringUtils.isEmpty(student.getName())) {
					hql.append(" and l.student.name like ? ");
					if (HqlEscapeUtil.IsNeedEscape(student.getName())) {
						values.add("%" + HqlEscapeUtil.escape(student.getName()) + "%");
						hql.append(HqlEscapeUtil.HQL_ESCAPE);
					} else
						values.add("%" + student.getName() + "%");
				}
			}
			
			if(leave.getYear() !=null && !StringUtils.isEmpty(leave.getYear().getId()))
			{
				hql.append(" and l.year.id = ? ");
				values.add(leave.getYear().getId());
			}
		
	    }
	     
	     hql.append(" order by l.updateTime desc");
	     if (values.size() == 0) {
		      return pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		 }
		      return pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}
	
	
	@Override
	public LifeInfo queryLifeInfoByStudenntId(String studentId)
	{
		return (LifeInfo)this.queryUnique("from LifeInfo where student.id=?", new Object[] {studentId});
	}
	
	@Override
	public Page queryLeaveInfoPage(int pageNo, int pageSize, VLeaveInfo leaveInfo, HttpServletRequest request){
		List<Object> values = new ArrayList<Object>();
		String orgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		Dic statusDic=this.dicUtil.getDicInfo("LEAVE_HANDLE_STATUS", "HANDLE");// 办理状态
		StringBuffer hql = new StringBuffer(" from VLeaveInfo t where 1=1 ");
		
		if (DataUtil.isNotNull(leaveInfo) && DataUtil.isNotNull(leaveInfo.getStudent())) {
			if (DataUtil.isNotNull(leaveInfo.getStudent().getCollege().getId())) {//学院
				hql.append(" and t.student.college.id = ? ");
				values.add(leaveInfo.getStudent().getCollege().getId());
			}
			
			if (DataUtil.isNotNull(leaveInfo.getStudent().getMajor().getId())) {// 专业
				hql.append(" and t.student.major.id = ? ");
				values.add(leaveInfo.getStudent().getMajor().getId());
			}
			
			if (DataUtil.isNotNull(leaveInfo.getStudent().getClassId().getId())) {// 班级
				hql.append(" and t.student.classId.id = ? ");
				values.add(leaveInfo.getStudent().getClassId().getId());
			}
			
			if (DataUtil.isNotNull(leaveInfo.getStudent().getName())) {//学生姓名   模糊查询
				hql.append(" and t.student.name like ? ");
				values.add("%" + HqlEscapeUtil.escape(leaveInfo.getStudent().getName()) + "%");
			}
			
			if (DataUtil.isNotNull(leaveInfo.getStudent().getStuNumber())) {//学号   模糊查询
				hql.append(" and t.student.stuNumber like ? ");
				values.add("%" + HqlEscapeUtil.escape(leaveInfo.getStudent().getStuNumber()) + "%");
			}
		}
		if(DataUtil.isNotNull(leaveInfo.getYearId())) {
			hql.append(" and t.yearId = ? ");
			values.add(leaveInfo.getYearId());
		}
		if(DataUtil.isNotNull(leaveInfo.getStatus())) {
			if((leaveInfo.getStatus()).equals(statusDic.getId())){//已办理状态
				hql.append(" and t.status = ? ");
				values.add(leaveInfo.getStatus());
			}else{
				hql.append("and ( t.status is null or  t.status = ? )");
				values.add(leaveInfo.getStatus());
			}
		}
		//二级学院只查看自己学院的记录
		if(null!=orgId && CheckUtils.isCurrentOrgEqCollege(orgId)){
			hql.append(" and t.student.college.id = ? ");
			values.add(orgId);
		}
		
		hql.append(" order by t.student desc ");
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());
		}
	}

	/***
	 * 查询离校(宿管)
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryLeaveDormPage(int pageNum, int pageSize, LeaveDorm leave, HttpServletRequest request){
		List<Object> values = new ArrayList<Object>();
		Dic graduatedDic=this.dicUtil.getDicInfo("Y&N", "Y");// 是否毕业
		Dic statusDic=this.dicUtil.getDicInfo("LEAVE_HANDLE_STATUS", "HANDLE");// 办理状态
		String orgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		
		StringBuffer hql = new StringBuffer("select p, t from LeaveDorm t right outer join t.student p where p.edusStatus in ?");
		values.add(ProjectConstants.STUDENT_NORMAL_STAUTS_STRING);
		
		if (DataUtil.isNotNull(leave) && DataUtil.isNotNull(leave.getStudent())) {
			if (DataUtil.isNotNull(leave.getStudent().getCollege().getId())) {//学院
				hql.append(" and p.college.id = ? ");
				values.add(leave.getStudent().getCollege().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getMajor().getId())) {// 专业
				hql.append(" and p.major.id = ? ");
				values.add(leave.getStudent().getMajor().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getClassId().getId())) {// 班级
				hql.append(" and p.classId.id = ? ");
				values.add(leave.getStudent().getClassId().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getName())) {//学生姓名   模糊查询
				hql.append(" and p.name like ? ");
				values.add("%" + HqlEscapeUtil.escape(leave.getStudent().getName()) + "%");
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getStuNumber())) {//学号   模糊查询
				hql.append(" and p.stuNumber like ? ");
				values.add("%" + HqlEscapeUtil.escape(leave.getStudent().getStuNumber()) + "%");
			}
		}
		if(DataUtil.isNotNull(leave.getYear()) && DataUtil.isNotNull(leave.getYear().getId())) {
			hql.append(" and t.year.id = ? ");
			values.add(leave.getYear().getId());
		}
		if(DataUtil.isNotNull(leave.getStatus()) && DataUtil.isNotNull(leave.getStatus().getId())) {
			if(leave.getStatus().getId().equals(statusDic.getId())){//已办理状态
				hql.append(" and t.status.id = ? ");
				values.add(leave.getStatus().getId());
			}else{
				hql.append("and ( t.status.id is null or  t.status.id = ? )");
				values.add(leave.getStatus().getId());
			}
		}
		//二级学院只查看自己学院的记录
		if(null!=orgId && CheckUtils.isCurrentOrgEqCollege(orgId)){
			hql.append(" and p.college.id = ? ");
			values.add(orgId);
		}
		//毕业班
		hql.append(" and p.classId.id in(select q.id from BaseClassModel q where q.isGraduatedDic.id='"+graduatedDic.getId()+"') ");
		
		hql.append(" order by p.college.id, p.major.id, p.classId.id desc ");
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNum, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNum, pageSize,values.toArray());
		}
	}

	/***
	 * 查询学生离校宿管办理情况
	 * @param studentId
	 * @return
	 */
	public LeaveDorm getLeaveDormByStuId(String studentId){
		List<LeaveDorm> list=this.query("from LeaveDorm t where t.student.id = ? ", new Object[]{studentId});
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public VLeaveInfo getVLeaveInfoByStudentId(String userId){
		return (VLeaveInfo)this.queryUnique("from VLeaveInfo where student.id=?", new Object[] {userId});
	}
	
	/***
	 * 查询学生离校办理情况
	 * @param studentId
	 * @return
	 */
	public CollegeHandle getLeaveByStuId(String studentId){
		List<CollegeHandle> list=this.query("from CollegeHandle t where t.student.id = ? ", new Object[]{studentId});
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	/***
	 * 查询离校办理 2016-5-17
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return 
	 */
	public Page queryLeaveSchoolPage(int pageNum, int pageSize, LeaveSchool leave, HttpServletRequest request){
		List<Object> values = new ArrayList<Object>();
		String orgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		String currentUserId=this.sessionUtil.getCurrentUserId();
		StringBuffer hql = new StringBuffer("from LeaveSchool t where (t.graduateStatus is null or t.graduateStatus='') "); //过滤掉已毕业学生
		
		if (DataUtil.isNotNull(leave) && DataUtil.isNotNull(leave.getStudent())) {
			if (DataUtil.isNotNull(leave.getStudent().getCollege().getId())) {//学院
				hql.append(" and t.student.college.id = ? ");
				values.add(leave.getStudent().getCollege().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getMajor().getId())) {// 专业
				hql.append(" and t.student.major.id = ? ");
				values.add(leave.getStudent().getMajor().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getClassId().getId())) {// 班级
				hql.append(" and t.student.classId.id = ? ");
				values.add(leave.getStudent().getClassId().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getName())) {//学生姓名  模糊查询
				hql.append(" and t.student.name like ? ");
				values.add("%" + HqlEscapeUtil.escape(leave.getStudent().getName()) + "%");
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getStuNumber())) {//学生学号 模糊查询
				hql.append(" and t.student.stuNumber like ? ");
				values.add("%" + HqlEscapeUtil.escape(leave.getStudent().getStuNumber()) + "%");
			}
		}
		if(DataUtil.isNotNull(leave.getYear())) {
			hql.append(" and t.year = ? ");
			values.add(leave.getYear());
		}
		//学生处查询全部  否则只是查询本学院相关数据
		if(null!=orgId && CheckUtils.isCurrentOrgEqCollege(orgId)){
			hql.append(" and t.student.college.id = ? ");
			values.add(orgId);
		}
		
		if(this.commonRoleDao.getRoleByCode(currentUserId, "HKY_LEAVE_COLLEGE_ADMIN") != null){
			System.out.println("离校二级学院管理员");
		}else if(this.jobTeamService.isTeacherCounsellor(currentUserId)){
			//教学辅导员只能查看自己班级列表
			hql.append(" and t.student.classId.id in "+this.getCollegeIds(currentUserId));
		}
		
		hql.append(" order by t.student desc ");
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNum, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNum, pageSize,values.toArray());
		}
	}
	
	/***
	 * 查询学生离校情况 2016-5-17
	 * @param studentId
	 * @return
	 */
	public LeaveSchool queryLeaveSchoolByStudentId(String studentId){
		List<LeaveSchool> list=this.query("from LeaveSchool t where t.student.id = ? ", new Object[]{studentId});
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	/***
	 * 更新离校办理状态 2016-5-17
	 * @param leave
	 */
	public void updateLeaveSchool(LeaveSchool leave){
		this.update(leave);
	}
	
	/**
	 * @Description: 办理年份信息 封装返回调用 离校模块调用
	 * @return  2016-5-17
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public List<String> listLeaveSchoolYearList(){
		return this.query("select distinct year from LeaveSchool order by year desc ");
	}
	
	/***
	 * 查询所有未发起的离校信息  2016-5-17
	 * @param leave
	 * @return
	 */
	public List<LeaveSchool> queryLeaveSchoolList(LeaveSchool leave, HttpServletRequest request){
		List<Object> values = new ArrayList<Object>();
		String orgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		
		StringBuffer hql = new StringBuffer("from LeaveSchool t where (t.graduateStatus is null or t.graduateStatus = '')"); //过滤掉已毕业学生
		
		hql.append(" and t.status = '0' ");//未发起状态
		
		if (DataUtil.isNotNull(leave) && DataUtil.isNotNull(leave.getStudent())) {
			if (DataUtil.isNotNull(leave.getStudent().getCollege().getId())) {//学院
				hql.append(" and t.student.college.id = ? ");
				values.add(leave.getStudent().getCollege().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getMajor().getId())) {// 专业
				hql.append(" and t.student.major.id = ? ");
				values.add(leave.getStudent().getMajor().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getClassId().getId())) {// 班级
				hql.append(" and t.student.classId.id = ? ");
				values.add(leave.getStudent().getClassId().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getName())) {//学生姓名  模糊查询
				hql.append(" and t.student.name like ? ");
				values.add("%" + HqlEscapeUtil.escape(leave.getStudent().getName()) + "%");
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getStuNumber())) {//学生学号 模糊查询
				hql.append(" and t.student.stuNumber like ? ");
				values.add("%" + HqlEscapeUtil.escape(leave.getStudent().getStuNumber()) + "%");
			}
		}
		if(DataUtil.isNotNull(leave.getYear())) {
			hql.append(" and t.year = ? ");
			values.add(leave.getYear());
		}
		//学生处查询全部  否则只是查询本学院相关数据
		if(null!=orgId && CheckUtils.isCurrentOrgEqCollege(orgId)){
			hql.append(" and t.student.college.id = ? ");
			values.add(orgId);
		}
		
		hql.append(" order by t.student desc ");
		List<LeaveSchool> list=this.query(hql.toString(), values.toArray());
		return list;
	}
	
	/***
	 * 查询党员办理 2016-5-17
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryCaucusPage(int pageNum, int pageSize, LeaveSchool leave, HttpServletRequest request){
		List<Object> values = new ArrayList<Object>();
		String orgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		
		StringBuffer hql = new StringBuffer("from LeaveSchool t where (t.graduateStatus is null or t.graduateStatus='') "); //过滤掉已毕业学生
		hql.append(" and t.student.politicalDic in (?,?)");
		values.add(dicUtil.getDicInfo("SCH_POLITICAL_STATUS", "01"));
		values.add(dicUtil.getDicInfo("SCH_POLITICAL_STATUS", "02"));
		if (DataUtil.isNotNull(leave) && DataUtil.isNotNull(leave.getStudent())) {
			if (DataUtil.isNotNull(leave.getStudent().getCollege().getId())) {//学院
				hql.append(" and t.student.college.id = ? ");
				values.add(leave.getStudent().getCollege().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getMajor().getId())) {// 专业
				hql.append(" and t.student.major.id = ? ");
				values.add(leave.getStudent().getMajor().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getClassId().getId())) {// 班级
				hql.append(" and t.student.classId.id = ? ");
				values.add(leave.getStudent().getClassId().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getName())) {//学生姓名  模糊查询
				hql.append(" and t.student.name like ? ");
				values.add("%" + HqlEscapeUtil.escape(leave.getStudent().getName()) + "%");
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getStuNumber())) {//学生学号 模糊查询
				hql.append(" and t.student.stuNumber like ? ");
				values.add("%" + HqlEscapeUtil.escape(leave.getStudent().getStuNumber()) + "%");
			}
		}
		if(DataUtil.isNotNull(leave.getYear())) {
			hql.append(" and t.year = ? ");
			values.add(leave.getYear());
		}
		//学生处查询全部  否则只是查询本学院相关数据
		if(null!=orgId && CheckUtils.isCurrentOrgEqCollege(orgId)){
			hql.append(" and t.student.college.id = ? ");
			values.add(orgId);
		}
		
		hql.append(" order by t.student desc ");
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNum, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNum, pageSize,values.toArray());
		}
	}
	
	/***
	 * 查询户口办理 2016-5-17
	 * @param pageNum
	 * @param pageSize
	 * @param leave  户口办理的状态：0 无需办理；1 需要办理；2 已办理
	 * @return 
	 */
	public Page querySecurityPage(int pageNum, int pageSize, LeaveSchool leave){
		List<Object> values = new ArrayList<Object>();
		
		StringBuffer hql = new StringBuffer("from LeaveSchool t where (t.graduateStatus is null or t.graduateStatus='') "); //过滤掉已毕业学生
		hql.append(" and t.security in ('1','2')");
		
		if (DataUtil.isNotNull(leave) && DataUtil.isNotNull(leave.getStudent())) {
			if (DataUtil.isNotNull(leave.getStudent().getCollege().getId())) {//学院
				hql.append(" and t.student.college.id = ? ");
				values.add(leave.getStudent().getCollege().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getMajor().getId())) {// 专业
				hql.append(" and t.student.major.id = ? ");
				values.add(leave.getStudent().getMajor().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getClassId().getId())) {// 班级
				hql.append(" and t.student.classId.id = ? ");
				values.add(leave.getStudent().getClassId().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getName())) {//学生姓名  模糊查询
				hql.append(" and t.student.name like ? ");
				values.add("%" + HqlEscapeUtil.escape(leave.getStudent().getName()) + "%");
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getStuNumber())) {//学生学号 模糊查询
				hql.append(" and t.student.stuNumber like ? ");
				values.add("%" + HqlEscapeUtil.escape(leave.getStudent().getStuNumber()) + "%");
			}
		}
		if(DataUtil.isNotNull(leave.getYear())) {
			hql.append(" and t.year = ? ");
			values.add(leave.getYear());
		}
		
		hql.append(" order by t.student desc ");
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNum, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNum, pageSize,values.toArray());
		}
	}
	
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
	public Page queryStudentPage(Integer pageNo,Integer pageSize,StudentInfoModel student){
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select p from LeaveSchool t left join t.student p where 1=1  ");
		hql.append(" and (t.graduateStatus is null or t.graduateStatus='') and t.security = '0' ");
		
		if(null != student){
			if(!StringUtils.isEmpty(student.getName())){
				hql.append(" and p.name like ? ");
				if(HqlEscapeUtil.IsNeedEscape(student.getName())){
					values.add("%" + HqlEscapeUtil.escape(student.getName()) + "%");
					hql.append(HqlEscapeUtil.HQL_ESCAPE);
				}else{
					values.add("%" +student.getName()+ "%");
				}
			}
			if(!StringUtils.isEmpty(student.getStuNumber())){
				hql.append(" and p.stuNumber like ? ");
				if(HqlEscapeUtil.IsNeedEscape(student.getStuNumber())){
					values.add("%" + HqlEscapeUtil.escape(student.getStuNumber()) + "%");
					hql.append(HqlEscapeUtil.HQL_ESCAPE);
				}else{
					values.add("%" +student.getStuNumber()+ "%");
				}
			}
			//学院单位
			if(null != student.getCollege() && !StringUtils.isEmpty(student.getCollege().getId())){
				hql.append(" and p.college.id = ? ");
				values.add(student.getCollege().getId());
			}
			//专业
			if(null != student.getMajor() && !StringUtils.isEmpty(student.getMajor().getId())){
				hql.append(" and p.major.id = ? ");
				values.add(student.getMajor().getId());
			}
			//班级
			if(null != student.getClassId() && !StringUtils.isEmpty(student.getClassId().getId())){
				hql.append(" and p.classId.id = ? ");
				values.add(student.getClassId().getId());
			}
		}
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());
		}
	}
	
	/***
	 * 查询离校办理(全部) 2016-5-17
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryAllLeaveSchoolPage(int pageNum, int pageSize, LeaveSchool leave, HttpServletRequest request){
		List<Object> values = new ArrayList<Object>();
		String orgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		
		StringBuffer hql = new StringBuffer("from LeaveSchool t where 1=1 "); //过滤掉已毕业学生
		
		if (DataUtil.isNotNull(leave) && DataUtil.isNotNull(leave.getStudent())) {
			if (DataUtil.isNotNull(leave.getStudent().getCollege().getId())) {//学院
				hql.append(" and t.student.college.id = ? ");
				values.add(leave.getStudent().getCollege().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getMajor().getId())) {// 专业
				hql.append(" and t.student.major.id = ? ");
				values.add(leave.getStudent().getMajor().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getClassId().getId())) {// 班级
				hql.append(" and t.student.classId.id = ? ");
				values.add(leave.getStudent().getClassId().getId());
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getName())) {//学生姓名  模糊查询
				hql.append(" and t.student.name like ? ");
				values.add("%" + HqlEscapeUtil.escape(leave.getStudent().getName()) + "%");
			}
			
			if (DataUtil.isNotNull(leave.getStudent().getStuNumber())) {//学生学号 模糊查询
				hql.append(" and t.student.stuNumber like ? ");
				values.add("%" + HqlEscapeUtil.escape(leave.getStudent().getStuNumber()) + "%");
			}
		}
		if(DataUtil.isNotNull(leave.getYear())) {
			hql.append(" and t.year = ? ");
			values.add(leave.getYear());
		}
		//学生处查询全部  否则只是查询本学院相关数据
		if(null!=orgId && CheckUtils.isCurrentOrgEqCollege(orgId)){
			hql.append(" and t.student.college.id = ? ");
			values.add(orgId);
		}
		
		//办理状态查询
		if(DataUtil.isNotNull(leave.getStatus())) {
			if("3".equals(leave.getStatus())){
				//新增需求，添加‘未办结’状态包含‘未发起’、‘发起’状态
				hql.append(" and t.status <> 2");
			}else{
				hql.append(" and t.status = ? ");
				values.add(leave.getStatus());
			}
		}
		
		//缴费状态(计财处)
		if (DataUtil.isNotNull(leave.getFinance())) {//学生学号 模糊查询
			hql.append(" and t.finance like ? ");
			values.add("%" + HqlEscapeUtil.escape(leave.getFinance()) + "%");
		}
		
		//离校原因
		if (DataUtil.isNotNull(leave.getReason())) {//学生学号 模糊查询
			hql.append(" and t.reason like ? ");
			values.add("%" + HqlEscapeUtil.escape(leave.getReason()) + "%");
		}
		
		hql.append(" order by t.student desc ");
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNum, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNum, pageSize,values.toArray());
		}
	}
	
	/**
	 * 
	 * @Title: queryQuesInfo
	 * @Description: TODO(通过证件号和当前学年查询出学生信息)
	 * @param certificatCode
	 *            证件号码
	 * @return studentInfo
	 * @author wangcl
	 */
	public StudentInfoModel getStudentByCertificatCode(String certificateCode){
		String hql = "from StudentInfoModel stu where stu.certificateCode = ? ";
		Object object = this.queryUnique(hql, new Object[] { certificateCode });

		return DataUtil.isNotNull(object) ? (StudentInfoModel) object : null;
	}
	
	private String getCollegeIds(String userId) {
		List<BaseClassModel> list=this.jobTeamService.queryBaseClassModelByTCId(userId);
		StringBuffer sbff = new StringBuffer();
		sbff.append(" (");
		for (int i = 0; i < list.size(); i++) {
			if(list.size()-1==i){
				sbff.append("'"+list.get(i).getId()+"'");
			}else{
				sbff.append("'"+list.get(i).getId()+"'").append(",");
			}
		} 
		sbff.append(")");

		return sbff.toString();
	}
}

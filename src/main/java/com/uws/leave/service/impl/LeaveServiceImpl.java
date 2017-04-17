package com.uws.leave.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.common.dao.IStudentCommonDao;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.SchoolYearUtil;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.leave.CollegeHandle;
import com.uws.domain.leave.LeaveDorm;
import com.uws.domain.leave.LeaveInfo;
import com.uws.domain.leave.LeaveSchool;
import com.uws.domain.leave.LifeInfo;
import com.uws.domain.leave.VLeaveInfo;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.leave.dao.ILeaveDao;
import com.uws.leave.service.ILeaveService;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.webservice.IBookService;
import com.uws.webservice.IFeeServcie;

/***
 * @author Jiangbl
 * @date 2015-11-12
 */

@Service("com.uws.leave.service.impl.LeaveServiceImpl")
public class LeaveServiceImpl extends BaseServiceImpl implements ILeaveService{
	
	@Autowired 
	private ILeaveDao leaveDao;
	@Autowired
	private IStudentCommonDao commonStudentDao;
	
	@Autowired
	private IStudentCommonService studentCommonService;
	
	@Autowired
	private IFeeServcie feeService;
	
	@Autowired
	private IBookService bookService;
	
	//字典工具类
	private DicUtil dicUtil=DicFactory.getDicUtil();
	
	/***
	 * 查询离校办理
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryLeavePage(int pageNum, int pageSize, LeaveInfo leave, HttpServletRequest request){
		return this.leaveDao.queryLeavePage(pageNum, pageSize, leave, request);
	}
	
	/****
	 * 办理离校
	 * @param studentId
	 */
	public void operateLeave(String studentId){
		LeaveInfo leave=this.leaveDao.queryLeaveByStudentId(studentId);
		Dic handle=this.dicUtil.getDicInfo("Y&N", "Y");// 办理
		Calendar a=Calendar.getInstance();
		if(null != leave){//修改
			leave.setLibrary(handle);//图书馆
			leave.setDorm(handle);//宿舍
			leave.setFinance(handle);//财务
			leave.setEdu(handle);//教务
			leave.setCollege(handle);//二级学院
			leave.setYear(a.get(Calendar.YEAR)+"");
			this.leaveDao.updateLeave(leave);
		}else{//新增
			leave=new LeaveInfo();
			StudentInfoModel student=new StudentInfoModel();
			student.setId(studentId);
			
			leave.setStudent(student);
			leave.setLibrary(handle);//图书馆
			leave.setDorm(handle);//宿舍
			leave.setFinance(handle);//财务
			leave.setEdu(handle);//教务
			leave.setCollege(handle);//二级学院
			leave.setYear(a.get(Calendar.YEAR)+"");
			this.leaveDao.addLeave(leave);
		}
	}

	/****
	 * 取消办理离校
	 * @param studentId
	 */
	public void cancelLeave(String studentId){
		LeaveInfo leave=this.leaveDao.queryLeaveByStudentId(studentId);
		Dic handle=this.dicUtil.getDicInfo("Y&N", "N");// 取消
		Calendar a=Calendar.getInstance();
		if(null != leave){//修改
			leave.setLibrary(handle);//图书馆
			leave.setDorm(handle);//宿舍
			leave.setFinance(handle);//财务
			leave.setEdu(handle);//教务
			leave.setCollege(handle);//二级学院
			this.leaveDao.updateLeave(leave);
		}else{//新增
			leave=new LeaveInfo();
			StudentInfoModel student=new StudentInfoModel();
			student.setId(studentId);
			
			leave.setStudent(student);
			leave.setLibrary(handle);//图书馆
			leave.setDorm(handle);//宿舍
			leave.setFinance(handle);//财务
			leave.setEdu(handle);//教务
			leave.setCollege(handle);//二级学院
			leave.setYear(a.get(Calendar.YEAR)+"");
			this.leaveDao.addLeave(leave);
		}
	}
	
	/***
	 * 取消办理(单个)
	 * @param studentId
	 * @param type
	 * @return
	 */
	public void singleOperateLeave(String studentId, String type){
		LeaveInfo leave=this.leaveDao.queryLeaveByStudentId(studentId);
		Dic handle=this.dicUtil.getDicInfo("Y&N", "Y");// 办理
		Calendar a=Calendar.getInstance();
		if(null != leave){//修改
			if("library".equals(type)){
				leave.setLibrary(handle);//图书馆
			}else if("dorm".equals(type)){
				leave.setDorm(handle);//宿舍
			}else if("finance".equals(type)){
				leave.setFinance(handle);//财务
			}else if("edu".equals(type)){
				leave.setEdu(handle);//教务
			}else if("college".equals(type)){
				leave.setCollege(handle);//二级学院
			}
			this.leaveDao.updateLeave(leave);
		}else{//新增
			leave=new LeaveInfo();
			StudentInfoModel student=new StudentInfoModel();
			student.setId(studentId);
			
			leave.setStudent(student);
			if("library".equals(type)){
				leave.setLibrary(handle);//图书馆
			}else if("dorm".equals(type)){
				leave.setDorm(handle);//宿舍
			}else if("finance".equals(type)){
				leave.setFinance(handle);//财务
			}else if("edu".equals(type)){
				leave.setEdu(handle);//教务
			}else if("college".equals(type)){
				leave.setCollege(handle);//二级学院
			}
			leave.setYear(a.get(Calendar.YEAR)+"");
			this.leaveDao.addLeave(leave);
		}
	}
	
	/***
	 * 取消办理(单个)
	 * @param studentId
	 * @param type
	 * @return
	 */
	public void singleCancelLeave(String studentId, String type){
		LeaveInfo leave=this.leaveDao.queryLeaveByStudentId(studentId);
		Dic handle=this.dicUtil.getDicInfo("Y&N", "N");// 取消
		Calendar a=Calendar.getInstance();
		if(null != leave){//修改
			if("library".equals(type)){
				leave.setLibrary(handle);//图书馆
			}else if("dorm".equals(type)){
				leave.setDorm(handle);//宿舍
			}else if("finance".equals(type)){
				leave.setFinance(handle);//财务
			}else if("edu".equals(type)){
				leave.setEdu(handle);//教务
			}else if("college".equals(type)){
				leave.setCollege(handle);//二级学院
			}
			this.leaveDao.updateLeave(leave);
		}else{//新增
			leave=new LeaveInfo();
			StudentInfoModel student=new StudentInfoModel();
			student.setId(studentId);
			
			leave.setStudent(student);
			if("library".equals(type)){
				leave.setLibrary(handle);//图书馆
			}else if("dorm".equals(type)){
				leave.setDorm(handle);//宿舍
			}else if("finance".equals(type)){
				leave.setFinance(handle);//财务
			}else if("edu".equals(type)){
				leave.setEdu(handle);//教务
			}else if("college".equals(type)){
				leave.setCollege(handle);//二级学院
			}
			leave.setYear(a.get(Calendar.YEAR)+"");
			this.leaveDao.addLeave(leave);
		}
	}
	
	/**
	 * 
	 * @Title: listGradeList
	 * @Description: 毕业年份信息 封装返回调用 离校模块调用
	 * @return
	 * @throws
	 */
	public List<String> listLeaveYearList(){
		return this.leaveDao.listLeaveYearList();
	}

	@Override
	public void importLifeInfoData(List<LifeInfo> list,HttpServletRequest request){
		//判断是否已导入过学生，如果系统已导入则跟新，否则新增。
		for (LifeInfo lifeInfo : list){
			 //默认是当前年
			 Dic yearDic = SchoolYearUtil.getYearDic();
			 String number = lifeInfo.getStuNumber();
			 BigDecimal bd = new BigDecimal(number);
			 String stuNumber = bd.toString();
			 StudentInfoModel studentInfo = commonStudentDao.queryStudentByStudentNo(stuNumber);
			 LifeInfo lifeInfoPo = this.queryLifeInfoByStudenntId(studentInfo.getId());
			 if(null != lifeInfoPo && StringUtils.isNotBlank(lifeInfoPo.getId()))
			 {    
				 lifeInfoPo.setStudent(studentInfo);
				 lifeInfoPo.setYear(yearDic);
				 lifeInfoPo.setStatus(lifeInfo.getStatus());
				 this.leaveDao.updateLifeInfo(lifeInfoPo);
			 }else
			 {
				 lifeInfo.setStudent(studentInfo);
				 lifeInfo.setYear(yearDic);
				 this.leaveDao.saveLifeInfo(lifeInfo);
			 }
		}
	}

	
	
	@Override
	public Page queryLifeInfoPage(int pageNo, int pageSize, LifeInfo leave,
	        HttpServletRequest request)
	{
	    return this.leaveDao.queryLifeInfoPage(pageNo,pageSize,leave,request);
	}

	@Override
    public LifeInfo queryLifeInfoByStudenntId(String studentId)
    {
	    return this.leaveDao.queryLifeInfoByStudenntId(studentId);
    }
	
	@Override
	public void deleteLifeInfoById(String[] ids)
	{
		if (!ArrayUtils.isEmpty(ids))
		{
			for (String id : ids)
				this.leaveDao.deleteById(LifeInfo.class, id);
		}
	}
	
	@Override
	public Page queryLeaveInfoPage(int pageNo, int pageSize, VLeaveInfo leaveInfo, HttpServletRequest request)
	{
	    return this.leaveDao.queryLeaveInfoPage(pageNo,pageSize, leaveInfo, request);
	}

	/***
	 * 查询离校(宿管)
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryLeaveDormPage(int pageNum, int pageSize, LeaveDorm leave, HttpServletRequest request){
		return this.leaveDao.queryLeaveDormPage(pageNum, pageSize, leave, request);
	}
	
	/***
	 * 宿管办理
	 * @param id
	 */
	public void operateDorm(String studentId, String command){
		//宿管办理更新或着存储
		LeaveDorm leaveDorm = this.leaveDao.getLeaveDormByStuId(studentId);
		Dic handle = dicUtil.getDicInfo("LEAVE_HANDLE_STATUS", command);
		if(DataUtil.isNotNull(leaveDorm)) {
			//修改
			Dic yearDic = SchoolYearUtil.getYearDic();//默认当前学年
			if("UNHANDLE".equals(command)){
				leaveDorm.setYear(null);
			}else{
				leaveDorm.setYear(yearDic);
			}
			leaveDorm.setStatus(handle);
			leaveDao.update(leaveDorm);
		}else {//新增
			LeaveDorm newLeaveDorm = new LeaveDorm();
			Dic yearDic = SchoolYearUtil.getYearDic();//默认当前学年
			newLeaveDorm.setYear(yearDic);
			newLeaveDorm.setStatus(handle);
			StudentInfoModel student = this.studentCommonService.queryStudentById(studentId);
			newLeaveDorm.setStudent(student);
			leaveDao.save(newLeaveDorm);
		}
	}
	
	/***
	 * 二级学院办理
	 */
	public void operateCollege(String studentId, String command){
		//二级学院更新或着存储
		CollegeHandle rCollegeHandel = this.leaveDao.getLeaveByStuId(studentId);
		Dic handle = dicUtil.getDicInfo("LEAVE_HANDLE_STATUS", command);
		if(DataUtil.isNotNull(rCollegeHandel)) {
			//修改
			Dic yearDic = SchoolYearUtil.getYearDic();//默认当前学年
			if("UNHANDLE".equals(command)){
				rCollegeHandel.setHandleYear(null);
			}else{
				rCollegeHandel.setHandleYear(yearDic);
			}
			rCollegeHandel.setHandleStatus(handle);
			leaveDao.update(rCollegeHandel);
		}else {//新增
			CollegeHandle collegeHandel = new CollegeHandle();
			Dic yearDic = SchoolYearUtil.getYearDic();//默认当前学年
			collegeHandel.setHandleYear(yearDic);
			collegeHandel.setHandleStatus(handle);
			StudentInfoModel student = this.studentCommonService.queryStudentById(studentId);
			collegeHandel.setStudent(student);
			leaveDao.save(collegeHandel);
		}
	}
	
	@Override
	public VLeaveInfo getVLeaveInfoByStudentId(String userId){
	    return this.leaveDao.getVLeaveInfoByStudentId(userId);
	}
	
	/***
	 * 查询离校办理 2016-5-17
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryLeaveSchoolPage(int pageNum, int pageSize, LeaveSchool leave, HttpServletRequest request){
		return this.leaveDao.queryLeaveSchoolPage(pageNum, pageSize, leave, request);
	}
	
	/****
	 * 发起离校 2016-5-17
	 * @param studentId
	 * 状态 ：0 未发起 ;1  发起   ;2 办结
	 */
	public void operateLeaveSchool(String studentId, String status){
		LeaveSchool leave=this.leaveDao.queryLeaveSchoolByStudentId(studentId);
		if(null != leave){
			leave.setStatus(status);//修改状态 
			/*Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");*/
			leave.setStartDate(new Date());
			this.leaveDao.updateLeaveSchool(leave);
		}
	}
	
	/**
	 * @Description: 办理年份信息 封装返回调用 离校模块调用
	 * @return  2016-5-17
	 * @throws
	 */
	public List<String> listLeaveSchoolYearList(){
		return this.leaveDao.listLeaveSchoolYearList();
	}
	
	/***
	 * 查询所有未发起的离校信息  2016-5-17
	 * @param leave
	 * @return
	 */
	public List<LeaveSchool> queryLeaveSchoolList(LeaveSchool leave, HttpServletRequest request){
		return this.leaveDao.queryLeaveSchoolList(leave, request);
	}
	
	/***
	 * 查询党员办理 2016-5-17
	 * @param pageNum
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryCaucusPage(int pageNum, int pageSize, LeaveSchool leave, HttpServletRequest request){
		return this.leaveDao.queryCaucusPage(pageNum, pageSize, leave, request);
	}
	
	/****
	 * 离校党关系办理 2016-5-17
	 * @param studentId
	 * 状态 ：0 未发起 ;1  发起   ;2 办结
	 */
	public void operateCaucus(String studentId, String status){
		LeaveSchool leave=this.leaveDao.queryLeaveSchoolByStudentId(studentId);
		if(null != leave){
			leave.setCaucus(status);//修改状态 
			this.leaveDao.updateLeaveSchool(leave);
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
		return this.leaveDao.querySecurityPage(pageNum, pageSize, leave);
	}
	
	/****
	 * 离校户口转移办理 2016-5-17
	 * @param studentId
	 * 户口办理的状态：0 无需办理；1 需要办理；2 已办理
	 */
	public void operateSecurity(String studentId, String status){
		LeaveSchool leave=this.leaveDao.queryLeaveSchoolByStudentId(studentId);
		if(null != leave){
			leave.setSecurity(status);//修改户口状态 
			this.leaveDao.updateLeaveSchool(leave);
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
		return this.leaveDao.queryStudentPage(pageNo,pageSize,student);
	}
	
	/****
	 * 二级学院办结离校手续 2016-5-17
	 * @param studentId
	 * 状态 ：0 未发起 ;1  发起   ;2 办结
	 */
	public void operateCollegeLeave(String studentId, String status){
		LeaveSchool leave=this.leaveDao.queryLeaveSchoolByStudentId(studentId);
		if(null != leave){
			leave.setCollege(status);//修改离校状态 
			if("1".equals(status)){
				leave.setStatus("2");//办结
				leave.setGraduateStatus("1");
				leave.setEndDate(new Date());
			}
			this.leaveDao.updateLeaveSchool(leave);
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
		return this.leaveDao.queryAllLeaveSchoolPage(pageNum, pageSize, leave, request);
	}
	
	/***
	 * 查询学生离校情况 2016-5-17
	 * @param studentId
	 * @return
	 */
	@Override
	public LeaveSchool updateAndgetLeaveSchoolByStudentId(String studentId){
		LeaveSchool leave = this.leaveDao.queryLeaveSchoolByStudentId(studentId);
		if(null!= leave){
			if(null==leave.getFinance() || !("缴清".equals(leave.getFinance()))){
				//通过身份证查询计财处数据
				String status = feeService.getStudentFeeStatusStr(leave.getStudent().getCertificateCode(), "JFZT");
				leave.setFinance(status);
			}
			if(null==leave.getOneCard() || !("0".equalsIgnoreCase(leave.getOneCard()))){
				Map<String,Object> map = feeService.getYktSignIn();
				if(null != map && "1".equals(map.get("resultFlag"))){
					List list = (List) map.get("resultValue");
					String fee = feeService.getStudentYktFee(studentId, list.get(1).toString(), list.get(2).toString(), list.get(3).toString());
					leave.setOneCard(fee);
				}
			}
			if(null==leave.getLibrary() ||!"true".equalsIgnoreCase(leave.getLibrary())){
				bookService.checkConnect();
				String status = "";
				if(null!=leave.getStatus() && "1".equals(leave.getStatus()))
					 status = bookService.getBookReturnStatus(studentId,true);
				else
					 status = bookService.getBookReturnStatus(studentId,false);
				
				leave.setLibrary(status);
			}
			leaveDao.updateLeaveSchool(leave);
		}
		return leave;
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
		return this.leaveDao.getStudentByCertificatCode(certificateCode);
	}
}

package com.uws.leave.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.leave.CaucusHandle;
import com.uws.domain.leave.LeaveInfo;
import com.uws.domain.leave.LeaveSchool;
import com.uws.domain.leave.VLeaveInfo;
import com.uws.leave.service.ICaucusService;
import com.uws.leave.service.ILeaveService;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.user.model.User;
import com.uws.user.service.IUserService;
import com.uws.util.CheckUtils;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;
import com.uws.common.service.IBaseDataService;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseController;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;

/***
 * 离校学生
 * @author Jiangbl
 * @date 2015-12-14
 */

@Controller
public class StudentLeaveController extends BaseController {
	private Logger log = new LoggerFactory(StudentLeaveController.class);
	
	@Autowired
	private ICaucusService caucusService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IBaseDataService baseDateService;
	
	@Autowired
	private ICompService compService;
	
	@Autowired
	private ILeaveService leaveService;
	
	//字典工具类
	private DicUtil dicUtil=DicFactory.getDicUtil();
	
	private SessionUtil sessionUtil = SessionFactory.getSession(com.uws.sys.util.Constants.MENUKEY_SYSCONFIG);
	
	/***
	 * 学生查看个人离校办理情况
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping({"/leave/studentView/opt-view/queryStuHandleInfoPage"})
	public String stuViewHandleInfoList(ModelMap model, HttpServletRequest request) {
		boolean flag = ProjectSessionUtils.checkIsStudent(request);//判断是否学生
		if(flag){
			String userId = this.sessionUtil.getCurrentUserId();
			LeaveSchool leave=this.leaveService.updateAndgetLeaveSchoolByStudentId(userId);
			model.addAttribute("leave", leave);
			model.addAttribute("name", ProjectSessionUtils.getCurrentUserName(request));
		}
		return "/leave/student/stuView";
	}
	/*public String stuViewHandleInfoList(ModelMap model, HttpServletRequest request) {
		boolean flag = ProjectSessionUtils.checkIsStudent(request);//判断是否学生
		if(flag){
			String userId = this.sessionUtil.getCurrentUserId();
			//User user = this.userService.getUserById(userId);
			
			LeaveInfo leaveInfo = this.caucusService.getLeaveInfoByStuId(userId);
			CaucusHandle caucus=this.caucusService.getCaucusHandleByStuId(userId);
			VLeaveInfo vLeaveInfo = this.caucusService.getVLeaveInfoByStudentId(userId);
			model.addAttribute("vLeaveInfo", vLeaveInfo);
			model.addAttribute("leaveInfo", leaveInfo);
			model.addAttribute("caucus", caucus);
			model.addAttribute("name", ProjectSessionUtils.getCurrentUserName(request));
		}
		return "/leave/student/stuView";
	}*/
	
	/***
	 * 办理人查看个人离校办理情况
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping({"/leave/studentView/view/queryStudentLeave"})
	public String queryStudentLeave(ModelMap model, HttpServletRequest request, String id) {
		LeaveInfo leaveInfo = this.caucusService.getLeaveInfoByStuId(id);
		CaucusHandle caucus=this.caucusService.getCaucusHandleByStuId(id);
		User user = this.userService.getUserById(id);
		model.addAttribute("leaveInfo", leaveInfo);
		model.addAttribute("caucus", caucus);
		model.addAttribute("name", user.getName());
		return "/leave/student/viewStudentLeave";
	}
	
	/***
	 * 离校办理信息查询
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("leave/leave/opt-view/queryLeavePage")
	public String queryLeaveSchoolPage(ModelMap model, HttpServletRequest request, LeaveSchool leave){
		//学院列表
		List<BaseAcademyModel> collegeList = new ArrayList<BaseAcademyModel>();
		String orgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		if( null!=orgId && CheckUtils.isCurrentOrgEqCollege(orgId)){
			collegeList.add(baseDateService.findAcademyById(orgId));
		}else{
			collegeList = baseDateService.listBaseAcademy();
		}
		List<BaseMajorModel> majorList = null;
		//根据返回的学院查询专业
		if(DataUtil.isNotNull(leave.getStudent()) && DataUtil.isNotNull(leave.getStudent().getCollege()) 
				&& DataUtil.isNotNull(leave.getStudent().getCollege().getId())){
			majorList = compService.queryMajorByCollage(leave.getStudent().getCollege().getId());
		}
			
		List<BaseClassModel> classList = null;
		//根据返回的专业查询班级
		if(DataUtil.isNotNull(leave.getStudent()) && DataUtil.isNotNull(leave.getStudent().getMajor()) 
				&& DataUtil.isNotNull(leave.getStudent().getMajor().getId())){
			classList = compService.queryClassByMajor(leave.getStudent().getMajor().getId());
		}
		
		//办理年份
		List<String> yearList = this.leaveService.listLeaveSchoolYearList();
		//办理状态
		List<Dic> stautsDic = this.dicUtil.getDicInfoList("LEAVE_HANDLE_STATUS");
		
		
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		Page page = this.leaveService.queryAllLeaveSchoolPage(pageNo, Page.DEFAULT_PAGE_SIZE, leave, request);
		
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("yearList", yearList);
		model.addAttribute("stautsDic", stautsDic);
		model.addAttribute("leave", leave);
		model.addAttribute("page", page);
		log.info("离校办理查询");
		return "/leave/student/leaveSchoolList";
	}
	/*public String queryLeavePage(ModelMap model, HttpServletRequest request, VLeaveInfo leaveInfo){
		
		//学院列表
		List<BaseAcademyModel> collegeList = new ArrayList<BaseAcademyModel>();
		String orgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		if(null!=orgId && !ProjectConstants.STUDNET_OFFICE_ORG_ID.equals(orgId)){
			collegeList.add(baseDateService.findAcademyById(orgId));
		}else{
			collegeList = baseDateService.listBaseAcademy();
		}
		List<BaseMajorModel> majorList = null;
		//根据返回的学院查询专业
		if(DataUtil.isNotNull(leaveInfo.getStudent()) && DataUtil.isNotNull(leaveInfo.getStudent().getCollege()) 
				&& DataUtil.isNotNull(leaveInfo.getStudent().getCollege().getId())){
			majorList = compService.queryMajorByCollage(leaveInfo.getStudent().getCollege().getId());
		}
			
		List<BaseClassModel> classList = null;
		//根据返回的专业查询班级
		if(DataUtil.isNotNull(leaveInfo.getStudent()) && DataUtil.isNotNull(leaveInfo.getStudent().getMajor()) 
				&& DataUtil.isNotNull(leaveInfo.getStudent().getMajor().getId())){
			classList = compService.queryClassByMajor(leaveInfo.getStudent().getMajor().getId());
		}
		
		//办理年份
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");	//学年
		
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		Page page = this.leaveService.queryLeaveInfoPage(pageNo, Page.DEFAULT_PAGE_SIZE, leaveInfo, request);
		
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("leave", leaveInfo);
		model.addAttribute("page", page);
		log.info("离校办理查询");
		return "/leave/student/leaveSchoolList";
	}*/
	
}

package com.uws.leave.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.ILeaveCommonService;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseController;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.leave.CaucusHandle;
import com.uws.domain.leave.LeaveInfo;
import com.uws.domain.leave.LeaveSchool;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.leave.service.ILeaveService;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.util.CheckUtils;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;
import com.uws.webservice.IBookService;

/****
 * @author Jiangbl
 * @date 2015-11-12
 */

@Controller
public class LeaveController extends BaseController{
	
	private Logger log = new LoggerFactory(LeaveController.class);
	
	@Autowired
	private IBaseDataService baseDateService;
	
	@Autowired
	private ICompService compService;
	
	@Autowired
	private ILeaveService leaveService;
	
	@Autowired
	private IBookService bookService;
	
	@Autowired
	private ILeaveCommonService leaveCommonService;
	
	//字典工具类
	private DicUtil dicUtil=DicFactory.getDicUtil();
	
	@RequestMapping("leave/leave/opt-query/queryLeavePage")
	public String queryLeavePage(ModelMap model, HttpServletRequest request, LeaveInfo leave){
		//学院列表
		//List<BaseAcademyModel> collegeList = baseDateService.listBaseAcademy();
		List<BaseAcademyModel> collegeList = new ArrayList<BaseAcademyModel>();
		String orgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		if(null!=orgId && !ProjectConstants.STUDNET_OFFICE_ORG_ID.equals(orgId)){
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
		List<String> yearList = this.leaveService.listLeaveYearList();
		//办理状态
		List<Dic> stautsDic = this.dicUtil.getDicInfoList("LEAVE_HANDLE_STATUS");
		
		
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		Page page = this.leaveService.queryLeavePage(pageNo, Page.DEFAULT_PAGE_SIZE, leave, request);
		
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("yearList", yearList);
		model.addAttribute("stautsDic", stautsDic);
		model.addAttribute("leave", leave);
		model.addAttribute("page", page);
		log.info("离校办理查询");
		return "/leave/leave/leaveList";
	}
	
	/**
	 * 办理离校手续(多个类型一块办理)
	 * @param baseClass
	 * @param model
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/leave/leave/opt-edit/operateLeave"} , produces={"text/plain;charset=UTF-8"})
	public String operateLeave(String studentId){
		this.leaveService.operateLeave(studentId);
		log.info("离校办理");
		return "success";
	}

	/**
	 * 取消办理离校手续(多个类型一块取消)
	 * @param baseClass
	 * @param model
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/leave/leave/opt-edit/cancelLeave"} , produces={"text/plain;charset=UTF-8"})
	public String cancelLeave(String studentId){
		this.leaveService.cancelLeave(studentId);
		log.info("离校办理取消");
		return "success";
	}
	
	/**
	 * 办理离校手续(单个类型)
	 * @param baseClass
	 * @param model
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/leave/leave/opt-edit/singleOperateLeave"} , produces={"text/plain;charset=UTF-8"})
	public String singleOperateLeave(String studentId, String type){
		this.leaveService.singleOperateLeave(studentId, type);
		log.info("离校办理");
		return "success";
	}
	
	/**
	 * 取消办理离校手续(单个类型)
	 * @param baseClass
	 * @param model
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/leave/leave/opt-edit/singleCancelLeave"} , produces={"text/plain;charset=UTF-8"})
	public String singleCancelLeave(String studentId, String type){
		this.leaveService.singleCancelLeave(studentId, type);
		log.info("离校办理取消");
		return "success";
	}
	
	/**
	 * 批量办理离校手续
	 * @param baseClass
	 * @param model
	 * @param fileId
	 * @return
	 */
	 
	@ResponseBody
	@RequestMapping(value={"/leave/leave/opt-edit/mulOperate"} , produces={"text/plain;charset=UTF-8"})
	public String mulOperate(String studentIds) {
		String[] stuIds = studentIds.split(",");
		for (int i = 0; i < stuIds.length; i++) {
			String studentId = stuIds[i];
			this.leaveService.operateLeave(studentId);
		}
		return "success";
	}
	
	/***
	 * 离校 2016-5-17
	 * @param model
	 * @param request
	 * @param leave
	 * @return 
	 */
	@RequestMapping("leave/operate/opt-query/queryLeaveSchoolPage")
	public String queryLeaveSchoolPage(ModelMap model, HttpServletRequest request, LeaveSchool leave, String type, HttpSession session){
		//学院列表
		if("1".equals(type)){
			leave = (LeaveSchool) session.getAttribute("leave");
		}
		List<BaseAcademyModel> collegeList = new ArrayList<BaseAcademyModel>();
		String orgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		if(null!=orgId && CheckUtils.isCurrentOrgEqCollege(orgId)){
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
		Page page = this.leaveService.queryLeaveSchoolPage(pageNo, Page.DEFAULT_PAGE_SIZE, leave, request);
		
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("yearList", yearList);
		model.addAttribute("stautsDic", stautsDic);
		model.addAttribute("leave", leave);
		model.addAttribute("page", page);
		log.info("离校办理查询");
		return "/leave/leave/leaveSchoolList";
	}
	
	/**
	 * 发起离校  2016-5-17
	 * @param baseClass
	 * @param model
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/leave/operate/opt-edit/operateLeaveSchool"} , produces={"text/plain;charset=UTF-8"})
	public String operateLeaveSchool(String studentId, String status){
		bookService.checkConnect();//图书馆链接校验
		String libraryStatus = bookService.getBookReturnStatus(studentId,true);//查询学生图书馆状态
		leaveCommonService.operateLibrary(studentId, libraryStatus);
		
		this.leaveService.operateLeaveSchool(studentId, status);
		log.info("离校办理");
		return "success";
	}
	
	/***
	 * 离校 2016-5-17
	 * @param model
	 * @param request
	 * @param leave
	 * @return /leave/operate/opt-edit/mulOperate
	 */
	@RequestMapping("/leave/operate/opt-edit/mulOperateLeaveSchool")
	public String mulOperateLeaveSchool(HttpSession session, HttpServletRequest request, LeaveSchool leave, String status){
		List<LeaveSchool> leaveList=this.leaveService.queryLeaveSchoolList(leave, request);
		bookService.checkConnect();//图书馆链接校验
		for (Iterator iterator = leaveList.iterator(); iterator.hasNext();) {
			LeaveSchool leaveSchool = (LeaveSchool) iterator.next();
			String studentId = leaveSchool.getStudent().getId();
			String libraryStatus = bookService.getBookReturnStatus(studentId,true);//查询学生图书馆状态
			//图书馆为空 或者 不为空并且是true时操作
			if(!(DataUtil.isNotNull(leaveSchool.getLibrary()) && leaveSchool.getLibrary().equals(libraryStatus) 
					&& "true".equals(leaveSchool.getLibrary())) || DataUtil.isNotNull(leaveSchool.getLibrary())){
				leaveCommonService.operateLibrary(studentId, libraryStatus);
			}
			
			this.leaveService.operateLeaveSchool(studentId, status);
		}
		session.setAttribute("leave", leave);
		return "redirect:/leave/operate/opt-query/queryLeaveSchoolPage.do?type=1";
	}
	
	/**
	 * 党团关系办理列表
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({"/leave/caucus/opt-query/queryCaucusPage"})
	public String queryCaucusPage(ModelMap model, HttpServletRequest request, LeaveSchool leave){
		List<BaseAcademyModel> collegeList = new ArrayList<BaseAcademyModel>();
		String orgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		if(null!=orgId && CheckUtils.isCurrentOrgEqCollege(orgId)){
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
		Page page = this.leaveService.queryCaucusPage(pageNo, Page.DEFAULT_PAGE_SIZE, leave, request);
		
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("yearList", yearList);
		model.addAttribute("stautsDic", stautsDic);
		model.addAttribute("leave", leave);
		model.addAttribute("page", page);
		log.info("离校办理查询");
		return "/leave/leave/caucusList";
	}
	
	/**
	 * 离校党关系办理  2016-5-17
	 * @param baseClass
	 * @param model
	 * @param fileId
	 * @returnleave
	 */
	@ResponseBody
	@RequestMapping(value={"/leave/operate/opt-edit/operateCaucus"} , produces={"text/plain;charset=UTF-8"})
	public String operateCaucus(String studentId, String status){
		this.leaveService.operateCaucus(studentId, status);
		log.info("离校党组织办理");
		return "success";
	}
	
	/***
	 * 离校 2016-5-17
	 * @param model
	 * @param request
	 * @param leave
	 * @return 
	 */
	@RequestMapping("/leave/security/opt-query/querySecurityPage")
	public String querySecurityPage(ModelMap model, HttpServletRequest request, LeaveSchool leave){
		List<BaseAcademyModel> collegeList = baseDateService.listBaseAcademy();
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
		
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		Page page = this.leaveService.querySecurityPage(pageNo, Page.DEFAULT_PAGE_SIZE, leave);
		
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("yearList", yearList);
		model.addAttribute("leave", leave);
		model.addAttribute("page", page);
		log.info("离校办理查询");
		return "/leave/leave/securityList";
	}
	
	/**
	 * 
	 * @Title: queryStudent
	 * @Description: 离校学生查询   只查询除了需要办理状态的离校学生
	 * @param model
	 * @param request
	 * @param student
	 * @return
	 * @throws
	 */
	@SuppressWarnings("unchecked")
    @RequestMapping(value={"comp/leave/nsm/queryCheckStudent"})
	public String queryStudent(ModelMap model, HttpServletRequest request,StudentInfoModel student,String selectedStudentId,String formId,String queryFlag)
	{
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page =  this.leaveService.queryStudentPage(pageNo, 5, student);
		model.addAttribute("page", page);
		Collection<StudentInfoModel> list = page.getResult();
		for( StudentInfoModel stu : list )
		{
			stu.setStudentInfo(
					new StringBuffer()
					.append("{id:'").append(stu.getId()).append("',")
					.append("name:'").append(stu.getName()).append("',")
					.append("bankCode:'").append(stu.getBankCode()).append("',")
					.append("bank:'").append(stu.getBank()).append("',")
					.append("className:'").append(stu.getClassId().getClassName()).append("',")
					.append("classId:'").append(stu.getClassId().getId()).append("',")
					.append("majorId:'").append(stu.getMajor().getId()).append("',")
					.append("majorName:'").append(stu.getMajor().getMajorName()).append("',")
					.append("genderId:'").append(stu.getGenderDic().getId()).append("',")
					.append("genderName:'").append(stu.getGenderDic().getName()).append("',")
					.append("sourceLandId:'").append(stu.getSourceLand()).append("',")
					.append("sourceLandName:'").append(stu.getSourceLand()).append("',")
					.append("nativeId:'").append(null == stu.getNativeDic() ? "" : stu.getNativeDic().getId()).append("',")
					.append("nativeName:'").append(null == stu.getNativeDic() ? "" : stu.getNativeDic().getName()).append("',")
					.append("birthDay:'").append(stu.getBrithDate()).append("',")
					.append("collegeId:'").append(stu.getCollege().getId()).append("',")
					.append("collegeName:'").append(stu.getCollege().getName()).append("',")
					.append("stuNumber:'").append(stu.getStuNumber()).append("',")
					.append("certificateCode:'").append(stu.getCertificateCode()).append("',")
					.append("schoolYear:'").append(null == stu.getMajor()? "" : stu.getMajor().getSchoolYear()).append("'}")
					.toString()
			);
		}
		model.addAttribute("selectedId", selectedStudentId);
		model.addAttribute("formId", formId);
		model.addAttribute("queryFlag", queryFlag);
		model.addAttribute("student", student);
		
		return "/leave/leave/studentCheckTable";
	}
	
	/**
	 * 离校户口转移初始化  2016-5-17
	 * @param baseClass
	 * @param model
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/leave/security/opt-edit/operateSecurityStudent"} , produces={"text/plain;charset=UTF-8"})
	public String operateSecurityStudent(String studentIds, String status){
		String[] ids=studentIds.split(",");
		for (String studentId : ids) {
			this.leaveService.operateSecurity(studentId, "1");
		}
		
		log.info("离校户口转移初始化");
		return "success";
	}
	
	/**
	 * 离校户口转移办理  2016-5-17
	 * @param baseClass
	 * @param model
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/leave/security/opt-edit/operateSecurity"} , produces={"text/plain;charset=UTF-8"})
	public String operateSecurity(String studentId, String status){
		this.leaveService.operateSecurity(studentId, status);
		log.info("离校户口转移办理");
		return "success";
	}
	
	/***
	 *离校 二级学院 查询 2016-5-17
	 * 因时间紧，暂时在刷身份证时调用图书馆、计财处、一卡通接口
	 * @param model
	 * @param request
	 * @param leave
	 * @return 
	 */
	@RequestMapping("/leave/college/opt-query/queryCollegeLeavePage")
	public String queryCollegePage(ModelMap model, HttpServletRequest request, LeaveSchool leave, String cardId){
		//学院列表
		List<BaseAcademyModel> collegeList = new ArrayList<BaseAcademyModel>();
		String orgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		if(null!=orgId && CheckUtils.isCurrentOrgEqCollege(orgId)){
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
		
		if(DataUtil.isNotNull(cardId)){
			StudentInfoModel student = this.leaveService.getStudentByCertificatCode(cardId);
			leave.setStudent(student);
		}
		
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		Page page = this.leaveService.queryLeaveSchoolPage(pageNo, Page.DEFAULT_PAGE_SIZE, leave, request);
		
		if(DataUtil.isNotNull(cardId)){//刷身份证时调用图书馆、计财处、一卡通接口
			List<LeaveSchool> list = (List<LeaveSchool>) page.getResult();
			if(list!= null && list.size()>0){
				LeaveSchool leaveInfo = list.get(0);
				if(leaveInfo!=null && leaveInfo.getStudent()!=null){
					leaveInfo = this.leaveService.updateAndgetLeaveSchoolByStudentId(leaveInfo.getStudent().getId());
					page = this.leaveService.queryLeaveSchoolPage(pageNo, Page.DEFAULT_PAGE_SIZE, leave, request);
				}
			}
		}
		
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("yearList", yearList);
		model.addAttribute("stautsDic", stautsDic);
		model.addAttribute("leave", leave);
		model.addAttribute("page", page);
		log.info("离校办理查询");
		return "/leave/leave/collegeList";
	}
	
	/**
	 * 二级学院办结离校手续  2016-5-17
	 * @param baseClass
	 * @param model
	 * @param fileId
	 * @returnleave
	 */
	@ResponseBody
	@RequestMapping(value={"/leave/operate/opt-edit/operateCollegeLeave"} , produces={"text/plain;charset=UTF-8"})
	public String operateCollegeLeave(String studentId, String status){
		this.leaveService.operateCollegeLeave(studentId, status);
		log.info("二级学院办结离校手续");
		return "success";
	}
	
	/**
	 * 刷新 图书馆、计财处、宿舍及一卡通状态
	 * @param baseClass
	 * @param model
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/leave/refreshStatus/opt-edit/refreshLeaveStatus"} , produces={"text/plain;charset=UTF-8"})
	public String refreshLeaveStatus(String studentId){
		this.leaveService.updateAndgetLeaveSchoolByStudentId(studentId);
		log.info("刷新状态");
		return "success";
	}
}

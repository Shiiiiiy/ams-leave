package com.uws.leave.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.common.service.IBaseDataService;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseController;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.base.StudentRoomModel;
import com.uws.domain.leave.LeaveDorm;
import com.uws.leave.service.ILeaveService;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;

/***
 * 离校办理（公寓办理）
 * @author Jiangbl
 * @date 2016-1-18
 */
@Controller
public class DormController extends BaseController{
	private Logger log = new LoggerFactory(LeaveController.class);
	
	@Autowired
	private IBaseDataService baseDateService;
	
	@Autowired
	private ICompService compService;
	
	@Autowired
	private ILeaveService leaveService;
	
	//字典工具类
	private DicUtil dicUtil=DicFactory.getDicUtil();
	
	/****
	 * 离校办理（公寓办理）查询
	 * @param model
	 * @param request
	 * @param leave
	 * @return
	 */
	@RequestMapping("leave/dorm/opt-query/queryDormPage")
	public String queryDormPage(ModelMap model, HttpServletRequest request, LeaveDorm leave){
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
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");	//学年
		//办理状态
		List<Dic> stautsDic = this.dicUtil.getDicInfoList("LEAVE_HANDLE_STATUS");
		
		
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		Page page = this.leaveService.queryLeaveDormPage(pageNo, Page.DEFAULT_PAGE_SIZE, leave, request);
		
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("stautsDic", stautsDic);
		model.addAttribute("leave", leave);
		model.addAttribute("page", page);
		log.info("离校办理(公寓办理)查询");
		return "/leave/dorm/dormList";
	}
	
	/**
	 * 离校办理（公寓办理）
	 * @param request
	 * @param yearCode
	 * @param handle_status
	 * @param stuIds
	 * @return
	 */
	@RequestMapping({"/leave/dorm/opt-edit/operateDorm"})
	@ResponseBody
	public String operateDorm(String studentId, String command) {
		if(DataUtil.isNotNull(studentId)) {
			this.leaveService.operateDorm(studentId, command);
		}
		return "success";
	}
	
	/***
	 * 查询住宿信息
	 * @param model
	 * @param studentId
	 * @param cardID
	 * @return
	 */
	@RequestMapping("/leave/dorm/nsm/queryStudentDorm")
	public String queryStudentDorm(ModelMap model, String studentId, String cardID){
		//学生住宿信息
		StudentRoomModel studentRoom=new StudentRoomModel();
		if(StringUtils.isNotEmpty(studentId)){//学号获取住宿信息
			studentRoom=this.baseDateService.findRoomByStudentId(studentId);
		}else if(StringUtils.isNotEmpty(cardID)){//刷身份证获取住宿信息
			studentRoom=this.baseDateService.findRoomByCardID(cardID);
		}
		
		model.addAttribute("studentRoom", studentRoom);
		log.info("学生住宿信息查询");
		return "/leave/dorm/viewStudentDorm";
	}
}

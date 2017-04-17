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
import com.uws.domain.leave.VLeaveInfo;
import com.uws.leave.service.ILeaveService;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;

/***
 * 离校办理（二级学院办理）
 * @author Jiangbl
 * @date 2016-1-18
 */
@Controller
public class CollegeController extends BaseController{
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
	 * 离校信息办理
	 * @param model
	 * @param request
	 * @param leave
	 * @return
	 */
	@RequestMapping("leave/college/opt-query/queryCollegePage")
	public String queryLeavePage(ModelMap model, HttpServletRequest request, VLeaveInfo leaveInfo){
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
		//办理状态
		List<Dic> stautsDic = this.dicUtil.getDicInfoList("LEAVE_HANDLE_STATUS");
		
		
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		Page page = this.leaveService.queryLeaveInfoPage(pageNo, Page.DEFAULT_PAGE_SIZE, leaveInfo, request);
		
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("stautsDic", stautsDic);
		model.addAttribute("leave", leaveInfo);
		model.addAttribute("page", page);
		log.info("离校办理(公寓办理)查询");
		return "/leave/college/collegeList";
	}
	
	/***
	 * 查询离校信息
	 * @param model
	 * @param studentId
	 * @param cardID
	 * @return
	 */
	@RequestMapping("/leave/college/nsm/queryStudentLeave")
	public String queryStudentLeave(ModelMap model, String studentId, String cardID){
		VLeaveInfo leaveInfo=new VLeaveInfo();
		if(StringUtils.isNotEmpty(studentId)){//学号获取住宿信息
			leaveInfo = this.leaveService.getVLeaveInfoByStudentId(studentId);
		}else if(StringUtils.isNotEmpty(cardID)){//刷身份证获取住宿信息
			//VLeaveInfo vLeaveInfo = this.caucusService.getVLeaveInfoByCardID(cardID);
		}
		
		model.addAttribute("leaveInfo", leaveInfo);
		log.info("学生住宿信息查询");
		return "/leave/college/viewStudentLeave";
	}
	
	/***
	 * 离校办理（二级学院办理）
	 * @param studentId
	 * @param command
	 * @return
	 */
	@RequestMapping({"/leave/college/opt-edit/operateCollege"})
	@ResponseBody
	public String operateCollege(String studentId, String command) {
		if(DataUtil.isNotNull(studentId)) {
			this.leaveService.operateCollege(studentId, command);
		}
		return "success";
	}
}

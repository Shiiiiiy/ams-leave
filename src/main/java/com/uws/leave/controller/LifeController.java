package com.uws.leave.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.uws.common.service.IBaseDataService;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseController;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.leave.LifeInfo;
import com.uws.leave.service.ILeaveService;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.sys.util.MultipartFileValidator;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;

/***
 * 离校办理（水电费欠费办理）
 * @author Jiangbl
 * @date 2016-1-18
 */
@Controller
public class LifeController extends BaseController{
	private Logger log = new LoggerFactory(LifeController.class);
	@Autowired
	private IBaseDataService baseDateService;
	@Autowired
	private ILeaveService leaveService;
	@Autowired
	private ICompService compService;
	//数据字典工具类
    private DicUtil dicUtil = DicFactory.getDicUtil();
	private FileUtil fileUtil=FileFactory.getFileUtil();
	
	/***
	 * 水电费办理
	 * @param model
	 * @param request
	 * @param leave
	 * @return
	 */
	@RequestMapping("leave/life/opt-query/queryLifePage")
	public String queryLifePage(ModelMap model, HttpServletRequest request, LifeInfo leave){
		//学院列表
		//List<BaseAcademyModel> collegeList = baseDateService.listBaseAcademy();//全部学院
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
		//List<String> yearList = this.leaveService.listLeaveYearList();
		//办理状态
		//List<Dic> stautsDic = this.dicUtil.getDicInfoList("LEAVE_HANDLE_STATUS");
		
		
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		Page page = this.leaveService.queryLifeInfoPage(pageNo, Page.DEFAULT_PAGE_SIZE, leave, request);
		
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("yearList", dicUtil.getDicInfoList("YEAR"));
		//model.addAttribute("stautsDic", stautsDic);
		model.addAttribute("leave", leave);
		model.addAttribute("page", page);
		log.info("离校水电费欠费办理查询");
		return "/leave/life/lifeList";
	}
	
	
	/**
	 * 执行导入
	 * @param model
	 * @param file
	 * @param maxSize
	 * @param allowedExt
	 * @param request
	 * @return
	 * @throws exception 
	 * @throws Exception 
	 */
	@RequestMapping(value="/leave/life/opt-import/importLifeInfo")
	public String importPsychologyInfo(ModelMap model, @RequestParam("file") MultipartFile file, 
		   String maxSize, String allowedExt, HttpServletRequest request, HttpSession session) throws Exception{

	     List errorText = new ArrayList();
	 
	     MultipartFileValidator validator = new MultipartFileValidator();
	     if (org.apache.commons.lang.StringUtils.isNotEmpty(allowedExt)) {
	       validator.setAllowedExtStr(allowedExt.toLowerCase());
	     }
	     if (org.apache.commons.lang.StringUtils.isNotEmpty(maxSize))
	       validator.setMaxSize(Long.valueOf(maxSize).longValue());
	     else {
	       validator.setMaxSize(setMaxSize());
	     }
	     String returnValue = validator.validate(file);
	     if (!returnValue.equals("")) {
	       errorText.add(returnValue);
	       model.addAttribute("errorText", errorText);
	       model.addAttribute("importFlag", Boolean.valueOf(true));
	       return "/warning/import/importPsychologyInfo";
	     }
	 
	     String tempFileId = this.fileUtil.saveSingleFile(true, file);
	     File tempFile = this.fileUtil.getTempRealFile(tempFileId);
	     String filePath = tempFile.getAbsolutePath();
	 
	     session.setAttribute("filePath", filePath);
	     try
	     {
	       ImportUtil iu = new ImportUtil();
	       List list = iu.getDataList(filePath, "importLifeInfo", null, LifeInfo.class);
	       //比较数据是否重复
	      // List arrayList = this.leaveService.compareLifeInfoData(list);
	     //  if ((arrayList == null) || (arrayList.size() == 0))
	       {
	         this.leaveService.importLifeInfoData(list,request);
	       }
	     /* else {
	       //  session.setAttribute("arrayList", arrayList);
	       //  this.leaveService.importData(arrayList, filePath, compareId);
	         List subList = null;
	         //if (arrayList.size() >= Page.DEFAULT_PAGE_SIZE)
	        //   subList = arrayList.subList(0, Page.DEFAULT_PAGE_SIZE);
	      //   else
	        //   subList = arrayList;
	         Page page = new Page();
	         page.setPageSize(Page.DEFAULT_PAGE_SIZE);
	         page.setResult(subList);
	         page.setStart(0L);
	        // page.setTotalCount(arrayList.size());
	         model.addAttribute("page", page);
	       }*/
	     }
	     catch (ExcelException e) {
	       errorText = e.getMessageList();
	 
	       errorText = errorText.subList(0, errorText.size() > 20 ? 20 : errorText.size());
	       model.addAttribute("errorText", errorText);
	     } catch (InstantiationException e) {
	       e.printStackTrace();
	     } catch (IOException e) {
	       e.printStackTrace();
	     } catch (IllegalAccessException e) {
	       e.printStackTrace();
	     } catch (ClassNotFoundException e) {
	       e.printStackTrace(); } finally {
	     }
	     model.addAttribute("importFlag", Boolean.valueOf(true));
	     return "/leave/life/importLifeInfo";
	}
	
		 /**
		 * 设置最大
		 * @return
		 */
		private int setMaxSize(){
			return 20971520;//20M
		}
		
		
	    /**
	     * 
	     * @Title: LifeController.java 
	     * @Package com.uws.leave.controller 
	     * @Description:批量删除水电费欠费信息
	     * @author LiuChen 
	     * @date 2016-1-19 下午3:21:46
	     */
	    @ResponseBody
		@RequestMapping("/leave/life/opt-del/deleteLifeInfo")
		public String deleteLifeInfo(ModelMap model, HttpServletRequest request,HttpServletResponse response) 
		{
			String[] ids =  request.getParameterValues("lifeInfoId");
			if (!ArrayUtils.isEmpty(ids)) 
				leaveService.deleteLifeInfoById(ids);
			return "success";
		}
	
	 
}

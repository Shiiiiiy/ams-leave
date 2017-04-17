package com.uws.leave.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
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
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.leave.CaucusHandle;
import com.uws.domain.warning.StudyWarningModel;
import com.uws.leave.service.ICaucusService;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.sys.util.MultipartFileValidator;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;

/**
 * @className CaucusController.java
 * @package com.uws.leave.controller
 * @description
 * @date 2015年11月11日 16:31:45
 */

@Controller
public class CaucusController extends BaseController
{
	private Logger log = new LoggerFactory(this.getClass());
	@Autowired
	private IBaseDataService baseDateService;
	@Autowired
	private ICompService compService;
	@Autowired
	private ICaucusService caucusService;

	private DicUtil dicUtil = DicFactory.getDicUtil();
	private FileUtil fileUtil = FileFactory.getFileUtil();
	
	/**
	 * 党团关系办理列表
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	/*@RequestMapping({"/leave/caucus/opt-query/queryCaucusPage"})*/
	public String CaucusHandleList(ModelMap model, HttpServletRequest request, CaucusHandle po)
	{
		List<BaseAcademyModel> collegeList = new ArrayList<BaseAcademyModel>();
		String orgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		if (null != orgId && !ProjectConstants.STUDNET_OFFICE_ORG_ID.equals(orgId)){
			collegeList.add(baseDateService.findAcademyById(orgId));
		}else{
			collegeList = baseDateService.listBaseAcademy();
		}

		List<BaseMajorModel> majorList = null;
		// 根据返回的学院查询专业
		if (DataUtil.isNotNull(po.getStudent())&& DataUtil.isNotNull(po.getStudent().getCollege()) && DataUtil.isNotNull(po.getStudent().getCollege().getId())){
			majorList = compService.queryMajorByCollage(po.getStudent().getCollege().getId());
		}
		List<BaseClassModel> classList = null;
		// 根据返回的专业查询班级
		if (DataUtil.isNotNull(po.getStudent())&& DataUtil.isNotNull(po.getStudent().getMajor()) && DataUtil.isNotNull(po.getStudent().getMajor().getId())){
			classList = compService.queryClassByMajor(po.getStudent().getMajor().getId());
		}
		// 党团关系办理状态
		List<Dic> handleStauts = dicUtil.getDicInfoList("CAUCUS_HANDLE_STATUS");
		// 学年
		List<Dic> yearList = dicUtil.getDicInfoList("YEAR");
		String pageNo = request.getParameter("pageNo") != null ? request.getParameter("pageNo") : "1";
		Page page = caucusService.queryCaucusListPage(po, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE, request);

		model.addAttribute("yearList", yearList);
		model.addAttribute("handleStauts", handleStauts);
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("po", po);
		model.addAttribute("page", page);

		return "/leave/caucus/caucusList";
	}

	
	@SuppressWarnings({ "finally", "deprecation" })
    @RequestMapping("/leave/caucus/opt-import/importCaucus")
	public String importCaucus(ModelMap model, @RequestParam("file")  MultipartFile file, String maxSize,String allowedExt,HttpServletRequest request)
	{
		List<Object> errorText = new ArrayList<Object>();
		String errorTemp = "";
		try {
			//构建文件验证对象
	    	MultipartFileValidator validator = new MultipartFileValidator();
	    	if(DataUtil.isNotNull(allowedExt)){
	    		validator.setAllowedExtStr(allowedExt.toLowerCase());
	    	}
	    	//设置文件大小
	    	if(DataUtil.isNotNull(maxSize)){
	    		validator.setMaxSize(Long.valueOf(maxSize));//20M
	    	}else{
	    		validator.setMaxSize(1024*1024*20);//20M
	    	}
			//调用验证框架自动验证数据
	        String returnValue=validator.validate(file);                
	        if(!returnValue.equals("")){
				errorTemp = returnValue;       	
				errorText.add(errorTemp);
	        	model.addAttribute("errorText",errorText);
	        	return "/leave/caucus/caucusImport";
	        }
	        String tempFileId=fileUtil.saveSingleFile(true, file); 
	        File tempFile=fileUtil.getTempRealFile(tempFileId);
	        caucusService.importCaucusData(tempFile.getAbsolutePath(), "importCaucusData", null ,CaucusHandle.class);
		} catch (OfficeXmlFileException e) {
			log.error(e.getMessage());
			errorTemp = "OfficeXmlFileException" + e.getMessage();
			errorText.add(errorTemp);
		} catch (ExcelException e) { 
			log.error(e.getMessage());
			errorTemp = e.getMessage();
			errorText.add(errorTemp);
		} catch (InstantiationException e) {
			log.error(e.getMessage());
			errorTemp = "InstantiationException" + e.getMessage();
			errorText.add(errorTemp);
		} catch (IOException e) {
			log.error(e.getMessage());
			errorTemp = "IOException" + e.getMessage();
			errorText.add(errorTemp);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage());
			errorTemp = "IllegalAccessException" + e.getMessage();
			errorText.add(errorTemp);
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			errorText.add("模板不正确或者模板内数据异常，请检查后再导入。");
		} finally {
			model.addAttribute("errorText",errorText);
	        return "/leave/caucus/caucusImport";
		}
	}
	
	
	
	
	/**
	 * 编辑党团关系信息
	 * 
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@Deprecated
	@RequestMapping({ "/leave/caucus/opt-add/caucusHandleInfo" })
	public String CancusHandleEdit(ModelMap model, HttpServletRequest request,CaucusHandle po)
	{
		// 状态
		List<Dic> handleStatus = dicUtil.getDicInfoList("CAUCUS_HANDLE_STATUS");
		String deptId = "-1";// 默认不存在的学院
		// 判断是否学生处
		String orgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		if (null != orgId && !ProjectConstants.STUDNET_OFFICE_ORG_ID.equals(orgId))
			deptId = orgId;

		model.addAttribute("deptId", deptId);
		model.addAttribute("handleStatus", handleStatus);
		model.addAttribute("po", po);

		return "/leave/caucus/caucusEdit";
	}

	/**
	 * 保存党团关系信息
	 * 
	 * @param request
	 * @param yearCode
	 * @param handle_status
	 * @param stuIds
	 * @return
	 */
	@Deprecated
	@RequestMapping({ "/leave/caucus/opt-save/saveCaucusHandleInfo" })
	public String saveCancusHandle(HttpServletRequest request,String handleStatusCode, String stuIds)
	{
		CaucusHandle caucus = new CaucusHandle();
		// 办理年份(当前年)
		Calendar a = Calendar.getInstance();
		caucus.setYear(a.get(Calendar.YEAR) + "");
		// 办理状态
		Dic status = dicUtil.getDicInfo("CAUCUS_HANDLE_STATUS", handleStatusCode);
		caucus.setHandleStatus(status);
		caucusService.saveCaucusHandleInfo(caucus, stuIds);
		return "redirect:" + "/leave/caucus/opt-query/queryCaucusListPage.do";
	}

	/**
	 * 办理党团关系
	 * 
	 * @param request
	 * @param yearCode
	 * @param handle_status
	 * @param stuIds
	 * @return
	 */
	@Deprecated
	@ResponseBody
	@RequestMapping({ "/leave/caucus/opt-edit/operateCaucus" })
	public String operateCaucus(String studentId, String command)
	{
		if (DataUtil.isNotNull(studentId))
		{
			this.caucusService.operateCaucus(studentId, command);
		}
		return "success";
	}

	/**
	 * 删除党团关系信息
	 * 
	 * @param request
	 * @param studentId
	 * @param id
	 * @return
	 */
	@Deprecated
	@ResponseBody
	@RequestMapping({ "/leave/caucus/opt-del/multyCancel" })
	public String delCaucusHandle(HttpServletRequest request, String studentId, String id)
	{
		if (DataUtil.isNotNull(studentId)){
			this.caucusService.delCaucusHandle(studentId);
		} else if (DataUtil.isNotNull(id)){
			this.caucusService.delCancusHandleById(id);
		}
		return "success";
	}

	/**
	 * 
	 * @Title: viewCancusHandle
	 * @Description: 党团关系办理 信息查看
	 * @param model
	 * @param request
	 * @param id
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/leave/caucus/nsm/viewcaucus" })
	public String viewCancusHandle(ModelMap model, HttpServletRequest request,String id)
	{
		CaucusHandle caucus = new CaucusHandle();
		if(!StringUtils.isEmpty(id)){
			caucus = caucusService.queryCaucusById(id);
		}
		model.addAttribute("caucus", caucus);
		return "/leave/caucus/caucusView";
	}
	
	
}

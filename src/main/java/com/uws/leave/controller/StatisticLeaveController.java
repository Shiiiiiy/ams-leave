package com.uws.leave.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import com.uws.common.service.IBaseDataService;
import com.uws.common.util.SchoolYearUtil;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseController;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.service.IExcelService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.evaluation.EvaluationInfo;
import com.uws.domain.leave.QueryLeave;
import com.uws.leave.service.ILeaveService;
import com.uws.leave.service.IStatisticLeaveService;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.IDicService;
import com.uws.sys.service.impl.DicFactory;

/****
 * 离校管理统计
 * @author Jiangbl
 *
 */

@Controller
public class StatisticLeaveController extends BaseController {
	
	private static Logger log = new LoggerFactory(StatisticLeaveController.class);
	@Autowired
	private static DicUtil dicUtil = DicFactory.getDicUtil();
	
	@Autowired
	private IDicService dicService;
	
	@Autowired
	private IBaseDataService baseDataService;
	
	@Autowired
	private ICompService compService;
	
	@Autowired
	private IExcelService excelService;
	
	@Autowired
	private ILeaveService leaveService;
	
	@Autowired
	private IStatisticLeaveService statisticLeaveService;
	
	/***
	 * 离校办理率统计
	 * @param model
	 * @param request
	 * @param leave
	 * @return
	 */
	@RequestMapping({"/leave/handle/opt-query/queryHandleStatisticPage"})
	public String handleStatisticList(ModelMap model, HttpServletRequest request, QueryLeave leave){
		log.info("离校办理率统计");
		//办理年份
		List<String> yearList = this.leaveService.listLeaveYearList();
		//默认按学院统计
		if(DataUtil.isNotNull(leave) && DataUtil.isNull(leave.getRange())){
			leave.setRange("1");
		}
		
    	List<BaseAcademyModel> collegeList = this.baseDataService.listBaseAcademy();
    	model.addAttribute("collegeList", collegeList);
    	
    	if(DataUtil.isNotNull(leave.getCollegeId())) {
    		List<BaseMajorModel> majorList = this.compService.queryMajorByCollage(leave.getCollegeId());
    		model.addAttribute("majorList", majorList);
    	}
    	
    	if(DataUtil.isNotNull(leave.getMajorId())) {
    		List<BaseClassModel> classList = this.compService.queryClassByMajor(leave.getMajorId());
    		model.addAttribute("classList", classList);
    	}
    	
    	Integer pageNo=request.getParameter("pageNo")!=null?Integer.valueOf(request.getParameter("pageNo")):1;
		Page page=this.statisticLeaveService.queryLeaveStatistics(pageNo, Page.DEFAULT_PAGE_SIZE, leave);
		//返回页面
		String returnUrl = "";
		if("1".equals(leave.getRange())) {
			log.info("页面按学院统计");
			returnUrl = "leaveStatisticCollegeList";
 		}else if("2".equals(leave.getRange())) {
 			log.info("页面按专业统计");
 			returnUrl = "leaveStatisticMajorList";
 		}else if("3".equals(leave.getRange())) {
 			log.info("页面按班级统计");
 			returnUrl = "leaveStatisticClassList";
 		}
		
    	model.addAttribute("yearList", yearList);
    	model.addAttribute("leave", leave);
		model.addAttribute("page", page);
		
		return "/leave/statistic/" + returnUrl ;
	}
	
	/***
	 * 导出预处理 (离校)
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping({"/leave/handle/opt-query/nsm/exportHandleStatistic"})
	public String exportHandleStatistic(ModelMap model, HttpServletRequest request){
	    int exportSize = Integer.valueOf(request.getParameter("exportSize")).intValue();
	    int pageTotalCount = Integer.valueOf(request.getParameter("pageTotalCount")).intValue();
	    int maxNumber = 0;
	    if(pageTotalCount < exportSize){
	    	maxNumber = 1;
	    }else if(pageTotalCount % exportSize == 0){
	    	maxNumber = pageTotalCount / exportSize;
	    }else{
	      maxNumber = pageTotalCount / exportSize + 1;
	    }
	    
	    if(maxNumber <= 500){
	    	model.addAttribute("isMore", "false");
	    }else{
	      model.addAttribute("isMore", "true");
	    }
	    
	    model.addAttribute("exportSize", Integer.valueOf(exportSize));
	    model.addAttribute("maxNumber", Integer.valueOf(maxNumber));
	    
	    return "/leave/statistic/exportLeaveList";
	 }
	
	 /***
	   * 导出离校统计
	   * @param model
	   * @param request
	   * @param response
	   * @param leave
	   */
	  @RequestMapping({"/leave/handle/opt-query/exportLeave"})
	  public void exportLeave(ModelMap model, HttpServletRequest request, HttpServletResponse response, QueryLeave leave){
  		String exportSize = request.getParameter("exportSize");
  		String exportPage = request.getParameter("exportPage");
  		Page page = this.statisticLeaveService.queryLeaveStatistics(Integer.parseInt(exportPage), Integer.parseInt(exportSize), leave);
	    List listMap = new ArrayList();
	    List<Object[]> leaveList = (List)page.getResult();
	    for(Object[] object : leaveList) {
	    	Map map = new HashMap();
	    	map.put("year", DataUtil.isNotNull(object[0])?object[0].toString():"");
	    	map.put("name", object[1].toString());
	    	map.put("sumNum", DataUtil.isNotNull(object[2])?object[2].toString():"0");
	    	map.put("sumHandle", DataUtil.isNotNull(object[3])?object[3].toString():"0");
	    	map.put("rate", (DataUtil.isNotNull(object[4])?object[4].toString():"0")+"%");
	    	listMap.add(map);
	    }
		    
	    HSSFWorkbook wb=new HSSFWorkbook();
		try {
			if("1".equals(leave.getRange())) {
				wb = this.excelService.exportData("export_college_leave.xls", "exportCollegeLeave", listMap);
	 		}else if("2".equals(leave.getRange())) {
	 			wb = this.excelService.exportData("export_major_leave.xls", "exportMajorLeave", listMap);
	 		}else if("3".equals(leave.getRange())) {
	 			wb = this.excelService.exportData("export_class_leave.xls", "exportClassLeave", listMap);
	 		}
			
			String filename = "离校统计表" + exportPage + ".xls";
		    response.setContentType("application/x-excel");
		    response.setHeader("Content-disposition", "attachment;filename=" + new String(filename.getBytes("GBK"), "iso-8859-1"));
		    response.setCharacterEncoding("UTF-8");
		    OutputStream ouputStream = response.getOutputStream();
		    wb.write(ouputStream);
		    ouputStream.flush();
		    ouputStream.close();
		} catch (ExcelException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	  }
	
	/***
	 * 离校项目办理率统计
	 * @param model
	 * @param request
	 * @param yearDic
	 * @param range
	 * @param collegeId
	 * @param majorId
	 * @param classId
	 * @return
	 */
	@RequestMapping({"/leave/project/opt-query/queryProStatisticPage"})
	public String proStatisticList(ModelMap model, HttpServletRequest request, QueryLeave leave) {
		log.info("离校项目办理率统计");
		//办理年份
		//List<String> yearList = this.leaveService.listLeaveYearList();
		
		//办理年份
		List<String> yearList = this.leaveService.listLeaveSchoolYearList();
		//默认按学院统计
		if(DataUtil.isNotNull(leave) && DataUtil.isNull(leave.getRange())){
			leave.setRange("1");
		}
		
    	List<BaseAcademyModel> collegeList = this.baseDataService.listBaseAcademy();
    	model.addAttribute("collegeList", collegeList);
    	
    	if(DataUtil.isNotNull(leave.getCollegeId())) {
    		List<BaseMajorModel> majorList = this.compService.queryMajorByCollage(leave.getCollegeId());
    		model.addAttribute("majorList", majorList);
    	}
    	
    	if(DataUtil.isNotNull(leave.getMajorId())) {
    		List<BaseClassModel> classList = this.compService.queryClassByMajor(leave.getMajorId());
    		model.addAttribute("classList", classList);
    	}
    	
    	Integer pageNo=request.getParameter("pageNo")!=null?Integer.valueOf(request.getParameter("pageNo")):1;
		Page page=this.statisticLeaveService.queryLeaveProjectStatistics(pageNo, Page.DEFAULT_PAGE_SIZE, leave);
		//返回页面
		String returnUrl = "";
		if("1".equals(leave.getRange())) {
			log.info("页面按学院统计");
			returnUrl = "leaveStatisticCollegeList";
 		}else if("2".equals(leave.getRange())) {
 			log.info("页面按专业统计");
 			returnUrl = "leaveStatisticMajorList";
 		}else if("3".equals(leave.getRange())) {
 			log.info("页面按班级统计");
 			returnUrl = "leaveStatisticClassList";
 		}
		
    	model.addAttribute("yearList", yearList);
    	model.addAttribute("leave", leave);
		model.addAttribute("page", page);
		
		return "/leave/statistic/project/" + returnUrl ;
	}
	
	/***
	 * 导出预处理 (离校项目)
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping({"/leave/project/opt-query/nsm/exportLeaveStatistic"})
	public String exportLeaveStatistic(ModelMap model, HttpServletRequest request){
	    int exportSize = Integer.valueOf(request.getParameter("exportSize")).intValue();
	    int pageTotalCount = Integer.valueOf(request.getParameter("pageTotalCount")).intValue();
	    int maxNumber = 0;
	    if(pageTotalCount < exportSize){
	    	maxNumber = 1;
	    }else if(pageTotalCount % exportSize == 0){
	    	maxNumber = pageTotalCount / exportSize;
	    }else{
	      maxNumber = pageTotalCount / exportSize + 1;
	    }
	    
	    if(maxNumber <= 500){
	    	model.addAttribute("isMore", "false");
	    }else{
	      model.addAttribute("isMore", "true");
	    }
	    
	    model.addAttribute("exportSize", Integer.valueOf(exportSize));
	    model.addAttribute("maxNumber", Integer.valueOf(maxNumber));
	    
	    return "/leave/statistic/project/exportLeaveProjectList";
	 }
	
	 /***
	   * 导出离校项目统计
	   * @param model
	   * @param request
	   * @param response
	   * @param leave
	   */
	  @RequestMapping({"/leave/project/opt-query/exportLeaveProject"})
	  public void exportLeaveProject(ModelMap model, HttpServletRequest request, HttpServletResponse response, QueryLeave leave){
  		String exportSize = request.getParameter("exportSize");
  		String exportPage = request.getParameter("exportPage");
  		Page page = this.statisticLeaveService.queryLeaveProjectStatistics(Integer.parseInt(exportPage), Integer.parseInt(exportSize), leave);
	    List listMap = new ArrayList();
	    List<Object[]> leaveList = (List)page.getResult();
	    for(Object[] object : leaveList) {
	    	Map map = new HashMap();
	    	map.put("year", DataUtil.isNotNull(object[1])?object[1].toString():"");
	    	map.put("name", object[3].toString());
	    	map.put("sumNum", DataUtil.isNotNull(object[4])?object[4].toString():"0");
	    	map.put("dormSum", DataUtil.isNotNull(object[5])?object[5].toString():"0");
	    	map.put("dormRate", (DataUtil.isNotNull(object[6])?object[6].toString():"0")+"%");
	    	map.put("librarySum", DataUtil.isNotNull(object[7])?object[7].toString():"0");
	    	map.put("libraryRate", (DataUtil.isNotNull(object[8])?object[8].toString():"0")+"%");
	    	map.put("financeSum", DataUtil.isNotNull(object[9])?object[9].toString():"0");
	    	map.put("financeRate", (DataUtil.isNotNull(object[10])?object[10].toString():"0")+"%");
	    	map.put("oneCardSum", DataUtil.isNotNull(object[17])?object[17].toString():"0");
	    	map.put("oneCardRate", (DataUtil.isNotNull(object[18])?object[18].toString():"0")+"%");
	    	map.put("caucusSum", DataUtil.isNotNull(object[11])?object[11].toString():"0");
	    	map.put("caucusRate", (DataUtil.isNotNull(object[12])?object[12].toString():"0")+"%");
	    	map.put("securitySum", DataUtil.isNotNull(object[13])?object[13].toString():"0");
	    	map.put("securityRate", (DataUtil.isNotNull(object[14])?object[14].toString():"0")+"%");
	    	map.put("collegeSum", DataUtil.isNotNull(object[15])?object[15].toString():"0");
	    	map.put("collegeRate", (DataUtil.isNotNull(object[16])?object[16].toString():"0")+"%");
	    	listMap.add(map);
	    }
		    
	    HSSFWorkbook wb=new HSSFWorkbook();
		try {
			if("1".equals(leave.getRange())) {
				wb = this.excelService.exportData("export_college_leave_project.xls", "exportCollegeLeaveProject", listMap);
	 		}else if("2".equals(leave.getRange())) {
	 			wb = this.excelService.exportData("export_major_leave_project.xls", "exportMajorLeaveProject", listMap);
	 		}else if("3".equals(leave.getRange())) {
	 			wb = this.excelService.exportData("export_class_leave_project.xls", "exportClassLeaveProject", listMap);
	 		}
			
			String filename = "离校统计表" + exportPage + ".xls";
		    response.setContentType("application/x-excel");
		    response.setHeader("Content-disposition", "attachment;filename=" + new String(filename.getBytes("GBK"), "iso-8859-1"));
		    response.setCharacterEncoding("UTF-8");
		    OutputStream ouputStream = response.getOutputStream();
		    wb.write(ouputStream);
		    ouputStream.flush();
		    ouputStream.close();
		} catch (ExcelException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	  }
}

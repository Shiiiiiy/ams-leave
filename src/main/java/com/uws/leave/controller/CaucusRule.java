package com.uws.leave.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.uws.common.service.IStudentCommonService;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.rule.IRule;
import com.uws.core.excel.vo.ExcelColumn;
import com.uws.core.excel.vo.ExcelData;
import com.uws.core.util.SpringBeanLocator;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;

/**
 * 
* @ClassName: CaucusRule 
* @Description: 党团关系导入的验证
* @author 联合永道
* @date 2016-1-19 下午3:39:13 
*
 */
public class CaucusRule implements IRule
{   
	//数据字典工具类
    private DicUtil dicUtil = DicFactory.getDicUtil();
    List<Dic> handleStatusList = dicUtil.getDicInfoList("CAUCUS_HANDLE_STATUS");
	List<Dic> yearList = dicUtil.getDicInfoList("YEAR");
	@Override
    public void format(ExcelData arg0, ExcelColumn arg1, Map arg2)
    {
    }

	@Override
    public void operation(ExcelData arg0, ExcelColumn arg1, Map arg2,Map<String, ExcelData> arg3, int arg4)
    {
		if("handleStatusValue".equals(arg1.getName())){
			String untinValue = getString(arg4, arg3, "S");
			for (Dic dic : handleStatusList)
				if (untinValue.equals(dic.getName())) {
					arg0.setValue(dic);
					break;
		        }
		}
		if("yearValue".equals(arg1.getName())){
			String untinValue = getString(arg4, arg3, "B");
			for (Dic dic : yearList)
				if (untinValue.equals(dic.getName())) {
					arg0.setValue(dic);
					break;
		        }
		}
    }


	@Override
    public void validate(ExcelData arg0, ExcelColumn column, Map arg2)
            throws ExcelException
    {
		String value = arg0.getValue().toString();
		if ("handleStatusStr".equalsIgnoreCase(column.getTable_column())){
			boolean flag = false;
			for (Dic dic : handleStatusList){
				if (value.equals(dic.getName())){
					flag = true;
					break;
				}
	       }
			if(!flag){
				String isText = arg0.getId().replaceAll("\\$", "");
				throw new ExcelException(isText + "单元格值("+ value+ ")与在系统中没有找到匹配的办理状态请联系管理或修正后重新上传；<br/>");
			}
		}
		if ("year".equalsIgnoreCase(column.getTable_column())){
			boolean flag = false;
			for (Dic dic : yearList){
				if (value.equals(dic.getName())){
					flag = true;
					break;
				}
	       }
			if(!flag){
				String isText = arg0.getId().replaceAll("\\$", "");
				throw new ExcelException(isText + "单元格值("+ value+ ")与在系统中没有找到匹配的学年请联系管理或修正后重新上传；<br/>");
			}
		}
		IStudentCommonService studentCommonService = (IStudentCommonService)SpringBeanLocator.getBean("com.uws.common.service.impl.StudentCommonServiceImpl");
		if ("学号".equals(column.getName())) {
			String code = arg0.getValue().toString();
			BigDecimal bd = new BigDecimal(code);
			code = bd.toString();
			if (studentCommonService.queryStudentByStudentNo(code)==null){
				String isText = arg0.getId().replaceAll("\\$", "");
				throw new ExcelException(isText + "单元格值("+ code+ ")与在系统中没有找到匹配学号的学生信息，请修正后重新上传；<br/>");
			}
		}
    }
	
	private String getString(int site, Map eds, String key){
        String s = "";
        String keyName = (new StringBuilder("$")).append(key).append("$").append(site).toString();
        if(eds.get(keyName) != null && ((ExcelData)eds.get(keyName)).getValue() != null)
            s = (new StringBuilder(String.valueOf(s))).append((String)((ExcelData)eds.get(keyName)).getValue()).toString();
        return s.trim();
    }
	
	private String subStr(String str) {
		
		if(str.endsWith(".0")) {
			str = str.replace(".0", "");
		}
		return str.trim();
	}
}

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
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;

public class LifeRule implements IRule
{   
	//数据字典工具类
    private DicUtil dicUtil = DicFactory.getDicUtil();
    List<Dic> statusList = dicUtil.getDicInfoList("LIFE_STATUS");
	@Override
    public void format(ExcelData arg0, ExcelColumn arg1, Map arg2)
    {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void operation(ExcelData arg0, ExcelColumn arg1, Map arg2,
            Map<String, ExcelData> arg3, int arg4)
    {
		if("status".equals(arg1.getName())){
			String untinValue = getString(arg4, arg3, "C");
			for (Dic dic : this.statusList)
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
		boolean flag = false; boolean insert = false;
		if ("statusText".equalsIgnoreCase(column.getTable_column())){
			insert = true;
			for (Dic dic : this.statusList){
				if (value.equals(dic.getName())){
					flag = true;
					break;
				}
	       }
		}
		
		IStudentCommonService studentCommonService = (IStudentCommonService)SpringBeanLocator.getBean("com.uws.common.service.impl.StudentCommonServiceImpl");
		if ("stuNumber".equalsIgnoreCase(column.getTable_column())){
			insert = true;
			BigDecimal bd = new BigDecimal(value);
			String certNum = bd.toString();
			StudentInfoModel studentInfo = studentCommonService.queryStudentByStudentNo(certNum);
			boolean checkIsGraduateStudent = studentCommonService.checkIsGraduateStudent(certNum);
			if(studentInfo==null)
			{
				flag = false;
				if ((insert) && (!flag)){
					String isText = arg0.getId().replaceAll("\\$", "");
					throw new ExcelException(isText + "单元格属性值(" + certNum + ")与在系统中没有找到匹配学号的学生信息，请修正后重新上传；<br/>");
				}
			}else
			{
				flag = true;
				if(checkIsGraduateStudent == false)
				{
					String isText = arg0.getId().replaceAll("\\$", "");
					throw new ExcelException(isText + "单元格属性值(" + certNum + ")在系统中不是毕业班的学生，请重新选择毕业生；<br/>");
				}
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
	
}

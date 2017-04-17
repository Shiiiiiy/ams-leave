package com.uws.leave.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.common.service.IStudentCommonService;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.leave.CaucusHandle;
import com.uws.domain.leave.LeaveInfo;
import com.uws.domain.leave.VLeaveInfo;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.leave.dao.ICaucusDao;
import com.uws.leave.service.ICaucusService;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.user.model.User;

@Service("com.uws.leave.service.impl.CaucusServiceImpl")
public class CaucusServiceImpl extends BaseServiceImpl implements ICaucusService {
	@Autowired
	private ICaucusDao caucusDao;
	@Autowired
	private IStudentCommonService studentCommonService;
	
	private DicUtil dicUtil = DicFactory.getDicUtil();
	
	private SessionUtil sessionUtil = SessionFactory.getSession("/leave/caucus/");
	
	@Override
	public Page queryCaucusListPage(CaucusHandle po, int pageNo, int pageSize, HttpServletRequest request) {
		return this.caucusDao.queryCaucusListPage(po, pageNo, pageSize, request);
	}

	@Override
	public void saveCaucusHandleInfo(CaucusHandle caucus, String stuIds) {
		String[] ids = stuIds.split(",");
		for(String id :ids) {
			//党团关系办理更新或着存储
			CaucusHandle po = this.caucusDao.getCaucusHandleByStuId(id);
			if(DataUtil.isNotNull(po)) {
				//修改
				po.setYear(caucus.getYear());
				po.setHandleStatus(caucus.getHandleStatus());
				this.caucusDao.update(po);
			}else {//新增
				CaucusHandle newCaucus = new CaucusHandle();
				newCaucus.setHandleStatus(caucus.getHandleStatus());
				newCaucus.setYear(caucus.getYear());
				StudentInfoModel sim = studentCommonService.queryStudentById(id);
				newCaucus.setStudent(sim);
				this.caucusDao.save(newCaucus);
			}
			//学生离校办理之党团关系办理信息更新或者存储
			LeaveInfo leave = this.caucusDao.getLeaveInfoByStuId(id);
			if(DataUtil.isNotNull(leave)) {
				if(DataUtil.isNotNull(leave.getCaucus())) {
					if(!(leave.getCaucus().getId().equals(caucus.getHandleStatus().getId()))){
						leave.setCaucus(caucus.getHandleStatus());
					}
				}else {
					leave.setCaucus(caucus.getHandleStatus());
				}
				this.caucusDao.update(leave);
			}else {
				leave = new LeaveInfo();
				StudentInfoModel student = studentCommonService.queryStudentById(id);
				leave.setYear(caucus.getYear());
				leave.setStudent(student);
				leave.setCaucus(caucus.getHandleStatus());
				this.caucusDao.save(leave);
			}
		}
	}

	@Override
	public void delCaucusHandle(String studentIds) {
		String[] stuIds = studentIds.split(",");
		for(String id : stuIds) {
			CaucusHandle ch = this.caucusDao.getCaucusHandleByStuId(id);
			this.caucusDao.deleteById(CaucusHandle.class, ch.getId());
			//将离校信息表中该学生的党团办理状态级联置空
			LeaveInfo leave = this.caucusDao.getLeaveInfoByStuId(id);
			if(DataUtil.isNotNull(leave)) {
				leave.setCaucus(null);
				this.caucusDao.update(leave);
			}
		}
	}

	@Override
	public void delCancusHandleById(String id) {
		CaucusHandle caucus=this.caucusDao.getCaucusHandleById(id);
		String studentId="";
		if(DataUtil.isNotNull(caucus)){
			studentId=caucus.getStudent().getId();
		}
		this.caucusDao.deleteById(CaucusHandle.class, id);
		//将离校信息表中该学生的党团办理状态级联置空
		LeaveInfo leave = this.caucusDao.getLeaveInfoByStuId(studentId);
		if(DataUtil.isNotNull(leave)) {
			leave.setCaucus(null);
			this.caucusDao.update(leave);
		}
	}

	@Override
	public LeaveInfo getLeaveInfoByStuId(String userId) {
		return this.caucusDao.getLeaveInfoByStuId(userId);
	}
	
	/***
	 * 单个办理
	 * @param id
	 */
	public void operateCaucus(String studentId, String command){
		//党团关系办理更新或着存储
		CaucusHandle caucus = this.caucusDao.getCaucusHandleByStuId(studentId);
		Dic handle = dicUtil.getDicInfo("CAUCUS_HANDLE_STATUS", command);
		if(DataUtil.isNotNull(caucus)) {
			caucus.setHandleStatus(handle);
			this.caucusDao.update(caucus);
		}
		//学生离校办理之党团关系办理信息更新或者存储
		LeaveInfo leave = this.caucusDao.getLeaveInfoByStuId(studentId);
		if(DataUtil.isNotNull(leave)) {
			leave.setCaucus(handle);
			this.caucusDao.update(leave);
		}else {
			leave = new LeaveInfo();
			StudentInfoModel student = studentCommonService.queryStudentById(studentId);
			leave.setYear(caucus.getYear());
			leave.setStudent(student);
			leave.setCaucus(handle);
			this.caucusDao.save(leave);
		}
	}

	/**
	 * 
	 * @Title: listGradeList
	 * @Description: 毕业年份信息 封装返回调用 离校模块调用
	 * @return
	 * @throws
	 */
	public List<String> listCaucusYearList(){
		return this.caucusDao.listCaucusYearList();
	}
	
	/***
	 * 个人党团关系
	 * @param studentId
	 * @return
	 */
	public CaucusHandle getCaucusHandleByStuId(String studentId){
		return this.caucusDao.getCaucusHandleByStuId(studentId);
	}

	
	/**
	 * 描述信息: 党团关系导入
	 * @param filePath
	 * @param excelId
	 * @param initData
	 * @param clazz
	 * @throws Exception
	 * 2016-1-19 下午3:08:07
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public void importCaucusData(String filePath, String excelId, Map initData,  Class<CaucusHandle> clazz) throws Exception
    {
		ImportUtil iu = new ImportUtil();
		List<CaucusHandle> caucusList = iu.getDataList(filePath, excelId, initData, clazz);
	    User user = new User();
	    user.setId(sessionUtil.getCurrentUserId());
	    StudentInfoModel student = null;
	    CaucusHandle caucusPo = null;
	    for(CaucusHandle caucus : caucusList)
	    {
	    	try{
	    		String code = caucus.getStudentNumber();
				BigDecimal bd = new BigDecimal(code);
	    		/*
	    		 * 注意:当前系统 学号的ID是一样的所以可以直接赋值的方式保存,如果不是则用下边的查询方法代替
	    		 * student = commonStudentDao.queryStudentByStudentNo(bd.toString());
	    		 */
				student = new StudentInfoModel();
				student.setId(bd.toString());
				caucus.setStudent(student);
				caucus.setUpdator(user);
				caucusPo =  this.getCaucusHandleByStuId(bd.toString());
				if(null!=caucusPo)//更新
				{
					BeanUtils.copyProperties(caucus, caucusPo, new String[]{"id","creator","createTime",""});
					caucusDao.update(caucusPo);
				}else{//新增
					caucus.setCreator(user);
					caucusDao.save(caucus);
				}
    		}
			catch(Exception e){
				e.printStackTrace();
				throw new ExcelException("学号为 "+ caucus.getStudentNumber() +" 的学生党团关系数据出现问题,请联系管理员或修正后重新上传；<br/>");
			}
	    }
    }

	@Override
    public CaucusHandle queryCaucusById(String id)
    {
	    return (CaucusHandle) caucusDao.get(CaucusHandle.class, id);
    }
	
	@Override
	public VLeaveInfo getVLeaveInfoByStudentId(String userId)
	{
	    return this.caucusDao.getVLeaveInfoByStudentId(userId);
	}
	
}

package com.uws.leave.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.leave.CaucusHandle;
import com.uws.domain.leave.LeaveInfo;
import com.uws.domain.leave.LifeInfo;
import com.uws.domain.leave.VLeaveInfo;
import com.uws.leave.dao.ICaucusDao;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;

@Repository("com.uws.leave.dao.impl.CaucusDaoImpl")
public class CaucusDaoImpl extends BaseDaoImpl implements ICaucusDao {

	@Override
	public Page queryCaucusListPage(CaucusHandle po, int pageNo, int pageSize, HttpServletRequest request) {
		String orgId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		String hql = "from CaucusHandle t where 1=1";
		Map<String, Object> params = new HashMap<String, Object>();
		if(DataUtil.isNotNull(po)) {
			if(DataUtil.isNotNull(po.getStudent())) {
				//学院
				if(DataUtil.isNotNull(po.getStudent().getCollege()) && DataUtil.isNotNull(po.getStudent().getCollege().getId())) {
					hql += "and t.student.college.id =:collegeId ";
					params.put("collegeId", po.getStudent().getCollege().getId());
				}
				//专业
				if(DataUtil.isNotNull(po.getStudent().getMajor()) && DataUtil.isNotNull(po.getStudent().getMajor().getId())) {
					hql += "and t.student.major.id =:majorId ";
					params.put("majorId", po.getStudent().getMajor().getId());
				}
				//班级
				if(DataUtil.isNotNull(po.getStudent().getClassId()) && DataUtil.isNotNull(po.getStudent().getClassId().getId())) {
					hql += "and t.student.classId.id =:classId ";
					params.put("classId", po.getStudent().getClassId().getId());
				}
				//姓名
				if(DataUtil.isNotNull(po.getStudent().getName())) {
					hql += "and t.student.name like :stuName ";
					if (HqlEscapeUtil.IsNeedEscape(po.getStudent().getName())) {
						params.put("stuName", "%"+po.getStudent().getName() + HqlEscapeUtil.HQL_ESCAPE +"%");
					} else
						params.put("stuName", "%"+po.getStudent().getName()+"%");
				}
				//学号
				if(DataUtil.isNotNull(po.getStudent().getStuNumber())) {
					hql += "and t.student.stuNumber like :stuNumber ";
					if (HqlEscapeUtil.IsNeedEscape(po.getStudent().getStuNumber())) {
						params.put("stuNumber", "%"+po.getStudent().getStuNumber() + HqlEscapeUtil.HQL_ESCAPE +"%");
					} else
						params.put("stuNumber", "%"+po.getStudent().getStuNumber()+"%");
				}	
			}
			//办理年份
			if(DataUtil.isNotNull(po.getYearDic()) && DataUtil.isNotNull(po.getYearDic().getId()) ) {
				hql += "and t.yearDic =:yearDic";
				params.put("yearDic", po.getYearDic());
			}
			
			//二级学院只查看自己学院的记录
			if(null!=orgId && !ProjectConstants.STUDNET_OFFICE_ORG_ID.equals(orgId)){
				hql+=" and t.student.college.id = :orgId ";
				params.put("orgId", orgId);
			}
			
			//办理状态
			if(DataUtil.isNotNull(po.getHandleStatus()) && DataUtil.isNotNull(po.getHandleStatus().getId())) {
				hql += "and t.handleStatus.id =:handleStatusId ";
				params.put("handleStatusId", po.getHandleStatus().getId());
			}
		}
		return pagedQuery(hql, params, pageSize, pageNo);
	}

	@Override
	public CaucusHandle getCaucusHandleByStuId(String id) {
		String hql = "from CaucusHandle c where c.student.id =? ";
		return (CaucusHandle) this.queryUnique(hql, new String[]{id});
	}

	@Override
	public LeaveInfo getLeaveInfoByStuId(String id) {
		String hql = "from LeaveInfo l where l.student.id =? ";
		return (LeaveInfo) this.queryUnique(hql, new String[]{id});
	}
	
	/**
	 * 
	 * @Description: 毕业年份信息 封装返回调用 离校模块调用
	 * @return
	 * @throws
	 */
	@SuppressWarnings("unchecked")
    public List<String> listCaucusYearList(){
		return this.query("select distinct year from CaucusHandle order by year desc ");
	}
	
	/***
	 * 通过id查询党团办理
	 * @param id
	 * @return
	 */
	public CaucusHandle getCaucusHandleById(String id){
		String hql = "from CaucusHandle l where l.id =? ";
		return (CaucusHandle) this.queryUnique(hql, new String[]{id});
	}
	
	@Override
	public VLeaveInfo getVLeaveInfoByStudentId(String userId)
	{
		return (VLeaveInfo)this.queryUnique("from VLeaveInfo where student.id=?", new Object[] {userId});
	}

}

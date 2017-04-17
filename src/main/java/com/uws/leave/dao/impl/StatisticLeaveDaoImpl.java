package com.uws.leave.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.leave.QueryLeave;
import com.uws.leave.dao.IStatisticLeaveDao;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.util.ProjectConstants;

@Repository("com.uws.leave.dao.impl.StatisticLeaveDaoImpl")
public class StatisticLeaveDaoImpl extends BaseDaoImpl implements IStatisticLeaveDao{
	//字典工具类
	private DicUtil dicUtil=DicFactory.getDicUtil();
	
	/***
	 * 离校办理统计查询 学院
	 * @param pageNo
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryCollegeLeaveStatistics(int pageNo, int pageSize, QueryLeave leave){
		Dic statusDic=this.dicUtil.getDicInfo("Y&N", "Y");//已办理状态
		List<Object> values = new ArrayList<Object>();
		
		StringBuffer hql = new StringBuffer("select pp.year_id,tt.name,tt.sumNum,pp.sumHandle,round(pp.sumHandle/tt.sumNum,4)*100"+
						" from (select s.college, p.name, count(s.college) sumNum "+
						" from hky_student_info s left join hky_base_collage p on s.college = p.id"+
						" group by s.college, p.name) tt");
		if(DataUtil.isNotNull(leave.getYear())){
			hql.append(" right join ");
		}else{
			hql.append(" left join ");
		}
						
		hql.append("(select m.year_id, n.college, count(*) sumHandle "+
		" from hky_leave_info m left join hky_student_info n on m.student_id = n.id"+
		" where m.library = '"+statusDic.getId()+"' and m.edu = '"+statusDic.getId()+
		"' and m.dorm = '"+statusDic.getId()+"' and m.finance = '"+statusDic.getId()+
		"' and m.college = '"+statusDic.getId()+"' group by m.year_id, n.college) pp on tt.college = pp.college where 1=1");
		
		//学年
		if(DataUtil.isNotNull(leave.getYear())){
			hql.append(" and pp.year_id = ? ");
			values.add(leave.getYear());
		}

		//学院
		if(DataUtil.isNotNull(leave.getCollegeId())){
			hql.append(" and tt.college  = ? ");
			values.add(leave.getCollegeId());
		}
		//排序条件
		hql.append(" order by tt.college desc ");
		return this.pagedSQLQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}
	
	/***
	 * 离校办理统计查询 	专业
	 * @param pageNo
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryMajorLeaveStatistics(int pageNo, int pageSize, QueryLeave leave){
		Dic statusDic=this.dicUtil.getDicInfo("Y&N", "Y");//已办理状态
		List<Object> values = new ArrayList<Object>();
		
		StringBuffer hql = new StringBuffer("select pp.year_id, tt.major_name, tt.sumNum, pp.sumHandle, round(pp.sumHandle / tt.sumNum, 4) * 100"+
						" from (select s.major, s.college, p.major_name, count(s.major) sumNum "+
						" from hky_student_info s left join hky_base_major p on s.major = p.id"+
						" group by s.major, p.major_name, s.college) tt");
		if(DataUtil.isNotNull(leave.getYear())){
			hql.append(" right join ");
		}else{
			hql.append(" left join ");
		}
						
		hql.append("(select m.year_id, n.major, count(*) sumHandle "+
		" from hky_leave_info m left join hky_student_info n on m.student_id = n.id"+
		" where m.library = '"+statusDic.getId()+"' and m.edu = '"+statusDic.getId()+
		"' and m.dorm = '"+statusDic.getId()+"' and m.finance = '"+statusDic.getId()+
		"' and m.college = '"+statusDic.getId()+"' group by m.year_id, n.major) pp on tt.major = pp.major where 1=1");
		
		//学年
		if(DataUtil.isNotNull(leave.getYear())){
			hql.append(" and pp.year_id = ? ");
			values.add(leave.getYear());
		}

		//学院
		if(DataUtil.isNotNull(leave.getCollegeId())){
			if(DataUtil.isNotNull(leave.getMajorId())){
				hql.append(" and tt.major  = ? ");
				values.add(leave.getMajorId());
			}else{
				hql.append(" and tt.college  = ? ");
				values.add(leave.getCollegeId());
			}
		}
		//排序条件
		hql.append(" order by tt.college, tt.major desc ");
		return this.pagedSQLQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}
	
	/***
	 * 离校办理统计查询 	班级
	 * @param pageNo
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryClassLeaveStatistics(int pageNo, int pageSize, QueryLeave leave){
		Dic statusDic=this.dicUtil.getDicInfo("Y&N", "Y");//已办理状态
		List<Object> values = new ArrayList<Object>();
		
		StringBuffer hql = new StringBuffer("select pp.year_id, tt.class_name, tt.sumNum, pp.sumHandle, round(pp.sumHandle / tt.sumNum, 4) * 100"+
						" from (select s.class_id, s.major, s.college, p.class_name, count(s.class_id) sumNum "+
						" from hky_student_info s left join hky_base_class p on s.class_id = p.id"+
						"  group by  s.class_id, s.major, p.class_name, s.college) tt");
		if(DataUtil.isNotNull(leave.getYear())){
			hql.append(" right join ");
		}else{
			hql.append(" left join ");
		}
						
		hql.append("(select m.year_id, n.class_id, count(*) sumHandle "+
		"  from hky_leave_info m left join hky_student_info n on m.student_id = n.id"+
		" where m.library = '"+statusDic.getId()+"' and m.edu = '"+statusDic.getId()+
		"' and m.dorm = '"+statusDic.getId()+"' and m.finance = '"+statusDic.getId()+
		"' and m.college = '"+statusDic.getId()+"' group by m.year_id, n.major, n.class_id) pp on tt.class_id = pp.class_id where 1=1");
		
		//学年
		if(DataUtil.isNotNull(leave.getYear())){
			hql.append(" and pp.year_id = ? ");
			values.add(leave.getYear());
		}

		//学院
		if(DataUtil.isNotNull(leave.getCollegeId())){
			if(DataUtil.isNotNull(leave.getMajorId())){
				//专业不为空
				if(DataUtil.isNotNull(leave.getClassId())){
					//班级不为空
					hql.append(" and tt.class_id  = ? ");
					values.add(leave.getClassId());
				}else{
					hql.append(" and tt.major  = ? ");
					values.add(leave.getMajorId());
				}
			}else{
				hql.append(" and tt.college  = ? ");
				values.add(leave.getCollegeId());
			}
		}
		//排序条件
		hql.append(" order by tt.college, tt.major, tt.class_id desc ");
		return this.pagedSQLQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}

	/***
	 * 离校项目办理统计查询 	 学院
	 * @param pageNo
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryCollegeLeaveProjectStatistics(int pageNo, int pageSize, QueryLeave leave){
		List<Object> values = new ArrayList<Object>();
		
		StringBuffer hql = new StringBuffer("select * from v_leaveschool_college t where 1=1 ");
						
		//学年
		if(DataUtil.isNotNull(leave.getYear())){
			hql.append(" and t.year = ? ");
			values.add(leave.getYear());
		}

		//学院
		if(DataUtil.isNotNull(leave.getCollegeId())){
			hql.append(" and t.collegeId  = ? ");
			values.add(leave.getCollegeId());
		}
		return this.pagedSQLQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}
	
	/***
	 * 离校项目办理统计查询 	 专业
	 * @param pageNo
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryMajorLeaveProjectStatistics(int pageNo, int pageSize, QueryLeave leave){
		List<Object> values = new ArrayList<Object>();
		
		StringBuffer hql = new StringBuffer("select * from v_leaveschool_major t where 1=1 ");
						
		//学年
		if(DataUtil.isNotNull(leave.getYear())){
			hql.append(" and t.year = ? ");
			values.add(leave.getYear());
		}

		//学院
		if(DataUtil.isNotNull(leave.getCollegeId())){
			if(DataUtil.isNotNull(leave.getMajorId())){
				//专业
				hql.append(" and t.majorId  = ? ");
				values.add(leave.getMajorId());
			}else{
				hql.append(" and t.collegeId  = ? ");
				values.add(leave.getCollegeId());
			}
		}
		return this.pagedSQLQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}
	
	/***
	 * 离校项目办理统计查询 	 班级
	 * @param pageNo
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryClassLeaveClassStatistics(int pageNo, int pageSize, QueryLeave leave){
		List<Object> values = new ArrayList<Object>();
		
		StringBuffer hql = new StringBuffer("select * from v_leaveschool_class t where 1=1 ");
		
		//学年
		if(DataUtil.isNotNull(leave.getYear())){
			hql.append(" and t.year = ? ");
			values.add(leave.getYear());
		}

		//学院
		if(DataUtil.isNotNull(leave.getCollegeId())){
			if(DataUtil.isNotNull(leave.getMajorId())){
				//专业不为空
				if(DataUtil.isNotNull(leave.getClassId())){
					//班级不为空
					hql.append(" and t.classId  = ? ");
					values.add(leave.getClassId());
				}else{
					hql.append(" and t.majorId  = ? ");
					values.add(leave.getMajorId());
				}
			}else{
				hql.append(" and t.collegeId  = ? ");
				values.add(leave.getCollegeId());
			}
		}
		//排序条件
		hql.append(" order by t.collegeId, t.majorId,  t.classId desc");
		return this.pagedSQLQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}

}

package com.uws.leave.dao;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.leave.QueryLeave;

public interface IStatisticLeaveDao extends IBaseDao{
	/***
	 * 离校办理统计查询 	 学院
	 * @param pageNo
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryCollegeLeaveStatistics(int pageNo, int pageSize, QueryLeave leave);
	
	/***
	 * 离校办理统计查询 	专业
	 * @param pageNo
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryMajorLeaveStatistics(int pageNo, int pageSize, QueryLeave leave);
	
	/***
	 * 离校办理统计查询 	班级
	 * @param pageNo
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryClassLeaveStatistics(int pageNo, int pageSize, QueryLeave leave);

	/***
	 * 离校项目办理统计查询 	 学院
	 * @param pageNo
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryCollegeLeaveProjectStatistics(int pageNo, int pageSize, QueryLeave leave);
	
	/***
	 * 离校项目办理统计查询 	 专业
	 * @param pageNo
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryMajorLeaveProjectStatistics(int pageNo, int pageSize, QueryLeave leave);
	
	/***
	 * 离校项目办理统计查询 	 班级
	 * @param pageNo
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryClassLeaveClassStatistics(int pageNo, int pageSize, QueryLeave leave);

}

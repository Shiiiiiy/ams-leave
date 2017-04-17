package com.uws.leave.service;

import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.leave.QueryLeave;

public interface IStatisticLeaveService extends IBaseService{

	/***
	 * 离校办理统计查询(学院、专业、班级)
	 * @param pageNo
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryLeaveStatistics(int pageNo, int pageSize, QueryLeave leave);
	
	
	/***
	 * 离校项目办理统计查询(学院、专业、班级)
	 * @param pageNo
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryLeaveProjectStatistics(int pageNo, int pageSize, QueryLeave leave);

}

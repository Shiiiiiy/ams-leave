package com.uws.leave.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.leave.QueryLeave;
import com.uws.leave.dao.IStatisticLeaveDao;
import com.uws.leave.service.IStatisticLeaveService;

@Service("com.uws.leave.service.impl.StatisticLeaveServiceImpl")
public class StatisticLeaveServiceImpl extends BaseServiceImpl implements IStatisticLeaveService {
	@Autowired
	private IStatisticLeaveDao statisticLeaveDao;

	/***
	 * 离校办理统计查询
	 * @param pageNo
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryLeaveStatistics(int pageNo, int pageSize, QueryLeave leave){
		if("1".equals(leave.getRange())){
			//按学院
			return this.statisticLeaveDao.queryCollegeLeaveStatistics(pageNo, pageSize, leave);
		}else if("2".equals(leave.getRange())){
			//按专业
			return this.statisticLeaveDao.queryMajorLeaveStatistics(pageNo, pageSize, leave);
		}else{
			//按班级
			return this.statisticLeaveDao.queryClassLeaveStatistics(pageNo, pageSize, leave);
		}
	}
	
	/***
	 * 离校项目办理统计查询(学院、专业、班级)
	 * @param pageNo
	 * @param pageSize
	 * @param leave
	 * @return
	 */
	public Page queryLeaveProjectStatistics(int pageNo, int pageSize, QueryLeave leave){
		if("1".equals(leave.getRange())){
			//按学院
			return this.statisticLeaveDao.queryCollegeLeaveProjectStatistics(pageNo, pageSize, leave);
		}else if("2".equals(leave.getRange())){
			//按专业
			return this.statisticLeaveDao.queryMajorLeaveProjectStatistics(pageNo, pageSize, leave);
		}else{
			//按班级
			return this.statisticLeaveDao.queryClassLeaveClassStatistics(pageNo, pageSize, leave);
		}
	}
}

package com.bdqn.appInfo.dao;

import com.bdqn.appInfo.pojo.Info;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Info record);

    int insertSelective(Info record);

    /**
     * @Description: 查询到APP信息通过id或名称
     * @param: [id, apkName]
     * @return: com.bdqn.appInfo.pojo.Info
     * @Date: 2019/07/22 11:44
     */
    Info selectByPrimaryKey(@Param("id") Long id, @Param("apkName") String apkName);

    int updateByPrimaryKeySelective(Info record);

    int updateByPrimaryKey(Info record);

    /**
     * @Description: 查找到App的数量
     * @param: [querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId]
     * @return: int
     * @Date: 2019/07/19 15:28
     */
    int selectAppInfoCount(@Param("querySoftwareName") String querySoftwareName,
                           @Param("queryStatus") Integer queryStatus,
                           @Param("queryCategoryLevel1") Integer queryCategoryLevel1,
                           @Param("queryCategoryLevel2") Integer queryCategoryLevel2,
                           @Param("queryCategoryLevel3") Integer queryCategoryLevel3,
                           @Param("queryFlatformId") Integer queryFlatformId,
                           @Param("devId") Long devId);

    /**
     * @Description: 查询到APP的所有信息
     * @param: [querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId, from, pageSize]
     * @return: java.util.List<com.sun.deploy.ui.AppInfo>
     * @Date: 2019/07/19 15:51
     */
    List<Info> selectAppInfoList(@Param("querySoftwareName") String querySoftwareName,
                                 @Param("queryStatus") Integer queryStatus,
                                 @Param("queryCategoryLevel1") Integer queryCategoryLevel1,
                                 @Param("queryCategoryLevel2") Integer queryCategoryLevel2,
                                 @Param("queryCategoryLevel3") Integer queryCategoryLevel3,
                                 @Param("queryFlatformId") Integer queryFlatformId,
                                 @Param("devId") Long devId,
                                 @Param("from") int from,
                                 @Param("pageSize") int pageSize);
}
package com.imooc.grocers.dal;

import com.imooc.grocers.model.RecommendDO;

public interface RecommendDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Fri Mar 27 13:53:20 EDT 2020
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Fri Mar 27 13:53:20 EDT 2020
     */
    int insert(RecommendDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Fri Mar 27 13:53:20 EDT 2020
     */
    int insertSelective(RecommendDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Fri Mar 27 13:53:20 EDT 2020
     */
    RecommendDO selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Fri Mar 27 13:53:20 EDT 2020
     */
    int updateByPrimaryKeySelective(RecommendDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Fri Mar 27 13:53:20 EDT 2020
     */
    int updateByPrimaryKey(RecommendDO record);
}
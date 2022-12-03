package com.AlMLand.repository

import com.AlMLand.dto.enums.Category
import com.AlMLand.entity.CourseCategory
import org.springframework.data.repository.CrudRepository
import java.util.*

interface CourseCategoryRepository : CrudRepository<CourseCategory, UUID> {

    fun existsByCategoryAndDescription(category: Category, description: String?): Boolean

    //    @Query(
//        """
//        select * from course_categories as cc
//        left join course_coursecategory as ccc on cc.id = ccc.course_category_id
//        left join courses as c on ccc.course_id = c.id
//        where c.name = :name
//    """, nativeQuery = true
//    )
    fun findByCoursesName(name: String): List<CourseCategory>

}

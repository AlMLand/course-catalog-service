package com.AlMLand.repository

import com.AlMLand.dto.enums.Category
import com.AlMLand.entity.Course
import com.AlMLand.entity.CourseCategory
import org.springframework.data.repository.CrudRepository

interface CourseRepository : CrudRepository<Course, Int> {
    //    @Query(
//        """
//        select * from courses as c
//        left join course_coursecategory as ccc on c.id = ccc.course_id
//        left join course_categories as cc on ccc.course_category_id = cc.id
//        where cc.category = :category
//    """, nativeQuery = true
//    )
//    fun findByCategory(category: String): List<Course>

    //    @Query(
//        """
//        select course from Course course
//        left join course.category category
//        where category.category = :category
//    """
//    )
    fun findByCategoryCategory(category: Category): List<Course>
    fun existsFirst1ByNameAndCategoryIn(name: String, courseCategory: List<CourseCategory>): Boolean
    fun findByNameContainingIgnoreCase(name: String): List<Course>
    fun findByNameContainingIgnoreCaseAndCategoryCategory(name: String, category: Category): List<Course>
    
}

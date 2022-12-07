package com.madinaty.app.data.mapper

import com.madinaty.app.data.model.DepartmentChildDto
import com.madinaty.app.data.model.DepartmentDto
import com.madinaty.app.domain.model.Department
import com.madinaty.app.domain.model.DepartmentChild


fun DepartmentDto.toDepartment() = Department(
    id = id,
    name = name,
    departmentChilds = departmentChilds?.map { it.toDepartmentChild() }
)

fun DepartmentChildDto.toDepartmentChild(): DepartmentChild {
    if (childs == null)
        return DepartmentChild(
            id = id,
            name = name,
            departmentId = departmentId,
            childs = null
        )
    if (childs.isEmpty())
        return DepartmentChild(
            id = id,
            name = name,
            departmentId = departmentId,
            childs = emptyList()
        )

    return DepartmentChild(
        id = id,
        name = name,
        departmentId = departmentId,
        childs = childs.map { it.toDepartmentChild() }
    )
}
package com.madinaty.app.data.mapper

import com.madinaty.app.data.model.DepartmentChildDto
import com.madinaty.app.data.model.DepartmentDto
import com.madinaty.app.domain.model.Department
import com.madinaty.app.domain.model.DepartmentChild


fun DepartmentDto.toDepartment(): Department {
    if (places.isNullOrEmpty()) {
        return Department(
            id = id,
            name = name,
            departmentChilds = departmentChilds?.map { it.toDepartmentChild() },
            places = null
        )
    }

    return Department(
        id = id,
        name = name,
        departmentChilds = departmentChilds?.map { it.toDepartmentChild() },
        places = places.map { it.toPlace() }
    )
}

fun DepartmentChildDto.toDepartmentChild(): DepartmentChild {
    if (childs == null)
        return DepartmentChild(
            id = id,
            name = name,
            departmentId = departmentId,
            childs = null,
            places = places?.map { it.toPlace() }
        )
    if (childs.isEmpty())
        return DepartmentChild(
            id = id,
            name = name,
            departmentId = departmentId,
            childs = emptyList(),
            places = places?.map { it.toPlace() }
        )

    return DepartmentChild(
        id = id,
        name = name,
        departmentId = departmentId,
        childs = childs.map { it.toDepartmentChild() },
        places = places?.map { it.toPlace() }
    )
}
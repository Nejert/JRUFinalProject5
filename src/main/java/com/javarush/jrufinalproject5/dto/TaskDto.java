package com.javarush.jrufinalproject5.dto;

import com.javarush.jrufinalproject5.dto.task.TaskIn;
import com.javarush.jrufinalproject5.dto.task.TaskOut;
import com.javarush.jrufinalproject5.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskDto {
    @Mapping(target = "user", ignore = true)
    Task from(TaskIn taskIn);

    TaskOut from(Task task);
}

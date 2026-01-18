package com.javarush.jrufinalproject5.dto;

import com.javarush.jrufinalproject5.dto.task.TaskIn;
import com.javarush.jrufinalproject5.dto.task.TaskOut;
import com.javarush.jrufinalproject5.dto.task.UserMapper;
import com.javarush.jrufinalproject5.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface TaskDto {
    @Mapping(source = "userId", target = "user")
    Task from(TaskIn taskIn);
    @Mapping(source = "user.id", target = "userId")
    TaskOut from(Task task);
}

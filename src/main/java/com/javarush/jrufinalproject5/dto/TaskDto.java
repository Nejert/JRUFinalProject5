package com.javarush.jrufinalproject5.dto;

import com.javarush.jrufinalproject5.dto.task.TaskIn;
import com.javarush.jrufinalproject5.dto.task.TaskOut;
import com.javarush.jrufinalproject5.dto.task.UserMapper;
import com.javarush.jrufinalproject5.entity.Task;
import com.javarush.jrufinalproject5.entity.User;
import com.javarush.jrufinalproject5.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface TaskDto {
    @Mapping(source = "userId", target = "user")
    Task from(TaskIn taskIn);
    @Mapping(source = "user.id", target = "userId")
    TaskOut from(Task task);
}

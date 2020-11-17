package com.SpringBatch_CsvToH2AndH2ToCsv.batch;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.SpringBatch_CsvToH2AndH2ToCsv.model.User;

@Component
public class Processor1 implements ItemProcessor<User, User> {

    private static final Map<String, String> DEPT_NAMES =
            new HashMap<>();

    List<User> list=new ArrayList<>();
    public Processor1() {
        DEPT_NAMES.put("1", "Technology");
        DEPT_NAMES.put("2", "Operations");
        DEPT_NAMES.put("3", "Accounts");
    }

    @Override
    public User process(User user) throws Exception {
    	System.out.println("inside processor 1");
        String deptCode = user.getDept();
        String dept = DEPT_NAMES.get(deptCode);
        user.setDept(dept);
        user.setTime(new Date());
        user.setSalary(user.getSalary());
        list.add(user);
        System.out.println(String.format("Converted from [%s] to [%s]", deptCode, dept));
        System.out.println(list);
        return user;
    }

	
}

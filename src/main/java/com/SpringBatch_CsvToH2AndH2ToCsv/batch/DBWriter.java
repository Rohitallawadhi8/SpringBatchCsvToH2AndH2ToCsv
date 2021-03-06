//package com.SpringBatch_CsvToH2AndH2ToCsv.batch;
//
//import java.util.List;
//
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.SpringBatch_CsvToH2AndH2ToCsv.model.User;
//import com.SpringBatch_CsvToH2AndH2ToCsv.repository.UserRepository;
//
//
//
//@Component
//public class DBWriter implements ItemWriter<User> {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public void write(List<? extends User> users) throws Exception {
//    	System.out.println("Inside writer 1");
//        System.out.println("\nData Saved for Users: " + users);
//        	userRepository.save(users);
//    }
//}

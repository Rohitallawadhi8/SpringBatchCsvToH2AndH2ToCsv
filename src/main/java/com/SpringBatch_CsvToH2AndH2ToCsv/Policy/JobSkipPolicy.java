package com.SpringBatch_CsvToH2AndH2ToCsv.Policy;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

public class JobSkipPolicy implements SkipPolicy {

	@Override
	public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {//skipcount=failedcount
		// TODO Auto-generated method stub
		//return false;   //will fail the job
		
		
		//return true;  // will skip the all bad record
		
		System.out.println("Failedcount="+skipCount);
		
		return (skipCount>5)? false: true;
	}

}

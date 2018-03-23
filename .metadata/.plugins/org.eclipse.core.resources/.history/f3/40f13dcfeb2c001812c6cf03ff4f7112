package com.techolution.mauritus.data.simulator;

import java.text.ParseException;

import com.techolution.mauritius.data.simulator.service.FlowDataSimiulator;
import com.techolution.mauritius.data.simulator.service.IStubData;

public class PopulateData {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String kpi=args[0];
		String meterid=args[1];
		String starttime=args[2];
		String endtime=args[3];
		String sleeptime=args[4];
		String incrementtime=args[5];
		
		int meter=Integer.valueOf(meterid).intValue();
		long sleepval=Long.valueOf(sleeptime).longValue();
		int incrementval=Integer.valueOf(incrementtime).intValue();
		
		
		IStubData data=null;
		if("flow".equalsIgnoreCase(kpi)){
			data=new FlowDataSimiulator();
			data.startProcess(meter, starttime, endtime, sleepval, incrementval);
			
			
		}
	}

}

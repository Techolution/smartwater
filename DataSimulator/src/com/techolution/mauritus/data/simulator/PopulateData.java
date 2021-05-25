package com.techolution.mauritus.data.simulator;

import java.text.ParseException;

import com.techolution.mauritius.data.simulator.service.BatteryDataSimiulator;
import com.techolution.mauritius.data.simulator.service.FlowDataSimiulator;
import com.techolution.mauritius.data.simulator.service.IStubData;
import com.techolution.mauritius.data.simulator.service.MeterOffDataSimulator;
import com.techolution.mauritius.data.simulator.service.MeterOnDataSimulator;

public class PopulateData {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String kpi=args[0];
		String meterid=args[1];
		String starttime=args[2];
		String endtime=args[3];
		String sleeptime=args[4];
		String incrementtime=args[5];
		System.out.println("KPI**:"+kpi);
		System.out.println("meterid**:"+meterid);
		System.out.println("starttime**:"+starttime);
		System.out.println("endtime**:"+endtime);
		System.out.println("sleeptime**:"+sleeptime);
		System.out.println("incrementtime**:"+incrementtime);
		
		
		int meter=Integer.valueOf(meterid).intValue();
		long sleepval=Long.valueOf(sleeptime).longValue();
		int incrementval=Integer.valueOf(incrementtime).intValue();
		
		
		IStubData data=null;
		if("flow".equalsIgnoreCase(kpi)){
			data=new FlowDataSimiulator();
			data.startProcess(meter, starttime, endtime, sleepval, incrementval);
			
			
		}else if("battery".equalsIgnoreCase(kpi)){
			data=new BatteryDataSimiulator();
			data.startProcess(meter, starttime, endtime, sleepval, incrementval);
		}
		
		else if("meteron".equalsIgnoreCase(kpi)){
			data=new MeterOnDataSimulator();
			data.startProcess(meter, starttime, endtime, sleepval, incrementval);
		}
		else if("meteroff".equalsIgnoreCase(kpi)){
			data=new MeterOffDataSimulator();
			data.startProcess(meter, starttime, endtime, sleepval, incrementval);
		}
	}

}

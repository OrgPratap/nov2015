package com.sps.updation;

import com.sps.emp.Emp;
public class Updation {
	public void updateEmp(Emp e, Emp newEmp) {
		//e=newEmp;
		newEmp=e;
		System.out.println("-------------["+newEmp.toString()+"]-------------");
	}
}

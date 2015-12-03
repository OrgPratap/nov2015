package com.sps.main;

import java.util.ArrayList;
import com.sps.emp.Emp;
import com.sps.updation.Updation;

public class MainClass {
	public static void main(String[] args) {
		Updation updation =new Updation();
		ArrayList<Emp> empList = new ArrayList<>();
		empList.add(new Emp(100, "ab"));
		empList.add(new Emp(101, "cd"));
		empList.add(new Emp(102, "ef"));
		empList.add(new Emp(103, "gh"));
		Emp newEmp = new Emp(200, "xy");
		System.out.println("+++++++++++++NEW EMP+++++++++++++++");
		System.out.println(newEmp.toString());
		System.out.println("+++++++++++++++++++++PREVIOUS++++++++++++++++");
		for(Emp e : empList){
			System.out.println(e);
		}
		for(Emp e : empList){
			if(e.getName().equalsIgnoreCase("ef")){
				updation.updateEmp(e, newEmp);
			}
		}
		System.out.println("+++++++++++++++++++++NOW++++++++++++++++");
		for(Emp e : empList){
			System.out.println(e);
		}
		System.out.println("+++++++++++++NEW EMP+++++++++++++++");
		System.out.println(newEmp.toString());
	}
}

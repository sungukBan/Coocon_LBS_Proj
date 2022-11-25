package com.coocon.lbs.test;

public class TestString {
	public static void main(String [] args){
		TestString cs = new TestString();
		cs.start();
	}

	private void start() {
		try{
			String sInput = "0000";
			
			if(sInput.trim().equals("")|| sInput.trim().equals("0000")){
				System.out.println("match");
			}else{
				System.out.println("not-match");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}

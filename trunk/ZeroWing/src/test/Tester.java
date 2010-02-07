package test;

import mechanic.VersionVector;

public class Tester {

	public static void main(String[] args) {
		VersionVector vv = new VersionVector("");
		VersionVector vv2 = new VersionVector("A:1 B:2 C:3");
		VersionVector vv3 = new VersionVector("A:3 D:1 C:2");
		
		System.out.println(vv);
		System.out.println(vv2);
		System.out.println(vv3);
		
		vv.addVersionVector(vv2);
		
		System.out.println(vv);
		
		vv.addVersionVector(vv3);
		
		System.out.println(vv);
	}

}

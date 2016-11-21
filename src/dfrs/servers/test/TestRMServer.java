package dfrs.servers.test;

import java.util.Scanner;

import dfrs.sequencer.ClusterManagerSender;
import dfrs.utils.Utils;

public class TestRMServer {
	public static void main(String[] args) {
		while(true) {
			System.out.println("\n****Welcome to DFRS System****\n");
			System.out.println("Please select your test item (1-4)");
			System.out.println("1. Send Four request");
			System.out.println("2. Only Send Three request");
			System.out.println("3. FE Reply To RM No Crash");
			System.out.println("4. FE Reply To RM With Crash");
			System.out.println("5. Test Wrong Expect ID");
			Scanner keyboard = new Scanner(System.in);
			int choose = Utils.validInputOption(keyboard, 5);
			if(choose == 1) {
				new ClusterManagerSender("localhost",7101,"2$555$MTL$MTL1111$NDL$20161010$2$2$2$").start();
				new ClusterManagerSender("localhost",7102,"2$555$MTL$MTL1111$NDL$20161010$2$2$2$").start();
				new ClusterManagerSender("localhost",7103,"2$555$MTL$MTL1111$NDL$20161010$2$2$2$").start();
				new ClusterManagerSender("localhost",7104,"2$555$MTL$MTL1111$NDL$20161010$2$2$2$").start();
			}else if(choose == 2) {
				new ClusterManagerSender("localhost",7101,"2$556$MTL$MTL1111$NDL$20161010$2$2$2$").start();
				new ClusterManagerSender("localhost",7102,"2$556$MTL$MTL1111$NDL$20161010$2$2$2$").start();
				new ClusterManagerSender("localhost",7103,"2$556$MTL$MTL1111$NDL$20161010$2$2$2$").start();
//				new ClusterManagerSender("localhost",7104,"2$6$MTL$MTL1111$NDL$20161010$2$2$2$").start();
			} else if(choose == 3) {
				new ClusterManagerSender("localhost",7001,"5$correct$1$wrong$2$correct$3$correct$4").start();
				new ClusterManagerSender("localhost",7002,"5$correct$1$wrong$2$correct$3$correct$4").start();
				new ClusterManagerSender("localhost",7003,"5$correct$1$wrong$2$correct$3$correct$4").start();
				new ClusterManagerSender("localhost",7004,"5$correct$1$wrong$2$correct$3$correct$4").start();
			} else if(choose == 4) {
				new ClusterManagerSender("localhost",7001,"5$correct$1$wrong$2$crash$3$correct$4").start();
				new ClusterManagerSender("localhost",7002,"5$correct$1$wrong$2$crash$3$correct$4").start();
				new ClusterManagerSender("localhost",7003,"5$correct$1$wrong$2$crash$3$correct$4").start();
				new ClusterManagerSender("localhost",7004,"5$correct$1$wrong$2$crash$3$correct$4").start();
			} else if(choose == 5) {
				new ClusterManagerSender("localhost",7101,"2$557$MTL$MTL1111$NDL$20161010$2$2$2$").start();
				new ClusterManagerSender("localhost",7102,"2$557$MTL$MTL1111$NDL$20161010$2$2$2$").start();
				new ClusterManagerSender("localhost",7103,"2$557$MTL$MTL1111$NDL$20161010$2$2$2$").start();
				new ClusterManagerSender("localhost",7104,"2$557$MTL$MTL1111$NDL$20161010$2$2$2$").start();
			}
		}
	}
}

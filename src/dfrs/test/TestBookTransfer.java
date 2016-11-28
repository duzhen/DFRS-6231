package dfrs.test;

import dfrs.ServerInterface;

public class TestBookTransfer extends dfrs.test.Test {
	private static final int THREAD = 4;
	
//	@Before
	public void setUp() throws Exception {
		dfrs = new ServerInterface[3*THREAD];
		initConnection();
	}

	public static void main(String args[]) {
		TestBookTransfer test = new TestBookTransfer();
		try {
			test.setUp();
			test.test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@After
	public void tearDown() throws Exception {
	}

//	@Test
	public void test() {
		for(int i=0;i<dfrs.length;i++) {
			if(i%3==0) {
				if(i>3) {
					startTransferTest(dfrs[i], i, 0);
				} else {
					startBookTest(dfrs[i], i, 0, true);
				}
			} else if(i%3==1) {
				if((i-1)>3) {
					startTransferTest(dfrs[i], i, 1);
				} else {
					startBookTest(dfrs[i], i, 1, true);
				}
			} else if(i%3==2) {
				if((i-2)>3) {
					startTransferTest(dfrs[i], i, 2);
				} else {
					startBookTest(dfrs[i], i, 2, true);
				}
			}
		}
		System.out.println("\nBook and Transfer Testing...");
	}

	@Override
	protected int getThreadNum() {
		return THREAD;
	}

}

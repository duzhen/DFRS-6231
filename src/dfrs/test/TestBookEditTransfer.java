package dfrs.test;

import dfrs.ServerInterface;

public class TestBookEditTransfer extends dfrs.test.Test {
	private static final int THREAD = 6;
	
//	@Before
	public void setUp() throws Exception {
		dfrs = new ServerInterface[3*THREAD];
		initConnection();
	}

	public static void main(String args[]) {
		TestBookEditTransfer test = new TestBookEditTransfer();
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
				int order = i/3;
				if(order<=1) {
					startBookTest(dfrs[i], i, 0, true);
				} else if(order>=2&&order<=3) {
					startEditTest(dfrs[i], i, 0, true);
				} else if(order>=4&&order<=5) {
					startTransferTest(dfrs[i], i, 0);
				}
			} else if(i%3==1) {
				int order = (i-1)/3;
				if(order<=1) {
					startBookTest(dfrs[i], i, 1, true);
				} else if(order>=2&&order<=3) {
					startEditTest(dfrs[i], i, 1, true);
				} else if(order>=4&&order<=5) {
					startTransferTest(dfrs[i], i, 1);
				}
			} else if(i%3==2) {
				int order = (i-2)/3;
				if(order<=1) {
					startBookTest(dfrs[i], i, 2, true);
				} else if(order>=2&&order<=3) {
					startEditTest(dfrs[i], i, 2, true);
				} else if(order>=4&&order<=5) {
					startTransferTest(dfrs[i], i, 2);
				}
			}
		}
		System.out.println("\nBook Edit and Transfer Testing...");
	}

	@Override
	protected int getThreadNum() {
		return THREAD;
	}

}

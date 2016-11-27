package dfrs.servers2;

public interface ITransaction {
	void doCommit() throws TransactionException ;
	void backCommit();
}

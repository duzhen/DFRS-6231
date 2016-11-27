package dfrs.servers4;

public interface ITransaction {
	void doCommit() throws TransactionException ;
	void backCommit();
}

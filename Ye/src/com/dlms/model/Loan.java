package com.dlms.model;

/**
 *
 * @author yucunli
 */
public class Loan {
    
    public String ID = null;
    public String accountNumber = null;
    public String amount = "";
    public String dueDate = "";
    
    public Loan (){}
    
    //Initialize with ID
    public Loan (String _ID, String _accountNumber, String _amount, String _dueDate)
    {
        ID = _ID;
        accountNumber = _accountNumber;
        amount = _amount;
        dueDate = _dueDate;
    }
    
}

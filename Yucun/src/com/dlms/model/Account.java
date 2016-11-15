package com.dlms.model;

/**
 *
 * @author yucunli
 */
public class Account {
    public String accountNumber = null;
    public String firstName = null;
    public String lastName = null;
    public String emailAddress = null;
    public String phoneNumber = null;
    public String password = null;
    public int creditLimit = (int)0;
    

    public Account() {}

    public Account (String _firstName, String _lastName, String _emailAddress, String _phoneNumber, String _password, int _creditLimit)
    {
      accountNumber = (_firstName+_lastName).hashCode() + "";
      firstName = _firstName;
      lastName = _lastName;
      emailAddress = _emailAddress;
      phoneNumber = _phoneNumber;
      password = _password;
      creditLimit = _creditLimit;
    } 
    
    public String toString() {
        return "Customer [account=" + accountNumber + ", firstName=" + firstName + ", lastName=" + lastName + ", email="
                        + emailAddress + ", phoneNumber=" + phoneNumber + ", password=" + password + ", creditLimit=" + creditLimit
                        + "]" + "\n";
    }

}

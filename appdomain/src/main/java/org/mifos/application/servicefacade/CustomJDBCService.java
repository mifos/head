package org.mifos.application.servicefacade;

public interface CustomJDBCService {
    boolean mifos4948IssueKeyExists();

    void insertMifos4948Issuekey();
    
    boolean mifos5722IssueKeyExists();

    void insertMifos5722Issuekey();
    
    boolean mifos5763IssueKeyExists();

    void insertMifos5763Issuekey();
    
    void insertMifos5632IssueKey();
    
    boolean mifos5632IssueKeyExists();
}

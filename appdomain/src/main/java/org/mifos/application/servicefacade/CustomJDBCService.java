package org.mifos.application.servicefacade;

public interface CustomJDBCService {
    boolean mifos4948IssueKeyExists();

    void insertMifos4948Issuekey();
}

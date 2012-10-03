package org.mifos.reports.admindocuments.business;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.framework.business.AbstractBusinessObject;

/**
 * This class extend admin documents functionality. Now it is possible to attach admin documents to payments made to specified transactions(receipts).
 * It keeps old convention (see {@link AdminDocAccStateMixBo} but persistence layer of admin documents and receipts data should be refactored.
 *
 */

@NamedQueries({ @NamedQuery(name = "admindocument.getAccActionMixByAdministrativeDocumentId",
                            query = "from AdminDocAccActionMixBO admindocmix where admindocmix.adminDocument.admindocId =:admindocId") })
@Entity
@Table(name = "admin_document_acc_action_mix")
public class AdminDocAccActionMixBO extends AbstractBusinessObject {

    @Id
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "increment")
    @Column(name = "admin_doc_acc_action_mix_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "account_action_id", referencedColumnName = "account_action_id")
    private AccountActionEntity accountAction;

    @ManyToOne
    @JoinColumn(name = "admin_document_id", referencedColumnName = "admin_document_id")
    private AdminDocumentBO adminDocument;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AccountActionEntity getAccountAction() {
        return accountAction;
    }

    public void setAccountAction(AccountActionEntity accountActionEntity) {
        this.accountAction = accountActionEntity;
    }

    public AdminDocumentBO getAdminDocument() {
        return adminDocument;
    }

    public void setAdminDocument(AdminDocumentBO adminDocumentBO) {
        this.adminDocument = adminDocumentBO;
    }

}

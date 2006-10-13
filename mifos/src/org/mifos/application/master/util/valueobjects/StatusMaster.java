
   package org.mifos.application.master.util.valueobjects;

    import java.util.List;

import org.mifos.framework.util.valueobjects.ValueObject;

    public class StatusMaster extends ValueObject
    {

        private java.lang.Short statusId;
        private String statusName;
        private List flagList;




        public void setStatusId(java.lang.Short statusId) {
			this.statusId = statusId;
		}



		public void setStatusName(String statusName) {
			this.statusName = statusName;
		}



		public StatusMaster(Short statusId , String statusName)
        {
        	this.statusId = statusId;
        	this.statusName = statusName;

		}



	public java.lang.Short getStatusId()
        {
             return statusId;
        }

        public String getStatusName()
        {
             return statusName;
        }

        public void setFlagList(List flagMaster)
        {
			this.flagList = flagMaster;
		}

        public List getFlagList()
        {
			return flagList;
		}


}



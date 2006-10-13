
   package org.mifos.application.master.util.valueobjects;

    import java.util.Set;

import org.mifos.framework.util.valueobjects.ValueObject;

    public class Status extends ValueObject
    {

        private java.lang.Short statusId;
        private java.lang.Short levelId;

	    private Integer lookUpId;
        private Set lookUpValueLocale;
        private String statusDescription;
        private Set statusFlag;




        public Status()
        {
		}

        public void setStatusId(java.lang.Short statusId)
        {
             this.statusId = statusId;
        }


	public java.lang.Short getStatusId()
        {
             return statusId;
        }


      public void setLookUpValueLocale(Set lookUpValueLocale)
      {

		  this.lookUpValueLocale = lookUpValueLocale;
	  }

      public Set getLookUpValueLocale()
      {
		    return lookUpValueLocale;

	  }
	public Integer getLookUpId() {
		return lookUpId;
	}

	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}



            public void setLevelId(java.lang.Short levelId)
	        {
	             this.levelId = levelId;
	        }


		public java.lang.Short getLevelId()
	        {
	             return levelId;
	        }

	        public void setStatusDescription(String description)
	        {
				this.statusDescription = description;
			}

	        public String getStatusDescription()
	        {
				return statusDescription;
			}

			public Set getStatusFlag()
			{
				return statusFlag;
			}

			public void  setStatusFlag(Set statusFlag)
			{
				this.statusFlag =  statusFlag;
			}


		}



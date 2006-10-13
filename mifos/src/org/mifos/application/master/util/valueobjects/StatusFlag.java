
   package org.mifos.application.master.util.valueobjects;

    import java.util.Set;

import org.mifos.framework.util.valueobjects.ValueObject;

    public class StatusFlag extends ValueObject
    {

        private java.lang.Short flagId;
        private java.lang.Short statusId;

		private Integer lookUpId;
        private Set lookUpValueLocale;
        private String flagDescription;
        private Short isBlacklisted;




        public StatusFlag()
        {
		}


        public void setIsBlacklisted(java.lang.Short isBlacklisted)
        {
             this.isBlacklisted = isBlacklisted;
        }


	public java.lang.Short getIsBlacklisted()
        {
             return isBlacklisted;
        }

        public void setFlagId(java.lang.Short flagId)
        {
             this.flagId = flagId;
        }


	public java.lang.Short getFlagId()
        {
             return flagId;
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



            public void setStatusId(java.lang.Short statusId)
	        {
	             this.statusId = statusId;
	        }


		public java.lang.Short getStatusId()
	        {
	             return statusId;
	        }

	        public void setFlagDescription(String flagDescription)
	        {

				this.flagDescription = flagDescription;
			}

	        public String getFlagDescription()
	        {

				return flagDescription;
			}

		}




   package org.mifos.application.master.util.valueobjects;

    import org.mifos.framework.util.valueobjects.ValueObject;
    import java.util.Set;

    public class Position extends ValueObject
    {

        private java.lang.Integer positionId;

private Integer lookUpId;
        private Set lookUpValueLocale;




        public Position()
        {
		}

        public void setPositionId(java.lang.Integer positionId)
        {
             this.positionId = positionId;
        }


	public java.lang.Integer getPositionId()
        {
             return positionId;
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

    }



   package org.mifos.application.program.util.valueobjects;





    import org.mifos.application.office.util.valueobjects.Office;
    import org.mifos.framework.util.valueobjects.ValueObject;
import java.util.Set;


    public class Program extends ValueObject
    {

        // need to put lookup_id and office associations

        private java.lang.Short programId;


        private java.lang.Short glcodeId;


        private java.lang.Short confidentiality;


        private java.sql.Date endDate;


        private java.sql.Date startDate;

        private Set lookUpValueLocale;

        private Integer lookUpId;


        public Program()
        {
		}

        public void setProgramId(java.lang.Short programId)
        {
             this.programId = programId;
        }


	public java.lang.Short getProgramId()
        {
             return programId;
        }

        public void setGlcodeId(java.lang.Short glcodeId)
        {
             this.glcodeId = glcodeId;
        }


	public java.lang.Short getGlcodeId()
        {
             return glcodeId;
        }






        public void setConfidentiality(java.lang.Short confidentiality)
        {
             this.confidentiality = confidentiality;
        }


	public java.lang.Short getConfidentiality()
        {
             return confidentiality;
        }



        public void setStartDate(java.sql.Date startDate)
	{
	        this.startDate = startDate;
	}

        public java.sql.Date getStartDate()
	{
	        return startDate;
	}

        public void setEndDate(java.sql.Date endDate)
	{
	        this.endDate = endDate;
	}

        public java.sql.Date getEndDate()
	{
	        return endDate;
	}

      public void setLookUpValueLocale(Set lookUpValueLocale)
      {
		  this.lookUpValueLocale = lookUpValueLocale;
	  }

      public Set getLookUpValueLocale()
      {
		    return lookUpValueLocale;

	  }

      public void setLookUpId(Integer lookUpId) {
  		this.lookUpId = lookUpId;
  	}


  	public Integer getLookUpId()
  	{
  		return lookUpId;
  	}

      
    }

